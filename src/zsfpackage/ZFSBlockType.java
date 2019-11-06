package zsfpackage;

import java.util.HashMap;

import org.apache.log4j.Logger;

import tools.PrintTools;

public class ZFSBlockType {
	
	private static final Logger log = Logger.getLogger(ZFSBlockType.class.getName()); 

	private HashMap<Integer, String> hashMapType = new HashMap<>();
	private HashMap<Integer, String> hashMapDescriptor = new HashMap<>();
	
	public String Type			= "DMU_OT_NONE";
	public String Descriptor	= "Unallocated object";
	public byte   Num 			= 0;
	
	public ZFSBlockType() {

	}

	public void Pack(byte b) {
		
		Num=b;
		log.trace("ZFSBlockType = "+b);
		InitHashMaps();
		Type = GetBlockType();
		Descriptor = GetDescriptorName();
	}
	
	public void Print() {
		
		System.out.println("BlockType = "+Num);
		System.out.println("Descriptor: "+Descriptor);
		System.out.println("Type = "+Type);		
	}
	
	public String GetDescriptorName() {		
		
		return PrintTools.GetHashMapKeyVlaue(hashMapDescriptor, (int)Num);
	}

	public String GetBlockType() {		
		
		return PrintTools.GetHashMapKeyVlaue(hashMapType, (int)Num); 
	}
	
	private void InitHashMaps() {

		hashMapType.put(0,  "DMU_OT_NONE");
		hashMapType.put(1,  "DMU_OT_OBJECT_DIRECTORY"); 
		hashMapType.put(10, "DMU_OT_DNODE");  
		hashMapType.put(11, "DMU_OT_OBJSET"); 
		hashMapType.put(19, "DMU_OT_PLAIN_FILE_CONTENTS"); 
		hashMapType.put(20, "DMU_OT_DIRECTORY_CONTENTS"); 
		hashMapType.put(21, "DMU_OT_MASTER_NODE"); 
		hashMapType.put(23, "DMU_OT_ZVOL"); 
		hashMapType.put(24, "DMU_OT_ZVOL_PROP"); 

		hashMapDescriptor.put(0,  "Unallocated object"); 
		hashMapDescriptor.put(1,  "DSL object directory ZAP object");  	
		hashMapDescriptor.put(10, "DNode"); 	  	
		hashMapDescriptor.put(11, "Object set"); 	  	
		hashMapDescriptor.put(19, "ZPL plain file"); 	  	
		hashMapDescriptor.put(20, "ZPL directory ZAP Object");
		hashMapDescriptor.put(21, "ZPL Master node ZAP object: " +
				"head object used to identify root directory, " +
				"delete queue, and version for a filesystems");
		hashMapDescriptor.put(23, "ZFS volume (ZVOL)");
		hashMapDescriptor.put(24, "ZVOL properties");
	}

}
