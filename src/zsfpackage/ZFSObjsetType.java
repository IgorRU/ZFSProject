package zsfpackage;

import org.apache.log4j.Logger;

public class ZFSObjsetType extends ObjType  {
	
	private static final Logger log = Logger.getLogger(ZFSObjsetType.class.getName()); 	
	
	public ZFSObjsetType() {		
		
	}
	
	public void Pack(byte b) {
		
		Num=b;
		log.trace("Compression is "+Num);
		InitHashMaps();
		ObjsetType = GetName();
		Descriptor = GetDescriptorName();
	}
	
	public void Print() {
		
		System.out.println("Checksum = "   + Num);
		System.out.println("Descriptor: "  + Descriptor);
		System.out.println("ObjsetType = " + ObjsetType);		
	}
	
	private void InitHashMaps() {

		hashMapType.put(0, "DMU_OST_NONE");
		hashMapType.put(1, "DMU_OST_META");
		hashMapType.put(2, "DMU_OST_ZFS");
		hashMapType.put(3, "DMU_OST_ZVOL");
		hashMapType.put(4, "DMU_OST_OTHER");
		hashMapType.put(5, "DMU_OST_ANY");
		hashMapType.put(6, "DMU_OST_NUMTYPES");

		hashMapDescriptor.put(0, "inherit"); 
		hashMapDescriptor.put(1, "MOS"); 
		hashMapDescriptor.put(2, "Dataset"); 
		hashMapDescriptor.put(3, "Block drive zfs (vol)"); 	
		hashMapDescriptor.put(4, "For testing only!"); 
		hashMapDescriptor.put(5, "Be careful!"); 
		hashMapDescriptor.put(6, "num types"); 			
	}	
}
