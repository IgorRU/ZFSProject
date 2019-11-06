package hdd;

import java.util.HashMap;

import org.apache.log4j.Logger;

import tools.PrintTools;

public class PartitionType {
	
	private static final Logger log = Logger.getLogger(PartitionType.class.getName()); 
	
	public HashMap<Integer, String> hashMap     = new HashMap<>();
	public HashMap<String,  String> hashMapGUID = new HashMap<>();
	
	public PartitionType() {
	
		log.trace("PartitionType");
		hashMap.put(0x00, "Пустая запись (свободное место)");
		hashMap.put(0x01, "FAT-12 (если это логический раздел или раздел расположен"+
				" в первых 32 мегабайтах диска, иначе используется код 06h)");
		hashMap.put(0x02, "XENIX root");
		hashMap.put(0x03, "XENIX usr");
		hashMap.put(0x04, "FAT-16 до 32 Мбайт (если раздел первичный, то должен "+
				"находиться в первых физических 32 Мб диска, иначе используется код 06h)");
		hashMap.put(0x05, "Расширенный раздел");
		hashMap.put(0x06, "FAT-16B, а также FAT-16, не попадающий под условия кода 04h"+
				" и FAT-12, не попадающий под условия кода 01h");
		hashMap.put(0x07, "IFS, HPFS, NTFS, exFAT (и некоторые другие — тип определяется по содержимому загрузочной записи)");
		hashMap.put(0x08, "AIX");
		hashMap.put(0x09, "AIX загрузочный");
		hashMap.put(0x0A, "OS/2 Boot менеджер, OPUS");
		hashMap.put(0x0B, "FAT-32");
		hashMap.put(0x0C, "FAT-32X (FAT-32 с использованием LBA)");
		hashMap.put(0x0D, "Зарезервирован");
		hashMap.put(0x0E, "FAT-16X (FAT-16 с использованием LBA) (VFAT)");
		hashMap.put(0x0F, "Расширенный раздел LBA (то же что и 05h, с использованием LBA)");
		hashMap.put(0x10, "OPUS");
		hashMap.put(0x11, "Скрытый FAT (аналогичен коду 01h)");		
		hashMap.put(0x12, "Compaq, Сервисный раздел");
		hashMap.put(0x14, "Скрытый FAT (аналогичен коду 04h)");
		hashMap.put(0x15, "Скрытый Расширенный раздел (аналогичен коду 05h)");
		hashMap.put(0x16, "Скрытый FAT (аналогичен коду 06h)");
		hashMap.put(0x17, "Скрытый раздел HPFS/NTFS/IFS/exFAT");
		hashMap.put(0x18, "AST SmartSleep");
		hashMap.put(0x19, "OFS1");
		hashMap.put(0x1B, "	Скрытый раздел FAT-32 (см. 0Bh)");
		hashMap.put(0x1C, "Скрытый раздел FAT-32X (см. 0Ch)");
		hashMap.put(0x1E, "Скрытый раздел FAT-16X (VFAT) (см. 0Eh)");
		hashMap.put(0x1F, "Скрытый Расширенный раздел LBA (см. 0Fh)");
		hashMap.put(0x20, "OFS1");
		hashMap.put(0x21, "FSo2");
		hashMap.put(0x22, "Расширенный раздел FS02");
		hashMap.put(0x24, "NEC DOS");
		hashMap.put(0x25, "Windows Mobile IMGFS");
		hashMap.put(0x27, "Скрытый NTFS (Раздел восстановления системы)");
		hashMap.put(0x28, "Зарезервирован для FAT-16+");
		hashMap.put(0x29, "Зарезервирован для FAT-32+");
		hashMap.put(0x2A, "AFS (AthFS)");
		hashMap.put(0x35, "JFS");
		hashMap.put(0x38, "THEOS 3.2");
		hashMap.put(0x39, "Plan 9");
		hashMap.put(0x3A, "THEOS 4");
		hashMap.put(0x3B, "Расширенный раздел THEOS 4");
		hashMap.put(0x3C, "Partition Magic, NetWare");
		hashMap.put(0x3D, "Скрытый раздел NetWare");
		hashMap.put(0x40, "Venix 80286, PICK R83");
		hashMap.put(0x41, "Старый Linux/Minix, PPC PReP Boot");
		hashMap.put(0x42, "Старый своп Linux, SFS");
		hashMap.put(0x43, "Старый Linux");
		hashMap.put(0x4A, "ALFS");
		hashMap.put(0x4C, "A2 (Aos)");
		hashMap.put(0x4D, "QNX4.x");
		hashMap.put(0x4E, "QNX4.x 2-я часть");
		hashMap.put(0x4F, "QNX4.x 3-я часть");
		hashMap.put(0x50, "OnTrack DM (только чтение)");
		hashMap.put(0x51, "OnTrack DM6 (чтение и запись)");
		hashMap.put(0x52, "CP/M");
		hashMap.put(0x53, "OnTrack DM6 Aux3");
		hashMap.put(0x54, "OnTrack DM6 DDO");
		hashMap.put(0x55, "EZ-Drive");
		hashMap.put(0x56, "Golden Bow");
		hashMap.put(0x56, "Novell VNDI");
		hashMap.put(0x5C, "Priam Edisk");
		hashMap.put(0x61, "SpeedStor");
		hashMap.put(0x62, "GNU HURD");
		hashMap.put(0x63, "UNIX");
		hashMap.put(0x64, "NetWare (0x64)");
		hashMap.put(0x65, "NetWare (0x65)");
		hashMap.put(0x66, "NetWare (0x66)");
		hashMap.put(0x67, "NetWare (0x67)");
		hashMap.put(0x68, "NetWare (0x68)");
		hashMap.put(0x69, "NetWare (0x69)");
		hashMap.put(0x77, "VNDI, M2FS, M2CS");
		hashMap.put(0x78, "XOSL");
		hashMap.put(0x7F, "Данный код зарезервирован для исследовательских или учебных проектов");
		hashMap.put(0x81, "MINIX");
		hashMap.put(0x82, "Linux swap, Sun Solaris (старый)");
		hashMap.put(0x83, "Linux");
		hashMap.put(0x85, "Linux extended (расширенный)");
		hashMap.put(0x86, "Раздел FAT-16 stripe-массива Windows NT");
		hashMap.put(0x87, "Раздел NTFS/HPFS stripe-массива Windows NT");
		hashMap.put(0x8E, "Раздел LVM");
		hashMap.put(0x93, "Amoeba, Скрытый Linux (см. код 83h)");
		hashMap.put(0x94, "Amoeba BBT");
		hashMap.put(0x94, "ISO-9660");
		hashMap.put(0x9E, "ForthOS");
		hashMap.put(0xA5, "Раздел гибернации");
		hashMap.put(0xA5, "NetBSD (старый),FreeBSD,BSD/386");
		hashMap.put(0xA6, "OpenBSD");
		hashMap.put(0xA7, "NeXTSTEP");
		hashMap.put(0xA8, "Apple Darwin, Mac OS X UFS");
		hashMap.put(0xA9, "NetBSD");
		hashMap.put(0xAF, "Mac OS X HFS и HFS+, ShangOS");
		hashMap.put(0xB1, "QNX6.x");
		hashMap.put(0xB2, "QNX6.x");
		hashMap.put(0xB3, "QNX6.x");
		hashMap.put(0xB6, "Зеркальный master-раздел FAT-16 Windows NT");
		hashMap.put(0xB7, "Зеркальный master-раздел NTFS/HPFS Windows NT");
		hashMap.put(0xBE, "Solaris 8 загрузочный");
		hashMap.put(0xBF, "Solaris");
		hashMap.put(0xC2, "Скрытый Linux");
		hashMap.put(0xC3, "Скрытый своп Linux");
		hashMap.put(0xC6, "Зеркальный slave-раздел FAT-16 Windows NT");
		hashMap.put(0xC7, "Зеркальный slave-раздел NTFS Windows NT");
		hashMap.put(0xCD, "Дамп памяти");
		hashMap.put(0xD8, "CP/M-86");
		hashMap.put(0xDA, "Данные — не файловая система");
		hashMap.put(0xDB, "CP/M-86");
		hashMap.put(0xDD, "Скрытый дамп памяти");
		hashMap.put(0xDE, "Dell Utility");
		hashMap.put(0xEB, "BFS");
		hashMap.put(0xEC, "SkyOS");
		hashMap.put(0xED, "Гибридный GPT");
		hashMap.put(0xEE, "GPT");
		hashMap.put(0xEF, "Системный раздел UEFI");
		hashMap.put(0xF7, "EFAT, SolidState");
		hashMap.put(0xFB, "VMFS");
		hashMap.put(0xFC, "Своп VMFS");
		hashMap.put(0xFE, "LANstep, PS/2 IML");
		hashMap.put(0xFF, "XENIX BBT");
		
		hashMapGUID.put("00000000-0000-0000-0000-000000000000", "Unused entry");
	}
	public String GetPartitionGUID(String GUID) {		
		
		return PrintTools.GetHashMapKeyVlaue(hashMapGUID, GUID);
	}

	public String GetPartitionName(int part) {				
		
		return PrintTools.GetHashMapKeyVlaue(hashMap, part);
	}
}
