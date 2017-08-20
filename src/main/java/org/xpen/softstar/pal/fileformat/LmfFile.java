package org.xpen.softstar.pal.fileformat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class LmfFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(LmfFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public String format;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    public void setFatEntries(List<FatEntry> fatEntries) {
		this.fatEntries = fatEntries;
	}

	protected String fileName;
    
    public LmfFile() {
    }
    
    public LmfFile(String fileName) throws Exception {
    	this.format = FilenameUtils.getExtension(fileName);
    	this.fileName = FilenameUtils.getBaseName(fileName);
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }

	public void decode() throws Exception {
    	decodeFat();
        decodeDat();
    }

	private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fatStartOffset = buffer.getInt();
		fileChannel.position(fatStartOffset);
        
        
        while (true) {
        	FatEntry fatEntry = new FatEntry();
        	
            buffer = ByteBuffer.allocate(16);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(16);
            fileChannel.read(buffer);
            buffer.flip();
            
        	fatEntry.decode(buffer);
        	
        	if (fatEntry.offset == 0 && fatEntry.size == 0 && fatEntry.unknown == 0) {
        		break;
        	}
        	fatEntries.add(fatEntry);
        }
	}

	protected void decodeDat() throws Exception {
		int errorCount = 0;
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            byte[] bytes;
            boolean hasException = false;
            
            try {
                bytes = noCompress(fatEntry);
            } catch (NegativeArraySizeException e) {
            	errorCount++;
            	continue;
            }
            
        	detectAndHandle(fatEntry, bytes);
            

//            File outFile = null;
//            //String extension = FilenameUtils.getExtension(fatEntry.fname);
//            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName, String.valueOf(fatEntry.crc));
//            if (hasException) {
//                outFile = new File(UserSetting.rootOutputFolder + "/" + fileName + "/exception",  String.valueOf(fatEntry.crc));
//            }
//            File parentFile = outFile.getParentFile();
//            parentFile.mkdirs();
//            
//            OutputStream os = new FileOutputStream(outFile);
//            
//            IOUtils.write(bytes, os);
//            os.close();
        }
        
        System.out.println("errorCount=" + errorCount);
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
    	String detectedType;
    	//if (this.format.equals("lmf")) {
    	//	detectedType = "tsw";
    	//} else {
    		detectedType = LmfFileTypeDetector.detect(entry, b);
    	//}
    	
        FileTypeHandler fileTypeHandler = TswFileTypeDetector.getFileTypeHandler(detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", true);
        }
        
        boolean isUnknown = false;
        
        fileTypeHandler.handle(b, this.fileName, String.valueOf(entry.fname), isUnknown);
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int offset;
        public int size;
        public int unknown;

		public void decode(ByteBuffer buffer) throws Exception {
			//fname = ByteBufferUtil.getNullTerminatedString(buffer, "Big5");
			offset = buffer.getInt();
			size = buffer.getInt();
			unknown = buffer.getInt();
			fname = String.valueOf(unknown);
			buffer.getInt();
			LOG.debug(toString());
		}
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
