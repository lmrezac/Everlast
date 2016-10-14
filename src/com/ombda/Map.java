package com.ombda;

import static com.ombda.Debug.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.ombda.scripts.Function;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;
import com.ombda.scripts.Struct;
import com.ombda.scripts.VarNotExists;
import com.ombda.tileentities.Doorway;
import com.ombda.tileentities.TileEntity;
import com.ombda.tileentities.Triangle;
import com.ombda.tileentities.Wall;

public class Map extends Struct{
	private short[][] foreground, background;
	private int width = 0, height = 0;
	private String name;
	public int playerSpawnX = 0, playerSpawnY = 0;
	private HashMap<Integer,Sprite> sprites = new HashMap<>();
	private List<Entity> entities = new ArrayList<>();
	private TileEntity[][] tileEntities;
	private Color clr;
	public Map(String name, int width, int height, Color clr){
		super(Scope.map_type,Arrays.asList("width","height","toString"));
		this.name = name;
		this.width = width;
		this.height = height;
		this.clr = clr;
		foreground = new short[height][width];
		background = new short[height][width];
		tileEntities = new TileEntity[height][width];
		maps.put(name, this);
	}
	public Map(String name){
		super(Scope.map_type,Arrays.asList("width","height","toString"));
		this.name = name;
		File f = new File(Files.localize("maps\\"+name));
		if(!f.exists()) throw new RuntimeException("Directory maps\\"+name+" doesn't exist!");
		foreground = load(Files.readBytes("maps\\"+name+"\\fore.map"));
		background = load(Files.readBytes("maps\\"+name+"\\back.map"));
		maps.put(name, this);
		if(Script.exists("map_"+name))
			Panel.getInstance().runScript(Script.getScript("map_"+name));
		f = new File(Files.localize("maps\\"+name+"\\background.color"));
		if(!f.exists())
			throw new RuntimeException("Background color info file for map "+name+" doesn't exist!");
		List<String> lines = Files.read(f);
		if(lines.size() != 1) throw new RuntimeException("Background color info file for map "+name+" is invalid, too many lines.");
		String colorstr = lines.get(0);
		if(colorstr.length() != 6)
			throw new RuntimeException("Background color info file for map "+name+" is invalid, too long or too short. Must be 6-character-long hexadecimal color literal.");
		int byte1 = Integer.parseInt(colorstr.substring(0,2),16);
		int byte2 = Integer.parseInt(colorstr.substring(2,4),16);
		int byte3 = Integer.parseInt(colorstr.substring(4),16);
		clr = new Color(byte1,byte2,byte3);
		f = new File(Files.localize("maps\\"+name+"\\tileEntities.info"));
		if(!f.exists())
			throw new RuntimeException("Entity info file for map "+name+" doesn't exist!");
		loadEntities(Files.read(f));
	}
	private final Function toString = new Function(null,null,false){
		@Override
		public int args_length(){ return 0; }
		@Override
		public String call(Scope script,List<String> values){
			return Map.this.name;
		}
	};
	@Override
	public String getVar(String varname, Scope scopeIn){
		if(varname.equals("width"))
			return Integer.toString(Map.this.width);
		else if(varname.equals("height"))
			return Integer.toString(Map.this.height);
		else if(varname.equals("tostring"))
			return toString.getIdStr();
		else if(varname.equals("name"))
			return name;
		else throw new VarNotExists("Variable "+varname+" does not exist in map "+Map.this.name);
	}
	@Override
	public void setVar(String varname, String value, boolean isfinal, Scope scopeIn){
		throw new RuntimeException("Cannot set variable "+varname+" in map "+Map.this.name);
	}
	public Color getBackground(){
		return clr;
	}
	private short[][] load(byte[] bytes){
		width = 0;
		int index = 0;
		for(; bytes[index] != 0; index++){
			width += bytes[index];
		}
		index++;
		//debug("index = "+index);
		//debug("width = "+width);
		//debug("bytes.length = "+bytes.length);
		short[][] tiles = new short[height = (bytes.length-index)/2/width][width];
		//debug("height = "+height);
		int x = 0, y = 0;
		for(; index < bytes.length-1; index+=2){
			//debug("x:"+x+" y:"+y+" indexes: "+index+" "+(index+1));
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
	private void loadEntities(List<String> lines){
		this.tileEntities = new TileEntity[height][width];
		for(String line : lines){
			line = line.trim();
			List<String> args = new ArrayList<>();
			Scanner scan = new Scanner(line);
			scan.useDelimiter(" ");
			while(scan.hasNext()) args.add(scan.next());
			scan.close();
			if(!args.isEmpty() && !line.equals("")){
				doTileEntity(args);
			}
		}
	}
	public void doTileEntity(List<String> args){
		
		if(args.size() < 3) throw new RuntimeException("Expected x and y for tile entity");
		int tilex = Integer.parseInt(args.remove(0));
		int tiley = Integer.parseInt(args.remove(0));
		if(tilex < 0 || tilex >= tileEntities[0].length)
			throw new RuntimeException("Invalid tile x "+tilex+" tiles[0].lenght = "+tileEntities[0].length);
		if(tiley < 0 || tiley >= tileEntities.length)
			throw new RuntimeException("Invalid tile y"+tiley);
		if(tileEntities[tiley][tilex] != null)
			throw new RuntimeException("Multiple tile entities found at ["+tilex+","+tiley+"]");
		boolean disableCollisions = Boolean.parseBoolean(args.remove(0));
		String cmd = args.remove(0);
		if(cmd.equals("warp")){
			if(args.size() != 3)
				throw new RuntimeException("tile entity warp requires 3 values : destination map, destination tile x, destination tile y.");
			String map = args.get(0);
			int dx = Integer.parseInt(args.get(1));
			int dy = Integer.parseInt(args.get(2));
			File f = new File(Files.localize("maps\\"+map));
			if(!f.exists() || !f.isDirectory())
				throw new RuntimeException("There is no map called "+map);
			
			tileEntities[tiley][tilex] = new Doorway(tilex,tiley,disableCollisions,map,dx,dy,true);
		}else if(cmd.equals("door")){
			if(args.size() != 3)
				throw new RuntimeException("tile entity warp requires 3 values : destination map, destination tile x, destination tile y.");
			String map = args.get(0);
			int dx = Integer.parseInt(args.get(1));
			int dy = Integer.parseInt(args.get(2));
			File f = new File(Files.localize("maps\\"+map));
			if(!f.exists() || !f.isDirectory())
				throw new RuntimeException("There is no map called "+map);
			
			tileEntities[tiley][tilex] = new Doorway(tilex,tiley,disableCollisions,map,dx,dy,false);
		}else if(cmd.equals("wall")){
			if(args.size() != 1)
				throw new RuntimeException("tile entity wall requires 3 values : tile x, tile y, direction");
			Facing f = Facing.fromString(args.get(0));
			tileEntities[tiley][tilex] = new Wall(tilex,tiley,disableCollisions,f);
		}else if(cmd.equals("triangle")){
			if(args.size() != 10)
				throw new RuntimeException("tile entity triangle requires 12 values : tile x, tile y, x1, y1, x2, y2, x3, y3, fromNorth, fromEast, fromSouth, fromWest");
			tileEntities[tiley][tilex] = new Triangle(tilex,tiley,disableCollisions,Integer.parseInt(args.get(0)),Integer.parseInt(args.get(1)),Integer.parseInt(args.get(2)),Integer.parseInt(args.get(3)),Integer.parseInt(args.get(4)),Integer.parseInt(args.get(5)),Integer.parseInt(args.get(6)),Integer.parseInt(args.get(7)),Integer.parseInt(args.get(8)),Integer.parseInt(args.get(9)));
		}else if(cmd.equals("box")){
			if(args.size() != 2)
				throw new RuntimeException("tile entity box requires 4 values : tile x, tile y, width, height");
			tileEntities[tiley][tilex] = new com.ombda.tileentities.BoundingBox(tilex,tiley,disableCollisions,Integer.parseInt(args.get(0)),Integer.parseInt(args.get(1)));
		}else throw new RuntimeException("Invalid tile entity : "+cmd);
		debug("new tile entity created at ["+tilex+","+tiley+"] : "+tileEntities[tiley][tilex].save());
	}
	private static String toHexString(int i){
		String str = Integer.toHexString(i);
		if(str.length() == 1) return "0"+str;
		return str;
	}
	public void save(){
		debug("Saved map to "+Files.localize("maps\\"+name));
		saveShorts(foreground,"maps\\"+name+"\\fore.map");
		saveShorts(background,"maps\\"+name+"\\back.map");
		List<String> lines = new ArrayList<>();
		lines.add(toHexString(clr.getRed())+toHexString(clr.getGreen())+toHexString(clr.getBlue()));
		Files.write("maps\\"+name+"\\background.color", lines);
		lines.clear();
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				if(tileEntities[y][x] != null)
					lines.add(x+" "+y+" "+tileEntities[y][x].disableTileCollisions()+" "+tileEntities[y][x].save());
			}
		}
		Files.write("maps\\"+name+"\\tileEntities.info", lines);
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
	public void setSize(int newwidth, int newheight,boolean bottom, boolean left){
		int oldwidth = this.width;
		int oldheight = this.height;
		this.width = newwidth;
		this.height = newheight;
		short[][] newfore = new short[newheight][newwidth], newback = new short[newheight][newwidth];
		TileEntity[][] newte = new TileEntity[newheight][newwidth];
		//int startY = oldheight < height && bottom? height-oldheight : 0;
		//int startX = oldwidth < width && left? width-oldwidth : 0;
		/*for(int y = startY; y < Math.min(oldheight,height); y++){
			for(int x = startX; x < Math.min(oldwidth, width); x++){
				newfore[y][x] = this.foreground[y-startY][x-startX];
				newback[y][x] = this.background[y-startY][x-startX];
				newte[y][x] = this.tileEntities[y-startY][x-startX];
				if(newte[y][x] != null){
					newte[y][x].x = Tile.SIZE*x;
					newte[y][x].y = Tile.SIZE*y;
				}
			}
		}*/
		if(bottom){
			if(left){
				for(int y = newheight-1, y2 = oldheight-1; y >= Math.max(0, newheight-oldheight); y--, y2--){
					for(int x = newwidth-1, x2 = oldwidth-1; x >= Math.max(0,newwidth-oldwidth); x--, x2--){
						newfore[y][x] = this.foreground[y2][x2];
						newback[y][x] = this.background[y2][x2];
						newte[y][x] = this.tileEntities[y2][x2];
						if(newte[y][x] != null){
							newte[y][x].x = Tile.SIZE*x;
							newte[y][x].y = Tile.SIZE*y;
						}
					}
				}
			}else{
				for(int y = newheight-1, y2 = oldheight-1; y >= Math.max(0, newheight-oldheight); y--, y2--){
					for(int x = 0; x < newwidth; x++){
						newfore[y][x] = this.foreground[y2][x];
						newback[y][x] = this.background[y2][x];
						newte[y][x] = this.tileEntities[y2][x];
						if(newte[y][x] != null){
							newte[y][x].x = Tile.SIZE*x;
							newte[y][x].y = Tile.SIZE*y;
						}
					}
				}
			}
		}else{
			if(left){
				for(int y = 0; y < newheight; y++){
					for(int x = newwidth-1, x2 = oldwidth-1; x >= Math.max(0,newwidth-oldwidth); x--, x2--){
						newfore[y][x] = this.foreground[y][x2];
						newback[y][x] = this.background[y][x2];
						newte[y][x] = this.tileEntities[y][x2];
						if(newte[y][x] != null){
							newte[y][x].x = Tile.SIZE*x;
							newte[y][x].y = Tile.SIZE*y;
						}
					}
				}
			}else{
				for(int y = 0; y < newheight; y++){
					for(int x = 0; x < newwidth; x++){
						newfore[y][x] = this.foreground[y][x];
						newback[y][x] = this.background[y][x];
						newte[y][x] = this.tileEntities[y][x];
						if(newte[y][x] != null){
							newte[y][x].x = Tile.SIZE*x;
							newte[y][x].y = Tile.SIZE*y;
						}
					}
				}
			}
		}
		this.foreground = newfore;
		this.background = newback;
		this.tileEntities = newte;
	}
	
	public Tile getTileAt(int x, int y, int layer){  
		x /= Tile.SIZE;
		y /= Tile.SIZE;
		if(y < 0 || y >= height) return null;
		if(x < 0 || x >= width) return null; 
		if(layer == 0)
			return Tile.getTile(background[y][x]);
		else return Tile.getTile(foreground[y][x]);
	}
	public void setTileAt(int x, int y, int layer, Tile t){
		debug("Set tile at ("+x+","+y+") to id "+t.id);
		assert layer != 0 && layer != 1 : "Invalid layer: "+layer;
		if(layer == 0)
			background[y/Tile.SIZE][x/Tile.SIZE] = t.id;
		else foreground[y/Tile.SIZE][x/Tile.SIZE] = t.id;
	}
	public void drawBackground(Graphics2D g2d, int offsetX, int offsetY){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				Tile t = getTileAt(Tile.SIZE*x,Tile.SIZE*y,0);
				t.draw(g2d, Tile.SIZE*x+offsetX, Tile.SIZE*y+offsetY);
			}
		}
	}
	public void drawForeground(Graphics2D g2d, int offsetX, int offsetY){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				Tile t = getTileAt(Tile.SIZE*x,Tile.SIZE*y,1);
				t.draw(g2d, Tile.SIZE*x+offsetX, Tile.SIZE*y+offsetY);
			}
		}
	}
	public void drawTileEntities(Graphics2D g2d, int offsetX, int offsetY){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				TileEntity te = tileEntities[y][x];
				if(te != null){
					te.draw(g2d,offsetX,offsetY);
				}
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
	public Collection<Sprite> getSprites(){
		return sprites.values();
	}
	public void addSprite(Sprite s){
		sprites.put(s.hashCode(),s);
		addEntity(s);
	}
	public void removeSprite(Sprite s){
		sprites.remove(s.hashCode());
		removeEntity(s);
	}
	public void addEntity(Entity e){
		if(!entities.contains(e))
			entities.add(e);
	}
	public void removeEntity(Entity e){
		entities.remove(e);
	}
	public TileEntity getTileEntityAt(int tilex, int tiley){ 
		return tileEntities[tiley][tilex];
	}
	public void deleteTileEntityAt(int tilex, int tiley){
		tileEntities[tiley][tilex] = null;
	}
	public Sprite getSprite(int hashcode){
		if(!sprites.containsKey(hashcode))
			throw new RuntimeException("A sprite with id "+hashcode+" in map "+toString()+" does not exist!");
		return sprites.get(hashcode);
	}
}
