package org.xpen.chunsoft.zeroescape.fileformat;

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
    
    //private FatEntry optionalEntry;
    private List<FolderEntry> folderEntries = new ArrayList<FolderEntry>();
    private String fileName;
    
    
    public CfsiFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".cfsi"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * CFSI File format
     * 1 folder count 0x01 or 0xFC (if = 0xFC, read next 2 bytes)
     * 2 folder count (optional)
     * 1 folder name length
     * X folder name
     * 1 entry count (if = 0xFC, read next 2 bytes)
     * 2 entry count (optional)
     * ----entry Count
     * |
     * |  LOOP ---1 file name length
     * |       |  X file name
     * |       |  4 start (*16)
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
        
        int folderCount = buffer.get();
        //optional file
        if (folderCount == -4) {
            buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            fileChannel.read(buffer);
            buffer.flip();
            folderCount = buffer.getShort();
        }
        
        for (int i = 0; i < folderCount; i++) {
        
	    	FolderEntry folderEntry = new FolderEntry();
	    	folderEntries.add(folderEntry);
	    	
	        buffer = ByteBuffer.allocate(1);
	        buffer.order(ByteOrder.LITTLE_ENDIAN);
	        
	        fileChannel.read(buffer);
	        buffer.flip();
	        int length = buffer.get();
	        
	        String folderName = "";
	        if (length != 0) {
		        buffer = ByteBuffer.allocate(length);
		        buffer.order(ByteOrder.LITTLE_ENDIAN);
		        
		        fileChannel.read(buffer);
		        buffer.flip();
		    	
		        folderName = ByteBufferUtil.getFixedLengthString(buffer, length);
	        }
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
	        if (fileCount < 0) {
	        	fileCount = fileCount + 256;
	        }
	        LOG.debug("pos={}, folderEntry.name={}, fileCount={}",
	            fileChannel.position(), folderEntry.name, fileCount);
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
	            fatEntry.start = buffer.getInt() * 16;
	            fatEntry.length = buffer.getInt();
	        	
	        }
        }
        
    }

    private void decodeDat() throws Exception {
        LOG.debug("pos={}",
	            fileChannel.position());
    	long previousPos = fileChannel.position();
    	if (previousPos % 16 != 0) {
    		previousPos = previousPos + 16 - previousPos % 16;
    	}
    	
        for (int i = 0; i < folderEntries.size(); i++) {
        	FolderEntry folderEntry = folderEntries.get(i);
        	
        	for (int j = 0; j < folderEntry.fatEntries.size(); j++) {
            	FatEntry fatEntry = folderEntry.fatEntries.get(j);
            	
//            	if (previousPos % 16 != 0) {
//            		previousPos = previousPos + 16 - previousPos % 16;
//            	}
                raf.seek(previousPos + fatEntry.start);
                LOG.debug("start={}, length={}, name={}, seek={}",
                		previousPos, fatEntry.length, folderEntry.name + fatEntry.name, fatEntry.start);
                
                byte[] bytes = new byte[fatEntry.length];
                raf.readFully(bytes);
                
                //previousPos = previousPos + fatEntry.length;

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
        public int start;
        public int length;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
