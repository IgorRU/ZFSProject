package zsfpackage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import tools.PrintTools;
import tools.VarTools;

public class ZFSChecksum {
	
	private static final Logger log = Logger.getLogger(ZFSChecksum.class.getName()); 

	public String Algorothm = "inherit";
	public String Descriptor = "inherit";
	public byte Num = 0;

	private HashMap<Integer, String> hashMapAlgorothm  = new HashMap<>();
	private HashMap<Integer, String> hashMapDescriptor = new HashMap<>();
	
	public ZFSChecksum() {
		
	}
	
	public void Pack(byte b) {
			
		Num=b;
		log.trace("Checksum is "+Num);
		InitHashMaps();
		Algorothm = GetAlgorothmName();
		Descriptor = GetDescriptorName();
	}

	public void testHashWithJavaMessageDigest() throws Exception {
		
		String originalValue = "abc123";
		String hashedValue = "6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090";

		String currentHashedValue = HashWithJavaMessageDigest(originalValue);
        System.out.println(hashedValue.equals(currentHashedValue) ? "Ok!" : "No.");
    }
	
	public String HashWithJavaMessageDigest(String originalString) {
         MessageDigest digest = null;
		 try {
			digest = MessageDigest.getInstance("SHA-256");
		 } catch (NoSuchAlgorithmException e) { 
			e.printStackTrace();
		 }
         byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
         return VarTools.bytesToHex(encodedhash);
    }
	
	 
	public String GetAlgorothmName() {		
		
		return PrintTools.GetHashMapKeyVlaue(hashMapAlgorothm, Num); 
	}
	
	public String GetDescriptorName() {		

		return PrintTools.GetHashMapKeyVlaue(hashMapDescriptor, Num); 
	}

	private void Init1(int n, String Algorothm, String Descriptor) {

		hashMapAlgorothm.put( n, Algorothm);
		hashMapDescriptor.put(n, Descriptor); 		
	}
	private void InitHashMaps() {
		 
		Init1(0, "inherit",		"inherit");
		Init1(7, "fletcher4",	"fletcher4");
		Init1(8, "SHA256",		"SHA256");		  	
	}	

	public void Print() {
		
		System.out.println("Checksum = "+Num);
		System.out.println("Descriptor: "+Descriptor);
		System.out.println("Algorothm = "+Algorothm);		
	}
	
	public void PrintHashMaps() {

		PrintTools.PrintHashMapKeyVlaues(hashMapAlgorothm);
		PrintTools.PrintHashMapKeyVlaues(hashMapDescriptor);
	}
	
	public void fletcher_8( ) {
		
		// https://stackoverflow.com/questions/35886276/what-is-the-correct-implementation-of-the-8-bit-fletcher-algorithm-in-java
		// https://stackoverflow.com/questions/8315215/checksum-algorithm-based-on-j-g-fletcher
		// https://en.wikipedia.org/wiki/Fletcher%27s_checksum#Straightforward 
		
		String bin = "100100101011111011101011";
		char[] cA = bin.toCharArray();
		int ckA = 0, ckB = 0;
		for (int i = 0; i < cA.length; i++){
		    ckA = (ckA + Integer.valueOf(cA[i])/49) % 255;
		    ckB = (ckB + ckA) % 255;
		}
		System.out.println(ckA);
		System.out.println(ckB);
		System.out.println((ckB << 8) | ckA);
	}
	
}
