package desktop;

import org.apache.log4j.Logger;

/**
 * Класс определения параметров элемента узла дерева 
 */
public class CheckBoxElement {
	
	private static final Logger log = Logger.getLogger(CheckBoxElement.class.getName()); 
	// Данные узла
	public boolean selected;
	public String  text;
	// Конструктор
	public CheckBoxElement(boolean selected, String text)
	{
		log.info("CheckBoxElement");
		this.selected = selected;
		this.text = text;
	}
}
