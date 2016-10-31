package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ombda.scripts.Function;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;
import static com.ombda.Debug.debug;
public class SetScript extends ScriptStep{
	protected List<ScriptStep> steps;
	protected boolean function;
	protected List<String> args = null;
	protected String varname;
	protected boolean finalvar;
	public SetScript(boolean b,List<String> args,List<ScriptStep> params){
		this.finalvar = b;
		this.steps = params;
		if(args.isEmpty()) throw new RuntimeException("script step : set script must take a minimum of one argument.");
		if(this.function = args.get(0).equals("function")){
			args.remove(0);
			if(args.size() < 2) throw new RuntimeException("script step : set script function must take a minimum of two arguments.");
		}
		varname = evalVarName(args,true);
		if(function){
			if(args.isEmpty()) throw new RuntimeException("script step : set script function expects the last parameter to be a variable list in the form of parenthesis containing optional variable names separated by commas.");
			String arglist = args.remove(0);
			if(!arglist.startsWith("(") && !arglist.endsWith(")")) throw new RuntimeException("In script step : set script function : variable list MUST be enclosed by parenthesis ()!");
			arglist = arglist.substring(1,arglist.length()-1);
			Scanner scan = new Scanner(arglist);
			scan.useDelimiter(",");
			this.args = new ArrayList<>();
			while(scan.hasNext()) this.args.add(scan.next());
			scan.close();
		}else if(!args.isEmpty()) throw new RuntimeException("too many arguments passed to script step : set script function "+args);
	}
	public void execute(Scope script){
		Script thing;
		if(function) thing = new Function(args,steps);
		else thing = new Script(steps);
		if(finalvar) script.setFinalVar(varname,thing.getIdStr(),script);
		else script.setVar(varname,thing.getIdStr(),script);
		debug("function created: "+varname+" "+thing.getId()+" in scope "+script.getId());
	}
}