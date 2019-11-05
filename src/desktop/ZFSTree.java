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
		// ��������� ����
		addMouseListener(new MouseListener());
		// ����������� ������������ ������������� �������
		setCellRenderer(new CheckBoxRenderer());
	}
	// ������ ����������� ����� ������ � �������������� �������
	class CheckBoxRenderer extends JCheckBox implements TreeCellRenderer
	{
		private static final long serialVersionUID = 1L;
		public CheckBoxRenderer() {
			// ������ ����� ����������
			setOpaque(false);
		}
		// ����� ��������� ���������� ����
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			                          boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			// �������� �������������� ���� � ����������� ������
			if (!(value instanceof DefaultMutableTreeNode)) {
				// ���� ���, �� ������������ ����������� ������
				return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			}
			Object data = ((DefaultMutableTreeNode)value).getUserObject();
			// ��������, �������� �� ������ CheckBoxElement
			if (data instanceof CheckBoxElement ) {
				CheckBoxElement element = (CheckBoxElement)data;
				// ��������� ������ � ������
				setSelected(element.selected);
				setText(element.text);
				return this;
			}
			// � ��������� ������ ����� ���������� ����������� ������
			return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
	}
	// ��������� ������� ����
	class MouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			// ���� � ����
			TreePath path = getClosestPathForLocation(e.getX(), e.getY());
			if (path == null) 
				return;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			// �������� �������������� ���� � ����������� ������
			if (node.getUserObject() instanceof CheckBoxElement) {
				// ��������� ��������� ������
				CheckBoxElement element = (CheckBoxElement)node.getUserObject();
				element.selected = ! element.selected;
				repaint();
			}
		}
	}
}
