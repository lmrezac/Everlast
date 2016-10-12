package com.ombda.gui;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ombda.Frame;
import com.ombda.InputListener;
import com.ombda.Panel;

public class Input extends MessageBox{
	private List<InputListener> listeners = new ArrayList<>();
	public Input(){
		setMessage("");
	}
	public void addInputListener(InputListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	public void removeInputListener(InputListener l){
		listeners.remove(l);
	}
	public String getMessage(){
		return str;
	}
	
	public void reset(){
		setMessage("");
		listeners.clear();
		done = false;
	}
	
	@Override
	public void keyPressed(KeyEvent e){
		if(done) return;
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			if(str.length() > 0)
			setMessage(str.substring(0,str.length()-1));
		}else if(e.getKeyCode() == KeyEvent.VK_V && Frame.keys[KeyEvent.VK_CONTROL]){
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			try{
				String result = (String) clipboard.getData(DataFlavor.stringFlavor);
				if(result != null){
					setMessage(str+result);
				}
			}catch(UnsupportedFlavorException|IOException e1){
				debug(e1.getMessage());
				if(printStackTrace)
					e1.printStackTrace();
			
			}
		}else if(e.getKeyCode() == KeyEvent.VK_S && Frame.keys[KeyEvent.VK_CONTROL]){
			setMessage(str+SECTION);
		}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
			done = true;
			Panel panel = Panel.getInstance();
			panel.setGUI(panel.previous);
			for(int i = listeners.size()-1; i >= 0; i--){
				listeners.get(i).onInput(getMessage());
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e){
		
	}
	
	@Override
	public void keyTyped(KeyEvent e){
		if(done) return;
		char c = e.getKeyChar();
		if(e.getKeyCode() == KeyEvent.VK_TAB){
			c = '\t';
		}
		if(c >= ' ' || c == '\t')
		setMessage(str+c);
	}
	
	@Override
	public void draw(Graphics2D g2d){
		drawBox(g2d,0,400,32,7);
		if(arrowWait >= 10){
			arrowWait = 0;
			drawOtherArrow = !drawOtherArrow;
		}else{
			arrowWait++;
		}
		if(drawOtherArrow)
			drawString(g2d,str+"|",INIT_DRAW_X,INIT_DRAW_Y);
		else
			drawString(g2d,str,INIT_DRAW_X,INIT_DRAW_Y);
		
		if(debug && !Panel.noScreenDebug){
			Panel.drawDebugString(g2d,"str=\""+str+"\"",0,106);
		}
	}
	private boolean done = false;
	
	@Override
	public boolean pauseGame(){ return !done; }
	
	public String toString(){ return "input"; }
}
