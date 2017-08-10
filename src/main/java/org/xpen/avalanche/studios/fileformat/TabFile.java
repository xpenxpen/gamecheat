package org.xpen.avalanche.studios.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class TabFile {
    private static final Logger LOG = LoggerFactory.getLogger(TabFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

	//protected List<FileNameEntry> fileNameEntries = new ArrayList<FileNameEntry>();
    protected String fileName;
    
    public TabFile() {
    }
    
    public TabFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".tab"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * Tab File format
     */
    public void decode() throws Exception {
    	decodeFat();
    }

	private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fileCount = (int)(fileChannel.size() - 4) / 12;
        
        buffer = ByteBuffer.allocate(fileCount * 12);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(fileCount * 12);
        fileChannel.read(buffer);
        buffer.flip();
        
        for (int i = 0 ; i < fileCount; i++) {
        	FatEntry fatEntry = new FatEntry();
        	fatEntries.add(fatEntry);
        	fatEntry.decode(buffer);
        	
        }
	}

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
    
    public List<FatEntry> getFatEntries() {
		return fatEntries;
	}

    public class FatEntry {
        public String fname;
        public long crc;
        public int offset;
        public int size;

		public void decode(ByteBuffer buffer) throws Exception {
			crc = buffer.getInt() & 0xFFFFFFFFL;
			offset = buffer.getInt();
			size = buffer.getInt();
			LOG.debug(toString());
		}
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    public class FileNameEntry {
        public String fname;
        public int fnameOffset;
    }
}
