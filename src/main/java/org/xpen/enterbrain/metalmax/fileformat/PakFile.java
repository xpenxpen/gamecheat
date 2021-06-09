package org.xpen.enterbrain.metalmax.fileformat;

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

public class PakFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(PakFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private String folderName;
    private String fileName;
    private int errorCount;
    
    private int fileCount;
    private int fileTableOffset;
    private int filenameTreeOffset;
    
    
    public PakFile(String folderName, String fileName) throws Exception {
        this.folderName = folderName;
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, folderName + "/" + fileName+".pak"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * PAK File format
     * 0x00 - 2 bytes - File count
     * 0x02 - 4 bytes - File table offset
     * 0x06 - 2 bytes - Filename tree offset

     * 0x08 - Filename strings
     * 
     * [Filename tree] array of nodes with the following format:
     * 2 bytes - number of children
     * for each child:
     * 2 bytes - offset of string
     * 2 bytes - file table index if this is a leaf node (odd), otherwise offset in file table of first child (even)
     * 
     * [File table] array
     * 4 bytes - file size
     * 4 bytes - offset
     */
    public void decode() throws Exception {
        
        decodeFat();
        decodeDat();
        
    }


    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        fileChannel.read(buffer);
        buffer.flip();
        
        fileCount = buffer.getShort();
        fileTableOffset = buffer.getInt();
        filenameTreeOffset = buffer.getShort();
        
        if (fileCount != 0) {
            readFileNameTree();
        }
    }

    private void readFileNameTree() throws Exception {
        fileChannel.position(filenameTreeOffset);
        int currentFileNameInfoPos = filenameTreeOffset;
        
        
        ArrayList<String> queue = new ArrayList<>();
        queue.add("");
        
        while (!queue.isEmpty()) {
            String strPrefix = queue.remove(0);
            
            fileChannel.position(currentFileNameInfoPos);
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            fileChannel.read(buffer);
            buffer.flip();
            
            int filePartCount = buffer.getShort();
            List<String> toAddList = new ArrayList<>();
            currentFileNameInfoPos = (int)fileChannel.position();
            
            for (int i = 0; i < filePartCount; i++) {
                fileChannel.position(currentFileNameInfoPos);
                buffer = ByteBuffer.allocate(4);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                fileChannel.read(buffer);
                buffer.flip();
                
                int strOffset = buffer.getShort();
                int fileTableIndex = buffer.getShort();
                
                currentFileNameInfoPos = (int)fileChannel.position();
                fileChannel.position(strOffset);
                raf.seek(strOffset);
                
                String strPart = ByteBufferUtil.getNullTerminatedString(raf);
                String fullFileName = strPrefix + strPart;
    
                if (fileTableIndex % 2 == 1) {
                    int calFileTableIndex = fileTableIndex / 2 * 8 + fileTableOffset;
                    fileChannel.position(calFileTableIndex);
                    buffer = ByteBuffer.allocate(8);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    fileChannel.read(buffer);
                    buffer.flip();
                    
                    int fileSize = buffer.getInt();
                    int fileOffset = buffer.getInt();
                    
                    FatEntry fatEntry = new FatEntry();
                    fatEntry.fname = fullFileName;
                    fatEntry.start = fileOffset;
                    fatEntry.length = fileSize;
                    fatEntries.add(fatEntry);
                } else {
                    toAddList.add(fullFileName);
                }
            }
            if (!toAddList.isEmpty()) {
                queue.addAll(0, toAddList);
            }
        }
    }

    private void decodeDat() throws Exception {
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.start);
            
            byte[] bytes = new byte[fatEntry.length];
            raf.readFully(bytes);

            File outFile = new File(UserSetting.rootOutputFolder+"/"+folderName, fatEntry.fname);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(bytes, os);
            os.close();

            //fatEntry.fname = threeDigit;
            //detectAndHandle(fatEntry, bytes);
        }
        
    }

    /*private void detectAndHandle(FatEntry entry, byte[] b) throws Exception {
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
    }*/

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
