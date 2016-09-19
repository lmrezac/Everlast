package com.ombda;

public abstract class Entity{
	public double x, y;
	protected Map map;
	public Entity(int x, int y){
		this.x = x;
		this.y = y;
	}
	public Map getMap(){
		return map;
	}
	public void setMap(Map map){
		if(this.map != null){
			this.map.removeEntity(this);
		}
		this.map = map;
		this.map.addEntity(this);
	}
}
