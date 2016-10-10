package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;

public class Msg extends ScriptStep{
	private List<String> args;
	public Msg(List<String> args){
		this.args = args;
		this.executed = false;
	}
	public void execute(Scope script){
		if(executed) return;
		List<String> newargs = new ArrayList<String>(args);
		script.evalArgs(newargs);
		String result = "";
		for(int i = 0; i < newargs.size(); i++){
			String arg = newargs.get(i);
			result += arg;
			if(i != newargs.size()-1) result += " ";
		}
		System.out.println(result);
		this.executed = true;
	}
}