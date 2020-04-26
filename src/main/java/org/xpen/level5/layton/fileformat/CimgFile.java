package org.xpen.level5.layton.fileformat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.xpen.util.compress.NintendoLz10Compressor;

public class CimgFile extends ArcFile {
    
    @Override
    public void decode(String folderName, File f) throws Exception {
        byte[] inBytes = FileUtils.readFileToByteArray(f);
        byte[] outBytes = null;
        
        outBytes = NintendoLz10Compressor.decompress(inBytes);
        
        handle(folderName, f, outBytes);
    }

}
