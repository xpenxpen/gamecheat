package org.xpen.bigfish.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class Bfg83File {
    public static final byte[] MAGIC_BFG = {0x62, 0x66, 0x67}; //BFG
    public static final byte XOR_KEY = (byte)0x8F; //dat xor key
    
    private static final Logger LOG = LoggerFactory.getLogger(Bfg83File.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    private int ver;
    
    public Bfg83File() {
    }
    
    public Bfg83File(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".gpk"), "r");
        fileChannel = raf.getChannel();
    }

	public void decode() throws Exception {
    	int offset = decodeFat();
        decodeDat(offset);
    }
    
    /**
     * BFG83 File format
     * LOOP
     * |
     * --FAT
     *     |align 16 bytes 0x10--0x90
     *     ---- math + XOR to decode fname, size(detail see code)
     *          Final 0x80 bytes are ALL 0 (zero)
     * 
     * LOOP
     * |
     * --DAT
     *    ALL byte  XOR 8F -->real byte
     * 
     */
    public int decodeFat() throws Exception {
        byte[] magic = new byte[3];
        raf.readFully(magic);
        if (!Arrays.equals(magic, MAGIC_BFG)) {
            throw new RuntimeException("bad magic");
        }
        
        ver = raf.read();
        if (ver != 0x83) {
            throw new RuntimeException("unsupportted ver");
        }
        
        
        int startOffset = 0x10;
        int offset = startOffset;
        
	    
        File outFile = null;
        outFile = new File(UserSetting.rootOutputFolder + "/" + this.fileName, "11.txt");
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        //OutputStream os = new FileOutputStream(outFile);
        
        for (;;) {
            // data is aligned to 16 bytes, ugh
            if (offset % 16 != 0) {
                int skip = 16 - (offset % 16);
                offset += skip;
            }
            if (offset >= raf.length()) {
            	break;
            }
            //LOG.debug("seek:" + offset);
            raf.seek(offset);
            ByteBuffer buffer = ByteBuffer.allocate(0x80);
    	    buffer.order(ByteOrder.LITTLE_ENDIAN);
    	    buffer.limit(0x80);
    	    fileChannel.read(buffer);
    	    buffer.flip();
    	    
            ByteBuffer plainBuffer = ByteBuffer.allocate(0x80);
    	    
    	    byte KEY = (byte)0xBC;
    	    for (int i = 0; i < 0x80; i++) {
    		    byte tmp = buffer.get();
    		    tmp ^= KEY;
    	        KEY = (byte)((KEY & 0xFF) * 9 + i + 5);
    	        plainBuffer.put(tmp);
    	    }
            
            //IOUtils.write(plainBuffer.array(), os);
            
            plainBuffer.flip();
            String fname = ByteBufferUtil.getNullTerminatedString(plainBuffer);
            plainBuffer.position(100);
            String fileSizeStr = ByteBufferUtil.getNullTerminatedString(plainBuffer);
            //LOG.debug(fname);
            //LOG.debug(fileSizeStr);
            if (fname.equals("") && fileSizeStr.equals("")) {
                offset += 0x80;
            	break;
            }
            
            offset += 0x80;
            
            int num = convertOctalFileSize(fileSizeStr);
            //LOG.debug("num={}", num);
            
	    	FatEntry fatEntry = new FatEntry();
	    	fatEntry.fname = fname;
	    	fatEntry.start = offset;
	    	fatEntry.size = num;
	    	fatEntries.add(fatEntry);
	    	
	    	//offset += fatEntry.size;
        }
	    
        //os.close();
	        
        return offset;
    }


    protected void decodeDat(int offset) throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);           
			LOG.debug("fname={}, start={}, size={}", fatEntry.fname, fatEntry.start, fatEntry.size);
			if (fatEntry.size == 0) {
				continue;
			}
            
            // data is aligned to 16 bytes, ugh
            if (offset % 16 != 0) {
                int skip = 16 - (offset % 16);
                offset += skip;
            }
            
            byte[] bytes = new byte[fatEntry.size];
            fileChannel.position(offset);
            raf.readFully(bytes);
            for (int j = 0; j < bytes.length; j++) {
            	bytes[j] ^= XOR_KEY;
            }
            
            offset += fatEntry.size;
            

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder + "/" + this.fileName, fatEntry.fname);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(bytes, os);
            os.close();
        }
    }
    
    private int convertOctalFileSize(String fileSizeStr) {
    	return Integer.parseInt(fileSizeStr, 8);
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int start;
        public int size;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
