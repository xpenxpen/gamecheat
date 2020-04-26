package org.xpen.level5.layton.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;

public class FaFile {
    private static final Logger LOG = LoggerFactory.getLogger(FaFile.class);
    public static final int MAGIC_GFSA = 0x41534647; //GFSA
    
    public int block1Offset;
    public int block2Offset;
    public int block3Offset;
    public int block4Offset;
    public int dataPtr;
    public int folderCount;
    public int count2;
    public int unknown1;
    public int unknown2;
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private String fileName;
    
    public FaFile(String fileName) throws Exception {
        this.fileName = fileName;
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName + ".fa"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * FA File format
     * 4 magic 'GFSA'
     * 4 block1Offset 28 (length=1D4)
     * 4 block2Offset 1FC (length=9788)
     * 4 block3Offset 8984 (length=11C)
     * 4 block4Offset 8AA0 (length=3D9C)
     * 4 dataPtr C83C
     * 4 Folder count 48
     * 4 count2 116B
     * 4 ?  8D98
     * 4 ? 103B8
     */
    public void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(40);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.limit(40);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_GFSA) {
            throw new RuntimeException("bad magic");
        }
        
        block1Offset = buffer.getInt();
        block2Offset = buffer.getInt();
        block3Offset = buffer.getInt();
        block4Offset = buffer.getInt();
        dataPtr = buffer.getInt();
        folderCount = buffer.getInt();
        count2 = buffer.getInt();
        unknown1 = buffer.getInt();
        unknown2 = buffer.getInt();
        
        System.out.println(this);
        
    }
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

}
