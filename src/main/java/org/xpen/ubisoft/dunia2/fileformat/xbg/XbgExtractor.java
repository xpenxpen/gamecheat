package org.xpen.ubisoft.dunia2.fileformat.xbg;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.ubisoft.dunia2.fileformat.xbg.chunk.Chunk;
import org.xpen.ubisoft.dunia2.fileformat.xbg.chunk.RootChunk;

public class XbgExtractor {
    public static final int MAGIC_XBG = 0x4D455348; //'MESH'
    
    private static final Logger LOG = LoggerFactory.getLogger(XbgExtractor.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private byte[] bytes;
    private File file;
    public Chunk root;
    
    public XbgExtractor(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public XbgExtractor(File file) throws Exception {
        this.file = file;
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
        this.bytes = bytes;
    }

    public static void main(String[] args) throws Exception {
    	
    	File file = null;
    	
        //file = new File("D:/git/opensource/gamecheat/myex/patch/graphics/__fc3_graphics/sidequests/vehicles/sea/sha_boat_crane01.xbg");
        //file = new File("D:/git/opensource/gamecheat/myex/patch/graphics/__fc3_graphics/sidequests/vehicles/sea/sha_boat_cabin.xbg");
        file = new File("E:/aliBoxGames/games/5993/ex/fc3main/graphics/__fc3_graphics/_common/characters/animals/asian_black_bear/ab_bear_big.xbg");
        //file = new File("E:/aliBoxGames/games/5993/ex/fc3main/graphics/__fc3_graphics/_common/characters/animals/bird/bird.xbg");
        //file = new File("E:/aliBoxGames/games/5993/ex/fc3main/graphics/__fc3_graphics/_common/characters/unique/vaas/vaas.xbg");
        
//        JFileChooser chooser = new JFileChooser();
//        chooser.setCurrentDirectory(new File("E:/aliBoxGames/games/5993/ex"));
//        int result = chooser.showOpenDialog(null);
//        
//		if (result == JFileChooser.APPROVE_OPTION) {
//			file = chooser.getSelectedFile();
//	        
//		}
		
        XbgExtractor xbgExtractor = new XbgExtractor(file);
        xbgExtractor.decode();


    }

    /**
     * XBG File format
     * 4 'MESH'
     * 2 version major
     * 2 version minor
     * 4 unknown
     */
     private void decode() throws Exception {
        if (bytes.length <= 32) {
            throw new RuntimeException("not enough data for mesh header");
        }
        
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int magicMesh = buffer.getInt();
        if (magicMesh != MAGIC_XBG) {
            throw new RuntimeException("magic <> 'MESH'");
        }
        
        short majorVer = buffer.getShort();
        if (majorVer != 52) {
            throw new RuntimeException("majorVer wrong");
        }
        short minorVer = buffer.getShort();
        int unknown08 = buffer.getInt();
        
        RootChunk rootChunk = new RootChunk();
        rootChunk.majorVer = majorVer;
        rootChunk.minorVer = minorVer;
        
        RootChunk chunk = (RootChunk)rootChunk.decodeBlock(buffer, null);
        chunk.toObjFormat(file);
    }


}
