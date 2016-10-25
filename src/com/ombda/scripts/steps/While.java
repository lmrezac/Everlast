package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;
import static com.ombda.Debug.debug;

public class While extends ScriptStep{
	private List<String> args;
	private List<ScriptStep> steps;
	private int index = -1;
	private boolean done = false;
	public While(List<String> args, List<ScriptStep> steps){
		if(args.isEmpty()) throw new RuntimeException("Expected at least one argument for script step : if");
		this.args = args;
		this.steps = steps;
		index = steps.size();
	}
	public void execute(Scope scope){
		if(index == steps.size()){
			List<String> newargs = new ArrayList<>();
			
			newargs.clear();
			newargs.addAll(this.args);
			scope.evalArgs(newargs);
			if(newargs.size() != 1)
				throw new RuntimeException("Arguments passed to script step : while do not evaluate into one argument.");
			if(newargs.get(0).equals("0"))
				done = true;
			if(!done)
				index = 0;
		}else{
			steps.get(index).execute(scope);
			if(steps.get(index).done())
				index++;
		}
	}
	public boolean done(){
		if(done){
			index = steps.size();
			done = false;
			return true;
		}
		return false;
	}
}
