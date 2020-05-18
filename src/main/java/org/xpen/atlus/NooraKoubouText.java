package org.xpen.atlus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NooraKoubouText {
    
    private static final Logger LOG = LoggerFactory.getLogger(NooraKoubouText.class);
    private static final String ROOT_FOLDER = "D:/soft/ga/nds/8100387/root";
    private static final String OUT_FOLDER = "D:/soft/ga/nds/8100387/root/myex";
    private static Map<Integer, String> chnTable;
    private static int kanjiTotalCount;
    private static int kanjiHitCount;
    private static Set<Integer> unHitSet = new HashSet<>();

    public static void main(String[] args) throws Exception {
        loadChnTable();
        int totalCount = 0;
        int handleCount = 0;
        
        //script_0000~1642
        Path adv = Paths.get(ROOT_FOLDER, "scn/adv");
        Collection<File> files = FileUtils.listFiles(adv.toFile(),
            new IOFileFilter() {

                @Override
                public boolean accept(File file) {
                    String name = file.getName();
                    if (!name.startsWith("script_")) {
                        return false;
                    }
                    int no;
                    try {
                        no = Integer.parseInt(name.substring(name.indexOf('_') + 1, name.lastIndexOf('.')));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    if (no <= 1642) {
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean accept(File dir, String name) {
                    return false;
                }
            }, null);
        for (File f : files) {
            totalCount++;
            try {
                handle("scn/adv/" + f.getName());
                handleCount++;
            } catch (Exception e) {
                LOG.warn("Error occurred, skip {}", f.getName());
            }
        }
        System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);
        printHitPercent();
    }
    
    private static void handle(String fileName) throws Exception {
        Path path = Paths.get(ROOT_FOLDER, fileName);
        Path pathOut = Paths.get(OUT_FOLDER, fileName);
        
        File parentFile = pathOut.toFile().getParentFile();
        parentFile.mkdirs();
        Writer fw = new FileWriterWithEncoding(pathOut.toFile(), "UTF-8");
        
        byte[] inBytes = Files.readAllBytes(path);
        ByteBuffer buffer = ByteBuffer.wrap(inBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.position(12);
        int startOffset = buffer.getInt();
        
        buffer.position(startOffset);
        int firstEntryOffset = buffer.getInt();
        int entryCount = (firstEntryOffset - startOffset) / 4;
        
        List<Integer> offsets = new ArrayList<>();
        offsets.add(firstEntryOffset);
        for (int i = 1; i < entryCount; i++) {
            int entryOffset = buffer.getInt();
            offsets.add(entryOffset);
        }
        
        for (Integer offset: offsets) {
            int pointer = offset;
            while (pointer < inBytes.length) {
                int no = ((inBytes[pointer + 1] & 0xFF) << 8) + (inBytes[pointer] & 0xFF);
                if (no == 0) {
                    fw.write("\n");
                    break;
                }
                kanjiTotalCount++;
                if (no <= 0x7E) {
                    fw.write(Character.valueOf((char)no));
                    kanjiHitCount++;
                } else if (chnTable.containsKey(no)) {
                    fw.write(chnTable.get(no));
                    kanjiHitCount++;
                } else {
                    fw.write("[0x" + Integer.toHexString(no) + "]");
                    unHitSet.add(no);
                }
                pointer += 2;
            }
        }
        
        
        
        fw.close();
       
    }

    private static void printHitPercent() {
        System.out.println("---------");
        System.out.println("Totol char=" + kanjiTotalCount + ", Hit char=" + kanjiHitCount + ", HIT Percent(%):" + kanjiHitCount * 100 / (float)kanjiTotalCount);
        System.out.println("Unhit kanji count=" + unHitSet.size());
    }

    private static void loadChnTable() throws IOException {
        chnTable= new HashMap<>();
        String f = "src/main/resources/atlus/noora_koubou/chn_table.txt";
        List<String> lines = IOUtils.readLines(new FileInputStream(f), "UTF-8");
        for (String line : lines) {
            if (line.trim().length() == 0) {
                continue;
            }
            String[] split = line.split("\\=");
            chnTable.put(Integer.parseInt(split[0], 16), split[1]);
        }
    }

}
