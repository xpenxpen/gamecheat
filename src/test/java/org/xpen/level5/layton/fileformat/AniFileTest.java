package org.xpen.level5.layton.fileformat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.xpen.util.UserSetting;


public class AniFileTest {
    @Ignore
    public void testAni() throws Exception {
        UserSetting.rootInputFolder = "D:/soft/game/nds/8100435/root/data";
        UserSetting.rootOutputFolder = "D:/soft/game/nds/8100435/root/myex";
        byte[] inBytes = FileUtils.readFileToByteArray(new File("D:/soft/game/nds/Nintendo_DS_Compressors_v1.4-CUE/abc_de.arc"));
    	//AniHandler aniFile = new AniHandler("ani", "abc", inBytes);
        //aniFile.decode();
        //aniFile.close();
    }

}
