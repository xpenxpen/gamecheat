package org.xpen.westwood;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;
import org.xpen.westwood.fileformat.MixFile;

/**
 * Command & Conquer 1
 * 命令与征服：泰伯利亚黎明
 *
 */
public class CommandConquerMix {

    private static final Logger LOG = LoggerFactory.getLogger(CommandConquerMix.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/git/opensource/dunia2/dos/games-master/dos/C&C.boxer/C.harddisk/cc";
        UserSetting.rootOutputFolder = "D:/git/opensource/dunia2/dos/games-master/dos/C&C.boxer/C.harddisk/cc/myex";
        String[] fileNames = {"LOCAL", "SC-000", "SC-001", "SC-666", "SETUP", "SPEECH", "TRANSIT"};

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (String fileName: fileNames) {
            LOG.debug("---------Starting {}", fileName);

            MixFile mixFile = new MixFile(fileName);
            mixFile.decode();
            mixFile.close();
        }

        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
