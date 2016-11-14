package com.ombda.entities;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import com.ombda.Collideable;
import com.ombda.Map;

public class CollideableSprite extends Sprite implements Collideable{
	private Shape boundingBox;
	public CollideableSprite(int x, int y, ImageIcon bimg, int hash,Shape boundingBox){
		super(x, y, bimg, hash);
		this.boundingBox = boundingBox;
		
	}
	public CollideableSprite(int x, int y, ImageIcon bimg, int hash,Shape boundingBox,Map map){
		this(x,y,bimg,hash,boundingBox);
		this.setMap(map);
	}
	public CollideableSprite(int x, int y, ImageIcon bimg, int hash, int width, int height){
		this(x,y,bimg,hash,new Rectangle2D.Double(x,y,width,height));
	}
	public CollideableSprite(int x, int y, ImageIcon bimg, int hash, int width, int height,Map map){
		this(x,y,bimg,hash,new Rectangle2D.Double(x,y,width,height),map);
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
