package org.xpen.avalanche.studios.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.avalanche.studios.fileformat.TabFile.FatEntry;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.util.UserSetting;

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
            
//            if (fatEntry.size == 0) {
//            	continue;
//            }
            
            byte[] bytes;
            boolean hasException = false;
            
            bytes = noCompress(fatEntry);
            
        	detectAndHandle(fatEntry, bytes);
            

//            File outFile = null;
//            //String extension = FilenameUtils.getExtension(fatEntry.fname);
//            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName, String.valueOf(fatEntry.crc));
//            if (hasException) {
//                outFile = new File(UserSetting.rootOutputFolder + "/" + fileName + "/exception",  String.valueOf(fatEntry.crc));
//            }
//            File parentFile = outFile.getParentFile();
//            parentFile.mkdirs();
//            
//            OutputStream os = new FileOutputStream(outFile);
//            
//            IOUtils.write(bytes, os);
//            os.close();
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
