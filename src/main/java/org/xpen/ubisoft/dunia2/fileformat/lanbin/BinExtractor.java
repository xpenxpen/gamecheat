package org.xpen.ubisoft.dunia2.fileformat.lanbin;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class BinExtractor {
    
    private static final Logger LOG = LoggerFactory.getLogger(BinExtractor.class);
    
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private byte[] bytes;
	public List<Sector> sectors = new ArrayList<Sector>();
    private OasisStringExtractor oasisStringExtractor;
    
    public BinExtractor(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public BinExtractor(byte[] bytes, OasisStringExtractor oasisStringExtractor) {
        this.bytes = bytes;
        this.oasisStringExtractor = oasisStringExtractor;
    }

    public static void main(String[] args) throws Exception {
        //File file = new File("E:/aliBoxGames/games/5993/ex/common/languages/english/oasisstrings_compressed.bin");
        File file = new File("D:/git/opensource/dunia2/fc3dat/myex/common/languages/english/2.txt");
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
        BinExtractor binExtractor = new BinExtractor(bytes);
        binExtractor.decode();

    }

    /**
     * ----4 entry Count
     * |
     * | 4 LOOP unknown
     * |
     * ----
     * ----
     * |
     * | 4 LOOP string offset
     * |
     * ----
     * ----
     * |
     * | 4 LOOP id
     * | NULL terminated Unicode String      
     * |
     * ----
     * 
     */
    public void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int count = buffer.getInt();
        //System.out.println("count="+count);
        int[] offsets = new int[count];
        for (int i = 0; i < count; i++) {
            buffer.getInt(); //enum hash?
        }
        for (int i = 0; i < count; i++) {
            offsets[i] = buffer.getInt();
        }
        
        for (int i = 0; i < count; i++) {
            int startPos = buffer.position();
            int id = buffer.getInt();
            byte[] bytes2 = new byte[2];
            bytes2[1] = buffer.get();
            bytes2[0] = buffer.get();
            StringBuilder sb = new StringBuilder();
            
            while (bytes2[0]!=0 || bytes2[1]!=0) {
                if (bytes2[1]==0xA && bytes2[0]==0) { //omit carriage return
                } else {
                    sb.append(new String(bytes2, Charset.forName("Unicode")));
                }
                bytes2[1] = buffer.get();
                bytes2[0] = buffer.get();
            }
            if (oasisStringExtractor==null || oasisStringExtractor.printConsole) {
                //System.out.println(Integer.toHexString(bytes2[0]) + "-" + Integer.toHexString(bytes2[1]));
                System.out.println(Integer.toHexString(startPos)+"--"+Integer.toHexString(buffer.position()) +":"+ id+":"+sb.toString());
            } else {
              replaceDom(id, sb);
            }
            startPos = buffer.position();
        }
        
        
        if (buffer.remaining() != 0) {
            throw new RuntimeException("buffer.remaining=" + buffer.remaining());
        }
        

		
	}

    private void replaceDom(int id, StringBuilder sb) throws XPathExpressionException {
        Node node = oasisStringExtractor.languageMap.get(id);
        if (node!=null) {
            node.setNodeValue(sb.toString());
        }
    }

}
