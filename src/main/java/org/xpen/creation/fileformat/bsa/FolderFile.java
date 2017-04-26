package org.xpen.creation.fileformat.bsa;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class FolderFile {
    public long hash;
    public int fileSize;
    public int offset;
    public boolean compressed;
    public String folderPath;
    public String fileName;
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
