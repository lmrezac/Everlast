package com.ombda;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import static com.ombda.Debug.debug;
public class Frame extends JFrame implements KeyListener{
	private static final long serialVersionUID = -3581077622302427213L;
	private Panel panel;
	public static boolean[] keys = new boolean[0xFFFF];
	public static final int WIDTH = 600, HEIGHT = 600;
	//resolution
	public static final int PRF_WIDTH = 2*256, PRF_HEIGHT = 2*256;
	
	public Frame(){
		super("Game");
		
		this.add(panel = new Panel());
		
		this.setSize(WIDTH,HEIGHT);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLocationRelativeTo(null);
		
		this.addKeyListener(this);
		this.addKeyListener(panel);
		panel.setFocusTraversalKeysEnabled(false);
		setFocusTraversalKeysEnabled(false);
		this.addMouseListener(panel);
		this.addMouseMotionListener(panel);
		
		this.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent event){
				double height = Frame.this.getHeight();
				Frame.this.setSize((int)height,(int)height);
				Frame.this.setLocationRelativeTo(null);
			}
		});
		
		debug("New Frame created!");
	}
	
	
	protected void processWindowEvent(WindowEvent event){
		if(event.getID() == WindowEvent.WINDOW_CLOSING){
			this.panel.stop();
			this.dispose();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent arg0){
		keys[arg0.getKeyCode()] = true;
		//debug("key pressed : "+arg0.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent arg0){
		keys[arg0.getKeyCode()] = false;
		//debug("key released : "+arg0.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent arg0){}
}
