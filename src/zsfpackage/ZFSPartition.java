package zsfpackage;

import org.apache.log4j.Logger;

import tools.PrintTools;

public class ZFSPartition {
	
	private static final Logger log = Logger.getLogger(ZFSPartition.class.getName()); 

	static int  SectorLength = 512; 
	public long ZFSPartitionStart   = -1;  // 1050624   
	public long ZFSPartitionEnd     = -1;  //5860533134L; 
	public long ZFSSize;
	// ZFS Partition
	public long ZFSL0Start=0;
	public ZFSLabel[] L = new ZFSLabel[4]; // L0 = 0   ... 256 kb
	public long ZFSL1Start = 256*1024;
	// L1 = 256 ... 512 kb
	// data
	public long ZFSDataStart=512*1024;
	public long ZFSDataAfteBootStart=4*1024*1024;
	// L2 = N-512 ... N-256 kb
	// L3 = N-256 ... N kb 
	static int cZFSLabelSize = 512*SectorLength;
	public long ZFSLabelStart[] = {ZFSL0Start, ZFSL1Start, ZFSPartitionEnd-cZFSLabelSize*2, ZFSPartitionEnd-cZFSLabelSize};
	
	public ZFSPartition() {
		
		log.info("ZFSPartition");
	}
		
	public void Pack(long LBAStart, long LBAEnd, byte[] bs) {

		ZFSPartitionStart = LBAStart;
		ZFSPartitionEnd   = LBAEnd;
		//System.out.println("bs len = "+bs.length);
		this.Print();						
		for (int l = 0; l<4; l++) {
			L[l] = new ZFSLabel();
			L[l].Pack("L"+String.valueOf(l), bs,(int)(LBAStart+ZFSL0Start)); //
			L[l].Print();
		}
		PrintTools.Print10andHex("ZFSDataAfteBootStart", "%08X", ZFSDataAfteBootStart);
		PrintTools.Print10andHex("End   LBA", "%08X", ZFSPartitionEnd);
		System.out.println(" or "+ ZFSPartitionEnd/SectorLength);		
	} 

	public void Print() {

		System.out.println("\nZFS Partition");
		System.out.println("Start LBA = 0x" + String.format("%010X",ZFSPartitionStart) + 
				" or " + ZFSPartitionStart/SectorLength + " sectors");
		System.out.println("End   LBA = 0x" + String.format("%010X",ZFSPartitionEnd) + 
				" or " + ZFSPartitionEnd/SectorLength + " sectors");
		ZFSSize = ZFSPartitionEnd-ZFSPartitionStart;
		System.out.println("Size = 0x"+ String.format("%08X",ZFSSize)+" or "+ 
				ZFSSize/512+" sectors or "+ (ZFSSize/1024/1024/1024)+" Gb");
	}

}
