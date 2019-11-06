package zsfpackage;

import org.apache.log4j.Logger;

import tools.PrintTools;
import tools.VarTools;

public class ZFSZilHeader {
	
	private static final Logger log = Logger.getLogger(PrintTools.class.getName()); 

	public  int  nu;
	public  long zh_claim_txg;
	public  long zh_replay_seq;
	public  DNblkptr zh_log = new DNblkptr();
	private long zh_claim_seq;
	private long zh_flags;
	private long zh_claim_ir_seq;

	public ZFSZilHeader() {
		
	}
	
	public void Pack(byte[] bs, int ZIL_offset, int len) {
		
		log.trace("Pack");
		nu = ZIL_offset;
		zh_claim_txg 	= VarTools.ByteArray2Long1(bs,nu+0x00,8);
		zh_replay_seq 	= VarTools.ByteArray2Long1(bs,nu+0x08,8);
		zh_log.Pack(bs, nu+0x10);
		zh_claim_seq 	= VarTools.ByteArray2Long1(bs,nu+0x90,8); 
		zh_flags 		= VarTools.ByteArray2Long1(bs,nu+0x98,8); 
		zh_claim_ir_seq	= VarTools.ByteArray2Long1(bs,nu+0xA0,8); 
	}
		
	public void Print(boolean isPrint) {
		 
		if (!isPrint)
			return;
		PrintTools.Print10andHex("zh_claim_txg",   "%08X",zh_claim_txg);
		PrintTools.Print10andHex("zh_replay_seq",  "%08X",zh_replay_seq);
		PrintTools.Print10andHex("zh_claim_seq",   "%08X",zh_claim_seq);
		PrintTools.Print10andHex("zh_flags",       "%08X",zh_flags);
		PrintTools.Print10andHex("zh_claim_ir_seq","%08X",zh_claim_ir_seq);
		zh_log.Print(isPrint);
	}
	
}
