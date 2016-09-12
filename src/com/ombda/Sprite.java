package com.ombda;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Sprite{
	protected Map map;
	protected Image image;
	public double x, y;
	private int hash;
	protected Sprite(int hash, Image bimg, int x, int y){
		this.x = x;
		this.y = y;
		image = bimg;
		this.hash = hash;
	}
	public int hashCode(){
		return hash;
	}
	public void setMap(Map map){
		if(this.map != null){
			this.map.removeSprite(this);
		}
		this.map = map;
		this.map.addSprite(this);
	}
	public Map getMap(){
		return map;
	}
	public Sprite(int x, int y, Image bimg,int hash){
		this(hash,bimg,x,y);
		System.out.println("New Sprite created at ("+x+","+y+")");
	}
	public Sprite(BufferedImage bimg,int hash){
		this(0,0,bimg,hash);
	}
	public void setPos(int newx, int newy){
		x = newx;
		y = newy;
	}
	protected Point rayTrace(Point collision, Point origin){
		double deltaY = collision.y - origin.y;
		double deltaX = collision.x - origin.y;
		Tile t;
		double Xlast = origin.x, Ylast = origin.y;
		for(double X = origin.x, Y = origin.y; ((collision.y < origin.y)? Y > collision.y : Y < collision.y) && ((collision.x < origin.x)? X > collision.x : X < collision.x); X += deltaX, Y += deltaY){
			t = map.getTileAt((int)X,(int)Y, 0);
			if(t.doesPointCollide(X, Y)){
				return new Point((int)Xlast,(int)Ylast);
			}
			Xlast = X;
			Ylast = Y;
		}
		return new Point((int)Xlast,(int)Ylast);
	}
	public void setPos(double newx, double newy){
		setPos((int)newx,(int)newy);
	}
	
	public void draw(Graphics2D g, int offsetX, int offsetY){
		g.drawImage(image,(int)x+offsetX,(int)y+offsetY,null);
	}
}
