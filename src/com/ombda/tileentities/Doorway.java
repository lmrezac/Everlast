package com.ombda.tileentities;

import com.ombda.Collideable;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Player;

public class Doorway extends TileEntity{
	private String mapname;
	private int tilex, tiley;
	public Doorway(int x, int y, String map, int tilex, int tiley){
		super(x, y);
		this.tilex = tilex;
		this.tiley = tiley;
		this.mapname = map;
	}

	@Override
	public void manageCollision(Collideable c){
		if(c instanceof Player){
			Player p = (Player)c;
			Map map = Map.get(mapname);
			Panel.getInstance().setMap(map);
			p.setPos(16*tilex, 16*tiley);
		}
	}

	@Override
	public String save(){
		return "warp "+mapname+" "+tilex+" "+tiley;
	}

	@Override
	public void update(){}

	@Override
	public void onInteracted(Player p, int x, int y){
		if(doesPointCollide(x,y)){
			Map map = Map.get(mapname);
			Panel.getInstance().setMap(map);
			p.setPos(16*tilex, 16*tiley);
		}
	}
}
