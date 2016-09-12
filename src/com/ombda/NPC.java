package com.ombda;

import static com.ombda.Panel.borderX_left;
import static com.ombda.Panel.borderX_right;
import static com.ombda.Panel.borderY_bottom;
import static com.ombda.Panel.borderY_top;

import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;

import com.ombda.Player.Collision;

public class NPC extends Sprite implements Updateable, Collideable{
	private static HashMap<Integer,NPC> npcs = new HashMap<>();
	private int destX, destY;
	private double lastX, lastY;
	private Rectangle2D boundingBox;
	public NPC(int x, int y,int hash, Image[] animations){
		//animations == [N still, NE still, E still, SE still, S still, SW still, W still, NW still, N walk, NE walk, E walk, SE walk, S walk, SW walk, W walk, NW walk]
		super(hash,Images.getError(),x,y);
		assert animations.length == 16 : "Not enough images passed to NPC()";
		boundingBox = new Rectangle2D.Double(0,0,16,16);
		lastX = x;
		lastY = y;
		npcs.put(hash,this);
	}
	public static NPC getNPC(int hash){
		NPC npc = npcs.get(hash);
		if(npc == null){
			throw new RuntimeException("Could'nt find NPC of id "+hash);
		}
		return npc;
	}
	
	public void update(){
		if(x != destX){
			if(x < destX)
				setPos(x+1,y);
			else if(x > destX)
				setPos(x-1,y);
		}
		if(y != destY){
			if(y < destY)
				setPos(x,y+1);
			else if(y > destY)
				setPos(x,y-1);
		}
		testCollision();
		
	}
	public void setPos(double x, double y){
		lastX = this.x;
		lastY = this.y;
		super.setPos(x, y);
	}
	public void setDestination(int x, int y){
		destX = x;
		destY = y;
	}
	public Shape getBoundingBox(){
		return boundingBox;
	}
	@Override
	public boolean doesPointCollide(int x, int y){
		x -= this.x;
		y -= this.y;
		return boundingBox.contains(x, y);
	}
	public Collision[] collidedTiles(){
		// {   0    ,    1    ,     2     ,      3     }
		// {top-left,top-right,bottom-left,bottom-right}
		Collision[] result = new Collision[4];
		if((int)x % 16 == 0 && (int)y % 16 == 0){
			final Tile t = map.getTileAt((int)x, (int)y, 0); //top-left corner
			if(t.doesPointCollide(x, y))
				result[0] = new Collision(x,y,t);
		}else if((int)x % 16 == 0){ //lock on y axis
			final Tile t1 = map.getTileAt((int)x, (int)y, 0); //top-left corner
			if(t1.doesPointCollide(x, y)){
				result[0] = new Collision(x,y,t1);
			}
			final Tile t2 = map.getTileAt((int)x, (int)y+15, 0); //bottom-left corner
			if(t2.doesPointCollide(x, y)){
				result[2] = new Collision(x,y+15,t2);
			}
		}else if((int)y % 16 == 0){ //lock on x axis
			final Tile t1 = map.getTileAt((int)x, (int)y, 0); //top-left corner
			if(t1.doesPointCollide(x, y)){
				result[0] = new Collision(x,y,t1);
			}
			final Tile t2 = map.getTileAt((int)x+15, (int)y, 0); //top-right corner
			if(t2.doesPointCollide(x, y)){
				result[1] = new Collision(x+15,y,t2);
			}
		}else{
			final Tile t1 = map.getTileAt((int)x, (int)y, 0); //top-left corner
			if(t1.doesPointCollide(x, y)){
				result[0] = new Collision(x,y,t1);
			}
			final Tile t2 = map.getTileAt((int)x, (int)y+15, 0); //bottom-left corner
			if(t2.doesPointCollide(x, y)){
				result[2] = new Collision(x,y+15,t2);
			}
			final Tile t3 = map.getTileAt((int)x+15, (int)y, 0); //top-right corner
			if(t3.doesPointCollide(x, y)){
				result[1] = new Collision(x+15,y,t3);
			}
			final Tile t4 = map.getTileAt((int)x+15, (int)y+15, 0); //bottom-right corner
			if(t4.doesPointCollide(x, y)){
				result[3] = new Collision(x+15,y+15,t4);
			}
		}
		return result;
	}

	public void testCollision(){
		Panel panel = Panel.getInstance();
		//map border collisions
		if(x < 0){
			//lastX = x;
			x = 0;
		}else if(x > map.width()*16-16){
			//lastX = x;
			x = map.width()*16-16;//lastX;
		}
		if(y < 0){
			//lastY = y;
			y = 0;
		}else if(y > map.height()*16-16){
			//lastY = y;
			y = map.height()*16-16;//lastY;
		}
		
		//tile collisions
		Collision[] collisions = collidedTiles();
		Point trace, origin = new Point(lastX,lastY);
		//top_left = 0, top_right = 1, bottom_left = 2, bottom_right = 3;
		for(int i = 0; i <= 3; ++i){
			if(collisions[i] != null){
				trace = rayTrace(collisions[i].point,origin);
				lastX = x;
				lastY = y;
				x = trace.x;
				y = trace.y;
				//testCollision(panel);
				//return;
			}
		}
		Iterator<Sprite> sprites =  map.getSprites();
		while(sprites.hasNext()){
			Sprite s = sprites.next();
			if(s instanceof Collideable && s != this){
				Collideable c = (Collideable)s;
				if(c.getBoundingBox().intersects(boundingBox))
					manageCollision(c);
			}
		}
		
		
		//scrolls the map
		if(x+panel.offsetX > borderX_right){ //move panel.offset right
			//if(panel.offsetX > map.width()*16-PRF_WIDTH)
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
	@Override
	public void manageCollision(Collideable c){
		for(int tries = 0; tries < 20 && c.getBoundingBox().intersects(boundingBox); tries++){
			if(lastX < x)
				x--;
			else if(lastX > x)
				x++;
			if(lastY < y)
				y--;
			else if(lastY > y)
				y++;
		}
	}
}
