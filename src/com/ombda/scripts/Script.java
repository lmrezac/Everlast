package com.ombda.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.ombda.Panel;
import com.ombda.scripts.steps.*;

public class Script extends Scope{
	private static HashMap<String,Script> scripts = new HashMap<>();
	public static final String REF = "ǂ";
	protected Scope currentScope;
	private List<Scope> pastScopes = new ArrayList<>();
	protected List<ScriptStep> steps;
	public int index = 0;
	private String description;
	public Script(List<ScriptStep> steps){
		this(steps,true);
	}
	public Script(String desc, List<ScriptStep> steps){
		this(desc,steps,true);
	}
	public Script(List<ScriptStep> steps, boolean functions){
		this(null,steps,functions);
	}
	public Script(String desc,List<ScriptStep> steps,boolean functions){
		super(functions);
		this.steps = steps;	
		this.currentScope = this;
		this.description = desc;
		if(desc != null)
			scripts.put(desc, this);
	}
	public void reset(){
		index = 0;
		this.currentScope = this;
	}
	public void execute(Scope script){
		if(index == steps.size()){
				throw new RuntimeException("Script was not reset!");
		}
		//System.out.println("executing script");
		ScriptStep step = steps.get(index);
		if(step instanceof Msg && step.done()){
			index++;
			if(index != steps.size())
				execute(script);
			return;
		}
		Panel.getInstance().step = step.getClass().getSimpleName();
		if(index+1 < steps.size()){
			if(steps.get(index+1) instanceof Msg){
				Panel.getInstance().msgbox.closeWhenDone = false;
			}
		}else Panel.getInstance().msgbox.closeWhenDone = true;
		step.execute(script);
		if(step.done()){
			index++;
		}
	}
	public boolean done(){
		return index == steps.size();
	}
	public void run(){
		System.out.println("called!");
		new Exception().printStackTrace();
		while(!done()){
			execute(this.currentScope);
		}
	}
	public void setScope(Scope scope){
		pastScopes.add(this.currentScope);
		this.currentScope = scope;
	}
	public void exitScope(){
		if(pastScopes.size() != 0)
			this.currentScope = pastScopes.remove(pastScopes.size()-1);
	}
	public static List<String> scanLine(String line){
		
		List<String> result = new ArrayList<>();
		for(int i = 0; i < line.length(); i++){
			if(line.charAt(i) == '"'){
				int j = line.indexOf('"',i+1);
				if(j == -1) throw new RuntimeException("Unclosed string literal at index "+i+" in string "+line);
				if(j+1 < line.length() && line.charAt(j+1) != ' ') throw new RuntimeException("Expected space after closing \"");
				result.add(line.substring(i,j+1));
				i = j+1;
			}else{
				int depth = 0; int j;
				for(j = i; j < line.length() && (line.charAt(j) != ' ' || depth != 0); j++){
					if(j != 0 && line.charAt(j) == '{' && line.charAt(j-1) == '$') depth++;
					else if(line.charAt(j) == '}') depth--;
					if(depth < 0) depth = 0;
				}
				if(depth > 0) throw new RuntimeException("Unclosed variable in string "+line);
				result.add(line.substring(i,j).trim());
				i = j;
			}
		}
		return result;
	}
	public static Script compile(String name,final List<String> lines){
		for(int index = 0; index < lines.size(); index++){
			String line = lines.get(index).trim();
			if(line.startsWith("#") || line.isEmpty()){
				lines.remove(index);
				index--;
			}else{
				boolean instring = false;
				for(int i = 0; i < line.length(); i++){
					if(line.charAt(i) == '"') instring = !instring;
					if(!instring && line.substring(i).startsWith("--")){
						line = line.substring(0,i);
						break;
					}
				}
				if(line.isEmpty()){
					lines.remove(index);
					index--;
				}else
					lines.set(index,line);
			}
		}
		List<ScriptStep> steps = new ArrayList<>();
		for(int i = 0; i < lines.size(); i++){
			String line = lines.get(i).trim();
			if(line.contains(Script.REF))
				throw new RuntimeException("Illegal character "+Script.REF+" encountered at index "+line.indexOf(Script.REF)+" on line "+i);
			lines.set(i,line);
		}
		for(int i = 0; i < lines.size(); i++){
			String line = lines.get(i);
			List<String> args = scanLine(line);
			String cmd = args.remove(0);
			
			i = createStep(i,cmd,args,lines,steps);
		}
		return new Script(name,steps);
	}
	private static int createStep(int i, String cmd, List<String> args, final List<String> lines, List<ScriptStep> steps){
		if(args.size() - 3 >= 0 && args.get(args.size()-3).equals("new") && args.get(args.size()-2).equals("function")){
			List<ScriptStep> substeps = new ArrayList<>();
		//	int oldi = i;
			String end = "end function";
			for(i = i+1; i < lines.size(); i++){
				if(lines.get(i).equals(end)) break;
				List<String> subargs = scanLine(lines.get(i));
				String subcmd2 = subargs.remove(0);
				i = createStep(i,subcmd2,subargs,lines,substeps);
			}
			if(i == lines.size() && !lines.get(i).equals(end)) throw new RuntimeException("No corresponding end statement found for inline function creation");
			assert lines.get(i).equals(end);
			String arglist = args.remove(args.size()-1);
			if(!arglist.startsWith("(") && !arglist.endsWith(")")) throw new RuntimeException("In script step : set script function : variable list MUST be enclosed by parenthesis ()!");
			arglist = arglist.substring(1,arglist.length()-1);
			Scanner scan = new Scanner(arglist);
			scan.useDelimiter(",");
			List<String> argnames = new ArrayList<>();
			while(scan.hasNext()) argnames.add(scan.next());
			scan.close();
			args.remove(args.size()-1);
			args.set(args.size()-1,new Function(argnames,substeps).getIdStr());
		}
		if(cmd.equals("msg"))
			steps.add(new Msg(args));
		else if(cmd.equals("npc"))
			steps.add(new CreateNPC(args));
		else if(cmd.equals("sprite"))
			steps.add(new CreateSprite(args));
		else if(cmd.equals("collideable"))
			steps.add(new CreateCollideable(args));
		else if(cmd.equals("wait"))
			steps.add(new Wait(args));
		else if(cmd.equals("set")){
			if(args.isEmpty()) throw new RuntimeException("script step : set needs a minimum of two arguments.");
			String subcmd = args.get(0);
			boolean finalvar = false;
			if(subcmd.equals("final")){
				finalvar = true;
				args.remove(0);
				subcmd = args.get(0);
			}
			if(subcmd.equals("script")){
				args.remove(0);
				int k = 0;
				if(args.isEmpty()) throw new RuntimeException("script step : set script needs a minimum of one argument, followed by lines of code, followed by a corresponding end statement.");
				String name = "script";
				if(args.size() > 1 && args.get(0).equals("function")){
					name += " function";
					if(args.size() < 2)
						throw new RuntimeException("script step : set script function needs exactly 2 arguments: a name and variable list enclosed in parenthesis, followed by lines of code, followed by a corresponding end statement.");
					name += " "+args.get(1);
					if(name.endsWith("class")){
						if(args.size() == 2) throw new RuntimeException("Expected object name following class.");
						k = 2;
						name += args.get(2);
						while(k < args.size() && name.endsWith("class")){
							name += " "+args.get(i);
							k++;
						}
					}else name = Script.evalString(name);
				}else name += " "+args.get(k);
				String end = "end "+name;
				List<ScriptStep> substeps = new ArrayList<>();
				for(i = i+1; i < lines.size(); i++){
					if(lines.get(i).equals(end)) break;
					List<String> subargs = scanLine(lines.get(i));
					String subcmd2 = subargs.remove(0);
					i = createStep(i,subcmd2,subargs,lines,substeps);
				}
				if(i == lines.size() && !lines.get(i).equals(end)) throw new RuntimeException("No corresponding end statement found for scope creation : "+name);
				assert lines.get(i).equals(end);
				
				steps.add(new SetScript(finalvar,args,substeps));
			}else
				steps.add(new Set(finalvar,args));
		}
		else if(cmd.equals("scope"))
			steps.add(new SetScope(args));
		else if(cmd.equals("script"))
			steps.add(new RunScript(args));
		else if(cmd.equals("function"))
			steps.add(new RunFunction(args));
		else if(cmd.equals("delete"))
			steps.add(new DeleteVar(args));
		else if(cmd.equals("return"))
			steps.add(new Return(args));
		else if(cmd.equals("define")){
			if(args.isEmpty()) throw new RuntimeException("script step : set needs a minimum of two arguments.");
			String subcmd = args.get(0);
			boolean finalvar = false, isStatic = false, isPublic = true;
			if(subcmd.equals("public")){
				isPublic = true;
				args.remove(0);
				subcmd = args.get(0);
			}else if(subcmd.equals("private")){
				isPublic = false;
				args.remove(0);
				subcmd = args.get(0);
			}
			if(subcmd.equals("static")){
				isStatic = true;
				args.remove(0);
				subcmd = args.get(0);
			}
			if(subcmd.equals("final")){
				finalvar = true;
				args.remove(0);
				subcmd = args.get(0);
			}
			if(subcmd.equals("script")){
				args.remove(0);
				int k = 0;
				if(args.isEmpty()) throw new RuntimeException("script step : set script needs a minimum of one argument, followed by lines of code, followed by a corresponding end statement.");
				String name = "script";
				if(args.size() > 1 && args.get(0).equals("function")){
					name += " function";
					if(args.size() < 2)
						throw new RuntimeException("script step : set script function needs exactly 2 arguments: a name and variable list enclosed in parenthesis, followed by lines of code, followed by a corresponding end statement.");
					name += " "+args.get(1);
					if(name.endsWith("class") || name.endsWith("operator")){
						if(args.size() == 2) throw new RuntimeException("Expected object name following class.");
						k = 2;
						name += " "+args.get(2);
						while(k < args.size() && (name.endsWith("class") || name.endsWith("operator"))){
							name += " "+args.get(k);
							k++;
						}
					}else name = Script.evalString(name);
				}else name += " "+args.get(k);
				String end = "end "+name;
				List<ScriptStep> substeps = new ArrayList<>();
				for(i = i+1; i < lines.size(); i++){
					if(lines.get(i).equals(end)) break;
					List<String> subargs = scanLine(lines.get(i));
					String subcmd2 = subargs.remove(0);
					i = createStep(i,subcmd2,subargs,lines,substeps);
				}
				if(i == lines.size() && !lines.get(i).equals(end)) throw new RuntimeException("No corresponding end statement found for scope creation : "+name);
				assert lines.get(i).equals(end);
				
				steps.add(new DefineScript(finalvar,isStatic,isPublic,args,substeps));
			}else
				steps.add(new Define(finalvar,isStatic,isPublic,args));
		}
		else if(cmd.equals("struct")){
			String name = "struct ";
			
			name += args.get(i);
			
			for(int j = i+1; name.endsWith("class") && j < args.size(); j++){
				name += ' '+args.get(j);
			}
			String end = "end "+name;
			List<ScriptStep> substeps = new ArrayList<>();
			for(i = i+1; i < lines.size(); i++){
			//	System.out.println("lines = '"+lines.get(i)+"' end = '"+end+"' == "+lines.get(i).equals(end));
				if(lines.get(i).equals(end)) break;
				List<String> subargs = scanLine(lines.get(i));
				String subcmd2 = subargs.remove(0);
				i = createStep(i,subcmd2,subargs,lines,substeps);
			}
			if(i == lines.size() && !lines.get(i).equals(end)) throw new RuntimeException("No corresponding end statement found for scope creation : "+name);
			assert lines.get(i).equals(end);
			steps.add(new CreateStruct(args,substeps));
		}
		else if(cmd.equals("assert"))
			steps.add(new Assert(args));
		else if(cmd.equals("if")){
			int depth = 0;
			List<ScriptStep> trueSteps = new ArrayList<>(), falseSteps = new ArrayList<>();
			boolean elseEncountered = false;
			if(i+1 < lines.size() && args.get(args.size()-1).equals("then")){
				
				args.remove(args.size()-1);
				for(i = i+1; i < lines.size(); i++){
					if(lines.get(i).equals("end if") && depth == 0) break;
					else if(lines.get(i).equals("end if")) depth--;
					else if(lines.get(i).startsWith("if ") && lines.get(i).endsWith(" then")) depth++;
					else if(lines.get(i).equals("else do") && depth == 0){
						elseEncountered = true;
						i++;
					}else if(lines.get(i).equals("else")){
						i++;
						List<String> subargs = scanLine(lines.get(i));
						String subcmd2 = subargs.remove(0);
						i = createStep(i,subcmd2,subargs,lines, falseSteps);
						break;
					}
					List<String> subargs = scanLine(lines.get(i));
					String subcmd2 = subargs.remove(0);
					i = createStep(i,subcmd2,subargs,lines, elseEncountered? falseSteps : trueSteps);
				}
			}else{
				if(i+1 >= lines.size())
					throw new RuntimeException("Statement expected after if.");
				i++;
				List<String> subargs = scanLine(lines.get(i));
				String subcmd2 = subargs.remove(0);
				i = createStep(i,subcmd2,subargs,lines,trueSteps);
			}
			steps.add(new If(args,trueSteps,falseSteps));
		}else if(cmd.equals("while")){
			List<ScriptStep> substeps = new ArrayList<>();
			if(i+1 < lines.size() && args.get(args.size()-1).equals("do")){
				args.remove(args.size()-1);
				int depth = 0;
				for(i = i+1; i < lines.size(); i++){
					if(lines.get(i).equals("end while") && depth == 0) break;
					else if(lines.get(i).equals("end while")) depth--;
					else if(lines.get(i).startsWith("while ") && lines.get(i).endsWith(" do")) depth++;
					List<String> subargs = scanLine(lines.get(i));
					String subcmd2 = subargs.remove(0);
					i = createStep(i,subcmd2,subargs,lines, substeps);
				}
			}else{
				if(i+1 >= lines.size())
					throw new RuntimeException("Statement expected after if.");
				i++;
				List<String> subargs = scanLine(lines.get(i));
				String subcmd2 = subargs.remove(0);
				i = createStep(i,subcmd2,subargs,lines,substeps);
			}
			steps.add(new While(args,substeps));
		}
		else if(cmd.equals("end"))
			throw new RuntimeException("Invalid end statement : "+lines.get(i)+" : check your names!");
		else{
			if(!isReservedWord(cmd)){
				args.add(0,cmd);
				steps.add(new RunFunction(args));
			}else throw new RuntimeException("There is no script step of name "+cmd+"; line = "+lines.get(i));
		}
		return i;
	}
	public static Script getScript(String name){
		return scripts.get(name);
	}
	public static boolean exists(String name){
		return scripts.containsKey(name);
	}
	
