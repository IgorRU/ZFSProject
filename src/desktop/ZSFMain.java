package desktop;

import java.util.ArrayList;
/*
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
*/
import hdd.PhysicalDrive;
import tools.FileTools;
//import tools.LZJBjava;
//import tools.VarTools; 

public class ZSFMain { // extends JFrame {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	static String Ver 		= "0.0.1";
	static String ZFSDrive	= "\\\\.\\PhysicalDrive1";
	static String ZFSImage	= "K:\\zfs\\Images_zfsonline\\vdevs\\zol-0.6.1\\vdev0";
	private static PhysicalDrive pd;

	//final   String     ROOT  = "�������� ������";
	// ������ ������� ��������
	//final   String[]   nodes = new String[]  {"���������� (TXG)", "ZFS label"};
	//final   String[][] leafs = new String[][]{{"1", "2"}, {"0", "1"}};	
	
	public static void main(String[] args) {	
			
		System.out.println("PhysicalDrive info Ver "+Ver+" \n");
		
		ArrayList<String> ss = FileTools.GetFilesFromDir(null, 
				"K:\\zfs\\Images_zfsonline\\vdevs\\");
		for (String s: ss) {
			System.out.println(s);				
			//pd = new PhysicalDrive(ZFSDrive);
			//pd = new PhysicalDrive(ZFSImage, false);
			pd = new PhysicalDrive(s, false);
		}
		if (args.length>0) { 
			ZFSTools(args);
		}
		System.out.println("\nEnd.");				
	}

	private static void ZFSTools(String[] args) {

		 if (args[0].equals("zdb")) { 
			pd.zfs3.L[2].PrintZDB(args, ZFSDrive, true);  
		} else 
			PrintHelp(args);			
	}

	private static void PrintHelp(String[] args) {
		
		System.out.println("Help to ZSFProject.\n");	
		System.out.println("Run: ZSFProject <params>\n");	
		System.out.println("Params (output is equal to zfs command):");	
		System.out.println("zdb -l <device> - list ZFS label properties");			
	}
	/*
	 private ZSFMain() {

		super("ZFS viewer "+ZFSDrive);
		System.out.println("ZSFMain start");	
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// �������� ������ ������
		DefaultTreeModel model = createZFSModel();
		// �������� ������
		ZFSTree tree = new ZFSTree(model);
		// ���������� ������ � ����������
		getContentPane().add(new JScrollPane(tree));
		// ����� ���� �� �����
		setSize(800, 600);
		setVisible(true);		
		System.out.println("ZSFMain end");
	}
	
	private DefaultTreeModel createZFSModel() {
		System.out.println("createZFSModel ");	
		// �������� ���� ������
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ROOT);
		// ���������� ������ - �������� 1-�� ������
		DefaultMutableTreeNode drink = new DefaultMutableTreeNode(nodes[0]);
		DefaultMutableTreeNode sweet = new DefaultMutableTreeNode(nodes[1]);
		// ���������� ������ � �������� ������
		root.add(drink);
		root.add(sweet);
		// ���������� ������� - �������� 2-�� ������
		for ( int i = 0; i < leafs[0].length; i++)
			drink.add(new DefaultMutableTreeNode(new CheckBoxElement(false, leafs[0][i])));
		for ( int i = 0; i < leafs[1].length; i++)
			sweet.add(new DefaultMutableTreeNode(new CheckBoxElement(false, leafs[1][i])));
		// �������� ����������� ������
		return new DefaultTreeModel(root);
	}
	 */
	
		
}
