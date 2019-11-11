package hdd;

import org.apache.log4j.Logger;

import tools.VarTools;

public class Partition {
	
	private static final Logger log = Logger.getLogger(Partition.class.getName()); 

	private int Num;
	private int offset;
	private boolean isActive;
	public  boolean isFree = false;
	private HSC HSCStart;
	private HSC HSCEnd;
	private String Offset1Sector;
	private String SectorCount;
	private int PartNum;
	private String PartName;

	public Partition(byte[] b, int PartOffset, int nPart) {
		
		log.trace("Partition");
		Num = nPart;
		offset = PartOffset;
	    byte n = b[offset+0x00]; // 80h — раздел является активным, 00h — неактивным
	    isActive = (n==-1);
	    isFree   = (b[offset+0x04]==0);
	    HSCStart = new HSC(b[offset+0x01],b[offset+0x02],b[offset+0x03],true); 
		PartNum  = b[offset+0x04]+0x100;
	    HSCEnd   = new HSC(b[offset+0x05],b[offset+0x06],b[offset+0x07],false);
	    Offset1Sector = VarTools.ByteArray2HexsStr(b, offset+0x08,4);
	    SectorCount   = VarTools.ByteArray2HexsStr(b, offset+0x0C,4);
		PartName = (new PartitionType()).GetPartitionName(PartNum);
	}

	public void Print() {

	    log.trace("Partition "+Num+":");
	    log.trace("Active  = "+ (isActive ? "yes" : "no")); 
	    log.trace((isFree ? "Free" : "Data"));
	    HSCStart.Print();
	    HSCEnd.Print();
	    log.trace("1st sector offset = 0x" + Offset1Sector); 		
	    log.trace("Sectors = 0x" + SectorCount); 	
	    log.trace("Partition type code = "+PartName+" (0x"+String.format("%02X", PartNum)+")"); 
	}	
}
