package org.xpen.capcom.aceattorney.gk1.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

public class BinFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(BinFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private String fileName;
    
    private List<FatEntry> fatEntries = new ArrayList<>();
    
    /**
     * BIN File format
     * 4 LAST offet
     * ----LOOP
     * |--FAT
     * |   |--4 Offset
     * ----
     *
     */
    public void decode(String fileName, File f) throws Exception {
        this.fileName = fileName;
        raf = new RandomAccessFile(f, "r");
        fileChannel = raf.getChannel();
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        
        int lastOffset = buffer.getInt();
        int entryCount = (lastOffset - 1) / 4 - 1;
        int toReadBufferCount = entryCount * 4;
        
        fileChannel.position(8);
        buffer = ByteBuffer.allocate(toReadBufferCount);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();

        for (int i = 0; i < entryCount; i++) {
            FatEntry fatEntry = new FatEntry();
            fatEntry.decode(buffer);
            fatEntries.add(fatEntry);
        }
        for (int i = 0; i < entryCount - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            FatEntry nextFatEntry = fatEntries.get(i+1);
            fatEntry.length = nextFatEntry.start - fatEntry.start;
        }
        FatEntry lastFatEntry = fatEntries.get(entryCount - 1);
        lastFatEntry.length = (int)fileChannel.size() - lastFatEntry.start;
        
        for (int i = 0; i < entryCount; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            fatEntry.start += 4;
            fatEntry.length -= 4;
        }
        
    }

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.start);
            
            byte[] bytes = new byte[fatEntry.length];
            raf.readFully(bytes);
            
            String fourDigit = StringUtils.leftPad(String.valueOf(i + 1), 4, '0');
            fatEntry.fname = fourDigit;
            //detectAndHandle(fatEntry, bytes);
            FileTypeHandler fileTypeHandler = new SimpleCopyHandler("unknown", false);
            fileTypeHandler.handle(bytes, this.fileName, fatEntry.fname, false);
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
