package org.xpen.pal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;


public class TswTest {
	
    protected RandomAccessFile raf;
    protected FileChannel fileChannel;
    public Color[] colors;
    int width;
    int height;
    BufferedImage bi;
    private List<FatEntry> fatEntries = new ArrayList<FatEntry>();

    
    @Test
    public void testTsw() throws Exception {
        //String paleteFile = "G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2/myex/FDOTHER/000.lll";
        //InputStream isPalete = new FileInputStream(paleteFile);
        String file = "F:/game/pal1new/run/新仙剑奇侠传/myex/All_Map1/13058阿香";
        InputStream isDato = new FileInputStream(file);
        raf = new RandomAccessFile(new File(file), "r");
        fileChannel = raf.getChannel();
        
        getFat();
        getDat();
        //getPallete(isPalete);
        //test8080(isDato);
        //getPixel();
        close();
        
//        JFrame jFrame = new JFrame();
//        jFrame.setSize(800, 600);
//        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jFrame.add(new PanelImageDisplayer());
//        jFrame.setVisible(true);
    }

	private void getDat() throws Exception {
        for (int i = 0; i < fatEntries.size(); i++) {
            FatEntry fatEntry = fatEntries.get(i);  
            
            byte[] bytes;
            raf.seek(fatEntry.offset);
    		bytes = new byte[fatEntry.size];
    		raf.readFully(bytes);
    		
    		File outFile = new File("F:/game/pal1new/run/新仙剑奇侠传/myex/All_Map1/13058阿香" + i);
    		OutputStream os = new FileOutputStream(outFile);
    		IOUtils.write(bytes, os);
        }
	}

	private void getFat() throws Exception {
		fileChannel.position(6);
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(6);
        fileChannel.read(buffer);
        buffer.flip();
        
        int fatCount = buffer.getShort();
        for (int i = 0; i < fatCount; i++) {
        	FatEntry fatEntry = new FatEntry();
        	fatEntries.add(fatEntry);
        	
            buffer = ByteBuffer.allocate(0x24);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.limit(0x24);
            fileChannel.read(buffer);
            buffer.flip();
            
        	fatEntry.decode(buffer);
        }
		
	}


    private void getPallete(InputStream isPalete) throws Exception {
        
        colors = new Color[256];
        for (int i = 0; i < colors.length; i++) {
        	int r =  isPalete.read();
        	int g =  isPalete.read();
        	int b =  isPalete.read();
        	int a =  0xFF;
        	colors[i] = new Color(r,g,b,a).brighter().brighter().brighter().brighter();
        }
        isPalete.close();
	}

	private void getPixel() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(20);
        fileChannel.read(buffer);
        buffer.flip();
       
		int[] offsets = new int[4];
        for (int i = 0; i < 4; i++) {
        	offsets[i] = buffer.getInt();
            //System.out.println("offsets[i]="+offsets[i]);
        }
        
        width = buffer.getShort();
        height = buffer.getShort();
        
	    int total = 0;
	    int i = 0;
	    int j = 0;
	    bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    while (raf.getFilePointer() < offsets[1]) {
	        int aNum = raf.readUnsignedByte();
	        if ((aNum & 0xC0) == 0xC0) {
	            aNum = aNum & 0x3F;
	            total += aNum;
	            int colorIndex = raf.readUnsignedByte();
	            for (int k = 0; k < aNum; k++) {
	            	bi.setRGB(i, j, colors[colorIndex].getRGB());
	            	i++;
	            	if (i>=width) {
	            		i=0;
	            		j++;
	            	}
	            }
	        } else {
	            total += 1;
            	bi.setRGB(i, j, colors[aNum].getRGB());
            	i++;
            	if (i>=width) {
            		i=0;
            		j++;
            	}
	        }
	        System.out.println(",aNum="+aNum+",total="+total);
	    }
        
        ImageIO.write(bi, "PNG", new File("G:/f/VirtualNes/DOS/rom/FDgame/炎龙骑士团合集/GAME/fd2/myex/DATO/abc.png"));
	}
	
    public void close() throws Exception {
        fileChannel.close();
        raf.close();
    }
 // Make a subclass of the JPanel
    public class PanelImageDisplayer extends JPanel
    {
       // The paintComponent method of the canvas class is automatically 
       // called when the window is created or resized
       public void paintComponent(Graphics g)
       {
             g.drawImage(bi, 0, 0, null);

       }
    }

    public class FatEntry {
        public String fname;
        public int offset;
        public int size;
        public int width;
        public int height;
        public int unknown;

		public void decode(ByteBuffer buffer) throws Exception {
			offset = buffer.getInt();
			size = buffer.getInt();
			unknown = buffer.getInt();
			buffer.getInt();
			buffer.getInt();
			buffer.getInt();
			buffer.getInt();
			buffer.getInt();
			width = buffer.getShort();
			height = buffer.getShort();
			System.out.println(toString());
		}
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}