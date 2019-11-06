package zsfpackage;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import tools.PrintTools;
import tools.VarTools;

public class ZFSUberBlock {
	
	private static final Logger log = Logger.getLogger(ZFSUberBlock.class.getName()); 
	
	public Date ZFSLabelDate;
	private SimpleDateFormat dateFormat = 
			new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");	
	
	public int nu;
	public long UberOffsetAbs;
	public int nUberBlock;
	public String ub_magic;
	public String ub_magicDecription;
	public boolean isMagic = false;
	public String ub_version;
	private long ub_versioni; 
	public long ub_txg;
	public String ub_guid_sum; 
	
	private boolean isPrintDump = false;	
	public DNblkptr dn_blkptr = new DNblkptr();

	public ZFSUberBlock(byte[] bs, int UberOffset, int nUber) {
		
		log.trace("ZFSUberBlock");
		nUberBlock = nUber;
		boolean isPrint = false; // (nUberBlock==104); 
		if (isPrint)
			System.out.println("------------"); 
		nu = UberOffset+nUberBlock*1024;
		UberOffsetAbs = nUberBlock*1024;;
		if (isPrint)
			System.out.println("ZFSUber is " + nUberBlock + " offset = " + String.format("%08X",nu)+ 
				" byte[] size = " + String.format("%08X",bs.length)); 
		if (NoMagic(VarTools.ByteArray2HexsStr(bs,nu,4)))
			return;		
		if (isPrint)
			System.out.println("UberBlock is "+nUberBlock+" offset = "+String.format("%05X",nu)); 
		PrintTools.Dump(bs,nu,0x80,isPrintDump);
		ub_version = VarTools.ByteArray2HexsStr(bs,nu+0x08,8);
		ub_versioni = VarTools.ByteArray2Long1(bs,nu+0x08,8);
		ub_txg = VarTools.ByteArray2Long1(bs,nu+0x10,8); 
		//System.out.println(" txg =  " + ub_txg + " or 0x" + String.format("%5X",ub_txg));
		ub_guid_sum = VarTools.ByteArray2HexsStr(bs,nu+0x18,8);
		long dl = VarTools.ByteArray2Long1(bs, nu+0x20, 8)*1000;  
        ZFSLabelDate = new Date(dl); 
        dn_blkptr = new DNblkptr();  
        dn_blkptr.Pack(bs,nu + 0x28);    
		isMagic = true;
		Print(isPrintDump); 
	}

	private boolean NoMagic(String hex) {
		
		ub_magic = hex;
		if (ub_magic.equals("0CB1BA00")) 
			ub_magicDecription = "Little Endian";
		else
			if (ub_magic.equals("00BAB10C")) 
				ub_magicDecription = "Big Endian";
			else {
				ub_magicDecription = "No magic block";
				return true;
			}
		return false;
	}

	public void Print(boolean isPrint) {
 
		if (!isPrint)
			return;
		if (!isMagic) {
			System.out.println("Uber block is null."); 
			return;
		}
		System.out.println("ub_magic = 0x"+ub_magic+" is "+ub_magicDecription); 
		System.out.println("ub_version = 0x" + ub_version + " or "+ub_versioni); 
		System.out.println("ub_txg = 0x" + String.format("%08X",ub_txg) +" or "+ ub_txg); 
		System.out.println("ub_guid_sum = 0x"  + ub_guid_sum); 
		dn_blkptr.Print(isPrint);
        System.out.println("date = " + dateFormat.format(ZFSLabelDate)); 
        System.out.println(String.format("%08X: ",nu+0x60)+"Checksum:");
        System.out.println(String.format("%08X: ",nu+0x34)+"psize (Phisical block size) = " + 
        		dn_blkptr.psize + " or 0x" + String.format("%02X",dn_blkptr.psize));
        System.out.println(String.format("%08X: ",nu+0x36)+"lsize (Logical block size) = " + 
        		dn_blkptr.lsize +" 0x" + String.format("%02X",dn_blkptr.lsize));   
        System.out.println(String.format("%08X: ",nu+0x33)+ "comp = " + 
        		dn_blkptr.compress.Num +". "+dn_blkptr.compress.Descriptor+" or "+ 
        		dn_blkptr.compress.Algorithm);
	}
	 
}


