package org.xpen.softstar.pal.fileformat;

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
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;
import org.xpen.util.handler.SimpleCopyHandler;

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

    /**
     * TSW File format
     * 0x18 all zero
     * ----4 entry count (some strange file has wrong count)
     * |
     * | 44 LOOP entry
     * |    |----
     * |    | 20 file name (Big5) 
     * |    | 4  size
     * |    | 4  offset
     * |    | 4  index
     * |    | 12 unknown (all zero)
     * |    |----
     * ----
     * 
     *
     */
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
        //fix for strange file
        if (fatCount == 3437 && (fileName.equals("All_Char"))) {
            //pal1 new
            fatCount = 1713;
        } else if (fatCount == 66091 && (fileName.equals("all_magic"))) {
            //pal2
            fatCount = 247;
        }
        
        for (int i = 0; i < fatCount; i++) {
            FatEntry fatEntry = new FatEntry();
                
            buffer = ByteBuffer.allocate(44);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(44);
            fileChannel.read(buffer);
            buffer.flip();
            
            fatEntry.decode(buffer);
            if (fatEntry.index != 0) {
                fatEntries.add(fatEntry);
            }
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
        
        boolean isDone = false;
        
        while (!isDone) {
            try {
                fileTypeHandler.handle(b, this.fileName, String.valueOf(entry.fname), isUnknown);
                isDone = true;
                
            } catch (BufferNotEnoughException e) {
                LOG.warn("fileTypeHandler.handle", e);
                
                //try fixed size
                entry.size = e.bufferSize;
                b = noCompress(entry);
                
            } catch (Exception e) {
                LOG.error("fileTypeHandler.handle", e);
                errorCount++;
                fileTypeHandler = new SimpleCopyHandler("unknown", true);
                fileTypeHandler.handle(b, this.fileName, String.valueOf(entry.fname), isUnknown);
            }
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
            } else if (fname.startsWith("_2325_猴神君_新影")) {
                fname = "_2325_猴神君_新影";
            } else if (fname.startsWith("7002_土象法術二_石頭B")) {
                fname = "7002_土象法術二_石頭B";
            } else if (fname.startsWith("_15007_月涼山&紫竹林")) {
                fname = "_15007_月涼山_紫竹林";
            }

            
            //fix strange file size
            if (fname.equals("_1000李逍遙升級_影子") && size == 3072) {
                size = 3271;
            } else if (fname.equals("_1000李逍遙影(劍)all") && size == 8192) {
                size = 8257;
            } else if (fname.equals("_1001靈兒影(雙刃)all") && size == 7680) {
                size = 7905;
            } else if (fname.equals("_1002林月如影(劍)all") && size == 7424) {
                size = 7547;
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
