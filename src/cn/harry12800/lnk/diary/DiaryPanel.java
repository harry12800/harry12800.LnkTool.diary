package cn.harry12800.lnk.diary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.harry12800.Lnk.core.Context;
import cn.harry12800.Lnk.core.CorePanel;
import cn.harry12800.Lnk.core.FunctionPanelConfig;
import cn.harry12800.Lnk.core.FunctionPanelModel;
import cn.harry12800.Lnk.core.util.JsonUtil;
import cn.harry12800.j2se.action.CtrlSAction;
import cn.harry12800.j2se.action.EnterAction;
import cn.harry12800.j2se.component.ClickAction;
import cn.harry12800.j2se.component.InputText;
import cn.harry12800.j2se.component.MButton;
import cn.harry12800.j2se.component.PlainButton;
import cn.harry12800.j2se.dialog.InputMessageDialog;
import cn.harry12800.j2se.dialog.InputMessageDialog.Callback;
import cn.harry12800.j2se.style.UI;
import cn.harry12800.lnk.diary.entity.Aritcle;
import cn.harry12800.tools.DateUtils;
import cn.harry12800.tools.FileUtils;
import cn.harry12800.tools.Lists;
import cn.harry12800.tools.MachineUtils;
import cn.harry12800.tools.StringUtils;

