package org.xpen.capcom.aceattorney.gk1;

import java.io.File;
import java.io.FileInputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

public class EncodingConvert {
    private static final String ROOT_FOLDER = "D:/soft/ga/nds/8003809/root/ftc";
    private static final String OUT_FOLDER = "D:/soft/ga/nds/8003809/root/ftcout";

    public static void main(String[] args) throws Exception {
        Collection<File> files = FileUtils.listFiles(new File(ROOT_FOLDER), null, false);
        for (File f : files) {
            String outputF = OUT_FOLDER + "/" + f.getName();
            
            File parentFile = new File(outputF).getParentFile();
            parentFile.mkdirs();
            Writer fw = new FileWriterWithEncoding(outputF, "UTF-8");
            
            List<String> lines = IOUtils.readLines(new FileInputStream(ROOT_FOLDER + "/" + f.getName()), "SHIFT_JIS");
            for (String line : lines) {
                fw.write(line);
            }
            fw.close();
            
        }

    }

}
