package zsfpackage;

import org.apache.log4j.Logger;

import hdd.Block; 
import tools.PrintTools;
import tools.VarTools;

public class DNblkptr  extends Block  {
	
	private static final Logger log = Logger.getLogger(DNblkptr.class.getName()); 

	public int[]  vdev 	  = new int[3];
	public byte[] grid 	  = new byte[3];
	public int[]  asize	  = new int[3];
	public byte[] G 	  = new byte[3];
	public long[] offset  = new long[3];
	public long[] address = new long[3];
	public long Elong;
	public long ElongN;
	private String E;
	public int lvl;
	public ZFSBlockType btype  = new ZFSBlockType();
	public ZFSChecksum  cksum  = new ZFSChecksum(); 
	public ZFSCompression compress = new ZFSCompression();
	public int  psize;
	public int  lsize;
	public long txgPhisical;
	public long txgLogic;
	public long fillCount;
	public byte[] checksum = new byte[0x20];
	private int DNblkptrOffset;
    public boolean isNull = true; 

	public DNblkptr() {
		
	}
	
	public void Pack(byte[] bs, int DNoffset) {
		
		log.trace("DN_blkptr:");
		PrintTools.Dump(bs, DNoffset, 128);
		if (VarTools.CheckNull(bs,DNoffset,128))
			return;
		isNull = false;
		DNblkptrOffset = DNoffset;		
		braw = VarTools.byteArray2byteArrayShort(bs, DNblkptrOffset, 0x80);
		log.info("DN_blkptr raw bytes = 0x" + String.format("%08X",DNblkptrOffset));
		for (int i = 0; i<3; i++) {
			int nu0 = i*0x10;
	        vdev[i]  = (int)VarTools.ByteArray2Long1(bs,DNblkptrOffset+nu0+0x04,4);
	        grid[i]  = (byte)bs[DNblkptrOffset+nu0+0x03];
	        asize[i] = (int)VarTools.ByteArray2Long1(bs,DNblkptrOffset+nu0+0x00,3);
	        G[i]     = (byte)(((byte)bs[DNblkptrOffset+nu0+0x08]) & (0x01 << 7));	  
	        if (bs[DNblkptrOffset+nu0+0x08]<0) { // Gang block
	        	bs[DNblkptrOffset+nu0+0x08] = (byte)(-bs[DNblkptrOffset+nu0+0x08]);
	        	offset[i] = VarTools.ByteArray2Long1(bs,DNblkptrOffset+nu0+0x08,8);
	        	bs[DNblkptrOffset+nu0+0x08] = (byte)(-bs[DNblkptrOffset+nu0+0x08]);
	        } else
	        	offset[i] = VarTools.ByteArray2Long1(bs,DNblkptrOffset+nu0+0x08,8);
	        address[i] = (offset[i]<<9)+0x400000;
		}
		Elong  		= VarTools.ByteArray2Long1(bs,DNblkptrOffset+0x30,0x08);
		ElongN 		= VarTools.ByteArray2LongN(bs,DNblkptrOffset+0x30,0x08);
        E  			= (bs[DNblkptrOffset+0x37]<0 ? "Little Endian" : "Big Endian");
        lvl 		= bs[DNblkptrOffset+0x37]&0x1F; 
        btype.Pack(bs[DNblkptrOffset+0x36]);
        cksum.Pack(bs[DNblkptrOffset+0x35]);      
        compress.Pack( bs[DNblkptrOffset+0x34]); 
        lsize       = (int)VarTools.ByteArray2Long1(bs,DNblkptrOffset+0x30,0x02);
        psize       = (int)VarTools.ByteArray2Long1(bs,DNblkptrOffset+0x32,0x02);
        txgPhisical = VarTools.ByteArray2Long1(bs,DNblkptrOffset+0x48,0x08);
        txgLogic    = VarTools.ByteArray2Long1(bs,DNblkptrOffset+0x50,0x08); 
        fillCount   = VarTools.ByteArray2Long1(bs,DNblkptrOffset+0x58,0x08);
        checksum    = VarTools.byteArray2byteArrayShort(bs, DNblkptrOffset+0x60, 0x20);
	}
	
	public void Print() {
		 
		if (isNull) {
			log.debug("blkptr_t is null.");
			return;
		}
		log.debug("blkptr_t offset = 0x" + String.format("%08X",DNblkptrOffset));
		for (int i = 0; i<3; i++) {
			int nu0 = i*16;
			log.debug("DVA " + i+"            "+String.format("%08X: ",DNblkptrOffset+nu0+0x00));
			log.debug("---vdev1 = 0x" +  String.format("%04X",vdev[i]));
			log.debug("---grid  = 0x" +  String.format("%02X",grid[1]));
			log.debug("---asize (Allocated block size) = 0x" +  
	        		String.format("%02X",asize[i]));
			log.debug("---G = 0x" +  String.format("%01X",G[i]) + 
	        		(G[i] == -128 ? ". Gang block" : ". Non gang block"));
			log.debug("---offset = 0x" +  String.format("%08X",offset[i])  + " or " + offset[i]);
			log.debug("---address = 0x" + String.format("%08X",address[i]) + " or " + address[i]);
			log.debug("---end = 0x" +  String.format("%04X",nu0+0x10));	        
		}
		log.debug(String.format("%08X: ",DNblkptrOffset+0x30)+
        		"Elong = 0x" +  String.format("%016X",Elong));
		log.debug(String.format("%08X: ",DNblkptrOffset+0x30)+ "E = " + E);
		log.debug(String.format("%08X: ",DNblkptrOffset+0x30)+ "lvl = " + lvl);
		log.debug(String.format("%08X: ",DNblkptrOffset+0x30)+ "btype is " + 
        		btype.Descriptor + " (" +btype.Descriptor + ") or = "+btype.Num);  
        log.debug(String.format("%08X: ",DNblkptrOffset+0x32)+ "cksum = " + cksum.Num +
        		". "+cksum.ObjsetType+" or "+ cksum.Descriptor);  
        log.debug(String.format("%08X: ",DNblkptrOffset+0x33)+ "comp = " + compress.Num +
        		". "+compress.Descriptor+" or "+ compress.Algorithm);
        log.debug(String.format("%08X: ",DNblkptrOffset+0x34)+"psize (Phisical block size) = " + 
        		psize + " or 0x" + String.format("%02X",psize));
        log.debug(String.format("%08X: ",DNblkptrOffset+0x36)+"lsize (Logical block size) = " + 
        		lsize +" 0x" + String.format("%02X",lsize));   
        log.debug(String.format("%08X: ",DNblkptrOffset+0x48)+
        		"Birth TXG (phisical) = 0x"  +  String.format("%016X",txgPhisical));
        log.debug(String.format("%08X: ",DNblkptrOffset+0x50)+
        		"Birth TXG (logic) = 0x"  +  String.format("%016X",txgLogic));
        log.debug(String.format("%08X: ",DNblkptrOffset+0x58)+
        		"fill count = 0x" +  String.format("%016X",fillCount)); 
        log.debug(String.format("%08X: ",DNblkptrOffset+0x60)+"Checksum:");
        PrintTools.Dump(checksum,0,0x20);	
        log.debug("blkptr_t end = 0x" + String.format("%08X",DNblkptrOffset+128)+"\n"); 
	} 
}
