package zsfpackage;

import java.util.HashMap;

import org.apache.log4j.Logger;

import tools.PrintTools;

public class ZMSObjsetType {
	
	private static final Logger log = Logger.getLogger(ZMSObjsetType.class.getName()); 
 
	private HashMap<Integer, String> hashMapDescriptor = new HashMap<>();
	 
	public String Descriptor;
	public byte Num;

	public ZMSObjsetType() {
		
	}	

	public void Pack(byte b) {
		
		Num=b;
		log.trace("Compression is "+Num);
		InitHashMaps();
		Descriptor = GetDescriptorName();
	}

	public String GetDescriptorName() {				
		
		return PrintTools.GetHashMapKeyVlaue(hashMapDescriptor,Num); 
	}

	public void Print() {
		
		System.out.println("Checksum = "+Num);
		System.out.println("Descriptor: "+Descriptor);	
	}
	
	private void InitHashMaps() {

		hashMapDescriptor.put(0, "DMU_OST_NONE"); 
		hashMapDescriptor.put(1, "DMU_OST_META");  		
	}	
	
	
}
