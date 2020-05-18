package org.xpen.ds;

import java.io.File;
import java.util.Collection;
import java.util.Stack;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.format.FormatConstants;
import org.xpen.ds.format.Ncbr;
import org.xpen.ds.format.Ncer;
import org.xpen.ds.format.Nclr;
import org.xpen.util.HandleCount;
import org.xpen.util.UserSetting;

public class NclrNcbrNcer {
    
    private static final Logger LOG = LoggerFactory.getLogger(NclrNcbrNcer.class);

    /**
     * Extract Nclr Ncbr Ncer
     * 3 files for 1 set, they have the same file name.
     * If file name are different, you can use mapFunction to tell how the names are mapped
     * mapFunction (IN: Ncer name, OUT: Nclr and Ncbr name)
     */
    public static void extractNclrNcbrNcer(String[] folderNames, HandleCount countPair, Function<String, String> mapFunction) {
        Nclr nclr = new Nclr();
        Ncbr ncbr = new Ncbr();
        Ncer ncer = new Ncer();
        
        for (String folderName: folderNames) {
            LOG.debug("---------Starting {}", folderName);
            Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FormatConstants.FILE_SUFFIX_NCER}, true);
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
                    
                    File ncbrFile = new File(level1Parent, mappedNclrNcGrName + "." + FormatConstants.FILE_SUFFIX_NCBR);
                    //change to find NCGR if NCBR not exist
                    if (!ncbrFile.exists()) {
                        ncbrFile = new File(level1Parent, mappedNclrNcGrName + "." + FormatConstants.FILE_SUFFIX_NCGR);
                    }
                    byte[] inBytesNcbr = FileUtils.readFileToByteArray(ncbrFile);
                    ncbr.handle(inBytesNcbr, nclr);
                    
                    File ncerFile = new File(level1Parent, oldFileNameWithoutExt + "." + FormatConstants.FILE_SUFFIX_NCER);
                    byte[] inBytesNcer = FileUtils.readFileToByteArray(ncerFile);
                    ncer.setNclr(nclr);
                    ncer.setNcbr(ncbr);
                    ncer.handle(inBytesNcer, sb.toString(), oldFileNameWithoutExt, false);
                    
                    countPair.handleCount++;
                } catch (Exception e) {
                    LOG.warn("Error occurred, skip " + f.getName(), e);
                }
            }
        }
    }

}
