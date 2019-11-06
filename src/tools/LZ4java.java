package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;
import net.jpountz.lz4.LZ4SafeDecompressor;

public class LZ4java {
	
	private static final Logger log = Logger.getLogger(LZ4java.class.getName()); 
	
	private static LZ4Factory factory 				 = LZ4Factory.fastestInstance();
	private static LZ4Compressor compressor			 = factory.fastCompressor(); 
	private static LZ4FastDecompressor decompressor  = factory.fastDecompressor();
	private static LZ4SafeDecompressor decompressor2 = factory.safeDecompressor();
	
	static final int DEFAULT_COMPRESSION_LEVEL = 8+1;
	static final int MAX_COMPRESSION_LEVEL = 16+1;
	static final int LZ4_MAX_BLOCK_SIZE = 65536;
		
	public static byte[] lz4Compress(byte[] data) {
			
		byte[] compressed = null;
		compressed = compressor.compress(data, 0, data.length);
		return compressed;		
	}	

	public static byte[] lz4DecompressFast(byte[] data, int len) {

		byte[] restored = new byte[len]; 
		decompressor.decompress(data, 0, restored, 0, len);
		return restored;			
	}

	public static byte[] lz4DecompressSlow(byte[] data) {
		 
		byte[] restored = new byte[LZ4_MAX_BLOCK_SIZE];
		int decompressedLength = decompressor2.decompress(data, 0, data.length, restored, 0); 
		byte[] restored2 = new byte[decompressedLength];
		for (int i = 0; i<decompressedLength; i++)
			restored2[i] = restored[i];		
		System.out.println("Decompress size (len) = " + restored2.length);
		return restored2;			
	}
	
	public static void lz4CompressFileFromStream(String sFile, byte[] data) {		
		// некорректный
		System.out.println("Compress.");
		byte[] compressed = lz4Compress(data);
		System.out.println("Compress byte[]. Size from "+data.length+" to " + compressed.length);
		try {
			File f = new File(sFile);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(compressed);
			fos.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
		System.out.println("Save to file " + sFile);		
	}

	public static byte[] lz4DecompressFileFromStream(String sFile, int len) {

		log.info("Read file " + sFile + " to byte[].");
		byte[] restored = new byte[0];  
		try {
			File f = new File(sFile);
			restored = new byte[(int) f.length()];
			FileInputStream fis = new FileInputStream(f); 
			fis.read(restored);
			fis.close();
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
		log.info("Restored strint to size = " + restored.length);	
		return lz4DecompressSlow(restored);
	}

	public static void lz4CompressFile(String sFile, byte[] data) {
		
		LZ4FrameOutputStream outStream;
		log.info("Compress byte[] to file " + sFile);
		try {
			File f = new File(sFile);
			FileOutputStream fos = new FileOutputStream(f);
			outStream = new LZ4FrameOutputStream(fos);
			outStream.write(data);
			outStream.close();
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}		
	}

	public static byte[] lz4DecompressFile(String sFile, int decompressedLength) {

		log.info("Decompress. File " + sFile);
		byte[] restored = new byte[decompressedLength];
		LZ4FrameInputStream inStream;
		try {
			File f = new File(sFile);
			FileInputStream fis = new FileInputStream(f);
			inStream = new LZ4FrameInputStream(fis);
			inStream.read(restored);
			inStream.close();
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}	
		log.info("Restored. Size = " + restored.length);
		return restored;	
	}	
	
	public static void testZFS() {

		String sIn = "12345dfgsdf6454hsdfga123456a34345dkjsadkljfaskjdfajdnfasdf"+
				"fg735ghdiifghdfgh35673475645632342345678356723546234523673567345234572";
		sIn = sIn + sIn; // 256
		sIn = sIn + sIn; // 512
		sIn = sIn + sIn; // 1024		
		
		byte[] data = null;
		try {
			data = sIn.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		String sF1 = "1.lz4";
		System.out.println("testZFS. File " + sF1);
		LZ4java.lz4CompressFileFromStream(sF1, data);			
		System.out.println("LZ4 file size = " + (new File(sF1)).length());	

		String sF2 = "2.lz4";
		System.out.println("testZFS. File " + sF2);
		LZ4java.lz4CompressFile(sF2, data);			
		System.out.println("LZ4 file size = " + (new File(sF2)).length());		

		byte[] restored1 = LZ4java.lz4DecompressFileFromStream(sF1, data.length);	
		String s1 = new String(restored1);
		System.out.println("\nDecompressed string size = " + s1.length());
		System.out.println("String compare: " + (s1.equals(sIn) ? "Ok!" : "Error..."));	
		
		byte[] restored2 = LZ4java.lz4DecompressFile(sF2, data.length);	
		String s2 = new String(restored2);
		System.out.println("\nDecompressed string size = " + s2.length());
		System.out.println("String compare: " + (s2.equals(sIn) ? "Ok!" : "Error..."));	
	}	
	
}
