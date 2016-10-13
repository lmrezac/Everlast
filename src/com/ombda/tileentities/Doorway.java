package com.ombda.tileentities;

import com.ombda.Collideable;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Player;
import com.ombda.Tile;

public class Doorway extends TileEntity{
	private String mapname;
	private int tilex, tiley;
	private boolean onCollision;
	public Doorway(int x, int y, boolean disableCollisions, String map, int tilex, int tiley, boolean onCollision){
		super(x, y, disableCollisions);
		this.tilex = tilex;
		this.tiley = tiley;
		this.mapname = map;
		this.onCollision = onCollision;
	}

	@Override
	public void manageCollision(Collideable c){
		if(!onCollision) return;
		if(c instanceof Player){
			Player p = (Player)c;
			Map map = Map.get(mapname);
			Panel.getInstance().setMap(map);
			p.setPos(Tile.SIZE*tilex, Tile.SIZE*tiley);
		}
	}

	@Override
	public String save(){
		return (onCollision? "warp " : "door ")+mapname+" "+tilex+" "+tiley;
	}

	@Override
	public void update(){}

	@Override
	public void onInteracted(Player p, int x, int y){
		if(doesPointCollide(x,y)){
			Map map = Map.get(mapname);
			Panel.getInstance().setMap(map);
			p.setPos(Tile.SIZE*tilex, Tile.SIZE*tiley);
		}
	}
}
