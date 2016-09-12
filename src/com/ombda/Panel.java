package com.ombda;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;
import static com.ombda.Frame.PRF_HEIGHT;
import static com.ombda.Frame.PRF_WIDTH;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import com.ombda.gui.Console;
import com.ombda.gui.GUI;
import com.ombda.gui.HUD;
import com.ombda.gui.Input;
import com.ombda.gui.MapMaker;
import com.ombda.gui.MessageBox;
import com.ombda.scripts.Script;

//import javax.swing.Timer;

public class Panel extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener{
	private static final long serialVersionUID = -1418699633283587432L;
	private static Panel instance;
	
	private Player player;
	private Script currentScript = null;
	private Map map;
	private GUI gui;
	public GUI previous;
	public HUD hud;
	public Input input;
	public Console console;
	public MapMaker mapcreator;
	public String guiID;
	public MessageBox msgbox;
	public int offsetX = -3*16, offsetY = 0;
	private Image buffer;
	private boolean running = true;
	private int FPS = 60;
	private int mouseX = 0, mouseY = 0;
	public static final double borderX_left = (1.0/4)*(double)PRF_WIDTH;
	public static final double borderX_right = (3.0/4)*(double)PRF_WIDTH;
	public static final double borderY_top = (1.0/4.0)*(double)PRF_HEIGHT;
	public static final double borderY_bottom = (3.0/4)*(double)PRF_HEIGHT;

	// public final Timer timer;
	private RenderingHints renderingHints;
	private Thread animator;

