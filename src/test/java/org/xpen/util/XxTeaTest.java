package org.xpen.util;

import java.io.File;
import java.io.IOException;

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoCompressor;
import org.anarres.lzo.LzoConstraint;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.lzo_uintp;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;

public class XxTeaTest {
	
    @Ignore
    public void testLzo() throws IOException {
        File file = new File("D:\\git\\opensource\\dunia2\\paladindat\\matfx\\Shader\\ltMap.fx");
        byte[] inBytes = FileUtils.readFileToByteArray(file);
        byte[] outBytes = new byte[inBytes.length];
        LzoCompressor compressor;
        LzoAlgorithm algorithm = LzoAlgorithm.LZO1X;
        compressor = LzoLibrary.getInstance().newCompressor(algorithm, LzoConstraint.COMPRESSION);
        lzo_uintp outputBufferLen = new lzo_uintp();
        outputBufferLen.value = inBytes.length;
        compressor.compress(inBytes, 0, inBytes.length, outBytes, 0, outputBufferLen);
        
        FileUtils.writeByteArrayToFile(new File("D:\\git\\opensource\\dunia2\\paladindat\\matfx\\Shader\\ltMap.fx2222"), outBytes);

    }

}
