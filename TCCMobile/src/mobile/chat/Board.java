package mobile.chat;

import javax.microedition.lcdui.*;


public class Board extends CustomItem implements ItemCommandListener
{
	private final static int UPPER = 0;
    private final static int IN = 1;
    private final static int LOWER = 2;
    // status tells us where the cursor is on the form, UPPER, IN or LOWER
    private int status = UPPER;
    // location tells us where the cursor is inside the item
    private int location = 0;
    
    private int position = 0;
    
    
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

	protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) 
    {
		switch (dir) 
		{
			case Canvas.DOWN:
				if (status == UPPER) 
				{
					status = IN;
//					return false;
				} 
				else if(status == IN) 
				{
					if(location >= position)
					{
						status = LOWER;
						return false;
					}
					else location += font.getHeight();
				}
				
				
				repaint();
				break;
			
			case Canvas.UP:
				if (status == LOWER) {
					status = IN;
//					return false;
				} 
				else if(status == IN )
				{
					if(location <= 0)
					{
						status = UPPER;
						return false;
					}
					else location -= font.getHeight();
				}
				
				
				repaint();
				break;
			
		}
		return true;
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

	protected void paint(Graphics g, int w, int h) 
	{
		g.setColor(backgroundColor);
		g.fillRect(0, 0, w-1, h-1);
		
		g.setColor(borderColor);
		g.drawRect(0, 0, w-1, h-1);
		
		g.setFont(font);		
			
		int textHeight = getWrapHeight(g, textBoard, 5 , -(location), w-5, Graphics.LEFT | Graphics.TOP);
		position = textHeight - h;
		
			
		g.setColor(borderColor);
		g.fillRect(w-10, 0 , w-1, h-1);
		if(position > 0)
		{
			g.setColor(0xffffff);
			g.fillRect(w-11, (h/position)*(location-10) , w-3, 10);
		}
		
		g.setColor(textColor);
		wrap(g, textBoard, 5 , -(location), w-16, Graphics.LEFT | Graphics.TOP);
			
	}

	public void AppendText(String text)
	{
		if(text.equals("#CLEAR#")) textBoard ="";
		else textBoard += " "+text;
		
		if (position > 0)
			location = position + font.getHeight();
		
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


	public void commandAction(Command arg0, Item arg1) {
		// TODO Auto-generated method stub
		
	}

}
