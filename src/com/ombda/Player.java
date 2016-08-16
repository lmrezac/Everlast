package com.ombda;

import static com.ombda.Frame.keys;
import static com.ombda.Panel.borderX_left;
import static com.ombda.Panel.borderX_right;
import static com.ombda.Panel.borderY_bottom;
import static com.ombda.Panel.borderY_top;
import static com.ombda.Panel.dist;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_W;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends Sprite{
	private Map map;
	private double lastX, lastY;
	public boolean noclip = false;
	public Player(int x, int y){
		super(Images.getError(),x,y);
		lastX = x; lastY = y;
	}
	public void setMap(Map map){
		this.map = map;
	}
	public Map getMap(){
		return map;
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
	private Point rayTrace(Point collision, Point origin){
		double deltaY = collision.y - origin.y;
		double deltaX = collision.x - origin.y;
		Tile t;
		double Xlast = origin.x, Ylast = origin.y;
		for(double X = origin.x, Y = origin.y; ((collision.y < origin.y)? Y > collision.y : Y < collision.y) && ((collision.x < origin.x)? X > collision.x : X < collision.x); X += deltaX, Y += deltaY){
			t = map.getTileAt((int)X,(int)Y, 0);
			if(t.doesPointCollide(X, Y)){
				return new Point((int)Xlast,(int)Ylast);
			}
			Xlast = X;
			Ylast = Y;
		}
		return new Point((int)Xlast,(int)Ylast);
	}
	public void testCollision(){
		if(noclip) return;
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
	
	private double speed(){
		return keys[VK_SHIFT]? 1.5 : 1.0;
	}
	public void doKeys(){
		lastX = x; lastY = y;
		if(keys[VK_A]){
			if(keys[VK_W]){
				if(keys[VK_D])
					y-=speed();
				else{
					x-=dist*speed();
					y-=dist*speed();
				}
			}else if(keys[VK_S]){
				if(keys[VK_D])
					y+=speed();
				else{
					x-=dist*speed();
					y+=dist*speed();
				}
			}else if(!keys[VK_D])
				x-=speed();
		}
		else if(keys[VK_D]){
			if(keys[VK_W]){
				if(keys[VK_A])
					y-=speed();
				else{
					x+=dist*speed();
					y-=dist*speed();
				}
			}else if(keys[VK_S]){
				if(keys[VK_A])
					y+=speed();
				else{
					x+=dist*speed();
					y+=dist*speed();
				}
			}else if(!keys[VK_A])
				x+=speed();
		}
		else if(keys[VK_W]){
			if(keys[VK_A]){
				if(keys[VK_S])
					x-=speed();
				else{
					y-=dist*speed();
					x-=dist*speed();
				}
			}else if(keys[VK_D]){
				if(keys[VK_S])
					x+=speed();
				else{
					y-=dist*speed();
					x+=dist*speed();
				}
			}else if(!keys[VK_S])
				y-=speed();
		}
		else if(keys[VK_S]){
			if(keys[VK_A]){
				if(keys[VK_W])
					x-=speed();
				else{
					y+=dist*speed();
					x-=dist*speed();
				}
			}else if(keys[VK_D]){
				if(keys[VK_W])
					x+=speed();
				else{
					y+=dist*speed();
					x+=dist*speed();
				}
			}else if(!keys[VK_W])
				y+=speed();
		}
	}
	
	public static class Collision{
		public final Point point;
		public final Tile tile;
		public Collision(Point p, Tile t){
			this.point = p;
			this.tile = t;
		}
		public Collision(double x, double y, Tile t){
			this(new Point((int)x,(int)y),t);
		}
	}
}
