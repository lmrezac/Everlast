package com.ombda.scripts;

import static com.ombda.Debug.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.ombda.Map;
import com.ombda.NPC;
import com.ombda.Panel;
import com.ombda.Sprite;

public class Script{
	private static HashMap<String,Script> scripts = new HashMap<>();
	private static HashMap<String,String> vars = new HashMap<>();
	private List<ScriptStep> steps;
	private int pos = 0, lastPos = -1;
	private String description;
	public Script(String desc,List<ScriptStep> list){
		this.steps = list;
		this.description = desc;
		scripts.put(description, this);
	}
	public Script(List<ScriptStep> list){
		this.steps = list;
		this.description = "null";
	}
	private ScriptStep currentStep(){ return steps.get(pos); }
	private boolean done = false;
	public void execute(Panel game){
		if(pos >= steps.size())
			return;//throw new RuntimeException("Script is already completed! Cannot increment step!");
		ScriptStep step = currentStep();
	
		if(pos+1 < steps.size()){
			if(steps.get(pos+1) instanceof DispMessage){
				game.msgbox.closeWhenDone = false;
			}else if(steps.get(pos+1) instanceof If && pos+2 < steps.size() && steps.get(pos+2) instanceof DispMessage){
				game.msgbox.closeWhenDone = false;
			}
		}else game.msgbox.closeWhenDone = true;
		 
		step.execute(game, this);
		if(step instanceof If){
			if(step.done()){
				lastPos = pos;
				pos++;
			}else{
				lastPos = pos;
				pos+=2;
			}
		}else{
			if(step.done()){
				lastPos = pos;
				pos++;
			}
		}
		
	}
	public void reset(){
		done = false;
		pos = 0;
		
	}
	public boolean done(){
		if(pos >= steps.size()){
			reset();
			return true;
		}
		return false;
	}
	/*public boolean done(){
		if(currentScriptPos == -1){
			currentScriptPos = 0;
			return true;
		}
		return false;
	}*/
	public String toString(){
		return description;
	}
	public static String saveVars(){
		String result = "_";
		List<String> keys = new ArrayList<>(vars.keySet());
		for(int i = 0; i < keys.size(); i++){
			String str = keys.get(i);
			result += '"'+str+"\"=\""+vars.get(str)+"\"";
			if(i != keys.size()-1)
				result += ' ';
		}
		return result;
	}
	public static void loadVars(String str){
		str = str.substring(1).trim();
		if(str.equals("")) return;
		Scanner scan = new Scanner(str);
		scan.useDelimiter(" ");
		while(scan.hasNext()){
			String value = scan.next();
			int i = value.indexOf('"',1);
			if(value.charAt(i+1) != '=') throw new RuntimeException("Expected = at index "+(i+1)+" in string "+value);
			vars.put(value.substring(1,i),value.substring(i+3,value.length()-1));
		}
	}
	
	public static Script getScript(String name){
		Script s = scripts.get(name);
		if(s == null)
			throw new RuntimeException("Couldn't find script "+name);
		return s;
	}
	public static boolean exists(String name){
		return scripts.containsKey(name);
	}
	
