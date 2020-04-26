package org.xpen.avalanche.studios.fileformat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.avalanche.studios.fileformat.TabFile.FatEntry;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.ubisoft.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.DeflateCompressor;

public class ArcFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(ArcFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    public void setFatEntries(List<FatEntry> fatEntries) {
		this.fatEntries = fatEntries;
	}

	protected String fileName;
    
    public ArcFile() {
    }
    
    public ArcFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".arc"), "r");
        fileChannel = raf.getChannel();
    }

	public void decode() throws Exception {
        decodeDat();
    }

	protected void decodeDat() throws Exception {
		int errorCount = 0;
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);  
            
            if (fatEntry.size == 0) {
            	continue;
            }
            
            byte[] bytes;
            boolean hasException = false;
            
            bytes = noCompress(fatEntry);
            byte[] uncompressedBytes = new byte[fatEntry.size * 2];
            if (bytes[0] == 0x78 && bytes[1] == 0x01) {
                //try deflate with double size
                int actualSize = DeflateCompressor.decompress(bytes, uncompressedBytes);
                bytes = new byte[actualSize];
                System.arraycopy(uncompressedBytes, 0, bytes, 0, actualSize);
            }
            
        	detectAndHandle(fatEntry, bytes);
            
        }
        
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
        String detectedType = FileTypeDetector.detect(entry, b);
        FileTypeHandler fileTypeHandler = FileTypeDetector.getFileTypeHandler(detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", true);
        }
        
        boolean isUnknown = true;
        
        fileTypeHandler.handle(b, this.fileName, String.valueOf(entry.crc), isUnknown);
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

}
