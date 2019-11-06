package zsfpackage;

import org.apache.log4j.Logger;

import tools.PrintTools;
import tools.VarTools;

public class ZFSlr {
	
	private static final Logger log = Logger.getLogger(ZFSlr.class.getName()); 

	private long lrc_txtype;
	private long lrc_reclen;
	private long lrc_txg;
	private long lrc_seq;
	private int nu;	
	
	public ZFSlr() {
		
		log.trace("ZFSlr");
	}
	
	public void Pack(byte[] bs, int offset, int len) {
		
		nu = offset; 
		lrc_txtype = VarTools.ByteArray2Long1(bs,nu+0x00,8);  
		lrc_reclen = VarTools.ByteArray2Long1(bs,nu+0x08,8);  
		lrc_txg    = VarTools.ByteArray2Long1(bs,nu+0x10,8);  
		lrc_seq    = VarTools.ByteArray2Long1(bs,nu+0x18,8);  
	}
	
	public void Print(boolean isPrint) {
		 
		if (!isPrint)
			return; 
		PrintTools.Print10andHex("lrc_txtype", "%08X",lrc_txtype); 
		PrintTools.Print10andHex("lrc_reclen", "%08X",lrc_reclen); 
		PrintTools.Print10andHex("lrc_txg",    "%08X",lrc_txg); 
		PrintTools.Print10andHex("lrc_seq",    "%08X",lrc_seq); 
	}
	
}
