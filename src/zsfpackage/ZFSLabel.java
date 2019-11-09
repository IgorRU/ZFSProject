package zsfpackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import tools.VarTools;

public class ZFSLabel {
	
	private static final Logger log = Logger.getLogger(ZFSLabel.class.getName()); 

	static int SectorLength = 512; 
	public int ZFSLabelOffset;	 
	// ZFS Label format:
	// 0   ... 8k   - Blank space
	static int  cZFSBlankSpaceBegin = 0;
	// 8   ... 16k  - Boot header
	static int  cZFSBootHeaderBegin = 16*SectorLength;   
	// 16  ... 128k - Name = Value  
	static int  cZFSNameValueBegin  = 32*SectorLength;     
	// 128 ... 256k - UberArray
	public ZFSUberBlocks Ubers = new ZFSUberBlocks();
	//private Date ZFSLabelDate; 
	static int  cZFSUberArrayBegin  = 256*SectorLength;    
	static int  cZFSUberArrayEnd    = 512*SectorLength;  
	static int  cZFSLabelSize       = 256*1024;  		// 256 kb
	static int  cZFSUberBlockSize   = 2*SectorLength;   //  
	// начало чтения (байт)     
	//public long  cSecStart  = SecStartZFS + cZFSUberArrayBegin;
	//public long  cBytesStart = SectorLength * cSecStart;
	 // прочитать сектров
	//static int  cSectors    = cZFSUberblockSize;  // размер L0,L1,L2,L3
	//static int  cBytesRead  = SectorLength * cSectors;
	public  boolean isWrite2File = false;
	//public  String Write2FileName = "K:\\\\zfs\\";
	public  String ZFSLabel;
	private byte[] ZFSLabelData;
	
	final   String[] zdbKeys = new String[]  {"version", "name", "state", "txg", "pool_guid", 
			"errata", "hostid", "hostname", "top_guid", "guid", "vdev_children"};
	final   String[] zdbVdevKeys = new String[]  {"type", "id","guid", "path", "nparity", 
				"metaslab_array","metaslab_shift","ashift","asize","is_log","create_txg"};
	final   String[] zdbVdevChildKeys = new String[]  {"type", "id", "guid", "path",
				"whole_disk", "DTL","create_txg"};
	final   String[] zdbKeys2 = new String[]  {"features_for_read"};
	final   String[] zdbVdevKeys2 = new String[]  {"com.delphix:hole_birth",
				"com.delphix:embedded_data"};
	public  HashMap<String, String> zdb 			= new HashMap<>();	
	public  HashMap<String, String> zdbVdev 		= new HashMap<String, String>();	
	public  List<HashMap<String, String>> zdbVdevChild 	= new ArrayList<HashMap<String, String>>();	
	
	public ZFSLabel() {
	 
	}	
	
