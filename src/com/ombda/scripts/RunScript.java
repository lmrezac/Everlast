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
		String name = Script.parseString(scriptName);
		if(!(scriptName.startsWith("\"") && scriptName.endsWith("\"")))
			name = script.evalVar(name);
		Script s = Script.getScript(name);
		s.run();
	}

	@Override
	public boolean done(){
		return true;
	}


	//format: script <string : script name>
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("script");
		if(args.length != 2) throw new RuntimeException("Expected 1 argument passed to script step: script (got:"+Arrays.toString(args)+")");
		return new RunScript(args[1]);
	}
}
