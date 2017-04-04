package org.xpen.dunia2.fileformat.xbg;

import org.junit.Assert;
import org.junit.Test;

public class HalfFloatUtilTest {
	
    @Test
    public void testToFloat() {
    	Assert.assertEquals(0f, HalfFloatUtil.toFloat(0), 0.01f);
    	Assert.assertEquals(-40480f, HalfFloatUtil.toFloat(-1807), 0.01f);
    	Assert.assertEquals(0f, HalfFloatUtil.toFloat(2610), 0.01f);
    }

}
