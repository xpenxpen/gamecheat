package org.xpen.softstar.pal.fileformat;

import java.awt.Color;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.ubisoft.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.UserSetting;

public class MkfFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(MkfFile.class);
    
    public static final String PAT_FILE_NAME = "PAT";
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();
    private String fileName;
    private boolean isPallete;
    public Color[][] palletes;

    private int errorCount;
    
    
    public MkfFile(String fileName) throws Exception {
    	this.fileName = fileName;
    	if (fileName.equals(PAT_FILE_NAME)) {
    	    isPallete = true;
    	}
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".MKF"), "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * MKF File format
     * 4 entryCount
     * ----LOOP
     * | FAT
     * ----
     *
     */
    public void decode() throws Exception {
        
        decodeFat();
        if (isPallete) {
            decodePallete();
            Yj1Handler.palettes = palletes;
        } else {
            decodeDat();
            System.out.println("errorCount="+errorCount);
        }
        
    }


    private void decodeFat() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        
        int toReadBufferCount = buffer.getInt();
        int entryCount = toReadBufferCount / 4 - 1;
        
        fileChannel.position(0);
        buffer = ByteBuffer.allocate(toReadBufferCount);
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
            
            String threeDigit = StringUtils.leftPad(String.valueOf(i + 1), 3, '0');
            fatEntry.fname = threeDigit;
            detectAndHandle(fatEntry, bytes);
        }
        
    }

    private void decodePallete() throws Exception {
        palletes = new Color[fatEntries.size() - 1][];
        for (int i = 0; i < fatEntries.size() - 1; i++) {
            FatEntry fatEntry = fatEntries.get(i);
            
            raf.seek(fatEntry.start);
            
            byte[] bytes = new byte[fatEntry.length];
            raf.readFully(bytes);
            
            palletes[i] = new Color[256];
            for (int j = 0; j < palletes[i].length; j++) {
                int r =  (bytes[j * 3] * 4) & 0xFF;
                int g =  (bytes[j * 3 + 1] * 4) & 0xFF;
                int b =  (bytes[j * 3 + 2] * 4) & 0xFF;
                int a =  0xFF;
                palletes[i][j] = new Color(r,g,b,a);
            }
        }
        
    }

    private void detectAndHandle(FatEntry entry, byte[] b) throws Exception {
        String detectedType = MkfFileTypeDetector.detect(entry, b);
        FileTypeHandler fileTypeHandler = MkfFileTypeDetector.getFileTypeHandler(detectedType);
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
    }

    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class FatEntry {
        public String fname;
        public int start;
        public int length;
        
        public void decode(ByteBuffer buffer) {
            this.start = buffer.getInt();
            //LOG.debug(toString());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
