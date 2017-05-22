package org.xpen.kingsoft.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.LzoCompressor;
import org.xpen.util.UserSetting;

public class PakFile {
    public static final byte[] MAGIC_PACKAGE = {0x50, 0x41, 0x43, 0x4B, 0x41, 0x47, 0x45, 0x00}; //PACKAGE
    public static final int MAGIC_7x7M = 0x4D37BD37; //'7.7M'
    public static final byte XOR_KEY = (byte)0xF7; //xor key
    public static final int XOR_KEY_INT = 0xF7F7F7F7;
    
    private static final Logger LOG = LoggerFactory.getLogger(PakFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    
    public PakFile() {
    }
    
    public PakFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".pak"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * 7x7M File format
     * ALL byte  XOR 0F -->real byte
     * the first 4 bytes are the signature 0xc0 0x4a 0xc0 0xba
     * go at offset 9 and start the loop:
     * - 1 byte: length of the filename (n)
     * - n bytes: the filename
     * - 4 bytes: the size of the file
     * - 8 bytes: skip them
     * - 1 byte: 0x00 if there are other files, 0x80 if the list of files is terminated
     *
     */
    public void decode() throws Exception {
        byte[] magic = new byte[8];
        raf.readFully(magic);
        if (!Arrays.equals(magic, MAGIC_PACKAGE)) {
            throw new RuntimeException("bad magic");
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(8);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fileCount = buffer.getInt();
        buffer.getInt(); //Unknown =02
        
	    for (int i = 0; i < fileCount; i++) {
	        buffer = ByteBuffer.allocate(12);
		    buffer.order(ByteOrder.LITTLE_ENDIAN);
		    buffer.limit(12);
            fileChannel.read(buffer);
            buffer.flip();
            
	    	FatEntry fatEntry = new FatEntry();
	    	fatEntry.decode(buffer);
	    	fatEntries.add(fatEntry);
	    }

        
        buffer.clear();
        

        decodeDat();
        
    }

    protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            ByteBuffer buffer = ByteBuffer.allocate(4);
		    buffer.order(ByteOrder.LITTLE_ENDIAN);
		    buffer.limit(4);
            fileChannel.read(buffer);
            buffer.flip();
            
            fatEntry.compressedSize = buffer.getShort();
            fatEntry.unknownFlag = buffer.getShort();
            
            byte[] b = new byte[fatEntry.compressedSize];
            raf.readFully(b);
            
            byte[] ub = new byte[fatEntry.uncompressedSize];
            //TODO not lzo :-(
			LOG.debug("i={}, compressedSize={}, uncompressedSize={}", i, fatEntry.compressedSize, fatEntry.uncompressedSize);
    		LzoCompressor.decompress(b, 0, fatEntry.compressedSize, ub, 0, fatEntry.uncompressedSize);
            
            String threeDigit = StringUtils.leftPad(String.valueOf(i), 3, '0');

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + threeDigit + ".txt");
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(ub, os);
            os.close();
        }
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int crc;
        public int start;
        public int compressedSize;
        public int uncompressedSize;
        public int unknownFlag;

		public void decode(ByteBuffer buffer) {
			crc = buffer.getInt();
			start = buffer.getInt();
			uncompressedSize = buffer.getInt();
		}
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
