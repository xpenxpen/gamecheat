package org.xpen.namco.fileformat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.namco.fileformat.IdxFile.FatEntry;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.DeflateCompressor;
import org.xpen.util.compress.LzmaCompressor;

public class ApkFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(ApkFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    public void setFatEntries(List<FatEntry> fatEntries) {
		this.fatEntries = fatEntries;
	}

	protected String fileName;
    
    public ApkFile() {
    }
    
    public ApkFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
        raf = new RandomAccessFile(new File(UserSetting.rootInputFolder, fileName+".apk"), "r");
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
            
            if (fatEntry.compressFlag == 512) {
            	//deflate
                raf.seek(fatEntry.offset);
                LOG.debug("offset={}", fatEntry.offset);
                bytes = new byte[fatEntry.size];
                byte[] compressedBytes = new byte[fatEntry.zsize];
                raf.readFully(compressedBytes);
                try {
                    DeflateCompressor.decompress(compressedBytes, bytes);
                } catch (Exception e) {
                	errorCount++;
                	continue;
                    //throw new RuntimeException(e);
                }
            } else if (fatEntry.compressFlag == 768) {
            	//lzma
                raf.seek(fatEntry.offset);
                LOG.debug("offset={},size={},zsize={}", fatEntry.offset, fatEntry.size, fatEntry.zsize);
                byte[] inBytes = new byte[fatEntry.zsize];
                raf.readFully(inBytes);
                bytes = new byte[fatEntry.size];
                try {
                	LzmaCompressor.decompress(inBytes, bytes);
                } catch (Exception e) {
                	errorCount++;
                	continue;
                    //throw new RuntimeException(e);
                }
            } else if (fatEntry.compressFlag == 0) {
            	//no compress
                raf.seek(fatEntry.offset);
                bytes = new byte[fatEntry.size];
                raf.readFully(bytes);
            } else {
            	throw new RuntimeException("fatEntry.compressFlag=" + fatEntry.compressFlag);
            }
            
            

            File outFile = null;
            String extension = FilenameUtils.getExtension(fatEntry.fname);
            outFile = new File(UserSetting.rootOutputFolder + "/" + fileName + "/" + extension, fatEntry.fname);
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            OutputStream os = new FileOutputStream(outFile);
            
            IOUtils.write(bytes, os);
            os.close();
        }
        
        LOG.info("errorCount={}", errorCount);
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

}
