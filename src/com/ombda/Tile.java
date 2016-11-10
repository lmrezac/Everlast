package com.ombda;

import static com.ombda.Debug.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;

import javax.swing.ImageIcon;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

import com.ombda.entities.Player;
import com.ombda.tileentities.TileEntity;
public class Tile extends AbstractJSObject implements Collideable, Interactable{
	public static final int SIZE = 32;
	public static Tile[] tiles = new Tile[0xFF];
	private static short id_count = 0;
	private ImageIcon image;
	private Image frame = null;
	private Shape boundingBox;
	public final short id;
	public Tile(short id, ImageIcon img, Shape boundingBox){
		//super(Script.tile_type, Arrays.asList("id","isAnimated","animationFrame"));
		/*this.setFinalVar("isAnimated", new Function(null,null,false){
			public int args_length(){ return 0; }
			public String call(Scope scopeIn, List<String> args){
				return image instanceof AnimatedImage? "1" : "0";
			}
		}.getIdStr(), this);
		this.setFinalVar("animationFrame", new Function(null,null,false){
			public int args_length(){ return 0; }
			public String call(Scope scopeIn, List<String> args){
				String result;
				if(!(image instanceof AnimatedImage)) result = "0";
				result = Script.toString(((AnimatedImage)image).index);
				debug("Getanimationframe = "+result);
				return result;
			}
		}.getIdStr(), this);
		*/
		this.image = img;
		this.boundingBox = boundingBox;
		if(tiles[id] != null) throw new RuntimeException("Duplicate tile id: 0x"+Integer.toHexString(id));
		tiles[id] = this;
		this.id = id;
		//super.setVar("id",Short.toString(this.id),false,this);
	}
	public Tile(short id, ImageIcon image){
		this(id,image,Tiles.EMPTY);
	}
	public Tile(short id, String image, Shape boundingBox){
		this(id,Images.retrieve(image),boundingBox);
	}
	public Tile(short id, String image){
		this(id,image,Tiles.EMPTY);
	}
	public Tile(int i, ImageIcon retrieve, Shape boundingBox){
		this((short)i,retrieve,boundingBox);
	}
	@Override
	public void setMember(String varname, Object value){
		if(varname.equals("id")){
			throw new RuntimeException("Cannot set variable id, it is final");
		}else if(varname.equals("animationFrame")){
			if(!(this.image instanceof AnimatedImage)) throw new RuntimeException("Cannot set animationFrame on non-animated tile");
			if(!(value instanceof Integer)) throw new RuntimeException("Cannot set tile variable animationFrame to value of type "+value.getClass().getName());
			((AnimatedImage)this.image).index = (Integer)value;
		}else
			super.setMember(varname,value);
	}
	@Override
	public boolean hasMember(String name){
		return name.equals("id") || name.equals("animationFrame") || name.equals("isAnimated");
	}
	@Override
	public Object getMember(String varname){
		if(varname.equals("id"))
			return this.id;
		else if(varname.equals("animationFrame")){
			int result;
			if(!(image instanceof AnimatedImage)) result = 0;
			result = ((AnimatedImage)image).index;
			debug("Getanimationframe = "+result);
			return result;
		}else if(varname.equals("isAnimated")){
			return image instanceof AnimatedImage;
		}else return super.getMember(varname);
	}
	public void incrementFrame(){
		frame = image.getImage();
	}
	public void draw(Graphics2D g, int x, int y){
		g.drawImage(frame,x,y,null);
	}
	public void drawBoundingBox(Graphics2D g, int x, int y){
		if(x >= 0 && y >= 0 && hasTileEntity(x/SIZE,y/SIZE)){
			TileEntity te = Panel.getInstance().getPlayer().getMap().getTileEntityAt(x/SIZE, y/SIZE);
			if(te != null){
				if(te.disableTileCollisions()) return;
			}
		}
		g.translate(x, y);
		g.setColor(new Color(255,128,0,128));
		g.fill(getBoundingBox());
		g.translate(-x, -y);
	}
	public String toString(){
		return "[object Tile]";
	}
	public ImageIcon getIcon(){
		return image;
	}
	public boolean doesPointCollide(int x, int y){
		if(hasTileEntity(x/SIZE,y/SIZE)){
			TileEntity te = Panel.getInstance().getPlayer().getMap().getTileEntityAt(x/SIZE, y/SIZE);
			if(te != null){
				if(te.getBoundingBox().contains(x % SIZE,y % SIZE)){
					te.manageCollision(Panel.getInstance().getPlayer());
					return true;
				}
				if(te.disableTileCollisions()) return false;
			}
		}
		x %= SIZE;
		y %= SIZE;
		
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
	@Override
	public Object getDefaultValue(Class<?> hint){
		if(hint == String.class){
			return this.toString();
		}else return super.getDefaultValue(hint);
	}
	public static Tile getTile(int id){
		if(id < 0 || id >= 0xFF) throw new RuntimeException("Invalid id: "+id);
		if(tiles[id] == null) throw new RuntimeException("No tile with id "+Integer.toHexString(id));
		return tiles[id];
	}
	public static final JSObject TILES_JS = new AbstractJSObject(){
		@Override
		public boolean hasSlot(int id){
			return id >= 0 && id < 0xFF && tiles[id] != null;
		}
		@Override
		public Object getSlot(int id){
			debug("Getslot "+id);
			return getTile(id);
		}
		@Override
		public String getClassName(){
			return "Tile[]";
		}
		@Override
		public String toString(){
			return "Tile[]";
		}
	};
	public Shape getBoundingBox(){
		return boundingBox;
	}
	@Override
	public void onInteracted(Player p, int x, int y){
		if(hasTileEntity(x/SIZE,y/SIZE)){
			TileEntity te = Panel.getInstance().getPlayer().getMap().getTileEntityAt(x/SIZE,y/SIZE);
			te.onInteracted(p, x, y);
		}
	}
	
}
