package org.xpen.ubisoft.dunia2.fileformat.xbg.chunk;

public class ChunkType {
    public static final int ROOT = 0x00000000;
    
    public static final int MATERIAL_REFERENCE = 0x524D544C; // 'RMTL'
    public static final int SKEL = 0x534B454C; // 'SKEL'
    public static final int NODES = 0x4E4F4445; // 'NODE'
    public static final int SKID = 0x534B4944; // 'SKID'
    public static final int SKND = 0x534B4E44; // 'SKND'
    public static final int CLUSTER = 0x434C5553; // 'CLUS'
    public static final int LODS = 0x04C4F4453; // 'LODS'
    public static final int BOUNDING_BOX = 0x42424F58; // 'BBOX'
    public static final int BOUNDING_SPHERE = 0x42535048; // 'BSPH'
    public static final int LOD_INFO = 0x004C4F44; // 'LOD\0'
    public static final int PCMP = 0x50434D50; // 'PCMP'
    public static final int UCMP = 0x55434D50; // 'UCMP'
    public static final int MATERIAL_DESCRIPTOR = 0x444D544C; // 'DMTL'

    public static final int O2BM = 0x4F32424D; // 'O2BM'
    public static final int IKDA = 0x494B4441; // 'IKDA'

}
