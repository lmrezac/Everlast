package com.ombda.scripts;

import java.util.List;

import com.ombda.scripts.steps.ScriptStep;

public class Function extends Script{
	protected List<String> args;
	public Scope caller;
	public String returnedValue;
	public boolean returned = false;
	public Function(List<String> args,List<ScriptStep> steps){
		this(args,steps,true);
	}
	public Function(List<String> args,List<ScriptStep> steps,boolean functions){
		super(steps,functions);
		this.args = args;
		
	}
	public int args_length(){
		return args.size();
	}
	public String call(Scope script,List<String> values){
		returnedValue = "0";
		this.currentScope = this;
		this.caller = script;
		if(values.size() != args_length())
			throw new RuntimeException("Invalid number of arguments passed");
		for(int i = 0; i < args_length(); i++){
		//	System.out.println("i = "+i+" args = "+this.args+" id = "+this.getId());
			String varname = this.args.get(i);
		//	System.out.println("varname = "+varname);
			String value = values.get(i);
		//	System.out.println("value = "+value);
			this.setVar(varname,value,this);
		}
		setFinalVar("outer",this.caller.getIdStr(),this);
		for(ScriptStep step : this.steps){
			step.execute(this.currentScope);
		}
		this.vars.clear();
		this.finalvars.clear();
		return returnedValue;
	}
	public boolean done(){
		return this.returned;
	}
}
