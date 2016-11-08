package com.ombda;

import static com.ombda.Debug.debug;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.ombda.entities.Player;

public class MapOld{
	private Tilemap tilemap;
	private int[][][] layers;
	private boolean[] layerIsBackground;
	private int width, height;
	public MapOld(String name){
		List<String> lines = Files.readLines("maps\\"+name+"\\.map");
		String header = lines.get(0);
		debug("lines: "+lines);
		int tilemapid = 0;
		if(header.startsWith("#tilemap ")){
			lines.remove(0);
			tilemapid = Integer.parseInt(header.substring(9));
		}
		
		loadObjects(Files.readLines("maps\\"+name+"\\.objects"));
		
		this.tilemap = new Tilemap(tilemapid);
		load(lines);
	}
	private HashMap<String,String> getInfo(String str){
		HashMap<String,String> info = new HashMap<>();
		int startpos = 0, pos = 0;
		boolean inString = false;
		for(; pos < str.length(); pos++){
			if(str.charAt(pos) == ',' && !inString){
				String key = str.substring(startpos,pos);
				String value = key;
				if(key.contains("=")){
					int i = key.indexOf("=");
					value = key.substring(i+1);
					key = key.substring(0, i);
				}
				info.put(key, value);
				startpos = pos+1;
			}else if(str.charAt(pos) == '"') inString = !inString;
		}
		String key = str.substring(startpos,pos);
		String value = key;
		if(key.contains("=")){
			int i = key.indexOf("=");
			value = key.substring(i+1);
			key = key.substring(0, i);
		}
		info.put(key, value);
		return info;
	}
	private void loadObjects(List<String> lines){
		for(String str : lines){
			if(str.startsWith("#")&&!str.startsWith("##")){
				///if(str.matches("[\\w\\s]+\\=.+")){
				HashMap<String,String> info = getInfo(str.substring(1));
				if(info.containsKey("start")){
					int startX = 0, startY = 0;
					String value = info.get("start");
					int i = value.indexOf(' ');
					startX = Integer.parseInt(value.substring(0, i));
					startY = Integer.parseInt(value.substring(i+1));
					Player player = Panel.getInstance().getPlayer();
					player.x = 16*startX;
					player.y = 16*startY;
				}
				//}
			}
		}
	}
	private void load(List<String> lines){
		
		List<HashMap<String,String>> layerData = new ArrayList<>();
		List<List<String>> layers = new ArrayList<>();
		//layers.add(new ArrayList<String>());
		for(String str : lines){
			if(str.startsWith("#")){
				layers.add(new ArrayList<String>());
				HashMap<String,String> info = getInfo(str.substring(1));
				layerData.add(info);
			}else{
				layers.get(layers.size()-1).add(str);
			}
		}
		this.layers = new int[layers.size()][][];
		this.layerIsBackground = new boolean[layers.size()];
		
		for(int i = 0; i < layers.size(); i++){
			List<String> layer = layers.get(i);
			this.layers[i] = new int[layer.size()][];
			for(int j = 0; j < layer.size(); j++){
				
				String row = layer.get(j);
				debug("row = "+row);
				List<Integer> values = new ArrayList<>();
				Scanner scan = new Scanner(row);
				scan.useDelimiter(",");
				while(scan.hasNext())
					values.add(getTileIDFromString(scan.next()));
				scan.close();
				this.layers[i][j] = new int[values.size()];
				//debug(values);
				for(int k = 0; k < values.size(); k++){
					this.layers[i][j][k] = values.get(k);
				}
				//debug(Arrays.toString(this.layers[i][j]));
			}
		}
		for(int i = 0; i < layerData.size(); i++){
			HashMap<String,String> info = layerData.get(i);
			if(info.containsKey("background")){
				layerIsBackground[i] = true;
			}else if(info.containsKey("foreground")){
				layerIsBackground[i] = false;
			}
		}
		
		for(int i = 0; i < this.layers.length; i++){
			for(int j = 0; j < this.layers[i].length; j++){
				debug("@["+i+","+j+"]="+Arrays.toString(this.layers[i][j]));
			}
		}
		
		
		height = this.layers[0].length;
		width = this.layers[0][0].length;
		for(int[][] layer : this.layers){
			if(layer.length > height)
				height = layer.length;
			for(int[] row : layer){
				if(row.length > width)
					width = row.length;
			}
		}
		debug("width = "+width+" height = "+height);
	}
	public int width(){
		return width;
	}
	public int height(){
		return height;
	}
	private static int getTileIDFromString(String str){
		return Integer.parseInt(str);
	}
	public void drawBackground(Graphics2D g, int offsetX, int offsetY){
		for(int i = 0; i < layers.length; i++){
			if(layerIsBackground[i]){
				int[][] layer = layers[i];
				for(int y = 0; y < height; y++){
					for(int x = 0; x < width; x++){
						tilemap.getTile(layer[y][x]).draw(g,16*x+offsetX,16*y+offsetY);
					}
				}
			}
		}
	}
	public void drawForeground(Graphics2D g, int offsetX, int offsetY){
		for(int i = 0; i < layers.length; i++){
			if(!layerIsBackground[i]){
				int[][] layer = layers[i];
				for(int y = 0; y < height; y++){
					for(int x = 0; x < width; x++){
						tilemap.getTile(layer[y][x]).draw(g,16*x+offsetX,16*y+offsetY);
					}
				}
			}
		}
	}
	public String toString(){
		return "[object Map]";
	}
	public Tile getTileAt(int pixelX, int pixelY,int layer){
		return tilemap.getTile(layers[layer][pixelY/16][pixelX/16]);
	}
	
}
