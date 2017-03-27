package org.xpen.dunia2.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.fat2.Entry;
import org.xpen.dunia2.fileformat.fat2.EntrySerializer;
import org.xpen.dunia2.fileformat.fat2.EntrySerializerV9;
import org.xpen.dunia2.fileformat.fat2.SubFatEntry;

public class Fat2File {
    public static final int MAGIC_FAT2 = 0x46415432; //'FAT2'
    
    private static final Logger LOG = LoggerFactory.getLogger(Fat2File.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private int version;
    private int platform;
    
    private List<Entry> entries = new ArrayList<Entry>();

    private List<SubFatEntry> subFats = new ArrayList<SubFatEntry>();
    
    private EntrySerializer[] entrySerializers = {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new EntrySerializerV9()
    };
    
    public Fat2File(File file) throws Exception {
        
        raf = new RandomAccessFile(file, "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * FAT2 File format
     * 4 'FAT2'
     * 4 version
     * 4 (optional) platform
     * 4 (optional) subfat TotalEntryCount
     * 4 (optional) subfat Count
     * ----4 entry Count
     * |
     * | 20 LOOP entry
     * |
     * ----
     * 4 unknown1Count = 0000
     * ----4 unknown2Count
     * |
     * | 16 LOOP unknown
     * |
     * ----
     * ----sub fat
     * |
     * |  4 LOOP subfatEntryCount
     * |   ----
     * |   | LOOP entry
     * |   ----
     * |
     * ----
     * 
     *
     */
    public void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.limit(8);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_FAT2) {
            throw new RuntimeException("bad magic");
        }
        
        int version = buffer.getInt();
        if (version < 2 || version > 9) {
            throw new RuntimeException("unsupported file version");
        }
        this.version = version;
        
        if (version >= 3) {
            buffer.limit(4);
            buffer.rewind();
            fileChannel.read(buffer);
            buffer.flip();
            
            int unknown74 = buffer.getInt();
            int platform = unknown74 & 0xFF;
            unknown74 >>= 8;
            this.platform = platform;
        }
        
        int subfatTotalEntryCount = 0;
        int subfatCount = 0;

        if (version >= 9) {
            
            buffer.limit(8);
            buffer.rewind();
            fileChannel.read(buffer);
            buffer.flip();
            
            subfatTotalEntryCount = buffer.getInt();
            if (subfatTotalEntryCount < 0) {
                throw new RuntimeException("invalid subfat total entry count");
            }

            subfatCount = buffer.getInt();
            if (subfatCount < 0) {
                throw new RuntimeException("invalid subfat count");
            }
        }

        buffer.limit(4);
        buffer.rewind();
        fileChannel.read(buffer);
        buffer.flip();
        int entryCount = buffer.getInt();
        
        for (int i = 0; i < entryCount; i++) {
            EntrySerializer entrySerializer = entrySerializers[version];
            if (entrySerializer == null) {
                throw new RuntimeException("unimplemented EntrySerializer version:" + version);
            }
            Entry entry = entrySerializer.deserialize(fileChannel);
            entries.add(entry);
        }

        buffer.limit(4);
        buffer.rewind();
        fileChannel.read(buffer);
        buffer.flip();
        int unknown1Count = buffer.getInt();
        
        if (version >= 7) {
            buffer.limit(4);
            buffer.rewind();
            fileChannel.read(buffer);
            buffer.flip();
            
            int unknown2Count = buffer.getInt();
            for (int i = 0; i < unknown2Count; i++) {
                buffer.limit(16);
                buffer.rewind();
                fileChannel.read(buffer);
                buffer.flip();
            }
        }
        
        for (int i = 0; i < subfatCount; i++) {
            SubFatEntry subFat = new SubFatEntry();
            
            buffer.limit(4);
            buffer.rewind();
            fileChannel.read(buffer);
            buffer.flip();
            
            int subfatEntryCount = buffer.getInt();
            for (int j = 0; j < subfatEntryCount; j++) {
                
                EntrySerializer entrySerializer = entrySerializers[version];
                if (entrySerializer == null) {
                    throw new RuntimeException("unimplemented EntrySerializer version:" + version);
                }
                Entry entry = entrySerializer.deserialize(fileChannel);
                subFat.entries.add(entry);
            }
            this.subFats.add(subFat);
        }
        
        //Check SUM(SubFats) = subfat TotalEntryCount
        int totalEntryCountCheck = 0;
        for (SubFatEntry subFatEntry : subFats) {
            for (Entry entry : subFatEntry.entries) {
                totalEntryCountCheck++;
            }
            
        }
        
        if (totalEntryCountCheck != subfatTotalEntryCount) {
            throw new RuntimeException("subfat total entry count mismatch(" + totalEntryCountCheck + "!=" + subfatTotalEntryCount +")");
        }
        LOG.debug("entryCount={}, subfatCount={}, totalEntryCountCheck={}, subfatTotalEntryCount={}",
                entryCount, subfatCount, totalEntryCountCheck, subfatTotalEntryCount);
        
        buffer.clear();
        
    }
    
    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
    
    public List<Entry> getEntries() {
        return entries;
    }

}
