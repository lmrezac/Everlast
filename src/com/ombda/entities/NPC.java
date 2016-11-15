package com.ombda.entities;

import static com.ombda.Debug.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.ScriptFunction;

import com.ombda.Collideable;
import com.ombda.Facing;
import com.ombda.Interactable;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.ScriptThread;
import com.ombda.Updateable;


public class NPC extends Sprite implements Updateable, Collideable, Interactable{
	private static HashMap<Integer,NPC> npcs = new HashMap<>();
	private String onInteractedScript = null;
	private Thread onUpdateThread = null;
	public int destX, destY;
	protected double lastX, lastY;
	protected Rectangle2D boundingBox;
	private int id;
	protected ImageIcon[] images;
	protected Facing direction;
	public double speed = 1;
	protected int yminus;
	private boolean onUpdateSet = false;
	public JSObject onInteracted = null, onUpdate = null;
	/*public NPC(int x, int y, int hash, int yminus, JSObject obj){
		super(hash,evalImages(obj)[Facing.N.ordinal()],x,y);
		this.boundingBox = new Rectangle2D.Double(0,0,images[0].getIconWidth(),images[0].getIconHeight());
	}*/
	public NPC(int x, int y,int hash, int yminus,JSObject obj,int width, int height){
		this(x,y,hash,yminus,evalImages(obj),new Rectangle2D.Double(0,0,width,height),Panel.getInstance().getPlayer().getMap());
	}
	private static ImageIcon[] evalImages(JSObject obj){
		ImageIcon[] images = new ImageIcon[16];
		if(obj.isArray()){
			for(int i = 0; i < Math.min((Integer)obj.getMember("length"),16); i++){
				images[i] = (ImageIcon)obj.getSlot(i);
			}
		}else{
			String[] names = new String[8];
			for(int i = 0; i < 8; i++){
				names[i] = Facing.values()[i].toString();
			}
			if(obj.hasMember("still")){
				JSObject still = (JSObject)obj.getMember("still");
				for(String name : names){
					if(still.hasMember(name))
						images[Facing.valueOf(name).ordinal()] = (ImageIcon)still.getMember(name);
				}
			}
			if(obj.hasMember("walk")){
				JSObject walk = (JSObject)obj.getMember("walk");
				for(String name : names){
					if(walk.hasMember(name))
						images[Facing.valueOf(name).ordinal()+8] = (ImageIcon)walk.getMember(name);
				}
			}
		}
		return images;
	}
	public NPC(int x, int y,int hash, int yminus,ImageIcon[] animations,Rectangle2D box,Map map){
		//animations == [N still, NE still, E still, SE still, S still, SW still, W still, NW still, N walk, NE walk, E walk, SE walk, S walk, SW walk, W walk, NW walk]
		super(hash,animations[Facing.N.ordinal()],x,y,map);
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
	public void setUpdateScript(String str){
		onUpdateThread = new ScriptThread(str);
		onUpdateThread.start();
		synchronized(onUpdateThread){
			try{
				onUpdateThread.wait();
			}catch(InterruptedException e){
			
			}
		}
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
		if(!onUpdateSet && onUpdate != null){
			onUpdateSet = true;
			onUpdateThread = 
		}
		if(onUpdateThread != null){
		synchronized(onUpdateThread){
			onUpdateThread.notify();
		}
		}
		double newx = x, newy = y;
		if(x != destX){
			
			if(x < destX)
				newx = x+speed;
			else if(x > destX)
				newx = x-speed;
		}
		if(y != destY){
			if(y < destY)
				newy = y+speed;
			else if(y > destY)
				newy = y-speed;
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
		
		synchronized(onUpdateThread){
			try{
				onUpdateThread.wait();
			}catch(InterruptedException e){
			}
		}
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
		return "npc 0x"+Integer.toHexString(this.id);
	}
	
	@Override
	public void draw(Graphics2D g, int offsetX, int offsetY){
		if(hidden || image == null) return;
		g.drawImage(image.getImage(),(int)x+offsetX,(int)y-yminus+offsetY,null);
	}
	@Override
	public void drawBoundingBox(Graphics2D g, int offsetX, int offsetY){
		Rectangle2D b = boundingBox.getBounds2D();
		double Xadd = b.getX();
		double Yadd = b.getY();
		BufferedImage image = new BufferedImage((int)(Xadd >= 0? Xadd : 0)+(int)b.getWidth()+1,(int)(Yadd >= 0? Yadd : 0)+(int)b.getHeight()+1,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.red);
		/*if(Xadd < 0){
			g2d.translate(-Xadd,0);
			if(Yadd < 0){
				g2d.translate(0,-Yadd);
				g2d.draw(boundingBox);
				g2d.translate(0, Yadd);
			}else{
				g2d.draw(boundingBox);
			}
			g2d.translate(Xadd,0);
		}else if(Yadd < 0){
			g2d.translate(0,-Yadd);
			g2d.draw(boundingBox);
			g2d.translate(0,Yadd);
		}else g2d.draw(boundingBox);*/
		g2d.draw(boundingBox);
		g2d.dispose();
		g.drawImage(image,(int)x+offsetX,(int)y+offsetY,null);
	}
	@Override
	public void onInteracted(final Player p, final int x, final int y){
		debug("onInteracted npc ");
		if(onInteracted != null){
			//debug("int y = "+y+" x = "+x+" my y = "+this.y+" "+this.yminus+" "+boundingBox.getHeight()+" "+(int)((this.y-yminus)+boundingBox.getHeight()-1)+" my x = "+this.x+" "+(this.x+this.boundingBox.getWidth()));
			if(y >= (int)(this.y-yminus+boundingBox.getHeight()-1) && y <= (int)(this.y-yminus+boundingBox.getHeight()+1) && this.x < x && x < this.x+this.boundingBox.getWidth()){
				debug("running script");
				//new ScriptThread(onInteractedScript).start();
				new Thread(){
					public void run(){
						onInteracted.call(this, p,x,y);
					}
				}.start();
			}
		}
	}
}
