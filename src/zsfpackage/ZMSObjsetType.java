package zsfpackage;

import java.util.HashMap;

import tools.PrintTools;

public class ZMSObjsetType {
 
	private HashMap<Integer, String> hashMapDescriptor = new HashMap<>();
	 
	public String Descriptor;
	public byte Num;

	public ZMSObjsetType() {
		
	}	

	public void Pack(byte b) {
		
		Num=b;
		//System.out.println("Compression is "+Num);
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
