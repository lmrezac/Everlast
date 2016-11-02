package com.ombda.gui;

import static com.ombda.Debug.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.ombda.Frame;
import com.ombda.MessageListener;
import com.ombda.Panel;

public class MessageBox extends GUI{
	private List<MessageListener> listeners = new ArrayList<>();
	public void addInputListener(MessageListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	public void removeInputListener(MessageListener l){
		listeners.remove(l);
	}
	@Override
	public void mouseClicked(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mousePressed(MouseEvent e){}

	@Override
	public void mouseReleased(MouseEvent e){}

	@Override
	public void mouseDragged(MouseEvent e){}

	@Override
	public void mouseMoved(MouseEvent e){}

	@Override
	public void keyPressed(KeyEvent e){
		if(waitingForInput){
			if((e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_ENTER) && zReleased){
				startIndexAt = index;
				waitingForInput = false;
				Frame.keys[e.getKeyCode()] = false;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_Z){
			//if(indexWaitMax == 10)
			zReleased = false;
			if(indexWaitMax == 5)
				indexWaitMax = 2;
			else if(indexWaitMax == 2)
				indexWaitMax = 1;
		}else if(e.getKeyCode() == KeyEvent.VK_X){
			instant();
		}
	}

	@Override
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_Z){
			if(indexWaitMax == 1)
				indexWaitMax = 2;
			else if(indexWaitMax == 2)
				indexWaitMax = 5;
			zReleased = true;
		}
	}

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
	
	private char[] string;
	protected String str;
	public void instant(){
		int i = str.indexOf(WAIT,index)+1;
		waitingForInput = i != 0;
		if(i == 0) i = str.length();
		index = i;
		
	}
	public void setMessage(String str){
		string = str.toCharArray();
		this.str = str;
		index = 1;
		startIndexAt = 0;
		waitingForInput = false;
		indexWait = 0;
		finished = false;
		//debug(str);
	}
	public void update(){
	}
	protected static final int INIT_DRAW_X = 16, INIT_DRAW_Y = 416;
	protected int index = 1, drawX = INIT_DRAW_X, drawY = INIT_DRAW_Y;
	protected int indexWait = 0, arrowWait = 0;
	private static int indexWaitMax = 5;
	public boolean waitingForInput = false;
	private boolean zReleased = true;
	private int startIndexAt = 0;
	public static final char DOWN_ARROW = '\u0010', LEFT_ARROW = '\u0012', UP_ARROW = '\u0014', RIGHT_ARROW = '\u0016';
	public static final char WAIT = '\u0005', CONTINUE = '\u0006';
	protected boolean drawOtherArrow = false;
	private boolean finished = false;
	public boolean isFinished(){ return finished/* index >= string.length && !waitingForInput*/; }
	@Override
	public void draw(Graphics2D g2d){
		drawBox(g2d,0,400,32,7);
		Color tint = null;
		drawX = INIT_DRAW_X; drawY = INIT_DRAW_Y;
		boolean bold = false, underline = false;
		for(int i = startIndexAt; i < index; i++){
			char c = string[i];
			if(c == '\n'){
				drawY += 20;
				drawX = INIT_DRAW_X;
			}else if(c == '\t'){
				g2d.drawImage(letters[' '],drawX,drawY,null);
				drawX += letters[' '].getWidth(null);
				g2d.drawImage(letters[' '],drawX,drawY,null);
				drawX += letters[' '].getWidth(null);
			}else if(c == WAIT){
				if(arrowWait >= 10){
					arrowWait = 0;
					drawOtherArrow = !drawOtherArrow;
				}else{
					arrowWait++;
				}
				g2d.drawImage(letters[drawOtherArrow? DOWN_ARROW+1 : DOWN_ARROW], drawX, drawY, null);
				waitingForInput = true;
				break;
			}else if(c == SECTION){ /* section symbol */
				if(++i >= string.length){
					g2d.drawImage(letters[SECTION], drawX, drawY, null);
					drawX += letters[SECTION].getWidth(null);
					
				//	throw new RuntimeException("Letter modifier format at index "+i+" in string \""+str+"\".");
				}else{
					c = string[i];
					if(c == 'c'){
						if(i >= string.length-8){
							g2d.drawImage(letters[SECTION], drawX, drawY, null);
							drawX += letters[SECTION].getWidth(null);
							g2d.drawImage(letters[SECTION], drawX, drawY, null);
							
							drawX += letters[SECTION].getWidth(null);
							//throw new RuntimeException("Letter modifier format at index "+i+" in string \""+str+"\".");
						}else{
							String color = str.substring(i+1,i+9);
							int a = Integer.decode("#"+color.substring(0, 2));
							int r = Integer.decode("#"+color.substring(2,4));
							int g = Integer.decode("#"+color.substring(4,6));
							int b = Integer.decode("#"+color.substring(6));
							if(a == 0)
								tint = null;
							else
								tint = new Color(r,g,b,a);
							//debug("set color to "+tint.toString());
							i+=8;
						}
					}else if(c == '0'){
						tint = null;
						bold = false;
						underline = false;
					}else if(c == 'r'){
						tint = Color.RED;
					}else if(c == 'g'){
						tint = Color.GREEN;
					}else if(c == 'b'){
						tint = Color.BLUE;
					}else if(c == 'o'){
						tint = Color.ORANGE;
					}else if(c == 'y'){
						tint = Color.YELLOW;
					}else if(c == 'p'){
						tint = PURPLE;
					}else if(c == 'u'){
						underline = !underline;
					}else if(c == 'B'){
						bold = !bold;
					}else{
						i--;
						c = string[i];
						//g2d.drawImage(letters[SECTION], drawX, drawY, null);
						//drawX += letters[SECTION].getWidth();
						g2d.drawImage(letters[SECTION], drawX, drawY, null);
						drawX += letters[SECTION].getWidth(null);
					}
					
				}
			}else{
				Color underlineColor = Color.WHITE;
				if(tint != null)
					underlineColor = new Color((tint.getRed()+255)/2,(tint.getGreen()+255)/2,(tint.getBlue()+255)/2);
				if(bold){
					if(underline){
						g2d.setColor(underlineColor);
						BufferedImage letter;
						if(c >= letters.length)
							letter = letters['?'];
						else letter = letters[c];
						g2d.drawLine(drawX, drawY+9, drawX+letter.getWidth(null)+1, drawY+9);
					}
					BufferedImage letter;
					if(c >= letters.length)
						letter = letters['?'];
					else letter = letters[c];
					g2d.drawImage(tint(letter,tint),drawX,drawY,null);
					g2d.drawImage(tint(letter,tint),drawX+1,drawY,null);
					
					drawX++;
				}else{
					if(underline){
						g2d.setColor(underlineColor);
						BufferedImage letter;
						if(c >= letters.length)
							letter = letters['?'];
						else letter = letters[c];
						g2d.drawLine(drawX, drawY+9, drawX+letter.getWidth(null), drawY+9);
					}
					BufferedImage letter;
					if(c >= letters.length)
						letter = letters['?'];
					else letter = letters[c];
					g2d.drawImage(tint(letter,tint),drawX,drawY,null);
				}
				BufferedImage letter;
				if(c >= letters.length)
					letter = letters['?'];
				else letter = letters[c];
				drawX += letter.getWidth(null);
			}
		}
		
		if(!waitingForInput){
			if(index < string.length){
				if(indexWait >= indexWaitMax){
					index++;
					indexWait = 0;
				}else indexWait++;
			}
			else{
				/*if(!(Panel.getInstance().previous instanceof MessageBox))
					Panel.getInstance().setGUI(Panel.getInstance().previous);
				else*/ 
				finished = true;
				for(int i = listeners.size()-1; i >= 0; i--){
					listeners.get(i).onMessageFinish();
				}
				if(closeWhenDone)
					Panel.getInstance().setGUI(Panel.getInstance().hud);
			}
		}
		if(debug && !Panel.noScreenDebug){
			Panel.drawDebugString(g2d,"wait="+indexWaitMax+" index="+index+" indexWait="+indexWait+" size = "+string.length+(waitingForInput? " waiting" : ""),0,106);
		}
	}
	public boolean closeWhenDone = true;
	@Override
	public boolean blockInput(){
		return true;
	}
	
	
	public String toString(){ return "msgbox"; }
}
