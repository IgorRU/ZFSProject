package zsfpackage;

import org.apache.log4j.Logger;

import tools.PrintTools;
import tools.VarTools;

public class DNblkptr {
	
	private static final Logger log = Logger.getLogger(DNblkptr.class.getName()); 

	public int[]  vdev 		= new int[3];
	public byte[] grid 		= new byte[3];
	public int[]  asize		= new int[3];
	public byte[] G 		= new byte[3];
	public long[] offset	= new long[3];
	public long[] address	= new long[3];
	public long Elong;
	public long ElongN;
	private String E;
	public int lvl;
	public ZFSBlockType btype  = new ZFSBlockType();
	public ZFSChecksum  cksum  = new ZFSChecksum(); 
	public ZFSCompression compress = new ZFSCompression();
	public  int psize;
	public  int lsize;
	public long txgPhisical;
	public long txgLogic;
	public long fillCount;
	public byte[] checksum = new byte[0x20];
	private int nu;
    public boolean isNull = true;

	public DNblkptr() {
		
	}
	
	public void Pack(byte[] bs, int DN_offset) {
		
		log.trace("DN_blkptr:");
		//PrintTools.Dump(bs, DN_offset, 128, true);
		if (VarTools.CheckNull(bs,DN_offset,128))
			return;
		isNull = false;
		nu = DN_offset;		
		for (int i = 0; i<3; i++) {
			int nu0 = i*0x10;
	        vdev[i]  = (int)VarTools.ByteArray2Long1(bs,nu+nu0+0x04,4);
	        grid[i]  = (byte)bs[nu+nu0+0x03];
	        asize[i] = (int)VarTools.ByteArray2Long1(bs,nu+nu0+0x00,3);
	        G[i]     = (byte)(((byte)bs[nu+nu0+0x08]) & (0x01 << 7));	  
	        if (bs[nu+nu0+0x08]<0) { // Gang block
	        	bs[nu+nu0+0x08] = (byte)(-bs[nu+nu0+0x08]);
	        	offset[i] = VarTools.ByteArray2Long1(bs,nu+nu0+0x08,8);
	        	bs[nu+nu0+0x08] = (byte)(-bs[nu+nu0+0x08]);
	        } else
	        	offset[i] = VarTools.ByteArray2Long1(bs,nu+nu0+0x08,8);
	        address[i] = (offset[i]<<9)+0x400000;
	        //if (isPrint)
			//System.out.println("---asize (Allocated block size) = 0x" + String.format("%02X",asize[i]));
		}
		Elong  = VarTools.ByteArray2Long1(bs,nu+0x30,0x08);
		ElongN = VarTools.ByteArray2LongN(bs,nu+0x30,0x08);
        E  = (bs[nu+0x37]<0 ? "Little Endian" : "Big Endian");
        lvl = bs[nu+0x37]&0x1F;
        //lvl = (byte) (lvl>0 ? lvl : -lvl);
        btype.Pack(bs[nu+0x36]);
        cksum.Pack(bs[nu+0x35]);      
        compress.Pack( bs[nu+0x34]);
        //psize = VarTools.ByteArray2ShortByte(bs,nu+0x34);
        //lsize = (int)VarTools.ByteArray2ShortByte(bs,nu+0x36);
        lsize = (int)VarTools.ByteArray2Long1(bs,nu+0x30,0x02);
        psize = (int)VarTools.ByteArray2Long1(bs,nu+0x32,0x02);
        txgPhisical = VarTools.ByteArray2Long1(bs,nu+0x48,0x08);
        txgLogic = VarTools.ByteArray2Long1(bs,nu+0x50,0x08);
		//System.out.println("  txgPhisical =  " + ub_txg + " txgLogic = " + txgLogic);
        fillCount = VarTools.ByteArray2Long1(bs,nu+0x58,0x08);
        checksum=VarTools.byteArray2byteArrayShort(bs, nu+0x60, 0x20);
	}
	
	public void Print(boolean isPrint) {
		 
		if (!isPrint||isNull)
			return;
		log.info("\nblkptr_t offset = 0x" + String.format("%08X",nu));
		for (int i = 0; i<3; i++) {
			int nu0 = i*16;
			log.info("DVA " + i+"            "+String.format("%08X: ",nu+nu0+0x00));
			log.info("---vdev1 = 0x" +  String.format("%04X",vdev[i]));
			log.info("---grid = 0x" +  String.format("%02X",grid[1]));
			log.info("---asize (Allocated block size) = 0x" +  
	        		String.format("%02X",asize[i]));
			log.info("---G = 0x" +  String.format("%01X",G[i]) + 
	        		(G[i] == -128 ? ". Gang block" : ". Non gang block"));
			log.info("---offset = 0x" +  String.format("%08X",offset[i])+" or "+
	        		offset[i]);
			log.info("---address = 0x" +  String.format("%08X",address[i])+" or "+
	        		address[i]);
			log.info("---end = 0x" +  String.format("%04X",nu0+0x10));	        
		}
		log.info(String.format("%08X: ",nu+0x30)+
        		"Elong = 0x" +  String.format("%016X",Elong));
		log.info(String.format("%08X: ",nu+0x30)+ "E = " + E);
		log.info(String.format("%08X: ",nu+0x30)+ "lvl = " + lvl);
		log.info("lvl ="+lvl);
		log.info(String.format("%08X: ",nu+0x30)+ "btype is " + 
        		btype.Descriptor + " (" +btype.Type + ") or = "+btype.Num); 
        if (btype.Num!=0)
        	log.info("btype.Num ="+btype.Num);
        log.info(String.format("%08X: ",nu+0x32)+ "cksum = " + cksum.Num +
        		". "+cksum.Descriptor+" or "+ cksum.Algorothm);  
        if (cksum.Num!=7)
        	log.info("cksum.Num ="+cksum.Num);
        log.info(String.format("%08X: ",nu+0x33)+ "comp = " + compress.Num +
        		". "+compress.Descriptor+" or "+ compress.Algorithm);
        log.info(String.format("%08X: ",nu+0x34)+"psize (Phisical block size) = " + 
        		psize + " or 0x" + String.format("%02X",psize));
        log.info(String.format("%08X: ",nu+0x36)+"lsize (Logical block size) = " + 
        		lsize +" 0x" + String.format("%02X",lsize));   
        log.info(String.format("%08X: ",nu+0x48)+
        		"Birth TXG (phisical) = 0x"  +  String.format("%016X",txgPhisical));
        log.info(String.format("%08X: ",nu+0x50)+
        		"Birth TXG (logic) = 0x"  +  String.format("%016X",txgLogic));
        log.info(String.format("%08X: ",nu+0x58)+
        		"fill count = 0x" +  String.format("%016X",fillCount)); 
        log.info(String.format("%08X: ",nu+0x60)+"Checksum:");
        PrintTools.Dump(checksum,0,0x20,true);	
        log.info("blkptr_t end = 0x" + String.format("%08X",nu+128)+"\n"); 
	}
	
}
