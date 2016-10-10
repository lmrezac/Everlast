package com.ombda.scripts.steps;

import java.util.List;

import com.ombda.scripts.Scope;

public class DeleteVar extends ScriptStep{
	private List<String> args;
	public DeleteVar(List<String> args){
		if(args.isEmpty()) throw new RuntimeException("script step : delete must be passed at least one argument.");
		this.args = args;
	}
	public void execute(Scope scopeIn){
		Scope currentScope = scopeIn.getCurrentScope();
		String varname = "";
		for(int i = 0; i < args.size(); i++){
			varname += args.get(i);
			if(i != args.size()-1) varname += " ";
		}
		if(varname.startsWith("function ")) varname = varname.substring(9);
		currentScope.deleteVar(varname);
	}
}
