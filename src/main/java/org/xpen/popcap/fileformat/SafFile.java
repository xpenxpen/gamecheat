package org.xpen.popcap.fileformat;

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

public class SafFile {
    public static final int MAGIC_FFAS = 0x53414646; //'FFAS'
    public static final int FAT_START = 0xDD0FC9;
    public static final int FILE_COUNT = 1932;
    
    private static final Logger LOG = LoggerFactory.getLogger(SafFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    
    public SafFile() {
    }
    
    public SafFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".saf"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * SAF File format
     * go at offset FAT_START and start the loop:
     * - 4 bytes: offset
     * - 4 bytes: size
     * - 16 bytes: unknown
     * - 2 byte: length of the filename (n)
     * - n bytes: the filename
     *
     */
    public void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_FFAS) {
        	throw new RuntimeException("bad magic");
        }
        
        int fileCount = FILE_COUNT;
        raf.seek(FAT_START);
        
        for (int i = 0; i < fileCount; i++) {
            buffer = ByteBuffer.allocate(24);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(24);
            fileChannel.read(buffer);
            buffer.flip();
            
            FatEntry fatEntry = this.new FatEntry();
            fatEntry.offset = buffer.getInt();
            fatEntry.size = buffer.getInt();
            fatEntry.unknown1 = buffer.getInt();
            fatEntry.unknown2 = buffer.getInt();
            fatEntry.unknown3 = buffer.getInt();
            fatEntry.unknown4 = buffer.getInt();
            
            buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(2);
            fileChannel.read(buffer);
            buffer.flip();
            int fNameLength = buffer.getShort();
            
            buffer = ByteBuffer.allocate(fNameLength);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(fNameLength);
            fileChannel.read(buffer);
            buffer.flip();
            
            String fName = ByteBufferUtil.getNullTerminatedFixedLengthString(buffer, fNameLength);
        	//LOG.debug("pos={}, fName={}", fileChannel.position(), fName);
            
            fatEntry.fname = fName;
            fatEntries.add(fatEntry);
        }
        
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);           
        	LOG.debug("fatEntry={}", fatEntry);
        }

        decodeDat();
        
    }

    protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);           
            
            raf.seek(fatEntry.offset);
            byte[] bytes = new byte[fatEntry.size];
            raf.readFully(bytes);
            

            File outFile = null;
            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName, fatEntry.fname);
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
        public String fname;
        public int offset;
        public int size;
        public int unknown1;
        public int unknown2;
        public int unknown3;
        public int unknown4;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
