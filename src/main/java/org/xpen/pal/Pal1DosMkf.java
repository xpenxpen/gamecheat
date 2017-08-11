package org.xpen.pal;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.pal.fileformat.MkfFile;
import org.xpen.util.UserSetting;

public class Pal1DosMkf {

    private static final Logger LOG = LoggerFactory.getLogger(Pal1DosMkf.class);

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "G:/f/VirtualNes/pal/Pal";
        UserSetting.rootOutputFolder = "G:/f/VirtualNes/pal/Pal/myex";
        String[] fileNames = {"BALL", "FBP"};
        String patFileName = MkfFile.PAT_FILE_NAME;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        MkfFile patFile = new MkfFile(patFileName);
        patFile.decode();
        patFile.close();

        for (String fileName: fileNames) {
            LOG.debug("---------Starting {}", fileName);

            MkfFile mkfFile = new MkfFile(fileName);
            mkfFile.decode();
            mkfFile.close();
        }

        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
