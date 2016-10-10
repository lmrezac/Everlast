package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;

public class If extends ScriptStep{
	private List<String> args;
	private List<ScriptStep> trueSteps, falseSteps;
	public If(List<String> args,List<ScriptStep> steps,List<ScriptStep> elsesteps){
		if(args.isEmpty()) throw new RuntimeException("Expected at least one argument for script step : if");
		this.args = args;
		this.trueSteps = steps;
		this.falseSteps = elsesteps;
	}
	public void execute(Scope scope){
		System.out.println("If scope = "+scope.toString());
		List<String> newargs = new ArrayList<>(args);
		scope.evalArgs(newargs);
		if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : if do not evaluate into one argument : "+newargs);
		if(newargs.get(0).equals("0"))
			for(ScriptStep step : falseSteps)
				step.execute(scope);
		else for(ScriptStep step : trueSteps)
				step.execute(scope);
	}
}
