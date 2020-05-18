package org.xpen.falcom.ys;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Ys2
 * 伊苏2
 * イース2
 * 70/70
 *
 */
public class Ys2Ds extends Ys1Ds {
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100048/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100048/myex";
        String[] folderNames = {"ending", "opening", "portrait", "shop"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HandleCount countPair = new HandleCount();
        
        NclrNcgrNscr.extractNclrNcgrNscr(folderNames, countPair,
        (t)-> {
            if (t.startsWith("ED_ROLL")) {
                return "ED_ROLL";
            }
            return t;
        });
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+countPair.totalCount + ",handleCount= "+countPair.handleCount);
    }

}
