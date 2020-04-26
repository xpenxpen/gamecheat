package org.xpen.aquaplus.fileformat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.ubisoft.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.DeflateCompressor;

public class DarFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(DarFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    protected String fileName;
    public boolean isNoCompress = false;
    
    public DarFile() {
    }
    
    public DarFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".dar"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * DAR File format
     * 4 fileCount
     * 4 unknown
     * 4 unknown
     * 4 FAT start offset
     * 0x0010--0x8780 file names
     * 0x8780--FAT
     * |
     * |--4 file name offset
     * |--4 zsize
     * |--4 size
     * |--4 offset
     */
    public void decode() throws Exception {
    	decodeFat();
    	decodeDat();
    }

	private void decodeFat() throws Exception {
        ByteBuffer headerbuffer = ByteBuffer.allocate(16);
        headerbuffer.order(ByteOrder.LITTLE_ENDIAN);
        headerbuffer.limit(16);
        fileChannel.read(headerbuffer);
        headerbuffer.flip();
        
        int fileCount = headerbuffer.getInt();
        headerbuffer.getInt();
        headerbuffer.getInt();
        int fatStartOffset = headerbuffer.getInt();
        
        
        ByteBuffer fileNamebuffer = ByteBuffer.allocate(fatStartOffset-16);
        fileNamebuffer.order(ByteOrder.LITTLE_ENDIAN);
        fileNamebuffer.limit(fatStartOffset-16);
        fileChannel.read(fileNamebuffer);
        fileNamebuffer.flip();
        
        //get FAT
        raf.seek(fatStartOffset);
        ByteBuffer buffer = ByteBuffer.allocate(fileCount * 16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(fileCount * 16);
        fileChannel.read(buffer);
        buffer.flip();
        for (int i = 0 ; i < fileCount; i++) {
        	FatEntry fatEntry = new FatEntry();
        	fatEntries.add(fatEntry);
        	fatEntry.decode(buffer, fileNamebuffer);
        	
        }
	}
	
	protected void decodeDat() throws Exception {
		int errorCount = 0;
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);  
            
            byte[] bytes;
            boolean hasException = false;
            
        	if (isNoCompress) {
            	bytes = noCompress(fatEntry);
        	} else {
                //deflate
                raf.seek(fatEntry.offset);
                bytes = new byte[fatEntry.size];
                byte[] compressedBytes = new byte[fatEntry.zsize];
                raf.readFully(compressedBytes);
                try {
                    DeflateCompressor.decompress(compressedBytes, bytes);
                } catch (Exception e) {
                	errorCount++;
                	hasException = true;
                	bytes = noCompress(fatEntry);
                    //throw new RuntimeException(e);
                }
        	}
        	
        	detectAndHandle(fatEntry, bytes);

//            File outFile = null;
//            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName, fatEntry.fname);
//            if (hasException) {
//                outFile = new File(UserSetting.rootOutputFolder + "/" + fileName + "/exception", fatEntry.fname);
//            }
//            File parentFile = outFile.getParentFile();
//            parentFile.mkdirs();
//            
//            OutputStream os = new FileOutputStream(outFile);
//            
//            IOUtils.write(bytes, os);
//            os.close();
        }
        
        LOG.info("errorCount={}", errorCount);
    }

	private byte[] noCompress(FatEntry fatEntry) throws IOException {
		byte[] bytes;
		//no compress
		raf.seek(fatEntry.offset);
		bytes = new byte[fatEntry.size];
		raf.readFully(bytes);
		return bytes;
	}


    private void detectAndHandle(FatEntry entry, byte[] b) throws Exception {
        String detectedType = FileTypeDetector.detect(entry, b);
        FileTypeHandler fileTypeHandler = FileTypeDetector.getFileTypeHandler(detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", true);
        }
        
        boolean isUnknown = false;
        
        fileTypeHandler.handle(b, this.fileName, entry.fname, isUnknown);
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
        public int zsize;
		
		public void decode(ByteBuffer buffer, ByteBuffer fileNamebuffer) throws Exception {
			int fileNameOffset = buffer.getInt();
			fileNamebuffer.position(fileNameOffset-16);
			fname = ByteBufferUtil.getNullTerminatedString(fileNamebuffer);
			zsize = buffer.getInt();
			size = buffer.getInt();
			offset = buffer.getInt();
			LOG.debug(toString());
		}
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
