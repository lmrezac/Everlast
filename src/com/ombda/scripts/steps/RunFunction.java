package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Function;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;

public class RunFunction extends ScriptStep{
	private String varname;
	private List<String> args;
	public RunFunction(List<String> args){
		if(args.isEmpty()) throw new RuntimeException("script step : function must be passed 2 arguments.");
		varname = evalVarName(args,false);
		this.args = args;
	}
	public void execute(Scope scopeIn){
		List<String> newargs = new ArrayList<>(args);
		scopeIn.evalArgs(newargs);
		String value = scopeIn.getVar(varname,scopeIn);
		if(!value.startsWith(Script.REF))
			throw new RuntimeException("value passed to script step : script must be a scope!");
		Scope scope = Scope.getId(value);
		if(!(scope instanceof Function))
			throw new RuntimeException("Value passed to script step : script MUST be a script!");
		Function func = (Function)scope;
		func.call(scopeIn,newargs);
	}
}
