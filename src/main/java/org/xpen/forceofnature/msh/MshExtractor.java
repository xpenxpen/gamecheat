package org.xpen.forceofnature.msh;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.util.ByteBufferUtil;

public class MshExtractor {
    
    private static final Logger LOG = LoggerFactory.getLogger(MshExtractor.class);
    
    private static final String BAKED_MESH = "<baked mesh>";
    private RandomAccessFile raf;
    private FileChannel fileChannel;
    private byte[] bytes;
    private File file;
    
    public MshExtractor(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public MshExtractor(File file) throws Exception {
        this.file = file;
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
        this.bytes = bytes;
    }

    public static void main(String[] args) throws Exception {
        decode1(new File("D:/git/opensource/dunia2/fon/myex/Objects/Environment/Trees/DeadFir_01_p1.msh"));
        //decode1(new File("D:/git/opensource/dunia2/fon/myex/Objects/Environment/Harvest/Violets_01.msh"));
        //decode1(new File("D:/git/opensource/dunia2/fon/myex/Objects/Environment/Trees/AgedTree_01_p1.msh"));
        //decode1(new File("D:/git/opensource/dunia2/fon/myex/Objects/Effects/Ash_01.msh"));
    }
    

    private static void decode1(File file) throws Exception {
        LOG.debug("Starting:{}", file);
        MshExtractor matExtractor = new MshExtractor(file);
        matExtractor.decode();
    }

    /**
     * MAT File format
     * 4 0000
     * '<baked mesh>'
     *     Plane001
     *     Textures: XX
     * 4   Vertext count
     * 4   Face count
     * ----LOOP
     * |   32 vertex buffer (x y z w u v)
     * ----
     * ----LOOP
     * |   6 Face
     * ----
     */
     private void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int magicMesh = buffer.getInt();
        if (magicMesh != 0) {
            throw new RuntimeException("magic <> 0");
        }
        
        while (buffer.hasRemaining()) {
            String startTag = ByteBufferUtil.getNullTerminatedString(buffer);
            if (startTag.equals(BAKED_MESH)) {
                LOG.debug("position:{}", buffer.position());
                Mat mat = new Mat();
                mat.decode(buffer);
                LOG.debug("mat={}", mat);
                mat.toObjFormat(file);
                String endTag = ByteBufferUtil.getNullTerminatedString(buffer);
                if (!(endTag.replaceAll("\\\\", "").equals(startTag))) {
                    LOG.error("position:{}", buffer.position());
                    throw new RuntimeException("end tag not match start tag:"+endTag);
                }
            } else {
                break;
            }
        }
        
    }
     
    public class Mat {
        public String name;
        public String texture;
        public int vertexCount;
        public int faceCount;
        public List<Vert> verts = new ArrayList<Vert>();
        public List<Face> faces = new ArrayList<Face>();
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
        
        public void decode(ByteBuffer buffer) throws Exception {
            name = ByteBufferUtil.getNullTerminatedString(buffer);
            texture = ByteBufferUtil.getNullTerminatedString(buffer);
            vertexCount = buffer.getInt();
            faceCount = buffer.getInt();
            
            for (int i = 0; i < vertexCount; i++) {
                Vert vert = new Vert();
                verts.add(vert);
                vert.decode(buffer);
            }
            
            for (int i = 0; i < faceCount; i++) {
                Face face = new Face();
                faces.add(face);
                face.decode(buffer);
            }
       }

       public void toObjFormat(File file) throws Exception {
            LOG.debug("toObjFormat start");
            
            int lastDot = file.getAbsolutePath().lastIndexOf(".");
            DecimalFormat nf = new DecimalFormat("0.######");
                
            String objFileName = file.getAbsolutePath().substring(0, lastDot) + "_" + this.name + ".obj";
            LOG.debug("objFileName={}", objFileName);
            BufferedWriter bw = new BufferedWriter(new FileWriter(objFileName));

                
//            bw.write("mtllib abc.mtl");
//            bw.newLine();
//            bw.write("usemtl abc");
//            bw.newLine();
            
            bw.write("#number of vert:" + verts.size());
            bw.newLine();
            for (Vert vert : verts) {
                bw.write("v " + nf.format(vert.vx)
                + " " + nf.format(vert.vy)
                + " " + nf.format(vert.vz));
                bw.newLine();
            }
            
            bw.newLine();
            bw.write("#number of uv:" + verts.size());
            bw.newLine();
            for (Vert vert : verts) {
                bw.write("vt " + nf.format(vert.u)
                + " " + nf.format(vert.v));
                bw.newLine();
            }
            
            bw.newLine();
            bw.write("#number of face:" + faces.size());
            bw.newLine();
            for (Face face : faces) {
                bw.write(
                  "f " + (face.i1+1) + "/" + (face.i1+1)
                + " " + (face.i2+1) + "/" + (face.i2+1)
                + " " + (face.i3+1) + "/" + (face.i3+1));
                bw.newLine();
            }
                
                bw.close();
        }
    }

    
    public class Vert {
        //vertex 16
        public float vx;
        public float vy;
        public float vz;
        public float vw;
        //uv 8
        public float u;
        public float v;
        
        public byte[] bytes = new byte[32];
       
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }


        public void decode(ByteBuffer buffer) {
            int readBytes = 0;
//            vx = buffer.getFloat();
//            vy = buffer.getFloat();
//            vz = buffer.getFloat();
//            vw = buffer.getFloat();
//            u = buffer.getFloat();
//            v = buffer.getFloat();
//            readBytes += 24;
//            vx = buffer.getShort() / 16383.5f;
//            vy = buffer.getShort() / 16383.5f;
//            vz = buffer.getShort() / 16383.5f;
//            vw = buffer.getShort() / 16383.5f;
//            u = buffer.getFloat();
//            v = buffer.getFloat();
//            readBytes += 16;
            buffer.get(bytes);

            //buffer.position(buffer.position() + 32 - readBytes);
        }
    }
    
    public class Face {
        public short i1;
        public short i2;
        public short i3;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }

        public void decode(ByteBuffer buffer) {
            i1 = buffer.getShort();
            i2 = buffer.getShort();
            i3 = buffer.getShort();
        }
    }

}
