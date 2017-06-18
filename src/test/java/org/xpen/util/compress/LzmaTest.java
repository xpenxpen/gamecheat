package org.xpen.util.compress;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;

public class LzmaTest {
	
    @Ignore
    public void testLzma() throws IOException {
        File file = new File("D:\\psp\\akb149\\NoLabel\\PSP_GAME\\USRDIR\\dev\\ms0\\myex\\ALLD2\\2dc\\001_00-01-00.2dc");
        byte[] inBytes = FileUtils.readFileToByteArray(file);
        byte[] outBytes = LzmaCompressor.decompress(inBytes);
        FileUtils.writeByteArrayToFile(new File("111.2dc"), outBytes);
    }

}
