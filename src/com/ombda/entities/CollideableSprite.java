package com.ombda.entities;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import com.ombda.Collideable;
import com.ombda.Map;
import com.ombda.Tile;

public class CollideableSprite extends Sprite implements Collideable{
	private Shape boundingBox;
	/*public CollideableSprite(int x, int y, ImageIcon bimg, int hash,Shape boundingBox){
		super(hash,bimg,x,y);
		this.boundingBox = boundingBox;
		
	}*/
	public CollideableSprite(int hash,ImageIcon bimg, int x, int y,Shape boundingBox,Map map){
		super(hash,bimg,x,y,map);
		this.boundingBox = boundingBox;
	}
	public CollideableSprite(int hash, ImageIcon bimg, int x, int y, int width, int height,Map map){
		this(hash,bimg,x,y,new Rectangle2D.Double((Tile.SIZE/16)*x,(Tile.SIZE/16)*y,(Tile.SIZE/16)*width,(Tile.SIZE/16)*height),map);
	}
	public CollideableSprite(int hash, ImageIcon bimg, int x, int y,Map map){
		this(hash,bimg,x,y,new Rectangle2D.Double((Tile.SIZE/16)*x,(Tile.SIZE/16)*y,(Tile.SIZE/16)*bimg.getIconWidth(),(Tile.SIZE/16)*bimg.getIconHeight()),map);
	}

	@Override
	public boolean doesPointCollide(int x, int y){
		return boundingBox.contains(x, y);
	}

	@Override
	public Shape getBoundingBox(){
		return boundingBox;
	}

	@Override
	public void manageCollision(Collideable c){}

}
