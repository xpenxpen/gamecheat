package org.xpen.odinsoft.fileformat;

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

public class PakFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(PakFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    int errorCount = 0;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    public void setFatEntries(List<FatEntry> fatEntries) {
        this.fatEntries = fatEntries;
    }

    protected String fileName;
    
    public PakFile() {
    }
    
    public PakFile(String fileName) throws Exception {
        this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".pak"), "r");
        fileChannel = raf.getChannel();
    }

    public void decode() throws Exception {
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(16);
        fileChannel.read(buffer);
        buffer.flip();
        
        buffer.position(4);
        int fatCount = buffer.getInt();
        int fatStart = buffer.getInt();
        
        fileChannel.position(fatStart);
        for (int i = 0; i < fatCount; i++) {
            FatEntry fatEntry = new FatEntry();
            fatEntries.add(fatEntry);
                
            buffer = ByteBuffer.allocate(64);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(64);
            fileChannel.read(buffer);
            buffer.flip();
            
            fatEntry.decode(buffer);
        }
    }

    protected void decodeDat() throws Exception {
        int errorCount = 0;
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);  
            
            byte[] bytes;
            boolean hasException = false;
            
            bytes = noCompress(fatEntry);
            
            detectAndHandle(fatEntry, bytes);
            
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
        String detectedType = PakFileTypeDetector.detect(entry, b);
        FileTypeHandler fileTypeHandler = PakFileTypeDetector.getFileTypeHandler(detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", true);
        }
        
        boolean isUnknown = false;
        
        try {
            fileTypeHandler.handle(b, this.fileName, String.valueOf(entry.fname), isUnknown);
        } catch (Exception e) {
            LOG.error("fileTypeHandler.handle", e);
            errorCount++;
            fileTypeHandler = new SimpleCopyHandler("unknown", true);
            fileTypeHandler.handle(b, this.fileName, String.valueOf(entry.fname), true);
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

        public void decode(ByteBuffer buffer) throws Exception {
            size = buffer.getInt();
            offset = buffer.getInt();
            buffer.position(buffer.position() + 16);
            fname = ByteBufferUtil.getNullTerminatedString(buffer);
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
