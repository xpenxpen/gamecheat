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
    private LodChunk lodChunk;
    public short majorVer;
    public short minorVer;

    @Override
    public void decode(ByteBuffer buffer, Chunk chunk) {
        
    }

	public void toObjFormat(File file) throws Exception {
		LOG.debug("toObjFormat start");
		for (Chunk chunk: this.children) {
			if (chunk.getType() == ChunkType.PCMP) {
				pcmpChunk = (PcmpChunk)chunk;
			} else if (chunk.getType() == ChunkType.UCMP) {
				ucmpChunk = (UcmpChunk)chunk;
			} else if (chunk.getType() == ChunkType.LODS) {
				lodChunk = (LodChunk)chunk;
			}
		}
		
		int lastDot = file.getAbsolutePath().lastIndexOf(".");
        DecimalFormat nf = new DecimalFormat("0.######");
		
		//export all lods (named _0, _1, _2...)
        for (int i = 0; i < lodChunk.lods.size(); i++) {
			
			String objFileName = file.getAbsolutePath().substring(0, lastDot) + "_" + i + ".obj";
			LOG.debug("objFileName={}", objFileName);
	        BufferedWriter bw = new BufferedWriter(new FileWriter(objFileName));
			

	        
			LevelOfDetail levelOfDetail = lodChunk.lods.get(i);
			for (VertexBuffer vertexBuffer : levelOfDetail.vbs) {
				bw.write("#number of vert:" + vertexBuffer.verts.size());
				bw.newLine();
				for (Vert vert : vertexBuffer.verts) {
					bw.write("v " + nf.format(vert.vx * pcmpChunk.y + pcmpChunk.x)
					+ " " + nf.format(vert.vy * pcmpChunk.y + pcmpChunk.x)
					+ " " + nf.format(vert.vz * pcmpChunk.y + pcmpChunk.x));
					bw.newLine();
				}
//						for (Vert vert : vertexBuffer.verts) {
//							bw.write("v " + nf.format(HalfFloatUtil.toFloat(vert.vx))
//							+ " " + nf.format(HalfFloatUtil.toFloat(vert.vy))
//							+ " " + nf.format(HalfFloatUtil.toFloat(vert.vz)));
//							bw.newLine();
//						}
				
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
			
			bw.close();
		}
		
		
		
		
	}


}
