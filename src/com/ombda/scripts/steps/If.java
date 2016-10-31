package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;
import static com.ombda.Debug.debug;
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
		//debug("If scope = "+scope.toString());
		List<String> newargs = new ArrayList<>(args);
		scope.evalArgs(newargs);
		debug("If: args = "+args+" result = "+newargs);
		if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : if do not evaluate into one argument : "+newargs);
		if(newargs.get(0).equals("0"))
			for(int i = 0; i < falseSteps.size(); i++){
				ScriptStep step = falseSteps.get(i);
				step.execute(scope);
			}
		else for(int i = 0; i < trueSteps.size(); i++){
			ScriptStep step = trueSteps.get(i);
			step.execute(scope);
		}	
	}
}
