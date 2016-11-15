package com.ombda.entities;

import static com.ombda.Frame.keys;
import static com.ombda.Panel.borderX_left;
import static com.ombda.Panel.borderX_right;
import static com.ombda.Panel.borderY_bottom;
import static com.ombda.Panel.borderY_top;
import static com.ombda.Panel.dist;
import static java.awt.event.KeyEvent.*;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;

import com.ombda.Collideable;
import com.ombda.Facing;
import com.ombda.Images;
import com.ombda.Panel;
import com.ombda.Tile;

public class Player extends NPC{
	
	public boolean noclip = false;
	//private ImageIcon[] walking = new ImageIcon[8], still = new ImageIcon[8];
	public Player(int x, int y,Facing dir){
		super(x,y,0,-1,
			new ImageIcon[]{
				Images.retrieve("base_still_N",false),
				Images.retrieve("base_still_NE",false),
				Images.retrieve("base_still_E",false),
				Images.retrieve("base_still_SE",false),
				Images.retrieve("base_still_S",false),
				Images.retrieve("base_still_SW",false),
				Images.retrieve("base_still_W",false),
				Images.retrieve("base_still_NW",false),
				Images.retrieve("base_walk_N",false),
				Images.retrieve("base_walk_NE",false),
				Images.retrieve("base_walk_E",false),
				Images.retrieve("base_walk_SE",false),
				Images.retrieve("base_walk_S",false),
				Images.retrieve("base_walk_SW",false),
				Images.retrieve("base_walk_W",false),
				Images.retrieve("base_walk_NW",false)
			},
			new Rectangle2D.Double(0,0,28,16),null);
		this.direction = dir;
		image = images[direction.ordinal()];
		yminus = (int)(images[0].getIconHeight() - boundingBox.getHeight());
	}
	
	
	private void testCollision(){
		
		Panel panel = Panel.getInstance();
		//map border collisions
		/*if(x < 0){
			//lastX = x;
			x = 0;
		}else if(x > map.width()*Tile.SIZE-boundingBox.getWidth()){
			//lastX = x;
			x = map.width()*Tile.SIZE-boundingBox.getWidth();//lastX;
		}
		if(y < 0){
			//lastY = y;
			y = 0;
		}else if(y > map.height()*Tile.SIZE-boundingBox.getHeight()){
			//lastY = y;
			y = map.height()*Tile.SIZE-boundingBox.getHeight();//lastY;
		}*/
		
		if(!noclip){
			image = images[direction.ordinal() + ((lastX != x || lastY != y)? 8 : 0)];
			
			List<Tile> tiles = new ArrayList<>();
			Tile t = map.getTileAt((int)x, (int)y, 0);
			if(t != null) tiles.add(t);
			t = map.getTileAt((int)(x+boundingBox.getWidth()),(int)y,0);
			if(t != null && !tiles.contains(t)) tiles.add(t);
			t = map.getTileAt((int)x, (int)(y+boundingBox.getHeight()),0);
			if(t != null && !tiles.contains(t)) tiles.add(t);
			t = map.getTileAt((int)(x+boundingBox.getWidth()),(int)(y+boundingBox.getHeight()),0);
			if(t != null && !tiles.contains(t)) tiles.add(t);
			for(Tile tile : tiles){
				tile.manageCollision(this);
			}
		}
		//scrolls the map
		if(x+panel.offsetX > borderX_right){ //move panel.offset right
			//if(panel.offsetX > map.width()*Tile.SIZE-PRF_WIDTH)
			panel.offsetX -= x+panel.offsetX-borderX_right;
		}else if(x+panel.offsetX < borderX_left){ //move panel.offset left
			//if(panel.offsetX < 0)
			panel.offsetX += borderX_left - (x+panel.offsetX);
		}
		if(y+panel.offsetY > borderY_bottom){
			panel.offsetY -= y+panel.offsetY-borderY_bottom;
		}else if(y+panel.offsetY < borderY_top){
			panel.offsetY += borderY_top - (y+panel.offsetY);
		}
		
		
	}
	public boolean doesPointCollide(int x, int y){
		x -= this.x;
		y -= this.y;
		return boundingBox.contains(x, y);
	}
	
