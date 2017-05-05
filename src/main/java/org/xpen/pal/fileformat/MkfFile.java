package org.xpen.pal.fileformat;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class MkfFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(MkfFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private String fileName;
    
    
    public MkfFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".MKF"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * MKF File format
     * 4 entryCount
     * 4 version
     * 4 headerLength
     * 4 flag1
     * 4 folderCount
     * 4 fileCount
     * 4 folderNamesLength
     * 4 fileNamesLength
     * 4 flag2
     * ----folderCount
     * |
     * |  LOOP ---folder_file_count
     * |       |  LOOP file
     * |       ---
     * |
     * ----
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
        
        int toReadBufferCount = buffer.getInt();
        int entryCount = toReadBufferCount / 4 - 2;
        
        buffer = ByteBuffer.allocate(toReadBufferCount - 4);
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

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.start);
            
            byte[] bytes = new byte[fatEntry.length];
            raf.readFully(bytes);

            File outFile = null;
            String threeDigit = StringUtils.leftPad(String.valueOf(i), 3, '0');
            outFile = new File(UserSetting.rootOutputFolder, fileName + "/" + threeDigit + ".yj1");
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(bytes, os);
            os.close();
        }
        
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
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
