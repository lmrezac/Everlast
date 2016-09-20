package com.ombda.scripts;

import java.util.Arrays;

import com.ombda.Panel;

public class RunScript implements ScriptStep{
	private String scriptName;
	
	public RunScript(String scriptName){
		this.scriptName = scriptName;
	}
	@Override
	public void execute(Panel game, Script script){
		Script s = getScript();
		s.execute(game);
	}
	private Script getScript(){
		String name = Script.parseString(scriptName);
		return Script.getScript(name);
	}
	@Override
	public boolean done(){
		return getScript().done();
	}


	//format: script <string : script name>
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("script");
		if(args.length != 2) throw new RuntimeException("Expected 1 argument passed to script step: script (got:"+Arrays.toString(args)+")");
		return new RunScript(args[1]);
	}
}
