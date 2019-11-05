package zsfpackage;

import tools.PrintTools;
import tools.VarTools;

public class ZFSZioBlockTail {
	
	public  long ZBT_MAGIC = 0x210da7ab10c7a11L; // 
	public  long zbt_magic;
	public  byte[] zbt_cksum = new byte[32];
	public  boolean isMagic = false; 
	private int nu;

	public ZFSZioBlockTail () {
		
	}

	public void Pack(byte[] bs, int offset) {

		nu = (int)offset;
		zbt_magic = VarTools.ByteArray2Long1(bs,nu+0x00,8); 
		isMagic = (ZBT_MAGIC == zbt_magic);
		zbt_cksum = VarTools.byteArray2byteArrayShort(bs, offset, 32);
	}
	
	public void Print(boolean isPrint) {
		 
		if (!isPrint)
			return;
		PrintTools.Print10andHex("zbt_magic",  "%16X",zbt_magic);
		PrintTools.Dump(zbt_cksum, 0, isPrint);
	}

}
