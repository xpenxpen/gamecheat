package org.xpen.blossomtale.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class BlossomTalesFile {
    public static final byte[] MAGIC_XNBW = {0x58, 0x4E, 0x42, 0x77}; //XNBw
    
    private static final Logger LOG = LoggerFactory.getLogger(BlossomTalesFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    
    public BlossomTalesFile() {
    }
    
    public BlossomTalesFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName), "r");
        fileChannel = raf.getChannel();
    }

	public void decode() throws Exception {
        decodeFat();
        System.out.println("Total " + fatEntries.size() + " files");
        decodeDat();
    }
	
    public void decodeFat() throws Exception {
        long fileSize = fileChannel.size();
        int outIndex = 0 ;
        byte[] magic = new byte[4];
        long currentPos = 0x13088E3A;
        
        while (currentPos < 0x2482E5C0) {
            fileChannel.position(currentPos);
            
            //4->size
            ByteBuffer buffer = ByteBuffer.allocate(0x4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(0x4);
            fileChannel.read(buffer);
            buffer.flip();
            int headerSize = buffer.getInt();
            
            buffer = ByteBuffer.allocate(headerSize);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(headerSize);
            fileChannel.position(currentPos + 4);
            fileChannel.read(buffer);
            buffer.flip();
            
            //12->Unknown
            int unknown1 = buffer.getInt();
            int unknown2 = buffer.getInt();
            int unknown3 = buffer.getInt();
            System.out.println("unknown1="+unknown1+", unknown2="+unknown2+", unknown3="+unknown3);
            
            String fileName = ByteBufferUtil.getNullTerminatedString2Bytes(buffer);
            System.out.println(fileName);
            
            buffer.position(buffer.position() + 3);
            int size = buffer.getInt();
            
            
            if (unknown3 == 0) {
                FatEntry fatEntry = new FatEntry();
                fatEntry.fname = fileName;
                fatEntry.start = (int)fileChannel.position();
                fatEntry.size = size;
                fatEntries.add(fatEntry);
                //outIndex++;
                System.out.println(fatEntry);
                currentPos = fatEntry.start + fatEntry.size;
            } else {
                //do not handle folder
                currentPos = currentPos + 4 + headerSize;
            }
            
            System.out.println("currentPos="+currentPos);
        }
    }
    

    protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            try {
                FatEntry fatEntry = fatEntries.get(i);
                if (fatEntry.size == 0) {
                    continue;
                }
    
                raf.seek(fatEntry.start);
                byte[] bytes = new byte[fatEntry.size];
                raf.readFully(bytes);
    
                File outFile = null;
                outFile = new File(UserSetting.rootOutputFolder + "/" + fileName, fatEntry.fname);
                File parentFile = outFile.getParentFile();
                parentFile.mkdirs();
    
                OutputStream os = new FileOutputStream(outFile);
    
                IOUtils.write(bytes, os);
                os.close();
            } catch (Exception  e) {
                System.err.println("Error for file " + i);
            }
        }
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int start;
        public int size;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
