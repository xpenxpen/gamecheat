package org.xpen.bigfish.fileformat;

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
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class PakFile {
    public static final byte[] MAGIC_BASPACK = {0x42, 0x41, 0x53, 0x50, 0x41, 0x43, 0x4B}; //BASPACK
    public static final byte XOR_KEY = (byte)0x37; //xor key
    
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
     * BASPACK File format
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
        byte[] magic = new byte[7];
        raf.readFully(magic);
        if (!Arrays.equals(magic, MAGIC_BASPACK)) {
            throw new RuntimeException("bad magic");
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.order(ByteOrder.LITTLE_ENDIAN);
	    buffer.limit(8);
	    fileChannel.read(buffer);
	    buffer.flip();
	    
	    int fatStart = buffer.getInt();
	    int fileCount = buffer.getInt() / 64; //why divide 64, magic!
	    
	    fileChannel.position(fatStart);
        
	    
	    for (int i = 0; i < fileCount; i++) {
	        buffer = ByteBuffer.allocate(263);
		    buffer.order(ByteOrder.LITTLE_ENDIAN);
		    buffer.limit(263);
            fileChannel.read(buffer);
            buffer.flip();
            
	    	FatEntry fatEntry = new FatEntry();
	    	fatEntry.decode(buffer);
	    	fatEntries.add(fatEntry);
	    }
	        
        decodeDat();
        
    }

    protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);           
            
            byte[] bytes = new byte[fatEntry.size];
            fileChannel.position(fatEntry.start);
            raf.readFully(bytes);
            for (int j = 0; j < bytes.length; j++) {
            	bytes[j] ^= XOR_KEY;
            }
            

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder, fatEntry.fname);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(bytes, os);
            os.close();
        }
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int start;
        public int size;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }

		public void decode(ByteBuffer buffer) {
			fname = ByteBufferUtil.getNullTerminatedString(buffer);
			buffer.position(263 - 8);
			start = buffer.getInt();
			size = buffer.getInt();
			
			LOG.debug("fname={}, start={}, size={}", fname, start, size);
		}
    }
}
