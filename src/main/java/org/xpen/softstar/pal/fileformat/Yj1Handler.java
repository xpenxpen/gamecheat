package org.xpen.softstar.pal.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.UserSetting;

public class Yj1Handler implements FileTypeHandler {
    
    
    private static final Logger LOG = LoggerFactory.getLogger(Yj1Handler.class);
    
    private String datFileName;
    private String fname;
    private byte[] bytes;
    ByteBuffer buffer;
    
    public static Color[][] palettes;
    private Header header;
    
    private TreeNode[] treeNodes;
    private TreeNode root;
    private String fileName;
    
    private int headerSize; //include huffman tree
    
    private int bitPos;
    private int tempByte;
    
    int width;
    int height;
    BufferedImage bi;

    public Yj1Handler() {
    }

    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.datFileName = datFileName;
        this.fname = newFileName;
        this.bytes = b;
        //LOG.debug("STARTING fname={}", fname);
        
        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        getWidthHeight();
        decodeHeader();
        decodeHuffmanTree();
        decodeBlock();
        writeFile();
    }


    private void getWidthHeight() {
        if (this.datFileName.equals("FBP")) {
            width = 320;
            height = 200;
        } else if (this.datFileName.equals("BALL")) {
            width = 48;
            height = 47;
        }
        
    }
    
    /**
     * JY1 File format
     * 0x00--0x0F header
     * 0x10--     huffman tree
     * See rest of code
     *
     */
    private void decodeHeader() throws Exception {
        header = new Header();
        header.decode(buffer);
        
        LOG.debug("header={}", header);
    }

    private void decodeHuffmanTree() throws Exception {
        int twice = header.huffmanTreeLength * 2;
        int flagByteCount = twice / 16;
        if (twice % 16 != 0) {
            flagByteCount++;
        }
        
        headerSize = 16 + twice + flagByteCount * 2;
        
        treeNodes = new TreeNode[twice];
        for (int i = 0; i < twice; i++) {
            treeNodes[i] = new TreeNode();
        }
        
        root = new TreeNode();
        root.leaf = false;
        root.value = 0;
        root.left = treeNodes[0];
        root.right = treeNodes[1];
        
        for (int i = 0; i < twice; i++) {
            treeNodes[i].value = buffer.get() & 0xFF;
        }
        BitSet bitSet = BitSet.valueOf(buffer);
        for (int i = 0; i < twice; i++) {
            int get = i/16*16+16 - i%16 - 1;
            treeNodes[i].leaf = !bitSet.get(get);
            if (!treeNodes[i].leaf) {
                treeNodes[i].left = treeNodes[treeNodes[i].value  * 2];
                treeNodes[i].right = treeNodes[treeNodes[i].value  * 2 + 1];
            }
        }
        
        for (int i = 0; i < treeNodes.length; i++) {
            LOG.debug((i+1) + "treeNodes->" + treeNodes[i].leaf + ", " + treeNodes[i].value
                    + ", left=" + (treeNodes[i].left==null?null:treeNodes[i].left.value)
                    + ", right=" + (treeNodes[i].right==null?null:treeNodes[i].right.value));
        }
        
        
    }

    private void decodeBlock() throws Exception {
        int pixelPos = 0;
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        buffer.position(headerSize);

        for (int i = 0; i < header.blockCount; i++) {
            LOG.debug("buffer.position()={}", buffer.position());
            
            BlockHeader blockHeader = new BlockHeader();
            blockHeader.decode(buffer);
            LOG.debug("blockHeader={}", blockHeader);
            
            
            //YJ_1每个编码块中的编码由两种编码混合而成：HUFFMAN编码及LZSS压缩编码
            //交替组成，如下所示：
            //[HUFFMAN编码块][LZSS编码块][HUFFMAN编码块][LZSS编码块]...
            
            int memPos = buffer.position();
            LOG.debug("memPos={}", memPos);
            
            
            byte[] destB = new byte[blockHeader.uncompressedLength];
            int destBPos = 0;
            
            bitPos = 16;
            for (;;) {
                //1. huffman
                int loop = getLoop(blockHeader);
                LOG.debug("huffman loop={}", loop);
                if (loop == 0) {
                    break;
                }
                while (loop > 0) {
                    TreeNode node = root;
                    for (; !node.leaf;) {
                        if (getBit() == 1) {
                            node = node.right;
                        } else {
                            node = node.left;
                        }
                    }
                    destB[destBPos] = (byte)node.value;
                    destBPos++;
                    loop--;
                }
                
                
                //2. LZSS
                loop = getLoop(blockHeader);
                LOG.debug("lzss loop={}", loop);
                if (loop == 0) {
                    break;
                }
                while (loop > 0) {
                    int count = getLzssCount(blockHeader);
                    int offset = getLzssOffset(blockHeader);
                    while (count >  0) {
                        destB[destBPos] = destB[destBPos - offset];
                        destBPos++;
                        count--;
                    }
                    
                    loop--;
                }
            }
            
            for (int k = 0; k < destB.length; k++) {
                bi.setRGB(pixelPos % width, pixelPos / width, palettes[0][destB[k] & 0xFF].getRGB());
                pixelPos++;
            }
            
            buffer.position(memPos + blockHeader.compressedLength-24);
        }
    }


    private int getLoop(BlockHeader blockHeader) throws Exception {
        //编码    值           长度
        //1   CodeCountTable[0]   -
        //000 CodeCountTable[1]   -
        //001 接下来的位数据     CodeCountCodeLengthTable[0]
        //010 接下来的位数据     CodeCountCodeLengthTable[1]
        //011 接下来的位数据     CodeCountCodeLengthTable[2]
        
        if (getBit() == 1) {
            return blockHeader.codeCountTable[0] & 0xFF;
        }
        int flag = getBit() * 2 + getBit();
        if (flag == 0) {
            return blockHeader.codeCountTable[1] & 0xFF;
        }
        int nextReadLength = blockHeader.codeCountCodeLengthTable[flag - 1] & 0xFF;
        int loop = 0;
        for (int i = 0; i < nextReadLength; i++) {
            loop = loop * 2 + getBit();
        }
        
        return loop;
    }

    private int getLzssCount(BlockHeader blockHeader) throws Exception {
        //编码  值           长度
        //00  LZSSRepeatTable[0]  -
        //010 LZSSRepeatTable[1]  -
        //100 LZSSRepeatTable[2]  -
        //110 LZSSRepeatTable[3]  -
        //011 接下来的位数据     LZSSRepeatCodeLengthTable[0]
        //101 接下来的位数据     LZSSRepeatCodeLengthTable[1]
        //111 接下来的位数据 LZSSRepeatCodeLengthTable[2]        
        
        int flag = getBit() * 2 + getBit();
        if (flag == 0) {
            return (blockHeader.lzssRepeatTable[0] & 0xFF) | ((blockHeader.lzssRepeatTable[1] & 0xFF) << 8);
        }
        if (getBit() == 0) {
            return (blockHeader.lzssRepeatTable[flag * 2] & 0xFF) | ((blockHeader.lzssRepeatTable[flag * 2 + 1] & 0xFF) << 8);
        }

        int nextReadLength = blockHeader.lzssRepeatCodeLengthTable[flag - 1] & 0xFF;
        int loop = 0;
        for (int i = 0; i < nextReadLength; i++) {
            loop = loop * 2 + getBit();
        }
        
        return loop;
    }

    private int getLzssOffset(BlockHeader blockHeader) throws Exception {
        //编码  值           长度
        //00  接下来的位数据     LZSSOffsetCodeLengthTable[0]
        //01  接下来的位数据     LZSSOffsetCodeLengthTable[1]
        //10  接下来的位数据     LZSSOffsetCodeLengthTable[2]
        //11  接下来的位数据     LZSSOffsetCodeLengthTable[3]
        int flag = getBit() * 2 + getBit();
        int nextReadLength = blockHeader.lzssOffsetCodeLengthTable[flag] & 0xFF;
        int offset = 0;
        for (int i = 0; i < nextReadLength; i++) {
            offset = offset * 2 + getBit();
        }
        
        return offset;
    }
    
    private int getBit() throws Exception {
        if (bitPos == 16) {
            bitPos = 0;
            tempByte = buffer.getShort() & 0xFFFF;
            LOG.debug("tempByte={}", tempByte);
        }
        int bit = (tempByte >>> (15 - bitPos)) & 0x01;
        bitPos++;
        
        return bit;
        
    }

    private void writeFile() throws Exception {
        File outFile = null;
        outFile = new File(UserSetting.rootOutputFolder, datFileName + "/" + fname + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }

    public class Header {
        public int magic; //'YJ_1'
        public int uncompressedLength;
        public int compressedLength;
        public short blockCount;
        public byte unknown;
        public int huffmanTreeLength;
        
        public void decode(ByteBuffer buffer) {
            this.magic = buffer.getInt();
            this.uncompressedLength = buffer.getInt();
            this.compressedLength = buffer.getInt();
            this.blockCount = buffer.getShort();
            this.unknown = buffer.get();
            this.huffmanTreeLength = buffer.get() & 0xFF;
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public class BlockHeader {
        public int uncompressedLength;     //本块压缩前长度，最大为0x4000
        public int compressedLength;       //本块压缩后长度，含块头
        public byte[] lzssRepeatTable = new byte[8];     //LZSS重复次数表
        public byte[] lzssOffsetCodeLengthTable = new byte[4];   //LZSS偏移量编码长度表
        public byte[] lzssRepeatCodeLengthTable = new byte[3];   //LZSS重复次数编码长度表
        public byte[] codeCountCodeLengthTable = new byte[3];    //同类编码的编码数的编码长度表
        public byte[] codeCountTable = new byte[2]; //同类编码的编码数表
        
        public void decode(ByteBuffer buffer) {
            this.uncompressedLength = buffer.getShort() & 0xFFFF;
            this.compressedLength = buffer.getShort() & 0xFFFF;
            buffer.get(lzssRepeatTable);
            buffer.get(lzssOffsetCodeLengthTable);
            buffer.get(lzssRepeatCodeLengthTable);
            buffer.get(codeCountCodeLengthTable);
            buffer.get(codeCountTable);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    public class TreeNode {
        int value;
        boolean leaf;
        TreeNode parent;
        TreeNode left;
        TreeNode right;
    }

}
