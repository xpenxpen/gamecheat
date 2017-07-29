package org.xpen.alawar.format;

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

public class VstFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(VstFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    protected String fileName;
    public boolean isNoCompress = false;
    
    public VstFile() {
    }
    
    public VstFile(String fileName) throws Exception {
        this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * VST File format
     * 0x0C246C48 FAT start
     */
    public void decode() throws Exception {
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        raf.seek(raf.length() - 4);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fatStartOffset = buffer.getInt();
        raf.seek(fatStartOffset);
        
        buffer.rewind();
        fileChannel.read(buffer);
        buffer.flip();
        
        int fileCount = buffer.getInt();
        
        for (int i = 0; i < fileCount; i++) {
            buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(4);
            fileChannel.read(buffer);
            buffer.flip();
            
            int fnameSize = buffer.getInt();
            
            buffer = ByteBuffer.allocate(fnameSize + 8);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(fnameSize + 8);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = new FatEntry();
            fatEntries.add(fatEntry);
            fatEntry.fnameSize = fnameSize;
            fatEntry.decode(buffer);
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

    public class FatEntry {
        public String fname;
        public int fnameSize;
        public int offset;
        public int size;
                
        public void decode(ByteBuffer buffer) throws Exception {
            fname = ByteBufferUtil.getFixedLengthString(buffer, fnameSize);
            offset = buffer.getInt();
            size = buffer.getInt();
            LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
