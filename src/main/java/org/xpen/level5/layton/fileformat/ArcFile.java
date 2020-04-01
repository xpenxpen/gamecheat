package org.xpen.level5.layton.fileformat;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.compress.NintendoLz10Compressor;
import org.xpen.util.compress.NintendoRleCompressor;

/**
 * 4 Compression Type
 * Next all is compressed data
 * After decompress, decode animation
 *
 */
public class ArcFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(ArcFile.class);
    
    public static void decode(String folderName, File f) throws Exception {
        //System.out.println("Start "+ f.getName());
        byte[] inBytes = FileUtils.readFileToByteArray(f);
        byte[] outBytes = null;
        
        //Support type 01/02 now
        if (inBytes[0] == 2 && inBytes[1] == 0 && inBytes[2] == 0 && inBytes[3] == 0) {
            inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
            outBytes = NintendoLz10Compressor.decompress(inBytes);
        } else if (inBytes[0] == 1 && inBytes[1] == 0 && inBytes[2] == 0 && inBytes[3] == 0) {
            inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
            outBytes = NintendoRleCompressor.decompress(inBytes);
        } else {
            LOG.warn("Unknown compress type, skip {}", f.getName());
            throw new RuntimeException("Unknown compress type");
        }
        
        
        //FileUtils.writeByteArrayToFile(new File("D:/git/opensource/gamecheat/1.dat"), outBytes);
        //break;
        
        AniFile aniFile = new AniFile(folderName,
                f.getName().substring(0, f.getName().lastIndexOf(".")), outBytes);
        aniFile.decode();
        aniFile.close();
    }
}
