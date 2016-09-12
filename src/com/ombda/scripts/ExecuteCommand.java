package com.ombda.scripts;

import java.util.ArrayList;
import java.util.List;

import com.ombda.Panel;

public class ExecuteCommand implements ScriptStep{
	private List<String> args;
	private boolean noEval = false;
	public ExecuteCommand(List<String> args){
		if(args.contains("*noEval")){
			noEval = true;
			args.remove("*noEval");
		}
		this.args = args;
	}
	@Override
	public void execute(Panel game, Script script){
		for(int i = 1; i < args.size(); i++){
			String arg = args.get(i);
			if(arg.startsWith("\"") && arg.endsWith("\""))
				arg = Script.parseString(arg);
			else if(!(arg.equals("player.x") || arg.equals("player.y")))
				arg = script.evalVar(Script.parseString(arg));
			args.set(i, arg);
		}
		game.console.executeCommand(args);
	}

	@Override
	public boolean done(){
		return true;
	}

	
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("cmd");
		List<String> list = new ArrayList<>();
		for(String s : args)
			list.add(s);
		list.remove(0);
		return new ExecuteCommand(list);
	}
}
