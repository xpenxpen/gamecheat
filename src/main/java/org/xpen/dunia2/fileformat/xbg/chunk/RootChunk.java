package org.xpen.dunia2.fileformat.xbg.chunk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.dunia2.fileformat.xbg.chunk.LodChunk.Face;
import org.xpen.dunia2.fileformat.xbg.chunk.LodChunk.LevelOfDetail;
import org.xpen.dunia2.fileformat.xbg.chunk.LodChunk.Vert;
import org.xpen.dunia2.fileformat.xbg.chunk.LodChunk.VertexBuffer;

public class RootChunk extends AbstractChunk {
    
    private static final Logger LOG = LoggerFactory.getLogger(RootChunk.class);
    
    private PcmpChunk pcmpChunk;
    private UcmpChunk ucmpChunk;
    public short majorVer;
    public short minorVer;

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        
    }

	public void toObjFormat(File file) throws Exception {
		int lastDot = file.getAbsolutePath().lastIndexOf(".");
		String objFileName = file.getAbsolutePath().substring(0, lastDot) + ".obj";
		LOG.debug("objFileName={}", objFileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(objFileName));
		
        DecimalFormat nf = new DecimalFormat("0.######");
        
        //First round get vertex mad constants
		for (Chunk chunk: this.children) {
			if (chunk.getType() == ChunkType.PCMP) {
				pcmpChunk = (PcmpChunk)chunk;
			} else if (chunk.getType() == ChunkType.UCMP) {
				ucmpChunk = (UcmpChunk)chunk;
			}
		}
        
		for (Chunk chunk: this.children) {
			if (chunk.getType() == ChunkType.LODS) {
				LOG.debug("toObjFormat start");
				LodChunk lodChunk = (LodChunk)chunk;
				LevelOfDetail levelOfDetail = lodChunk.lods.get(0);
				for (VertexBuffer vertexBuffer : levelOfDetail.vbs) {
					bw.write("#number of vert:" + vertexBuffer.verts.size());
					bw.newLine();
					for (Vert vert : vertexBuffer.verts) {
						bw.write("v " + nf.format(vert.vx * pcmpChunk.y + pcmpChunk.x)
						+ " " + nf.format(vert.vy * pcmpChunk.y + pcmpChunk.x)
						+ " " + nf.format(vert.vz * pcmpChunk.y + pcmpChunk.x));
						bw.newLine();
					}
//					for (Vert vert : vertexBuffer.verts) {
//						bw.write("v " + nf.format(HalfFloatUtil.toFloat(vert.vx))
//						+ " " + nf.format(HalfFloatUtil.toFloat(vert.vy))
//						+ " " + nf.format(HalfFloatUtil.toFloat(vert.vz)));
//						bw.newLine();
//					}
					
					bw.newLine();
					bw.write("#number of face:" + vertexBuffer.faces.size());
					bw.newLine();
					for (Face face : vertexBuffer.faces) {
						bw.write("f " + (face.i1+1)
						+ " " + (face.i2+1)
						+ " " + (face.i3+1));
						bw.newLine();
					}
				}
			}
		}
		
		bw.close();
		
	}


}
