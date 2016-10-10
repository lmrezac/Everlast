package com.ombda.scripts.steps;

import java.util.List;

import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;
import com.ombda.scripts.VarNotExists;

public class SetScope extends ScriptStep{
	private String varname;
	public SetScope(List<String> args){
		if(args.isEmpty()) throw new RuntimeException("script step : scope must be passed exactly 1 argument.");
		varname = evalVarName(args,false);
		if(!args.isEmpty()) throw new RuntimeException("Too many arguments passed to script step : scope");
	}
	public void execute(Scope scopeIn){
		Script script;
		while(!(scopeIn instanceof Script)){
			try{
				String id = scopeIn.getVar("outer",scopeIn);
				scopeIn = Scope.getId(id);
			}catch(VarNotExists ex){
				throw new RuntimeException("Cannot call script step : scope on non-script value");
			}
		}
		script = (Script)scopeIn;
		if(Script.evalString(varname).equals("outer") || Script.evalString(varname).equals("${outer}")){
			script.exitScope();
			return;
		}
		Scope scope;
		try{
			String value = script.getVar(varname,scopeIn);
			if(!value.startsWith(Script.REF))
				throw new RuntimeException("Value passed to script step : scope MUST be a scoped variable.");
			scope = Scope.getId(value);
			try{
				String idstr = script.getVar("outer",scopeIn);
				if(idstr.startsWith(Script.REF)){
					Scope outer = Scope.getId(idstr);
					if(outer == scope){
						script.exitScope();
						return;
					}
				}
			}catch(VarNotExists ex){}
		}catch(VarNotExists ex){
			scope = new Scope();
			script.setVar(varname,scope.getIdStr(),scopeIn);
		}
		if(scope.getClass() != Scope.class)
			throw new RuntimeException("Value passed to script step : scope MUST be a scope, it cannot be an object or function.");
		scope.setVar("outer",script.getIdStr(),scopeIn);
		script.setScope(scope);
	}
}
