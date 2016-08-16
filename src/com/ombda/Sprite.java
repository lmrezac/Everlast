package com.ombda;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite{
	protected BufferedImage image;
	public double x, y;
	protected Sprite(BufferedImage bimg, int x, int y){
		this.x = x;
		this.y = y;
		image = bimg;
	}
	public Sprite(int x, int y, BufferedImage bimg){
		this(bimg,x,y);
		System.out.println("New Sprite created at ("+x+","+y+")");
	}
	public Sprite(BufferedImage bimg){
		this(0,0,bimg);
	}
	public void setPos(int newx, int newy){
		x = newx;
		y = newy;
	}
	
	public void draw(Graphics2D g, int offsetX, int offsetY){
		g.drawImage(image,(int)x+offsetX,(int)y+offsetY,null);
	}
}
