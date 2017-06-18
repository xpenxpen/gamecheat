package org.xpen.util.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;

public class LzmaCompressor {
	
	public static byte[] decompress(byte[] inByte) throws IOException {
		InputStream in = new ByteArrayInputStream(inByte);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(in, 2048);
        final byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = lzmaIn.read(buffer))) {
        	baos.write(buffer, 0, n);
        }
        baos.close();
        lzmaIn.close();
        return baos.toByteArray();
	}

}
