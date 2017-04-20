package org.xpen.dunia2.fileformat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeDetector;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.dunia2.fileformat.dat.LzoCompressor;
import org.xpen.dunia2.fileformat.dat.SimpleCopyHandler;
import org.xpen.dunia2.fileformat.fat2.CompressionScheme;
import org.xpen.dunia2.fileformat.fat2.Entry;
import org.xpen.dunia2.fileformat.fat2.FileListManager;
import org.xpen.dunia2.fileformat.fat2.SubFatEntry;
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
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".dat"), "r");
        fileChannel = raf.getChannel();
    }

	public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

	public void decode() throws Exception {
        List<Entry> entries = fat2File.getEntries();
        decode(entries);
        
        //sub
        List<SubFatEntry> subFats = fat2File.getSubFats();
        for (SubFatEntry subFatEntry : subFats) {
            decode(subFatEntry.entries);
        }
    }

    /**
     * @param entries
     * @throws IOException
     * @throws Exception
     */
    private void decode(List<Entry> entries) throws IOException, Exception {
        for (Entry entry : entries) {
            LOG.debug("processing " + entry.toString());
        	raf.seek(entry.offset);
        	
        	if (entry.compressionScheme == CompressionScheme.NONE) {
        		byte[] b = new byte[entry.compressedSize];
            	raf.readFully(b);
            	
            	detectAndHandle(entry, b);
            	
        	} else if (entry.compressionScheme == CompressionScheme.LZO1X) {
                byte[] b = new byte[entry.compressedSize];
                byte[] ub = new byte[entry.uncompressedSize];
                raf.readFully(b);
                
                decompressLzo(entry, b, ub);
                
                detectAndHandle(entry, ub);
                
                
        	} else {
                throw new UnsupportedOperationException();
        	}
        }
    }

	private void decompressLzo(Entry entry, byte[] b, byte[] ub) {
		LzoCompressor.decompress(b, 0, entry.compressedSize, ub, 0, entry.uncompressedSize);
	}

    private void detectAndHandle(Entry entry, byte[] b) throws Exception {
        String detectedType = FileTypeDetector.detect(b);
        FileTypeHandler fileTypeHandler = FileTypeDetector.getFileTypeHandler(detectedType);
        if (fileTypeHandler == null) {
            fileTypeHandler = new SimpleCopyHandler("unknown", true);
        }
        
        Map<Long, String> crcMap = flm.getCrcMap();
        String newFileName = null;
        boolean isUnknown = false;
        if (crcMap.containsKey(entry.nameHash.longValue())) {
        	String fileName = crcMap.get(entry.nameHash.longValue());
        	newFileName = fileName;
        	//flm.addMatch(fileName);
        } else {
            newFileName = StringUtils.leftPad(entry.nameHash.toString(16), 16, '0');
            isUnknown = true;
        }
        
        fileTypeHandler.handle(b, this.fileName, newFileName, isUnknown);
    }

}
