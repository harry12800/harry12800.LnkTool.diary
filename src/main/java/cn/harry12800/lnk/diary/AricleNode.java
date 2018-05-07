package cn.harry12800.lnk.diary;

import java.awt.Component;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.harry12800.Lnk.core.util.ImageUtils;
import cn.harry12800.Lnk.core.util.JsonUtil;
import cn.harry12800.j2se.style.UI;
import cn.harry12800.lnk.diary.AricleItemPanel.Builder;
import cn.harry12800.lnk.diary.entity.Aritcle;
import cn.harry12800.tools.StringUtils;

public class AricleNode extends DefaultMutableTreeNode {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Icon icon;
	public AricleItemPanel plainButton = null;
	private File file;
	public Builder builder;
	public Date date;
	public Aritcle aritcle;
	public DiaryPanel diaryPanel;
	public AricleNode(DiaryPanel diaryPanel, File file,Aritcle aritcle) {
		this.diaryPanel = diaryPanel;
		setFile(file,aritcle);
		builder = AricleItemPanel.createBuilder(this,UI.backColor);
		builder.image = ImageUtils.getByName("arrow_left.png");
		plainButton = new AricleItemPanel(this.aritcle.title, 200, 30,builder);
		SimpleDateFormat format = new SimpleDateFormat(StringUtils.yyyy_MM_dd_HH24_mm_ss);
	    date = new Date();
		try {
			this.date = format.parse(this.aritcle.updateTime);
		} catch (ParseException e) {
//			e.printStackTrace();
		}
		plainButton.updatedateL.setText(RelativeDateFormat.format(date));
		plainButton.setBounds(0	, 0, 200, 30);
	}

	public Component getView() {
		plainButton.updatedateL.setText(RelativeDateFormat.format(date));
		return plainButton;
	}

	public Icon getIcon() {
		return icon;
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
	 * @param aritcle2 
	 */
	public void setFile(File file, Aritcle aritcle2) {
		this.file = file;
//		System.out.println(file);
		try {
			this.aritcle  = JsonUtil.string2Json(file, Aritcle.class);
			if(this.aritcle == null) this.aritcle = new Aritcle();
			aritcle.sort = aritcle2.sort;
//			System.out.println(aritcle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	@Override
	public String toString() {
		return file.toString();
	}

	public Component setSelect(boolean selected, boolean mouseEnter) {
		plainButton.updatedateL.setText(RelativeDateFormat.format(date));
		plainButton.isSelect = selected;
		plainButton.hover = mouseEnter;
		return plainButton;
	}

}
