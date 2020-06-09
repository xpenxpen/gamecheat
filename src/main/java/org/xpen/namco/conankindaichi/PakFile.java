package org.xpen.namco.conankindaichi;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.HandleCount;
import org.xpen.util.compress.NintendoLz10Compressor;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class PakFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(PakFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private String folderName;
    private String fileName;
    public Class<?> fileTypeDetector;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    
    /**
     * PAK File format
     * 4 entryCount
     * ----LOOP
     * |--FAT
     * |   |--4 Offset
     * ----
     * @param countPair 
     *
     */
    public void decode(String folderName, String fileName, File f, HandleCount countPair) throws Exception {
        this.folderName = folderName;
        this.fileName = fileName;
        raf = new RandomAccessFile(f, "r");
        fileChannel = raf.getChannel();
        decodeFat();
        decodeDat(countPair);
    }

    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        
        int entryCount = buffer.getInt();
        int toReadBufferCount = entryCount * 4 + 4;
        
        fileChannel.position(4);
        buffer = ByteBuffer.allocate(toReadBufferCount);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();

        for (int i = 0; i < entryCount + 1; i++) {
            FatEntry fatEntry = new FatEntry();
            fatEntry.decode(buffer);
            fatEntries.add(fatEntry);
        }
        for (int i = 0; i < entryCount; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            FatEntry nextFatEntry = fatEntries.get(i+1);
            fatEntry.length = nextFatEntry.start - fatEntry.start;
        }
        
    }

    private void decodeDat(HandleCount countPair) throws Exception {
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.start);
            
            byte[] bytes = new byte[fatEntry.length];
            raf.readFully(bytes);
            
            String threeDigit = StringUtils.leftPad(String.valueOf(i + 1), 3, '0');
            fatEntry.fname = threeDigit;
            
            try {
                bytes = NintendoLz10Compressor.decompress(bytes);
            } catch (Exception e) {
                fatEntry.fname = "_notcompress" + threeDigit;
            }
            
            detectAndHandle(fatEntry, bytes, countPair);
        }
        
    }

    private void detectAndHandle(FatEntry entry, byte[] b, HandleCount countPair) throws Exception {
        Method detectMethod = fileTypeDetector.getMethod("detect", String.class, String.class);
        Method getFileTypeHandlerMethod = fileTypeDetector.getMethod("getFileTypeHandler", String.class);
        String detectedType = (String)detectMethod.invoke(null, this.fileName, entry.fname);
        FileTypeHandler fileTypeHandler = (FileTypeHandler)getFileTypeHandlerMethod.invoke(null, detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", true);
        }
        
        boolean isUnknown = false;
        
        if (!(fileTypeHandler instanceof SimpleCopyHandler)) {
            countPair.totalCount++;
        }
        try {
            fileTypeHandler.handle(b, this.fileName, entry.fname, isUnknown);
            if (!(fileTypeHandler instanceof SimpleCopyHandler)) {
                countPair.handleCount++;
            }
        } catch (Exception e) {
            LOG.error("Error: "  + this.fileName + "_" + entry.fname, e);
        }
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int start;
        public int length;
        
        public void decode(ByteBuffer buffer) {
            this.start = buffer.getInt();
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
