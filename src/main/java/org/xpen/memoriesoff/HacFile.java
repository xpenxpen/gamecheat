package org.xpen.memoriesoff;

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

public class HacFile {
    public static final int MAGIC_7x7M = 0x4D37BD37; //'7.7M'
    public static final byte XOR_KEY = (byte)0xF7; //xor key
    public static final int XOR_KEY_INT = 0xF7F7F7F7;
    
    private static final Logger LOG = LoggerFactory.getLogger(HacFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    
    public HacFile() {
    }
    
    public HacFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".HAC"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * HAC File format
     *
     */
    public void decode() throws Exception {
    	raf.seek(0x54E52723);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fileLength = buffer.getInt();
        
        buffer = ByteBuffer.allocate(fileLength * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(fileLength * 2);
        fileChannel.read(buffer);
        buffer.flip();
        
        String fileName = ByteBufferUtil.getFixedLengthString(buffer, fileLength * 2);

        
        //4 zero
        buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(8);
        fileChannel.read(buffer);
        buffer.flip();
        
        int compressLength = buffer.getInt();
        int uncompressLength = buffer.getInt();
        
        //16 zero
        buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(16);
        fileChannel.read(buffer);
        buffer.flip();
        
        buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        int start = buffer.getInt();
        
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
