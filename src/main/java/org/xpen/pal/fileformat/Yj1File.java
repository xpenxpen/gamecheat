package org.xpen.pal.fileformat;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Yj1File {
    
    private static final Logger LOG = LoggerFactory.getLogger(Yj1File.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    
    private Header header;
    
    private List<HuffmanTreeEntry> huffmanTreeEntries = new ArrayList<HuffmanTreeEntry>();
    private String fileName;
    
    public static void main(String[] args) throws Exception {
        
        File file = null;
        
        file = new File("D:/git/opensource/dunia2/dos/games-master/dos/pal1/D Pal.cdrom/myex/FBP/000.yj1");
        
        Yj1File yj1File = new Yj1File(file);
        yj1File.decode();


    }
    
    public Yj1File(File file) throws Exception {
        
        raf = new RandomAccessFile(file, "r");
        fileChannel = raf.getChannel();
    }
    
    /**
     * JY1 File format
     * 0x00--0x0F header
     * 0x10--     huffman tree
     * See rest of code
     *
     */
    public void decode() throws Exception {
        
        decodeHeader();
        decodeHuffmanTree();
        
    }


    private void decodeHeader() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        
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
        ByteBuffer buffer = ByteBuffer.allocate(twice + flagByteCount * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        
        for (int i = 0; i < twice; i++) {
            HuffmanTreeEntry huffmanTreeEntry = new HuffmanTreeEntry();
            huffmanTreeEntries.add(huffmanTreeEntry);
            huffmanTreeEntry.value = buffer.get();
        }
        BitSet bitSet = BitSet.valueOf(buffer);
        for (int i = 0; i < twice; i++) {
            int get = i/16*16+16 - i%16 - 1;
            LOG.debug("get={}", get);
            huffmanTreeEntries.get(i).flag = bitSet.get(get);
        }
        
        LOG.debug("bitSet.toByteArray()={}", bitSet.toByteArray());
        
        
        LOG.debug("huffmanTreeEntries={}", huffmanTreeEntries);
        
    }


    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }

    public class Header {
        public int magic; //'YJ_1'
        public int uncompressedLength;
        public int compressedLength;
        public short blockCount;
        public byte unknown;
        public byte huffmanTreeLength;
        
        public void decode(ByteBuffer buffer) {
            this.magic = buffer.getInt();
            this.uncompressedLength = buffer.getInt();
            this.compressedLength = buffer.getInt();
            this.blockCount = buffer.getShort();
            this.unknown = buffer.get();
            this.huffmanTreeLength = buffer.get();
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public class HuffmanTreeEntry {
        public boolean flag;
        public byte value;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
