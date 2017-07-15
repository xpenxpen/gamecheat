package org.xpen.pal;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.pal.fileformat.CpkFile;
import org.xpen.util.UserSetting;

public class Pal4Cpk {
    
    private static final Logger LOG = LoggerFactory.getLogger(Pal4Cpk.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "F:/game/pal4/gamedata";
        UserSetting.rootOutputFolder = "F:/game/pal4/myex";
    	String[] fileNames = {"2d", "database", "Effect", "MatFX",
    			"PALActor", "palobject", "palweapon", "scenedata", "script",
    			"ui", "VideoA", "videob",
    	        "PALWorld/m01",
    			"PALWorld/m02",
    			"PALWorld/m03",
    			"PALWorld/m04",
    			"PALWorld/m05",
    			"PALWorld/m06",
    			"PALWorld/m07",
    			"PALWorld/m08",
    			"PALWorld/m09",
    			"PALWorld/m10",
    			"PALWorld/m11",
    			"PALWorld/m12",
    			"PALWorld/m13",
    			"PALWorld/m14",
    			"PALWorld/m15",
    			"PALWorld/m16",
    			"PALWorld/m17",
    			"PALWorld/m18",
    			"PALWorld/m19",
    			"PALWorld/m20",
    			"PALWorld/q01",
    			"PALWorld/q02",
    			"PALWorld/q03",
    			"PALWorld/q04",
    			"PALWorld/q05",
    			"PALWorld/q06",
    			"PALWorld/q07",
    			"PALWorld/q08",
    			"PALWorld/q09",
    			"PALWorld/q10",
    			"PALWorld/q11",
    			"PALWorld/q12",
    			"PALWorld/q13",
    			"PALWorld/q14",

    			"PALWorld/CombatWorld/m01",
    			"PALWorld/CombatWorld/m02a",
    			"PALWorld/CombatWorld/m02c",
    			"PALWorld/CombatWorld/m03a",
    			"PALWorld/CombatWorld/m03b",
    			"PALWorld/CombatWorld/m03c",
    			"PALWorld/CombatWorld/m04",
    			"PALWorld/CombatWorld/m05",
    			"PALWorld/CombatWorld/m06",
    			"PALWorld/CombatWorld/m07a",
    			"PALWorld/CombatWorld/m07b",
    			"PALWorld/CombatWorld/m07c",
    			"PALWorld/CombatWorld/m07d",
    			"PALWorld/CombatWorld/m08",
    			"PALWorld/CombatWorld/m09a",
    			"PALWorld/CombatWorld/m09b",
    			"PALWorld/CombatWorld/m09c",
    			"PALWorld/CombatWorld/m10a",
    			"PALWorld/CombatWorld/m10c",
    			"PALWorld/CombatWorld/m11",
    			"PALWorld/CombatWorld/m12",
    			"PALWorld/CombatWorld/m13a",
    			"PALWorld/CombatWorld/m13b",
    			"PALWorld/CombatWorld/m14",
    			"PALWorld/CombatWorld/m15a",
    			"PALWorld/CombatWorld/m15b",
    			"PALWorld/CombatWorld/m15c",
    			"PALWorld/CombatWorld/m16",
    			"PALWorld/CombatWorld/m17a",
    			"PALWorld/CombatWorld/m17b",
    			"PALWorld/CombatWorld/m17c",
    			"PALWorld/CombatWorld/m17d",
    			"PALWorld/CombatWorld/m18",
    			"PALWorld/CombatWorld/m19a",
    			"PALWorld/CombatWorld/m19b",
    			"PALWorld/CombatWorld/m19c",
    			"PALWorld/CombatWorld/m20a",
    			"PALWorld/CombatWorld/m20b",
    			"PALWorld/CombatWorld/q02"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
            CpkFile cpkFile = new CpkFile(fileName);
            cpkFile.decode();
            cpkFile.close();
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
