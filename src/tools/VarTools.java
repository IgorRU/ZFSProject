package tools;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class VarTools {

	static public byte[] StrToByteArray(String s) {
		
		//byte[] b = s.getBytes(Charset.forName("UTF-8"));
		//byte[] b = s.getBytes(StandardCharsets.UTF_8); 		
		byte[] b = s.getBytes();
        return b;
}	
	
	static public String bytesToHex(byte[] hash) {
	        StringBuffer hexString = new StringBuffer();
	        for (byte h : hash) {
	            String hex = Integer.toHexString(0xff & h);
	            if (hex.length() == 1)
	                hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	}	
	
   static public long ByteArray2Long1(byte[] by, int offset, int count) {
		
		long value = 0;
		for (int i = offset; i < count+offset; i++) 
		   value += ((long) by[i] & 0xffL) << (8 * (i - offset)); 
		return value;
	}
	
   static public long ByteArray2LongN(byte[] by, int offset, int count) {
		
		long value = 0;
		for (int i = offset; i < count+offset; i++) 
		   value = (value << 8) + (by[i] & 0xff); 
		return value;
	}
	
   static public String ByteArray2HexsStr(byte[] b, int nu, int nnu) {

		String s = "";
		for (int i = nu; i<nnu+nu; i++)
			s = s + String.format( "%02X",b[i]);
		return s;
	}
	
   static public String ByteArray2Str(byte[] b, int nu, int nnu) {

		byte[] bb = new byte[nnu];
		for (int i = nu; i<nnu+nu; i++)
			bb[i-nu] = b[i];
		return new String(bb);
	}
   
   static public String byteArray2GUID(byte[] barr, int offset) {
		String s = "";
		for (int i=0; i<4; i++)
			s = s + String.format("%02X",barr[offset+i]); 
		s = s + "-";
		for (int i=0; i<2; i++)
			s = s + String.format("%02X",barr[offset+4+i]); 
		s = s + "-";
		for (int i=0; i<2; i++)
			s = s + String.format("%02X",barr[offset+6+i]); 
		s = s + "-";
		for (int i=0; i<2; i++)
			s = s + String.format("%02X",barr[offset+8+i]); 
		s = s + "-";
		for (int i=0; i<6; i++)
			s = s + String.format("%02X",barr[offset+10+i]); 		
		return s;
	}

   static public byte[] byteArray2byteArrayShort(byte[] barr,int start, int nb) {
		byte[] b = new byte[nb];
		for (int i=0; i<nb; i++)
			b[i]=barr[start+i]; 
		return b;
	}

	public static long ByteArray2Long(byte[] bs, int offset, int len) {
	
		byte[] b = byteArray2byteArrayShort(bs, offset, len);  
		ByteBuffer buffer = ByteBuffer.wrap(b);
		if (len==16)
			return buffer.getLong(); 
		if (len==8)
			return buffer.getLong(); 
		if (len==3)		
			return b[0]*16*16+b[1]*16+b[2]; 
		return -1;
	}
	public static int ByteArray2Int(byte[] bs, int offset) {
		
		byte[] b = byteArray2byteArrayShort(bs, offset, 4);  
		ByteBuffer buffer = ByteBuffer.wrap(b);
		return buffer.getInt(); 
	} 
	
	public static int ByteArray2ShortByte(byte[] bs, int offset) {
		
		byte[] b = byteArray2byteArrayShort(bs, offset, 2);  
		ByteBuffer buffer = ByteBuffer.wrap(b);
		return buffer.getShort(); 
	} 
	
	public static String Int2BitsStr(int param) {
		
		String s = "";
	    int mask = 1 << 31;	
	    for (int i = 1; i <= 32; i++, param <<= 1) {
	       s = s + ((param & mask) == 0 ? "0" : "1");
	       if (i % 8 == 0)
	    	   s = s + " ";
	    }
	    System.out.println(param +" = "+s);
		return s;
	 }
	
	public static String Long2BitsStr(long param) {
		
		System.out.println(String.format("%08X: ",param));
		String s = "";
	    long mask = 1 << 63;	
	    for (int i = 1; i <= 64; i++, param <<= 1) {
	       
	    	System.out.print((param & mask) == 0 ? "0" : "1");
	       s = s + ((param & mask) == 0 ? "0" : "1");
	       if (i % 8 == 0)
	    	   s = s + " ";
	    }
	    //System.out.println(param +" ==== "+s);
		return s;
	 }
	
	public static void showBits(int param) {
	    
		String s = "";
		int mask = 1 << 31;
	    for (int i = 1; i <= 32; i++, param <<= 1) {
	         System.out.print((param & mask) == 0 ? "0" : "1");
	         s = s + ((param & mask) == 0 ? "0" : "1");
	         if (i % 8 == 0)
	            System.out.print(" ");
	     }
	     System.out.println();
	     System.out.println(s);
	}

	public static BigInteger ByteArray2BigInt(byte[] bs, int n) {
		
		byte[] b = new byte[8];
		for (int i=0; i<8; i++)
			b[i]=bs[n+i]; 		
		BigInteger bi = new BigInteger(b);
		// TODO Auto-generated method stub
		return bi;
	}

	public static boolean CheckNull(byte[] bs, int n, int len) {

		for (int i=n; i<n+len; i++)
			if (bs[i]!=0)
				return false; 		
		return true;
	}

	/** the constant 2^64 */
	private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);

	public static String asUnsignedDecimalString(long l) {
	   BigInteger b = BigInteger.valueOf(l);
	   if(b.signum() < 0) {
	      b = b.add(TWO_64);
	   }
	   return b.toString();
	}
	
}
