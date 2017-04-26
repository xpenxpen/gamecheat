package org.xpen.dunia2.filenameguess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.Fat2File;
import org.xpen.dunia2.fileformat.fat2.CRC64;
import org.xpen.dunia2.fileformat.fat2.Entry;
import org.xpen.util.UserSetting;

public class FileNameGuesser {
    
    private static final Logger LOG = LoggerFactory.getLogger(FileNameGuesser.class);

    public static void main2(String[] args) throws Exception {
        UserSetting.rootInputFolder = "E:/aliBoxGames/games/5993/FarCry 3/data_win32";
        UserSetting.rootOutputFolder = "E:/aliBoxGames/games/5993/myex";
    	//String[] fileNames = {"common", "patch", "igepatch", "ige", "worlds/fc3_main/fc3_main"};
    	//String[] fileNames = {"worlds/fc3_main/fc3_main"};
    	String[] fileNames = {"ige"};
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        for (String fileName: fileNames) {
        	LOG.debug("---------Starting {}", fileName);
            
            Fat2File fat2File = new Fat2File(fileName);
            fat2File.decode();
            fat2File.close();
            
            List<Entry> entries = fat2File.getEntries();
            Set<Long> hashes = new HashSet<Long>();
	        for (Entry entry : entries) {
	        	hashes.add(entry.nameHash.longValue());
	        }
	        
	        BufferedWriter bw = new BufferedWriter(new FileWriter("guessFile.txt"));
	        
	        String guessFileNameBase = "ingameeditor\\thumbnails\\pc\\";
	        for (long i = 0; i < 5000000000L; i++) {
	        	String guessFileName = guessFileNameBase + i + ".xbt";
	            long crc = new CRC64().update(guessFileName);
	            if (hashes.contains(crc)) {
	            	bw.write(guessFileName + "\r\n");
	            }
	        }
	        
//	        String guessFileNameBase = "ingameeditor\\thumbnails\\png_src\\";
//        	String guessFileName = guessFileNameBase + "1000533310.xbt";
//            long crc = new CRC64().update(guessFileName);
//            if (hashes.contains(crc)) {
//            	System.out.println(guessFileName + "\r\n");
//            }
	        
	        bw.close();
            
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

    public static void main(String[] args) throws Exception {
        //UserSetting.rootInputFolder = "D:/git/opensource/dunia2/fc3dat/myex/worlds/multicommon/multicommon/unknown/root.xml";
        UserSetting.rootInputFolder = "D:/git/opensource/dunia2/fc4dat/myex/worlds/fcc_main/fcc_main/generated/worlds/fcc_main/fcc_main_depload.root.xml";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        //Collection<File> listFiles = FileUtils.listFiles(new File(UserSetting.rootInputFolder), new String[]{"xml"}, false);
        //File file = new File(UserSetting.rootInputFolder+"/0be1c8d802bcc78e.root.xml");
        File file = new File(UserSetting.rootInputFolder);
        BufferedWriter bw = new BufferedWriter(new FileWriter("guessFile2.txt"));
        Pattern pattern = Pattern.compile("^.+ID\\=\"([^\"]+)\".+$");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line=br.readLine())!=null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String guessFile = matcher.group(1);
                    if (guessFile.indexOf("\\")==-1) {
                        continue;
                    }
                    bw.write(guessFile);
                    bw.write('\n');
                }
            }
            br.close();

            
        bw.close();
            
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }

}
