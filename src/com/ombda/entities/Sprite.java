package com.ombda.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import com.ombda.Map;
import com.ombda.Point;
import com.ombda.Tile;
import static com.ombda.Debug.debug;
public class Sprite extends Entity{
	
	public ImageIcon image;
	public boolean hidden = false;
	private int hash;
	public Sprite(int hash, ImageIcon bimg, int x, int y, Map map){
		super((Tile.SIZE/16)*x,(Tile.SIZE/16)*y);
		image = bimg;
		this.hash = hash;
		this.setMap(map);
		debug("New Sprite created of id "+Integer.toHexString(hash)+" at ("+x+","+y+")");
	}
/*	public Sprite(int x, int y, ImageIcon bimg,int hash,Map map){
		this(hash,bimg,(Tile.SIZE/16)*x,(Tile.SIZE/16)*y);
		setMap(map);
		debug("New Sprite created of id "+Integer.toHexString(hash)+" at ("+this.x+","+this.y+")");
	}
	public Sprite(int x, int y, ImageIcon bimg, int hash){
		this(hash,bimg,(Tile.SIZE/16)*x,(Tile.SIZE/16)*y);
		debug("New Sprite created of id "+Integer.toHexString(hash)+" at ("+x+","+y+")");
	}
	public Sprite(ImageIcon bimg,int hash){
		this(0,0,bimg,hash);
		debug("New Sprite created of id "+Integer.toHexString(hash)+" at ("+0+","+0+")");
	}*/
	public int hashCode(){
		return hash;
	}
	public void setMap(Map map){
		if(this.map != null){
			this.map.removeSprite(this);
		}
		this.map = map;
		if(map != null)
			this.map.addSprite(this);
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
		x = newx;
		y = newy;
	}
	public double spriteHeight(){
		return image.getIconHeight();
	}
	public void draw(Graphics2D g, int offsetX, int offsetY){
		if(hidden) return;
		g.drawImage(image.getImage(),(int)x+offsetX,(int)y+offsetY,null);
	}
	public void drawBoundingBox(Graphics2D g, int offsetX, int offsetY){
		g.setColor(Color.red);
		g.drawRect((int)x+offsetX,(int)y+offsetY, image.getIconWidth(),image.getIconHeight());
	}
	public String toString(){
		return "sprite "+map.toString()+".0x"+Integer.toHexString(hash);
	}
}
