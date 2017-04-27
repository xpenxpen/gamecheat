package org.xpen.creation.fileformat.bsa;

import com.github.czietsman.lz4.LZ4Factory;
import com.github.czietsman.lz4.LZ4FastDecompressor;

public class Lz4Compressor {
	
	private static LZ4FastDecompressor decompressor;
	
	
	static {
        decompressor = LZ4Factory.INSTANCE.fastDecompressor();
	}
	
	public static void decompress(byte[] inByte, byte[] outByte) {
        decompressor.decompress(inByte, 0, outByte, 0, outByte.length);
	}

}
