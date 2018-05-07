package cn.harry12800.lnk.diary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cn.harry12800.j2se.dialog.InputMessageDialog;
import cn.harry12800.j2se.dialog.InputMessageDialog.Callback;
import cn.harry12800.j2se.style.UI;
import cn.harry12800.j2se.utils.Clip;
import cn.harry12800.lnk.diary.entity.Aritcle;
import cn.harry12800.tools.FileUtils;

/**
 * 日志的目录面板
 * 
 * @author Yuexin
 * 
 */
public class CatalogPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	FileFilter filter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				char charAt = file.getName().charAt(0);
				boolean mark = true;
				if (charAt <= 'z' && charAt >= 'a') {
					mark = false;
				}
				return mark;
			}
			return file.getName().endsWith(".properties");
		}
	};
	JTree catalogTree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private DiaryPanel diaryPanel;

	public CatalogPanel(final DiaryPanel diaryPanel) {
		setLayout(new BorderLayout());
		this.diaryPanel = diaryPanel;
		root = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		setOpaque(false);
		File file = new File(diaryPanel.dirPath);
		File[] listFiles = file.listFiles(filter);
		int x =0 ;
		for (final File f : listFiles) {
			if (f.isDirectory()) {
				CategoryNode node = new CategoryNode(diaryPanel, f);
				root.add(node);
				int i =1;
				for (File file2 : f.listFiles(filter)) {
					Aritcle a = new Aritcle();
					a.sort = i;
					i++;
					AricleNode newChild = new AricleNode(diaryPanel,file2,a );
					node.add(newChild);
				}
			} else {
				x++;
				Aritcle a = new Aritcle();
				a.sort = x;
				AricleNode node = new AricleNode(diaryPanel, f,a);
				root.add(node);
			}
		}
		catalogTree = new JTree(model);
		catalogTree.setToggleClickCount(1);// 点击次数
		catalogTree.setOpaque(false);
		catalogTree.setBackground(Color.YELLOW);
		catalogTree.setRootVisible(false);// 隐藏根节点
		catalogTree.setDragEnabled(true);
		catalogTree.putClientProperty("JTree.lineStyle", "None");
		catalogTree.setCellRenderer(new TreeNodeRenderer());
		catalogTree.setUI(new MyTreeUI());
		catalogTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) catalogTree
						.getLastSelectedPathComponent(); // 返回最后选中的结点
				if (node instanceof AricleNode) {
					diaryPanel.selectDiary(((AricleNode)node).getFile());
					diaryPanel.setCurrTree(catalogTree);
					diaryPanel.setCurrNode(node);
				}
				if (node instanceof CategoryNode) {
					 
				}
			}
		});
		catalogTree.addMouseListener(new MouseAdapter() {
			 
			@Override
			public void mouseExited(MouseEvent e) {
				TreeNodeRenderer.mouseRow=-1;
				 catalogTree.repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					final TreePath path = catalogTree.getPathForLocation(e.getX(), e.getY());
					if (null != path) {
						// path中的node节点（path不为空，这里基本不会空）
						final Object object = path.getLastPathComponent();
						if (object instanceof CategoryNode) {
							JPopupMenu pm = new JPopupMenu();
							pm.setBackground(Color.WHITE);
							pm.setBorder(LIGHT_GRAY_BORDER);
							pm.setBorderPainted(false);
							JMenuItem mit3 = new JMenuItem("删除分组");
//							mit0.setOpaque(false);
							mit3.setFont(BASIC_FONT);
							 
							JMenuItem mit1 = new JMenuItem("更换名称");
//							mit1.setOpaque(false);
							mit1.setFont(BASIC_FONT);
							JMenuItem mit2 = new JMenuItem("添加文章");
							JMenuItem mit0 = new JMenuItem("打开目录");
//							mit2.setOpaque(false);
							mit2.setFont(BASIC_FONT);
							mit0.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									File f = ((CategoryNode)(object)).getFile();
									try {
										Clip.openFile(f.getAbsolutePath());
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							});
							// 删除分组
							mit3.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									((CategoryNode)(object)).removeFromParent();
									FileUtils.deleteDir(((CategoryNode)(object)).getFile());
									catalogTree.setUI(new MyTreeUI());
									catalogTree.revalidate();
								}
							});
							// 更换名称
							mit1.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									 File dirFile = ((CategoryNode)(object)).getFile();
									 new InputMessageDialog(diaryPanel.getContext().getFrame(), 
												"目录更名", dirFile.getName(), new Callback(){
													public void callback(String string) {
														String path = dirFile.getParentFile().getAbsolutePath()+File.separator+string;
														dirFile.renameTo(new File(path));
													}
										});
									 catalogTree.setUI(new MyTreeUI());
									catalogTree.revalidate();
								}
							});
							mit2.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									CategoryNode  node =(CategoryNode)object;
									File f = diaryPanel.createAricle(node.getFile());
									Aritcle aritcle = new Aritcle();
									aritcle.sort =node.getFile().listFiles().length;
									AricleNode newChild = new AricleNode(diaryPanel,f,aritcle);
									
									((CategoryNode)(object)).insert(newChild, 0);
									catalogTree.expandPath(path);
									catalogTree.setUI(new MyTreeUI());
									catalogTree.revalidate();
								}
							});
							pm.add(mit0);
							pm.add(mit1);
							pm.add(mit2);
							pm.add(mit3);
						
							pm.show(catalogTree, e.getX(), e.getY());
						}
						if(object instanceof AricleNode){
							JPopupMenu pm = new JPopupMenu();
//							pm.setBackground(Color.WHITE);
							pm.setBorder(LIGHT_GRAY_BORDER);
							pm.setBorderPainted(false);
							JMenuItem mit0 = new JMenuItem("更换名称");
							JMenuItem mit1= new JMenuItem("打开文件");
							JMenuItem mit2 = new JMenuItem("删除文章");
//							mit0.setOpaque(false);
							mit0.setFont(BASIC_FONT);
//							mit0.setFont(BASIC_FONT);
							mit2.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									((AricleNode)(object)).removeFromParent();
									diaryPanel.delAricle(((AricleNode)(object)).getFile());
									catalogTree.setUI(new MyTreeUI());
								}
							});
							mit1.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									File f = ((AricleNode)(object)).getFile();
									try {
										Clip.openFile(f.getAbsolutePath());
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							});
							mit0.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									 final File aritcleFile = ((AricleNode)(object)).getFile();
									new InputMessageDialog(diaryPanel.getContext().getFrame(), 
											"文章更名", aritcleFile.getName(), new Callback(){
												public void callback(String string) {
													String path = aritcleFile.getParentFile().getAbsolutePath()+File.separator+string+DiaryPanel.diarySuffix;
													aritcleFile.renameTo(new File(path));
												}
									});
									catalogTree.setUI(new MyTreeUI());
								}
							});
							pm.add(mit0);
							pm.add(mit1);
							pm.add(mit2);
							pm.show(catalogTree, e.getX(), e.getY());
						}
					}
				}
			}
		});
		catalogTree.addMouseMotionListener(new MouseMotionAdapter() {
			
            @Override
            public void mouseMoved(MouseEvent arg0) {
                int x = (int) arg0.getPoint().getX();
                int y = (int) arg0.getPoint().getY();
//                TreePath path = catalogTree.getPathForLocation(x, y);
                catalogTree.getComponentAt(x, y).repaint();
                TreeNodeRenderer.mouseRow = catalogTree.getRowForLocation(x, y);
                catalogTree.repaint();
            }
        });
		catalogTree.setTransferHandler(new MyJTreeTransferHandler());  
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setOpaque(false);
		jScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		jScrollPane.setViewportView(catalogTree);
		jScrollPane.getViewport().setOpaque(false);
