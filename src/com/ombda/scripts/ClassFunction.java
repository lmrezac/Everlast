package com.ombda.scripts;

import java.util.List;

import com.ombda.scripts.steps.ScriptStep;

public class ClassFunction extends Function{
	public ClassFunction(List<String> args,List<ScriptStep> steps){
		super(args,steps,true);
	}
	public void setObject(Struct struct){
		this.caller = struct;
	}
	public Struct getObject(){ return (Struct)this.caller; }
	public String call(Scope script,List<String> values){
		returnedValue = "0";
		this.currentScope = this;
		if(values.size() != args_length())
			throw new RuntimeException("Invalid number of arguments passed");
		for(int i = 0; i < args_length(); i++){
			String varname = this.args.get(i);
			String value = values.get(i);
			this.setVar(varname,value,this);
		}
		setFinalVar("outer",script.getIdStr(),this);
		setFinalVar("this",this.caller.getIdStr(),this);
		for(ScriptStep step : this.steps)
			step.execute(this.currentScope);
		this.vars.clear();
		return returnedValue;
	}
}
