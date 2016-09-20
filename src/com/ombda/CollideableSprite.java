package com.ombda;

import java.awt.Shape;

import javax.swing.ImageIcon;

public class CollideableSprite extends Sprite implements Collideable{
	private Shape boundingBox;
	public CollideableSprite(int x, int y, ImageIcon bimg, int hash,Shape boundingBox){
		super(x, y, bimg, hash);
		this.boundingBox = boundingBox;
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
