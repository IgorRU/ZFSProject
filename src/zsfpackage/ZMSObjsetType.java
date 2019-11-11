package zsfpackage;

import org.apache.log4j.Logger; 

public class ZMSObjsetType extends ObjType {
	
	private static final Logger log = Logger.getLogger(ZMSObjsetType.class.getName());  

	public ZMSObjsetType() {
		
	}	

	public void Pack(byte b) {
		
		Num=b;
		log.trace("ZMSObjsetType is "+Num);
		InitHashMaps();
		Descriptor = GetDescriptorName();
	} 

	public void Print() {
		
		log.info("ZMSObjsetType = "   + Num);
		log.info("Descriptor = " + Descriptor);	
	}
	
	private void InitHashMaps() {

		hashMapDescriptor.put(0, "DMU_OST_NONE"); 
		hashMapDescriptor.put(1, "DMU_OST_META");  		
	}	
	
	
}
