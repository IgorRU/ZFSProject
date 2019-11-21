package desktop;

import java.util.ArrayList; 
import org.apache.log4j.Logger; 

import hdd.PhysicalDrive;
import tools.FileTools;  

public class ZSFMain {

	private static final Logger log = Logger.getLogger(ZSFMain.class.getName()); 
	
	static long   serialVersionUID 	= 1L;
	
	static int 	  BestZfsLabel 		= 2; 		  //  0..3
	static String Ver 				= "0.0.1";
	static String Title 			= "PhysicalDrive info Ver "+Ver+" \n";
	static String ZFSDrive			= "\\\\.\\PhysicalDrive1";
	static String ZFSImageDir		= "K:\\zfs\\";
	static String ZFSImageDirTests 	= ZFSImageDir + "Images_zfsonline\\vdevs\\";
	static String ZFSImage			= ZFSImageDirTests + "zol-0.6.1\\vdev0";
	static PhysicalDrive pdDrv = null; 
	
	public static void main(String[] args) {	

        log.info(Title); 
        ZfsTestDrive(ZFSDrive);
        //ZfsTestDDs(ZFSImageDirTests + "zol-0.6.1"); 
		/* ZfsTestDDs(ZFSImageDirTests);
		ZfsTestDrive(ZFSDrive); */
		if (args.length>0) { 
			ZFSTools(args);
		}
		log.info("End ZSFMain.");				
	}
	
		private static void ZfsTestDrive(String zFSDrive) {
		 
		pdDrv = new PhysicalDrive(ZFSDrive, true); 
		pdDrv.LogFile=ZFSImageDir; 
		pdDrv.ViewActiveBlock();
	}

	private static void ZfsTestDDs(String dir) {
		
		ArrayList<String> ss = FileTools.GetFilesFromDir(null,dir);
		for (String s: ss) { 		 
			 ZfsTestDD(s); 
		}		
	}
	private static void ZfsTestDD(String sFile) {
		
		PhysicalDrive pd = new PhysicalDrive(sFile, false);
		pd.PrintZDB(); 
		pd.ViewActiveBlock();
	}

	private static void ZFSTools(String[] args) {

		if (args.length>1) {
			if (args[0].equals("zdb"))  
				if (args[1].equals("-l")) 
					if (pdDrv != null)
						pdDrv.PrintZDB();   
		} else 
			PrintHelp();			
	}

	private static void PrintHelp() {
		
		log.info("Help to ZSFProject.\n");	
		log.info("Run: ZSFProject <params>\n");	
		log.info("Params (output is equal to zfs command):");	
		log.info("zdb -l <device> - list ZFS label properties");			
	}
	
}

