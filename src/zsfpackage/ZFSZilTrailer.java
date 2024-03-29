package zsfpackage;

import org.apache.log4j.Logger;

import hdd.Block;
import tools.PrintTools;
import tools.VarTools; 

public class ZFSZilTrailer extends Block  {
	
	private static final Logger log = Logger.getLogger(ZFSZilTrailer.class.getName()); 

		public int nu; 
		public DNblkptr zit_next_blk = new DNblkptr(); 
		private long zit_pad;
		private long zit_nused;
		ZFSZioBlockTail zit_bt = new ZFSZioBlockTail(); 

		public ZFSZilTrailer() {
			log.trace("ZFSZilTrailer");
		}
		
		public void Pack(byte[] bs, int ZIL_offset, int size) {
			
			nu = ZIL_offset;
			braw = VarTools.byteArray2byteArrayShort(bs, ZIL_offset, size);
			zit_pad = VarTools.ByteArray2Long1(bs,nu+0x00,8); 
			zit_next_blk.Pack(bs, nu+0x08);
			zit_nused = VarTools.ByteArray2Long1(bs,nu+0x88,8); 
			zit_bt.Pack(bs, nu+0x90);
		}
			
		public void Print() {
			 
			PrintTools.Print10andHex("zit_pad",  "%08X",zit_pad);
			PrintTools.Print10andHex("zit_nused","%08X",zit_nused);
			zit_next_blk.Print();
			zit_bt.Print();
		} 
}
