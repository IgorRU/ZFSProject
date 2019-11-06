package zsfpackage;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

public class ZFSUberBlocks {
	
	private static final Logger log = Logger.getLogger(ZFSUberBlocks.class.getName()); 

	static int SectorLength = 512; 
	static int cZFSUberArrayBegin  = 256*SectorLength;

	private static int UberBlocksInLabel = 128;

	public ZFSUberBlock[] UberBlocks = new ZFSUberBlock[UberBlocksInLabel];
	
	public int nActiveUber 		= -1;
	public int nActiveUberCount = -1;
	public long nActiveUberTxg	= -1;

	public int nMinUber			= -1;
	public int nMinUberCount	= -1;
	public long nMinUberTxg		= Long.MAX_VALUE;
	public ArrayList<Long> txgs = new ArrayList<Long>(); // список транзакций
	
	public ZFSUberBlocks() {
		
		log.trace("ZFSUberBlocks");
	}
	
	public void Pack(byte[] bs) {
		for (int i = 0; i<UberBlocks.length; i++) {			
			UberBlocks[i] = new ZFSUberBlock(bs,cZFSUberArrayBegin,i);
			long uu = UberBlocks[i].ub_txg;
			// max txg
			if (i==0) {
				nActiveUber = 0;
				nActiveUberCount = 1;
				nActiveUberTxg= uu;
			} else {
				if (nActiveUberTxg<uu) {			
					nActiveUber    		= i;
					nActiveUberTxg 		= (int)uu;
					nActiveUberCount	= 0;
				};
				if (nActiveUberTxg==uu)
					nActiveUberCount++;
			}
			// mix txg
			if ((nMinUber==-1)&&(uu>0)) { 
				nMinUber = 0;
				nMinUberCount = 1;
				nMinUberTxg= uu;
			} else {
				if ((nMinUberTxg>uu)&&(uu>0)) {			
					nMinUber    	= i;
					nMinUberTxg 	= (int)uu;
					nMinUberCount 	= 0;
				} 
				if (nMinUberTxg==uu)
						nMinUberCount++;
			}
			//if (UberArray[i].isMagic)
			//	System.out.println(String.format("%08X: ",UberArray[i].nu)+" "+UberArray[i].nUberBlock+" "+UberArray[i].btype.Descriptor);
			if (uu>0)
				if (!txgs.contains(uu))
					txgs.add(uu);
			UberBlocks[i].Print(false);
		} 
		Collections.sort(txgs);
	}
	
	public void PrintActive(boolean isPrint) {

		if (!isPrint)
			return;
		long n = UberBlocks[nActiveUber].UberOffsetAbs; 
		System.out.println("Active UberBlock is = " + nActiveUber +". Txg = "+ 
				nActiveUberTxg + ". Offset = 0x"+String.format("%08X",n)); 
		System.out.println("Active UberBlocks with Txg = "+ nActiveUberTxg+" is " + 
				nActiveUberCount);		
		System.out.println("txgs count = " +txgs.size()+ ", min = " + nMinUberTxg +
				", active (max) = " + nActiveUberTxg+" 0x"+String.format("%4X",nActiveUberTxg));
		System.out.println("min count = " + nMinUberCount + ", active (max) count = " + 
				nActiveUberCount);
		UberBlocks[nActiveUber].Print(true);
	}
	
}
