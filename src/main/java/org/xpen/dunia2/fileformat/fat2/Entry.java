package org.xpen.dunia2.fileformat.fat2;

public class Entry {
    public long nameHash;
    public int uncompressedSize;
    public int compressedSize;
    public long offset;
    public int compressionScheme;
    
    @Override
    public String toString() {
        return "Entry [nameHash=" + nameHash + "(" + Long.toHexString(nameHash)+")"
                + ", uncompressedSize=" + uncompressedSize + ", compressedSize=" + compressedSize
                + ", offset=" + offset + ", compressionScheme=" + compressionScheme + "]";
    }
    
    

}
