package org.xpen.ds.format;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ds.format.Ncgr.NcgrEntry;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;
import org.xpen.util.handler.FileTypeHandler;

public class Ncer implements FileTypeHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(Ncer.class);
    private static final Dimension[][] OAM_SIZE_TABLE = {
        {
            //Square
            new Dimension(8, 8),
            new Dimension(16, 16),
            new Dimension(32, 32),
            new Dimension(64, 64)
        }, {
            //Horizontal
            new Dimension(16, 8),
            new Dimension(32, 8),
            new Dimension(32, 16),
            new Dimension(64, 32)
        }, {
            //Vertical
            new Dimension(8, 16),
            new Dimension(8, 32),
            new Dimension(16, 32),
            new Dimension(32, 64)
        }
        
    };
    
    protected String subFolderName;
    protected String fileName;
    
    protected Nclr nclr;
    protected Ncbr ncbr;
    public NcerEntry ncerEntry;
    private byte[] bytes;
    
    @Override
    public void handle(byte[] b, String datFileName, String newFileName, boolean isUnknown) throws Exception {
        this.subFolderName = datFileName;
        this.fileName = newFileName;
        this.bytes = b;
        decodeDat();
    }
        
    public void setNclr(Nclr nclr) {
        this.nclr = nclr;
    }

    public void setNcbr(Ncbr ncbr) {
        this.ncbr = ncbr;
    }

    private void decodeDat() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        ncerEntry = new NcerEntry();
        ncerEntry.decode(buffer);
        
        
        for (int i = 0; i < ncerEntry.cebk.banks.length; i++) {
            BankEntry bank = ncerEntry.cebk.banks[i];
            if (bank.oamCount == 0) {
                continue;
            }
            //BufferedImage bi = Ntfs.getPixel(buffer, ncerEntry.nrcs.width, ncerEntry.nrcs.height, ncgr.ncgrEntry.rahc.tiles, nclr.nclrEntry.ttlp.colors);
            BufferedImage bi = drawImage(bank, nclr.nclrEntry.ttlp.colors, ncbr.ncgrEntry);
            writeFile(bi, i);
        }
        
        
    }

    //由多个oam拼图而成
    private BufferedImage drawImage(BankEntry bank, Color[][] colors, NcgrEntry ncgrEntry) {
        //recalculate width, height
        int minX = Arrays.asList(bank.oams).stream().min((t1, t2) -> t1.xOffset - t2.xOffset).get().xOffset;
        int minY = Arrays.asList(bank.oams).stream().min((t1, t2) -> t1.yOffset - t2.yOffset).get().yOffset;
        OamEntry maxEntryX = Arrays.asList(bank.oams).stream().max((t1, t2) -> t1.xOffset + t1.width - t2.xOffset - t2.width).get();
        OamEntry maxEntryY = Arrays.asList(bank.oams).stream().max((t1, t2) -> t1.yOffset + t1.height - t2.yOffset - t2.height).get();
        bank.xMin = minX;
        bank.yMin = minY;
        bank.xMax = maxEntryX.xOffset + maxEntryX.width;
        bank.yMax = maxEntryY.yOffset + maxEntryY.height;
        
        int width = bank.xMax - bank.xMin + 1;
        int height = bank.yMax - bank.yMin + 1;
        
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (OamEntry oam: bank.oams) {
            int startX = oam.xOffset - bank.xMin;
            int startY = oam.yOffset - bank.yMin;
            //System.out.println(startX + " " + startY);
            int xTileCount = oam.width / 8;
            int yTileCount = oam.height / 8;
            Color[] pickPalette = colors[oam.indexPalette];
            
            int[] tile = null;
            
            int m = (oam.tileOffset << bank.cebk.blockShift) * 64;
            //System.out.println(oam);
            //System.out.println("m=" + m);
            
            if (ncgrEntry.rahc.linearFlag == 1) {
                tile = ncgrEntry.rahc.tiles[0];
                for (int i = 0; i < oam.height; i++) {
                    for (int j = 0; j < oam.width; j++) {
                        int tileIndex = m + i * oam.width + j;
                        if (tile[tileIndex] > pickPalette.length) {
                            LOG.debug("Warning: tile out of index " + tile[tileIndex]);
                            tile[tileIndex] = tile[tileIndex] % colors.length;
                        }
                        bi.setRGB(startX + j, startY + i, pickPalette[tile[tileIndex]].getRGB());
                    }
                }
            } else {
                for (int y = 0; y < yTileCount; y++) {
                    for (int x = 0; x < xTileCount; x++) {
                        tile = ncgrEntry.rahc.tiles[m / 64 + y * xTileCount + x];
                        for (int k = 0; k < 64; k++) {
                            if (tile[k] > pickPalette.length) {
                                LOG.debug("Warning: tile out of index " + tile[k]);
                                tile[k] = tile[k] % colors.length;
                            }
                            bi.setRGB(startX + x * 8 + k % 8, startY + y * 8 + k / 8, pickPalette[tile[k]].getRGB());
                        }
                    }
                }
            }
            
            
        }
        return bi;
    }

    private void writeFile(BufferedImage bi, int index) throws Exception {
        File outFile = null;
        String oldFileNameWithoutExt = fileName + "_" + index;
        File parent = new File(UserSetting.rootOutputFolder, subFolderName);
        outFile = new File(parent, oldFileNameWithoutExt + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }
    
    public class NcerEntry {
        public DsGenericHeader genericHeader;
        public CebkEntry cebk;
        
        public void decode(ByteBuffer buffer) throws Exception {
            genericHeader = new DsGenericHeader();
            genericHeader.decode(buffer);
            cebk = new CebkEntry();
            cebk.decode(buffer);
            //System.out.println(this);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * CEBK
     * 4 magic 'CEBK'
     * 4 sectionSize(including the header)
     * 2 Bank count
     * 2 Bank type
     * 4 Unknown (always 0x18)
     * 4 Block shift
     * 4 Unknown (0?)
     * 8 Unknown (0?)
     * ----Data
     * Bank[]
     */
    public class CebkEntry {
        public String magic;
        public int sectionSize;
        public int bankCount;
        public int bankType;
        public int unknown1;
        public int blockShift;
        public int bankStartOffset;
        public BankEntry[] banks;
        
        public void decode(ByteBuffer buffer) throws Exception {
            magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
            sectionSize = buffer.getInt();
            bankCount = buffer.getShort();
            bankType = buffer.getShort();
            unknown1 = buffer.getInt();
            blockShift = buffer.getInt();
            buffer.getInt();
            buffer.getLong();
            banks = new BankEntry[bankCount];
            
            for (int i = 0; i < banks.length; i++) {
                banks[i] = new BankEntry();
                banks[i].cebk = this;
            }
            for (BankEntry bank : banks) {
                bank.decode(buffer);
            }
            bankStartOffset = buffer.position();
            for (BankEntry bank : banks) {
                bank.decodeOam(buffer);
            }
            //for (BankEntry bank : banks) {
            //    System.out.println(bank);
            //}
            
            
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * Bank
     * 每个Bank为一张图片,由多个oam碎块拼图而成
     * 2 OAM Count
     * 2 Unknown
     * 4 OAM Offset
     * ----If bankType==1
     * 2 xMax
     * 2 yMax
     * 2 xMin
     * 2 yMin
     * ----Data
     * Oam[]
     */
    public class BankEntry {
        public int oamCount;
        public int oamOffset;
        public int xMax;
        public int yMax;
        public int xMin;
        public int yMin;
        public OamEntry[] oams;
        public CebkEntry cebk;
        
        public void decode(ByteBuffer buffer) throws Exception {
            oamCount = buffer.getShort();
            buffer.getShort();
            oamOffset = buffer.getInt();
            if (cebk.bankType == 1) {
                xMax = buffer.getShort();
                yMax = buffer.getShort();
                xMin = buffer.getShort();
                yMin = buffer.getShort();
            }
            
        }
        
        public void decodeOam(ByteBuffer buffer) throws Exception {
            if (oamCount == 0) {
                return;
            }
            oams = new OamEntry[oamCount];
            for (int i = 0; i < oams.length; i++) {
                oams[i] = new OamEntry();
            }
            buffer.position(cebk.bankStartOffset + oamOffset);
            for (OamEntry oam : oams) {
                oam.decode(buffer);
            }
            
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * Oam
     * 2 att1
     * 2 att2
     * 2 att3
     */
    public class OamEntry {
        public int att1;
        public int att2;
        public int att3;
        public int width;
        public int height;
        
        //----------att1---------------
        //SS D M OO P R YYYYYYYY
        
        public byte yOffset;       // Bit0-7
        public byte rsFlag;       // Bit8 -> Rotation / Scale flag
        public byte objDisable;   // Bit9 -> if r/s == 0
        public byte doubleSize;   // Bit9 -> if r/s != 0
        public byte objMode;      // Bit10-11 -> 0 = normal; 1 = semi-trans; 2 = window; 3 = invalid
        public byte mosaicFlag;   // Bit12 
        public byte depth;        // Bit13 -> 0 = 4bit; 1 = 8bit
        public byte shape;        // Bit14-15 -> 0 = square; 1 = horizontal; 2 = vertial; 3 = invalid
        
        
        //----------att2---------------
        //SS PPPPP XXXXXXXXX
        
        public int xOffset;   // Bit0-8 (if >= 0x100, must subtract 0x200)

        // If R/S == 0
        public byte unused; // Bit9-11
        public byte flipX;  // Bit12
        public byte flipY;  // Bit13
        // If R/S != 0
        public byte rsParam;   //Bit9-13 -> rotation or scale parameters

        public byte size;   // Bit14-15
        
        //----------att3---------------
        //IIII PP TTTTTTTTTT
        
        public int tileOffset;     // Bit0-9
        public byte priority;      // Bit10-11 (0 high priority)
        public byte indexPalette;  // Bit12-15
        
        public void decode(ByteBuffer buffer) throws Exception {
            att1 = buffer.getShort() & 0xFFFF;
            att2 = buffer.getShort() & 0xFFFF;
            att3 = buffer.getShort() & 0xFFFF;
            

            //----------att1---------------
            yOffset = (byte)(att1 & 0xFF);
            rsFlag = (byte)((att1 >> 8) & 1);
            if (rsFlag == 0) {
                objDisable = (byte)((att1 >> 9) & 1);
            } else {
                doubleSize = (byte)((att1 >> 9) & 1);
            }
            objMode = (byte)((att1 >> 10) & 3);
            mosaicFlag = (byte)((att1 >> 12) & 1);
            depth = (byte)((att1 >> 13) & 1);
            shape = (byte)((att1 >> 14) & 3);

            //att2
            xOffset = att2 & 0x01FF;
            if (xOffset >= 0x100) {
                xOffset -= 0x200;
            }
            if (rsFlag == 0) {
                unused = (byte)((att2 >> 9) & 7);
                flipX = (byte)((att2 >> 12) & 1);
                flipY = (byte)((att2 >> 13) & 1);
            } else {
                rsParam = (byte)((att2 >> 9) & 0x1F);
            }
            size = (byte)((att2 >> 14) & 3);

            //att3
            tileOffset = att3 & 0x03FF;
            priority = (byte)((att3 >> 10) & 3);
            indexPalette = (byte)((att3 >> 12) & 0xF);

            Dimension dimension = getOamSize(shape, size);
            width = dimension.width;
            height = dimension.height;
            
            //System.out.println(this);
        }
        
        private Dimension getOamSize(byte shape, byte size) {
            return OAM_SIZE_TABLE[shape][size];
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
