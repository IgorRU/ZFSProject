package desktop;

/**
 * Класс определения параметров элемента узла дерева 
 */
public class CheckBoxElement
{
	// Данные узла
	public boolean selected;
	public String  text;
	// Конструктор
	public CheckBoxElement(boolean selected, String text)
	{
		this.selected = selected;
		this.text = text;
	}
}
