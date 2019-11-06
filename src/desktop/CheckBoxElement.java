package desktop;

import org.apache.log4j.Logger;

/**
 * ����� ����������� ���������� �������� ���� ������ 
 */
public class CheckBoxElement {
	
	private static final Logger log = Logger.getLogger(CheckBoxElement.class.getName()); 
	// ������ ����
	public boolean selected;
	public String  text;
	// �����������
	public CheckBoxElement(boolean selected, String text)
	{
		log.info("CheckBoxElement");
		this.selected = selected;
		this.text = text;
	}
}
