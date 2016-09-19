package com.ombda.gui;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import com.ombda.Frame;
import com.ombda.InputListener;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Tile;
import com.ombda.Tiles;
public class MapMaker extends GUI implements InputListener{
	private short tileId = 0;
	public MapMaker(){
		/*BufferedImage saveImage = new BufferedImage(34,12,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = saveImage.createGraphics();
		g2d.setColor(new Color(185,229,229));
		g2d.fill3DRect(0, 0, 34, 12, true);
		drawString(g2d," Save",1,1);
		g2d.dispose();
		BufferedImage saveImagePressed = new BufferedImage(34,12,BufferedImage.TYPE_INT_ARGB);
		g2d = saveImagePressed.createGraphics();
		g2d.setColor(new Color(0,229,146));
		g2d.fill3DRect(0, 0, 34, 12, true);
		drawString(g2d," §ySave",1,1);
		g2d.dispose();*/
		buttons.add(new Button("Save",0,0){
			@Override
			public void buttonPressed(){
				debug("Save button pressed!");
				getMap().save();
			}
		});
		buttons.add(new Button("Load",34,0){
			@Override
			public void buttonPressed(){
				debug("tile image pressed");
				Panel.getInstance().input.reset();
				Panel.getInstance().input.addInputListener(MapMaker.this);
				Panel.getInstance().setGUI(Panel.getInstance().input);
			}
		});
	}
	private Map getMap(){
		return Panel.getInstance().getPlayer().getMap();
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
		if(e.getButton() == MouseEvent.BUTTON1){
			int x = e.getX();
			int y = e.getY();
			int[] coords = Panel.screenCoordsToImageCoords(x,y);
			x = coords[0]-Panel.getInstance().offsetX;
			y = coords[1]-Panel.getInstance().offsetY-16;
			int layer;
			if(Frame.keys[KeyEvent.VK_SHIFT])
				layer = 1;
			else layer = 0;
			getMap().setTileAt(x, y, layer, Tile.getTile(tileId));
		}else if(e.getButton() == MouseEvent.BUTTON3){
			int x = e.getX();
			int y = e.getY();
			int[] coords = Panel.screenCoordsToImageCoords(x,y);
			x = coords[0]-Panel.getInstance().offsetX;
			y = coords[1]-Panel.getInstance().offsetY-16;
			int layer;
			if(Frame.keys[KeyEvent.VK_SHIFT])
				layer = 1;
			else layer = 0;
			Tile t = getMap().getTileAt(x, y, layer);
			debug("set tileid to "+t.id);
			tileId = t.id;
		}
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
	public String toString(){ return "mapcreator"; }
	@Override
	public void onInput(String id){
		debug("set id to "+id);
		Panel panel = Panel.getInstance();
		panel.input.removeInputListener(this);
		if(id.matches("\\d+")){
			tileId = Short.parseShort(id);
		}else if(id.matches("0x[\\dA-Za-z]+")){
			tileId = Short.decode(id);
		}else if(id.matches("[\\w_]+")){
			Class<Tiles> c = Tiles.class;
			try{
				Field f = c.getDeclaredField(id.toUpperCase());
				try{
					Object obj = f.get(null);
					if(!(obj instanceof Short)){
						panel.msgbox.setMessage("Invalid tile ID given"+MessageBox.WAIT);
						panel.msgbox.instant();
						panel.setGUI(panel.msgbox);
						return;
					}else{
						tileId = (short)obj;
					}
				}catch(IllegalArgumentException|IllegalAccessException e1){
					debug(e1.getMessage());
					if(printStackTrace)
						e1.printStackTrace();
					System.exit(0);
					return;
				}
				
			}catch(NoSuchFieldException ex){
				panel.msgbox.setMessage("Invalid tile ID given"+MessageBox.WAIT);
				panel.msgbox.instant();
				panel.setGUI(panel.msgbox);
				return;
			}catch(SecurityException f){
				debug(f.getMessage());
				if(printStackTrace){
					f.printStackTrace();
				}
				System.exit(0);
				return;
			}
		}else{
			panel.msgbox.setMessage("Invalid tile ID given"+MessageBox.WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
			return;
		}
	}
}
