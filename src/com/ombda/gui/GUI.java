package com.ombda.gui;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.ombda.Files;
import com.ombda.Images;
import com.ombda.Tile;

public abstract class GUI implements MouseListener, MouseMotionListener, KeyListener{
	protected static final BufferedImage[] letters;
	static{
		BufferedImage image = Images.load(new File(Files.localize("images\\gui\\font\\normal.png")),true);
		byte[] font_sizes = null;
		try{
			font_sizes = java.nio.file.Files.readAllBytes(Paths.get(com.ombda.Files.localize("images\\gui\\font\\normal.png.sizes")));
		}catch(IOException e){
			if(debug){
				debug(e.getMessage());
				if(printStackTrace)
					e.printStackTrace();
				System.exit(0);
			}
		}
		letters = new BufferedImage[font_sizes.length];
		int x = 2, y = 2, col = 0;
		for(char c = '\u0000'; c < letters.length; c++){
			char width = (char)font_sizes[c];
			//debug("width for char '"+c+"' is "+(int)width+" (x="+x+",y="+y+")");
			letters[c] = Images.crop(image, x, y, width*2, 20);
			col++;
			if(col > 0x0F){
				col = 0;
				y+=22;
				x=2;
			}else{
				x+=2*(width+1);
			}
		}
	}
	
	public abstract boolean drawMap();
	public abstract boolean pauseGame();
	public abstract boolean blockInput();
	public void update(){}
	public void draw(Graphics2D g2d){
		for(Button b : buttons)
			b.draw(g2d);
	}
	
	protected static final BufferedImage msgbox_tl, msgbox_t, msgbox_tr, msgbox_l, msgbox_m, msgbox_r, msgbox_bl, msgbox_b, msgbox_br;
	static{
		msgbox_tl = Images.load(new File(Files.localize("images\\gui\\msgbox_tl.png")),true);
		msgbox_t = Images.load(new File(Files.localize("images\\gui\\msgbox_t.png")),true);
		msgbox_tr = Images.load(new File(Files.localize("images\\gui\\msgbox_tr.png")),true);
		msgbox_l = Images.load(new File(Files.localize("images\\gui\\msgbox_l.png")),true);
		msgbox_m = Images.load(new File(Files.localize("images\\gui\\msgbox_m.png")),true);
		msgbox_r = Images.load(new File(Files.localize("images\\gui\\msgbox_r.png")),true);
		msgbox_bl = Images.load(new File(Files.localize("images\\gui\\msgbox_bl.png")),true);
		msgbox_b = Images.load(new File(Files.localize("images\\gui\\msgbox_b.png")),true);
		msgbox_br = Images.load(new File(Files.localize("images\\gui\\msgbox_br.png")),true);
	}
	
