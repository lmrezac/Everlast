package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;

public class Assert extends ScriptStep{
	private List<String> args;
	public Assert(List<String> args){
		if(args.isEmpty()) throw new RuntimeException("script step : assert must take at least one argument.");
		this.args = args;
	}
	public void execute(Scope scope){
		List<String> newargs = new ArrayList<>(args);
		scope.evalArgs(newargs);
		if(newargs.size() <= 0)
			throw new RuntimeException("script step : assert takes one or two arguments only.");
		if(newargs.get(0).equals("0")){
			if(newargs.size() > 1){
				newargs.set(0,"Assertation failure ");
				new Msg(newargs).execute(scope);
			}else System.out.println("Assertation failure ");
			throw new Error();
		}
	}
}
