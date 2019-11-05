package zsfpackage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tools.PrintTools;

public class ZFSCompression   {

	private HashMap<Integer, String> hashMapCompression = new HashMap<>();
	private HashMap<Integer, String> hashMapDescriptor  = new HashMap<>();
	
	public String Algorithm;
	public String Descriptor;
	public byte Num;
	
	public ZFSCompression() {		
		
	}
	
	public void Pack(byte b) {
		
		Num=b;
		//System.out.println("Compression is "+Num);
		InitHashMaps();
		Algorithm = GetCompressionName();
		Descriptor = GetDescriptorName();
	}

	public String GetDescriptorName() {				
		
		return PrintTools.GetHashMapKeyVlaue(hashMapDescriptor,Num); 
	}

	public String GetCompressionName() {		
		
		return PrintTools.GetHashMapKeyVlaue(hashMapCompression,Num); 
	}

	public void Print() {
		
		System.out.println("Checksum = "+Num);
		System.out.println("Descriptor: "+Descriptor);
		System.out.println("Algorothm = "+Algorithm);		
	}
	
	public void PrintHashMaps() {

		PrintTools.PrintHashMapKeyVlaues(hashMapCompression);
		Set<Entry<Integer, String>> set = hashMapDescriptor.entrySet();
		for (Map.Entry<Integer, String> me : set) {
		    System.out.println(me.getKey() + ": " + me.getValue());
		}
	}
	
	private void Init1(int n, String Compress, String Descriptor) {

		hashMapCompression.put(n, Compress);
		hashMapDescriptor.put( n, Descriptor); 		
	}
	
	private void InitHashMaps() {

		Init1(0, "inherit",	"inherit");
		Init1(1, "lzjb",	"on");
		Init1(2, "none",	"off");
		Init1(3, "lzjb",	"lzjb");
		Init1(15,"lz4",		"lz4");
	}	
}
