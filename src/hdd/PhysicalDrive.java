package hdd;

import java.io.File;

import org.apache.log4j.Logger;

import tools.FileTools;
import tools.LZJBjava;
//import tools.LZJBjava;
import tools.PrintTools;
import zsfpackage.DNblkptr;
import zsfpackage.DNodePhys;
import zsfpackage.ZFSLabel;
import zsfpackage.ZFSPartition;
import zsfpackage.ZFSUberBlock;

public class PhysicalDrive {	

	private static final Logger log = Logger.getLogger(PhysicalDrive.class.getName()); 
	
	static int  BIOSBootStart = 0; 
	static int  BIOSBootEnd = 33; 
	static int  ZFSPartionStart = 1050624; 
	static long ZFSL0Start = ZFSPartionStart;
	static long ZFSL0NameValueStart = ZFSL0Start+32;
	
	static int SectorLength = 512; 
	static int ZFSLabelSize = SectorLength*1024*512; //   256k
	public String PhysicalDrivePath;
	public GPT gptpd = new GPT();
	private int LabelBest = 2;

	private long LBAStart3Offset = 1050624L*SectorLength;	 //  0x000100800
	private long LBAStart3End    = 5860533134L*SectorLength; //  0x15D50A38E		
	
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
		gptpd.Print();
		// Работа с разделом #3 (zfs)		
		ZFSStart = (isDrive ? LBAStart3Offset : 0);		 
		ZFSEnd   = (isDrive ? LBAStart3End    : FileTools.GetFileSize(path));
		FileTools.SetOffset(gptpd.fc, ZFSStart);
		byte[] bs = FileTools.GetBytes(gptpd.raf,gptpd.fc, ZFSLabelSize);
		zfs3.Pack(ZFSStart, ZFSEnd, bs); 
		//zfs3.Print();
	}

	public void ViewActiveBlock() {
		
		log.info("=== Вывод активного блока ZFSUberBlock");		
		ZFSLabel L = zfs3.L[LabelBest];
		int n = L.Ubers.nActiveUber;
		log.info("zfs3.L["+LabelBest+"].nActiveUber: " + n );
		ZFSUberBlock u = L.Ubers.UberBlocks[n];
		u.Print();
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
		System.out.println("=== получение MOS ==========================================");	
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
			FileTools.SetOffset(gptpd.fc,adr);
			//int sectors = b.psize+1; - в диске
			int sectors = b.lsize+1;
			byte[] bbb = FileTools.GetBytes(gptpd.raf,gptpd.fc, sectors*SectorLength); // 4k = MOS size
			String sF = sDirBlockWrite+"Blocks/"+String.format("%020X",adr)+"_"+b.txgLogic+".dat";
			FileTools.WriteBlock(sF, bbb, sectors*SectorLength);
			String alg = b.compress.Algorithm;
			System.out.println("compression = " + alg + ", checksum = " + b.cksum.Algorothm); 	
			//if (alg.equals("lzjb"))
			//	FileTools.WriteBlock(sDirBlockWrite+String.format("%020X",adr)+"_"+b.txgLogic+" decompress.dat", 
			//			LZJBjava.decompress(bbb), sectors*SectorLength);						
		}
		for (int i=0; i<3; i++) {
			System.out.println(i+" =============================================");
			long root_off = ZFSStart + u.dn_blkptr.address[i];			
			//--- no use PrintTools.Print10andHex("u.dn_blkptr.address[i]","%08X",u.dn_blkptr.address[i]);		
			PrintTools.Print10andHex("root_off","%08X",root_off);
			FileTools.SetOffset(gptpd.fc, root_off);
			int sectors = (u.dn_blkptr.psize > u.dn_blkptr.lsize ? u.dn_blkptr.psize : u.dn_blkptr.lsize) + 1;
			sectors = (sectors<4 ? 4 : sectors);
			byte[] bbs = FileTools.GetBytes(gptpd.raf, gptpd.fc, sectors*SectorLength);
			DNodePhys dn_root = new DNodePhys();
			dn_root.Pack(bbs, 0, sectors*SectorLength);
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
						FileTools.WriteBlock(sFile, bb, len*SectorLength);	
						//byte[] bout = LZ4java.lz4DecompressSlow(bb);
						//System.out.println("bout len"+bout.length);	 
						
						if (ext.equals("lzjb")) {
							byte[] bdecomp = LZJBjava.decompress(bb);
							String sFF = sDirBlockWrite+String.format("%020X",ext)+"_"+b.txgLogic+" decompress.dat";
							FileTools.WriteBlock(sFF, bdecomp, len*SectorLength);		
						}
						addr = addr+LBAStart3Offset;	
						PrintTools.Print10andHex("addr","%08X",addr);
						FileTools.SetOffset(gptpd.fc, addr);
						byte[] bbb = FileTools.GetBytes(gptpd.raf,gptpd.fc, len*SectorLength);
						String sFile2 = sDirBlockWrite + String.format("%020X", addr)+"_"+dn1.txgLogic +"_MOS+LBA." + ext;
						FileTools.WriteBlock(sFile2, bbb, len*SectorLength);	
						//byte[] bout2 = LZ4java.lz4DecompressSlow(bb);
						//System.out.println("bout2.len"+bout2.length);
					}
				}
			}
		}		
	}

	public void PrintZDB() {

		for (int i=0; i<4; i++) { 
			String sFile = (isDrive ? "" : (new File(PhysicalDrivePath)).getName() );
			sFile = sDirBlockWrite + sFile + "." + zfs3.L[i].ZFSLabel + ".zdb.txt";
			zfs3.L[i].PrintZDB2File(sFile);
		}
		
	}		

}
