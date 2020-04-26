package org.xpen.util.compress;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class Nintendolz10Test {
    
    @Test
    public void testLz10() throws IOException {
        File file = new File("D:/soft/ga/nds/Nintendo_DS_Compressors_v1.4-CUE/596_notcompress2");
        byte[] inBytes = FileUtils.readFileToByteArray(file);
        //inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
        
        byte[] outBytes = NintendoLz10Compressor.decompress(inBytes);
        FileUtils.writeByteArrayToFile(new File("D:/soft/ga/nds/Nintendo_DS_Compressors_v1.4-CUE/akira_e_face_my.arc"), outBytes);
    }

}
