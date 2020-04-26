package org.xpen.ubisoft.dunia2.filenameguess;

import java.io.BufferedReader;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExistingCheck {
    
    private static final Logger LOG = LoggerFactory.getLogger(ExistingCheck.class);

    public static void main(String[] args) throws Exception {
    	BufferedReader br = new BufferedReader(new FileReader("E:\\git\\opensource\\gamecheat\\src\\main\\resources\\farcry3\\files\\ige.filelist"));
    	String line =null;
    	while ((line=br.readLine())!=null) {
    		if (line.contains("!")) {
    			System.out.println(line);
    		}
    	}
    }

}
