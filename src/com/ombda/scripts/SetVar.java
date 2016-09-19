package com.ombda.scripts;

import java.util.Arrays;

import com.ombda.Facing;
import com.ombda.Map;
import com.ombda.NPC;
import com.ombda.Panel;
import com.ombda.Player;
import com.ombda.Sprite;
import static com.ombda.Debug.*;
public class SetVar implements ScriptStep{
	private String[] args;
	public SetVar(String[] a){
		args = a;
	}
	private RuntimeException invalidLength(int length){
		return new RuntimeException("Invalid number of arguments given for script step : set (expected:"+length+",found:"+args.length);
	}
	private void testLength(int length){
		if(args.length < length)
			throw invalidLength(length);
	}
	@Override
	public void execute(Panel game, Script script){
		if(args[1].equals("sprite")){
			testLength(4);
			args[2] = Script.parseString(args[2]);
			int i = args[2].indexOf('.');
			if(i == -1)
				throw new RuntimeException("Invalid script step: expected mapname, id and variable at index 2 of SetVar sprite");
			String map_name;
			if(args[2].matches("(0x|#)?[A-Za-z\\d]+\\.(x|y|map)")){
				i = -1;
				map_name = game.getPlayer().getMap().toString();
			}else map_name = script.evalVar(args[2].substring(0, i));
			int j = args[2].indexOf('.',i+1);
			if(j == -1)
				throw new RuntimeException("Invalid script step: expected mapname, id and variable at index 2 of SetVar sprite");
			int hash = Script.parseInt(args[2].substring(i+1, j));
			String var = args[2].substring(j+1);
			Map sprite_map = Map.get(map_name);
			Sprite sprite = sprite_map.getSprite(hash);
			if(var.equals("map")){
				String mapname;
				if(Script.isString(args[3])){
					mapname = Script.parseString(args[3]);
				}else
					mapname = script.evalVar(args[3]);
				Map map = Map.get(mapname);
				sprite.setMap(map);
			}else if(var.equals("x")){
				int x = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				sprite.setPos(x, sprite.y);
			}else if(var.equals("y")){
				int y = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				sprite.setPos(sprite.x, y);
			}else if(var.equals("pos")){
				testLength(5);
				int x = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				int y = Script.parseInt(script.evalVar(Script.parseString(args[4])));
				sprite.setPos(x,y);
			}else throw new RuntimeException("Invalid sprite var: "+var);
		}else if(args[1].equals("npc")){
			testLength(4);
			//String npcInfoAndVar = args[2];
			args[2] = Script.parseString(args[2]);
			int i = args[2].indexOf('.');
			if(i == -1){
				throw new RuntimeException("Invalid script step: expected id and variable at index 2 of SetVar npc");
			}
			int hash = Script.parseInt(args[2].substring(0, i));
			String var = args[2].substring(i+1);
			NPC npc = NPC.getNPC(hash);
			if(var.equals("map")){
				String mapname;
				if(Script.isString(args[3])){
					mapname = Script.parseString(args[3]);
				}else
					mapname = script.evalVar(args[3]);
				Map map = Map.get(mapname);
				npc.setMap(map);
			}else if(var.equals("x")){
				int x = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				npc.setPos(x, npc.y);
			}else if(var.equals("y")){
				int y = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				npc.setPos(npc.x, y);
			}else if(var.equals("onInteracted")){
				String str = "";
				for(int q = 3; q < args.length; q++){
					str += args[q];
					if(q != args.length-1)
						str += ' ';
				}
				debug("set ncp oninteract");
				npc.onInteractedScript = new Script(Arrays.asList(Script.loadStep(str)));
			}else if(var.equals("pos")){
				testLength(5);
				int x = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				int y = Script.parseInt(script.evalVar(Script.parseString(args[4])));
				npc.setPos(x,y);
			}else if(var.equals("dest")){
				testLength(5);
				int x = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				int y = Script.parseInt(script.evalVar(Script.parseString(args[4])));
				npc.setDestination(x, y);
			}else if(var.equals("xy")){
				testLength(5);
				int x = Script.parseInt(script.evalVar(Script.parseString(args[3])));
				int y = Script.parseInt(script.evalVar(Script.parseString(args[4])));
				npc.setDestination(x, y);
				npc.setPos(x,y);
			}else if(var.equals("facing")){
				String facing = script.evalVar(Script.parseString(args[3]));
				Facing dir = Facing.fromString(facing.toUpperCase());
				npc.setDirection(dir);
			}else throw new RuntimeException("Invalid npc var: "+var);
		}else if(args[1].equals("player.x")){
			testLength(3);
			int x = Script.parseInt(args[2]);
			Player p = game.getPlayer();
			p.setPos(x, p.y);
		}else if(args[1].equals("player.y")){
			testLength(3);
			int y = Script.parseInt(args[2]);
			Player p = game.getPlayer();
			p.setPos(p.x, y);
		}else if(args[1].equals("player.pos")){
			testLength(4);
			int x = Script.parseInt(args[2]);
			int y = Script.parseInt(args[3]);
			game.getPlayer().setPos(x, y);
		}else if(args[1].equals("player.facing")){
			testLength(3);
			String facing = script.evalVar(Script.parseString(args[3]));
			Facing dir = Facing.fromString(facing.toUpperCase());
			game.getPlayer().setDirection(dir);
		}else if(args.length == 3 && !args[1].equals("map") && !args[1].equals("player.map")){
			script.setVar(Script.parseString(args[1]),script.evalVar(Script.parseString(args[2])));
		}else if(args.length == 5 && (Script.parseString(args[2]).equals("add") || args[2].equals("+"))){
			String arg1s = script.evalVar(Script.parseString(args[3]));
			String arg2s = script.evalVar(Script.parseString(args[4]));
			try{
				int arg1 = Script.parseInt(arg1s);
				int arg2 = Script.parseInt(arg2s);
				script.setVar(Script.parseString(args[1]), Integer.toString(arg1+arg2));
			}catch(NumberFormatException e){
				script.setVar(Script.parseString(args[1]), arg1s+arg2s);
			}
		}else if(args.length == 5 && (Script.parseString(args[2]).equals("subtract") || args[2].equals("-"))){
			String arg1s = script.evalVar(Script.parseString(args[3]));
			String arg2s = script.evalVar(Script.parseString(args[4]));
			int arg1 = Script.parseInt(arg1s);
			int arg2 = Script.parseInt(arg2s);
			script.setVar(Script.parseString(args[1]), Integer.toString(arg1-arg2));
		}else if(args.length == 5 && (Script.parseString(args[2]).equals("multiply") || args[2].equals("*"))){
			String arg1s = script.evalVar(Script.parseString(args[3]));
			String arg2s = script.evalVar(Script.parseString(args[4]));
			int arg1 = Script.parseInt(arg1s);
			int arg2 = Script.parseInt(arg2s);
			script.setVar(Script.parseString(args[1]), Integer.toString(arg1*arg2));
		}else if(args.length == 5 && (Script.parseString(args[2]).equals("divide") || args[2].equals("/"))){
			String arg1s = script.evalVar(Script.parseString(args[3]));
			String arg2s = script.evalVar(Script.parseString(args[4]));
			int arg1 = Script.parseInt(arg1s);
			int arg2 = Script.parseInt(arg2s);
			script.setVar(Script.parseString(args[1]), Integer.toString(arg1/arg2));
		}else if((args.length == 4 || args.length == 5)&& Script.parseString(args[2]).equals("sub")){
			//begin
			String arg1s = script.evalVar(Script.parseString(args[3]));
			//end
			int start, end;
			start = Script.parseInt(arg1s);
			String value = script.evalVar(Script.parseString(args[1]));
			if(args.length == 6)
				end = Script.parseInt(script.evalVar(Script.parseString(args[4])));
			else
				end = value.length();
			script.setVar(Script.parseString(args[1]), value.substring(start,end));
		}else{
			game.console.executeCommand(Arrays.asList(args));
		}
	}

	@Override
	public boolean done(){
		return true;
	}

	//format: set <string literal : varname> <string : value>
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("set");
		//if(args.length != 3) throw new RuntimeException("Expected 2 arguments passed to script step: set (got:"+Arrays.toString(args)+")");
		return new SetVar(args);
	}
}
