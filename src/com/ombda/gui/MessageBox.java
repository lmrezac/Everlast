package com.ombda.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ombda.Images;
import com.ombda.Panel;

import static com.ombda.Debug.*;

public class MessageBox extends GUI{
	
	
	
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
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_Z){
			//if(indexWaitMax == 10)
			zReleased = false;
			if(indexWaitMax == 5)
				indexWaitMax = 2;
			else if(indexWaitMax == 2)
				indexWaitMax = 1;
		
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
		return false;
	}
	
	private char[] string;
	protected String str;
	public void instant(){
		indexWaitMax = 2;
	}
	public void setMessage(String str){
		string = str.toCharArray();
		this.str = str;
		index = 1;
		startIndexAt = 0;
	}
	
	protected static final int INIT_DRAW_X = 8, INIT_DRAW_Y = 208;
	protected int index = 1, drawX = INIT_DRAW_X, drawY = INIT_DRAW_Y;
	protected int indexWait = 0, arrowWait = 0;
	private static int indexWaitMax = 5;
	private boolean waitingForInput = false, zReleased = true;
	private int startIndexAt = 0;
	public static final char DOWN_ARROW = '\u0010', LEFT_ARROW = '\u0012', UP_ARROW = '\u0014', RIGHT_ARROW = '\u0016';
	public static final char WAIT = '\u0005', CONTINUE = '\u0006';
	protected boolean drawOtherArrow = false;
	
	@Override
	public void draw(Graphics2D g2d){
		drawBox(g2d,0,200,32,7);
		Color tint = null;
		drawX = INIT_DRAW_X; drawY = INIT_DRAW_Y;
		
		for(int i = startIndexAt; i < index; i++){
			char c = string[i];
			if(c == '\n'){
				drawY += 10;
				drawX = INIT_DRAW_X;
			}else if(c == '\t'){
				g2d.drawImage(letters[' '],drawX,drawY,null);
				drawX += letters[' '].getWidth();
				g2d.drawImage(letters[' '],drawX,drawY,null);
				drawX += letters[' '].getWidth();
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
					drawX += letters[SECTION].getWidth();
					
				//	throw new RuntimeException("Letter modifier format at index "+i+" in string \""+str+"\".");
				}else{
					c = string[i];
					if(c == 'c'){
						if(i >= string.length-8){
							g2d.drawImage(letters[SECTION], drawX, drawY, null);
							drawX += letters[SECTION].getWidth();
							g2d.drawImage(letters[SECTION], drawX, drawY, null);
							
							drawX += letters[SECTION].getWidth();
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
						drawX += letters[SECTION].getWidth();
					}
				}
			}else{
				Color underlineColor = Color.WHITE;
				if(tint != null)
					underlineColor = new Color((tint.getRed()+255)/2,(tint.getGreen()+255)/2,(tint.getBlue()+255)/2);
				if(bold){
					if(underline){
						g2d.setColor(underlineColor);
						g2d.drawLine(drawX, drawY+9, drawX+letters[c].getWidth()+1, drawY+9);
					}
					g2d.drawImage(tint(letters[c],tint),drawX,drawY,null);
					g2d.drawImage(tint(letters[c],tint),drawX+1,drawY,null);
					
					drawX++;
				}else{
					if(underline){
						g2d.setColor(underlineColor);
						g2d.drawLine(drawX, drawY+9, drawX+letters[c].getWidth(), drawY+9);
					}
					g2d.drawImage(tint(letters[c],tint),drawX,drawY,null);
				}
				
				drawX += letters[c].getWidth();
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
				Panel.getInstance().setGUI(Panel.getInstance().previous);
			}
		}
		if(debug && !Panel.noScreenDebug){
			Panel.drawDebugString(g2d,"wait="+indexWaitMax+" index="+index+" indexWait="+indexWait+(waitingForInput? " waiting" : ""),0,106);
		}
	}

	@Override
	public boolean blockInput(){
		return true;
	}
	
}
