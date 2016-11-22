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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JPanel;

import com.ombda.entities.Player;
import com.ombda.entities.Sprite;
import com.ombda.gui.Console;
import com.ombda.gui.GUI;
import com.ombda.gui.HUD;
import com.ombda.gui.Input;
import com.ombda.gui.MapMaker;
import com.ombda.gui.MessageBox;
import com.ombda.gui.Picture;
import com.ombda.scripts.Script;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

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
	public Picture img;
	
	public ScriptEngine scriptEngine;
	private Object global;
	public int offsetX = -3*Tile.SIZE, offsetY = 0;
	private Image buffer;
	boolean running = true;
	private int FPS = 60;
	private int mouseX = 0, mouseY = 0;
	public boolean drawBoundingBoxes = false;
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
		img = new Picture();
		gui = hud;
		previous = hud;
		
		Tiles.loadTiles();
		
		scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		loadScriptEngine();
		
		loadScripts();
		
		loadSaveFile();

		debug("New Panel created!");
	}
	public HashMap<String,String> scripts = new HashMap<>();
	private void loadScriptEngine(){
		Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("TILES",Tile.TILES_JS);
		bindings.put("MAPS", Map.MAPS_JS);
		bindings.put("msg", new Msg());
		bindings.put("north", Facing.N);
		bindings.put("northeast",Facing.NE);
		bindings.put("east",Facing.E);
		bindings.put("southeast",Facing.SE);
		bindings.put("south", Facing.S);
		bindings.put("southwest",Facing.SW);
		bindings.put("west",Facing.W);
		bindings.put("northwest",Facing.NW);
		try{
			scriptEngine.eval("var global = {}");
			scriptEngine.eval("var Map = Java.type('com.ombda.Map')");
			scriptEngine.eval("var Sprite = Java.type('com.ombda.entities.Sprite')");
			scriptEngine.eval("var CollideableSprite = Java.type('com.ombda.entities.CollideableSprite')");
			scriptEngine.eval("var NPC = Java.type('com.ombda.entities.NPC')");
			scriptEngine.eval("var game = Java.type('com.ombda.Panel').getInstance()");
			scriptEngine.eval("var player = game.player");
			scriptEngine.eval("var image = Java.type('com.ombda.Images').retrieve");
			scriptEngine.eval("var Image = Java.type('javax.swing.ImageIcon')");
			scriptEngine.eval("var AnimatedImage = Java.type('com.ombda.AnimatedImage')");
			scriptEngine.eval("var Tile = Java.type('com.ombda.Tile')");
			
		}catch(ScriptException e){
			throw new RuntimeException(e);
		}
		bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		global = bindings.get("global");
	}
	private void loadScripts(){
		File f = new File(Files.localize("scripts"));
		assert f.exists() : "Scripts directory not found!";
		assert f.isDirectory() : "\\resources\\scripts was not a directory!";
		File[] files = f.listFiles(new FilenameFilter(){
			public boolean accept(File arg0, String arg1){
				return arg1.endsWith(".ejs");
			}
		});
		for(File file : files){
			String name = file.getName();
			int i = name.indexOf('.');
			if(i != -1)
				name = name.substring(0, i);
			debug("adding script "+name);
			scripts.put(name,evaluateScriptFile(Files.read(file)));
			//Script.compile(name,Files.read(file));
		}
		try{
			scriptEngine.eval(scripts.get("global"));
		}catch(ScriptException e){
			throw new RuntimeException(e);
		}
	}
	public static String evaluateScriptFile(String str){
		Matcher m = Pattern.compile("(?=(-?((\\d*\\.\\d+)|(\\d+(\\.\\d*)?))))t").matcher(str);
		int start = 0;
		int sub = 0;
		/*while(m.find(start)){
			String group = m.group();
			debug("GROUP = "+group);
			group = group.substring(0, group.length()-1);
			String replacement;
			//if(group.contains(".")){
			//	replacement = Script.toString(Double.parseDouble(group)*(Tile.SIZE/16));
			//}else replacement = String.valueOf(Integer.parseInt(group)*(Tile.SIZE/16));
			//debug("REPLACEMENT = "+replacement);
			//str = str.substring(0, m.start()-sub++)+replacement+(m.end() == str.length()? "" : str.substring(m.end()-sub));
			str = m.replaceFirst("*16");
			m.reset(str);
		}*/
		str = str.replaceAll("//[^\n]*\n","")
				.replaceAll("(?<=\\d)t(?![\\d\\w_])","*16")
				.replaceAll("(?<=(^|\\(|,)\\s*)\\*wait(?=\\s*(\\)|$|,))","'__WAIT__'")
				.trim();
		//debug("Loaded script "+str);
		return str;
	}
	public void setMap(Map map){
		this.map = map;
		player.setMap(map);
		player.x = map.playerSpawnX;
		player.y = map.playerSpawnY;
		
		//script map variable
		setScriptMap(map);
	}
	private void setScriptMap(Map map){
		Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("map", this.map);
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
	//private List<Script> scripts = new ArrayList<>();
	public void runScript(String str){
		/*if(!scripts.contains(s)){
			scripts.add(s);
		}*/
		
		String script = scripts.get(str);
		if(script != null){
			debug("running new script "+str);
			new ScriptThread(script).start();
		}else{
			debug("Error: script "+str+" doesn't exist!");
			throw new FatalError();
		}
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
			debug(Files.readLines(f));
		}
		List<String> lines = Files.readLines(f);
		if(lines.size() != 7) throw new RuntimeException("Invalid save file : expected 7 lines, got "+lines.size());
		debug(lines);
		player_name = lines.get(0);
		Map map = Map.get(lines.get(1));
		setMap(map);
		player.setPos(Double.parseDouble(lines.get(2)), Double.parseDouble(lines.get(3)));
		offsetX = Integer.parseInt(lines.get(4));
		offsetY = Integer.parseInt(lines.get(5));
		loadGlobal(lines.get(6));
		
	}
	public void saveGame(){
		JSObject Save = (JSObject)scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).get("Save");
		JSObject functions = (JSObject)Save.getMember("functions");
		int length = (Integer)functions.getMember("length");
		for(int i = 0; i < length; i++){
			JSObject func = (JSObject)functions.getSlot(i);
			func.call(null);
		}
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
		lines.add(saveGlobal());
		Files.write("saves/save0.dat", lines);
		debug("Game saved!");
	}
	private void loadGlobal(String str){
		str = str.substring(1);
		List<String> stuff = Script.scanLine(str);
		JSObject obj = (JSObject)global;
		try{
		for(int i = 0; i < stuff.size()-1; i+=2){
			String name = stuff.get(i);
			String value = stuff.get(i+1);
			name = name.substring(1, name.length()-1);
			if(value.startsWith("\"") && value.endsWith("\"")){
				obj.setMember(name,value.substring(1, value.length()-1));
			}else if(value.contains(".")){
				obj.setMember(name, Double.parseDouble(value));
			}else obj.setMember(name, Integer.parseInt(value));
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		debug("LOADED GLOBAL");
	}
	private String saveGlobal(){
		String result = "_";
		JSObject fields = (JSObject)global;
		for(String str : fields.keySet()){
			Object obj = fields.getMember(str);
			String val;
			if(obj instanceof String) val = '"'+obj.toString()+'"';
			else if(obj instanceof Number) val = obj.toString();
			else val = null;
			result += '"'+str+'"'+' '+val+' ';
		}
		return result.trim();
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
				drawDebugString(g2d,"map "+map.toString()+" {width:"+map.width()+", height:"+map.height()+"}",0,10+2*size);
				int[] mouseCoords = screenCoordsToImageCoords(mouseX,mouseY);
				int[] tileCoords = screenCoordsToTiles(mouseX,mouseY);
				drawDebugString(g2d,"mouse : ("+mouseCoords[0]+","+mouseCoords[1]+") ["+tileCoords[0]+","+tileCoords[1]+"]",0,10+3*size);
				drawDebugString(g2d,"layer: "+(Frame.keys[KeyEvent.VK_SHIFT]? "FOREGROUND" : "BACKGROUND"),0,10+4*size);
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
	
	public void update(){
	
		if(!gui.pauseGame()){
			if(!gui.blockInput()){
				
				
				player.update();
			}

			List<Sprite> sprites = map.getSprites();
			for(int i = 0; i < sprites.size(); i++){
				Sprite s = sprites.get(i);
				if(s instanceof Updateable)
					((Updateable)s).update();
			}
			
		}
		gui.update();
	}

	
	
	@Override
	public synchronized void run(){
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
				debug("Interrupted: " + e.getMessage());
			
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
		ScriptThread.stopAll();
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

	private static class Msg extends AbstractJSObject implements MessageListener{
		@Override
		public boolean isFunction(){return true;}
		boolean eventReceived = false;
		@Override
		public Object call(Object thiz, Object... args){
			eventReceived = false;
			Panel panel = Panel.getInstance();
			String message = "";
			for(Object obj : args){
				String str = obj.toString();
				if(str.equals("__WAIT__"))
					message += MessageBox.WAIT;
				else message += str;
			}
			panel.msgbox.setMessage(message);
			panel.msgbox.addInputListener(this);
			panel.setGUI(panel.msgbox);
			panel.msgbox.waitingForInput = false;
			while(!eventReceived){
				debug("waiting");
			}
			return null;
		}
		@Override
		public void onMessageFinish(){
			eventReceived = true;
			debug("Message finished");
			Panel.getInstance().msgbox.removeInputListener(this);
		}
	}

}
