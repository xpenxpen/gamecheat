package org.xpen.falcom.ys;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.xpen.ds.NclrNcbrNcer;
import org.xpen.ds.NclrNcgrNscr;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

/**
 * Ys1
 * 伊苏1
 * イース1
 * 29/29
 *
 */
public class Ys1Ds {
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100050/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100050/myex";
        String[] folderNames = {"event", "shop"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HandleCount countPair = new HandleCount();
        
        NclrNcgrNscr.extractNclrNcgrNscr(folderNames, countPair, null);
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+countPair.totalCount + ",handleCount= "+countPair.handleCount);
    }

}
