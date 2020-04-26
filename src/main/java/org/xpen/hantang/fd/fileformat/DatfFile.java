package org.xpen.hantang.fd.fileformat;

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
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.ubisoft.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.UserSetting;

public class DatfFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(DatfFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private String fileName;
    private int errorCount;
    
    
    public DatfFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".dat"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * DATF File format
     * 4 entryCount
     * ----LOOP
     * | FAT
     * ----
     *
     */
    public void decode() throws Exception {
        
        decodeFat();
        decodeDat();
        
    }


    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        fileChannel.read(buffer);
        buffer.flip();
        
        
        

        while (true) {
            buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = new FatEntry();
            fatEntry.datFileName = this.fileName;
            fatEntry.decode(buffer);
            fatEntries.add(fatEntry);
            
            if (fatEntry.start >= fileChannel.size()) {
                break;
            }
        }
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            FatEntry nextFatEntry = fatEntries.get(i+1);
            fatEntry.length = nextFatEntry.start - fatEntry.start;
        }
        
    }

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.start);
            
            byte[] bytes = new byte[fatEntry.length];
            raf.readFully(bytes);

            String threeDigit = StringUtils.leftPad(String.valueOf(i), 3, '0');
            fatEntry.fname = threeDigit;
            detectAndHandle(fatEntry, bytes);
        }
        
    }

    private void detectAndHandle(FatEntry entry, byte[] b) throws Exception {
        String detectedType = DatfFileTypeDetector.detect(entry, b);
        FileTypeHandler fileTypeHandler = DatfFileTypeDetector.getFileTypeHandler(detectedType);
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
        public String datFileName;
        public String fname;
        public int start;
        public int length;
        
        public void decode(ByteBuffer buffer) {
            this.start = buffer.getInt();
            //System.out.println("start="+start);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
