package com.ombda.gui;

import static com.ombda.Debug.debug;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import com.ombda.Frame;
import com.ombda.Panel;
public abstract class Button implements MouseListener{
	private BufferedImage normal, pressed;
	private int x, y;
	private boolean isBeingPressed = false;
	public Button(BufferedImage image, BufferedImage image2, int x, int y){
		normal = image;
		pressed = image2;
		this.x = x;
		this.y = y;
		if(image.getHeight() != image2.getHeight() || image.getWidth() != image2.getWidth())
			throw new RuntimeException("No hitbox provided, but images aren't the same size!");
		//hitbox = new Rectangle2D.Double(x,y,image.getWidth(),image.getHeight());
	}
	public Button(BufferedImage image, int x, int y){
		this(image,image,x,y);
	}
	/*public Button(BufferedImage image, BufferedImage image2, Shape s, int x, int y){
		normal = image;
		pressed = image2;
		this.x = x;
		this.y = y;
		hitbox = s;
	}*/
	
	@Override
	public void mouseClicked(MouseEvent e){}
	
	@Override
	public final void mouseEntered(MouseEvent e){}
	
	@Override
	public final void mouseExited(MouseEvent e){}
	
	@Override
	public void mousePressed(MouseEvent e){
		int[] coords = Panel.screenCoordsToImageCoords(e.getX(), e.getY());
		int x = coords[0], y = coords[1]-(int)(17*(Frame.HEIGHT/(double)Panel.getInstance().getParent().getHeight()));
		
		if(x >= this.x && x <= this.x+normal.getWidth() && y > this.y && y <= this.y+normal.getHeight()){
			isBeingPressed = true;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e){
		int[] coords = Panel.screenCoordsToImageCoords(e.getX(), e.getY());
		int x = coords[0], y = coords[1]-(int)(17*(Frame.HEIGHT/(double)Panel.getInstance().getParent().getHeight()));;
		
		if(isBeingPressed && x >= this.x && x <= this.x+normal.getWidth() && y > this.y && y <= this.y+normal.getHeight()){
			isBeingPressed = false;
			buttonPressed();
		}
	}
	
	public abstract void buttonPressed();
	
	public void draw(Graphics2D g2d){
		if(isBeingPressed){
			g2d.drawImage(pressed,x,y,null);
		}else g2d.drawImage(normal, x, y, null);
	}
	
}
