package zsfpackage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import tools.PrintTools;

public class ObjType {

	private static final Logger log = Logger.getLogger(ObjType.class.getName()); 

	protected HashMap<Integer, String> hashMapType       = new HashMap<>();
	protected HashMap<Integer, String> hashMapDescriptor = new HashMap<>();
	
	public String ObjsetType;
	public String Descriptor;
	public byte   Num;
	
	public ObjType() {
		
		log.trace("ObjType constructor");		
	}

	public String GetDescriptorName() {				
		
		return PrintTools.GetHashMapKeyVlaue(hashMapDescriptor,Num); 
	}

	public String GetName() {		
		
		return PrintTools.GetHashMapKeyVlaue(hashMapType,Num); 
	}
	
	public void PrintHashMaps() {

		PrintTools.PrintHashMapKeyVlaues(hashMapType);
		Set<Entry<Integer, String>> set = hashMapDescriptor.entrySet();
		for (Map.Entry<Integer, String> me : set) {
		    System.out.println(me.getKey() + ": " + me.getValue());
		}
	}
}
