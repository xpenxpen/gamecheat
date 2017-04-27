package org.xpen.creation.fileformat.bsa;

import java.util.zip.Inflater;

public class ZlibCompressor {
	
	private static Inflater decompressor;
	
	public static void decompress(byte[] inByte, byte[] outByte) throws Exception {
	    decompressor = new Inflater();
	    decompressor.setInput(inByte, 0, inByte.length);
        decompressor.inflate(outByte);
        decompressor.end();
	}

}
