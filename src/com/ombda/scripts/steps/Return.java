package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Function;
import com.ombda.scripts.Scope;

public class Return extends ScriptStep{
	private List<String> args;
	public Return(List<String> args){
		this.args = args;
	}
	public void execute(Scope scopeIn){
		if(!(scopeIn instanceof Function))
			throw new RuntimeException("Tried to return a value outside a function.");
		Function func = (Function)scopeIn;
		List<String> newargs = new ArrayList<>(args);
		scopeIn.evalArgs(newargs);
		if(newargs.size() != 1)
			throw new RuntimeException("Arguments passed to script step : return did not evaluate into a single value.");
		func.returnedValue = newargs.get(0);
		func.returned = true;
	}
}
