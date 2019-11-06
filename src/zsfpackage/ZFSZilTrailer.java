package zsfpackage;

import org.apache.log4j.Logger;

import tools.PrintTools;
import tools.VarTools; 

public class ZFSZilTrailer {
	
	private static final Logger log = Logger.getLogger(ZFSZilTrailer.class.getName()); 

		public int nu; 
		public DNblkptr zit_next_blk = new DNblkptr(); 
		private long zit_pad;
		private long zit_nused;
		ZFSZioBlockTail zit_bt = new ZFSZioBlockTail();

		public ZFSZilTrailer() {
			log.trace("ZFSZilTrailer");
		}
		
		public void Pack(byte[] bs, int ZIL_offset, int len) {
			
			nu = ZIL_offset;
			zit_pad = VarTools.ByteArray2Long1(bs,nu+0x00,8); 
			zit_next_blk.Pack(bs, nu+0x08);
			zit_nused = VarTools.ByteArray2Long1(bs,nu+0x88,8); 
			zit_bt.Pack(bs, nu+0x90);
		}
			
		public void Print(boolean isPrint) {
			 
			if (!isPrint)
				return;
			PrintTools.Print10andHex("zit_pad",  "%08X",zit_pad);
			PrintTools.Print10andHex("zit_nused","%08X",zit_nused);
			zit_next_blk.Print(isPrint);
			zit_bt.Print(isPrint);
		}

}
