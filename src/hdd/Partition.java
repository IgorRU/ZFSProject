package hdd;

import tools.VarTools;

public class Partition {

	private int Num;
	private int offset;
	private boolean isActive;
	public  boolean isFree = false;
	private HSC HSCStart;
	private HSC HSCEnd;
	private String Offset1Sector;
	private String SectorCount;
	private int PartNum;
	private String PartName;

	public Partition(byte[] b, int PartOffset, int nPart) {
		
		Num = nPart;
		offset = PartOffset;
	    byte n = b[offset+0x00]; // 80h � ������ �������� ��������, 00h � ����������
	    isActive = (n==-1);
	    isFree   = (b[offset+0x04]==0);
	    HSCStart = new HSC(b[offset+0x01],b[offset+0x02],b[offset+0x03],true); 
		PartNum  = b[offset+0x04]+0x100;
	    HSCEnd   = new HSC(b[offset+0x05],b[offset+0x06],b[offset+0x07],false);
	    Offset1Sector = VarTools.ByteArray2HexsStr(b, offset+0x08,4);
	    SectorCount   = VarTools.ByteArray2HexsStr(b, offset+0x0C,4);
		PartName = (new PartitionType()).GetPartitionName(PartNum);
	}

	public void Print() {

	    System.out.println("Partition "+Num+":");
		System.out.println("���������� �������  = "+ (isActive ? "��" : "���")); 
	    System.out.println((isFree ? "��������� �����" : "������ ������."));
	    HSCStart.Print();
	    HSCEnd.Print();
		System.out.println("�������� ������� ������� = 0x" + Offset1Sector); 		
		System.out.println("���������� �������� ������� = 0x" + SectorCount); 	
		System.out.println("��� ���� ������� = "+PartName+" (0x"+String.format("%02X", PartNum)+")"); 
	}	
}
