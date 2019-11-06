package desktop;

import java.util.ArrayList; 
import org.apache.log4j.Logger;

/*
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
*/
import hdd.PhysicalDrive;
import tools.FileTools;  

public class ZSFMain { // extends JFrame {

	// TRACE, DEBUG, INFO, WARN, ERROR and FATAL
	private static final Logger log = Logger.getLogger(ZSFMain.class.getName()); 
	
	static long serialVersionUID 	= 1L;
	static int BestZfsLabel 		= 2;
	static String Ver 				= "0.0.1";
	static String ZFSDrive			= "\\\\.\\PhysicalDrive1";
	static String ZFSImageDir		= "K:\\zfs\\";
	static String ZFSImageDirTests 	= ZFSImageDir + "Images_zfsonline\\vdevs\\";
	static String ZFSImage			= ZFSImageDirTests + "zol-0.6.1\\vdev0";
	static PhysicalDrive pdDrv;

	//final   String     ROOT  = "Корневая запись";
	// Массив листьев деревьев
	//final   String[]   nodes = new String[]  {"Транзакции (TXG)", "ZFS label"};
	//final   String[][] leafs = new String[][]{{"1", "2"}, {"0", "1"}};	
	
	public static void main(String[] args) {	
		
		log.info("PhysicalDrive info Ver "+Ver+" \n");
		ZfsTestDDs(ZFSImageDirTests);
		ZfsTestDrive(ZFSDrive);
		if (args.length>0) { 
			ZFSTools(args);
		}
		log.info("End ZSFMain.");				
	}

	private static void ZfsTestDrive(String zFSDrive) {
		 
		pdDrv = new PhysicalDrive(ZFSDrive, true); 
		pdDrv.LogFile=ZFSImageDir;
	}

	private static void ZfsTestDDs(String dir) {
		
		ArrayList<String> ss = FileTools.GetFilesFromDir(null,dir);
		for (String s: ss) { 		 
			PhysicalDrive pd = new PhysicalDrive(s, false);
			pd.PrintZDB(); 
		}		
	}

	private static void ZFSTools(String[] args) {

		if (args.length>1) {
			if (args[0].equals("zdb"))  
				if (args[1].equals("-l")) 
					pdDrv.PrintZDB();   
		} else 
			PrintHelp();			
	}

	private static void PrintHelp() {
		
		log.info("Help to ZSFProject.\n");	
		log.info("Run: ZSFProject <params>\n");	
		log.info("Params (output is equal to zfs command):");	
		log.info("zdb -l <device> - list ZFS label properties");			
	}
	/*
	 private ZSFMain() {

		super("ZFS viewer "+ZFSDrive);
		System.out.println("ZSFMain start");	
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Создание модели дерева
		DefaultTreeModel model = createZFSModel();
		// Создание дерева
		ZFSTree tree = new ZFSTree(model);
		// Размещение дерева в интерфейсе
		getContentPane().add(new JScrollPane(tree));
		// Вывод окна на экран
		setSize(800, 600);
		setVisible(true);		
		System.out.println("ZSFMain end");
	}
	
	private DefaultTreeModel createZFSModel() {
		System.out.println("createZFSModel ");	
		// Корневой узел дерева
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ROOT);
		// Добавление ветвей - потомков 1-го уровня
		DefaultMutableTreeNode drink = new DefaultMutableTreeNode(nodes[0]);
		DefaultMutableTreeNode sweet = new DefaultMutableTreeNode(nodes[1]);
		// Добавление ветвей к корневой записи
		root.add(drink);
		root.add(sweet);
		// Добавление листьев - потомков 2-го уровня
		for ( int i = 0; i < leafs[0].length; i++)
			drink.add(new DefaultMutableTreeNode(new CheckBoxElement(false, leafs[0][i])));
		for ( int i = 0; i < leafs[1].length; i++)
			sweet.add(new DefaultMutableTreeNode(new CheckBoxElement(false, leafs[1][i])));
		// Создание стандартной модели
		return new DefaultTreeModel(root);
	}
	 */
	
		
}

