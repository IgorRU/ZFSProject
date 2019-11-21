package hdd;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;

import tools.FileTools;
import tools.LZ4java;
import tools.LZJBjava;
import tools.PrintTools;
import zsfpackage.DNblkptr;
import zsfpackage.DNodePhys;
import zsfpackage.ZFSLabel;
import zsfpackage.ZFSPartition;
import zsfpackage.ZFSUberBlock;
import zsfpackage.ZfsConst;

public class PhysicalDrive {	

	private static final Logger log = Logger.getLogger(PhysicalDrive.class.getName()); 
	
	static int  BIOSBootStart = 0; 
	static int  BIOSBootEnd = 33; 
	static int  ZFSPartionStart = 1050624; 
	static long ZFSL0Start = ZFSPartionStart;
	static long ZFSL0NameValueStart = ZFSL0Start+32;
	
	static int ZFSLabelSize = ZfsConst.SectorLength*1024*512; //   256k
	public String PhysicalDrivePath;
	public GPT gptpd = new GPT();
	private int LabelBest = 2;

	private long LBAStart3Offset = 1050624L*ZfsConst.SectorLength;	  //  0x000100800
	private long LBAStart3End    = 5860533134L*ZfsConst.SectorLength; //  0x15D50A38E		
	
	public boolean isPrint = false;
	
	public  ZFSPartition zfs3 = new ZFSPartition();
	private boolean isDrive;
	private long ZFSStart;
	private long ZFSEnd;
	public  String LogFile;

	private String sDirBlockWrite = "k:/zfs/Blocks/";
	
	public PhysicalDrive(String path, boolean isDisk) {	

		isDrive = isDisk;
		PhysicalDrivePath = path;
		LogFile = PhysicalDrivePath;
		if (!isDrive)
			sDirBlockWrite = FileTools.GetBlockDir(path);
		gptpd.Pack(PhysicalDrivePath, isDrive);	 
		//gptpd.Print();
		// Use partition #3 (zfs)		
		ZFSStart = (isDrive ? LBAStart3Offset : 0);		 
		ZFSEnd   = (isDrive ? LBAStart3End    : FileTools.GetFileSize(path));
		FileTools.SetOffset(gptpd.fc, ZFSStart);
		byte[] bs = FileTools.GetBytes(gptpd.raf,gptpd.fc, ZFSLabelSize);
		zfs3.Pack(ZFSStart, ZFSEnd, bs); 
		//zfs3.Print();
	}

	public void ViewActiveBlock() {
		
		log.info("=== Active block ===");		
		ZFSLabel L = zfs3.L[LabelBest];
		int n = L.Ubers.nActiveUber;
		log.info("zfs3.L["+LabelBest+"].nActiveUber: " + n );
		ZFSUberBlock u = L.Ubers.UberBlocks[n];
		u.Print();
		String sF = sDirBlockWrite+"Blocks/"+String.format("%020X",u.offset)+"_"+
				u.ub_txg+".dat";
		u.WriteBlock(sF);			
		GetMOS(u);
		//for (int i =0; i<256; i++ )  
		//	if (L.UberArray[i].ub_txg>0)
		//		PrintTools.Print10andHex("ub_txg", "%08X",L.UberArray[i].ub_txg); 
		for (int i = 0; i < L.Ubers.UberBlocks.length; i++) 
			if (L.Ubers.UberBlocks[i].ub_txg>0) {
				PrintTools.Print10andHex("i", "%08X",i);				
				GetMOS(L.Ubers.UberBlocks[i]);
			}
	}
	
