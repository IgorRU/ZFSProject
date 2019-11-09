package hdd;

import org.apache.log4j.Logger;

public class HSC {
	
	private static final Logger log = Logger.getLogger(HSC.class.getName()); 

	public int Head = 0;
	public int Sector = 0;
	public int Cilinder = 0;
	public boolean isBegin;
	private String HSCBytes;
	
	public HSC(byte b1, byte b2, byte b3, boolean isBegin) {

		log.trace("HSC");
		this.isBegin = isBegin;
		Head     =  b1;
		Sector   =  b2 & 0xFC ;
		Cilinder = (b2 & 0x03)*0xFF+b3;
		HSCBytes = String.format(" %02X",b1)+String.format(" %02X",b2)+String.format(" %02X",b3);
		//Print();
	}

	public void Print() { 

		log.trace((isBegin ? "Начало раздела:" : "Конец раздела:")); 
		log.trace("Hex   (HSC) = " + HSCBytes); 	
		log.trace("Головка (H) = " + Head); 	
		log.trace("Сектор  (S) = " + Sector);  	
		log.trace("Цилиндр (С) = " + Cilinder);  		
	}
}
