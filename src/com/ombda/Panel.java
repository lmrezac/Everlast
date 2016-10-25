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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;

import com.ombda.entities.Player;
import com.ombda.entities.Sprite;
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
	private String player_name;
	private Map map;
	public GUI gui;
	public GUI previous;
	public HUD hud;
	public Input input;
	public Console console;
	public MapMaker mapcreator;
	public MessageBox msgbox;
	public int offsetX = -3*Tile.SIZE, offsetY = 0;
	private Image buffer;
	boolean running = true;
	private int FPS = 60;
	private int mouseX = 0, mouseY = 0;
	public boolean drawBoundingBoxes = false;
	public String step = null;
	public static final double borderX_left = (3.0/8)*(double)PRF_WIDTH;
	public static final double borderX_right = (5.0/8)*(double)PRF_WIDTH;
	public static final double borderY_top = (3.0/8)*(double)PRF_HEIGHT;
	public static final double borderY_bottom = (5.0/8)*(double)PRF_HEIGHT;

	// public final Timer timer;
	private RenderingHints renderingHints;
	private Thread animator;

	public Panel(){
		instance = this;
		Images.init();
		
		buffer = new BufferedImage(PRF_WIDTH,PRF_HEIGHT,BufferedImage.TYPE_INT_ARGB);
		
		player = new Player(0,0,Facing.N);
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
		
		Tiles.loadTiles();
		loadScripts();
		
		loadSaveFile();

		debug("New Panel created!");
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
			Script.compile(name,Files.read(file));
		}
		/*new Script("map_test3",Arrays.<ScriptStep>asList(
			new ScriptStep(){

				@Override
				public void execute(Panel game, Script script){
					
				}

				@Override
				public boolean done(){
					return false;
				}
				
			}
		));*/
		
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
		
		/*if(gui == hud) guiID = "hud";
		else if(gui == msgbox) guiID = "msgbox";
		else if(gui == console) guiID = "console";
		else if(gui == input) guiID = "input";
		else if(gui == mapcreator)
			guiID = "mapcreator";*/
		debug(gui.toString());
	}
	private List<Script> scripts = new ArrayList<>();
	public void runScript(Script s){
		if(!scripts.contains(s)){
			scripts.add(s);
		}
		//debug("running new script");
	}
	public void loadSaveFile(){
	
		File f = new File(Files.localize("saves/save0.dat"));
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(IOException e){
				debug("Error creating save file at "+f.getAbsolutePath());
				if(printStackTrace)
					e.printStackTrace();
				throw new FatalError();
			}
			Files.write(f.getAbsolutePath(), Arrays.asList("player","test"));
			System.out.println(Files.read(f));
		}
		List<String> lines = Files.read(f);
		if(lines.size() != 7) throw new RuntimeException("Invalid save file : expected 7 lines, got "+lines.size());
		System.out.println(lines);
		player_name = lines.get(0);
		setMap(Map.get(lines.get(1)));
		player.setPos(Double.parseDouble(lines.get(2)), Double.parseDouble(lines.get(3)));
		offsetX = Integer.parseInt(lines.get(4));
		offsetY = Integer.parseInt(lines.get(5));
		Script.loadVars(lines.get(6));
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
				throw new FatalError();
			}
		}
		List<String> lines = new ArrayList<>();
		lines.add(player_name);
		lines.add(player.getMap().toString());
		lines.add(Double.toString(player.x));
		lines.add(Double.toString(player.y));
		lines.add(Integer.toString(offsetX));
		lines.add(Integer.toString(offsetY));
		lines.add(Script.saveVars());
		Files.write("saves/save0.dat", lines);
		debug("Game saved!");
	}
	public static Panel getInstance(){ return instance;}
	public Player getPlayer(){ return player; }
	
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
		offG.setColor(map.getBackground());
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
		throw new FatalError();
	}
	}
	public static boolean noScreenDebug = false;
	public long lastFrame = System.currentTimeMillis();
	private void paintOffscreen(Graphics2D g2d){
		
		if(gui.drawMap()){
			map.drawBackground(g2d,offsetX,offsetY);
		

			if(drawBoundingBoxes)
				map.drawTileEntities(g2d,offsetX,offsetY);
		
			List<Sprite> sprites = new ArrayList<>(map.getSprites());
			
			//List<Sprite> undrawn = new ArrayList<Sprite>();
			double minY = 1;
			while(!sprites.isEmpty()){
				for(int i = sprites.size()-1; i >= 0; i--){
					Sprite s = sprites.get(i);
					double h = s.y+s.spriteHeight();
					if(h <= minY){
						s.draw(g2d, offsetX, offsetY);
						if(drawBoundingBoxes)
							s.drawBoundingBox(g2d, offsetX, offsetY);
						sprites.remove(i);
					}
				}
				minY++;
			}
			
			map.drawForeground(g2d,offsetX,offsetY);
			
			
			Tiles.incrementAnimationFrames();
		}
		gui.draw(g2d);
		if(debug && !noScreenDebug){
			long temp = 0;
			int size = 12;
			if(gui == mapcreator){
				drawDebugString(g2d,"map "+map.toString()+" {width:"+map.width()+", height:"+map.height()+"}",0,10);
				int[] mouseCoords = screenCoordsToImageCoords(mouseX,mouseY);
				int[] tileCoords = screenCoordsToTiles(mouseX,mouseY);
				drawDebugString(g2d,"mouse : ("+mouseCoords[0]+","+mouseCoords[1]+") ["+tileCoords[0]+","+tileCoords[1]+"]",0,10+size);
				drawDebugString(g2d,"layer: "+(Frame.keys[KeyEvent.VK_SHIFT]? "FOREGROUND" : "BACKGROUND"),0,10+2*size);
			}else{
				drawDebugString(g2d,"player ("+Math.round(player.x)/(Tile.SIZE/16)+","+Math.round(player.y)/(Tile.SIZE/16)+") ["+(int)(Math.round(player.x)/(double)Tile.SIZE)+","+(int)(Math.round(player.y)/(double)Tile.SIZE)+"]",0,10);
				drawDebugString(g2d,"map "+map.toString()+" {width:"+map.width()+", height:"+map.height()+"}",0,10+size);
				drawDebugString(g2d,"offset X = "+(int)offsetX+" offset Y = "+(int)offsetY,0,10+2*size);
				drawDebugString(g2d,"facing: "+player.getDirection(),0,10+3*size);
				drawDebugString(g2d,"border Y : ["+borderY_top+";"+borderY_bottom+"]",0,10+4*size);
				drawDebugString(g2d,"fpms:"+(((temp = System.currentTimeMillis())-lastFrame)),0,10+5*size);
				int[] mouseCoords = screenCoordsToImageCoords(mouseX,mouseY);
				int[] tileCoords = screenCoordsToTiles(mouseX,mouseY);
				drawDebugString(g2d,"mouse : ("+mouseCoords[0]+","+mouseCoords[1]+") ["+tileCoords[0]+","+tileCoords[1]+"]",0,10+6*size);
				drawDebugString(g2d,"gui : "+gui.toString()+" blockinput = "+gui.blockInput(),0,10+7*size);
				drawDebugString(g2d,"step = "+step,0,10+9*size);
			}
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
	
	/*public void resetScript(){
		debug("reset script");
		for(int i = currentScripts.size()-1; i>=0; i--){
			currentScripts.get(i).reset();
		}
		currentScripts.clear();
		if(!scripts.isEmpty()){
			
			currentScripts.add(scripts.remove(scripts.size()-1));
			while(!scripts.isEmpty() && currentScript().done()){
				currentScripts.set(currentScripts().size()-1,scripts.remove(scripts.size()-1);
				
			}
			if(scripts.isEmpty() && currentScript.done()) 
				currentScript = null;
		}
	}*/
	public void update(){
		if(!scripts.isEmpty()){
			for(int i = scripts.size()-1; i>=0; i--){
				Script script = scripts.get(i);
				if(script.done()){
					script.reset();
					scripts.remove(i);
				}else{
					script.execute(script);
					if(script.done()){
						script.reset();
						scripts.remove(i);
					}
				}
			}
			/*currentScript.execute(currentScript);
			if(currentScript != null && currentScript.done()){
				resetScript();
			}*/
		}
		if(!gui.pauseGame()){
			if(!gui.blockInput()){
				
				
				player.update();
			}

			Collection<Sprite> sprites = map.getSprites();
			for(Sprite s : sprites){
				if(s instanceof Updateable)
					((Updateable)s).update();
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
			
			}

			beforeTime = System.currentTimeMillis();
		}
	}catch(Exception e){
		debug(e.getMessage());
		if(printStackTrace)
			e.printStackTrace();
		throw new FatalError();
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
		if(e.getKeyChar() == '`' && gui != mapcreator){
			if(gui == console){
				console.reset();
				setGUI(hud);
			}
			else setGUI(console);
		}else{
			if(e.getKeyChar() == 'z' && gui == hud){
				Frame.keys[KeyEvent.VK_Z] = false;
				Collection<Sprite> sprites = map.getSprites();
				int x, y;
				if(player.getDirection() == Facing.N){
					x = (int)(player.x+(((Rectangle2D)player.getBoundingBox()).getWidth()/2));
					y = (int)(player.y-1);
					
				}else if(player.getDirection() == Facing.E){
					x = (int)(player.x+((Rectangle2D)player.getBoundingBox()).getWidth());
					y = (int)(player.y+(((Rectangle2D)player.getBoundingBox()).getHeight()/2));
					
				}else if(player.getDirection() == Facing.S){
					x = (int)(player.x+(((Rectangle2D)player.getBoundingBox()).getWidth()/2));
					y = (int)(player.y+((Rectangle2D)player.getBoundingBox()).getHeight());
					
				}else if(player.getDirection() == Facing.W){
					x = (int)(player.x-1);
					y = (int)(player.y+(((Rectangle2D)player.getBoundingBox()).getHeight()/2));	
					
				}else return;
				
				map.getTileAt(x,y, 0).onInteracted(player, x, y);
				for(Sprite s : sprites){
					if(s instanceof Interactable){
						((Interactable)s).onInteracted(player, x, y);
					}
				}
			}
			gui.keyTyped(e);
			
		}
		
	}
	public static int[] screenCoordsToTiles(int x, int y){
		int[] coords = Panel.screenCoordsToImageCoords(x, y);
		x = coords[0]-Panel.getInstance().offsetX;
		y = coords[1]-Panel.getInstance().offsetY;
		x /= Tile.SIZE;
		y /= Tile.SIZE;
		return new int[]{x,y};
	}

	

}
