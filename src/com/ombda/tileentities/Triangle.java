package com.ombda.tileentities;

import java.awt.Polygon;
import java.awt.Shape;

import com.ombda.Collideable;
import com.ombda.Facing;
import com.ombda.Player;

public class Triangle extends TileEntity{
	int x1,y1,x2,y2,x3,y3;
	int fromNorth, fromSouth, fromEast, fromWest;
	public Triangle(int x, int y, int x1, int y1, int x2, int y2, int x3, int y3, int i, int j, int k, int l){
		super(x, y);
		Polygon p = new Polygon();
		p.addPoint(x1, y1);
		p.addPoint(x2, y2);
		p.addPoint(x3, y3);
		this.boundingBox = (Shape)p;
		this.x1 = x1; this.y1 = y1;
		this.x2 = x2; this.y2 = y2;
		this.x3 = x3; this.y3 = y3;
		this.fromNorth = i;
		this.fromSouth = k;
		this.fromEast = j;
		this.fromWest = l;
	}

	@Override
	public void manageCollision(Collideable c){
		System.out.println("collision");
		if(c instanceof Player){
			Player p = (Player)c;
			Facing d = p.getDirection();
			if(d == Facing.N){
				p.x += fromNorth;
			}else if(d == Facing.E){
				p.y += fromEast;
			}else if(d == Facing.S){
				p.x += fromSouth;
			}else if(d == Facing.W){
				p.y += fromWest;
			}
		}
	}

	@Override
	public void update(){}

	@Override
	public void onInteracted(Player p, int x, int y){}

	@Override
	public String save(){
		return "triangle "+x1+" "+y1+" "+x2+" "+y2+" "+x3+" "+y3+" "+fromNorth+" "+fromEast+" "+fromSouth+" "+fromWest;
	}

}
