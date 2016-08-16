package com.ombda.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.ombda.Frame;
import com.ombda.Map;
import com.ombda.Panel;
import static com.ombda.Debug.*;
public class MapMaker extends GUI{
	private Map map;
	public MapMaker(){
		map = Panel.getInstance().getPlayer().getMap();
		BufferedImage saveImage = new BufferedImage(34,12,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = saveImage.createGraphics();
		g2d.setColor(new Color(185,229,229));
		g2d.fill3DRect(0, 0, 34, 12, true);
		drawString(g2d," Save",1,1);
		g2d.dispose();
		BufferedImage saveImagePressed = new BufferedImage(34,12,BufferedImage.TYPE_INT_ARGB);
		g2d = saveImagePressed.createGraphics();
		g2d.setColor(new Color(0,229,146));
		g2d.fill3DRect(0, 0, 34, 12, true);
		drawString(g2d," �ySave",1,1);
		g2d.dispose();
		buttons.add(new Button(saveImage,saveImagePressed,0,0){
			@Override
			public void buttonPressed(){
				debug("Save button pressed!");
				map.save();
			}
		});
	}
	
	@Override
	public void update(){
		Panel panel = Panel.getInstance();
		boolean[] keys = Frame.keys;
		if(keys[KeyEvent.VK_A])
			panel.offsetX++;
		if(keys[KeyEvent.VK_D])
			panel.offsetX--;
		if(keys[KeyEvent.VK_W])
			panel.offsetY++;
		if(keys[KeyEvent.VK_S])
			panel.offsetY--;
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		super.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e){
		super.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e){
		super.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e){
		debug("mouse pressed");
		super.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e){
		super.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e){}

	@Override
	public void mouseMoved(MouseEvent e){}

	@Override
	public void keyPressed(KeyEvent e){
	}

	@Override
	public void keyReleased(KeyEvent e){}

	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public boolean drawMap(){
		return true;
	}

	@Override
	public boolean pauseGame(){
		return true;
	}

	@Override
	public boolean blockInput(){
		return true;
	}

	@Override
	public void draw(Graphics2D g2d){
		super.draw(g2d);
	}

}