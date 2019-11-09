package hdd;

import java.io.RandomAccessFile; 
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import tools.FileTools;
import tools.PrintTools;
import tools.VarTools;

public class GPTHeader {
	
	private static final Logger log = Logger.getLogger(GPTHeader.class.getName()); 

	static  int  SectorLength = 512;
	private int  GPTHeaderSize = SectorLength;	
	private byte HeaderSize;
	private long CRC32zLib;
	private byte CurrentLBA;
	private int  BackupLBA;
	private int  CurrentLBAUseble;
	private int  BackupLBAUseble;
	private long StartingLBAPartitions;
	private int  NPartitions;
	private byte PartitionSize;
	private int  CRCzPart; 
	private String DiskGUID;
	private byte[] PrimaryGPTHeader = new byte[SectorLength];
	private String GPTSignature;
	
	public GPTHeader(RandomAccessFile raf, FileChannel fc, int primaryGPTHeaderStart) {

		log.trace("GPTHeader");
		FileTools.SetOffset(fc,primaryGPTHeaderStart);
		PrimaryGPTHeader=FileTools.GetBytes(raf, fc, SectorLength);		
		GPTSignature = (new String(PrimaryGPTHeader)).substring(0, 8);
		HeaderSize   = PrimaryGPTHeader[12];
		CRC32zLib    = VarTools.ByteArray2Int(PrimaryGPTHeader,16);	
		CurrentLBA   = PrimaryGPTHeader[24];
		BackupLBA    = VarTools.ByteArray2Int(PrimaryGPTHeader,32);	  
		CurrentLBAUseble  = VarTools.ByteArray2Int(PrimaryGPTHeader,40);
		BackupLBAUseble   = VarTools.ByteArray2Int(PrimaryGPTHeader,48);  
		DiskGUID = VarTools.byteArray2GUID(PrimaryGPTHeader,56);	
		StartingLBAPartitions = VarTools.ByteArray2Long(PrimaryGPTHeader,72,8);
		NPartitions   = VarTools.ByteArray2Int(PrimaryGPTHeader,80);  
		PartitionSize = PrimaryGPTHeader[84];
		CRCzPart = 	VarTools.ByteArray2Int(PrimaryGPTHeader,88);
	}
	
	public void Print() {
		
		System.out.println("\nGPTHeader info:");
		System.out.println("Length = " + GPTHeaderSize );
		System.out.println("GPTSignature= "+GPTSignature+
				(GPTSignature.equals("EFI PART") ? ". Ok." : ". Bad."));
		PrintTools.PrintByreArr2Hex("Revision (0x00000100 is 1.0 from May 2017)","%02X",PrimaryGPTHeader,8,4);
		PrintTools.Print10andHex("HeaderSize (92 bytes)","%02X",HeaderSize); 
		PrintTools.Print10andHex("CRC32zLib", "%02X",CRC32zLib);  
		PrintTools.Print10andHex("CurrentLBA","%04X",CurrentLBA); 
		PrintTools.Print10andHex("BackupLBA", "%04X",BackupLBA);  
		PrintTools.Print10andHex("CurrentLBAUseble", "%04X",CurrentLBAUseble);  
		PrintTools.Print10andHex("BackupLBAUseble",  "%04X",BackupLBAUseble);   
		System.out.println("Disk GUID = "+DiskGUID);	 
		System.out.println("NPartitions = "+NPartitions);
		PrintTools.Print10andHex("StartingLBAPartitions", "%04X",StartingLBAPartitions);   
		PrintTools.Print10andHex("PartitionSize", "%04X",PartitionSize);   
		PrintTools.Print10andHex("CRCzPart",      "%04X",CRCzPart);  
		PrintTools.Dump(PrimaryGPTHeader,92,GPTHeaderSize-92);
	}
		
}
