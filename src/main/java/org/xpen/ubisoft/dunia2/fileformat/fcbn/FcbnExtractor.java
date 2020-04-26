package org.xpen.ubisoft.dunia2.fileformat.fcbn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FcbnExtractor {
    public static final int MAGIC_FCBN = 0x4643626E; //'FCbn'
    
    private static final Logger LOG = LoggerFactory.getLogger(FcbnExtractor.class);
    
    private byte[] bytes;
    private File file;
    public File outFile;
    
    public FcbnExtractor(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public FcbnExtractor(File file) throws Exception {
        this.file = file;
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
        this.bytes = bytes;
    }

    public static void main(String[] args) throws Exception {
        
        //File file = null;
        
        //file = new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/yanzhou-m-1006201152940415.material.material.bin");
        
//        JFileChooser chooser = new JFileChooser();
//        chooser.setCurrentDirectory(new File("E:/aliBoxGames/games/5993/ex"));
//        int result = chooser.showOpenDialog(null);
//        
//      if (result == JFileChooser.APPROVE_OPTION) {
//          file = chooser.getSelectedFile();
//          
//      }
        
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/nbabin-m-2010102641657815.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/yanzhou-m-1006201152940415.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/grassam-m-1403201250256585.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/njinnah-m-0106201158152331.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/smaingot-m-2011031039392746.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/ocean.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/JLI-M-107201142503596.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/JLI-M-107201151201026.material.bin"));
//        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/YANZHOU-M-3101201251837020.material.bin"));
        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/common/generated/databases/generic/shoppingitems.fcb"));
        //decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/common/generated/databases/generic/vehiclecallingservice.fcb"));
        //decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/common/generated/nomadobjecttemplates.fcb"));

    }
    
    public static void decode1(File file) throws Exception {
        LOG.debug("Starting:{}", file);
        FcbnExtractor fcbnExtractor = new FcbnExtractor(file);
        String absolutePathIn = file.getAbsolutePath();
        String fullPath = FilenameUtils.getFullPath(absolutePathIn);
        String baseName = FilenameUtils.getBaseName(absolutePathIn);
        fcbnExtractor.outFile = new File(fullPath + baseName + ".xml");
        fcbnExtractor.decode();
    }

    /**
     * FCBN File format
     * 4 'FCbn'
     */
     public void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        Unknown0 unknown0 = new Unknown0();
        unknown0.decode(buffer);
        LOG.debug("unknown0={}", unknown0);
    }
     
    public class Unknown0 {
        
        
        public void decode(ByteBuffer buffer) throws Exception {
            
            int magicMesh = buffer.getInt();
            if (magicMesh != MAGIC_FCBN) {
                throw new RuntimeException("magic <> 'fcbn'");
            }
            
            short version = buffer.getShort();
            if (version != 2) {
                throw new RuntimeException("unsupportted version");
            }
            
            short flag = buffer.getShort();
            if (flag != 0) {
                throw new RuntimeException("unsupportted flag");
            }
            
            int totalObjectCount = buffer.getInt();
            int totalValueCount = buffer.getInt();
            
            //Pair<Integer, Boolean> pair = BinaryObject.getCount(buffer);
            
            //read count
            LOG.debug("totalObjectCount={}, totalValueCount={}", totalObjectCount, totalValueCount);

            BinaryObject bo = new BinaryObject();
            bo.level=0;
            bo.decode(buffer);
            
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            bo.dump2Xml(sb);
            IOUtils.write(sb.toString(), new FileOutputStream(outFile), Charset.forName("ISO-8859-1"));
       }
    }


}
