package org.xpen.dunia2.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeDetector;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.dunia2.fileformat.fat2.CompressionScheme;
import org.xpen.dunia2.fileformat.fat2.Entry;
import org.xpen.dunia2.fileformat.fat2.FileListManager;
import org.xpen.farcry3.UserSetting;

public class DatFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(DatFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private String fileName;
    private Fat2File fat2File;
    private FileListManager flm;
    
    public DatFile(String fileName, Fat2File fat2File, FileListManager flm) throws Exception {
    	this.fileName = fileName;
    	this.fat2File = fat2File;
    	this.flm = flm;
        
        raf = new RandomAccessFile(new File(fileName+".dat"), "r");
        fileChannel = raf.getChannel();
    }

	public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

	public void decode() throws Exception {
        List<Entry> entries = fat2File.getEntries();
        for (Entry entry : entries) {
            //LOG.debug(entry.toString());
        	raf.seek(entry.offset);
        	if (entry.compressionScheme == CompressionScheme.NONE) {
        		byte[] b = new byte[entry.compressedSize];
            	raf.readFully(b);
            	String detectedType = FileTypeDetector.detect(b);
            	FileTypeHandler fileTypeHandler = FileTypeDetector.getFileTypeHandler(detectedType);
            	if (fileTypeHandler == null) {
            	    fileTypeHandler = new SimpleCopyHandler("unknown");
            	}
            	
            	Map<Long, String> crcMap = flm.getCrcMap();
            	String newFileName = null;
            	boolean isUnknown = false;
            	if (crcMap.containsKey(entry.nameHash.longValue())) {
            		String fileName = crcMap.get(entry.nameHash.longValue());
            		newFileName = fileName;
            	} else {
            	    newFileName = entry.nameHash.toString(16);
            	    isUnknown = true;
            	}
            	
            	fileTypeHandler.handle(b, newFileName, isUnknown);
            	
        	} else {
        		throw new UnsupportedOperationException();
        	}
        }
		
	}

}
