package tools;

import org.apache.log4j.Logger;

public class LZJBjava {
	
	private static final Logger log = Logger.getLogger(LZJBjava.class.getName()); 

	private static final int TABLE_SIZE  = 256;
	private static final int MATCH_BITS  = 6;
	private static final int MATCH_MIN   = 3;
	private static final int MATCH_MAX   = ((1 << MATCH_BITS) + (MATCH_MIN - 1));
	private static final int OFFSET_MASK = ((1 << (16 - MATCH_BITS)) - 1); 
	
	public static byte[] compress(byte[] b) {
		
		String s = compress(new String(b));	
		return s.getBytes();		
	}
	
	public static byte[] decompress(byte[] b) {
		
		String s = decompress(new String(b));	
		return s.getBytes();		
	}
	
	public static String compress(String text) {
		
		int[]  table = new int[TABLE_SIZE];
		char[] input = text.toCharArray();
		StringBuffer output = new StringBuffer();		
		int len = input.length;
		int src = 0;
		int dst = 0;
		int cpy = 0;
		int copymask = 1 << 7;	// next indicator bit to be set
		int copymap = 0;		// last indicator byte
		
		for (int i = 0; i < TABLE_SIZE; ++i) 
			table[i] = 0xdeadbeef;		
		while (src < len) {
			
			copymask <<= 1;			
			if (copymask == 1 << 8) {
				// need next indicator byte.
				copymask = 1;
				copymap  = dst;
				output.append((char)0);
				dst++;
			}			
			if (src > len - MATCH_MAX) {
				// better not match past this point
				output.append(input[src++]);
				dst++;
				continue;
			}			
			// simple hash function
			int hash = (((int)(input[src]) + 13) ^
					    ((int)(input[src + 1]) - 13) ^
					    ((int)(input[src + 2]))) % TABLE_SIZE;			
			// hash-table lookup
			int offset = (src - table[hash]) & OFFSET_MASK;			
			// store current trigram position to hash table
			table[hash] = src;
			cpy = src - offset;			
			// check whether real match found or overwritten table entry
			if (cpy >= 0 && cpy != src && 
					input[src] == input[cpy] &&
					input[src + 1] == input[cpy + 1] &&
					input[src + 2] == input[cpy + 2]) {
				
				// toggle indicator bit in indicator byte.
				output.setCharAt(copymap, (char) (output.charAt(copymap) | copymask));				
				// do forward matching from there
				int mlen;
				for (mlen = MATCH_MIN; mlen < MATCH_MAX; ++mlen)  
					if (input[src + mlen] != input[cpy + mlen]) 
						break; 				
				// output 2 bytes, 6 bit length + 10 bit offset
				output.append( (char)(
						    ((mlen - MATCH_MIN ) << (8 - MATCH_BITS)) |
						    (offset >> 8) ));
				output.append( (char)(offset & 0xff) );
				src += mlen;
				dst += 2;				
			// no match, just emit uncompressed symbol
			} else {
				output.append(input[src++]);
				dst++;
			}			
		}
		return output.toString();
	}
	
	public static String decompress(String source) {
		
		char[] input = source.toCharArray();
		StringBuffer output = new StringBuffer();		
		int src = 0;
		int dst = 0;
		int cpy = 0;
		int copymask = 1 << 7;
		int copymap = 0;
		int len = input.length;		
		while (src < len) {
			
			copymask <<= 1;			
			if (copymask == 1 << 8) {
				// indicator byte. just read it.
				copymask = 1;
				copymap = input[src++];
			}			
			// indicator byte indicates a backward reference here
			if ((copymap & copymask) > 0) {				
				// read and decode length and offset of reference
				int mlen = (input[src] >> (8 - MATCH_BITS)) + MATCH_MIN;
				int offset = ((input[src] << 8) | input[src + 1]) & OFFSET_MASK;
				src += 2;
				cpy = dst - offset;				
				// copy all bytes from reference
				if (cpy >= 0) {
					while (--mlen >= 0) {
						output.append(output.charAt(cpy));
						cpy++;
						dst++;
					}
				} else {
					// stream broken. just emit what we have and cancel.
					return output.toString();
					//throw new RuntimeException("Compressed data corrupted");
				}			
			// no match, just emit uncompressed symbol
			} else {
				output.append(input[src++]);
				dst++;
			}
		}		
		return output.toString();
	}

	public static void Test() {
		
		String s = "11111aaaaa33333ööööö5555566666666666666666666666666666666"+
				"666666666666666666666666666666666666666666666666666666666666" + 
				"666666666666666666666666666666666666666666666666666666666666" + 
				"66666666666666666666666999990000000000888889999900000";	
		String out = LZJBjava.compress(s); 
		log.info(out);	
		log.info(VarTools.bytesToHex(out.getBytes()));		
		String s2 = LZJBjava.decompress(out);
		log.info("Source size is "+s.length()+"-> target"+out.length()+" "+
				(s.equals(s2) ? "Ok!" : "Bad"));		
	}
}
