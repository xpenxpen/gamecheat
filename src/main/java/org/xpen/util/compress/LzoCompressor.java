package org.xpen.util.compress;

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.lzo_uintp;

public class LzoCompressor {
	
	private static LzoDecompressor decompressor;
	
	
	static {
        LzoAlgorithm algorithm = LzoAlgorithm.LZO1X;
        decompressor = LzoLibrary.getInstance().newDecompressor(algorithm, null);

	}
	
	public static void decompress(byte[] inByte, int start1, int inLength, byte[] outByte, int start2, int outLength) {
        lzo_uintp outputBufferLen = new lzo_uintp();
        outputBufferLen.value = outLength;
        decompressor.decompress(inByte, start1, inLength, outByte, start2, outputBufferLen);
	}

}
