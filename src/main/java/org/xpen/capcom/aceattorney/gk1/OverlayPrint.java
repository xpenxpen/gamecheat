package org.xpen.capcom.aceattorney.gk1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;

public class OverlayPrint {
    /*
     * overlay9_1
2A0 cc->c4
560 3C
644 68->64
834 98->94
     */
    
    private static final String ROOT_FOLDER = "D:/soft/ga/nds/8003809/root/ftc";
    //private static final String ROOT_FOLDER = "D:/soft/ga/nds/8100412/root/ftc";
    private static final String OUT_FOLDER = "D:/soft/ga/nds/8003809/root/ftcout2";
    //private static final String OUT_FOLDER = "D:/soft/ga/nds/8100412/root/ftcout2";
    
    private static Map<Integer, String> jpnTable;

    public static void main(String[] args) throws Exception {
        loadJpnTable();
        //overlay9_0 ~ 191
        Collection<File> files = FileUtils.listFiles(new File(ROOT_FOLDER),
            new IOFileFilter() {

                @Override
                public boolean accept(File file) {
                    String name = file.getName();
                    if (!name.startsWith("overlay9_")) {
                        return false;
                    }
                    int no;
                    try {
                        no = Integer.parseInt(name.substring(name.indexOf('_') + 1));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    if (no <= 191) {
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
            handle(f);
        }
        //handle(new File(ROOT_FOLDER, "overlay9_1"));

    }
    
    private static void handle(File f) throws Exception {
        Path path = Paths.get(ROOT_FOLDER, f.getName());
        String outputF = OUT_FOLDER + "/" + f.getName();
        
        File parentFile = new File(outputF).getParentFile();
        parentFile.mkdirs();
        Writer fw = new FileWriterWithEncoding(outputF, "UTF-8");
        
        //C0 決
        
        byte[] inBytes = Files.readAllBytes(path);
        int pointer = 0x20;
        boolean isMoji = false;
        while (pointer < inBytes.length) {
            int cur = inBytes[pointer] & 0xFF;
            if (cur == 0xFF) {
                isMoji = false;
                pointer++;
                int symbol = inBytes[pointer] & 0xFF;
                switch (symbol) {
                case 2: //02 01 橙色 //02 03 绿色
                    printCommand(pointer, 2, inBytes, fw, " COLOR");
                    pointer += 2;
                    break;
                case 3: //03 00 等待按键
                    printCommand(pointer, 2, inBytes, fw, null);
                    pointer += 2;
                    break;
                case 4: //下一页
                    printCommand(pointer, 2, inBytes, fw, null);
                    pointer += 2;
                    break;
                case 5: //暂停 
                    printCommand(pointer, 2, inBytes, fw, " PAUSE");
                    pointer += 2;
                    break;
                case 7: //?title
                    printCommand(pointer, 2, inBytes, fw, null);
                    pointer += 2;
                    break;
                case 8: //?flash
                    printCommand(pointer, 3, inBytes, fw, " FLASH");
                    pointer += 3;
                    break;
                case 0x09: //?
                    printCommand(pointer, 3, inBytes, fw, null);
                    pointer += 3;
                    break;
                case 0x0A: //?music
                    printCommand(pointer, 2, inBytes, fw, " MUSIC");
                    pointer += 2;
                    break;
                case 0x0B: //?sound
                    printCommand(pointer, 2, inBytes, fw, " SOUND");
                    pointer += 2;
                    break;
                case 0x10: //?
                    printCommand(pointer, 4, inBytes, fw, " PORTRAIT");
                    pointer += 4;
                    break;
                case 0x16: //?speed
                    printCommand(pointer, 2, inBytes, fw, null);
                    pointer += 2;
                    break;
                case 0x18: //?pic?
                    printCommand(pointer, 4, inBytes, fw, " PIC");
                    pointer += 4;
                    break;
                case 0x1F: //?
                    printCommand(pointer, 2, inBytes, fw, null);
                    pointer += 2;
                    break;
                case 0x20: //?
                    printCommand(pointer, 2, inBytes, fw, null);
                    pointer += 2;
                    break;
                    
                default:
                    System.out.println("symbol=" + symbol + "," +  Integer.toHexString(pointer));
                    printCommand(pointer, 1, inBytes, fw, null);
                    pointer += 1;
                    break;
                }
            } else if (cur == 0xFE) {
                isMoji = false;
                fw.write("[0xFE]");
                pointer++;
            } else {
                if (!isMoji) {
                    isMoji = true;
                    fw.write("\n");
                    fw.write(Integer.toHexString(pointer) + ":");
                }
                if (cur >= 0x81) {
                    byte[] moji = new byte[2];
                    moji[0] = inBytes[pointer];
                    moji[1] = inBytes[pointer + 1];
                    fw.write(new String(moji, Charset.forName("SHIFT_JIS")));
                    pointer += 2;
                } else {
                    if (jpnTable.containsKey(cur)) {
                        fw.write(jpnTable.get(cur));
                    } else {
                        fw.write("[0x" + Integer.toHexString(cur) + "]");
                    }
                    pointer += 1;
                }
            }
        }
        
        
        fw.close();
       
    }

    private static void printCommand(int pointer, int length, byte[] inBytes, Writer fw, String message) throws Exception {
        fw.write("[0xFF");
        for (int i = 0; i < length; i++) {
            String twoDigit = StringUtils.leftPad(Integer.toHexString(inBytes[pointer + i] & 0xFF), 2, '0');
            fw.write(twoDigit);
        }
        if (message != null) {
            fw.write(message);
        }
        fw.write("]");
    }

    private static void loadJpnTable() throws IOException {
        jpnTable= new HashMap<>();
        String f = "src/main/resources/capcom/aceattorney/jpn_table.txt";
        List<String> lines = IOUtils.readLines(new FileInputStream(f), "UTF-8");
        for (String line : lines) {
            if (line.trim().length() == 0) {
                continue;
            }
            String[] split = line.split("\\=");
            jpnTable.put(Integer.parseInt(split[0], 16), split[1]);
        }
    }

}
