package org.xpen.dunia2.fileformat.fat2;

import java.util.Arrays;
import java.util.zip.CRC32;

import org.junit.Assert;
import org.junit.Test;

public class CRC64Test {

    
    @Test
    public void testCRC() {
        CRC64 crc64 = new CRC64();
        System.out.println(Arrays.toString(crc64.crcTable));
        System.out.println(Arrays.toString(crc64.table));
        
        //long update = crc64.update("aa");
        long update = crc64.update("hidSingleObject");
        Assert.assertEquals(123, update);
        
    }
    @Test
    public void testCRC32() {
        CRC32 crc32 = new CRC32();
        
        crc32.update("hidSingleObject".getBytes());
        Assert.assertEquals(1643932418, crc32.getValue());
        
    }}
