package desktop;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath; 

public class ZFSTree extends JTree {

	private static final long serialVersionUID = 1L;
	private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	
	public ZFSTree(DefaultTreeModel model) 
	{
		super(model);
		// Слушатель мыши
		addMouseListener(new MouseListener());
		// Определение собственного отображающего объекта
		setCellRenderer(new CheckBoxRenderer());
	}
	// Объект отображения узлов дерева с использованием флажков
	class CheckBoxRenderer extends JCheckBox implements TreeCellRenderer
	{
		private static final long serialVersionUID = 1L;
		public CheckBoxRenderer() {
			// Флажок будет прозрачным
			setOpaque(false);
		}
		// Метод получения компонента узла
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			                          boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			// Проверка принадлежности узла к стандартной модели
			if (!(value instanceof DefaultMutableTreeNode)) {
				// Если нет, то используется стандартный объект
				return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			}
			Object data = ((DefaultMutableTreeNode)value).getUserObject();
			// Проверка, являются ли данные CheckBoxElement
			if (data instanceof CheckBoxElement ) {
				CheckBoxElement element = (CheckBoxElement)data;
				// Настройка флажка и текста
				setSelected(element.selected);
				setText(element.text);
				return this;
			}
			// В противном случае метод возвращает стандартный объект
			return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
	}
	// Слушатель событий мыши
	class MouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			// Путь к узлу
			TreePath path = getClosestPathForLocation(e.getX(), e.getY());
			if (path == null) 
				return;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// Проверка принадлежности узла к стандартной модели
			if (node.getUserObject() instanceof CheckBoxElement) {
				// Изменение состояния флажка
				CheckBoxElement element = (CheckBoxElement)node.getUserObject();
				element.selected = ! element.selected;
				repaint();
			}
		}
	}
}
