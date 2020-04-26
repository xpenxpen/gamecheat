package org.xpen.ds.format;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.dat.FileTypeHandler;
import org.xpen.util.ByteBufferUtil;
import org.xpen.util.UserSetting;

public class Nscr implements FileTypeHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(Nscr.class);
    public static final String FILE_SUFFIX_NSCR = "NSCR";
    
    protected String subFolderName;
    protected String fileName;
    
    protected Nclr nclr;
    protected Ncgr ncgr;
    public NscrEntry nscrEntry;
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

    public void setNcgr(Ncgr ncgr) {
        this.ncgr = ncgr;
    }

    private void decodeDat() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        nscrEntry = new NscrEntry();
        nscrEntry.decode(buffer);
        
        BufferedImage bi = Ntfs.getPixel(buffer, nscrEntry.nrcs.width, nscrEntry.nrcs.height, ncgr.ncgrEntry.rahc.tiles, nclr.nclrEntry.ttlp.colors);
        writeFile(bi);
    }

    private void writeFile(BufferedImage bi) throws Exception {
        File outFile = null;
        String oldFileNameWithoutExt = fileName;
        File parent = new File(UserSetting.rootOutputFolder, subFolderName);
        outFile = new File(parent, oldFileNameWithoutExt + ".png");
        
        File parentFile = outFile.getParentFile();
        parentFile.mkdirs();
        
        ImageIO.write(bi, "PNG", outFile);
    }
    
    public class NscrEntry {
        public DsGenericHeader genericHeader;
        public NrcsEntry nrcs;
        
        public void decode(ByteBuffer buffer) throws Exception {
            genericHeader = new DsGenericHeader();
            genericHeader.decode(buffer);
            nrcs = new NrcsEntry();
            nrcs.decode(buffer);
            //System.out.println(this);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * NRCS
     * 4 magic 'NRCS'
     * 4 sectionSize(including the header)
     * 2 width
     * 2 height
     * 4 0x00000000
     * 4 Data Size
     * ----Data
     * NTFS
     */
    public class NrcsEntry {
        public String magic;
        public int sectionSize;
        public int width;
        public int height;
        public int dataSize;
        
        public void decode(ByteBuffer buffer) throws Exception {
            magic = ByteBufferUtil.getFixedLengthString(buffer, 4);
            sectionSize = buffer.getInt();
            width = buffer.getShort();
            height = buffer.getShort();
            buffer.getInt();
            dataSize = buffer.getInt();
            
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

}
