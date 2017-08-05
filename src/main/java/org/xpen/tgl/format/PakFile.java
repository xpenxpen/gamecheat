package org.xpen.tgl.format;

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

public class PakFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(PakFile.class);
    
    public static final int MAGIC_IPAC = 0x43415049; //IPAC
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    protected String fileName;
    public boolean isNoCompress = false;
    
    public PakFile() {
    }
    
    public PakFile(String fileName) throws Exception {
        this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName + ".pak"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * PAK File format
     */
    public void decode() throws Exception {
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(8);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_IPAC) {
            throw new RuntimeException("bad magic");
        }
        
        int fileCount = buffer.getInt();
        
        for (int i = 0; i < fileCount; i++) {
            buffer = ByteBuffer.allocate(24);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(24);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = new FatEntry();
            fatEntries.add(fatEntry);
            fatEntry.decode(buffer);
        }
    }
        
    private void decodeDat() throws Exception {
        int errorCount = 0;
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);  
            
            byte[] bytes;

            raf.seek(fatEntry.offset);
            bytes = new byte[fatEntry.size];
            raf.readFully(bytes);

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName, fatEntry.fname);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(bytes, os);
            os.close();
        }
        
        LOG.info("errorCount={}", errorCount);
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int offset;
        public int size;
        
        public void decode(ByteBuffer buffer) throws Exception {
            fname = ByteBufferUtil.getNullTerminatedString(buffer);
            buffer.position(16);
            offset = buffer.getInt();
            size = buffer.getInt();
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