	public void Pack(String sZFSLabel, byte[] bs, int offset) {
		
		log.trace("Pack");
		ZFSLabel = sZFSLabel;
		//ZFSLabelData = CheckWriteFile(bs,true);
		ZFSLabelOffset = offset;
		ZFSLabelData = bs;		
		GetNameValue(ZFSLabelData);	
		Ubers.Pack(ZFSLabelData); 
		Ubers.PrintActive();
		PrintZDB(false);
	}	
	/*
	private byte[] CheckWriteFile(byte[] bs, boolean isWrite) {

		Write2FileName = Write2FileName+ZFSLabel+".img";
		File f = new File(Write2FileName);
		if (f.getAbsoluteFile().exists()) {
			ZFSLabelData = null;
			 try {
				 ZFSLabelData = Files.readAllBytes(f.toPath());
             } catch (IOException e) {
     			e.printStackTrace();
             }
			System.out.print("Read  "+ZFSLabel+" from "+Write2FileName+" "+
					ZFSLabelData.length/1024+" kbytes.");
			isWrite2File = true;
			return ZFSLabelData;
		}
		byte[] ZFSLabelData = new byte[cZFSLabelSize]; // 256 kb
		for (int i=0; i<cZFSLabelSize; i++)
			ZFSLabelData[i]=bs[ZFSLabelOffset+i];
		try {
			Files.write(f.toPath(), ZFSLabelData);
			System.out.print("Write  "+ZFSLabel+" to "+Write2FileName+" "+
					ZFSLabelData.length/1024+" kbytes.");
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return ZFSLabelData;	
	}
*/
	private void GetNameValue(byte[] bs) {
		 
		int nu = cZFSNameValueBegin;
		log.debug("\nencoding methode and host endian = 0x" + VarTools.ByteArray2HexsStr(bs,nu+0x00,4)); 
		log.debug("nvl_version = 0x" + VarTools.ByteArray2HexsStr(bs,nu+0x04,4)); 
		log.debug("nvl_nvflag  = 0x" + VarTools.ByteArray2HexsStr(bs,nu+0x08,4)); 
		while (true) {
			//System.out.println("encoded size of the nvpairg = " + ByteArray2HexsStr(bs,nu+0x0C,4)); 
			//System.out.println("decoded size of the nvpairg = " + ByteArray2HexsStr(bs,nu+0x10,4)); 
			//long nl = (int)VarTools.ByteArray2LongN(bs,nu+0x14,4);
			long nl = (int)VarTools.ByteArray2LongN(bs,nu+0x14,4);
			int n = (int)nl;
			//System.out.println("size of key (long) " + nl);
			//System.out.println("size of key  (int) " + n );
			String sKey = VarTools.ByteArray2Str(bs,nu+0x18,n);
			//System.out.println("key = " + sKey); 
			nu = nu + n + (n % 4 == 0 ? 0 : 4 - n % 4);
			int nDataType = (int)VarTools.ByteArray2LongN(bs,nu+0x18,4);
			//System.out.println("data type =  " + nDataType); 
			int nPair = (int)VarTools.ByteArray2LongN(bs,nu+0x1C,4);
			//System.out.println("number of elements in the nvpair =  " + nPair); 
			long nValUINT64 = -1;
			if (nDataType==8) { // DATA_TYPE_UINT64
				//System.out.println("value hex = " + ByteArray2HexsStr(bs,nu+0x20+n,8)); 
				nValUINT64= VarTools.ByteArray2LongN(bs,nu+0x20,8);
				Add2ZDBl(zdb, zdbKeys, sKey, nValUINT64);
				log.debug("==="+sKey+" = " + nValUINT64+" or 0x"+String.format("%08X",nValUINT64)); 
				nu = nu + 0x28;
			} else
			if (nDataType==9) { // DATA_TYPE_STRING
				int nn = (int)VarTools.ByteArray2LongN(bs,nu+0x20,4);
				//System.out.println("size of val = " + nn);
				//System.out.println("value hex = " + ByteArray2HexsStr(bs,nu+0x24,nn)); 
				String sVal = VarTools.ByteArray2Str(bs, nu+0x24, nn);
				Add2ZDB(zdb, zdbKeys, sKey,sVal,true);
				log.debug("==="+sKey+" =  " + sVal); 
				nu = nu+ 0x24 + nn + (nn % 4 == 0 ? 0 : 4 - nn % 4); 
			} else
			if (nDataType==19) { // DATA_TYPE_NVLIST
				int nn = (int)VarTools.ByteArray2LongN(bs,nu+0x20,4);
				//System.out.println("??0 = " + nn);  
				nn = (int)VarTools.ByteArray2LongN(bs,nu+0x24, 4);
				log.debug("==="+sKey+" =  " + nn);  
				nu = nu+ 0x28; 
				//System.out.println("nvl_nvflag = 0x" + ByteArray2HexsStr(bs,nu,8)); 
				nu = nu - 0x0C;
				long ender = VarTools.ByteArray2LongN(bs,nu,8);
				if (ender==0)
					break;
				//boolean isChildren = false;					
				while (true) {
					n = (int)VarTools.ByteArray2LongN(bs,nu+0x14,4);
					//System.out.println("size of key  " + n);
					sKey = VarTools.ByteArray2Str(bs,nu+0x18,n);
					//System.out.println("key = " + sKey); 
					nu = nu + n + (n % 4 == 0 ? 0 : 4 - n % 4);
					nDataType = (int)VarTools.ByteArray2LongN(bs,nu+0x18,4);
					//System.out.println("data type =  " + nDataType); 
					nPair = (int)VarTools.ByteArray2LongN(bs,nu+0x1C,4);
					//System.out.println("number of elements in the nvpair =  " + nPair); 
					//nValUINT64 = -1;
					if (nDataType==1) { // DATA_TYPE_BOOLEAN
						//System.out.println("value hex = " + ByteArray2HexsStr(bs,nu+0x20,8)); 
						//nValUINT64= ByteArray2LongN(bs,nu+0x20,8);
						String snPair = (nPair==1 ? "yes" : "no");
						if (!sKey.trim().isEmpty())
							System.out.println("======"+sKey+" = " + snPair); 
						Add2ZDB(zdbVdev, zdbKeys, sKey, snPair, true);
						nu = nu + 0x20;
					} else
					if (nDataType==8) { // DATA_TYPE_UINT64
						//System.out.println("value hex = " + ByteArray2HexsStr(bs,nu+0x20+n,8)); 
						nValUINT64= VarTools.ByteArray2LongN(bs,nu+0x20,8);
						Add2ZDBl(zdbVdev, zdbVdevKeys, sKey, nValUINT64);
						if (!sKey.trim().isEmpty())
							log.debug("======"+sKey+" = " + nValUINT64+" or 0x"+String.format("%08X",nValUINT64)); 
						nu = nu + 0x28;
					} else
					if (nDataType==9) { // DATA_TYPE_STRING
						nn = (int)VarTools.ByteArray2LongN(bs,nu+0x20,4);
						//System.out.println("size of val = " + nn);
						//System.out.println("value hex = " + ByteArray2HexsStr(bs,nu+0x24,nn)); 
						String sVal = VarTools.ByteArray2Str(bs, nu+0x24, nn);
						if (!sKey.trim().isEmpty())
							log.debug("======"+sKey+" = " + sVal); 
						Add2ZDB(zdbVdev, zdbVdevKeys, sKey, sVal, true);
						nu = nu + 0x24 + nn + (nn % 4 == 0 ? 0 : 4 - nn % 4); 
					} else
					if (nDataType==20) {  // DATA_TYPE_NVLIST_ARRAY = 20
						//isChildren = true;	
						nn = (int)VarTools.ByteArray2LongN(bs,nu+0x20,4);
						//System.out.println("??0 = " + nn);  
						nn = (int)VarTools.ByteArray2LongN(bs,nu+0x24, 4);
						log.debug("==array===="+sKey+" =  " + nPair);  
						//Add2ZDBl(zdbVdev, zdbVdevKeys, sKey, nPair);
						nu = nu + 0x28; 
						//System.out.println("--array----nvl_nvflag = 0x" + ByteArray2HexsStr(bs,nu,8)); 
						nu = nu +8;
						HashMap<String, String> z = new HashMap<String, String>();
						int nChild = -1;
						while (true) {
							n = (int)VarTools.ByteArray2LongN(bs,nu,4);
							//System.out.println("--array----size of key  " + n);
							sKey = VarTools.ByteArray2Str(bs,nu+0x04,n);
							if (sKey.equals("type")) {
								if (z.size()>0) {
									zdbVdevChild.add(z);
									z = new HashMap<String, String>();
								}
								z.clear();		
								nChild++;
								log.debug("--array----children[" + nChild +"]");
							}
							//System.out.println("--array----key = " + sKey); 
							n = n + (n % 4 == 0 ? 0 : 4 - n % 4);
							nDataType = (int)VarTools.ByteArray2LongN(bs,nu+0x04+n,4);
							//System.out.println("--array----data type =  " + nDataType); 
							if (nDataType==8) { // DATA_TYPE_UINT64
								nu = nu + 0x0C + n;
								//System.out.println("--array----value hex = " + ByteArray2HexsStr(bs,nu,8)); 
								long nValUINT64l = VarTools.ByteArray2LongN(bs,nu,8);
								log.debug("==array===="+sKey+" = " + nValUINT64l+
										" or 0x"+String.format("%08X",nValUINT64l)); 
								Add2ZDBl(z, zdbVdevChildKeys, sKey, nValUINT64l);
								nu = nu + 0x08;
							} else
							if (nDataType==9) { // DATA_TYPE_STRING
								nn = (int)VarTools.ByteArray2LongN(bs,nu+0x10,4);
								//System.out.println("size of val = " + nn);
								//System.out.println("value hex = " + ByteArray2HexsStr(bs,nu+0x24,nn)); 
								String sVal = VarTools.ByteArray2Str(bs, nu+0x14, nn);
								log.debug("==array===="+sKey+" = " + sVal); 
								Add2ZDB(z, zdbVdevChildKeys, sKey, sVal, true);
								nu = nu + 0x14 + nn + (nn % 4 == 0 ? 0 : 4 - nn % 4); 
							} else { 
								log.debug("nDataType = " + nDataType);
								if (VarTools.ByteArray2LongN(bs,nu,8)==0)
									break;
							}				
							//System.out.println("------nvl_nvflag = 0x" + ByteArray2HexsStr(bs,nu,8)); 
							ender = VarTools.ByteArray2LongN(bs,nu,8);
							nu = nu + 0x08;
							if (ender==0) {
								nPair--;
								if (nPair==0)
									break;
								nu = nu + 0x10;
							}
						}
						//System.out.println("---zdbVdevChild add--- "+nChild);
						zdbVdevChild.add(z);
					}
					else { 
						log.debug("nDataType = " + nDataType);
						if (VarTools.ByteArray2LongN(bs,nu,8)==0)
							break;
					}				
					//System.out.println("------nvl_nvflag = 0x" + ByteArray2HexsStr(bs,nu,8)); 
					ender = VarTools.ByteArray2LongN(bs,nu,8);
					nu = nu - 0x0C;
					if (ender==0) {
						nu = nu + 0x14;
						//System.out.println("----"+ByteArray2HexsStr(bs,nu-0x08,16)); 
						break;
					}
				}
			}
			else {
				log.debug("nDataType = " + nDataType);
			}				
			//System.out.println("nvl_nvflag = 0x" + ByteArray2HexsStr(bs,nu,8)); 
			long ender = VarTools.ByteArray2LongN(bs,nu,8);
			nu = nu - 0x0C;
			if (ender==0)
				break;
		}
		log.debug("----------------------------\n");
	} 

