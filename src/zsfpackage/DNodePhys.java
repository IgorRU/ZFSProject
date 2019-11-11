package zsfpackage;

import org.apache.log4j.Logger;

import hdd.Block; 
import tools.PrintTools;
import tools.VarTools;

public class DNodePhys extends Block  {
	
	private static final Logger log = Logger.getLogger(DNodePhys.class.getName()); 

	static int SectorLength = 512;
	
	public  byte   dn_type;
	public  long   DMUadress;
	public  byte   dn_indblkshift;
	public  byte   dn_nlevels;
	private byte   dn_nblkptr;
	private byte   dn_bonustype;
	public  byte   dn_checksum;
	public  byte   dn_compress;
	public  byte   dn_flags;
	private int    dn_datablkszsec;
	public  byte   dn_extra_slots;
	public  int    dn_bonuslen;
	public  long   dn_maxblkid;
	public  long   dn_used;
	private long   dn_pad2;
	private byte[] dn_pad3;
	public  DNblkptr[] dn_blkptr = new DNblkptr[3];
	public  ZFSChecksum  cksum  = new ZFSChecksum(); 
	public  ZFSCompression compress = new ZFSCompression();
	private long os_flag;
	public ZMSObjsetType os_type = new ZMSObjsetType();
	//private String sDNodeFile; 

	public DNodePhys() {
		
	}
	
	public void Pack(byte[] bs, long offset, long size) {
			
		log.trace("DNode: ");
		DMUadress = offset;	 	
		braw = VarTools.byteArray2byteArrayShort(bs, (int)offset, (int)size); 
		PrintTools.Dump(braw, (int)offset, 0x40);
		dn_type			= braw[0x00];	 // dmu_object_type_t		   	 0x0A = 10 - DMU_OT_DNODE
		dn_indblkshift 	= braw[0x01];	 // ln2(indirect block size)   	 0x17 = 128k
		dn_nlevels 		= braw[0x02];	 // 1=dn_blkptr->data blocks   	 0x02
		dn_nblkptr		= braw[0x03];	 // length of dn_blkptr			 0x03
		dn_bonustype	= braw[0x04];	 // type of data in bonus buffer 0x00
		dn_checksum		= braw[0x05];	 // ZIO_CHECKSUM type			 0x00
        cksum.Pack(dn_checksum);      
		dn_compress		= braw[0x06];	 //	ZIO_COMPRESS type			 0x00 
        compress.Pack(dn_compress);
		dn_flags		= braw[0x07];	 // DNODE_FLAG_USED_BYTES (1<<0)
		dn_datablkszsec	= (int)VarTools.ByteArray2Long1(braw,0x08,2);	 // data block size in 512b sectors
		dn_bonuslen		= (int)VarTools.ByteArray2Long1(braw,0x0A,2);	 // length of dn_bonus
		dn_extra_slots	= braw[0x0C];	 // of subsequent slots consumed
		dn_pad2			= VarTools.ByteArray2Long( braw,0x0D,3);  // 
		dn_maxblkid		= VarTools.ByteArray2Long1(braw,0x10,8); // 0x1C largest allocated block ID
		dn_used			= VarTools.ByteArray2Long1(braw,0x18,8); // 0x024000 bytes (or sectors) of disk space
		dn_pad3			= VarTools.byteArray2byteArrayShort(braw,0x20, 8 * 4 ); // reserved
		for (int i = 0 ; i<3; i++ ) 			
			dn_blkptr[i] = new DNblkptr();
		int nn = 0x40;
		dn_blkptr[0].Pack(braw, nn+0x000);	
		log.info("dn_blkptr[0] " + PrintTools.PrintIsNull(dn_blkptr[0],"fill")); 
		if (dn_nblkptr>1) {
			dn_blkptr[1].Pack(braw, nn+0x080);
			log.info("dn_blkptr[1] " + PrintTools.PrintIsNull(dn_blkptr[1],"fill")); 
			dn_blkptr[2].Pack(braw, nn+0x100);
			log.info("dn_blkptr[2] " + PrintTools.PrintIsNull(dn_blkptr[2],"fill")); 
		} else
			log.info("dn_blkptr[1] and dn_blkptr[2] not present.");	
		os_type.Pack(braw[0x2C0]);
		os_flag	= VarTools.ByteArray2Long1(braw,0x2C8,8);  		
		PrintTools.Dump(braw, nn+0x180, (int)(size-nn-0x180));
	} 		

	public void Print(boolean isPrint) {
		 
		if (!isPrint)
			return;
		if (dn_blkptr[0].isNull) 
			 log.info("dn_blkptr[0] fill null.");
		if (dn_blkptr[1].isNull) 
			 log.info("dn_blkptr[1] fill null.");
		if (dn_blkptr[2].isNull) 
			 log.info("dn_blkptr[2] fill null."); 
		PrintTools.Print10andHex("dn_type",			"%04X",dn_type);
		PrintTools.Print10andHex("dn_indblkshift",	"%04X",dn_indblkshift);
		PrintTools.Print10andHex("dn_nlevels",		"%04X",dn_nlevels);
		PrintTools.Print10andHex("dn_nblkptr",		"%04X",dn_nblkptr);
		PrintTools.Print10andHex("dn_bonustype",	"%04X",dn_bonustype);
		PrintTools.Print10andHex("dn_checksum",		"%04X",dn_checksum);
		PrintTools.Print10andHex("dn_compress",		"%04X",dn_compress);
		PrintTools.Print10andHex("dn_flags",		"%04X",dn_flags);
		PrintTools.Print10andHex("dn_datablkszsec",	"%04X",dn_datablkszsec);
		PrintTools.Print10andHex("dn_bonuslen",		"%04X",dn_bonuslen);
		PrintTools.Print10andHex("dn_extra_slots",	"%04X",dn_extra_slots);
		PrintTools.Print10andHex("dn_pad2",			"%04X",dn_pad2);
		PrintTools.Print10andHex("dn_maxblkid",		"%04X",dn_maxblkid);
		PrintTools.Print10andHex("dn_used",			"%04X",dn_used); 
		PrintTools.Print10andHex("dn_pad3 length",	"%04X",dn_pad3.length); 
		os_type.Print();
		PrintTools.Print10andHex("os_flag",			"%04X",os_flag); 
		dn_blkptr[0].Print();
		if (dn_nblkptr>1) { 
			dn_blkptr[1].Print(); 
			dn_blkptr[2].Print();
		}			
	}
	
}
