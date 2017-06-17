package org.xpen.namco.fileformat;

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

public class IdxFile {
    public static final long MAGIC_GENESTRT = 0x54525453454E4547L; //'GENESTRT'
    public static final long MAGIC_PACKTOC  = 0x20434F544B434150L; //'PACKTOC '
    public static final long MAGIC_GENEEOF  = 0x20464F45454E4547L; //'GENEEOF '
    //public static final int FAT_START = 0x8430;
    //public static final int FAT_START = 0x0360;
    //public static final int FILE_COUNT = 20000;
    public static int GENESTRT_START;
    public static int PACKTOC_START;
    
    private static final Logger LOG = LoggerFactory.getLogger(IdxFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

	protected List<FileNameEntry> fileNameEntries = new ArrayList<FileNameEntry>();
    protected String fileName;
    
    public IdxFile() {
    }
    
    public IdxFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".idx"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * APK File format
     * 000070--0C9F80  PACKTOC
     * 0C9F80--0CAB30  PACKFSLS
     * 0CAB30--137130  GENESTRT
     * 137130--        GENEEOF
     */
    public void decode() throws Exception {
    	
    	decodeSectionStart();
    	decodeFileNames();
    	decodePackToc();
        
    }

	private void decodeSectionStart() throws Exception {
        raf.seek(0x10);
        
        while (true) {
	        ByteBuffer buffer = ByteBuffer.allocate(16);
	        buffer.order(ByteOrder.LITTLE_ENDIAN);
	        buffer.limit(16);
	        fileChannel.read(buffer);
	        buffer.flip();
	        
	        long magic = buffer.getLong();
	        if (magic == MAGIC_GENESTRT) {
	        	GENESTRT_START = (int)(raf.getFilePointer() - 16);
	        } else if (magic == MAGIC_PACKTOC) {
	        	PACKTOC_START = (int)(raf.getFilePointer() - 16);
	        } else if (magic == MAGIC_GENEEOF) {
	        	break;
	        }
	        
	        int sectionLength = (int)buffer.getLong();
	        buffer = ByteBuffer.allocate(sectionLength);
	        buffer.order(ByteOrder.LITTLE_ENDIAN);
	        buffer.limit(sectionLength);
	        fileChannel.read(buffer);
	        buffer.flip();
        }
		
	}

	private void decodeFileNames() throws Exception {
        raf.seek(GENESTRT_START);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(16);
        fileChannel.read(buffer);
        buffer.flip();
        
        long magic = buffer.getLong();
        if (magic != MAGIC_GENESTRT) {
        	throw new RuntimeException("bad magic GENESTRT");
        }
        
        int sectionLength = (int)buffer.getLong();
        buffer = ByteBuffer.allocate(sectionLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(sectionLength);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fileCount = buffer.getInt();
        int folderCount = buffer.getInt();
        int unknown1 = buffer.getInt();
        int unknown2 = buffer.getInt();
        
        for (int i = 0 ; i < fileCount; i++) {
        	FileNameEntry fileNameEntry = new FileNameEntry();
        	fileNameEntries.add(fileNameEntry);
        	
        	fileNameEntry.fnameOffset = buffer.getInt();
        }
        
        // data is aligned to 16 bytes
        if (buffer.position() % 16 != 0) {
            int skip = 16 - (buffer.position() % 16);
            buffer.position(buffer.position() + skip);
        }
        
        for (int i = 0 ; i < fileCount; i++) {
        	FileNameEntry fileNameEntry = fileNameEntries.get(i);
        	fileNameEntry.fname = ByteBufferUtil.getNullTerminatedString(buffer);
        	LOG.debug("i={}, fname={}", i, fileNameEntry.fname);
        }
		
	}

    private void decodePackToc() throws Exception {
        raf.seek(PACKTOC_START);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(16);
        fileChannel.read(buffer);
        buffer.flip();
        
        long magic = buffer.getLong();
        if (magic != MAGIC_PACKTOC) {
        	throw new RuntimeException("bad magic PACKTOC");
        }
        
        int sectionLength = (int)buffer.getLong();
        buffer = ByteBuffer.allocate(sectionLength);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(sectionLength);
        fileChannel.read(buffer);
        buffer.flip();
        
        int entrySize = buffer.getInt();
        int fileCount = buffer.getInt();
        int folderCount = buffer.getInt();
        int zero = buffer.getInt();
        
        for (int i = 0 ; i < folderCount; i++) {
        	buffer.position(buffer.position() + entrySize);
        }
        
        
        //int fileCount = FILE_COUNT;
        //raf.seek(FAT_START);
        
        for (int i = folderCount; i < fileCount; i++) {
            FatEntry fatEntry = this.new FatEntry();
            fatEntries.add(fatEntry);
            
//            fatEntry.fatOffset = (int)raf.getFilePointer();
//            buffer = ByteBuffer.allocate(40);
//            buffer.order(ByteOrder.LITTLE_ENDIAN);
//            buffer.limit(40);
//            fileChannel.read(buffer);
//            buffer.flip();
            
            fatEntry.decode(buffer);
        }
        
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);           
        	LOG.debug("{},{},{},{},{},{},{},{},{},{},{}", fatEntry.fatOffset,fatEntry.offset,
        			fatEntry.size,fatEntry.index,fatEntry.compressFlag,fatEntry.unknown2,
        			fatEntry.unknown3,fatEntry.unknown4,fatEntry.unknown5,fatEntry.zsize,
        			fatEntry.unknown7);
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
        public int fatOffset;
        public int compressFlag;
        public int offset;
        public int size;
        public int unknown2;
        public int unknown3;
        public int unknown4;
        public int unknown5;
        public int index;
        public int zsize;
        public int unknown7;

    	//from 8C28
    	//--------40
    	//4 offset 0445B800
    	//4 skip
    	//4 size 35F8
    	//16 skip
    	//4 flag 0406
    	//8 skip
    	//---------
//		public void decode2(ByteBuffer buffer) throws Exception {
//			offset = buffer.getInt();
//			compressFlag = buffer.getInt();
//			size = buffer.getInt();
//			unknown2 = buffer.getInt();
//			unknown3 = buffer.getInt();
//			unknown4 = buffer.getInt();
//			unknown5 = buffer.getInt();
//			index = buffer.getInt();
//			zsize = buffer.getInt();
//			unknown7 = buffer.getInt();
//		}
		
		public void decode(ByteBuffer buffer) throws Exception {
			compressFlag = buffer.getInt();
			index = buffer.getInt();
			LOG.debug("position={}, index={}", buffer.position(), index);
			fname = fileNameEntries.get(index).fname;
			unknown2 = buffer.getInt();
			unknown3 = buffer.getInt();
			offset = buffer.getInt();
			unknown4 = buffer.getInt();
			size = buffer.getInt();
			unknown5 = buffer.getInt();
			zsize = buffer.getInt();
			unknown7 = buffer.getInt();
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
