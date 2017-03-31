package org.xpen.dunia2.fileformat.xbg;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.xbg.chunk.Chunk;
import org.xpen.dunia2.fileformat.xbg.chunk.ChunkFactory;
import org.xpen.dunia2.fileformat.xbg.chunk.ChunkType;
import org.xpen.dunia2.fileformat.xbg.chunk.RootChunk;

public class XbgExtractor {
    public static final int MAGIC_XBG = 0x4D455348; //'MESH'
    
    private static final Logger LOG = LoggerFactory.getLogger(XbgExtractor.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private byte[] bytes;
    public Chunk root;
    
    public XbgExtractor(byte[] bytes) {
        this.bytes = bytes;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("D:/git/opensource/gamecheat/myex/patch/graphics/__fc3_graphics/sidequests/vehicles/sea/sha_boat_crane01.xbg");
        //File file = new File("D:/git/opensource/gamecheat/myex/patch/graphics/__fc3_graphics/sidequests/vehicles/sea/sha_boat_cabin.xbg");
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
        XbgExtractor xbgExtractor = new XbgExtractor(bytes);
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
        
        char majorVer = buffer.getChar();
        if (majorVer != 52) {
            throw new RuntimeException("majorVer wrong");
        }
        char minorVer = buffer.getChar();
        int unknown08 = buffer.getInt();
        
        RootChunk rootChunk = new RootChunk();
        
        Chunk chunk = rootChunk.decodeBlock(buffer, null);
    }


}
