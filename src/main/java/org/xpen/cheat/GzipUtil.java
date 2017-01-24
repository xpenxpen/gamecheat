package org.xpen.cheat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {
    
    public static byte[] fromByteToGByte(byte[] bytes) {
        ByteArrayOutputStream baos = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            baos = new ByteArrayOutputStream();
            GZIPOutputStream gzos = new GZIPOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len;
            while((len = bais.read(buffer)) >= 0) {
                gzos.write(buffer, 0, len);
            }
            gzos.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return(baos.toByteArray());
    }
    
    public static byte[] fromGByteToByte(byte[] gbytes) {
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(gbytes);
        try {
            baos = new ByteArrayOutputStream();
            GZIPInputStream gzis = new GZIPInputStream(bais);
            byte[] bytes = new byte[1024];
            int len;
            while((len = gzis.read(bytes)) > 0) {
                baos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return(baos.toByteArray());
    }
    
    public static byte[] decompressGzipFile(String gzipFile) {
        ByteArrayOutputStream baos = null;
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            //close resources
            baos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return(baos.toByteArray());
    }

    public static void compressGzipFile(byte[] bytes, String gzipFile) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=bais.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            bais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void main(String[] args) {
        byte[] gzByte = fromByteToGByte(new byte[]{0x01, 0x02});
        System.out.println(Arrays.toString(gzByte));
        byte[] plainByte = fromGByteToByte(gzByte);
        System.out.println(Arrays.toString(plainByte));
        
        compressGzipFile(new byte[]{0x01, 0x02}, "D:/gzip_test1.gz");
        plainByte = decompressGzipFile("D:/gzip_test1.gz");
        System.out.println(Arrays.toString(plainByte));
    }
    
}
