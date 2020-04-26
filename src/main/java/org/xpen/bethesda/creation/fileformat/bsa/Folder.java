package org.xpen.bethesda.creation.fileformat.bsa;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Folder {
    public long hash;
    public int folderFileCount;
    public int offset;
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
