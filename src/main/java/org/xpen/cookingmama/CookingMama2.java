package org.xpen.cookingmama;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.xpen.ds.NclrNcbrNcer;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Cooking Mama 2
 * 料理妈妈2
 * クッキングママ2
 * 1502/1634
 *
 */
public class CookingMama2 {
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8001664/root/data";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8001664/root/myex";
        String[] folderNames = {"ao", "color", "com", "font", "is", "Ishida", "tonaki", "US", "yama"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HandleCount countPair = new HandleCount();
        
        NclrNcgrNscr.extractNclrNcgrNscr(folderNames, countPair,
        (t)-> {
            if (t.startsWith("cook_U_BG")) {
                return "cook_U_BG00";
            }
            return t;
        });
        
        NclrNcbrNcer.extractNclrNcbrNcer(folderNames, countPair, null);
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+countPair.totalCount + ",handleCount= "+countPair.handleCount);
    }

}
