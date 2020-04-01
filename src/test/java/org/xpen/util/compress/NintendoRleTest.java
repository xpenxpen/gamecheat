package org.xpen.util.compress;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class NintendoRleTest {
    
    @Test
    public void testRle() throws IOException {
        File file = new File("D:/soft/game/nds/Nintendo_DS_Compressors_v1.4-CUE/akira_e_face.arc");
        byte[] inBytes = FileUtils.readFileToByteArray(file);
        inBytes = Arrays.copyOfRange(inBytes, 4, inBytes.length);
        
        byte[] outBytes = NintendoRleCompressor.decompress(inBytes);
        FileUtils.writeByteArrayToFile(new File("D:/soft/game/nds/Nintendo_DS_Compressors_v1.4-CUE/akira_e_face_my.arc"), outBytes);
    }

}
