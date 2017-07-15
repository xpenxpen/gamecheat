package org.xpen.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class ZipCompressor {
	
	private static final int BUFFER_SIZE = 524288; //512K
	
	public static void decompress(File inZip, File outFolder) throws IOException {
		decompress(inZip, outFolder, "Cp1252");
	}
	
	public static void decompress(File inZip, File outFolder, String encoding) throws IOException {
	    FileInputStream fis = null;
	    ZipArchiveInputStream zis = null;
	    FileOutputStream fos = null;
	    try {
	        byte[] buffer = new byte[BUFFER_SIZE];
	        fis = new FileInputStream(inZip);
	        zis = new ZipArchiveInputStream(fis, encoding, true); // this supports non-USACII names
	        ArchiveEntry entry;
	        while ((entry = zis.getNextEntry()) != null) {
	            File file = new File(outFolder, entry.getName());
	            if (entry.isDirectory()) {
	                file.mkdirs();
	            } else {
	                file.getParentFile().mkdirs();
	                fos = new FileOutputStream(file);
	                int read;
	                while ((read = zis.read(buffer,0,buffer.length)) != -1)
	                    fos.write(buffer,0,read);
	                fos.close();
	                fos=null;
	            }
	        }
	    } finally {
	        try { zis.close(); } catch (Exception e) { }
	        try { fis.close(); } catch (Exception e) { }
	        try { if (fos!=null) fos.close(); } catch (Exception e) { }
	    }
	}
}