	public static Script load(String name, List<String> lines){
		List<ScriptStep> list = new ArrayList<>();
		for(String str : lines){
			if(!str.startsWith("#"))
				list.add(loadStep(str));
		}
		return new Script(name,list);
	}
	public static ScriptStep loadStep(String line){
		debug("loading line: "+line);
		String[] args = scanLine(line);
		if(args[0].equals("sprite"))
			return CreateSprite.loadFromString(args);
		else if(args[0].equals("msg"))
			return DispMessage.loadFromString(args);
		else if(args[0].equals("script"))
			return RunScript.loadFromString(args);
		else if(args[0].equals("cmd"))
			return ExecuteCommand.loadFromString(args);
		else if(args[0].equals("set"))
			return SetVar.loadFromString(args);
		else if(args[0].equals("npc"))
			return CreateNPC.loadFromString(args);
		else if(args[0].equals("if"))
			return If.loadFromString(args);
		else 
			throw new RuntimeException("Invalid script step: "+line+" (args="+Arrays.toString(args)+")");
	}
	private static int nextIndexOf(String str, char search, int startIndex){
		int depth = 0;
		assert search != '{' && search != '}';
		for(int i = startIndex; i < str.length(); i++){
			char c = str.charAt(i);
			if(c == '{') depth++;
			else if(c == '}') depth--;
			else if(c == search && depth == 0)
				return i;
		}
		return -1;
	}
	public static String[] scanLine(String line){
		List<String> list = new ArrayList<>();
		boolean inString = false;
		String str = "";
		line = line.trim();
		for(int i = 0; i < line.length(); i++){
	
			if(line.charAt(i) == '"'){
				int j = nextIndexOf(line,'"',i+1);
				list.add(line.substring(i+1,j));
				i = j+1;
			}else{
				int j = nextIndexOf(line,' ',i+1);
				if(j == -1) j = line.length();
				list.add(line.substring(i,j));
				i = j;
			}
			if(i < line.length())
			if(line.charAt(i) != ' ')
				throw new RuntimeException("Error parsing arguments for script step; line = "+line+", pos = "+i+" (expected whitespace here)");
			//while(Character.isWhitespace(line.charAt(i))) i++;
			
		}
		return list.toArray(new String[list.size()]);
	}
	public String evalVar(String str){
		if(str.equals("player.x"))
			return ""+Panel.getInstance().getPlayer().x;
		if(str.equals("player.y"))
			return ""+Panel.getInstance().getPlayer().y;
		if(str.equals("player.map") || str.equals("<current map>"))
			return Panel.getInstance().getPlayer().getMap().toString();
		if(vars.containsKey(str))
			return vars.get(str);
		int i;
		while((i = str.indexOf("${")) != -1){
			int j = str.indexOf('}',i);
			if(j == -1) return str;
			String name = str.substring(i+2,j);
			String var;
			if(name.startsWith("npc ")){
				String name2 = name.substring(4);
				int index = name2.indexOf('.');
				if(index == -1)
					throw new RuntimeException("Invalid variable: "+name2);
				int hash = parseInt(name2.substring(4, index));
				NPC npc = NPC.getNPC(hash);
				String value = name2.substring(index+1);
				if(value.equals("x"))
					var = Integer.toString((int)npc.x);
				else if(value.equals("y"))
					var = Integer.toString((int)npc.y);
				else if(value.equals("map"))
					var = npc.getMap().toString();
				else throw new RuntimeException("Invalid script variable: "+name2);
			}else if(name.startsWith("sprite ")){
				String name2 = name.substring(7);
				int i2 = name2.indexOf('.');
				if(i2 == -1)
					throw new RuntimeException("Invalid script sprite variable: expected mapname, id and variable : "+name2);
				
				String map_name;
				if(name2.matches("(0x|#)?[A-Za-z\\d]+\\.(x|y)")){
					map_name = Panel.getInstance().getPlayer().getMap().toString();
					i2 = -1;
				}else{
					map_name = evalVar(name2.substring(0, i2));
				}
				int j2 = name2.indexOf('.',i2+1);
				debug("j2 = "+j2+" name = "+name2+" i2 = "+i2);
				if(j2 == -1)
					throw new RuntimeException("Invalid script sprite variable: expected mapname, id and variable : "+name2);
				int hash = Script.parseInt(name2.substring(i2+1, j2));
				String value = name2.substring(j2+1);
				Map sprite_map = Map.get(map_name);
				Sprite sprite = sprite_map.getSprite(hash);
				if(value.equals("x"))
					var = Integer.toString((int)sprite.x);
				else if(value.equals("y"))
					var = Integer.toString((int)sprite.y);
				else throw new RuntimeException("Invalid script variable: "+name);
			}else if(name.equals("player.x")){
				return ""+Panel.getInstance().getPlayer().x;
			}else if(name.equals("player.y")){
				return ""+Panel.getInstance().getPlayer().y;
			}else if(name.equals("player.map") || name.equals("current_map")){
				return Panel.getInstance().getPlayer().getMap().toString();
			}else{
				var = vars.get(name);
				if(var == null){
					var = "0";
					vars.put(name, var);
				}
			}
			str = str.replace("${"+name+"}",var);
		}
		return str;
	}
	public void setVar(String var, String val){
		vars.put(var, val);
	}
	public static int parseInt(String str){
		str = parseString(str);
		if(str.endsWith("t")){
			return 16*Integer.decode(str.substring(0, str.length()-1));
		}else return Integer.decode(str);
	}
	public static String parseString(String str){
		if(str.startsWith("\"")){
			if(!str.endsWith("\""))
				throw new RuntimeException("Expected string to start with AND end with quotations.");
			return str.substring(1, str.length()-1);
		}
		if(str.endsWith("\"")){
			throw new RuntimeException("Expected string to end with AND start with quotations.");
		}
		return str;
	}
	public static boolean isString(String s){
		return s.startsWith("\"") && s.endsWith("\"");
	}
}
