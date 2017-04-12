package org.xpen.dunia2.fileformat.mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.fcbn.FcbnExtractor;
import org.xpen.dunia2.fileformat.xbg.chunk.Chunk;

public class MatExtractor {
    public static final int MAGIC_MAT = 0x004D4154; //'\0MAT'
    public static final int MAGIC_FCBN = 0x4643626E; //'FCbn'
    
    private static final Logger LOG = LoggerFactory.getLogger(MatExtractor.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private byte[] bytes;
    private File file;
    public Chunk root;
    
    public MatExtractor(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public MatExtractor(File file) throws Exception {
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
        decode1(new File("D:/git/opensource/dunia2/fc3dat/myex/worlds/fc3_main/fc3_main/graphics/_materials/YANZHOU-M-3101201251837020.material.bin"));
        //decode1(new File("E:/aliBoxGames/games/5993/myex/worlds/fc3_main/fc3_main/graphics/_materials/VFORTIN-M-2011032246212114.material.bin"));

    }
    
    private static void decode1(File file) throws Exception {
        LOG.debug("Starting:{}", file);
        MatExtractor matExtractor = new MatExtractor(file);
        matExtractor.decode();
    }

    /**
     * MAT File format
     * 4 'MAT'
     * 4 'FCbn'
     */
     private void decode() throws Exception {
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
        public int totalObjectCount;
        public int totalValueCount;
        public byte unknown24;
        public byte unknown25;
        public int unknown26;
        public int unknown2A;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
        
        public void decode(ByteBuffer buffer) throws Exception {
            
            int magicMesh = buffer.getInt();
            if (magicMesh != MAGIC_MAT) {
                throw new RuntimeException("magic <> 'MAT'");
            }
            
            unknown0000 = buffer.getInt();
            if (unknown0000 != 0) {
                throw new RuntimeException("unknown0000 wrong");
            }
            
            unknown08 = buffer.getInt();
            unknown0C = buffer.getInt();
            unknown10 = buffer.getShort();  //0
            unknown12 = buffer.getShort();  //6/9/1
            
            byte[] array = buffer.array();
            byte[] dest = new byte[array.length-20];
            System.arraycopy(array, 20, dest, 0, array.length-20);
            
            FcbnExtractor fcbnExtractor = new FcbnExtractor(dest);
            fcbnExtractor.decode();
            
       }
        
        public void decode2(ByteBuffer buffer) {
            
            int magicMesh = buffer.getInt();
            if (magicMesh != MAGIC_MAT) {
                throw new RuntimeException("magic <> 'MAT'");
            }
            
            unknown0000 = buffer.getInt();
            if (unknown0000 != 0) {
                throw new RuntimeException("unknown0000 wrong");
            }
            
            unknown08 = buffer.getInt();
            unknown0C = buffer.getInt();
            unknown10 = buffer.getShort();  //0
            unknown12 = buffer.getShort();  //6/9/1
            int fcbn = buffer.getInt();
            if (fcbn != MAGIC_FCBN) {
                throw new RuntimeException("fcbn wrong");
            }
            
            unknown18 = buffer.getInt(); //2
            totalObjectCount = buffer.getInt(); //26/1/4
            totalValueCount = buffer.getInt(); //25/0/3
            
            unknown24 = buffer.get();//25/3/0
            unknown25 = buffer.get();//149
            unknown26 = buffer.getInt();
            if (unknown26 != 0x027CBE75) {
                throw new RuntimeException("unknown26 wrong");
            }
            unknown2A = buffer.getInt();
            if (unknown2A != 0x5E237E06) {
                throw new RuntimeException("unknown2A wrong");
            }
            
            byte keyLength = buffer.get();
            byte[] key = new byte[keyLength];
            buffer.get(key);
            String keyName = new String(key, 0, keyLength - 1, Charset.forName("UTF-8"));
            int u3f = buffer.getInt();
            
            //shader, see common\engine\shaders\materialdescriptors
            
            byte shaderLength = buffer.get();
            byte[] shader = new byte[shaderLength];
            buffer.get(shader);
            String shaderName = new String(shader, 0, shaderLength - 1, Charset.forName("UTF-8"));
            
            LOG.debug("keyName={}, shaderName={}", keyName, shaderName);
            
       }
    }


}
