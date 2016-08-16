package com.ombda;

import static com.ombda.Debug.debug;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map{
	private short[][] foreground, background;
	private int width = 0, height = 0;
	private String name;
	public int playerSpawnX = 0, playerSpawnY = 0;
	public Map(String name){
		this.name = name;
		foreground = load(Files.readBytes("maps\\"+name+"\\fore.map"));
		background = load(Files.readBytes("maps\\"+name+"\\back.map"));
	}
	private short[][] load(byte[] bytes){
		width = 0;
		int index = 0;
		for(; bytes[index] != 0; index++){
			width += bytes[index];
		}
		index++;
		debug("index = "+index);
		debug("width = "+width);
		debug("bytes.length = "+bytes.length);
		short[][] tiles = new short[height = (bytes.length-index)/2/width][width];
		debug("height = "+height);
		int x = 0, y = 0;
		for(; index < bytes.length-1; index+=2){
			debug("x:"+x+" y:"+y+" indexes: "+index+" "+(index+1));
			short id = (short)((short)bytes[index]+(short)bytes[index+1]);
			tiles[y][x] = id;
			x++;
			if(x >= width){
				x = 0;
				y++;
			}
		}
		return tiles;
		
	
		
	}
	public void save(){
		debug("Saved map to "+Files.localize("maps\\"+name));
		saveShorts(foreground,"maps\\"+name+"\\fore.map");
		saveShorts(background,"maps\\"+name+"\\back.map");
	}
	private void saveShorts(short[][] tiles,String filename){
		List<Byte> tempByteList = new ArrayList<>();
		int width = this.width;
		while(width > Byte.MAX_VALUE){
			tempByteList.add(Byte.MAX_VALUE);
			width-=Byte.MAX_VALUE;
		}
		tempByteList.add((byte)width);
		//System.out.println(tempByteList);
		byte[] bytes = new byte[tempByteList.size()+1+width*height*2];
		int index = 0;
		for(; index < tempByteList.size(); index++){
			bytes[index] = tempByteList.get(index);
		}
		index++;
		bytes[index] = 0;
		int x = 0, y = 0;
		for(; index < bytes.length-1; index+=2){
			short value = tiles[y][x];
			
			byte first, second;
			if(value > Byte.MAX_VALUE){
				first = Byte.MAX_VALUE;
				second = (byte)(value-Byte.MAX_VALUE);
			}else{
				first = (byte)value;
				second = 0;
			}
			bytes[index] = first;
			bytes[index+1] = second;
			
			x++;
			if(x >= width){
				x = 0;
				y++;
			}
		}
		Files.writeBytes(filename, bytes);
	}
	public int width(){
		return width;
	}
	public int height(){
		return height;
	}
	public Tile getTileAt(int x, int y, int layer){  
		assert layer != 0 && layer != 1 : "Invalid layer: "+layer;
		if(layer == 0)
			return Tile.getTile(background[y/16][x/16]);
		else return Tile.getTile(foreground[y/16][x/16]);
	}
	public void setTileAt(int x, int y, int layer, Tile t){
		debug("Set tile at ("+x+","+y+") to id "+t.id);
		assert layer != 0 && layer != 1 : "Invalid layer: "+layer;
		if(layer == 0)
			background[y/16][x/16] = t.id;
		else foreground[y/16][x/16] = t.id;
	}
	public void drawBackground(Graphics2D g2d, int offsetX, int offsetY){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				Tile t = getTileAt(16*x,16*y,0);
				t.draw(g2d, 16*x+offsetX, 16*y+offsetY);
			}
		}
	}
	public void drawForeground(Graphics2D g2d, int offsetX, int offsetY){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				Tile t = getTileAt(16*x,16*y,1);
				t.draw(g2d, 16*x+offsetX, 16*y+offsetY);
			}
		}
	}
	
	public String toString(){ return name;}
	
	private static HashMap<String,Map> maps = new HashMap<>();
	public static Map get(String name){
		if(maps.containsKey(name)) return maps.get(name);
		else{
			Map map = new Map(name);
			maps.put(name, map);
			return map;
		}
	}
	
}