	public static void loadVars(String line){
		if(line.charAt(0) != '_') throw new RuntimeException("Invalid line "+line);
		if(globalScope == null) throw new RuntimeException("Global scope not initialized yet!");
		line = line.substring(1);
		while(!line.isEmpty()){
			if(line.charAt(0) != '"') throw new RuntimeException("Invalid line "+line);
			int i = line.indexOf('"',1);
			if(i == -1) throw new RuntimeException("Invalid line "+line);
			String name = line.substring(1,i);
			if(line.charAt(i+1) != '=') throw new RuntimeException("Invalid line "+line);
			line = line.substring(i+2);
			if(line.charAt(0) != '"') throw new RuntimeException("Invalid line "+line);
			i = line.indexOf('"',1);
			if(i == -1) throw new RuntimeException("Invalid line "+line);
			if(i+1 < line.length() && line.charAt(i+1) != ' ') throw new RuntimeException("Invalid line "+line);
			String value = line.substring(1,i);
			globalScope.setVar(name, value, globalScope);
			if(i+2 >= line.length()) break;
			line = line.substring(i+2);
		}
	}
	public static String saveVars(){
		String result = "_";
		for(String name : globalScope.vars.keySet()){
			if(!globalScope.finalvars.get(name) && !globalScope.vars.get(name).startsWith(Script.REF))
				result += '"'+name+"\"=\""+globalScope.vars.get(name)+"\" ";
		}
		if(result.length() > 1) result = result.substring(0,result.length()-1);
		System.out.println(result);
		return result;
	}
	public String toString(){
		return description;
	}
}


