package org.xpen.dunia2.fileformat.lanbin;

import java.nio.ByteBuffer;

public class SectorLine {
	public int id;    //line id
	public int sec;   //same as sectorHash
	public int _enum; //crc32 hash of enum attribute in string
	public int pack; //crc32 Hash of 'Main'

	public void decode(ByteBuffer buffer) {
		id = buffer.getInt();
		sec = buffer.getInt();
		_enum = buffer.getInt();
		pack = buffer.getInt();
	}
}
