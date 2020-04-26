package org.xpen.ds.format;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.ColorUtil;

public class Nclr {
    
    private static final Logger LOG = LoggerFactory.getLogger(Nclr.class);
    public static final String FILE_SUFFIX_NCLR = "NCLR";
    
    private byte[] bytes;
    public NclrEntry nclrEntry;
    
    public void handle(byte[] b) throws Exception {
        this.bytes = b;
        decodeDat();
    }
        
    private void decodeDat() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        nclrEntry = new NclrEntry();
        nclrEntry.decode(buffer);
    }
    
    public class NclrEntry {
        public DsGenericHeader genericHeader;
        public TtlpEntry ttlp;
        
        public void decode(ByteBuffer buffer) throws Exception {
            genericHeader = new DsGenericHeader();
            genericHeader.decode(buffer);
            ttlp = new TtlpEntry();
            ttlp.decode(buffer);
            //System.out.println(this);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * TTLP
     * 4 magic 'TTLP'
     * 4 sectionSize(including the header)
     * 4 depth (3=4 bit, 4=8 bit)
     * 4 0x00000000
     * 4 Palette Length
     * 4 Colors Per Palette (Each palette has 16 colors)
     * ----Data
     * Palette Data stored as NTFP
     */
    public class TtlpEntry {
        public String magic;
        public int sectionSize;
        public int depth;
        public int paletteLength;
        public int colorCountPerPalette; //Always 16
        public Color[][] colors;
        
        public void decode(ByteBuffer buffer) throws Exception {
            magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
            sectionSize = buffer.getInt();
            depth = buffer.getInt();
            buffer.getInt();
            paletteLength = buffer.getInt();
            colorCountPerPalette = buffer.getInt();
            //BGR555
            colors = new Color[paletteLength / 2 / colorCountPerPalette][];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new Color[16];
                for (int j = 0; j < 16; j++) {
                    short colorBits = buffer.getShort();
                    Color c = ColorUtil.bgr555ToRgb888(colorBits);
                    colors[i][j] = c;
                }
            }
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
