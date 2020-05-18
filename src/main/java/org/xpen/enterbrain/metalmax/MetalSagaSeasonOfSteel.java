package org.xpen.enterbrain.metalmax;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.xpen.ds.NclrNcbrNcer;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Metal Saga: Season of Steel
 * 重装机兵 钢之季节
 * メタルサーガ 〜鋼の季節〜
 * 569/1584
 *
 */
public class MetalSagaSeasonOfSteel {
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100415/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100415/root/myex";
        String[] folderNames = {"bsctrler", "combat", "ending", "jukebox", "map", "mapdat_radar",
                "minigame", "oden", "poster", "seki_atmap", "system"};
        //String[] folderNames = {"t1"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HandleCount countPair = new HandleCount();
        
        NclrNcgrNscr.extractNclrNcgrNscr(folderNames, countPair, null);
        NclrNcbrNcer.extractNclrNcbrNcer(folderNames, countPair, null);
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+countPair.totalCount + ",handleCount= "+countPair.handleCount);
    }

}
