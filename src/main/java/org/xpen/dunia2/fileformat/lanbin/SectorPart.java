package org.xpen.dunia2.fileformat.lanbin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.xpen.dunia2.fileformat.dat.LzoCompressor;

public class SectorPart {
	
	private static int id = 0;
	
	public int unknown;
	public int cs;//compressed size of data
	public int ds; //decompressed size of data
	public byte cmpData[]; //These bytes are lzo compressed (lzo1x)
	
	public void decode(ByteBuffer buffer) {
		unknown = buffer.getInt();
		cs = buffer.getInt();
		ds = buffer.getInt();
		System.out.println("cs="+cs+", ds="+ds);
        
        byte[] b = new byte[cs];
        cmpData = new byte[ds];
        
        buffer.get(b);
        
		LzoCompressor.decompress(b, 0, cs, cmpData, 0, ds);
		
		
		FileOutputStream os;
		try {
			os = new FileOutputStream(new File("E:/aliBoxGames/games/5993/ex/common/languages/english/" + String.valueOf(id++) + ".txt"));
			IOUtils.write(cmpData, os);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
