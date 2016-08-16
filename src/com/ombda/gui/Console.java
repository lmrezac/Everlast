package com.ombda.gui;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.ombda.Frame;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Player;
import com.ombda.Tile;
import com.ombda.Tiles;

public class Console extends Input{
	private Panel panel;
	
	public Console(){
		setMessage("§o> §0");
		panel = Panel.getInstance();
	}
	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			executeCommand(str);
		}else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			if(str.length() > 6)
			setMessage(str.substring(0,str.length()-1));
		}else super.keyPressed(e);
	}
	
	public void executeCommand(String str){
		if(str.equals("§o> §0")){
			reset();
			return;
		}
		List<String> args = parseArgs(str.substring(6));
		if(args.get(0).equals("gui")){
			cmdGui(args);
		}else if(args.get(0).equals("noclip")){
			cmdNoclip(args);
		}else if(args.get(0).equals("close")){
			cmdClose(args);
		}else if(args.get(0).equals("quit")){
			System.exit(0);
		}else if(args.get(0).equals("set")){
			cmdSet(args);
		}else if(args.get(0).equals("save")){
			cmdSave(args);
		}else if(args.get(0).equals("fill")){
			cmdFill(args);
		}else if(args.get(0).equals("debug")){
			cmdDebug(args);
		}else if(args.get(0).equals("goto")){
			cmdGoto(args);
		}else if(args.get(0).equals("help")){
			cmdHelp(args);
		}else{
			debug("unknown command");
			panel.msgbox.setMessage("Error: unknown command"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
		}
		reset();
	}
	
	private void cmdGui(List<String> args){
		if(args.size() < 2){
			panel.msgbox.setMessage("Error: param count"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
			return;
		}
		if(args.get(1).equals("msgbox")){
			String display = ""+WAIT;
			if(args.size() > 2){
				String str2 = "";
				for(int i = 2; i < args.size(); i++)
					str2+=args.get(i);
				str2+=WAIT;
				display = str2;
			}
			while(display.contains("\\u")){
				int i = display.indexOf("\\u");
				String first = display.substring(0,i);
				String last = display.substring(i+6);
				int value = Integer.decode("#"+display.substring(i+2,i+6));
				display = first + (char)value + last;
			}
			panel.msgbox.setMessage(display);
			panel.setGUI(panel.msgbox);
		}else if(args.get(1).equals("input")){
			panel.input.reset();
			panel.setGUI(panel.input);
		}else if(args.get(1).equals("hud")){
			panel.setGUI(panel.hud);
			panel.previous = panel.hud;
			reset();
		}else if(args.get(1).equals("mapcreator")){
			panel.setGUI(panel.mapcreator);
			reset();
		}else if(args.get(1).equals("console")){
			panel.msgbox.setMessage("Cannot set gui to "+args.get(1)+WAIT);
			panel.setGUI(panel.msgbox);
		}else{
			panel.msgbox.setMessage("No such gui id: "+args.get(1));
			panel.setGUI(panel.msgbox);
		}
	}
	
	private void cmdNoclip(List<String> args){
		Player p = panel.getPlayer();
		if(args.size() > 1){
			String arg = args.get(1);
			if(arg.equals("off")||arg.equals("false")||arg.equals("0"))
				p.noclip = false;
			else if(arg.equals("on")||arg.equals("true")||arg.matches("\\d+"))
				p.noclip = true;
			else{
				panel.msgbox.setMessage("Invalid value given"+WAIT);
				panel.msgbox.instant();
				panel.setGUI(panel.msgbox);
			}
		}else{
			p.noclip = !p.noclip;
		}
		if(p.noclip){
			panel.msgbox.setMessage("Noclip ON        ");
		}else{
			panel.msgbox.setMessage("Noclip OFF        ");
		}

		panel.msgbox.instant();
		panel.setGUI(panel.msgbox);
		panel.previous = panel.hud;
	}
	
	private void cmdClose(List<String> args){
		panel.setGUI(panel.hud);
		panel.previous = panel.hud;
		reset();
	}
	
	private void cmdSet(List<String> args){
		try{
			int index = 0;
			if(args.get(++index).equals("tile")){
				if(args.get(index+1).equals("from")){
					index++;
					args.set(index, "fill");
					for(int i = 0; i < index; i++)
						args.remove(0);
					cmdFill(args);
				}else{
					if(args.get(index+1).equals("at"))
						index++;
					int x = Integer.decode(args.get(++index))*16+1;
					int y = Integer.decode(args.get(++index))*16+1;
					
					String layerName = args.get(++index);
					int layer = 0;
					if(layerName.equals("fore")||layerName.equals("foreground")||layerName.equals("1"))
						layer = 1;
					else if(layerName.equals("back")||layerName.equals("background")||layerName.equals("0"))
						layer = 0;
					else{
						index--;
					}
					if(args.get(index+1).equals("to")) 
						index++;
					
					String id = args.get(++index);
					short tileId = 0;
					if(id.matches("\\d+")){
						tileId = Short.parseShort(id);
					}else if(id.matches("[\\w_]+")){
						Class<Tiles> c = Tiles.class;
						try{
							Field f = c.getDeclaredField(id);
							try{
								Object obj = f.get(null);
								if(!(obj instanceof Short)){
									panel.msgbox.setMessage("Invalid tile ID given"+WAIT);
									panel.msgbox.instant();
									panel.setGUI(panel.msgbox);
									reset();
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
							panel.msgbox.setMessage("Invalid tile ID given"+WAIT);
							panel.msgbox.instant();
							panel.setGUI(panel.msgbox);
							reset();
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
						panel.msgbox.setMessage("Invalid tile ID given"+WAIT);
						panel.msgbox.instant();
						panel.setGUI(panel.msgbox);
						reset();
						return;
					}
					
					Map map = panel.getPlayer().getMap();
					map.setTileAt(x, y, layer, Tile.getTile(tileId));
					
				}
			}else if(args.get(index).equals("gui")){
				for(int i = 0; i < index; i++){
					args.remove(0);
				}
				cmdGui(args);
			}else if(args.get(index).equals("map")){
				index++;
				String str2 = "";
				for(int i = index; i < args.size(); i++)
					str2+=args.get(i);
				
				try{
					Map newMap = Map.get(str2);
					panel.setMap(newMap);
				}catch(RuntimeException e){
					panel.msgbox.setMessage(e.getMessage()+WAIT);
					panel.msgbox.instant();
					panel.setGUI(panel.msgbox);
				}
			}else if(args.get(index).startsWith("player.")){
				int i = args.get(index).indexOf('.');
				String var = args.get(index).substring(i+1);
				
				index++;
				String value = args.get(index);
				Player player = panel.getPlayer();
				if(var.equals("x")){
					player.x = Integer.decode(value);
				}else if(var.equals("y")){
					player.y = Integer.decode(value);
				}else if(var.equals("pos")){
					int x = Integer.decode(value);
					index++;
					int y = Integer.decode(args.get(index));
					player.setPos(x, y);
				}else{
					panel.msgbox.setMessage("Variable "+var+" does not exist."+WAIT);
					panel.msgbox.instant();
					panel.setGUI(panel.msgbox);
				}
			}else{
				panel.msgbox.setMessage("Error: cannot set a "+args.get(1)+WAIT);
				panel.msgbox.instant();
				panel.setGUI(panel.msgbox);
			}
			}catch(IndexOutOfBoundsException ef){
				panel.msgbox.setMessage("Error: param count"+WAIT);
				panel.msgbox.instant();
				panel.setGUI(panel.msgbox);
			}
	}
	
	private void cmdFill(List<String> args){
		int index = 0;
		int x1 = Integer.decode(args.get(++index))*16+1;
		int y1 = Integer.decode(args.get(++index))*16+1;
		if(args.get(index+1).equals("to")) 
			index++;
		int x2 = Integer.decode(args.get(++index))*16+1;
		int y2 = Integer.decode(args.get(++index))*16+1;
		String layerName = args.get(++index);
		int layer = 0;
		if(layerName.equals("fore")||layerName.equals("foreground")||layerName.equals("1"))
			layer = 1;
		else if(layerName.equals("back")||layerName.equals("background")||layerName.equals("0"))
			layer = 0;
		else{
			/*panel.msgbox.setMessage("Invalid layer given"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
			reset();
			return;*/
			index--;
		}
		if(args.get(index+1).equals("to")) 
			index++;
		String id = args.get(++index);
		short tileId = 0;
		if(id.matches("\\d+")){
			tileId = Short.parseShort(id);
		}else if(id.matches("[\\w_]+")){
			Class<Tiles> c = Tiles.class;
			try{
				Field f = c.getDeclaredField(id);
				try{
					Object obj = f.get(null);
					if(!(obj instanceof Short)){
						panel.msgbox.setMessage("Invalid tile ID given"+WAIT);
						panel.msgbox.instant();
						panel.setGUI(panel.msgbox);
						reset();
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
				panel.msgbox.setMessage("Invalid tile ID given"+WAIT);
				panel.msgbox.instant();
				panel.setGUI(panel.msgbox);
				reset();
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
			panel.msgbox.setMessage("Invalid tile ID given"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
			reset();
			return;
		}
		Map map = panel.getPlayer().getMap();
		Tile t = Tile.getTile(tileId);
		
		if(x1 > x2 || y1 > y2){
			panel.msgbox.setMessage("Invalid coordinates given"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
			reset();
			return;
		}
		if(y1 == y2){
			for(int x = x1; x < x2; x++){
				map.setTileAt(x, y1, layer, t);
			}
		}else if(x1 == x2){
			for(int y = y1; y < y2; y++){
				map.setTileAt(x1, y, layer, t);
			}
		}else{
			for(int y = y1; y < y2; y++){
				for(int x = x1; x < x2; x++){
					map.setTileAt(x, y, layer, t);
				}
			}
		}
	}
	
	private void cmdSave(List<String> args){
		if(args.size() > 2){
			panel.msgbox.setMessage("Error: param count"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
		}else{
		
			if(args.size() == 1 || args.get(1).equals("game")){
				panel.saveGame();
			}else if(args.get(1).equals("map")){
				panel.getPlayer().getMap().save();
			}else{
				panel.msgbox.setMessage("Don't know how to save '"+args.get(1)+"'"+WAIT);
				panel.msgbox.instant();
				panel.setGUI(panel.msgbox);
			}
		}
	}
	
	private void cmdDebug(List<String> args){
		if(args.size() < 2){
			panel.msgbox.setMessage("Error: param count"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
			return;
		}else{
			String arg = args.get(1);
			if(arg.equals("off")||arg.equals("false")||arg.equals("0"))
				debug = false;
			else if(arg.equals("on")||arg.equals("true")||arg.matches("\\d+")){
				debug = true;
				Panel.noScreenDebug = false;
			}else if(arg.equals("minimal"))
				Panel.noScreenDebug = true;
			else{
				panel.msgbox.setMessage("Invalid value given"+WAIT);
				panel.msgbox.instant();
				panel.setGUI(panel.msgbox);
				return;
			}
		}
		panel.setGUI(panel.hud);
	}
	
	private void cmdGoto(List<String> args){
		if(args.size() != 3){
			panel.msgbox.setMessage("Error: param count"+WAIT);
			panel.msgbox.instant();
			panel.setGUI(panel.msgbox);
			return;
		}
		int x = Integer.decode(args.get(1));
		int y = Integer.decode(args.get(2));
		panel.getPlayer().setPos(x, y);
		panel.setGUI(panel.hud);
	}
	
	private void cmdHelp(List<String> args){
		if(args.size() == 1){
			panel.msgbox.setMessage("Commands: gui, noclip, close, set, fill, save,\ndebug, goto, help, quit\n\nDo 'help <command>' for more information.");
			panel.setGUI(panel.msgbox);
			return;
		}else{
			String cmd = args.get(1);
			String message;
			if(cmd.equals("gui")){
				if(args.size() > 2){
					String gui = args.get(2);
					if(gui.equals("msgbox"))
						message = "gui msgbox [message]\nOpens the message box with the\ngiven message.";
					else if(gui.equals("input"))
						message = "gui input\nOpens the user-input box and waits until\nenter is pressed.";
					else if(gui.equals("console"))
						message = "This does not work as you are already\nin the console.";
					else if(gui.equals("hud"))
						message = "gui hud\nThis effectively just closes the console.";
					else message = gui+" is not a recognized GUI name.";
				}else{
					message = "gui <msgbox|input|console|hud> [args]\nOpens the specified GUI.";
				}
			}else if(cmd.equals("noclip")){
				message = "noclip [state]\nToggles collision for player.";
			}else if(cmd.equals("close")){
				message = "close (no other arguments)\nCloses the console.";
			}else if(cmd.equals("set")){
				if(args.size() > 2){
					String cmd2 = args.get(2);
					if(cmd2.equals("tile")){
						message = "set tile [at] <tile x> <tile y> [layer] <new tile>\nChanges a tile on the current map."+WAIT+
								"set tile from <x1> <y1> [to] <x2> <y2>\n  [layer] [to] <new tile>\nChanges an area of tiles.";
					}else if(cmd2.equals("map")){
						message = "set map <map name>\nChanges the current map.";
					}else if(cmd2.equals("gui")){
						message = "set gui <gui name>\nOpens the specified gui.";
					}else if(cmd2.matches("\\w+.\\w+")){
						message = "set <object>.<variable> <value>\nChanges an object's properties.";
					}else{
						message = "set <variable> <value>\nChanges a game value.";
					}
				}else{
					message = "set <tile|map|gui|...> <value>";
				}
			}else if(cmd.equals("fill")){
				message = "fill <x1> <y1> [to] <x2> <y2> [layer] <new tile>\nChanges an area of tiles on the current map.";
			}else if(cmd.equals("save")){
				if(args.size() > 2){
					String value = args.get(2);
					if(value.equals("map"))
						message = "save map (no other arguments)\nSaves the current map with any changes\nto its files.";
					else if(value.equals("game"))
						message = "save game (no other arguments)\nSaves the game.";
					else
						message = "Unknown value '"+value+"'.";
				}else{
					message = "save <map|game>\nSaves something.";
				}
			}else if(cmd.equals("debug")){
				message = "debug <state>\nChanges whether debug information is shown.\n'minimal' allows terminal output but does not draw\nto screen.";
			}else if(cmd.equals("goto")){
				message = "goto <x> <y>\nTeleports the player to the specified\nx/y coordinates, if they are in bounds.";
			}else if(cmd.equals("help")){
				message = "help [command]\nDisplays this message.";
			}else if(cmd.equals("quit")){
				message = "quit\nQuits the game and closes the app.\nDoes not save.";
			}else{
				message = "Unknown command '"+cmd+"'.";
			}
			message += WAIT;
			
			panel.msgbox.setMessage(message);
			panel.setGUI(panel.msgbox);
		}
	}
	
	@Override
	public void reset(){
		super.reset();
		setMessage("§o> §0");
		Panel panel = Panel.getInstance();
	}
	
	public static List<String> parseArgs(String str){
		List<String> list = new ArrayList<>();
		list.add("");
		boolean inString = false;
		for(char c : str.toCharArray()){
			if(c == '"'){
				inString = !inString;
			}else if(c == ' ' && !inString){
				list.add("");
			}else{
				list.set(list.size()-1, list.get(list.size()-1)+c);
			}
		}
		return list;
	}
}
