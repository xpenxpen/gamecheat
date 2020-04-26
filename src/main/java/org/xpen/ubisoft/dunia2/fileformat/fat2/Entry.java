package org.xpen.ubisoft.dunia2.fileformat.fat2;

import java.math.BigInteger;

public class Entry {
    //public long nameHash;
    public BigInteger nameHash;
    public int uncompressedSize;
    public int compressedSize;
    public long offset;
    public int compressionScheme;
    
    @Override
    public String toString() {
        return "Entry [nameHash=" + nameHash.longValue() + "(" + nameHash.toString(16) +")"
                + ", uncompressedSize=" + uncompressedSize + ", compressedSize=" + compressedSize
                + ", offset=" + offset + ", compressionScheme=" + compressionScheme + "]";
    }
    
    

}
