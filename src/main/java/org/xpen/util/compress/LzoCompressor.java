package org.xpen.util.compress;

import java.io.IOException;
import java.util.List;

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.lzo_uintp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LzoCompressor {
	
    private static final int SIZE_64K = 64 * 1024;
	private static LzoDecompressor decompressor;
	
    private static final Logger LOG = LoggerFactory.getLogger(LzoCompressor.class);
	
	static {
        LzoAlgorithm algorithm = LzoAlgorithm.LZO1X;
        decompressor = LzoLibrary.getInstance().newDecompressor(algorithm, null);

	}
	
	public static void decompress(byte[] inByte, int start1, int inLength, byte[] outByte, int start2, int outLength) {
        lzo_uintp outputBufferLen = new lzo_uintp();
        outputBufferLen.value = outLength;
        decompressor.decompress(inByte, start1, inLength, outByte, start2, outputBufferLen);
	}

    /**
     * Decompress LZO more than 64K
     * Loop decompress, each block is 64K
     */
	public static void decompress(byte[] inByte, int start1, int inLength, byte[] outByte, int start2, int outLength, List<Integer> lzoCompressSizes) throws IOException {
        int turn = outLength / SIZE_64K;
        if (outLength % SIZE_64K != 0) {
            turn++;
        }
        if (turn != lzoCompressSizes.size()) {
            LOG.error("turn != lzoCompressSizes.size(), turn={}, lzoCompressSizes.size()={}", turn, lzoCompressSizes.size());
            throw new RuntimeException("turn != lzoCompressSizes.size(), turn="+turn+", lzoCompressSizes.size()=" +lzoCompressSizes.size());
        }
        
        int compressedStart = 0;
        int uncompressedStart = 0;
        for (int i = 0; i < turn; i++) {
            int compressedSize = lzoCompressSizes.get(i);
            int uncompressedSize = 0;
            lzo_uintp outputBufferLen = new lzo_uintp();
            if (i < turn - 1) {
                uncompressedSize = SIZE_64K;
            } else {
                uncompressedSize = outLength % SIZE_64K;
            }
            outputBufferLen.value = uncompressedSize;
            decompressor.decompress(inByte, compressedStart, compressedSize, outByte, uncompressedStart, outputBufferLen);
            compressedStart += compressedSize;
            uncompressedStart += uncompressedSize;
        }
        
    }

}
