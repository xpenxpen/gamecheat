package org.xpen.dunia2.fileformat.xbg.chunk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LodChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(LodChunk.class);
    
    public List<LevelOfDetail> lods = new ArrayList<LevelOfDetail>();
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public void decode(ByteBuffer buffer) {
        
        int count = buffer.getInt();
        LOG.debug("count={}, pos={}", count, buffer.position());
        
        for (int i = 0; i < count; i++) {
            LevelOfDetail node = new LevelOfDetail();
            node.decode(buffer);
            lods.add(node);
        }
        //LOG.debug("lods={}", lods);
    }
    
    public class LevelOfDetail {
        public float drawDistance; // seems to be a distance value for determining which LOD to use
        public List<VertexBuffer> vbs = new ArrayList<VertexBuffer>();
        public List<SubMesh> subMeshs = new ArrayList<SubMesh>();
        //public byte[] vertexData;
        //public short[] indices;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
        
        public void decode(ByteBuffer buffer) {
            this.drawDistance = buffer.getFloat();
            int vbCount = buffer.getInt();
            LOG.debug("vertex buffer Count={}, pos={}", vbCount, buffer.position());
            
            for (int j = 0; j < vbCount; j++) {
                VertexBuffer vertexBuffer = new VertexBuffer();
                vertexBuffer.fvfCode = buffer.getInt();
                vertexBuffer.stride = buffer.getInt();
                vertexBuffer.count = buffer.getInt();
                vertexBuffer.offset = buffer.getInt();
                this.vbs.add(vertexBuffer);
                LOG.debug("vertexBuffer.format={}, vertexBuffer.stride={}, vertexBuffer.count={}, vertexBuffer.offset={}",
                		vertexBuffer.fvfCode, vertexBuffer.stride, vertexBuffer.count, vertexBuffer.offset);
            }   
            
            int submeshCount = buffer.getInt();
            LOG.debug("submeshCount={}, pos={}", submeshCount, buffer.position());
            for (int j = 0; j < submeshCount; j++) {
            	SubMesh subMesh = new SubMesh();
            	subMesh.vbId = buffer.getInt();
            	subMesh.nodeId = buffer.getInt();
            	subMesh.materialId = buffer.getInt();
            	subMesh.firstIndex = buffer.getInt();
            	subMesh.lastVertIndex = buffer.getInt();
            	subMesh.vbOffset = buffer.getInt();
            	subMesh.unknown6 = buffer.getInt(); //if fileVer >= 52 unsigned --mesh name?
                this.subMeshs.add(subMesh);
            }
            
            int vertexDataSize = buffer.getInt();
            LOG.debug("vertexDataSize={}, pos={}", vertexDataSize, buffer.position());
            int curPos = buffer.position();
            // data is aligned to 16 bytes, ugh
            if (curPos % 16 != 0) {
                int skip = 16 - (curPos % 16);
                buffer.position(curPos + skip);
            }
            LOG.debug("new pos={}", buffer.position());
            //input.Seek(input.Position.Align(16), SeekOrigin.Begin);
            byte[] vertexData = new byte[vertexDataSize];
            buffer.get(vertexData);
            
            decodeVertex(vertexData);
            

            int indexCount = buffer.getInt();
            LOG.debug("indexCount={}, pos={}", indexCount, buffer.position());
            if (indexCount % 3 != 0) {
            	LOG.warn("indexCount % 3 != 0");
            }
            
            // data is aligned to 16 bytes, ugh
            // data is aligned to 16 bytes, ugh
            curPos = buffer.position();
            if (curPos % 16 != 0) {
                int skip = 16 - (curPos % 16);
                buffer.position(curPos + skip);
            }
            //input.Seek(input.Position.Align(16), SeekOrigin.Begin);
            //this.indices = new short[indexCount];
            
            byte[] faces = new byte[indexCount*2];
            buffer.get(faces);
            decodeFace(faces, indexCount);
            
//            for (int i = 0; i < indexCount; i++) {
//                this.indices[i] = buffer.getShort();
//            }
        }

		private void decodeVertex(byte[] vertex) {
			ByteBuffer buffer = ByteBuffer.wrap(vertex);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			
			LOG.debug("vertex.length={}", vertex.length);
			
			for (VertexBuffer vb : vbs) {
				LOG.debug("vb.count={}", vb.count);
				for (int i = 0; i < vb.count; i++) {
					Vert vert = new Vert();
					vb.verts.add(vert);
					vert.decode(buffer, vb);
					
				}
			}
			
			if (buffer.remaining() != 0) {
				throw new RuntimeException("buffer.remaining=" + buffer.remaining());
			}
		}

		private void decodeFace(byte[] faces, int indexCount) {
			ByteBuffer buffer = ByteBuffer.wrap(faces);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			
			LOG.debug("indexCount/3={}, buffer.limit={}", indexCount/3, buffer.limit());
			
			for (VertexBuffer vb : vbs) {
				for (int i = 0; i < indexCount/3; i++) {
					Face face = new Face();
					vb.faces.add(face);
					face.decode(buffer);
					
				}
			}
			if (buffer.remaining() != 0) {
				throw new RuntimeException("buffer.remaining=" + buffer.remaining());
			}
		}
    }
    
    public class VertexBuffer {
        public int fvfCode;
        public int stride;
        public int count;
        public int offset;
        public List<Vert> verts = new ArrayList<Vert>();
        public List<Face> faces = new ArrayList<Face>();
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    public class SubMesh {
        public int vbId;
        public int nodeId;
        public int materialId;
        public int firstIndex;
        public int lastVertIndex;
        public int vbOffset;
        public int unknown6;
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    public class Vert {
    	//vertex 8
        public short vx;
        public short vy;
        public short vz;
        public short vw;
        //uv 4
        public short u;
        public short v;
        //uv2 8
        public int u2;
        public int v2;
        //normal 4
        public byte nx;
        public byte ny;
        public byte nz;
        public byte nw;
        //color?
        public byte a;
        public byte r;
        public byte g;
        public byte b;
        
        //8
        public int boneIndex;
        public int boneWeight;
        
        //8
        public int tangents;
        public int binormals;

       
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }


		public void decode(ByteBuffer buffer, VertexBuffer vertexBuffer) {
			int fvfCode = vertexBuffer.fvfCode;
			int readBytes = 0;
			if ((fvfCode & 0x2) == 0x2) {
				vx = buffer.getShort();
				vy = buffer.getShort();
				vz = buffer.getShort();
				vw = buffer.getShort();
				readBytes += 8;
			}
			
			if ((fvfCode & 0x8) == 0x8) {
			    u = buffer.getShort();
			    v = buffer.getShort();
				readBytes += 4;
			}
			
//			if (vertexBuffer.stride != 52 && vertexBuffer.stride != 40) {
//				throw new RuntimeException("unsupported size=" + vertexBuffer.stride);
//			}
			
			if ((fvfCode & 0x40) == 0x40) {
			    a = buffer.get();
			    r = buffer.get();
			    g = buffer.get();
			    b = buffer.get();
				readBytes += 4;
			}
			
//			u2 = buffer.getInt();
//			v2 = buffer.getInt();
			
			if ((fvfCode & 0x80) == 0x80) {
			    nx = buffer.get();
			    ny = buffer.get();
			    nz = buffer.get();
			    nw = buffer.get();
				readBytes += 4;
			}

			buffer.position(buffer.position() + vertexBuffer.stride - readBytes);
		}
    }
    
    public class Face {
//        public int bufferIndex;
//        public int skeletonIndex;
//        public int materialIndex;
//        public int indicesStartIndex;
//        public int unknown4;
//        public int unknown5;
//        public int unknown6;
        
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
