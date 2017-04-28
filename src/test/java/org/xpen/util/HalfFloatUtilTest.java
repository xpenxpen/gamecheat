package org.xpen.util;

import org.junit.Assert;
import org.junit.Test;
import org.xpen.util.HalfFloatUtil;

public class HalfFloatUtilTest {
	
    @Test
    public void testToFloat() {
    	Assert.assertEquals(0f, HalfFloatUtil.toFloat((short)0), 0.01f);
    	Assert.assertEquals(-40480f, HalfFloatUtil.toFloat((short)-1807), 0.01f);
    	Assert.assertEquals(0f, HalfFloatUtil.toFloat((short)2610), 0.01f);
    }

}
