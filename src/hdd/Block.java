package hdd;

import org.apache.log4j.Logger;

import tools.FileTools;

public class Block {

	private static final Logger log = Logger.getLogger(Block.class.getName()); 
	
	public long StartAddress;
	public byte[] braw;
	
	public Block() {
		
		log.trace("Constructor");
	}

	public void WriteBlock(String sF) {
		
		FileTools.WriteBlock_(sF, braw, braw.length);		
	}
	
}
