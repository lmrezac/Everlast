package com.ombda.tileentities;

import java.awt.geom.Rectangle2D;

import com.ombda.Collideable;
import com.ombda.Facing;
import com.ombda.Player;

public class Wall extends TileEntity{
	private Facing dir;
	public Wall(int x, int y,Facing direction){
		super(x,y);
		this.dir = direction;
		if(direction == Facing.N)
			boundingBox = new Rectangle2D.Double(0,0,16,1);
		else if(direction == Facing.E)
			boundingBox = new Rectangle2D.Double(15, 0, 1, 16);
		else if(direction == Facing.S)
			boundingBox = new Rectangle2D.Double(0, 15, 16, 1);
		else if(direction == Facing.W)
			boundingBox = new Rectangle2D.Double(0, 0, 1, 16);
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
