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
	
	public int  nActiveUber 	 = -1;
	public int  nActiveUberCount = -1;
	public long nActiveUberTxg	 = -1;

	public int  nMinUber		 = -1;
	public int  nMinUberCount	 = -1;
	public long nMinUberTxg		 = Long.MAX_VALUE;
	public ArrayList<Long> txgs  = new ArrayList<Long>();  
	
	public ZFSUberBlocks() {
		
		log.trace("ZFSUberBlocks");
	}
	
	public void Pack(byte[] bs) {
		
		for (int i = 0; i<UberBlocks.length; i++) {			
			
			log.info("Uber raw bytes = 0x" + String.format("%08X",cZFSUberArrayBegin));
			UberBlocks[i] = new ZFSUberBlock(bs,cZFSUberArrayBegin,i);
			long uu = UberBlocks[i].ub_txg;
			// max txg
			if (nActiveUber<0) {
				log.debug("New active (max) txg = " + uu);
				nActiveUber = 0;
				nActiveUberCount = 1;
				nActiveUberTxg= uu;
			} else {
				if (nActiveUberTxg<uu) {		
					log.debug("New active (max) txg = " + uu);
					nActiveUber    		= i;
					nActiveUberTxg 		= (int)uu;
					nActiveUberCount	= 0;
				};
				if (nActiveUberTxg==uu) {
					nActiveUberCount++;
					if (nActiveUberCount>1)
						log.debug("Txg active (max) count > 1 at uberblock = " + uu);
				}
			}
			if (nMinUber==-1) { 
				if (uu>0) {
					log.debug("New min txg = " + uu);
					nMinUber = 0;
					nMinUberCount = 1;
					nMinUberTxg= uu;
				}
			} else { 
				if (nMinUberTxg>uu) {		
					if (uu>0) {
						log.info("New min txg = " + uu);	
						nMinUber    	= i;
						nMinUberTxg 	= (int)uu;
						nMinUberCount 	= 0;
					}
				} 
				if (nMinUberTxg==uu) {
						nMinUberCount++;
						if (nMinUberCount>1)
							log.debug("Txg min count > 1 at uberblock = " + uu);
				}
			}
			if (uu>0)
				if (!txgs.contains(uu))
					txgs.add(uu);
			UberBlocks[i].Print();
		} 
		Collections.sort(txgs);
	}
	
	public void PrintActive() {

		long n = UberBlocks[nActiveUber].UberOffsetAbs; 	
		log.info("---Active UberBlock----------------"); 
		log.info("Active UberBlock is = " + nActiveUber +". Txg = "+ 
				nActiveUberTxg + ". Offset = 0x"+String.format("%08X",n)); 	
		log.info("Array of Txgs size = " +txgs.size()); 
		log.info("Active (count) = " + nActiveUberTxg + ", count = " + nActiveUberCount);
		log.info("Min = " + nMinUberTxg + ", count = " + nMinUberCount);
		UberBlocks[nActiveUber].Print();	
		log.info("-----------------------------------"); 
	}
	
}
