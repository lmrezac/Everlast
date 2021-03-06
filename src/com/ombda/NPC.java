package com.ombda;

import static com.ombda.Debug.debug;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.ombda.scripts.Script;


public class NPC extends Sprite implements Updateable, Collideable, Interactable{
	private static HashMap<Integer,NPC> npcs = new HashMap<>();
	public Script onInteractedScript = null;
	private int destX, destY;
	protected double lastX, lastY;
	protected Rectangle2D boundingBox;
	private int id;
	protected ImageIcon[] images;
	protected Facing direction;
	protected int yminus;
	public NPC(int x, int y,int hash, int yminus,ImageIcon[] animations,Rectangle2D box){
		//animations == [N still, NE still, E still, SE still, S still, SW still, W still, NW still, N walk, NE walk, E walk, SE walk, S walk, SW walk, W walk, NW walk]
		super(hash,animations[Facing.N.ordinal()],x,y);
		assert animations.length == 16 : "Not right number of images passed to NPC()";
		boundingBox = box;
		lastX = x;
		lastY = y;
		direction = Facing.N;
		this.id = hash;
		if(this.getClass() != Player.class)
			npcs.put(hash,this);
		setDestination(x,y);
		this.images = animations;
		this.yminus = yminus;
	}
	public static NPC getNPC(int hash){
		NPC npc = npcs.get(hash);
		if(npc == null){
			throw new RuntimeException("Could'nt find NPC of id "+hash);
		}
		return npc;
	}
	
	@Override
	public double spriteHeight(){
		return boundingBox.getHeight();
	}
	public Facing getDirection(){
		return direction;
	}
	public void update(){
		double newx = x, newy = y;
		if(x != destX){
			if(x < destX)
				newx = x+1;
			else if(x > destX)
				newx = x-1;
		}
		if(y != destY){
			if(y < destY)
				newy = y+1;
			else if(y > destY)
				newy = y-1;
		}
		setPos(newx,newy);
		
		if(lastX < x ){
			if(lastY < y)
				direction = Facing.SE;
			else if(lastY > y)
				direction = Facing.NE;
			else direction = Facing.E;
		}else if(lastX > x){
			if(lastY < y)
				direction = Facing.SW;
			else if(lastY > y)
				direction = Facing.NW;
			else direction = Facing.W;
		}else if(lastY < y)
			direction = Facing.S;
		else if(lastY > y)
			direction = Facing.N;
		
		this.image = images[direction.ordinal() + ((lastX != x || lastY != y)? 8 : 0)];
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
	public static void deleteNPC(int hash){
		npcs.put(hash, null);
	}
	public void setDirection(Facing f){
		direction = f;
	}
	@Override
	public boolean doesPointCollide(int x, int y){
		x -= this.x;
		y -= this.y;
		return boundingBox.contains(x, y);
	}
	public void manageCollision(Collideable c){
		
	}

	public String toString(){
		return "ncp 0x"+Integer.toHexString(this.id);
	}
	
	@Override
	public void draw(Graphics2D g, int offsetX, int offsetY){
		g.drawImage(image.getImage(),(int)x+offsetX,(int)y-yminus+offsetY,null);
	}
	@Override
	public void onInteracted(Player p, int x, int y){
		if(onInteractedScript != null){
			//debug("int y = "+y+" x = "+x+" my y = "+this.y+" "+this.yminus+" "+boundingBox.getHeight()+" "+(int)((this.y-yminus)+boundingBox.getHeight()-1)+" my x = "+this.x+" "+(this.x+this.boundingBox.getWidth()));
			if(y >= (int)(this.y-yminus+boundingBox.getHeight()-1) && y <= (int)(this.y-yminus+boundingBox.getHeight()+1) && this.x < x && x < this.x+this.boundingBox.getWidth()){
				debug("running script");
				onInteractedScript.reset();
				Panel.getInstance().runScript(onInteractedScript);
			}
		}
	}
}
