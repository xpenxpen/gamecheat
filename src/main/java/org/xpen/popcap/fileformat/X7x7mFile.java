package org.xpen.popcap.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class X7x7mFile {
    public static final int MAGIC_7x7M = 0x4D37BD37; //'7.7M'
    public static final byte XOR_KEY = (byte)0xF7; //xor key
    public static final int XOR_KEY_INT = 0xF7F7F7F7;
    
    private static final Logger LOG = LoggerFactory.getLogger(X7x7mFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    
    public X7x7mFile() {
    }
    
    public X7x7mFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
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
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.limit(9);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_7x7M) {
        	throw new RuntimeException("bad magic");
        }
        
        byte hasLatter;
        
        do {
            buffer = ByteBuffer.allocate(1);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(1);
            fileChannel.read(buffer);
            buffer.flip();
            
            byte fileNameLength = (byte)(buffer.get() ^ XOR_KEY);
            
            buffer = ByteBuffer.allocate(fileNameLength);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(fileNameLength);
            fileChannel.read(buffer);
            buffer.flip();
            
            String fName = ByteBufferUtil.getFixedLengthStringXor(buffer, fileNameLength, XOR_KEY);
            
            buffer = ByteBuffer.allocate(13);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(13);
            fileChannel.read(buffer);
            buffer.flip();
            
            int fileSize = buffer.getInt();
            fileSize ^= XOR_KEY_INT;
            
            buffer.getLong(); //skip 8
            
            
            FatEntry fatEntry = this.new FatEntry();
            fatEntry.fname = fName;
            fatEntry.size = fileSize;
            fatEntries.add(fatEntry);
            
        	hasLatter = buffer.get();
        	hasLatter ^= XOR_KEY;
        } while (hasLatter == 0);

        
        buffer.clear();
        

        decodeDat();
        
    }

    protected void decodeDat() throws Exception {
    	//DAT just follows FAT
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            
            byte[] bytes = new byte[fatEntry.size];
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
        public int size;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
