package org.xpen.util;


public final class XxTea {

    private static final int DELTA = 0x9E3779B9;

    private static int MX(int sum, int y, int z, int p, int e, int[] k) {
        return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
    }

    private XxTea() {}

    public static final byte[] encrypt(byte[] data, byte[] key, boolean handleMul4) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
                encrypt(toIntArray(data, false, handleMul4), toIntArray(fixKey(key), false, handleMul4)), false);
    }
    public static final byte[] decrypt(byte[] data, byte[] key, boolean handleMul4) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
                decrypt(toIntArray(data, false, handleMul4), toIntArray(fixKey(key), false, handleMul4)), false);
    }


    private static int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        int p, q = 6 + 52 / (n + 1);
        int z = v[n], y, sum = 0, e;

        while (q-- > 0) {
            sum = sum + DELTA;
            e = sum >>> 2 & 3;
            for (p = 0; p < n; p++) {
                y = v[p + 1];
                z = v[p] += MX(sum, y, z, p, e, k);
            }
            y = v[0];
            z = v[n] += MX(sum, y, z, p, e, k);
        }
        return v;
    }

    private static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        int p, q = 6 + 52 / (n + 1);
        int z, y = v[0], sum = q * DELTA, e;

        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = v[p - 1];
                y = v[p] -= MX(sum, y, z, p, e, k);
            }
            z = v[n];
            y = v[0] -= MX(sum, y, z, p, e, k);
            sum = sum - DELTA;
        }
        return v;
    }

    private static byte[] fixKey(byte[] key) {
        if (key.length == 16) return key;
        byte[] fixedkey = new byte[16];
        if (key.length < 16) {
            System.arraycopy(key, 0, fixedkey, 0, key.length);
        }
        else {
            System.arraycopy(key, 0, fixedkey, 0, 16);
        }
        return fixedkey;
    }

    private static int[] toIntArray(byte[] data, boolean includeLength, boolean handleMul4) {
    	int n;
    	//XXTEA can only decrypt data size which is multiple of 4.
    	//If not multiple of 4. Here is the workaround.
    	if (handleMul4) {
    		//pad 0 at tail
            n = (((data.length & 3) == 0)
                    ? (data.length >>> 2)
                    : ((data.length >>> 2) + 1));
    	} else {
    		//truncate tail
    		n = (data.length >>> 2);
    	}
        int[] result;

        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        }
        else {
            result = new int[n];
        }
        n = data.length;
        for (int i = 0; i < n; ++i) {
        	if (i / 4 >= result.length) {
        		break;
        	}
            result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
        }
        return result;
    }

    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;

        if (includeLength) {
            int m = data[data.length - 1];
            n -= 4;
            if ((m < n - 3) || (m > n)) {
                return null;
            }
            n = m;
        }
        byte[] result = new byte[n];

        for (int i = 0; i < n; ++i) {
            result[i] = (byte) (data[i >>> 2] >>> ((i & 3) << 3));
        }
        return result;
    }
}
