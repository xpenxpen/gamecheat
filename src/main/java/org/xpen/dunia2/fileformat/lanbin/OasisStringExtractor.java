package org.xpen.dunia2.fileformat.lanbin;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.xbg.XbgExtractor;

public class OasisStringExtractor {
    public static final int MAGIC_XBG = 0x4D455348; //'MESH'
    
    private static final Logger LOG = LoggerFactory.getLogger(OasisStringExtractor.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private byte[] bytes;
	public List<Sector> sectors = new ArrayList<Sector>();
    
    public OasisStringExtractor(byte[] bytes) {
        this.bytes = bytes;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("E:/aliBoxGames/games/5993/ex/common/languages/english/oasisstrings_compressed.bin");
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
        OasisStringExtractor oasisStringExtractor = new OasisStringExtractor(bytes);
        oasisStringExtractor.decode();

    }

	/**
	 * 
int stringTableCount;
struct stringTable{    
    int unknown;
    int sectorCount;
    struct section{
        int sectorHash;//crc32 of sector name
        int stringCount;
        struct{
            int id;    //line id
            int sec;   //same as sectorHash
            int _enum; //crc32 hash of enum attribute in string
            int pack; //crc32 Hash of 'Main'    
        }lines[stringCount]<optimize=false>;
        int cmpPartCount; //Number of lzo compressed parts
        struct{
            int unknown;
            int cs;//compressed size of data
            int ds; //decompressed size of data
            ubyte cmpData[cs]; //These bytes are lzo compressed (lzo1x)
       }parts[cmpPartCount]<optimize=false>;   
    }sectors[sectorCount]<optimize=false>;
}all[stringTableCount]<optimize=false>;

	 */
    private void decode() {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int stringTableCount = buffer.getInt();
        for (int i = 0; i < stringTableCount; i++) {
        	int unknown1 = buffer.getInt();
        	int sectorCount = buffer.getInt();
        	for (int j = 0; j < sectorCount; j++) {
        		System.out.println("--start sectorCount " + j);
        		Sector sector = new Sector();
        		sectors.add(sector);
        		sector.decode(buffer);
        		System.out.println("--end   sectorCount " + j);
        		
        	}
        }
		
	}

}
