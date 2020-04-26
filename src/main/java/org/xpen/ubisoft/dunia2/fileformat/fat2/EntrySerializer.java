package org.xpen.ubisoft.dunia2.fileformat.fat2;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public interface EntrySerializer {
    
    Entry deserialize(FileChannel fileChannel) throws Exception;
}
