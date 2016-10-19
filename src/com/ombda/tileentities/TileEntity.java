package com.ombda.tileentities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.ombda.Collideable;
import com.ombda.Interactable;
import com.ombda.Tile;
import com.ombda.Updateable;
import com.ombda.entities.Entity;

public abstract class TileEntity extends Entity implements Collideable, Updateable, Interactable{
	private Shape boundingBox;
	private Image image;
	protected boolean disableCollisions;
	private double Xadd, Yadd;
	public TileEntity(int x, int y,boolean disableCollisions){
		super(Tile.SIZE*x,Tile.SIZE*y);
		this.disableCollisions = disableCollisions;
		setBoundingBox(new Rectangle2D.Double(0, 0, Tile.SIZE, Tile.SIZE));
	}
	public boolean disableTileCollisions(){ return disableCollisions;}

	@Override
	public boolean doesPointCollide(int x, int y){
		x %= Tile.SIZE;
		y %= Tile.SIZE;
		return boundingBox.contains(x,y);
	}
	
	protected final void setBoundingBox(Shape s){
		this.boundingBox = s;
		Rectangle2D b = s.getBounds2D();
		Xadd = b.getX();
		Yadd = b.getY();
		this.image = new BufferedImage((int)(Xadd >= 0? Xadd : 0)+(int)b.getWidth()+1,(int)(Yadd >= 0? Yadd : 0)+(int)b.getHeight()+1,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = ((BufferedImage)image).createGraphics();
		g2d.setColor(Color.red);
		if(Xadd < 0){
			g2d.translate(-Xadd,0);
			if(Yadd < 0){
				g2d.translate(0,-Yadd);
				g2d.draw(s);
				g2d.translate(0, Yadd);
			}else{
				g2d.draw(s);
			}
			g2d.translate(Xadd,0);
		}else if(Yadd < 0){
			g2d.translate(0,-Yadd);
			g2d.draw(s);
			g2d.translate(0,Yadd);
		}else g2d.draw(s);
		g2d.dispose();
		
	}
	
	@Override
	public Shape getBoundingBox(){
		return boundingBox;
	}
	
	public void draw(Graphics2D g, int offsetX, int offsetY){
		g.drawImage(image,(int)x+offsetX+(int)Xadd,(int)y+offsetY+(int)Yadd,null);
	
	}

	public abstract String save();
}
