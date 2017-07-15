package org.xpen.eidos.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class DirFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(DirFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    public void setFatEntries(List<FatEntry> fatEntries) {
		this.fatEntries = fatEntries;
	}

	protected String fileName;
    
    public DirFile() {
    }
    
    public DirFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".dir"), "r");
        fileChannel = raf.getChannel();
    }

	public void decode() throws Exception {
    	decodeFat();
        decodeDat();
    }

	private void decodeFat() throws Exception {
		for (int i = 0 ; i < 1270; i++) {
	        ByteBuffer buffer = ByteBuffer.allocate(0x2C);
	        buffer.order(ByteOrder.LITTLE_ENDIAN);
	        buffer.limit(0x2C);
	        fileChannel.read(buffer);
	        buffer.flip();
	        
        	FatEntry fatEntry = new FatEntry();
        	fatEntries.add(fatEntry);
        	fatEntry.decode(buffer);
		}
	}

	protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            if (fatEntry.unknown != 0) {
            	continue;
            }
            
    		raf.seek(fatEntry.offset);
    		byte[] bytes = new byte[fatEntry.size];
    		raf.readFully(bytes);

            File outFile = null;
            String extension = FilenameUtils.getExtension(fatEntry.fname);
            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName + "/" + extension, String.valueOf(fatEntry.fname));
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
        public int offset;
        public int size;
        public int unknown;

		public void decode(ByteBuffer buffer) throws Exception {
			fname = ByteBufferUtil.getNullTerminatedString(buffer);
			buffer.position(0x20);
			unknown = buffer.get();
			buffer.position(0x24);
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
