package org.xpen.snowstep;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.snowstep.fileformat.StfFile;
import org.xpen.util.UserSetting;

/**
 * Turtle Bay
 *
 */
public class TurtleBayStf {

    private static final Logger LOG = LoggerFactory.getLogger(TurtleBayStf.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/KuaiwanGames/Games/2083/data";
        UserSetting.rootOutputFolder = "E:/KuaiwanGames/Games/2083/data/myex";
        String[] fileNames = {"data"};

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (String fileName: fileNames) {
            LOG.debug("---------Starting {}", fileName);

            StfFile stfFile = new StfFile(fileName);
            stfFile.decode();
            stfFile.close();
        }

        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
