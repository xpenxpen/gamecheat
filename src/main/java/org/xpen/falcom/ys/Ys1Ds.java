package org.xpen.falcom.ys;

import java.io.File;
import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.format.Ncgr;
import org.xpen.ds.format.Nclr;
import org.xpen.ds.format.Nscr;
import org.xpen.util.UserSetting;

/**
 * Ys1
 * 伊苏1
 * イース1
 * 29/29
 *
 */
public class Ys1Ds {
    
    private static final Logger LOG = LoggerFactory.getLogger(Ys1Ds.class);
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100050/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100050/myex";
        String[] folderNames = {"event", "shop"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        int totalCount = 0;
        int handleCount = 0;
        Nclr nclr = new Nclr();
        Ncgr ncgr = new Ncgr();
        Nscr nscr = new Nscr();
        
        for (String folderName: folderNames) {
            LOG.debug("---------Starting {}", folderName);
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{Nclr.FILE_SUFFIX_NCLR}, false);
            for (File f : files) {
                totalCount++;
                try {
                    byte[] inBytes = FileUtils.readFileToByteArray(f);
                    
                    String oldFileNameWithoutExt = f.getName().substring(0, f.getName().lastIndexOf("."));
                    File parent = f.getParentFile();
                    Stack<String> folderNameStack = new Stack<>();
                    while (!parent.getName().equals(folderName)) {
                        folderNameStack.push(parent.getName());
                        parent = parent.getParentFile();
                    }
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(folderName);
                    while (!folderNameStack.isEmpty()) {
                        sb.append("/");
                        sb.append(folderNameStack.pop());
                    }
                    
                    nclr.handle(inBytes);
                    
                    File ncgrFile = new File(parent, oldFileNameWithoutExt + "." + Ncgr.FILE_SUFFIX_NCGR);
                    byte[] inBytesNcgr = FileUtils.readFileToByteArray(ncgrFile);
                    ncgr.handle(inBytesNcgr, nclr);
                    
                    File nscrFile = new File(parent, oldFileNameWithoutExt + "." + Nscr.FILE_SUFFIX_NSCR);
                    byte[] inBytesNscr = FileUtils.readFileToByteArray(nscrFile);
                    nscr.setNclr(nclr);
                    nscr.setNcgr(ncgr);
                    nscr.handle(inBytesNscr, sb.toString(), oldFileNameWithoutExt, false);
                    
                    handleCount++;
                } catch (Exception e) {
                    LOG.warn("Error occurred, skip {}", f.getName());
                }
            }
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);
    }

}
