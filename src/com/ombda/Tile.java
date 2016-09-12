package com.ombda;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;

public class Tile implements Collideable{
	private static Tile[] tiles = new Tile[0xFF];
	
	private static short id_count = 0;
	private Image image;
	private Shape boundingBox;
	public final short id;
	//public static final int NO_COLLIDE = Color.white.getRGB(), COLLIDE = Color.black.getRGB(), WATER = Color.gray.getRGB();
	public Tile(Image image, Shape boundingBox){
		this.image = image;
		this.boundingBox = boundingBox;
		while(tiles[id_count] != null) id_count++;
		tiles[id_count] = this;
		id = id_count;
	}
	public Tile(short id, Image image, Shape boundingBox){
		this.image = image;
		this.boundingBox = boundingBox;
		if(tiles[id] != null) throw new RuntimeException("Duplicate tile id: "+id);
		tiles[id] = this;
		this.id = id;
	}
	public void draw(Graphics2D g, int x, int y){
		g.drawImage(image,x,y,null);
	}
	public String toString(){
		return "[object Tile]";
	}
	public boolean doesPointCollide(int x, int y){
		x %= 16;
		y %= 16;
		return boundingBox.contains(x,y);
	}
	public void testCollision(){}
	public boolean doesPointCollide(double x, double y){
		return doesPointCollide((int)x,(int)y);
	}
	public void manageCollision(Collideable c){}
	
	public static Tile getTile(int id){
		if(id < 0 || id >= 0xFF) throw new RuntimeException("Invalid id: "+id);
		if(tiles[id] == null) throw new RuntimeException("No tile with id "+id);
		return tiles[id];
	}
	public Shape getBoundingBox(){
		return boundingBox;
	}
	
}
