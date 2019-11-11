package zsfpackage;

public class ZfsConst {

	public static final int SectorLength = 512; 
	
	public static final String UBMagicLE 	 = "0CB1BA00"; 
	public static final String UBMagicLEDesc = "Little Endian"; 
	public static final String UBMagicBE 	 = "00BAB10C"; 
	public static final String UBMagicBEDesc = "Big Endian";
	public static final String UBMagicNo 	 = "No magic block";  

	// ZFS Partition
	// L1 = 0 .. 256 kb
	public static long ZFSL0Start = 0;
	public static long ZFSL1Start = 256*1024;  
	// L1 = 256 ... 512 kb
	// data
	public static long ZFSDataStart		 = 2*ZFSL1Start*1024;
	public static long ZFSDataAfteBootStart = 4*1024*1024;

	// ZFS Label format:
	// 0   ... 8k   - Blank space
	static int  cZFSBlankSpaceBegin = 0;
	// 8   ... 16k  - Boot header
	static int  cZFSBootHeaderBegin = 16*SectorLength;   
	// 16  ... 128k - Name = Value  
	static int  cZFSNameValueBegin  = 32*SectorLength;     
	static int  cZFSUberArrayBegin  = 256*SectorLength;    
	static int  cZFSUberArrayEnd    = 512*SectorLength;  
	static int  cZFSLabelSize       = 256*1024;  		// 256 kb
	static int  cZFSUberBlockSize   = 2*SectorLength;   //  
	
	// "zdb -l" fields	
	final static String[] zdbKeys = new String[]  {
			"version", 
			"name", 
			"state", 
			"txg", 
			"pool_guid", 
			"errata", 
			"hostid", 
			"hostname", 
			"top_guid", 
			"guid", 
			"vdev_children"
		};
	final static String[] zdbVdevKeys = new String[]  {
			"type", 
			"id",
			"guid", 
			"path", 
			"nparity", 
			"metaslab_array",
			"metaslab_shift",
			"ashift",
			"asize",
			"is_log",
			"create_txg"
		};
	final static String[] zdbVdevChildKeys = new String[]  {
			"type", "id", 
			"guid", "path",
			"whole_disk", 
			"DTL","create_txg"
		};
	final static String[] zdbKeys2 = new String[]  {
			"features_for_read"
		};
	final static String[] zdbVdevKeys2 = new String[]  {
			"com.delphix:hole_birth",
			"com.delphix:embedded_data"
		};
}
