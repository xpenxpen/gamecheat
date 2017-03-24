package org.xpen.dunia2.fileformat.fat2;

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
        
        long a = buffer.getLong();
        int c = buffer.getInt();
        int d = buffer.getInt();
        int e = buffer.getInt();
        
        Entry entry = new Entry();

        entry.nameHash = a;
        entry.uncompressedSize = (c & 0xFFFFFFFC) >> 2;
        entry.compressionScheme = c & 0x00000003;
        entry.offset = d << 2;
        entry.offset |= ((e & 0xC0000000) >> 30);
        entry.compressedSize = e & 0x3FFFFFFF;
        return entry;
    }

}