	private void GetMOS(ZFSUberBlock u) {
		System.out.println("=== MOS ===");	
		DNblkptr b = u.dn_blkptr;	
		if (b.Elong==0) {
			log.error("MOS is 0");
			return;
		}
		PrintTools.Print10andHex("Elong", "%08X",b.Elong);
		PrintTools.Print10andHex("ElongN","%08X",b.ElongN);
		PrintTools.Print10andHex("psize", "%08X",b.psize);
		PrintTools.Print10andHex("lsize", "%08X",b.lsize);
		System.out.println("Read adresses:");
		for (int i=0; i<3; i++) {
			long adr = ZFSStart + b.address[i];
			PrintTools.Print10andHex("addr["+i+"]","%08X",adr);	    
			FileTools.SetOffset(gptpd.fc, adr);
			int sectors = b.psize+1;
			int size = sectors*ZfsConst.SectorLength;
			// read ub_rootbp
			byte[] bbb = FileTools.GetBytes(gptpd.raf, gptpd.fc, size); // 4k = MOS size
			String alg = b.compress.Algorithm;
			log.info("compression = " + alg + ", checksum = " + b.cksum.ObjsetType); 
			byte[] bout = null;
			String sF = sDirBlockWrite+"Blocks/"+String.format("%020X",adr)+"_"+b.txgLogic;
			if (alg.equals("lzjb")) {
				FileTools.WriteBlock_(sF+".lzjb", bbb, size);	
				bout = LZJBjava.decompress(bbb);	
				log.debug("bout len"+bout.length);
				sF = sF+" decomp.dat";	 	
			}  else
				if (alg.equals("lz4")) {
					FileTools.WriteBlock_(sF+".lz4", bbb, size);	
					bout = LZ4java.lz4DecompressSlow(bbb);
					log.debug("bout len"+bout.length); 	
				}  else  {
					sF = sF+".dat"; 
					bout = bbb.clone();
					}
			log.info("------------------------------------------------------------");
			DNodePhys dn_root = new DNodePhys();
			// Analize dnode_phys	
			dn_root.Pack(bout, 0, size);
			dn_root.WriteBlock(sF);
			dn_root.Print(true);
			boolean isMOSOk = false;
			byte[] b0 = dn_root.dn_blkptr[0].braw;
			if (dn_root.dn_blkptr[1].isNull||dn_root.dn_blkptr[2].isNull) {
				log.info("dn_root.dn_blkptr[1..2] is null.");
				isMOSOk = true;
			} else {				
				byte[] b1 = dn_root.dn_blkptr[1].braw;
				byte[] b2 = dn_root.dn_blkptr[2].braw;
				if ((!Arrays.equals(b0, b1))||(!Arrays.equals(b0, b2)))
					log.error("Raw arrays dn_root.dn_blkptr[0..2] not equal.");
				else {
					isMOSOk = true;					
				}
			} 		
			if (isMOSOk) {
				alg =  dn_root.dn_blkptr[0].compress.Algorithm; 
				log.debug("dn_root compress is "+alg); 	
				DNblkptr dn = dn_root.dn_blkptr[0];
				sectors = dn.psize+1;
				size = sectors*ZfsConst.SectorLength;
				for (int ii=0; ii<3; ii++)
					log.info("address["+ii+"]="+String.format("%020X",dn.address[ii]));
				FileTools.SetOffset(gptpd.fc, dn.address[0]);
				byte[] bs = FileTools.GetBytes(gptpd.raf,gptpd.fc, size);
				log.debug("bs len is "+bs.length); 				
				sF = sDirBlockWrite+"Blocks/"+String.format("%020X",adr)+"_dn_root."+alg;				
				if (alg.equals("lz4")) { 
					FileTools.WriteBlock_(sF, bs, size);	
					FileTools.CheckLZ4Block(bs,size);
					//bout = LZ4java.lz4DecompressSlow(b0);	
					bout = LZ4java.lz4DecompressFileFromStream(sF, size);
					log.debug("bout len"+bout.length); 
					PrintTools.Dump(bout, 0, bout.length);						
				}  
			} else {
				log.error("Bad MOS format");
			}
			log.info("dn_root end.");			
		}
		/*
		for (int i=0; i<3; i++) {
			System.out.println(i+" =============================================");
			long root_off = ZFSStart + u.dn_blkptr.address[i];			
			//--- no use PrintTools.Print10andHex("u.dn_blkptr.address[i]","%08X",u.dn_blkptr.address[i]);		
			PrintTools.Print10andHex("root_off","%08X",root_off);
			FileTools.SetOffset(gptpd.fc, root_off);
			int sectors = (u.dn_blkptr.psize > u.dn_blkptr.lsize ? u.dn_blkptr.psize : u.dn_blkptr.lsize) + 1;
			sectors = (sectors<4 ? 4 : sectors);
			byte[] bbs = FileTools.GetBytes(gptpd.raf, gptpd.fc, sectors*ZfsConst.SectorLength);
			DNodePhys dn_root = new DNodePhys();
			dn_root.Pack(bbs, 0, sectors*ZfsConst.SectorLength);
			String sF = sDirBlockWrite+"Blocks/dn_root "+String.format("%016X",root_off)+" "+
					String.format("%04X",u.nUberBlock)+".dat";
			dn_root.WriteBlock(sF);
			dn_root.Print(false);
			for (int j=0; j<3; j++) {
				DNblkptr dn1 = dn_root.dn_blkptr[j];
				if (dn1.isNull)
					System.out.println("dn_root.dn_blkptr["+j+"] is null");
				else {		
					int len  = dn1.psize+1;						
					String ext = dn1.compress.Algorithm;
					for (int k=0; k<3; k++) {
						long addr = dn1.address[k];	
						if ((addr>ZFSEnd)||(addr<0))
							continue;
						PrintTools.Print10andHex("addr","%08X",addr); 
						FileTools.SetOffset(gptpd.fc, addr);
						if ((len*2>ZFSEnd)||(len<0))
							continue;						
						byte[] bb = FileTools.GetBytes(gptpd.raf,gptpd.fc, len*512);
						String sFile = sDirBlockWrite+ String.format("%020X", addr)+"_"+dn1.txgLogic +"_MOS." + ext;
						FileTools.WriteBlock_(sFile, bb, len*ZfsConst.SectorLength);	
						byte[] bout = LZ4java.lz4DecompressSlow(bb);
						System.out.println("bout len"+bout.length);	
						/*
						if (ext.equals("lzjb")) {
							byte[] bdecomp = LZJBjava.decompress(bb);
							String sFF = sDirBlockWrite+String.format("%020X",ext)+"_"+b.txgLogic+" decompress.dat";
							FileTools.WriteBlock(sFF, bdecomp, len*ZfsConst.SectorLength);		
						}
						*/
		/*
						addr = addr+LBAStart3Offset;	
						PrintTools.Print10andHex("addr","%08X",addr);
						FileTools.SetOffset(gptpd.fc, addr);
						byte[] bbb = FileTools.GetBytes(gptpd.raf,gptpd.fc, len*ZfsConst.SectorLength);
						String sFile2 = sDirBlockWrite + String.format("%020X", addr)+"_"+dn1.txgLogic +"_MOS+LBA." + ext;
						FileTools.WriteBlock_(sFile2, bbb, len*ZfsConst.SectorLength);	
						//byte[] bout2 = LZ4java.lz4DecompressSlow(bb);
						//System.out.println("bout2.len"+bout2.length);
					}
				}
			}
			
		}	*/	
	}

	public void PrintZDB() {

		for (int i=0; i<4; i++) { 
			String sFile = (isDrive ? "" : (new File(PhysicalDrivePath)).getName() );
			sFile = sDirBlockWrite + sFile + "." + zfs3.L[i].ZFSLabel + ".zdb.txt";
			zfs3.L[i].PrintZDB2File(sFile);
		}
		
	}		

}
