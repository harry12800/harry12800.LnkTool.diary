package cn.harry12800.lnk.diary;

import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.harry12800.lnk.core.util.ImageUtils;
import cn.harry12800.j2se.style.UI;
import cn.harry12800.lnk.diary.CatalogItemPanel.Builder;

public class CategoryNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Icon icon;
	public JLabel picture;
	public JLabel nickName;
	public CatalogItemPanel panelItem = null;
	private File file;

	public CategoryNode(DiaryPanel diaryPanel, File file) {
		this.file = file;
		icon = ImageUtils.getIcon("arrow_left.png");
		Builder createBgColorBuilder = CatalogItemPanel.createBuilder(this,
				UI.backColor);
		createBgColorBuilder.image = ImageUtils.getByName("arrow_left.png");
		panelItem = new CatalogItemPanel(file.getName(), 200, 30, createBgColorBuilder);
		panelItem.setBounds(0, 0, 200, 30);
	}
	public Component getView() {
		return panelItem;
	}

	/**
	 * 获取file
	 *	@return the file
	 */
	public File getFile() {
		return file;
	}
	/**
	 * 设置file
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		panelItem.picture.setIcon(icon);
		this.icon = icon;
	}

	@Override
	public String toString() {
		return file.getName();
	}
	public Component setSelect(boolean selected, boolean mouseEnter) {
		panelItem.hover = mouseEnter;
		return panelItem;
	}
	public Component getView(boolean mouseEnter) {
		panelItem.hover = mouseEnter;
		return panelItem;
	}
}
