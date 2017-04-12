package org.xpen.dunia2.fileformat.fcbn;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.xbg.chunk.Chunk;

public class FcbnExtractor {
    public static final int MAGIC_FCBN = 0x4643626E; //'FCbn'
    
    private static final Logger LOG = LoggerFactory.getLogger(FcbnExtractor.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private byte[] bytes;
    private File file;
    public Chunk root;
    
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

    }
    
    private static void decode1(File file) throws Exception {
        LOG.debug("Starting:{}", file);
        FcbnExtractor fcbnExtractor = new FcbnExtractor(file);
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
        public int unknown0000;
        public int unknown08;
        public int unknown0C;
        public short unknown10;
        public short unknown12;
        public int unknown18;
        public int unknown1C;
        public int unknown20;
        public byte unknown24;
        public byte unknown25;
        public int unknown26;
        public int unknown2A;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
        
        public void decode(ByteBuffer buffer) {
            
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
            bo.dump2Xml(sb);
            System.out.println(sb.toString());
       }
    }


}