	protected static void drawBox(Graphics2D g2d, int topx, int topy, int width, int height){
		/*BufferedImage msgbox_t = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = msgbox_t.createGraphics();
		g.drawImage(GUI.msgbox_t,0,0,null);
		g.dispose();
		BufferedImage msgbox_l = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		g = msgbox_l.createGraphics();
		g.drawImage(GUI.msgbox_l,0,0,null);
		g.dispose();
		BufferedImage msgbox_r = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		g = msgbox_r.createGraphics();
		g.drawImage(GUI.msgbox_r,0,0,null);
		g.dispose();
		BufferedImage msgbox_b = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		g = msgbox_b.createGraphics();
		g.drawImage(GUI.msgbox_b,0,0,null);
		g.dispose();
		BufferedImage msgbox_m = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		g = msgbox_m.createGraphics();
		g.drawImage(GUI.msgbox_m,0,0,null);
		g.dispose();*/
		g2d.drawImage(msgbox_tl,topx,topy,null);
		g2d.drawImage(msgbox_tr, topx+width*Tile.SIZE/2-Tile.SIZE/2, topy, null);
		g2d.drawImage(msgbox_bl, topx, topy+height*Tile.SIZE/2-Tile.SIZE/2, null);
		g2d.drawImage(msgbox_br, topx+width*Tile.SIZE/2-Tile.SIZE/2, topy+height*Tile.SIZE/2-Tile.SIZE/2, null);
		for(int x = Tile.SIZE/2; x < width*Tile.SIZE/2-Tile.SIZE/2; x+=Tile.SIZE/2){
			g2d.drawImage(msgbox_t, topx+x, topy, null);
			g2d.drawImage(msgbox_b, topx+x, topy+height*Tile.SIZE/2-Tile.SIZE/2, null);
		}
		for(int y = Tile.SIZE/2; y < height*Tile.SIZE/2-Tile.SIZE/2; y+=Tile.SIZE/2){
			g2d.drawImage(msgbox_l,topx, topy+y, null);
			g2d.drawImage(msgbox_r, topx+width*Tile.SIZE/2-Tile.SIZE/2, topy+y, null);
		}
		for(int x = Tile.SIZE/2; x < width*Tile.SIZE/2-Tile.SIZE/2; x+=Tile.SIZE/2){
			for(int y = Tile.SIZE/2; y < height*Tile.SIZE/2-Tile.SIZE/2; y+=Tile.SIZE/2){
				g2d.drawImage(msgbox_m,topx+x,topy+y,null);
			}
		}
		
	}
	public static Image tint(Image loadImg, Color color) {
		if(color == null) return loadImg;
	    BufferedImage img = new BufferedImage(loadImg.getWidth(null), loadImg.getHeight(null),
	            BufferedImage.TRANSLUCENT);
	    final float tintOpacity = 1f;
	    Graphics2D g2d = img.createGraphics(); 

	    //Draw the base image
	    g2d.drawImage(loadImg, 0, 0,null);
	    //Set the color to a transparent version of the input color
	    g2d.setColor(new Color(color.getRed() / 255f, color.getGreen() / 255f, 
	        color.getBlue() / 255f, tintOpacity));

	    //Iterate over every pixel, if it isn't transparent paint over it
	    Raster data = ((BufferedImage)loadImg).getData();
	    for(int x = data.getMinX(); x < data.getWidth(); x++){
	        for(int y = data.getMinY(); y < data.getHeight(); y++){
	            int[] pixel = data.getPixel(x, y, new int[4]);
	            if(pixel[3] > 0){ //If pixel isn't full alpha. Could also be pixel[3]==255
	                g2d.fillRect(x, y, 1, 1);
	            }
	        }
	    }
	    g2d.dispose();
	    return img;
	}
	protected static final Color PURPLE = new Color(178,0,255);
	protected static final char SECTION = '§';
	protected boolean bold = false, underline = false;
	public static int stringWidth(String str){
		str = str.replaceAll("§([0rgboypuB]|(c[\\dA-Za-z]{6}))", "");
		int length = 0;
		for(char c : str.toCharArray()){
			length += letters[c].getWidth();
		}
		return length;
	}
	public static void drawString(Graphics2D g2d, String str, int x, int y){
		char[] string = str.toCharArray();
		int startX = x;
		Color tint = null;
		boolean bold = false, underline = false;
		for(int i = 0; i < string.length; i++){
			char c = string[i];
			if(c == '\n'){
				y += 10;
				x = startX;
			}else if(c == '\t'){
				g2d.drawImage(letters[' '],x,y,null);
				x += letters[' '].getWidth(null);
				g2d.drawImage(letters[' '],x,y,null);
				x += letters[' '].getWidth(null);
			}else if(c == SECTION){ /* section symbol */
				if(++i >= string.length){
					g2d.drawImage(letters[SECTION],x,y,null);
					
					x += letters[SECTION].getWidth(null);
					//throw new RuntimeException("Letter modifier format at index "+i+" in string \""+str+"\".");
				}else{
					c = string[i];
					if(c == 'c'){
						if(i >= string.length-8){
							g2d.drawImage(letters[SECTION], x, y, null);
							x += letters[SECTION].getWidth(null);
							g2d.drawImage(letters[c],x,y,null);
							x += letters[c].getWidth(null);
							//throw new RuntimeException("Letter modifier format at index "+i+" in string \""+str+"\".");
						}else{
							String color = str.substring(i+1,i+9);
							
							int a = Integer.decode("#"+color.substring(0, 2));
							int r = Integer.decode("#"+color.substring(2,4));
							int g = Integer.decode("#"+color.substring(4,6));
							int b = Integer.decode("#"+color.substring(6));
							
							if(a == 0)
								tint = null;
							else
								tint = new Color(r,g,b,a);
							//debug("set color to "+tint.toString());
							i+=8;
							
						}
					}else if(c == '0'){
						tint = null;
						bold = false;
						underline = false;
					}else if(c == 'r'){
						tint = Color.RED;
					}else if(c == 'g'){
						tint = Color.GREEN;
					}else if(c == 'b'){
						tint = Color.BLUE;
					}else if(c == 'o'){
						tint = Color.ORANGE;
					}else if(c == 'y'){
						tint = Color.YELLOW;
					}else if(c == 'p'){
						tint = PURPLE;
					}else if(c == 'u'){
						underline = !underline;
					}else if(c == 'B'){
						bold = !bold;
					}else{
						i--;
						c = string[i];
						//g2d.drawImage(letters[SECTION], x, y, null);
						//x += letters[SECTION].getWidth();
						g2d.drawImage(letters[c],x,y,null);
						x += letters[c].getWidth(null);
					}
				}
			}else{
				//drawCharacter(g2d,letters[c],x,y,tint);
				Color underlineColor = Color.WHITE;
				if(tint != null)
					underlineColor = new Color((tint.getRed()+255)/2,(tint.getGreen()+255)/2,(tint.getBlue()+255)/2);
				if(bold){
					if(underline){
						g2d.setColor(underlineColor);
						g2d.drawLine(x, y+9, x+letters[c].getWidth(null)+1, y+9);
					}
					g2d.drawImage(tint(letters[c],tint),x,y,null);
					g2d.drawImage(tint(letters[c],tint),x+1,y,null);
					
					x++;
				}else{
					if(underline){
						g2d.setColor(underlineColor);
						g2d.drawLine(x, y+9, x+letters[c].getWidth(null), y+9);
					}
					g2d.drawImage(tint(letters[c],tint),x,y,null);
				}
				x += letters[c].getWidth(null);
			}
		}
	}
	
	protected List<Button> buttons = new ArrayList<>();
	
	@Override
	public void mousePressed(MouseEvent e){
		if(!buttons.isEmpty())
		for(Button b : buttons){
			b.mousePressed(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e){
		if(!buttons.isEmpty())
			for(Button b : buttons){
				b.mouseReleased(e);
			}
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		if(!buttons.isEmpty())
			for(Button b : buttons){
				b.mouseClicked(e);
			}
	}
	
	@Override
	public void mouseEntered(MouseEvent e){
		if(!buttons.isEmpty())
			for(Button b : buttons){
				b.mouseEntered(e);
			}
	}
	
	@Override
	public void mouseExited(MouseEvent e){
		if(!buttons.isEmpty())
			for(Button b : buttons){
				b.mouseExited(e);
			}
	}
}

