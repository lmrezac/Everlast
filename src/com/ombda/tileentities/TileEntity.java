package com.ombda.tileentities;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.ombda.Collideable;
import com.ombda.Entity;
import com.ombda.Interactable;
import com.ombda.Updateable;

public abstract class TileEntity extends Entity implements Collideable, Updateable, Interactable{
	protected Rectangle2D.Double boundingBox = new Rectangle2D.Double(0, 0, 16, 16);
	public TileEntity(int x, int y){
		super(16*x,16*y);
	}

	@Override
	public boolean doesPointCollide(int x, int y){
		x %= 16;
		y %= 16;
		return boundingBox.contains(x,y);
	}

	@Override
	public Shape getBoundingBox(){
		return boundingBox;
	}

	public abstract String save();
}
