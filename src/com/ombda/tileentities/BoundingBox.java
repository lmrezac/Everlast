package com.ombda.tileentities;

import java.awt.geom.Rectangle2D;

import com.ombda.Collideable;
import com.ombda.Player;
import com.ombda.Tile;

public class BoundingBox extends TileEntity{
	
	public BoundingBox(int x, int y, int width, int height){
		super(x, y);
		this.boundingBox = new Rectangle2D.Double(0,0,(Tile.SIZE/16)*width,(Tile.SIZE/16)*height);
	}

	@Override
	public void manageCollision(Collideable c){}

	@Override
	public void update(){}

	@Override
	public void onInteracted(Player p, int x, int y){}

	@Override
	public String save(){
		return "box "+((Rectangle2D.Double)this.boundingBox).getWidth()+" "+((Rectangle2D.Double)this.boundingBox).getHeight();
	}

}
