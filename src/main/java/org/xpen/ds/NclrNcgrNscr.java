package org.xpen.ds;

import java.io.File;
import java.util.Collection;
import java.util.Stack;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.format.FormatConstants;
import org.xpen.ds.format.Ncgr;
import org.xpen.ds.format.Nclr;
import org.xpen.ds.format.Nscr;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

public class NclrNcgrNscr {
    
    private static final Logger LOG = LoggerFactory.getLogger(NclrNcgrNscr.class);

    /**
     * Extract Nclr Ncgr Nscr
     * 3 files for 1 set, they have the same file name.
     * If file name are different, you can use mapFunction to tell how the names are mapped
     * mapFunction (IN: Nscr name, OUT: Nclr and Ncgr name)
     */
    public static void extractNclrNcgrNscr(String[] folderNames, HandleCount countPair, Function<String, String> mapFunction) {
        
        Nclr nclr = new Nclr();
        Ncgr ncgr = new Ncgr();
        Nscr nscr = new Nscr();
        
        for (String folderName: folderNames) {
            LOG.debug("---------Starting {}", folderName);
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FormatConstants.FILE_SUFFIX_NSCR}, true);
            for (File f : files) {
                countPair.totalCount++;
                try {
                    
                    String oldFileNameWithoutExt = f.getName().substring(0, f.getName().lastIndexOf("."));
                    File level1Parent = f.getParentFile();
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
                    
                    String mappedNclrNcGrName = oldFileNameWithoutExt;
                    if (mapFunction != null) {
                        mappedNclrNcGrName = mapFunction.apply(oldFileNameWithoutExt);
                    }
                    
                    File nclrFile = new File(level1Parent, mappedNclrNcGrName + "." + FormatConstants.FILE_SUFFIX_NCLR);
                    byte[] inBytesNclr = FileUtils.readFileToByteArray(nclrFile);
                    nclr.handle(inBytesNclr);
                    
                    File ncgrFile = new File(level1Parent, mappedNclrNcGrName + "." + FormatConstants.FILE_SUFFIX_NCGR);
                    byte[] inBytesNcgr = FileUtils.readFileToByteArray(ncgrFile);
                    ncgr.handle(inBytesNcgr, nclr);
                    
                    File nscrFile = new File(level1Parent, oldFileNameWithoutExt + "." + FormatConstants.FILE_SUFFIX_NSCR);
                    byte[] inBytesNscr = FileUtils.readFileToByteArray(nscrFile);
                    nscr.setNclr(nclr);
                    nscr.setNcgr(ncgr);
                    nscr.handle(inBytesNscr, sb.toString(), oldFileNameWithoutExt, false);
                    
                    countPair.handleCount++;
                } catch (Exception e) {
                    LOG.warn("Error occurred, skip " + f.getName(), e);
                }
            }
        }
    }

}
