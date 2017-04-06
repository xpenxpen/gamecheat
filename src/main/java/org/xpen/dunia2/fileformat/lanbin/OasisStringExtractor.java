package org.xpen.dunia2.fileformat.lanbin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OasisStringExtractor {
    
    private static final Logger LOG = LoggerFactory.getLogger(OasisStringExtractor.class);
    
    private byte[] bytes;
	public List<Sector> sectors = new ArrayList<Sector>();
	public Document document;
	public Map<Integer, Node> languageMap = new HashMap<Integer, Node>();
    
    public OasisStringExtractor(byte[] bytes) {
        this.bytes = bytes;
    }

    public static void main(String[] args) throws Exception {
        //File file = new File("E:/aliBoxGames/games/5993/ex/common/languages/english/oasisstrings_compressed.bin");
        String rootFolder = "D:/git/opensource/dunia2/fc3dat/myex/common/languages/english/";
        String binFile = rootFolder + "oasisstrings_compressed.bin";
        String inXmlPath = rootFolder + "oasisstrings.xml";
        String outXmlPath = rootFolder + "oasisstrings_cn.xml";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(new File(binFile)));
        OasisStringExtractor oasisStringExtractor = new OasisStringExtractor(bytes);
        
        oasisStringExtractor.parseXml(new FileInputStream(inXmlPath));
        
        oasisStringExtractor.decode();
        
        oasisStringExtractor.writeXml(outXmlPath);
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");

    }
    
    private void parseXml(InputStream is) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = builder.parse(is);
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "/stringtable/section/string";
        NodeList stringNodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        
          
        for (int i = 0, n = stringNodeList.getLength(); i < n; i++) {
            Node stringNode = stringNodeList.item(i);
            int id = Integer.parseInt(stringNode.getAttributes().getNamedItem("id").getNodeValue());
            Node namedItem = stringNode.getAttributes().getNamedItem("value");
            languageMap.put(id, namedItem);
        }

    }
    
    private void writeXml(String file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        File outFile = new File(file);
        StreamResult result = new StreamResult(outFile);
        transformer.transform(source, result);
    }

	/**
	 * 
int stringTableCount;
struct stringTable{    
    int unknown;
    int sectorCount;
    struct section{
        int sectorHash;//crc32 of sector name
        int stringCount;
        struct{
            int id;    //line id
            int sec;   //same as sectorHash
            int _enum; //crc32 hash of enum attribute in string
            int pack; //crc32 Hash of 'Main'    
        }lines[stringCount]<optimize=false>;
        int cmpPartCount; //Number of lzo compressed parts
        struct{
            int unknown;
            int cs;//compressed size of data
            int ds; //decompressed size of data
            ubyte cmpData[cs]; //These bytes are lzo compressed (lzo1x)
       }parts[cmpPartCount]<optimize=false>;   
    }sectors[sectorCount]<optimize=false>;
}all[stringTableCount]<optimize=false>;

	 */
    private void decode() throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int stringTableCount = buffer.getInt();
        for (int i = 0; i < stringTableCount; i++) {
        	int unknown1 = buffer.getInt();
        	int sectorCount = buffer.getInt();
        	for (int j = 0; j < sectorCount; j++) {
        		System.out.println("--start sectorCount " + j);
        		Sector sector = new Sector(this);
        		sectors.add(sector);
        		sector.decode(buffer);
        		System.out.println("--end   sectorCount " + j);
        		
        	}
        }
		
	}

}
