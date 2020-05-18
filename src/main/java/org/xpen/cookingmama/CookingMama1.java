package org.xpen.cookingmama;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.xpen.ds.NclrNcbrNcer;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Cooking Mama 1
 * 料理妈妈1
 * クッキングママ1
 * 586/664
 *
 */
public class CookingMama1 {
    
    public static void main(String[] args) throws Exception {
        //UserSetting.rootInputFolder = "D:/soft/ga/nds/8100216/root/data/is/food";
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100216/root/data";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100216/root/myex";
        String[] folderNames = {"com", "font", "is", "tonaki", "yama"};
        //String[] folderNames = {"obj"};
        
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
