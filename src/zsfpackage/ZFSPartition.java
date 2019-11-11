package zsfpackage;

import org.apache.log4j.Logger;

import tools.PrintTools;

public class ZFSPartition {
	
	private static final Logger log = Logger.getLogger(ZFSPartition.class.getName()); 

	public long ZFSPartitionStart   = -1;  // 1050624   
	public long ZFSPartitionEnd     = -1;  //5860533134L; 
	public long ZFSSize;
	public ZFSLabel[] L = new ZFSLabel[4]; // L0 = 0   ... 256 kb
	// ZFS Partition
	// L0+L1 = 0 ... 512 kb 
	// L2 = N-512 ... N-256 kb
	// L3 = N-256 ... N kb 
	static int cZFSLabelSize = 512*ZfsConst.SectorLength;
	public long ZFSLabelStart[] = {ZfsConst.ZFSL0Start, ZfsConst.ZFSL1Start, ZFSPartitionEnd-cZFSLabelSize*2, ZFSPartitionEnd-cZFSLabelSize};
	
	public ZFSPartition() {
		
		log.info("ZFSPartition constructor");
	}
		
	public void Pack(long LBAStart, long LBAEnd, byte[] bs) {

		ZFSPartitionStart = LBAStart;
		ZFSPartitionEnd   = LBAEnd; 
		this.Print();						
		for (int l = 0; l<4; l++) {
			L[l] = new ZFSLabel();
			L[l].Pack("L"+String.valueOf(l), bs, (int)(ZFSLabelStart[l])); //
			L[l].Print();
		}
		PrintTools.Print10andHex("ZFSDataAfteBootStart", "%08X", ZfsConst.ZFSDataAfteBootStart);
		PrintTools.Print10andHex("End   LBA", "%08X", ZFSPartitionEnd);
		log.info(" or "+ ZFSPartitionEnd/ZfsConst.SectorLength);		
	} 

	public void Print() {

		log.info("\nZFS Partition");
		log.info("Start LBA = 0x" + String.format("%010X",ZFSPartitionStart) + 
				" or " + ZFSPartitionStart/ZfsConst.SectorLength + " sectors");
		log.info("End   LBA = 0x" + String.format("%010X",ZFSPartitionEnd) + 
				" or " + ZFSPartitionEnd/ZfsConst.SectorLength + " sectors");
		ZFSSize = ZFSPartitionEnd-ZFSPartitionStart;
		log.info("Size = " + ZFSSize/512+" sectors or bytes:");
		log.info("  0x"+ String.format("%08X",ZFSSize)+"  or "+ (ZFSSize)+" b  or "+ 
				(ZFSSize/1024)+" kb  or "+ (ZFSSize/1024/1024)+" Mb  or "+  
				(ZFSSize/1024/1024/1024)+" Gb"); 
	}

}
