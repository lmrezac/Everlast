package com.ombda.scripts.steps;

import java.util.List;

import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;

public class RunScript extends ScriptStep{
	private String varname;
	public RunScript(List<String> args){
		if(args.isEmpty()) throw new RuntimeException("script step : script must be passed exactly 1 argument.");
		varname = evalVarName(args,true);
		if(!args.isEmpty()) throw new RuntimeException("Too many arguments passed to script step : script");
	}
	public void execute(Scope scopeIn){
		String value = scopeIn.evalVars(varname);
		if(!value.startsWith(Script.REF))
			throw new RuntimeException("value passed to script step : script must be a scope!");
		Scope scope = Scope.getId(value);
		if(scope.getClass() != Script.class)
			throw new RuntimeException("Value passed to script step : script MUST be a script!");
		Script script = (Script)scope;
		script.setScope(scopeIn);
		script.run();
		script.exitScope();
	}
}
