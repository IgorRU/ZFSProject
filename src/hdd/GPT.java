package hdd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import tools.FileTools;
import tools.PrintTools;
import tools.VarTools;

public class GPT {

	static int GPTStart     = 0; 
	static int ProtectiveMBRStart     = 0;
	static int PartitionEntriesStart = 2;
	static int GPTEnd       = 33; 
	static int SectorLength = 512; 
	
	private byte[] ProtectiveMBR = new byte[512];    			// LBA 0	
	static int[] PartitionOffset = new int[]{0x01BE, 0x01CE, 0x01DE, 0x01EE};	 	
	static int MBREnd = 0x01FE;
	
	// Primary GPT	
	static int PrimaryGPTHeaderStart = 1; 						// LBA 1 
	//private byte[] PrimaryGPTHeader = new byte[512]; 			// LBA 1 
	private byte[] PartitionEntries = new byte[512*(GPTEnd-1)]; // LBA 2...33	
	// Secondary GPT
	//private byte[] PartitionEntriesSecondary; // LBA -2...-33
	//private byte[] PrimaryGPTHeaderSecondary; // LBA -1	//
	
	// work
	public RandomAccessFile raf = null;
	public FileChannel fc = null; 
	private String partGUID;
	private String sPartpartGUIDName;
	private String partGUIDUniq;
	private String sPartpartGUIDUniqName;
	private byte FirstLBA;
	private byte LastLBA;
	private Partition[] Parts = new Partition[4]; 
	private boolean isMBROk;
	private String sMBREnd;
	private GPTHeader GPTH;
	private boolean isDrive;
	
	public GPT() {
		
	}
	public void Pack(String PhysicalDrivePath, boolean isDrive) {
		
		System.out.println((isDrive ? "PhysicalDrive GPT: " : "FileImage: ")+
				PhysicalDrivePath+"\n");	
		this.isDrive = isDrive;
		try {
			raf = new RandomAccessFile(PhysicalDrivePath, "r");
			fc = raf.getChannel();
			if (isDrive) {
				ProtectiveMBR=FileTools.GetBytes(raf, fc, SectorLength);
				GPTH = new GPTHeader(raf, fc, PrimaryGPTHeaderStart*SectorLength);
				PartitionEntries=FileTools.GetBytes(raf, fc, 
						PartitionEntries.length*SectorLength);						
				partGUID = VarTools.byteArray2GUID(PartitionEntries,0);
				sPartpartGUIDName = (new PartitionType()).GetPartitionGUID(partGUID);
				partGUIDUniq = VarTools.byteArray2GUID(PartitionEntries,16);
				sPartpartGUIDUniqName = (new PartitionType()).GetPartitionGUID(partGUID);		
			    FirstLBA = PartitionEntries[32];	    	
			    LastLBA  = PartitionEntries[40]; 
				for (int i=0; i<4; i++) 
					Parts[i] = new Partition(PartitionEntries, PartitionOffset[i], i);
				CheckMBR();						
			} else {
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	private void CheckMBR() {
	    sMBREnd = String.format("%02X", ProtectiveMBR[MBREnd])+
	    		  String.format("%02X", ProtectiveMBR[MBREnd+1]);
		isMBROk = ((ProtectiveMBR[MBREnd]==0x55)&&(ProtectiveMBR[MBREnd+1]==0xAA-256));		
	}

	public void Print(boolean isPrintDump) {
 
		if (!isPrintDump)
			return;
		if (isDrive) {		
			System.out.println("MBR Info:");
			System.out.println("Length= "+ProtectiveMBR.length);			
			PrintPartitionEntries(isPrintDump);
			PrintTools.Dump(ProtectiveMBR, 0, ProtectiveMBR.length, isPrintDump);
			GPTH.Print(isPrintDump);		 
			for (int i=0; i<4; i++) {
				Parts[i].Print();
			}
	    	System.out.println("\nMBR is " + (isMBROk ? "Ok." : "Bad." ));
		} else {
			System.out.println("FileImage info:");			
			try {
				System.out.println("Length (raf) = "+(raf.length()/1024/1024)+"Mb");
				System.out.println("Length (fc)  = "+(fc.size()/1024/1024)+"Mb");
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
	}
	
	public void PrintPartitionEntries(boolean isPrintDump) {
		
		System.out.println("\nPartitionEntries info:");
		System.out.println("Length = "+PartitionEntries.length);
		PrintTools.Dump(PartitionEntries, 0, 512, isPrintDump); 
	    System.out.println("partGUID = "+partGUID);
	    System.out.println("partGUIDName = "+sPartpartGUIDName);
	    System.out.println("partGUIDUniq = "+partGUIDUniq);
	    System.out.println("partGUIDUniqName = "+sPartpartGUIDUniqName);
		System.out.println("FirstLBA = " + FirstLBA);	
		System.out.println("LastLBA = "  + LastLBA);		
		System.out.println("MBREnd = "   + sMBREnd);			
	} 
}
