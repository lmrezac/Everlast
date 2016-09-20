package com.ombda.scripts;

import java.util.ArrayList;
import java.util.List;

import com.ombda.Panel;

public class If implements ScriptStep{
	private List<String> condition;
	private boolean result = false;
	private If(List<String> args){
		condition = args;
	}
	@Override
	public void execute(Panel game, Script script){
		if(condition.isEmpty()) throw new RuntimeException("Script step : if needs a condition!");

		if(condition.get(0).equals("equals")){
			if(condition.size() != 3) throw new RuntimeException("script step : if needs 2 values after equals.");
			String str1 = script.evalVar(condition.get(1));
			String str2 = script.evalVar(condition.get(2));
			result = str1.equals(str2);
		}else if(condition.get(0).equals("notequals")){
			if(condition.size() != 3) throw new RuntimeException("script step : if needs 2 values after notequals.");
			String str1 = script.evalVar(condition.get(1));
			String str2 = script.evalVar(condition.get(2));
			result = !str1.equals(str2);
		}else if(condition.size() == 3){
			int x = Integer.parseInt(script.evalVar(Script.parseString(condition.get(0))));
			int y = Integer.parseInt(script.evalVar(Script.parseString(condition.get(2))));
			if(condition.get(1).equals("<"))
				result = x < y;
			else if(condition.get(1).equals("<="))
				result = x <= y;
			else if(condition.get(1).equals(">"))
				result = x > y;
			else if(condition.get(1).equals(">="))
				result = x >= y;
			else if(condition.get(1).equals("="))
				result = x == y;
			else if(condition.get(1).equals("!="))
				result = x != y;
			else throw new RuntimeException("Invalid operator : "+condition.get(1)+" for script step : if");
		}else if(condition.get(0).equals("not")){
			if(condition.size() != 2) throw new RuntimeException("script step : if needs 1 value after not.");
			result = script.evalVar(condition.get(1)).equals("0");
		}else{
			if(condition.size() != 1) throw new RuntimeException("script step : if needs 1 value, got : "+condition);
			result = !script.evalVar(condition.get(0)).equals("0");
		}
		
	}

	@Override
	public boolean done(){
		return result;
	}
	
	public static If loadFromString(String[] args){
		
		if(args.length == 1) throw new RuntimeException("If requires at least one argument");
		List<String> list = new ArrayList<>();
		for(int i = 1; i < args.length; i++) list.add(args[i]);
		return new If(list);
	}

}
