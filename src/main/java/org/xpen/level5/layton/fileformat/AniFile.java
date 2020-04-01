package org.xpen.level5.layton.fileformat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ColorUtil;
import org.xpen.util.UserSetting;

public class AniFile {
    
    private static final Logger LOG = LoggerFactory.getLogger(AniFile.class);
    
    protected String subFolderName;
    protected String fileName;
    public boolean isNoCompress = false;
    private byte[] bytes;
    //Color depth: 3->4 bit, 4->8 bit
    public int cDepth;
    
    public AniFile(String subFolderName, String fileName, byte[] bytes) throws Exception {
        this.subFolderName = subFolderName;
        this.fileName = fileName;
        this.bytes = bytes;
    }
    
    /**
     * Ani File format
     */
    public void decode() throws Exception {
        decodeDat();
    }
        
    private void decodeDat() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        AniEntry aniEntry = new AniEntry();
        aniEntry.decode(buffer);
    }


    public void close() throws Exception {
    }
    
    /**
     * 动画
     *
     */
    public class AniEntry {
        public int frameCount;
        public int colorDepth;
        public List<FrameEntry> frames = new ArrayList<>();
        private Color[] colors;
        public int animCount;
        public List<String> animsName = new ArrayList<>();
        public List<AnimEntry> anims = new ArrayList<>();
        
        public void decode(ByteBuffer buffer) throws Exception {
            frameCount = buffer.getShort();
            colorDepth = buffer.getShort();
            cDepth = colorDepth;
            for (int i = 0; i < frameCount; i++) {
                FrameEntry frameEntry = new FrameEntry();
                frameEntry.decode(buffer);
                frames.add(frameEntry);
                //System.out.println(frameEntry);
                //System.out.println("--Parts--");
                //for (int j = 0; j < frameEntry.partCount; j++) {
                //    System.out.println(frameEntry.parts.get(j));
                //}
            }
            
            getPallete(buffer);
            //System.out.println(buffer.position());
            
            //All zero(0x1E)
            buffer.position(buffer.position() + 0x1E);
            
            animCount = buffer.getInt();
            //System.out.println("animCount="+animCount);
            for (int i = 0; i < animCount; i++) {
                byte[] nameB = new byte[0x1E];
                buffer.get(nameB);
                int nameLength = 0;
                while (nameB[nameLength] != 0) {
                    nameLength++;
                }
                byte[] nameB2 = Arrays.copyOfRange(nameB, 0, nameLength);
                
                animsName.add(new String(nameB2, StandardCharsets.ISO_8859_1));
            }
            
            //System.out.println(animsName);
            //System.out.println(buffer.position());
            
            for (int i = 0; i < animCount; i++) {
                AnimEntry animEntry = new AnimEntry();
                animEntry.decode(buffer);
                anims.add(animEntry);
            }
            
            //for (int i = 0; i < animCount; i++) {
            //    System.out.println(anims.get(i));
            //}
            //System.out.println(buffer.position());
            
            writePixel();
            
        }
        
        private void getPallete(ByteBuffer buffer) throws Exception {
            int colorCount = buffer.getInt();
            //BGR555
            colors = new Color[colorCount];
            for (int i = 0; i < colors.length; i++) {
                short colorBits = buffer.getShort();
                //System.out.println("Color " + i + "=" + colorBits);
                Color c = ColorUtil.bgr555ToRgb888(colorBits);
                colors[i] = c;
            }
        }
        
        private void writePixel() throws Exception {
            //Write to PNG
            for (int i = 0; i < animCount; i++) {
                String name = animsName.get(i);
                int frameCount = anims.get(i).frameCount;
                if (frameCount == 0) {
                    continue;
                }
                
                int imgIdx = anims.get(i).imagesIndexes[0];
                FrameEntry frameEntry = frames.get(imgIdx);
                
                //Recalculate image width height, original width height not accurate
                int maxWidth = 0;
                int maxHeight = 0;
                for (int j = 0; j < frameEntry.partCount; j++) {
                    PartEntry partEntry = frameEntry.parts.get(j);
                    int w = partEntry.posX + partEntry.width;
                    int h = partEntry.posY + partEntry.height;
                    if (w > maxWidth) {
                        maxWidth = w;
                    }
                    if (h > maxHeight) {
                        maxHeight = h;
                    }
                }
                
                BufferedImage bi = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
                for (int j = 0; j < frameEntry.partCount; j++) {
                    PartEntry partEntry = frameEntry.parts.get(j);
                    int imgSize = partEntry.width * partEntry.height;
                    //System.out.println("imgSize="+imgSize+",b="+partEntry.b.length);

                    
                    //1) Color depth = 3 --> 1 byte represent 2 pixels(Total 16 colors, Each pixel only use 4 bit)
                    //2) Else --> 1 byte represent 1 pixel
                    if (cDepth == 3) {
                        int imgSizeHalf = imgSize / 2;
                        for (int k = 0; k < imgSizeHalf; k++) {
                            int x = partEntry.posX + k * 2 % partEntry.width;
                            int y = partEntry.posY + k * 2 / partEntry.width;
                            
                            int pixel1 = (partEntry.b[k] & 0xF);
                            int pixel2 = ((partEntry.b[k] & 0xF0) >> 4);
                            bi.setRGB(x, y, colors[pixel1].getRGB());
                            bi.setRGB(x + 1, y, colors[pixel2].getRGB());
                        }
                    } else {
                        for (int k = 0; k < imgSize; k++) {
                            int x = partEntry.posX + k % partEntry.width;
                            int y = partEntry.posY + k / partEntry.width;
                            bi.setRGB(x, y, colors[partEntry.b[k] & 0xFF].getRGB());
                        }
                    }
                }
                //System.out.println("writeFile:"+name);
                writeFile(name, bi);
            }
            
        }

        private void writeFile(String name, BufferedImage bi) throws Exception {
            File outFile = null;
            String oldFileNameWithoutExt = fileName;
            File parent = new File(UserSetting.rootOutputFolder, subFolderName);
            //replace invalid file name char
            name = name.replace('*', '_');
            name = name.replace('?', '_');
            outFile = new File(parent, oldFileNameWithoutExt + "_" + name + ".png");
            
            File parentFile = outFile.getParentFile();
            parentFile.mkdirs();
            
            ImageIO.write(bi, "PNG", outFile);
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * 帧
     *
     */
    public class FrameEntry {
        public int width; //NOT accurate
        public int height; //NOT accurate
        public int partCount;
        public int unknown;
        public List<PartEntry> parts = new ArrayList<>();
        
        public void decode(ByteBuffer buffer) throws Exception {
            width = buffer.getShort();
            height = buffer.getShort();
            partCount = buffer.getShort();
            unknown = buffer.getShort();
            for (int i = 0; i < partCount; i++) {
                PartEntry partEntry = new PartEntry();
                partEntry.decode(buffer);
                parts.add(partEntry);
            }
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    /**
     * 每一帧的部件
     *
     */
    public class PartEntry {
        public int posX;
        public int posY;
        public int width;
        public int height;
        public byte[] b;
                
        public void decode(ByteBuffer buffer) throws Exception {
            posX = buffer.getShort();
            posY = buffer.getShort();
            width = (int)Math.pow(2, (buffer.getShort() + 3));
            height = (int)Math.pow(2, (buffer.getShort() + 3));
            
            int imgSize = width * height;
            if (cDepth == 3) {
                imgSize = imgSize / 2;
            }
            
            b = new byte[imgSize];
            buffer.get(b);
            //System.out.println(buffer.position());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    /**
     * 动画
     *
     */
    public class AnimEntry {
        public int frameCount;
        public int[] framesIds;
        public int[] framesUnks;
        public int[] imagesIndexes;
        
        public void decode(ByteBuffer buffer) throws Exception {
            frameCount = buffer.getInt();
            framesIds = new int[frameCount];
            framesUnks = new int[frameCount];
            imagesIndexes = new int[frameCount];
            
            for (int i = 0; i < frameCount; i++) {
                framesIds[i] = buffer.getInt();
            }
            for (int i = 0; i < frameCount; i++) {
                framesUnks[i] = buffer.getInt();
            }
            for (int i = 0; i < frameCount; i++) {
                imagesIndexes[i] = buffer.getInt();
            }
            
            //System.out.println(buffer.position());
        }
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
}
