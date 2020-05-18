package org.xpen.namco.conankindaichi;

import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.UserSetting;
import org.xpen.util.compress.NintendoLz10Compressor;

public class ConanKindaichiText {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConanKindaichiText.class);
    private static final String FILE_SUFFIX_ZZZ = "zzz";
    private static String folderName = "scrpt";

    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8003356/root";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8003356/root/myex";
        
            
        Collection<File> files = FileUtils.listFiles(new File(UserSetting.rootInputFolder, folderName),
                    new String[]{FILE_SUFFIX_ZZZ}, false);
        for (File f : files) {
            handle(f);
        }

    }
    
    private static void handle(File f) throws Exception {
        Path path = f.toPath();
        String outputF = UserSetting.rootOutputFolder + "/" + folderName + "/" + f.getName();
        
        File parentFile = new File(outputF).getParentFile();
        parentFile.mkdirs();
        Writer fw = new FileWriterWithEncoding(outputF, "UTF-8");
        
        //C0 決
        
        byte[] inBytes = Files.readAllBytes(path);
        inBytes = NintendoLz10Compressor.decompress(inBytes);
        
        int pointer = 0x0;
        boolean isMoji = false;
        boolean isKana = false;
        while (pointer < inBytes.length) {
            int cur = inBytes[pointer] & 0xFF;
            if (cur == 0x70) {
                isMoji = false;
                pointer++;
                int symbol = inBytes[pointer] & 0xFF;
                switch (symbol) {
                case 0x03: //?
                    printCommand(pointer, 5, inBytes, fw, " ?");
                    pointer += 5;
                    break;
                case 0x04: //?
                    printCommand(pointer, 5, inBytes, fw, " ?");
                    pointer += 5;
                    break;
                case 0x05: //?
                    printCommand(pointer, 2, inBytes, fw, " MAN NAME");
                    pointer += 2;
                    break;
                case 0x06: //color 01绿色 02蓝色 03红色
                    printCommand(pointer, 2, inBytes, fw, " COLOR");
                    pointer += 2;
                    break;
                case 0x07: //?
                    printCommand(pointer, 3, inBytes, fw, " ?");
                    pointer += 3;
                    break;
                case 0x08: //?
                    printCommand(pointer, 5, inBytes, fw, " ?");
                    pointer += 5;
                    break;
                case 0x0A: //?
                    printCommand(pointer, 3, inBytes, fw, " ?");
                    pointer += 3;
                    break;
                case 0x0B: //?
                    printCommand(pointer, 4, inBytes, fw, " ?");
                    pointer += 4;
                    break;
                case 0x0C: //?
                    printCommand(pointer, 3, inBytes, fw, " ?");
                    pointer += 3;
                    break;
                case 0x0D: //?
                    printCommand(pointer, 2, inBytes, fw, " ?");
                    pointer += 2;
                    break;
                case 0x14: //kana
                    if (!isKana) {
                        fw.write("\n[KANA]");
                        isKana = true;
                    } else {
                        fw.write("[/KANA]");
                        isKana = false;
                    }
                    pointer += 1;
                    break;
                case 0x17: //?
                    printCommand(pointer, 3, inBytes, fw, " ?");
                    pointer += 3;
                    break;
                case 0x18: //?
                    printCommand(pointer, 3, inBytes, fw, " ?");
                    pointer += 3;
                    break;
                case 0x19: //?
                    printCommand(pointer, 3, inBytes, fw, " ?");
                    pointer += 3;
                    break;
                case 0x1B: //?
                    printCommand(pointer, 4, inBytes, fw, " ?");
                    pointer += 4;
                    break;
                case 0x21: //等待按键
                    printCommand(pointer, 1, inBytes, fw, " INPUT");
                    pointer += 1;
                    break;
                case 0x22: //换行
                    printCommand(pointer, 1, inBytes, fw, " LINE");
                    pointer += 1;
                    break;
                    
                default:
                    System.out.println("symbol=" + symbol + "," +  Integer.toHexString(pointer));
                    printCommand(pointer, 1, inBytes, fw, null);
                    pointer += 1;
                    break;
                }
            } else {
                if (!isMoji && !isKana) {
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
                    fw.write("[0x" + Integer.toHexString(cur) + "]");
                    pointer += 1;
                }
            }
        }
        
        
        fw.close();
       
    }

    private static void printCommand(int pointer, int length, byte[] inBytes, Writer fw, String message) throws Exception {
        fw.write("[0x70");
        for (int i = 0; i < length; i++) {
            String twoDigit = StringUtils.leftPad(Integer.toHexString(inBytes[pointer + i] & 0xFF), 2, '0');
            fw.write(twoDigit);
        }
        if (message != null) {
            fw.write(message);
        }
        fw.write("]");
    }

}
