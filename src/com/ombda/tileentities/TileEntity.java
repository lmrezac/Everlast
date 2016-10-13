package com.ombda.tileentities;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.ombda.Collideable;
import com.ombda.Entity;
import com.ombda.Interactable;
import com.ombda.Tile;
import com.ombda.Updateable;

public abstract class TileEntity extends Entity implements Collideable, Updateable, Interactable{
	protected Shape boundingBox = new Rectangle2D.Double(0, 0, Tile.SIZE, Tile.SIZE);
	protected boolean disableCollisions;
	public TileEntity(int x, int y,boolean disableCollisions){
		super(Tile.SIZE*x,Tile.SIZE*y);
		this.disableCollisions = disableCollisions;
	}
	public boolean disableTileCollisions(){ return disableCollisions;}

	@Override
	public boolean doesPointCollide(int x, int y){
		x %= Tile.SIZE;
		y %= Tile.SIZE;
		return boundingBox.contains(x,y);
	}

	@Override
	public Shape getBoundingBox(){
		return boundingBox;
	}

	public abstract String save();
}
