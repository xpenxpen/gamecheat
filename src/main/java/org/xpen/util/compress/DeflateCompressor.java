package org.xpen.util.compress;

import java.util.zip.Inflater;

public class DeflateCompressor {
	
	private static Inflater decompressor;
	
	public static int decompress(byte[] inByte, byte[] outByte) throws Exception {
	    decompressor = new Inflater();
	    decompressor.setInput(inByte, 0, inByte.length);
        int actualSize = decompressor.inflate(outByte);
        decompressor.end();
        return actualSize;
	}

}
