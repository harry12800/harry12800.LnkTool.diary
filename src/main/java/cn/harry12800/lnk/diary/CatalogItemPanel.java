package cn.harry12800.lnk.diary;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cn.harry12800.j2se.component.ClickAction;
import cn.harry12800.j2se.style.UI;
import cn.harry12800.j2se.utils.Clip;

public class CatalogItemPanel extends JPanel {
	boolean hover = false;
	boolean isSelect = false;
	private int w;
	private int h;
	private String name;
	public Builder builder;
	private ClickAction clickAction;
	private HoverListener hoverAction;
	public JLabel picture = new JLabel();
	public JLabel text = new JLabel();
	static class Builder {
		public boolean hasborder = true;
		public boolean handCursor = true;
		public boolean hasTip;
		public boolean hasCheck = false;
		public boolean checked = false;
		public Image image= null;
		public Color bgcolor;
		public int borderRadius= 0;
		public CategoryNode categoryNode;
	}
 
	/**
	 * 
	 * @param name
	 * @param w
	 * @param h
	 * @param hasBorder
	 * @param handCursor
	 * @param hasTip
	 * @param hasCheck
	 * @param checked
	 */
	public CatalogItemPanel(String name, int w, int h, final Builder builder) {
		this.w = w;
		this.h = h;
		this.name = name;
		this.builder = builder;
		setPreferredSize(new Dimension(w, h));
		setSize(w, h);
		setLayout(null);
		setBackground(UI.backColor);
		Icon icon = builder.categoryNode.getIcon();
		add(picture);
		add(text);
		text.setText(name);
		text.setBounds(27, 0, w, h);
		text.setOpaque(false);
		picture.setIcon(icon);
		picture.setBounds(6, 8, 20, 16);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==10){
					if(clickAction!=null){
						isSelect = true;
						repaint();
						clickAction.leftClick(null);
					}
				}
			}
		});
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				hover = false;
				repaint();
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				hover = true;
				repaint();				
			}
		});
		setMinimumSize(new Dimension(w, h) );
		setMaximumSize( new Dimension(w, h) );
		setPreferredSize(new Dimension(w, h));
		setFont(UI.微软雅黑Font);
		Color color = new Color(231, 224, 224);
		setForeground(color);
		if (builder.handCursor)
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				Color color = new Color(255, 255, 255);
				setForeground(color);
				hover = true;
				repaint();
				if(hoverAction!=null)
					hoverAction.hover(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				Color color = new Color(186, 186, 186);
				setForeground(color);
				hover = false;
				repaint();
				if(hoverAction!=null)
					hoverAction.out();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
				}
				super.mouseReleased(e);
			}
		});
	}

	public interface ActionListener {
		void changed(boolean checked);
	}

	public void addMouseListener(ClickAction a) {
		this.clickAction = a;
		super.addMouseListener(clickAction);
	}

	public interface ChangeListener {
		void changed(boolean checked);
	}
	public interface HoverListener {
		void hover( MouseEvent e);
		void out();
	}
	public void addHoverListener(HoverListener a) {
		this.hoverAction = a;
	}

	public CatalogItemPanel(String name, int w, int h) {
		this(name, w, h, createBuilder());
	}

	private static Builder createBuilder() {
		return new Builder();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		if (builder.bgcolor != null) {
			g2d.setColor(builder.bgcolor);
			g2d.fillRoundRect(0, 0, w , h, builder.borderRadius*2, builder.borderRadius*2);
		}
		if (isSelect) {
			g2d.setColor(UI.foreColor);
			g2d.fillRoundRect(0, 0, w-1 , h-1, 0,0);
		}else{
			if (hover) {
				g2d.setColor(UI.scrollColor);
				g2d.fillRoundRect(0, 0, w-1 , h-1, 0,0);
			}
		}
//		GradientPaint p1 = new GradientPaint(0, 1, new Color(255, 255, 255, 255), 0, h ,
//				new Color(255, 255, 255, 255));
//		g2d.setPaint(p1);
//		Stroke stroke = g2d.getStroke();
//		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND)); // 设置新的画刷
//		if (hover) {
//			g2d.setColor(UI.hoverForeColor);
//		}else{
//			g2d.setColor(UI.fontColor);
//		}
//		g2d.setFont(UI.微软雅黑Font);
//		g2d.drawString(name, 15, h / 2 + 5);
//		g2d.setStroke(stroke);
	}
	
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	public static Builder createBgColorBuilder(Color color,File file) {
		Builder createBuilder = createBuilder();
		createBuilder.bgcolor = color;
		ImageIcon bigIcon = Clip.getBigIcon(file);
		if(bigIcon!=null){
			createBuilder.image = bigIcon.getImage();
		}
		return createBuilder;
	}
	public static Builder createBgColorBuilder(Color color, BufferedImage byName) {
		Builder createBuilder = createBuilder();
		createBuilder.bgcolor = color;
		createBuilder.image =byName;
		return createBuilder;
	}
	public static Builder createBgColorBuilder(Color color) {
		Builder createBuilder = createBuilder();
		createBuilder.bgcolor = color;
		return createBuilder;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
		repaint();
	}

	/**
	 * 获取name
	 *	@return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置name
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public static Builder createBuilder(CategoryNode categoryNode,
			Color backColor) {
		Builder builder = new Builder();
		builder.categoryNode = categoryNode;
		return builder;
	}
	
}
