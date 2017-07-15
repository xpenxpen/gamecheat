package org.xpen.cryengine.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.ZipCompressor;

public class PakFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(PakFile.class);
    
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    
    protected String fileName;
    
    public PakFile() {
    }
    
    public PakFile(String fileName) throws Exception {
    	this.fileName = fileName;
        
    }
    
    /**
     * PAK
     * it is zip format
     *
     */
    public void decode() throws Exception {
    	ZipCompressor.decompress(new File(UserSetting.rootInputFolder, fileName),
    			new File(UserSetting.rootOutputFolder));
    }




    public void close() throws Exception {
    }

}
