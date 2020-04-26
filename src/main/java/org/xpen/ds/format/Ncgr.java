package org.xpen.ds.format;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.ByteBufferUtil;

public class Ncgr {
    
    private static final Logger LOG = LoggerFactory.getLogger(Ncgr.class);
    public static final String FILE_SUFFIX_NCGR = "NCGR";

    protected Nclr nclr;
    private byte[] bytes;
    public NcgrEntry ncgrEntry;
    
    public void handle(byte[] b, Nclr nclr) throws Exception {
        this.nclr = nclr;
        this.bytes = b;
        decodeDat();
    }
        
    private void decodeDat() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        ncgrEntry = new NcgrEntry();
        ncgrEntry.decode(buffer);
    }
    
    public class NcgrEntry {
        public DsGenericHeader genericHeader;
        public RahcEntry rahc;
        
        public void decode(ByteBuffer buffer) throws Exception {
            genericHeader = new DsGenericHeader();
            genericHeader.decode(buffer);
            rahc = new RahcEntry();
            rahc.decode(buffer);
            //System.out.println(this);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * RAHC
     * 4 magic 'RAHC'
     * 4 sectionSize(including the header)
     * 2 Tile Count Y
     * 2 Tile Count X
     * 4 depth (3=4 bit, 4=8 bit)
     * 4 0x00000000
     * 4 Tile Data Size
     * 4 Unknown
     * ----Data
     * NTFT
     */
    public class RahcEntry {
        public String magic;
        public int sectionSize;
        public int tileCountY;
        public int tileCountX;
        public int tileCount;
        public int depth;
        public int flag;
        public int tileDataSize;
        public int unknown;
        public int[][] tiles;
        
        public void decode(ByteBuffer buffer) throws Exception {
            magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
            sectionSize = buffer.getInt();
            tileCountY = buffer.getShort();
            tileCountX = buffer.getShort();
            depth = buffer.getInt();
            buffer.getInt();
            flag = buffer.getInt();
            tileDataSize = buffer.getInt();
            unknown = buffer.getInt();
            tileCount = tileCountY * tileCountX;
            tiles = new int[tileCount][];
            for (int i = 0; i < tileCount; i++) {
                //A tile is a block of 8x8 pixels
                tiles[i] = new int[64];
                for (int j = 0; j < 64; j++) {
                    if (depth == 3) {
                        int bb = buffer.get() & 0xFF;
                        int pixel1 = bb & 0xF;
                        int pixel2 = (bb & 0xF0) >> 4;
                        tiles[i][j] = pixel1;
                        j++;
                        tiles[i][j] = pixel2;
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
