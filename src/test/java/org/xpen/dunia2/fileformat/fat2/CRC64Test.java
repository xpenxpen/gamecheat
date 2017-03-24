package org.xpen.dunia2.fileformat.fat2;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class CRC64Test {

    
    @Test
    public void testCRC() {
        CRC64 crc64 = new CRC64();
        System.out.println(Arrays.toString(crc64.crcTable));
        System.out.println(Arrays.toString(crc64.table));
        
        long update = crc64.update("aa");
        Assert.assertEquals(123, update);
        
    }
}