//		jScrollPane.getViewport().setBackground( UI.backColor);
//		jScrollPane.setBackground(UI.backColor);
		jScrollPane.getVerticalScrollBar().setBackground(UI.backColor);
//		jScrollPane.getVerticalScrollBar().setVisible(false);
		jScrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		// 屏蔽横向滚动条
		jScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(jScrollPane, BorderLayout.CENTER);
	}
	// 微软雅黑
	public static Font BASIC_FONT = new Font("微软雅黑", Font.PLAIN, 12);
	public static Font BASIC_FONT2 = new Font("微软雅黑", Font.TYPE1_FONT, 12);
	// 楷体
	public static Font DIALOG_FONT = new Font("楷体", Font.PLAIN, 16);
	
	public static Border GRAY_BORDER = BorderFactory.createLineBorder(Color.GRAY);
	public static Border ORANGE_BORDER = BorderFactory.createLineBorder(Color.ORANGE);
	public static Border LIGHT_GRAY_BORDER = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

	public void addNode(File file) {
		CategoryNode node = new CategoryNode(diaryPanel, file);
		System.out.println("-:"+root.getChildCount());
		root.insert(node, root.getChildCount());
		if(root.getChildCount()==1)
		{	
			model = new DefaultTreeModel(root);
			catalogTree.setModel(model);
		}
//		catalogTree.putClientProperty("JTree.lineStyle", "None");
		catalogTree.setUI(new MyTreeUI());
	}

}
