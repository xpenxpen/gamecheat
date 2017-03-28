package org.xpen.dunia2.fileformat.fat2;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class EntrySerializerV9 implements EntrySerializer {

    @Override
    public Entry deserialize(FileChannel fileChannel) throws Exception {
        //20 byte
        // hhhhhhhh hhhhhhhh hhhhhhhh hhhhhhhh
        // hhhhhhhh hhhhhhhh hhhhhhhh hhhhhhhh
        // uuuuuuuu uuuuuuuu uuuuuuuu uuuuuuss
        // oooooooo oooooooo oooooooo oooooooo
        // oocccccc cccccccc cccccccc cccccccc

        // [h] hash = 64 bits (8 byte) CRC64/CRC32
        // [u] uncompressed size = 30 bits
        // [s] compression scheme = 2 bits
        // [o] offset = 34 bits
        // [c] compressed size = 30 bits
        
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        fileChannel.read(buffer);
        buffer.flip();
        
        int a = buffer.getInt();
        int b = buffer.getInt();
        int c = buffer.getInt();
        int d = buffer.getInt();
        int e = buffer.getInt();
        
        Entry entry = new Entry();
        
//        long tempa = a;
//        if (a < 0) {
//        	tempa = tempa + Long.MAX_VALUE + 1 ;
//        }
//        long tempb = b;
//        if (b < 0) {
//        	tempb = tempb + Long.MAX_VALUE + 1 ;
//        }
        
        String aa = Integer.toHexString(a);
        String bb = Integer.toHexString(b);
        
        //BigInteger hash = BigInteger.valueOf(a);
        BigInteger hash = new BigInteger(aa+bb, 16);
        //hash = hash.shiftLeft(32);
        //hash = hash.add(BigInteger.valueOf(tempb));

        entry.nameHash = hash;
        entry.uncompressedSize = (c & 0xFFFFFFFC) >>> 2;
        entry.compressionScheme = c & 0x00000003;
        String dd = Integer.toHexString(d);
        BigInteger bi = new BigInteger(dd, 16);
        bi = bi.shiftLeft(2);
        int ee = (e & 0xC0000000) >>> 30;
        bi = bi.add(BigInteger.valueOf(ee));
        //entry.offset = d << 2;
        //entry.offset |= ((e & 0xC0000000) >>> 30);
        entry.offset = bi.longValueExact();
        entry.compressedSize = e & 0x3FFFFFFF;
        return entry;
    }

}
