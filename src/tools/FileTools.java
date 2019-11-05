package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileTools {

	static public boolean isDebug = true;
	
	static public ArrayList<String> GetFilesFromDir(ArrayList<String> ss, String dir) {
		 
		if (ss==null)
			ss = new ArrayList<>();
		File folder = new File(dir);
		for (File fileEntry : folder.listFiles()) {
	        String sName = fileEntry.getAbsolutePath();
			if (fileEntry.isDirectory()) {
	        	ss = GetFilesFromDir(ss, sName);
	        } else { 
	        	ss.add(sName);
	        }	        	
		}
		return ss;		
	}	
	
	static public byte[] GetBytes(RandomAccessFile f, FileChannel fchanel, int len) {
		
		ByteBuffer buf = ByteBuffer.allocate(len);
	    long pointer = 0;
		try {
			pointer = f.getFilePointer();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PrintTools.Print10andHex("Position into HDD =","%08X",pointer); 
		try {		        
			fchanel.read(buf);
		    pointer = f.getFilePointer();
		    if (isDebug)
		    	System.out.println("Read bytes: " + buf.position() +". Position = " + pointer);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return buf.array();
	}

	static public void SetOffset(FileChannel fchanel, long offset) {

		try {
			fchanel.position(offset);
		} catch (IOException e) { 
			e.printStackTrace();
		}		
	}		

	public static byte[] WriteBlock(String sFileOut, byte[] bs, int nBytes) {
		
		int n = nBytes;
		byte[] bOut = new byte[n];
		File f = new File(sFileOut); 
		for (int i=0; i < n; i++)
			bOut[i]=bs[i];
		try {
			Files.write(f.toPath(), bOut);
			System.out.println("Write  to "+sFileOut+" has "+ n+" bytes.");
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return bOut;
	}
	
	public void SaveToFile(byte b[], String path, boolean isDelete)  {
		
		if (isDelete) {
			try { 	            
				Files.deleteIfExists(Paths.get(path));
			} catch (IOException e) {
				e.printStackTrace();
			} 	       
	        System.out.println("Deletion "+path+" successful."); 
		}		
		
		try (FileOutputStream stream = new FileOutputStream(path)) {
		    stream.write(b);
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
	
	public static long GetFileSize(String sPath) {
		
		File f = new File(sPath);
		return f.getUsableSpace();
		
	}
	
}
