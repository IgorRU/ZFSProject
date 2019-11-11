package zsfpackage;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import tools.PrintTools;
import tools.VarTools;

public class ZFSChecksum  extends ObjType {
	
	private static final Logger log = Logger.getLogger(ZFSChecksum.class.getName());

	private static long[] checksum = new long[4]; 
	
	public byte Num = -1;
	public int CHECKSUM_DEFAULT = 7;
	
	private HashMap<Integer, String> hashMapAlgorothm  = new HashMap<>();
	private HashMap<Integer, String> hashMapDescriptor = new HashMap<>();
	
	public ZFSChecksum() {
		
	}
	
	public void Pack(byte b) {
			
		Num=b;
		log.trace("Checksum is "+Num);
		InitHashMaps();
		ObjsetType = GetName();
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

	private void Init1(int n, String Algorothm, String Descriptor) {

		hashMapAlgorothm.put( n, Algorothm);
		hashMapDescriptor.put(n, Descriptor); 		
	}
	
	private void InitHashMaps() {  
		
		Init1(0, "inherit",					 "inherit");
		Init1(1, "CHECKSUM_ON",				 "CHECKSUM_ON");
		Init1(2, "ZIO_CHECKSUM_OFF",		 "ZIO_CHECKSUM_OFF");
		Init1(3, "ZIO_CHECKSUM_LABEL",		 "ZIO_CHECKSUM_LABEL");
		Init1(4, "ZIO_CHECKSUM_GANG_HEADER", "ZIO_CHECKSUM_GANG_HEADER");
		Init1(5, "CHECKSUM_ZILOG",			 "CHECKSUM_ZILOG");
		Init1(6, "fletcher2",				 "fletcher2");
		Init1(7, "fletcher4",				 "fletcher4");
		Init1(8, "SHA256",					 "SHA256");		
		Init1(9, "ZILOG2",					 "ZILOG2");		
		Init1(10, "CHECKSUM_FUNCTIONS",		 "CHECKSUM_FUNCTIONS");		  	
	}	

	public void Print() {
		
		System.out.println("Checksum = "+Num);
		System.out.println("Descriptor: "+Descriptor);
		System.out.println("Algorithm = "+ObjsetType);		
	}
	
	public void PrintHashMaps() {

		PrintTools.PrintHashMapKeyVlaues(hashMapAlgorothm);
		PrintTools.PrintHashMapKeyVlaues(hashMapDescriptor);
	}
	
	static void fletcher_4(byte[] buf, int size) {		
		
		int[] bufi = VarTools.ByteArray2IntArray(buf, ByteOrder.LITTLE_ENDIAN); 		
		long a = 0;
		long b = 0;
		long c = 0;
		long d = 0; 			
		for (int ip=0; ip < bufi.length; ip++) {			
			a += bufi[0];
			b += a;
			c += b;
			d += c;
		}		
		checksum[0] = a;
		checksum[1] = b;
		checksum[2] = c;
		checksum[3] = d; 		
	}
	
	/*
	static void fletcher_4(const void* buf, uint64_t size, cksum_t* zcp)
	{
		const uint32_t* ip = (const uint32_t*)buf;
		const uint32_t* ipend = ip + (size / sizeof(uint32_t));

		uint64_t a, b, c, d;

		for(a = b = c = d = 0; ip < ipend; ip++)
		{
			a += ip[0];
			b += a;
			c += b;
			d += c;
		}

		zcp->set(a, b, c, d);
	}
	*/
	public void fletcher_8( ) {
		
		// https://stackoverflow.com/questions/35886276/what-is-the-correct-implementation-of-the-8-bit-fletcher-algorithm-in-java
		// https://stackoverflow.com/questions/8315215/checksum-algorithm-based-on-j-g-fletcher
		// https://en.wikipedia.org/wiki/Fletcher%27s_checksum#Straightforward 
		/*
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
		*/
	}
	public void fletcher_2( ) {
		
	}
	/*
	 static void fletcher_2(const void* buf, uint64_t size, cksum_t* zcp)
{
	const uint64_t* ip = (const uint64_t*)buf;
	const uint64_t* ipend = ip + (size / sizeof(uint64_t));

	uint64_t a0, b0, a1, b1;

	for(a0 = b0 = a1 = b1 = 0; ip < ipend; ip += 2)
	{
		a0 += ip[0];
		a1 += ip[1];
		b0 += a0;
		b1 += a1;
	}

	zcp->set(a0, a1, b0, b1);
}

 


	 */
	
	
}
