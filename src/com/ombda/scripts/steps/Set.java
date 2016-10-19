package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Tile;
import com.ombda.entities.NPC;
import com.ombda.entities.Sprite;
import com.ombda.scripts.FacingStruct;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;
import com.ombda.scripts.Struct;

public class Set extends ScriptStep{
	private String varname;
	private boolean index = false;
	private List<String> args;
	private boolean finalvar;
	public Set(boolean b,List<String> args){ 
		finalvar = b;
		if(args.get(0).equals("[]")){index = true;args.remove(0);}
		varname = evalVarName(args,false);
		if(args.isEmpty()) throw new RuntimeException("Script step : set needs a 2nd value");
		this.args = args;
	}
	public void execute(Scope script){
		List<String> newargs = new ArrayList<String>(args);
		script.evalArgs(newargs);
		
		if(varname.startsWith("npc ")){
			if(index) throw new RuntimeException("Syntax");
			varname = varname.substring(4);
			int i = varname.indexOf('.');
			if(i == -1) throw new RuntimeException("Expected . in npc setter");
			int id = Script.parseInt(varname.substring(0,i));
			NPC npc = NPC.getNPC(id);
			varname = varname.substring(i+1);
			if(varname.equals("map")){
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set npc map do not evaluate into a single value.");
				String mapname = newargs.get(0);
				Map map = Map.get(mapname);
				npc.setMap(map);
			}else if(varname.equals("facing")){
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set npc facing do not evaluate into a single value.");
				String value = newargs.get(0);
				if(!value.startsWith(Script.REF))
					throw new RuntimeException("Expected facing value, got : "+value);
				Scope scope = Scope.getId(value);
				if(!(scope instanceof Struct))
					throw new RuntimeException("Expected facing value, got : "+value);
				Struct struct = (Struct)scope;
				if(struct.getType() != Scope.facing_type)
					throw new RuntimeException("Expected facing value, got : "+value);
				FacingStruct facing = (FacingStruct)struct;
				npc.setDirection(facing.facing);
			}else if(varname.equals("x")){
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set npc x do not evaluate into a single value.");
				int x = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				npc.x = x;
				System.out.println("Set npc "+Integer.toHexString(id)+" x to "+x);
			}else if(varname.equals("y")){
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set npc x do not evaluate into a single value.");
				int y = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				npc.y = y;
				System.out.println("Set npc "+Integer.toHexString(id)+" y to "+y);
			}else if(varname.equals("pos")){
				if(newargs.size() != 2) throw new RuntimeException("Arguments passed to script step : set ncp pos do not evaluate into 2 values.");
				int x = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				int y = (Tile.SIZE/16) * Script.parseInt(newargs.get(1));
				npc.setPos(x, y);
				System.out.println("Set npc "+Integer.toHexString(id)+" pos to ("+x+","+y+")");
			}else if(varname.equals("dest")){
				if(newargs.size() != 2) throw new RuntimeException("Arguments passed to script step : set ncp pos do not evaluate into 2 values.");
				int x = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				int y = (Tile.SIZE/16) * Script.parseInt(newargs.get(1));
				npc.setDestination(x,y);
				System.out.println("Set npc "+Integer.toHexString(id)+" dest to ("+x+","+y+")");
			}else if(varname.equals("onInteracted")){
				if(newargs.isEmpty() || newargs.get(0).equals("null")){
					npc.onInteractedScript = null;
				}else{
					String line = "";
					for(int j = 0; j < newargs.size(); j++){
						line += newargs.get(j);
						if(j != newargs.size()-1) line += " ";
					}
					List<String> lines = new ArrayList<>();
					lines.add(line);
					npc.onInteractedScript = Script.compile(null,lines);
				}
			}else if(varname.equals("onUpdate")){
				if(newargs.isEmpty() || newargs.get(0).equals("null")){
					npc.updateScript = null;
				}else{
					String line = "";
					for(int j = 0; j < newargs.size(); j++){
						line += newargs.get(j);
						if(j != newargs.size()-1) line += " ";
					}
					List<String> lines = new ArrayList<>();
					lines.add(line);
					npc.updateScript = Script.compile(null,lines);
				}
			}else if(varname.equals("xy")){
				if(newargs.size() != 2) throw new RuntimeException("Arguments passed to script step : set ncp pos do not evaluate into 2 values.");
				int x = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				int y = (Tile.SIZE/16) * Script.parseInt(newargs.get(1));
				npc.setPos(x, y);
				npc.setDestination(x, y);
				System.out.println("Set npc "+Integer.toHexString(id)+" pos and dest to ("+x+","+y+")");
			}else throw new RuntimeException("Invalid npc varname: "+varname);
		}else if(varname.startsWith("sprite ")){
			if(index) throw new RuntimeException("Syntax");
			varname = varname.substring(7);
			int i = varname.indexOf('.');
			if(i == -1) throw new RuntimeException("Expected . in npc setter");
			int q = varname.indexOf('.',i+1);
			int p = varname.indexOf('.',q+1);
			Sprite sprite;
			int id;
			if(p == -1){
				id = Script.parseInt(varname.substring(0,q));
				sprite = Panel.getInstance().getPlayer().getMap().getSprite(id);
				varname = varname.substring(q+1);
			}else{
				String mapname = varname.substring(0,i);
				id = Script.parseInt(varname.substring(q+1,p));
				sprite = Map.get(mapname).getSprite(id);
				varname = varname.substring(p+1);
			}
			if(varname.equals("map")){
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set npc map do not evaluate into a single value.");
				String mapname = newargs.get(0);
				Map map = Map.get(mapname);
				sprite.setMap(map);
			}else if(varname.equals("x")){
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set npc x do not evaluate into a single value.");
				int x = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				sprite.x = x;
				System.out.println("Set sprite "+Integer.toHexString(id)+" x to "+x);
			}else if(varname.equals("y")){
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set npc x do not evaluate into a single value.");
				int y = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				sprite.y = y;
				System.out.println("Set sprite "+Integer.toHexString(id)+" y to "+y);
			}else if(varname.equals("pos")){
				if(newargs.size() != 2) throw new RuntimeException("Arguments passed to script step : set ncp pos do not evaluate into 2 values.");
				int x = (Tile.SIZE/16) * Script.parseInt(newargs.get(0));
				int y = (Tile.SIZE/16) * Script.parseInt(newargs.get(1));
				sprite.setPos(x, y);
				System.out.println("Set npc "+Integer.toHexString(id)+" pos to ("+x+","+y+")");
			}else throw new RuntimeException("Invalid sprite varname: "+varname);
		}else if(index){
			if(newargs.size() != 2) throw new RuntimeException("Arguments passed to script step : set [] do not evaluate into an index and a value");
			if(finalvar) throw new RuntimeException("Cannot set a final index.");
			if(!varname.startsWith("class ")) varname = "class "+varname;
			script.setVar(varname+"."+newargs.get(0),newargs.get(1),script);
		}else{
			if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : set do not evaluate into a single value! "+newargs);
			if(finalvar) script.setFinalVar(varname,newargs.get(0),script);
			else script.setVar(varname,newargs.get(0),script);
		}
	}
}
