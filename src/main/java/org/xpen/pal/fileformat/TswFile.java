package org.xpen.pal.fileformat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class TswFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(TswFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public String format;
    int errorCount = 0;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    public void setFatEntries(List<FatEntry> fatEntries) {
        this.fatEntries = fatEntries;
    }

    protected String fileName;
    
    public TswFile() {
    }
    
    public TswFile(String fileName) throws Exception {
        this.format = FilenameUtils.getExtension(fileName);
        this.fileName = FilenameUtils.getBaseName(fileName);
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }

    public void decode() throws Exception {
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        fileChannel.position(0x18);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fatCount = buffer.getInt();
        //int minDatOffset = Integer.MAX_VALUE;
        //fix for some fat count is wrong
        if (fatCount == 3437 && (fileName.equals("All_Char"))) {
            fatCount = 1713;
        }
        
        for (int i = 0; i < fatCount; i++) {
            //fix for some fat count is wrong
//            LOG.debug("minDatOffset={}", minDatOffset);
//            if (fileChannel.position() >= minDatOffset) {
//                break;
//            }
            FatEntry fatEntry = new FatEntry();
            fatEntries.add(fatEntry);
                
            buffer = ByteBuffer.allocate(44);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(44);
            fileChannel.read(buffer);
            buffer.flip();
            
            fatEntry.decode(buffer);
            
//            if ((fatEntry.offset != 0) && (fatEntry.offset < minDatOffset)) {
//                minDatOffset = fatEntry.offset;
//            }
        }
    }

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            if (fatEntry.index == 0) {
                break;
            }
            
            byte[] bytes;
            
            try {
                bytes = noCompress(fatEntry);
            } catch (NegativeArraySizeException e) {
                errorCount++;
                continue;
            }
            
            detectAndHandle(fatEntry, bytes);
        }
        
        System.out.println("errorCount=" + errorCount);
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
        String detectedType;
        if (this.format.equals("snd")) {
            detectedType = "wav";
        } else if (this.format.equals("tsw")) {
            detectedType = "tsw";
        } else {
            detectedType = TswFileTypeDetector.detect(entry, b);
        }
        
        FileTypeHandler fileTypeHandler = TswFileTypeDetector.getFileTypeHandler(detectedType);
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
            fileTypeHandler.handle(b, this.fileName, String.valueOf(entry.fname), isUnknown);
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
        public int index;

        public void decode(ByteBuffer buffer) throws Exception {
            fname = ByteBufferUtil.getNullTerminatedString(buffer, "Big5");
            //fix for strange file name
            if (fname.startsWith("6045_16_萬蟻蝕象殘影")) {
                fname = "6045_16_萬蟻蝕象殘影";
            } else if (fname.startsWith("6060_14_蛇女靈兒甩尾")) {
                fname = "6060_14_蛇女靈兒甩尾";
            }
            buffer.position(20);
            size = buffer.getInt();
            offset = buffer.getInt();
            index = buffer.getInt();
            buffer.getInt();
            buffer.getInt();
            buffer.getInt();
            LOG.debug(toString());
            //System.out.println(offset+","+size+","+ index);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
