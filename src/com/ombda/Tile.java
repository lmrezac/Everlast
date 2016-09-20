package com.ombda;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;

import javax.swing.ImageIcon;

import com.ombda.tileentities.TileEntity;

public class Tile implements Collideable, Interactable{
	public static Tile[] tiles = new Tile[0xFF];
	private static short id_count = 0;
	private ImageIcon image;
	private Image frame = null;
	private Shape boundingBox;
	public final short id;
	//public static final int NO_COLLIDE = Color.white.getRGB(), COLLIDE = Color.black.getRGB(), WATER = Color.gray.getRGB();
	public Tile(ImageIcon image, Shape boundingBox){
		this.image = image;
		this.boundingBox = boundingBox;
		while(tiles[id_count] != null) id_count++;
		tiles[id_count] = this;
		id = id_count;
	}
	public Tile(short id, ImageIcon image, Shape boundingBox){
		this.image = image;
		this.boundingBox = boundingBox;
		if(tiles[id] != null) throw new RuntimeException("Duplicate tile id: "+id);
		tiles[id] = this;
		this.id = id;
	}
	public Tile(int i, ImageIcon retrieve, Shape boundingBox){
		this((short)i,retrieve,boundingBox);
	}
	public void incrementFrame(){
		frame = image.getImage();
	}
	public void draw(Graphics2D g, int x, int y){
		g.drawImage(frame,x,y,null);
	}
	public String toString(){
		return "[object Tile]";
	}
	public boolean doesPointCollide(int x, int y){
		if(hasTileEntity(x/16,y/16)){
			TileEntity te = Panel.getInstance().getPlayer().getMap().getTileEntityAt(x/16, y/16);
			if(te != null)
				if(te.getBoundingBox().contains(x % 16,y % 16)){
					te.manageCollision(Panel.getInstance().getPlayer());
					return true;
				}
		}
		x %= 16;
		y %= 16;
		
		if(boundingBox.contains(x,y)){
			manageCollision(Panel.getInstance().getPlayer());
			return true;
		}
		return false;
	}
	public void testCollision(){}
	public boolean doesPointCollide(double x, double y){
		return doesPointCollide((int)x,(int)y);
	}
	public boolean hasTileEntity(int x, int y){
		return Panel.getInstance().getPlayer().getMap().getTileEntityAt(x, y) != null;
	}
	public void manageCollision(Collideable c){}
	
	public static Tile getTile(int id){
		if(id < 0 || id >= 0xFF) throw new RuntimeException("Invalid id: "+id);
		if(tiles[id] == null) throw new RuntimeException("No tile with id "+Integer.toHexString(id));
		return tiles[id];
	}
	public Shape getBoundingBox(){
		return boundingBox;
	}
	@Override
	public void onInteracted(Player p, int x, int y){
		if(hasTileEntity(x/16,y/16)){
			TileEntity te = Panel.getInstance().getPlayer().getMap().getTileEntityAt(x/16,y/16);
			te.onInteracted(p, x, y);
		}
	}
	
}
