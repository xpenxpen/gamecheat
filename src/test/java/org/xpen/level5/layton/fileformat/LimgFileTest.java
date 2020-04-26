package org.xpen.level5.layton.fileformat;

import java.io.File;

import org.junit.Test;
import org.xpen.util.UserSetting;


public class LimgFileTest {
    @Test
    public void testBg() throws Exception {
        UserSetting.rootInputFolder = "D:/soft/game/nds/8100437/root/lt3";
        UserSetting.rootOutputFolder = "D:/soft/game/nds/8100437/root/lt3/myex";
        
        CimgFile cimgFile = new CimgFile();
        cimgFile.addFolderType("nazo", new LimgHandler());
        File f = new File(UserSetting.rootInputFolder, "nazo/jp/bg_h12.cimg");
        cimgFile.decode("nazo", f);
    }

}
