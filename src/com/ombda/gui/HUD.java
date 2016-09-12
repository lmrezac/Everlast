package com.ombda.gui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class HUD extends GUI{

	@Override
	public void draw(Graphics2D g2d){
		drawString(g2d,"This is the HUD",1,com.ombda.Frame.PRF_HEIGHT-11);
		drawString(g2d,"§cFF111111This is the HUD",2,com.ombda.Frame.PRF_HEIGHT-9);
		drawString(g2d,"§cFFBBBBBBThis is the HUD",1,com.ombda.Frame.PRF_HEIGHT-10);
		
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
	public void keyPressed(KeyEvent e){}

	@Override
	public void keyReleased(KeyEvent e){}

	@Override
	public void keyTyped(KeyEvent e){}
	
	@Override
	public boolean pauseGame(){ return false; }
	
	@Override
	public boolean drawMap(){ return true; }

	@Override
	public boolean blockInput(){ return false; }

	public String toString(){ return "hud"; }
}
