package tools;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class PrintTools {
	
	private static final Logger log = Logger.getLogger(PrintTools.class.getName()); 

	   static public void Dump(byte[] bs, int start) {
		   
		   Dump(bs, start, -1);
	   }
	   
	   static public void Dump(byte[] bs, int start, int count) {

		   String sDump =""; 
			if (count==-1)
				count=bs.length;  
		    for (int i=0; i<=count-1; i++) {
		    	if (i==0)
		    		sDump=sDump+(String.format("%03X",start)+" ");
		    	int j = i + start;
		    	if ((i % 16 == 0)&&(i>0)) {
		    		log.debug(sDump);
		    		sDump=String.format("%03X",j)+" ";	 
		    	}
		    	sDump=sDump+(String.format("%02X", bs[j])+" ");
		    } 
			log.debug(sDump);
	   }
	   
	  static public String GetHashMapKeyVlaue(HashMap<Integer, String> h, int n) {
		Set<Map.Entry<Integer, String>> set = h.entrySet();
		for (Map.Entry<Integer, String> me : set) 
			if (me.getKey()==n)
				return me.getValue();
		return "";
	  }
	   
	  static public String GetHashMapKeyVlaue(HashMap<String, String> h, String s) {
			Set<Map.Entry<String, String>> set = h.entrySet();
			for (Map.Entry<String, String> me : set) 
				if (me.getKey().equals(s))
					return me.getValue();
			return "";
	  }
		
	  static public void PrintHashMapKeyVlaues(HashMap<Integer, String> h) {
		  
		  	Set<Map.Entry<Integer, String>> set = h.entrySet();
			for (Map.Entry<Integer, String> me : set) {
				log.info(me.getKey() + ": " + me.getValue());
			}
	  }

	  public static String PrintHashMapKeyVlaues(HashMap<String, String> h, String s) {
		
		Set<Map.Entry<String, String>> set = h.entrySet();
		for (Map.Entry<String, String> me : set) 
			if (me.getKey()==s)
				return me.getValue();
		return "";
	  }
	  public static void PrintByreArr2Hex(String sTitle, String sHexFormat, byte[] b, int offset, int count) {
			
			System.out.print(sTitle + " = 0x");	
			String sHex = "";
			for (int i=offset; i<=count+offset-1; i++) 
				sHex = sHex + String.format(sHexFormat,b[i]);	
			log.info(sHex);
	  }

	  public static void Print10andHex(String sTitle, String sHex, BigInteger b) {
		
		  log.info(sTitle + " = " + b + " or 0x" + String.format(sHex,b));	 
	  }
	  
	  public static void Print10andHex(String sTitle, String sHex, byte b) {
		
		  log.info(sTitle + " = " + b + " or 0x" + String.format(sHex,b));	 
	  }

	  public static void Print10andHex(String sTitle, String sHex, int b) {
		
		  log.info(sTitle + " = " + b + " or 0x" + String.format(sHex,b));	 
	  }

	  public static void Print10andHex(String sTitle, String sHex, long b) {
		
		  log.info(sTitle + " = " + b + " or 0x" + String.format(sHex,b));	 
	  }

	public static String PrintIsNull(Object obj, String sTag) { 
		return  sTag + (obj==null ? " null" : " no null");	
	}
}
