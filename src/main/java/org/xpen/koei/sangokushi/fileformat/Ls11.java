package org.xpen.koei.sangokushi.fileformat;

public class Ls11 {
    private byte[] inBytes;
    private byte[] dicts;
    private int inPos;
    private int outPos;
    private int bitPos;

    public Ls11(byte[] inBytes, byte[] dicts) {
        this.inBytes = inBytes;
        this.dicts = dicts;
    }

    public void decode(byte[] outBytes) {
        inPos = 0;
        outPos = 0;
        bitPos = 7;

        while (inPos < inBytes.length && outPos < outBytes.length) {
            int code = getCode();

            if (code < 256) {
                outBytes[outPos] = dicts[code];
                outPos++;
            } else {
                int off = code - 256;
                int len = getCode() + 3;
                for (int i = 0; i < len; i++) {
                    outBytes[outPos] = outBytes[outPos - off];
                    outPos++;
                }
            }
        }
    }

    private int getCode() {
        // 把若干个数分解后按b1b2顺序依次排列起来就形成一个二进制串，我们可以从头扫描唯一确定一个序列还原这些数。
        // 具体方法为从开头开始数连续的1的个数（设为a），则第一个分解是a+1位，第一个b1为1...10（a个1），再向后取a+1位是b2，将b1b2相加就得到第一个数，依次做下去就可以还原所有的数。
        int code = 0;
        int code2 = 0;
        int a = 0;
        int bit;

        do {
            bit = (inBytes[inPos] >>> bitPos) & 0x01;
            code = (code << 1) | bit;
            a++;
            bitPos--;
            if (bitPos < 0) {
                bitPos = 7;
                inPos++;
            }
        } while (bit == 1);
        
        for (int i = 0; i < a; i++) {
            bit = (inBytes[inPos] >>> bitPos) & 0x01;
            code2 = (code2 << 1) | bit;
            bitPos--;
            if (bitPos < 0) {
                bitPos = 7;
                inPos++;
            }
        }
        code += code2;

        return code;
    }

}
