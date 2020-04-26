package org.xpen.capcom.aceattorney;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Hex;

public class Aa {
    public static void main(String[] args) throws Exception {
        String aa = "検事局";
        byte[] bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        char[] encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "１ヶ月";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "留守間";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "刑事";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "Ｇ−３９０機内　１Ｆ";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "悪夢見気";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "６時１３分";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "１０分気絶";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "地震";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "痛";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "落物届";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "理由分";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        aa = "巻";
        bb = aa.getBytes(Charset.forName("SHIFT_JIS"));
        encodeHex = Hex.encodeHex(bb);
        System.out.println(encodeHex);
        //（  に  も   ど  っ   て   く  る   の  も   、
        //09 2A 38 28 23 25 15 40 2C 38 01
        
        //FF 05 10 FE 81 40 ?
        //换行
        
        //ぶ  り   と   い   っ  た  と   こ  ろ   か  ）
        //31 3F 27 0E 23 20 27 18 42 12 0A
        
        
        //(  の    こ  と  は   、 イ ト ノ コ ギ リ
        //09 2C 18 27 2D 01 
        
        
        //FF 05 10 57 70 75
        
        //62 5E 4F FE 81 40
        
        //4E 58 54 65
        //ラ  ウ   ン   ジ
        
        //02->....
        
        //27C
        //FC 地震
        //1DC サイフは
        
        //(确认)FF 03 00 00  ->等待按键
        //FF 03 00 09  ->等待按键
        //FF 05 0C FE? 换行？
        //(确认)FF 05 08 停顿？
        //FE->换行
    }

}
