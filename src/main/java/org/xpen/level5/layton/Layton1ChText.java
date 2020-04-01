package org.xpen.level5.layton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

public class Layton1ChText {
    private static final String ROOT_FOLDER = "D:/soft/game/nds/8100435/root/data/";
    private static Map<String, String> chnTable;
    private static int totalCount;
    private static int hitCount;
    private static Set<String> unHitSet = new HashSet<>();
    private static Set<Integer> solvedSet = new HashSet<>(Arrays.asList(
            1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
            21,22,23,24,25,26,27,29,30,32,33,34,35,36,37,38,39,40,
            41,42,43,44,45,49,50,51,54,57,59,60,
            62,64,65,67,68,69,70,73,74,78,79,80,
            81,84,90,95,98,
            103,105,107,113,115,117,120,
            121,122,124,125,129,134,135,136,137,140,
            142,143,144,151,159,160));

    public static void main(String[] args) throws Exception {
        loadChnTable();
        translateFolder("qtext");
        translateFolder("storytext");
        translateFolder("pitext");
        //translateFolder("etext");
        translateFolder("htext");
        translateFolder("itext");
        translateFolder("otext");
        translateFolder("wifi");
        translateFolder("room/tobj");
        printTitleSummary("qtext", "qtext/out/", "t_", 162, ".txt");
        //printTitleSummary("inspectMemo", "storytext/out/", "t_", 45, ".txt");
        printHitPercent();
    }

    private static void printHitPercent() {
        System.out.println("---------");
        System.out.println("Totol char=" + totalCount + ", Hit char=" + hitCount + ", HIT Percent(%):" + hitCount * 100 / (float)totalCount);
        System.out.println("Unhit kanji count=" + unHitSet.size());
    }

    private static void translateFolder(String folder) throws IOException {
        Collection<File> files = FileUtils.listFiles(new File(ROOT_FOLDER + folder), new String[]{"txt"}, false);
        for (File f : files) {
            translate(folder, f.getName());
        }
    }

    private static void printTitleSummary(String message, String folder,
            String prefix, int count, String suffix) throws IOException {
        System.out.println("---------");
        System.out.println(message);
        System.out.println("---------");
        for (int i = 1; i <= count; i++) {
            if (solvedSet.contains(i)) {
                continue;
            }
            File f = new File(ROOT_FOLDER + folder + prefix + i + suffix);
            String no = f.getName().substring(2);
            List<String> lines = IOUtils.readLines(new FileInputStream(f), "UTF-8");
            System.out.println(no + "-" + lines.get(0));
        }
        
    }

    private static void translate(String folder, String inputF) throws IOException {
        String outputF = ROOT_FOLDER + folder + "/out/" + inputF;
        
        File parentFile = new File(outputF).getParentFile();
        parentFile.mkdirs();
        
        Writer fw = new FileWriterWithEncoding(outputF, "UTF-8");
        
        List<String> lines = IOUtils.readLines(new FileInputStream(ROOT_FOLDER + folder + "/" + inputF), "SHIFT_JIS");
        for (String line : lines) {
            int length = line.length();
            for (int i = 0; i < length; i++) {
                String key = line.substring(i, i + 1);
                totalCount++;
                if (chnTable.containsKey(key)) {
                    hitCount++;
                    String mapped = chnTable.get(key);
                    fw.write(mapped);
                } else {
                    byte[] bytes = key.getBytes("SHIFT_JIS");
                    //System.out.println(bytes[0]);
                    //System.out.println(bytes[1]);
                    if (bytes[0] == 0xFF) {
                        //System.out.println("FFFF");
                        fw.write(0xFFFF);
                    } else {
                        unHitSet.add(key);
                        fw.write(key);
                    }
                }
            }
            fw.write("\r\n");
        }
        fw.close();
    }

    private static void loadChnTable() throws IOException {
        chnTable= new HashMap<>();
        String f = "src/main/resources/layton/layton1_chn_table.txt";
        List<String> lines = IOUtils.readLines(new FileInputStream(f), "UTF-8");
        for (String line : lines) {
            if (line.trim().length() == 0) {
                continue;
            }
            chnTable.put(line.substring(0, 1), line.substring(2, 3));
        }
    }

}