	private double speed(){
		return keys[VK_SHIFT]? 2.0 : 1.0;
	}
	public void update(){
		doKeys();
		testCollision();
	}
	private void doKeys(){
		lastX = x; lastY = y;
		if(keys[VK_A]){
			if(keys[VK_W]){
				if(keys[VK_D]){
					direction = Facing.N;
					y-=speed();
					testCollisionNorth();
				}else{
					direction = Facing.NW;
					x-=dist*speed();
					testCollisionWest();
					y-=dist*speed();
					testCollisionNorth();
				}
			}else if(keys[VK_S]){
				if(keys[VK_D]){
					direction = Facing.S;
					y+=speed();
					testCollisionSouth();
				}else{
					direction = Facing.SW;
					x-=dist*speed();
					testCollisionWest();
					y+=dist*speed();
					testCollisionSouth();
				}
			}else if(!keys[VK_D]){
				direction = Facing.W;
				x-=speed();
				testCollisionWest();
			}
		}
		else if(keys[VK_D]){
			if(keys[VK_W]){
				if(keys[VK_A]){
					direction = Facing.N;
					y-=speed();
					testCollisionNorth();
				}else{
					direction = Facing.NE;
					x+=dist*speed();
					testCollisionEast();
					y-=dist*speed();
					testCollisionNorth();
				}
			}else if(keys[VK_S]){
				if(keys[VK_A]){
					direction = Facing.S;
					y+=speed();
					testCollisionSouth();
				}else{
					direction = Facing.SE;
					x+=dist*speed();
					testCollisionEast();
					y+=dist*speed();
					testCollisionSouth();
				}
			}else if(!keys[VK_A]){
				direction = Facing.E;
				x+=speed();
				testCollisionEast();
			}
		}
		else if(keys[VK_W]){
			if(keys[VK_A]){
				if(keys[VK_S]){
					direction = Facing.W;
					x-=speed();
					testCollisionWest();
				}else{
					direction = Facing.NW;
					x-=dist*speed();
					testCollisionWest();
					y-=dist*speed();
					testCollisionNorth();
				}
			}else if(keys[VK_D]){
				if(keys[VK_S]){
					direction = Facing.E;
					x+=speed();
					testCollisionEast();
				}else{
					direction = Facing.NE;
					y-=dist*speed();
					testCollisionNorth();
					x+=dist*speed();
					testCollisionEast();
				}
			}else if(!keys[VK_S]){
				direction = Facing.N;
				y-=speed();
				testCollisionNorth();
			}
		}
		else if(keys[VK_S]){
			if(keys[VK_A]){
				if(keys[VK_W]){
					direction = Facing.W;
					x-=speed();
					testCollisionWest();
				}else{
					direction = Facing.SW;
					y+=dist*speed();
					testCollisionSouth();
					x-=dist*speed();
					testCollisionWest();
				}
			}else if(keys[VK_D]){
				if(keys[VK_W]){
					direction = Facing.E;
					x+=speed();
					testCollisionEast();
				}else{
					direction = Facing.SE;
					y+=dist*speed();
					testCollisionSouth();
					x+=dist*speed();
					testCollisionEast();
				}
			}else if(!keys[VK_W]){
				direction = Facing.S;
				y+=speed();
				testCollisionSouth();
			}
		}
		
	}
	
	private void testCollisionNorth(){
		if(y < 0){
			y = 0;
		}
		if(noclip) return;
		Collection<Sprite> sprites =  map.getSprites();
		for(int x = (int)this.x; x < (int)this.x+this.boundingBox.getWidth(); x++){
			Tile t;
			try{
			while((t = this.map.getTileAt(x, (int)this.y, 0)).doesPointCollide(x, (int)this.y)){
				t.manageCollision(this);
				y++;
			}
			}catch(NullPointerException e){}
			for(Sprite s : sprites){
				if(s instanceof Collideable && s != this){
					Collideable c = (Collideable)s;
					while(c.doesPointCollide(x, (int)this.y)){
						c.manageCollision(this);
						y++;
					}
				}
			}
		}
		
	}
	private void testCollisionEast(){
		while((int)(this.x+this.boundingBox.getWidth()) >= Tile.SIZE*map.width()){
			x--;// = Tile.SIZE*map.width() - this.boundingBox.getWidth()-1;
		}
		if(noclip) return;
		Collection<Sprite> sprites =  map.getSprites();
		for(int y = (int)this.y; y < (int)this.y+this.boundingBox.getHeight(); y++){
			Tile t;
			try{
			while((t = this.map.getTileAt((int)(this.x+this.boundingBox.getWidth()), y, 0)).doesPointCollide((int)(this.x+this.boundingBox.getWidth()),y)){
				t.manageCollision(this);
				x--;
			}
			}catch(NullPointerException e){}
			for(Sprite s : sprites){
				if(s instanceof Collideable && s != this){
					Collideable c = (Collideable)s;
					while(c.doesPointCollide((int)(this.x+this.boundingBox.getWidth()), y)){
						c.manageCollision(this);
						x--;
					}
				}
			}
		}
	}
	private void testCollisionSouth(){
		while((int)(this.y+this.boundingBox.getHeight()) >= Tile.SIZE*map.height())
			y--;
		if(noclip) return;
		Collection<Sprite> sprites =  map.getSprites();
		for(int x = (int)this.x; x < (int)this.x+this.boundingBox.getWidth(); x++){
			Tile t;
			try{
			while((t = this.map.getTileAt(x, (int)(this.y+this.boundingBox.getHeight()), 0)).doesPointCollide(x, (int)this.y+this.boundingBox.getHeight())){
				t.manageCollision(this);
				y--;
			}
			}catch(NullPointerException e){}
			for(Sprite s : sprites){
				if(s instanceof Collideable && s != this){
					Collideable c = (Collideable)s;
					while(c.doesPointCollide(x, (int)(this.y+this.boundingBox.getHeight()))){
						c.manageCollision(this);
						y--;
					}
				}
			}
		}
	}
	private void testCollisionWest(){
		if(x < 0)
			x = 0;
		if(noclip) return;
		Collection<Sprite> sprites =  map.getSprites();
		for(int y = (int)this.y; y < (int)this.y+this.boundingBox.getHeight(); y++){
			Tile t;
			try{
			while((t = this.map.getTileAt((int)this.x, y, 0)).doesPointCollide((int)this.x,y)){
				t.manageCollision(this);
				x++;
			}
			}catch(NullPointerException e){}
			for(Sprite s : sprites){
				if(s instanceof Collideable && s != this){
					Collideable c = (Collideable)s;
					while(c.doesPointCollide((int)this.x, y)){
						c.manageCollision(this);
						x++;
					}
				}
			}
		}
	}

	public String toString(){
		return "[object Player]";
	}
}
