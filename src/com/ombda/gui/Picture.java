package com.ombda.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.ombda.Frame;
import com.ombda.Panel;

public class Picture extends GUI{
	private ImageIcon picture = null;
	
	public void setImage(ImageIcon i){
		picture = i;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0){}

	@Override
	public void mouseMoved(MouseEvent arg0){}

	@Override
	public void keyPressed(KeyEvent arg0){}

	@Override
	public void keyReleased(KeyEvent arg0){}

	@Override
	public void keyTyped(KeyEvent arg0){
		if(arg0.getKeyChar() == 'z'){
			this.reset();
			Panel.getInstance().setGUI(Panel.getInstance().hud);
		}
	}
	
	public void reset(){
		
	}
	
	@Override
	public void draw(Graphics2D g){
		int x = Frame.PRF_WIDTH/2 - this.picture.getIconWidth()/2;
		int y = Frame.PRF_HEIGHT/2 - this.picture.getIconHeight()/2;
		g.setColor(new Color(0,0,0,100));
		g.fillRect(0, 0, Panel.getInstance().getWidth(), Panel.getInstance().getHeight());
		g.drawImage(picture.getImage(),x,y,null);
	}

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
	public String toString(){
		return "img";
	}

}
