package com.ombda.gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.ombda.Frame;
import com.ombda.Images;
import com.ombda.Panel;
import com.ombda.Tile;
public abstract class Button implements MouseListener{
	private ImageIcon normal, pressed;
	private int x, y;
	private int width, height;
	private boolean isBeingPressed = false;
	private String label;
	public Button(String str,int x, int y){
		this.label = str;
		normal = Images.retrieve("gui/button",false);
		pressed = Images.retrieve("gui/button_pressed",false);
		
		this.x = x;
		this.y = y;
		this.width =  normal.getIconWidth();
		this.height =  (Tile.SIZE/16)* normal.getIconHeight();
		if(normal.getIconHeight() != pressed.getIconHeight() || normal.getIconWidth() != pressed.getIconWidth())
			throw new RuntimeException("No hitbox provided, but images aren't the same size!");
		//hitbox = new Rectangle2D.Double(x,y,image.getWidth(),image.getHeight());
	}
	/*public Button(BufferedImage image, BufferedImage image2, Shape s, int x, int y){
		normal = image;
		pressed = image2;
		this.x = x;
		this.y = y;
		hitbox = s;
	}*/
	
	@Override
	public void mouseClicked(MouseEvent e){
		/*int[] coords = Panel.screenCoordsToImageCoords(e.getX(), e.getY());
		int x = coords[0], y = coords[1]-(int)(17*(Frame.HEIGHT/(double)Panel.getInstance().getParent().getHeight()));;
		if(x >= this.x && y >= this.y && x <= this.x+normal.getIconWidth() && y <= this.y+normal.getIconHeight()){
			isBeingPressed = true;
		}*/
	}
	
	@Override
	public final void mouseEntered(MouseEvent e){}
	
	@Override
	public final void mouseExited(MouseEvent e){}
	
	@Override
	public void mousePressed(MouseEvent e){
		int[] coords = Panel.screenCoordsToImageCoords(e.getX(), e.getY());
		int x = coords[0], y = coords[1]-(int)(17*(Frame.HEIGHT/(double)Panel.getInstance().getParent().getHeight()));
		
		if(x >= this.x && x <= this.x+width && y > this.y && y <= this.y+height){
			isBeingPressed = true;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e){
		int[] coords = Panel.screenCoordsToImageCoords(e.getX(), e.getY());
		int x = coords[0], y = coords[1]-(int)(17*(Frame.HEIGHT/(double)Panel.getInstance().getParent().getHeight()));;
		
		if(isBeingPressed && x >= this.x && x <= this.x+width && y > this.y && y <= this.y+height){
			isBeingPressed = false;
			buttonPressed();
		}
	}
	
	public abstract void buttonPressed();
	
	public void draw(Graphics2D g2d){
		if(isBeingPressed){
			g2d.drawImage(pressed.getImage(),x,y,null);
			int length = GUI.stringWidth(label);
			int drawX = this.normal.getIconWidth()/2 - length/2;
			GUI.drawString(g2d, GUI.SECTION+"y"+label, x+drawX, y+1);
		}else{
			g2d.drawImage(normal.getImage(), x, y, null);
			int length = GUI.stringWidth(label);
			int drawX = this.normal.getIconWidth()/2 - length/2;
			GUI.drawString(g2d, label, x+drawX, y+1);
		}
	}
	
}
