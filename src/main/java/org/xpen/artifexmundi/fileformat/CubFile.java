package org.xpen.artifexmundi.fileformat;

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

public class CubFile {
    public static final int MAGIC1 = 0x96F4E3F5;
    public static final int MAGIC_CUB = 0x627563;
    public static final byte XOR_KEY = (byte)0x96; //xor key
    public static final int XOR_KEY_INT = 0x96969696;
    
    private static final Logger LOG = LoggerFactory.getLogger(CubFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    private int fileCount;
    boolean needXor = false;
    
    public CubFile() {
    }
    
    public CubFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".cub"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * CUB File format
     * ALL byte  XOR 0x96 -->real byte
     * or not XOR
     * 4 CUB\0
     * 4 1.0
     * 4 entryCount
     * 0x100 Paczka danych CUBE
     * ----LOOP
     * | 0x100 filename
     * | 4 offset
     * | 4 size
     * ----
     */
    public void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(0x10C);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(0x10C);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic == MAGIC1) {
        	needXor = true;
        } else if (magic == MAGIC_CUB) {
        	needXor = false;
        } else {
        	throw new RuntimeException("bad magic");
        }
        
        int version = buffer.getInt();
        fileCount = buffer.getInt();
        if (needXor) {
            fileCount = (fileCount ^ XOR_KEY_INT) & 0xFFFFFFFF;
        }
        

        decodeFat();
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);           
        	LOG.debug("fatEntry={}", fatEntry);
        }
        decodeDat();
        
    }

    private void decodeFat() throws Exception {
    	for (int i = 0; i < fileCount; i++) {
    		byte[] fatByte = new byte[0x108];
    		raf.readFully(fatByte);
    		if (needXor) {
	            for (int j = 0; j < fatByte.length; j++) {
	            	fatByte[j] ^= XOR_KEY;
	            }
    		}
            ByteBuffer buffer = ByteBuffer.wrap(fatByte);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            String fName = ByteBufferUtil.getNullTerminatedString(buffer);
            
            buffer.position(0x100);
            int offset = buffer.getInt();
            int size = buffer.getInt();
            
	    	FatEntry fatEntry = new FatEntry();
	    	fatEntry.fname = fName;
	    	fatEntry.offset = offset;
	    	fatEntry.size = size;
	    	fatEntries.add(fatEntry);
    	}
	}

	protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);           
            
            byte[] bytes = new byte[fatEntry.size];
            raf.readFully(bytes);
    		if (needXor) {
	            for (int j = 0; j < bytes.length; j++) {
	            	bytes[j] ^= XOR_KEY;
	            }
    		}
            

            File outFile = null;
            String extension = FilenameUtils.getExtension(fatEntry.fname);
            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName + "/" + extension, fatEntry.fname);
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
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
