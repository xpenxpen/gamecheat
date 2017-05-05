package org.xpen.zeroescape.fileformat;

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

public class CfsiFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(CfsiFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    
    private FatEntry optionalEntry;
    private List<FolderEntry> folderEntries = new ArrayList<FolderEntry>();
    private String fileName;
    
    
    public CfsiFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".cfsi"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * CFSI File format
     * 1 0x01 or 0xFC (if = 0xFC, read next 4~ bytes)
     * 4 unknown (optional)
     * 1 file name length
     * X file name
     * 4 unknown
     * 4 length
     * 
     * 1 folder name length
     * X folder name
     * 1 entry count (if = 0xFC, read next 2 bytes)
     * 2 entry count (optional)
     * ----entry Count
     * |
     * |  LOOP ---1 file name length
     * |       |  X file name
     * |       |  4 unknown
     * |       ---4 length
     * |
     * |
     * ----
     * ----entry Count
     * |
     * |  LOOP ---start align to 16 byte
     * |       |  
     * |       ---DAT
     * |
     * ----
     *
     */
    public void decode() throws Exception {
        
        decodeFat();
        decodeDat();
        
    }


    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        
        int firstByte = buffer.get();
        //optional file
        if (firstByte == -4) {
            buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
        	
            buffer = ByteBuffer.allocate(1);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
            int length = buffer.get();
            
            buffer = ByteBuffer.allocate(length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
        	
        	String optionalFileName = ByteBufferUtil.getFixedLengthString(buffer, length);
        	
            buffer = ByteBuffer.allocate(8);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
            
        	optionalEntry = new FatEntry();
            optionalEntry.name = optionalFileName;
            optionalEntry.unknown = buffer.getInt();
            optionalEntry.length = buffer.getInt();
            
        }
        
    	FolderEntry folderEntry = new FolderEntry();
    	folderEntries.add(folderEntry);
    	
        buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        int length = buffer.get();
        
        buffer = ByteBuffer.allocate(length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
    	
    	String folderName = ByteBufferUtil.getFixedLengthString(buffer, length);
    	folderEntry.name = folderName;
    	
        buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        int fileCount = buffer.get();
        //more than 1 byte
        if (fileCount == -4) {
            buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
        	fileCount = buffer.getShort();
        }
        LOG.debug("fileCount={}", fileCount);
        for (int j = 0; j < fileCount; j++) {
        	FatEntry fatEntry = new FatEntry();
        	folderEntry.fatEntries.add(fatEntry);
        	
            buffer = ByteBuffer.allocate(1);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
            length = buffer.get();
            
            buffer = ByteBuffer.allocate(length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
        	
        	String innerFileName = ByteBufferUtil.getFixedLengthString(buffer, length);
            
            buffer = ByteBuffer.allocate(8);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
            
            fatEntry.name = innerFileName;
            fatEntry.unknown = buffer.getInt();
            fatEntry.length = buffer.getInt();
        	
        }
        
    }

    private void decodeDat() throws Exception {
    	long previousPos = fileChannel.position();
    	
        for (int i = 0; i < folderEntries.size(); i++) {
        	FolderEntry folderEntry = folderEntries.get(i);
        	
        	for (int j = 0; j < folderEntry.fatEntries.size(); j++) {
            	FatEntry fatEntry = folderEntry.fatEntries.get(j);
            	
            	if (previousPos % 16 != 0) {
            		previousPos = previousPos + 16 - previousPos % 16;
            	}
                raf.seek(previousPos);
                LOG.debug("start={}, length={}", previousPos, fatEntry.length);
                
                byte[] bytes = new byte[fatEntry.length];
                raf.readFully(bytes);
                
                previousPos = previousPos + fatEntry.length;

                File outFile = null;
                outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + folderEntry.name + fatEntry.name);
                File parentFile = outFile.getParentFile();
                parentFile.mkdirs();
                
                OutputStream os = new FileOutputStream(outFile);
                
                IOUtils.write(bytes, os);
                os.close();
        	}
            
        }
        
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
    
    public class FolderEntry {
        public String name;
        public List<FatEntry> fatEntries = new ArrayList<FatEntry>();
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public class FatEntry {
        public String name;
        public int unknown;
        public int length;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
