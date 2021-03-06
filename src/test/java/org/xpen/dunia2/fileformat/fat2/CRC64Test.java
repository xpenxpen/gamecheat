package org.xpen.dunia2.fileformat.fat2;

import java.util.Arrays;
import java.util.zip.CRC32;

import org.junit.Assert;
import org.junit.Test;
import org.xpen.ubisoft.dunia2.fileformat.fat2.CRC64;

public class CRC64Test {

    
    @Test
    public void testCRC() {
        CRC64 crc64 = new CRC64();
        System.out.println(Arrays.toString(crc64.crcTable));
        System.out.println(Arrays.toString(crc64.table));
        
        long update = crc64.update("domino\\library\\coop\\helpers.carriablereset.debug.lua");
        Assert.assertEquals(-1644077453726826717L, update);
        
    }
    @Test
    public void testCRC32() {
        CRC32 crc32 = new CRC32();
        
        crc32.update("hidSingleObject".getBytes());
        Assert.assertEquals("61fc6b02", Long.toHexString(crc32.getValue()));
        
    }}