	public Panel(){
		instance = this;
		Images.init();
		
		buffer = new BufferedImage(PRF_WIDTH,PRF_HEIGHT,BufferedImage.TYPE_INT_ARGB);
		
		player = new Player(0,0);
		renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderingHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		this.setDoubleBuffered(true);
		this.setIgnoreRepaint(true);
		
		hud = new HUD();
		msgbox = new MessageBox();
		input = new Input();
		console = new Console();
		mapcreator = new MapMaker();
		gui = hud;
		previous = hud;
		guiID = "hud";
		
		Tiles.loadTiles();
		loadScripts();
		
		loadSaveFile();
		
		
		
		
		
		System.out.println("New Panel created!");
	}
	private void loadScripts(){
		File f = new File(Files.localize("scripts"));
		assert f.exists() : "Scripts directory not found!";
		assert f.isDirectory() : "\\resources\\scripts was not a directory!";
		File[] files = f.listFiles(new FilenameFilter(){
			public boolean accept(File arg0, String arg1){
				return arg1.endsWith(".script");
			}
		});
		for(File file : files){
			String name = file.getName();
			int i = name.indexOf('.');
			if(i != -1)
				name = name.substring(0, i);
			Script.load(name,Files.read(file));
		}
	}
	public void setMap(Map map){
		this.map = map;
		player.setMap(map);
		player.x = map.playerSpawnX;
		player.y = map.playerSpawnY;
	}
	public void setGUI(GUI gui){
		previous = this.gui;
		this.gui = gui;
		if(gui == hud) guiID = "hud";
		else if(gui == msgbox) guiID = "msgbox";
		else if(gui == console) guiID = "console";
		else if(gui == input) guiID = "input";
		else if(gui == mapcreator){
			guiID = "mapcreator"; 
			Panel.noScreenDebug = true;
		}
	}
	public void runScript(Script s){
		this.currentScript = s;
	}
	private void loadSaveFile(){
	
		File f = new File(Files.localize("saves/save0.dat"));
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(IOException e){
				debug("Error creating save file at "+f.getAbsolutePath());
				if(printStackTrace)
					e.printStackTrace();
				System.exit(0);
			}
			Files.write(f.getAbsolutePath(), Arrays.asList("map=test"));
			System.out.println(Files.read(f));
		}
		List<String> lines = Files.read(f);
		System.out.println(lines);
		for(String str : lines){
			Object info = compartmentalize(str);
			if(info.equals("map")){
				setMap(Map.get(info.toString()));
				debug("map created!");
			}
		}
	}
	public void saveGame(){
		File f = new File(Files.localize("saves/save0.dat"));
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(IOException e){
				debug("Error creating save file at "+f.getAbsolutePath());
				if(printStackTrace)
					e.printStackTrace();
				System.exit(0);
			}
		}
		List<String> lines = new ArrayList<>();
		lines.add("map="+player.getMap().toString());
	}
	public static Panel getInstance(){ return instance;}
	public Player getPlayer(){ return player; }
	
	private static Object compartmentalize(String keyValue){
		int i = keyValue.indexOf("=");
		final String key = keyValue.substring(0,i), value = keyValue.substring(i+1);
		return new Object(){
			@Override
			public boolean equals(Object obj){
				if(obj instanceof String){
					String str = (String)obj;
					return str.equals(key);
				}else return false;
			}
			@Override
			public String toString(){
				return value;
			}
		};
	}

	@Override
	public void addNotify(){
		super.addNotify();

		animator = new Thread(this);
		animator.start();
	}
	
	public static int[] screenCoordsToImageCoords(int x, int y){
		return new int[]{(int)(x*(Frame.PRF_WIDTH/(double)Panel.getInstance().getParent().getWidth())),(int)(y*(Frame.PRF_HEIGHT/(double)Panel.getInstance().getParent().getHeight()))};
	}

	@Override
	public void paintComponent(Graphics g){
	try{
		Graphics2D g2d = (Graphics2D)g;
		g2d.addRenderingHints(renderingHints);
		Graphics2D offG = (Graphics2D)buffer.getGraphics();
		offG.setColor(getBackground());
		offG.fillRect(0, 0, Frame.PRF_WIDTH, Frame.PRF_HEIGHT);
		// Draw into the offscreen image.
		paintOffscreen(offG);
		// Put the offscreen image on the screen.
		Dimension size = getSize();
		g2d.drawImage(buffer.getScaledInstance(size.width,size.height,0),0,0,null);
	}catch(NullPointerException e){
		debug(e.getMessage());
		if(printStackTrace)
			e.printStackTrace();
		System.exit(0);
	}
	}
	public static boolean noScreenDebug = false;
	private long lastFrame = System.currentTimeMillis();
	private void paintOffscreen(Graphics2D g2d){
		
		if(gui.drawMap()){
			map.drawBackground(g2d,offsetX,offsetY);
		
			player.draw(g2d,offsetX,offsetY);
		
			Iterator<Sprite> sprites = map.getSprites();
			while(sprites.hasNext()){
				Sprite s = sprites.next();
				s.draw(g2d, offsetX, offsetY);
			}
			
			map.drawForeground(g2d,offsetX,offsetY);
		}
		gui.draw(g2d);
		if(debug && !noScreenDebug){
			long temp = 0;
			drawDebugString(g2d,"player ("+Math.round(player.x)+","+Math.round(player.y)+")",0,10);
			drawDebugString(g2d,"map {width:"+map.width()+", height:"+map.height()+"}",0,22);
			drawDebugString(g2d,"offset X = "+(int)offsetX+" offset Y = "+(int)offsetY,0,34);
			drawDebugString(g2d,"border X : ["+borderX_left+";"+borderX_right+"]",0,46);
			drawDebugString(g2d,"border Y : ["+borderY_top+";"+borderY_bottom+"]",0,58);
			drawDebugString(g2d,"fpms:"+(((temp = System.currentTimeMillis())-lastFrame)),0,70);
			int[] mouseCoords = screenCoordsToImageCoords(mouseX,mouseY);
			drawDebugString(g2d,"mouse X : "+mouseCoords[0]+" mouse Y : "+mouseCoords[1],0,82);
			drawDebugString(g2d,"gui : "+gui.toString(),0,94);
			lastFrame = temp;
		}
		Toolkit.getDefaultToolkit().sync();
	}
	public static void drawDebugString(Graphics2D g2d, String str, int x, int y){
		g2d.setColor(Color.black);
		g2d.drawString(str, x-1, y);
		g2d.drawString(str, x+1, y);
		g2d.drawString(str, x, y-1);
		g2d.drawString(str, x, y+1);
		/*g2d.drawString(str, x-1, y-1);
		g2d.drawString(str, x-1, y+1);
		g2d.drawString(str, x+1, y-1);
		g2d.drawString(str, x+1, y+1);*/
		g2d.setColor(Color.white);
		g2d.drawString(str, x, y);
	}
	
	public static final double dist = Math.sqrt(.5);
	

	public void update(){
		if(currentScript != null){
			currentScript.execute(this);
			if(currentScript.done())
				currentScript = null;
		}
		if(!gui.pauseGame()){
			if(!gui.blockInput()){
				//debug("gui == "+gui.toString()+" blockinput = "+gui.blockInput());
				player.doKeys();
			}

			player.testCollision();
			Iterator<Sprite> sprites = map.getSprites();
			while(sprites.hasNext()){
				Sprite s = sprites.next();
				if(s instanceof Updateable)
					((Updateable)s).update();
			}
			
			sprites = map.getSprites();
			while(sprites.hasNext()){
				Sprite s = sprites.next();
				if(s instanceof Collideable)
					((Collideable)s).testCollision();
			}
		}
		gui.update();
	}

	
	
	@Override
	public void run(){
	try{
		long beforeTime, timeDiff, sleep;

		beforeTime = System.currentTimeMillis();

		while(running){

			update();
			repaint();

			timeDiff = System.currentTimeMillis() - beforeTime;
			sleep = 1000 / FPS - timeDiff;

			if(sleep < 0){
				sleep = 2;
			}

			try{
				Thread.sleep(sleep);
			}catch(InterruptedException e){
				System.out.println("Interrupted: " + e.getMessage());
				return;
			}

			beforeTime = System.currentTimeMillis();
		}
	}catch(Exception e){
		debug(e.getMessage());
		if(printStackTrace)
			e.printStackTrace();
		System.exit(0);
	}
	}
	
	public void stop(){
		running = false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0){
		mouseX = arg0.getX();
		mouseY = arg0.getY()-38;
		gui.mouseDragged(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0){
		mouseX = arg0.getX();
		mouseY = arg0.getY()-38;
		gui.mouseMoved(arg0);
	}

	@Override
	public void mouseClicked(MouseEvent arg0){
		gui.mouseClicked(arg0);
	}

	@Override
	public void mouseEntered(MouseEvent arg0){
		gui.mouseEntered(arg0);
	}

	@Override
	public void mouseExited(MouseEvent arg0){
		gui.mouseExited(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0){
		gui.mousePressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0){
		gui.mouseReleased(arg0);
	}

	@Override
	public void keyPressed(KeyEvent e){
		gui.keyPressed(e);
		
	}

	@Override
	public void keyReleased(KeyEvent e){
		gui.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e){
		if(e.getKeyChar() == '`'){
			if(gui == console){
				console.reset();
				setGUI(hud);
			}
			else setGUI(console);
		}else{
			gui.keyTyped(e);
		}
		
	}

	

}
