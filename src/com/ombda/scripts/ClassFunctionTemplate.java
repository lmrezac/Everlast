package com.ombda.scripts;

import java.util.List;

import com.ombda.scripts.steps.ScriptStep;

public class ClassFunctionTemplate{
	private List<String> args;
	private List<ScriptStep> steps;
	public ClassFunctionTemplate(List<String> args, List<ScriptStep> steps){this.args = args;this.steps = steps;}
	public ClassFunction fill(Struct struct){ClassFunction func = new ClassFunction(args,steps);func.setObject(struct);return func;}
	public Function asFunction(){ return new Function(args,steps,true); }
}
