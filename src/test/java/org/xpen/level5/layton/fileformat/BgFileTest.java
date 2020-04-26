package org.xpen.level5.layton.fileformat;

import java.io.File;

import org.junit.Test;
import org.xpen.util.UserSetting;


public class BgFileTest {
    @Test
    public void testBg() throws Exception {
        UserSetting.rootInputFolder = "D:/soft/game/nds/8100435/root/data";
        UserSetting.rootOutputFolder = "D:/soft/game/nds/8100435/root/myex";
        
        ArcFile arcFile = new ArcFile();
        arcFile.addFolderType("ani", new AniHandler());
        arcFile.addFolderType("bg", new BgHandler());
        File f = new File(UserSetting.rootInputFolder, "bg/challenge_2.arc");
        arcFile.decode("bg", f);
    }

}
