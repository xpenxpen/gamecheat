package org.xpen.softstar.richman.fileformat;

import java.awt.Color;
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
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.UserSetting;

public class MkfFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(MkfFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private String fileName;

    private int errorCount;
    
    
    public MkfFile(String fileName) throws Exception {
    	this.fileName = fileName;
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".mkf"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * MKF File format
     *
     */
    public void decode() throws Exception {
        decodeFat();
        decodeDat();
    }


    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fatStartOffset = buffer.getInt();
        int lastOffset = fatStartOffset;
        
        while (lastOffset < fileChannel.size()) {
            fileChannel.position(lastOffset);
            buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            fileChannel.read(buffer);
            buffer.flip();
            
            int fatOffset = buffer.getInt();
            
            fileChannel.position(fatOffset);
            buffer = ByteBuffer.allocate(16);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = new FatEntry();
            fatEntry.decode(buffer);
            fatEntries.add(fatEntry);
            
            lastOffset += 4;
        }
    }

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.offset);
            
            byte[] bytes = new byte[fatEntry.uncompressedSize];
            raf.readFully(bytes);
            
            String threeDigit = StringUtils.leftPad(String.valueOf(i + 1), 3, '0');
            fatEntry.fname = threeDigit;
            detectAndHandle(fatEntry, bytes);
        }
        
    }

    private void detectAndHandle(FatEntry entry, byte[] b) throws Exception {
        String detectedType = MkfFileTypeDetector.detect(entry, b);
        FileTypeHandler fileTypeHandler = MkfFileTypeDetector.getFileTypeHandler(detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", false);
        }
        
        boolean isUnknown = true;
        
        try {
            fileTypeHandler.handle(b, this.fileName, entry.fname, isUnknown);
        } catch (Exception e) {
            errorCount++;
            fileTypeHandler = new SimpleCopyHandler("unknown", false);
            fileTypeHandler.handle(b, this.fileName, entry.fname, isUnknown);
        }
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int offset;
        public int compressedSize;
        public int uncompressedSize;
        
        public void decode(ByteBuffer buffer) throws Exception {
            this.offset = (int)fileChannel.position();
            this.compressedSize = buffer.getInt();
            this.uncompressedSize = buffer.getInt();
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
