package org.xpen.creation.fileformat.bsa;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

public class Lz4Compressor {
	
	private static LZ4SafeDecompressor decompressor;
	
	
	static {
        decompressor = LZ4Factory.nativeInstance().safeDecompressor();
	}
	
	public static void decompress(byte[] inByte, byte[] outByte) {
        decompressor.decompress(inByte, outByte);
	}

}
