package org.xpen.kingsoft.fileformat;

import org.junit.Test;


public class PakFileTest {
    @Test
    public void testHash() {
    	PakFile pakFile = new PakFile();
    	int nameHash = pakFile.nameHash("中");
    	System.out.println(Integer.toHexString(nameHash));
    }

}
