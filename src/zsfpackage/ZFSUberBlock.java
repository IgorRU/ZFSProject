package zsfpackage;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import hdd.Block;
import tools.PrintTools;
import tools.VarTools;

public class ZFSUberBlock extends Block {
	
	private static final Logger log = Logger.getLogger(ZFSUberBlock.class.getName()); 
	
	public Date ZFSLabelDate;
	private SimpleDateFormat dateFormat = 
			new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");	
	
	public  int offset;
	public  long UberOffsetAbs;
	public  int nUberBlock;
	public  String ub_magic;
	public  String ub_magicDecription;
	public  boolean isMagic = false;
	public  String ub_version;
	private long ub_versioni; 
	public  long ub_txg;
	public  String ub_guid_sum; 
	
	public DNblkptr dn_blkptr = new DNblkptr();
	private BigInteger ub_guid_sum_i;

	public ZFSUberBlock(byte[] bs, int UberOffset, int nUber) {
		
		nUberBlock = nUber;   
		UberOffsetAbs = nUberBlock * 1024;
		offset = (int)(UberOffset + UberOffsetAbs);
		log.debug("ZFSUber is " + nUberBlock + " offset = " + String.format("%08X",offset)+ 
				" byte[] size = " + String.format("%08X",bs.length)); 
		if (NoMagic(VarTools.ByteArray2HexsStr(bs,offset,4)))
			return;		
		log.trace("UberBlock is "+nUberBlock+" offset = "+String.format("%05X",offset)); 
		PrintTools.Dump(bs, offset, 0x80);
		braw = VarTools.byteArray2byteArrayShort(bs, offset, 0x400);
		ub_version  = VarTools.ByteArray2HexsStr(bs,offset+0x08,8);
		ub_versioni = VarTools.ByteArray2Long1(bs,offset+0x08,8);
		ub_txg      = VarTools.ByteArray2Long1(bs,offset+0x10,8); 
		log.debug(" txg =  " + ub_txg + " or 0x" + String.format("%5X",ub_txg));
		ub_guid_sum = VarTools.ByteArray2HexsStr(bs,offset+0x18,8);
		ub_guid_sum_i = VarTools.ByteArray2BigInt(bs, offset+0x18);
		ZFSLabelDate = GetDate2Long(bs, offset+0x20); 
        dn_blkptr = new DNblkptr();  
        dn_blkptr.Pack(bs,offset + 0x28);    
		isMagic = true;
		Print(); 
	}

	private Date GetDate2Long(byte[] bs, int off) { 
		
		long dl = VarTools.ByteArray2Long1(bs, off, 8)*1000;   
		return (new Date(dl));
	}

	private boolean NoMagic(String hex) {
		
		ub_magic = hex;
		if (ub_magic.equals(ZfsConst.UBMagicLE)) {
			ub_magicDecription = ZfsConst.UBMagicLEDesc;
			return false;			
		}
		if (ub_magic.equals(ZfsConst.UBMagicBE)) {
			ub_magicDecription = ZfsConst.UBMagicBEDesc;
			return false;			
		}
		ub_magicDecription = ZfsConst.UBMagicNo;
		log.debug("UberBlock = "+UberOffsetAbs+". "+ub_magicDecription);
		return true;
	}

	public void Print() {
 
		if (!isMagic) {
			log.debug("Uber block is null."); 
			return;
		}
		log.debug("ub_magic = 0x"+ub_magic+" is "+ub_magicDecription); 
		log.debug("ub_version = 0x" + ub_version + " or "+ub_versioni); 
		log.debug("ub_txg = 0x" + String.format("%08X",ub_txg) +" or "+ ub_txg); 
		log.debug("ub_guid_sum = 0x"  + ub_guid_sum + " or " + ub_guid_sum_i); 
		dn_blkptr.Print();
		log.debug("date = " + dateFormat.format(ZFSLabelDate)); 
		log.debug(String.format("%08X: ",offset+0x60)+"Checksum:");
		log.debug(String.format("%08X: ",offset+0x34)+"psize (Phisical block size) = " + 
        		dn_blkptr.psize + " or 0x" + String.format("%02X",dn_blkptr.psize));
		log.debug(String.format("%08X: ",offset+0x36)+"lsize (Logical block size) = " + 
        		dn_blkptr.lsize +" 0x" + String.format("%02X",dn_blkptr.lsize));   
		log.debug(String.format("%08X: ",offset+0x33)+ "comp = " + 
        		dn_blkptr.compress.Num +". "+dn_blkptr.compress.Descriptor+" or "+ 
        		dn_blkptr.compress.Algorithm);
		if ((dn_blkptr.compress.Num<3)&&(dn_blkptr.psize!=dn_blkptr.lsize))
			log.error("ZFSUber is " + nUberBlock + " offset = " + String.format("%08X",offset) +
					" no compressed. " + "psize (Phisical block size) = " + dn_blkptr.psize +
					" 0x" + String.format("%02X",dn_blkptr.psize) + " not equal "+ 
					"lsize (Logical block size) = " + dn_blkptr.lsize +
					" 0x" + String.format("%02X",dn_blkptr.lsize));   
	} 
	 
}


