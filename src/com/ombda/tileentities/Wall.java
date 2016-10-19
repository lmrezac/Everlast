package com.ombda.tileentities;

import java.awt.geom.Rectangle2D;

import com.ombda.Collideable;
import com.ombda.Facing;
import com.ombda.Tile;
import com.ombda.entities.Player;

public class Wall extends TileEntity{
	private Facing dir;
	public Wall(int x, int y,boolean disableCollisions,Facing direction){
		super(x,y,disableCollisions);
		this.dir = direction;
		if(direction == Facing.N)
			setBoundingBox(new Rectangle2D.Double(0,0,Tile.SIZE,Tile.SIZE/16));
		else if(direction == Facing.E)
			setBoundingBox(new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16, 0, Tile.SIZE/16, Tile.SIZE));
		else if(direction == Facing.S)
			setBoundingBox(new Rectangle2D.Double(0, Tile.SIZE-Tile.SIZE/16, Tile.SIZE, Tile.SIZE/16));
		else if(direction == Facing.W)
			setBoundingBox(new Rectangle2D.Double(0, 0, Tile.SIZE/16, Tile.SIZE));
	}

	@Override
	public void manageCollision(Collideable c){}

	@Override
	public void update(){}
	
	@Override
	public String save(){
		return "wall "+dir.toString();
	}

	@Override
	public void onInteracted(Player p, int x, int y){}
}
