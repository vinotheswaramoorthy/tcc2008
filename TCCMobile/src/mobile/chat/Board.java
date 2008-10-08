package mobile.chat;

import javax.microedition.lcdui.*;
public class Board extends CustomItem
{
	private String textBoard = " ";
	private Font font;
	private int borderColor = 0x9999ff;
	private int backgroundColor = 0xffffee;
	private int textColor = 0x0000ff;

	protected Board(String label, int width, int height) 
	{
		super(label);
		if(width > 0 && height > 0)
			this.setPreferredSize(width, height);
	}       


	public int getWrapHeight(Graphics g, String s, int x, int y, int w, int anchor)
	{
		return wrapImplementation(g, s, x, y, w, anchor, false);
	}

	private int wrap(Graphics g, String s, int x, int y, int w, int anchor)
	{
		return wrapImplementation(g, s, x, y, w, anchor, true);
	}

	private int wrapImplementation(Graphics g, String s, int x, int y, int w, int anchor, boolean draw)
	{
		Font f = g.getFont();
		int oldy = y;
		boolean trucking = true;
		int i = 0;
		int length = 0;
		int space = -1;
		while (trucking)
		{
			boolean write = false;
			if (i >= s.length()) trucking = false;
			else if (s.charAt(i) == ' ') i++;
			else
			{
				int pw = f.substringWidth(s, i, length);
				if (pw > w)
				{
					if (space > 0) length = space;
					else length--;
					write = true;
				}
				else if (i + length >= s.length())
				{
					write = true;
					trucking = false;
				}
				else
				{
					if (s.charAt(i + length) == ' ') space = length;
					length++;
				}
			}
			if (write)
			{
				if (draw) g.drawSubstring(s, i, length, x, y, anchor);
				i += length;
				y += f.getHeight();
				length = 0;
				space = -1;
			}
		}
		return y - oldy;
	}

	protected int getMinContentHeight() {
		return 30;
	}

	protected int getMinContentWidth() {
		return 70;
	}

	protected int getPrefContentHeight(int arg0) {
		return 60;
	}

	protected int getPrefContentWidth(int arg0) {
		return 100;
	}

	protected void paint(Graphics g, int w, int h) {

		g.setColor(backgroundColor);
		g.fillRect(0, 0, w, h);
		
		int anchor = Graphics.TOP | Graphics.LEFT;
		if (font == null)
		{
			int[] size = { Font.SIZE_LARGE, Font.SIZE_MEDIUM, Font.SIZE_SMALL };
			for (int i = 0; i < size.length; i++)
			{
				setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, size[i]));
				g.setFont(font);
				getWrapHeight(g, textBoard, 0, 0, w, anchor);
				//if (wh < h - y) break;
			}
		}
		g.setFont(font);
		g.setColor(textColor);
		wrap(g, textBoard, 5, 5, w-10, anchor);
		g.setColor(borderColor);
		g.drawRect(0, 0, w-1, h-1);
	}

	public void AppendText(String text)
	{
		textBoard += text;
		this.repaint();
	}


	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}


	public int getBorderColor() {
		return borderColor;
	}


	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}


	public int getTextColor() {
		return textColor;
	}


	public void setFont(Font font) {
		this.font = font;
	}

	
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}


	public int getBackgroundColor() {
		return backgroundColor;
	}

}
