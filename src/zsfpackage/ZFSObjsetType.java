package zsfpackage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import tools.PrintTools;

public class ZFSObjsetType {
	
	private HashMap<Integer, String> hashObjsetType = new HashMap<>();
	private HashMap<Integer, String> hashMapDescriptor = new HashMap<>();
	
	public String ObjsetType;
	public String Descriptor;
	public byte Num;
	
	public ZFSObjsetType() {		
		
	}
	
	public void Pack(byte b) {
		
		Num=b;
		//System.out.println("Compression is "+Num);
		InitHashMaps();
		ObjsetType = GetCompressionName();
		Descriptor = GetDescriptorName();
	}

	public String GetDescriptorName() {				
		
		return PrintTools.GetHashMapKeyVlaue(hashMapDescriptor,Num); 
	}

	public String GetCompressionName() {		
		
		return PrintTools.GetHashMapKeyVlaue(hashObjsetType,Num); 
	}

	public void Print() {
		
		System.out.println("Checksum = "+Num);
		System.out.println("Descriptor: "+Descriptor);
		System.out.println("ObjsetType = "+ObjsetType);		
	}
	
	public void PrintHashMaps() {

		PrintTools.PrintHashMapKeyVlaues(hashObjsetType);
		Set<Entry<Integer, String>> set = hashMapDescriptor.entrySet();
		for (Map.Entry<Integer, String> me : set) {
		    System.out.println(me.getKey() + ": " + me.getValue());
		}
	}
	
	private void InitHashMaps() {

		hashObjsetType.put(0, "DMU_OST_NONE");
		hashObjsetType.put(1, "DMU_OST_META");
		hashObjsetType.put(2, "DMU_OST_ZFS");
		hashObjsetType.put(3, "DMU_OST_ZVOL");
		hashObjsetType.put(4, "DMU_OST_OTHER");
		hashObjsetType.put(5, "DMU_OST_ANY");
		hashObjsetType.put(6, "DMU_OST_NUMTYPES");

		hashMapDescriptor.put(0, "inherit"); 
		hashMapDescriptor.put(1, "MOS"); 
		hashMapDescriptor.put(2, "Dataset"); 
		hashMapDescriptor.put(3, "Block drive zfs (vol)"); 	
		hashMapDescriptor.put(4, "For testing only!"); 
		hashMapDescriptor.put(5, "Be careful!"); 
		hashMapDescriptor.put(6, "num types"); 			
	}	
}