@FunctionPanelModel(configPath = "diary", height = 600, width = 700, backgroundImage = "diary_back.jpg", desc = "日记工具\r\n请使用markdown语法编写", headerImage = "diary1.png")
@FunctionPanelConfig(filename = "tmp.json")
public class DiaryPanel extends CorePanel<Object> implements DropTargetListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String diarySuffix = ".properties";
	MButton save = new MButton("保存", 80, 25);
	MButton see = new MButton("预览", 80, 25);
	MButton reader = new MButton("朗读", 80, 25);
	MButton stopReader = new MButton("停止朗读", 80, 25);
	MButton newA = new MButton("新建", 80, 25);
	MButton cmd = new MButton("cmd执行", 80, 25);
	AreaTextPanel areaTextPanel = new AreaTextPanel();
	InputText searchInputText = new SearchInputText(30);
	SearchResultPanel searchResultPanel = new SearchResultPanel();

	CatalogPanel catalogPanel;
	String currPath;
	private DefaultMutableTreeNode currNode;
	private JTree catalogTree;

	public static void main(String[] args) throws Exception {
		// Main.main(args);
	}

	@SuppressWarnings("deprecation")
	public DiaryPanel(Context context) {
		super(context);
		setSize(700, 600);
		searchInputText.setNextFocusableComponent(searchResultPanel);
		setPreferredSize(new Dimension(700, 600));
		setOpaque(false);
		setLayout(null);
		String currTime = DateUtils.getCurrTime();
		catalogPanel = new CatalogPanel(this);
		currPath = dirPath + File.separator + currTime + ".txt";
		System.err.println(currPath);

		if (new File(currPath).exists()) {
			try {
				areaTextPanel.setText(FileUtils.getSrcByFilePath(currPath, "utf-8"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		save.setBounds(201, this.getHeight() - 30, 80, 25);
		see.setBounds(291, this.getHeight() - 30, 80, 25);
		newA.setBounds(380, this.getHeight() - 30, 80, 25);
		cmd.setBounds(470, this.getHeight() - 30, 80, 25);
		reader.setBounds(540, this.getHeight() - 30, 80, 25);
		stopReader.setBounds(610, this.getHeight() - 30, 80, 25);

		catalogPanel.setBounds(1, 22, 200, getHeight() - 20);
		searchInputText.setBounds(201, 20, 495, 25);
		areaTextPanel.setBounds(201, 47, 495, 510);
		searchResultPanel.setBounds(201, 47, 495, 510);

		add(searchInputText);
		add(areaTextPanel);
		initBtnListener();
		add(save);
		add(see);
		add(newA);
		add(cmd);
		add(reader);
		add(stopReader);

		add(searchResultPanel);
		add(catalogPanel);
		searchInputText.setCtrlSAction(new CtrlSAction() {
			public void ctrlS() {
				saveAricle();
			}
		});
		searchInputText.setEnterAction(new EnterAction() {
			public void enter() {
				// areaTextPanel.setVisible(false);
				List<File> searchResult = getSearchResult();
				searchResultPanel.removeAll();
				int x = 0;
				for (final File file : searchResult) {
					Aritcle a = null;
					try {
						a = JsonUtil.string2Json(file, Aritcle.class);
					} catch (Exception e2) {
						continue;
					}
					PlainButton plainButton = new PlainButton(a.title, 490, 25,
							PlainButton.createBgColorBuilder(UI.foreColor));
					plainButton.addMouseListener(new ClickAction(plainButton) {
						public void leftClick(MouseEvent e) {
							searchResultPanel.setVisible(false);
							currPath = file.getAbsolutePath();
							Aritcle a;
							try {
								a = JsonUtil.string2Json(new File(currPath), Aritcle.class);
								areaTextPanel.setText(a.content);
								searchInputText.setText(a.title);
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							areaTextPanel.setVisible(true);
							areaTextPanel.requestFocus();
						}
					});
					plainButton.setBounds(1, x * 26 + 1, 490, 25);
					x++;
					searchResultPanel.add(plainButton);
					;
				}
				searchResultPanel.repaint();
				searchResultPanel.setVisible(true);
				moveToFront(searchResultPanel);
				searchResultPanel.requestFocus();
				searchResultPanel.addFocusListener(new FocusListener() {
					public void focusLost(FocusEvent e) {
						// searchResultPanel.setVisible(false);
					}

					public void focusGained(FocusEvent e) {

					}
				});
				searchResultPanel.addKeyListener(new KeyListener() {

					@Override
					public void keyTyped(KeyEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void keyReleased(KeyEvent e) {

						if (e.getKeyCode() == KeyEvent.VK_UP) {
						}
					}

					@Override
					public void keyPressed(KeyEvent e) {
						// TODO Auto-generated method stub

					}
				});
			}
		});

		areaTextPanel.setCtrlSAction(new CtrlSAction() {
			public void ctrlS() {
				saveAricle();
			}
		});
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	/**
	 * 保存文章。。 第一：保存本文章、
	 */
	protected void saveAricle() {
		String text = areaTextPanel.area.getText().trim();
		String title = searchInputText.getText().trim();
		if ("null".equals(title) || "".equals(title)) {
			title = text.split("\\n")[0];
		}
		searchInputText.setText(title);
		Aritcle a = new Aritcle();
		if (catalogTree != null) {
			a = ((AricleNode) currNode).aritcle;
		}
		a.title = title;
		a.content = text;
		a.updateTime = DateUtils.getCurrTimeByFormat(StringUtils.yyyy_MM_dd_HH24_mm_ss);
		JsonUtil.saveObj(a, currPath);
		if (catalogTree != null) {
			((AricleNode) currNode).builder.name = title;
			((AricleNode) currNode).date = new Date();
			((AricleNode) currNode).plainButton.text.setText(title);
			((AricleNode) currNode).plainButton.updatedateL.setText(RelativeDateFormat.format(new Date()));
			catalogTree.setUI(new MyTreeUI());
			catalogTree.revalidate();
		}
		alert("保存成功！");
	}

	protected List<File> getSearchResult() {
		String searchKey = searchInputText.getText().trim();
		List<File> list = FileUtils.traverseDir(dirPath, new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.isFile();
			}
		});

		List<File> files = Lists.newArrayList();
		for (File file : list) {
			String code = FileUtils.getSrcByFilePath(file, "UTF-8");
			if (code.contains(searchKey)) {
				files.add(file);
			}
		}
		return files;
	}

	private void initBtnListener() {
		save.addMouseListener(new ClickAction(save) {
			public void leftClick(MouseEvent e) {
				saveAricle();
			}
		});

		see.addMouseListener(new ClickAction(save) {
			public void leftClick(MouseEvent e) {
				// new DiaryScanDialog();
				if (dialog != null) {
					dialog.dispose();
					dialog = null;
				}
				File selectFile = new File(currPath);
				if (selectFile.exists()) {
					File parentFile = selectFile.getParentFile();
					File[] listFiles = parentFile.listFiles(new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							System.out.println(pathname.getName());
							return pathname.getName().endsWith("properties");
						}
					});
					System.out.println(listFiles.length);
					Aritcle[] aritcles = new Aritcle[listFiles.length];
					int currIndex = 0;
					for (int i=0;i<listFiles.length;i++) {
						try {
							if(selectFile.getName().equals(listFiles[i].getName()))currIndex = i;
							aritcles[i] = JsonUtil.string2Json(listFiles[i], Aritcle.class);
						} catch (Exception e1) {
							alert(e1.getMessage());
							return ;
						}
					}
					
//					dialog = new NativeDiaryScanDialog(DiaryPanel.this, aritcles);
					dialog = new  DiaryScanDialog(DiaryPanel.this,aritcles,currIndex);
					
					showDialog();
				} else {
					alert("没有可预览的文章！");
				}
			}
		});

		/**
		 * 新建
		 */
		newA.addMouseListener(new ClickAction(newA) {
			public void leftClick(MouseEvent e) {
				List<String> printName = Lists.newArrayList();
				printName.add("新建文件夹");
				printName.add("新建文章");
				new SelectButtonDialog(context.getFrame(), printName) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void exe(String name) {
						if ("新建文件夹".equals(name)) {
							new InputMessageDialog(getContext().getFrame(), name, name, new Callback() {
								public void callback(String string) {
									if (!new File(dirPath + File.separator + string).exists()) {
										FileUtils.createDirectory(dirPath + File.separator + string);
										catalogPanel.addNode(new File(dirPath + File.separator + string));
									} else {
										alert("已存在该目录！");
									}
								}
							});
						}
					}
				};
			}
		});

		cmd.addMouseListener(new ClickAction(cmd) {
			public void leftClick(MouseEvent e) {
				if (catalogTree != null) {
					File file = ((AricleNode) currNode).getFile();
					final String srcByFilePath = FileUtils.getSrcByFilePath(file, "utf-8");
					try {
						String[] runtimeOutErr = MachineUtils.runtimeOutErr(srcByFilePath);
						areaTextPanel.setText(srcByFilePath + "\r\n" + runtimeOutErr[0] + "\r\n" + runtimeOutErr[1]);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			}
		});
		reader.addMouseListener(new ClickAction(reader) {
			public void leftClick(MouseEvent e) {
				if (catalogTree != null) {
					stopReader();
					File file = ((AricleNode) currNode).getFile();
					final String srcByFilePath = FileUtils.getSrcByFilePath(file, "utf-8");
					thread = new BaiduReader(srcByFilePath);
					thread.start();
				}
			}
		});
		stopReader.addMouseListener(new ClickAction(stopReader) {
			public void leftClick(MouseEvent e) {
				stopReader();
			}
		});
	}

	protected void stopReader() {
		if (thread != null)
			thread.setReadStatus(false);
	}

	private BaiduReader thread;

	// coursor = 
	// Toolkit.getDefaultToolkit().createCustomCursor(new 
	// ImageIcon("image/pencil.gif").getImage(),new Point(10,20), "stick");
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		GradientPaint p2 = new GradientPaint(0, 1, new Color(186, 131, 164, 200), 0, 20, new Color(255, 255, 255, 255));
		g2d.setPaint(p2);
		// g2d.drawRoundRect(1, 20, width, size * 25 + 1, 5, 5);
		// g2d.fillRoundRect(1, 20, width, size * 25 + 1, 5, 5);
		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND)); // 设置新的画刷
		g2d.setFont(new Font("宋体", Font.PLAIN, 12));
		g2d.drawString("日记", 5, 15);
		g2d.dispose();
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) // 如果拖入的文件格式受支持
			{
				dtde.acceptDrop(DnDConstants.ACTION_MOVE);
				// 接收拖拽来的数据
				// @SuppressWarnings("unchecked")
				// List<File> list = (List<File>) (dtde.getTransferable()
				// .getTransferData(DataFlavor.javaFileListFlavor));
				// MainFrame.save(this, list);
			} else {
				dtde.rejectDrop();// 否则拒绝拖拽来的数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void selectDiary(File f) {
		if (f.isFile()) {
			Aritcle a;
			try {
				a = JsonUtil.string2Json(f, Aritcle.class);
				areaTextPanel.setText(a.content);
				searchInputText.setText(a.title);
			} catch (Exception e) {
				// e.printStackTrace();
			}

			this.currPath = f.getAbsolutePath();
		}
	}

	public void setCurrNode(DefaultMutableTreeNode node) {
		this.currNode = node;
	}

	public void setCurrTree(JTree catalogTree) {
		this.catalogTree = catalogTree;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public File createAricle(File file) {
		String pathString = file.getAbsolutePath() + File.separator + StringUtils.getUUID() + ".properties";
		FileUtils.createFile(pathString);
		return new File(pathString);
	}

	public void delAricle(File file) {
		if (catalogTree != null) {
			file.delete();
		}
	}
}
