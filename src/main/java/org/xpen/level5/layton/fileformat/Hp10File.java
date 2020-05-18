package org.xpen.level5.layton.fileformat;

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

public class Hp10File {
    
    private static final Logger LOG = LoggerFactory.getLogger(Hp10File.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    protected String fileName;
    public boolean isNoCompress = false;
    public int fileNameStart;
    public int fileDataStart;
    
    public Hp10File() {
    }
    
    public Hp10File(String fileName) throws Exception {
        this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }
    
    public void decode() throws Exception {
        decodeFat();
        decodeFileName();
        decodeDat();
    }

    /**
     * HP10 File format
     * 4 magic 'HP10'
     * 4 total file count AC7
     * 4 total file size
     * 4 ?
     * 4 file name start
     * 4 file data start
     * ----LOOP start from 0x30---
     * FatEntry
     */
    private void decodeFat() throws Exception {
        raf.seek(0);
        ByteBuffer buffer = ByteBuffer.allocate(24);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(24);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        int fileCount = buffer.getInt();
        buffer.position(16);
        fileNameStart = buffer.getInt();
        fileDataStart = buffer.getInt();
        
        raf.seek(0x30);
        buffer = ByteBuffer.allocate(fileCount * 32);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(fileCount * 32);
        fileChannel.read(buffer);
        buffer.flip();
        
        for (int i = 0; i < fileCount; i++) {
            FatEntry fatEntry = new FatEntry();
            fatEntries.add(fatEntry);
            fatEntry.decode(buffer);
        }
    }

    private void decodeFileName() throws Exception {
        raf.seek(fileNameStart);
        ByteBuffer buffer = ByteBuffer.allocate(fileDataStart - fileNameStart);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(fileDataStart - fileNameStart);
        fileChannel.read(buffer);
        buffer.flip();
        
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            fatEntry.fname = ByteBufferUtil.getNullTerminatedString(buffer);
            LOG.debug(fatEntry.toString());
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

    /**
    * |-4 file data offset
    * |-4 file data size
    * |-4 file name offset
    * |-20 unknown
    */
    public class FatEntry {
        public int fileNameOffset;
        public String fname;
        public int offset;
        public int size;
                
        public void decode(ByteBuffer buffer) throws Exception {
            offset = fileDataStart + buffer.getInt();
            size = buffer.getInt();
            fileNameOffset = buffer.getInt();
            buffer.position(buffer.position() + 20);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
