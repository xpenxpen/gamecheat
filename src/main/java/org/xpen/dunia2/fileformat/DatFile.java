package org.xpen.dunia2.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.fat2.CompressionScheme;
import org.xpen.dunia2.fileformat.fat2.Entry;
import org.xpen.dunia2.fileformat.fat2.FileListManager;

public class DatFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(DatFile.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private String fileName;
    private Fat2File fat2File;
    private FileListManager flm;
	private String rootOutputFolder;
    
    public DatFile(String fileName, Fat2File fat2File, FileListManager flm, String rootOutputFolder) throws Exception {
    	this.fileName = fileName;
    	this.fat2File = fat2File;
    	this.flm = flm;
		this.rootOutputFolder = rootOutputFolder;
        
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
            	
            	Map<Long, String> crcMap = flm.getCrcMap();
            	File outFile = null;
            	if (crcMap.containsKey(entry.nameHash.longValue())) {
            		String fileName = crcMap.get(entry.nameHash.longValue());
            		outFile = new File(rootOutputFolder, fileName);
            	} else {
            		outFile = new File(rootOutputFolder, "unknown/" + entry.nameHash.toString(16));
            	}
            	
            	File parentFile = outFile.getParentFile();
            	parentFile.mkdirs();
            	
            	OutputStream os = new FileOutputStream(outFile);
            	IOUtils.write(b, os);
        	} else {
        		throw new UnsupportedOperationException();
        	}
        }
		
	}

}
