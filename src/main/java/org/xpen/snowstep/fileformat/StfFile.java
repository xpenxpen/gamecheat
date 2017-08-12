package org.xpen.snowstep.fileformat;

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

public class StfFile {
    public static final int MAGIC_STF = 0x465453; //STF

    private static final Logger LOG = LoggerFactory.getLogger(StfFile.class);

    protected RandomAccessFile raf;
    protected FileChannel fileChannel;

    protected List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    protected String fileName;
    public String currentFolderName;
    public int folderFilesLeft;

    public StfFile() {
    }

    public StfFile(String fileName) throws Exception {
        this.fileName = fileName;

        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".stf"), "r");
        fileChannel = raf.getChannel();
    }

    public void decode() throws Exception {
        decodeFat();
        decodeDat();
    }

    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(4);
        fileChannel.read(buffer);
        buffer.flip();
        
        int magic = buffer.getInt();
        if (magic != MAGIC_STF) {
            throw new RuntimeException("bad magic");
        }

        
        fileChannel.position(0x29);
        int fileCount = 1815;

        for (int i = 0; i < fileCount; i++) {

            FatEntry fatEntry = new FatEntry();
            fatEntry.decode();
            fatEntries.add(fatEntry);
        }

        buffer.clear();
    }

    protected void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);
            if (fatEntry.size == 0) {
                continue;
            }

            //first 9 bytes in each file useless
            raf.seek(fatEntry.offset + 9);

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
        public String datFileName;
        public String fname;
        public int crc;
        public int offset;
        public int size;
        public int folderUnknownFlag;

        public void decode() throws Exception {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(2);
            fileChannel.read(buffer);
            buffer.flip();
            
            int fnameLength = buffer.getShort();
            
            buffer = ByteBuffer.allocate(fnameLength);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(fnameLength);
            fileChannel.read(buffer);
            buffer.flip();
            
            fname = ByteBufferUtil.getFixedLengthString(buffer, fnameLength);
            
            int flag = raf.read();
            if (flag == 1) {
                currentFolderName = fname;
                        
                buffer = ByteBuffer.allocate(5);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.limit(5);
                fileChannel.read(buffer);
                buffer.flip();
                
                folderUnknownFlag = buffer.getInt();
                folderFilesLeft = folderUnknownFlag;
                
            } else if (flag == 0) {
                buffer = ByteBuffer.allocate(9);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.limit(9);
                fileChannel.read(buffer);
                buffer.flip();
                
                size = buffer.getInt();
                offset = buffer.getInt();
                
                if (folderFilesLeft > 0) {
                    fname = currentFolderName + "/" + fname;
                    folderFilesLeft--;
                }
               
            } else {
                throw new RuntimeException("bad file flag");
            }
            
            LOG.debug(toString());
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
