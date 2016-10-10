package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;

public class While extends ScriptStep{
	private List<String> args;
	private List<ScriptStep> steps;
	public While(List<String> args, List<ScriptStep> steps){
		if(args.isEmpty()) throw new RuntimeException("Expected at least one argument for script step : if");
		this.args = args;
		this.steps = steps;
	}
	public void execute(Scope scope){
		boolean notdone = true;
		List<String> newargs = new ArrayList<>();
		do{
			newargs.clear();
			newargs.addAll(this.args);
			scope.evalArgs(newargs);
			if(newargs.size() != 1)
				throw new RuntimeException("Arguments passed to script step : if do not evaluate into one argument.");
			if(newargs.get(0).equals("0")){
				notdone = false;
			}else{
				for(ScriptStep step : steps)
					step.execute(scope);
			}
		}while(notdone);
	}
}
