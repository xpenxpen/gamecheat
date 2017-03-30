package org.xpen.dunia2.fileformat.lanbin;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Sector {
	public int sectorHash;//crc32 of sector name
	public int stringCount;
	public List<SectorLine> lines = new ArrayList<SectorLine>();
	public int cmpPartCount; //Number of lzo compressed parts
	public List<SectorPart> parts = new ArrayList<SectorPart>();
	
	
	public void decode(ByteBuffer buffer) {
		sectorHash = buffer.getInt();
		stringCount = buffer.getInt();
    	for (int i = 0; i < stringCount; i++) {
    		System.out.println("--start lines " + i);
    		SectorLine line = new SectorLine();
    		lines.add(line);
    		line.decode(buffer);
    		System.out.println("--end lines " + i);
    	}
    	
    	cmpPartCount = buffer.getInt();
    	for (int i = 0; i < cmpPartCount; i++) {
    		System.out.println("--start parts " + i);
    		SectorPart part = new SectorPart();
    		parts.add(part);
    		part.decode(buffer);
    		System.out.println("--end parts " + i);
    	}
		
	}
}
