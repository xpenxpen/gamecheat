package org.xpen.softworld.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.ZipCompressor;

public class PrjFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(PrjFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected String fileName;
    
    public PrjFile() {
    }
    
    public PrjFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
    }
    
    /**
     * Prj
     * it is zip format
     *
     */
    public void decode() throws Exception {
    	ZipCompressor.decompress(new File(UserSetting.rootInputFolder, fileName),
    			new File(UserSetting.rootOutputFolder), "Big5");
    }




    public void close() throws Exception {
    }

}