	private void Add2ZDBl(HashMap<String, String> z, String[] zKey, String sKey, long iVal) {

		Add2ZDB(z, zKey, sKey, VarTools.asUnsignedDecimalString(iVal), false);	
	}
	
	private boolean Add2ZDB(HashMap<String, String> z, String[] zKey, String sKey, String sVal, boolean isStr) {

		if (isStr)
			sVal = "'"+sVal+"'";
		for (String s: zKey)
			if (s.equals(sKey)) {
				z.put(sKey, sVal); 
				return true; 
			}
		if (!sKey.trim().isEmpty())
			System.out.println("Add2ZDB: key '" + sKey + "' not use in zdb output list.");
		return false; 
	}	

	private void PrintKeys(String[] zdbKeys2, HashMap<String, String> z , String prefix) {

		for (String s: zdbKeys2) 
			if (z.containsKey(s)) 
				System.out.println(prefix+s + ": " + z.get(s));				
			else
				if (s.length()>0)
					System.out.println(prefix+s + ":");		 
	}

	public void Print() {

		System.out.println("ZFS Label \""+ZFSLabel+"\" has offset = 0x"+ 
				String.format("%08X",ZFSLabelOffset)+" or "+ZFSLabelOffset);
	}

	public void PrintZDB(boolean b) {

		if (!b)
			return;
		System.out.println("-----------------------------------------------");
		System.out.println("LABEL " + ZFSLabel.substring(1));
		System.out.println("-----------------------------------------------");
		PrintKeys(zdbKeys, zdb, ""); 
		System.out.println("vdev_tree:"); 
		PrintKeys(zdbVdevKeys, zdbVdev, "\t");  
		int i =0;
		for (HashMap<String, String> z: zdbVdevChild) { 
			System.out.println("\tchildren["+(i++)+"]:");
			PrintKeys(zdbVdevChildKeys, z, "\t\t"); 
		}
		PrintKeys(zdbKeys2, zdb, ""); 
		PrintKeys(zdbVdevKeys2, zdbVdev, "\t");  		
	}
	
	public void PrintZDB2File(String sFile) {
		
		log.info("Print zdb output to " + sFile);
		PrintStream ps = System.out; 
		try {
			File f = new File(sFile);
			if (f.exists())
				f.delete();
			System.setOut(new PrintStream(sFile));
			PrintZDB(true);
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}
		System.setOut(ps);
	}
	
}
