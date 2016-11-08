package com.ombda;

import java.awt.Shape;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
@Deprecated
public class Tilemap{
	private Tile[] tiles;
	
	public Tilemap(int id){
		List<Tile> tiles = new ArrayList<>();
		File f = new File(Files.localize("tilemaps\\"+id));
		if(f.mkdir()){
			f.delete();
			throw new RuntimeException("Directory "+Files.localize("tilemaps\\"+id)+" is empty!");
		}
		File file = new File(Files.localize("tilemaps\\"+id+"\\0"));
		if(file.mkdir()){
			file.delete();
			throw new RuntimeException("Directory "+Files.localize("tilemaps\\"+id)+" needs to have a tile of id 0!");
		}
		int count = 1;
		do{
			
			Shape box = BoundingBox.FULL;
			ImageIcon image = null;
			if((f = new File(Files.localize("tilemaps\\"+id+"\\"+count+"\\tile.info"))).exists()){
				List<String> lines = Files.readLines(f);
				for(String str : lines){
					if(!str.startsWith("#")){
						int i = str.indexOf(' ');
						String key = str.substring(0,i);
						str = str.substring(i+1);
						if(key.equals("image"))
							image = Images.retrieve("tilemaps\\"+id+"\\"+count+"\\"+str+".png");
						else if(key.equals("collision")){
							box = new BoundingBox(str).shape;
						}
						else throw new RuntimeException("Invalid key in tile file "+Files.localize("tilemaps\\"+id+"\\"+count+"\\tile.info"));
					}
				}
			}
			if(image == null){
				debug("Loading default image for tile id "+count+" in tilemap id "+id);
				image = Images.retrieve("tilemaps\\"+id+"\\"+count+"\\layer0.png");
			}
			tiles.add(new Tile(image,box));
			file = new File(Files.localize("tilemaps\\"+id+"\\"+ ++count));
		}while(file.exists());
		
	}

	public Tile getTile(int id){
		return tiles[id];
	}
}
