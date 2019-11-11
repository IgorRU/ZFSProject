package zsfpackage;

import org.apache.log4j.Logger;

import hdd.Block;
import tools.PrintTools;
import tools.VarTools;

public class ObjsetPhys  extends Block  {
	
	private static final Logger log = Logger.getLogger(ObjsetPhys.class.getName()); 

	//public static int size = 512+192+8+8+512+512;
	
	public DNodePhys os_meta_dnode = new DNodePhys();		// 512 byte
	public ZFSZilHeader os_zil_header = new ZFSZilHeader(); // 192 byte
	public long os_flags;
	public long os_type;
	public DNodePhys os_userused_dnode  = new DNodePhys();  // 512 byte
	public DNodePhys os_groupused_dnode = new DNodePhys();  // 512 byte
	private int nu; 
	
	
	public ObjsetPhys() {
		
		log.trace("ObjsetPhys");
	}
	
	public void Pack(byte[] bs, int offset, int size) {

		//PrintTools.Print10andHex("ObjsetPhys begin ",  "%08X", offset);
		//PrintTools.Print10andHex("ObjsetPhys size ",  "%08X", size);
		//PrintTools.Print10andHex("ObjsetPhys end ",  "%08X", offset+size);
		nu = offset;
		braw = VarTools.byteArray2byteArrayShort(bs, (int)offset, (int)size); 
		os_meta_dnode.Pack(bs,nu+0x00,512);
		os_zil_header.Pack(bs,nu+512,0x88);
		os_type = VarTools.ByteArray2Long1(bs,nu+0x00,8); 
		os_flags = VarTools.ByteArray2Long1(bs,nu+0x00,8); 
		os_meta_dnode.Pack(bs,nu+1024,0x88);
		os_userused_dnode.Pack(bs,nu+1024+512,0x88);
		os_groupused_dnode.Pack(bs,nu+1024+512+512,0x88);
	}
	
	public void Print(boolean isPrint) {
		 
		if (!isPrint)
			return;
		os_meta_dnode.Print(isPrint);
		os_zil_header.Print(isPrint);
		os_meta_dnode.Print(isPrint);
		os_userused_dnode.Print(isPrint);
		os_groupused_dnode.Print(isPrint);
		PrintTools.Print10andHex("os_type",  "%08X",os_type);
		PrintTools.Print10andHex("os_flags", "%08X",os_flags);
	} 
}
