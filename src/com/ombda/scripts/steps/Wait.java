package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;

public class Wait extends ScriptStep{
	private List<String> args;
	private double waiting = 0, time = -1;
	public Wait(List<String> args){
		this.args = args;
	}
	@Override
	public void execute(Scope script) {
		if(waiting == 0){
			List<String> newargs = new ArrayList<>(args);
			script.evalArgs(newargs);
			if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step: wait do not evaluate into a single value!");
			String arg = args.get(0);
			if(arg.endsWith("s")){
				long fps = (System.currentTimeMillis()-com.ombda.Panel.getInstance().lastFrame);
				arg = arg.substring(0,arg.length()-1);
				waiting = Double.parseDouble(arg)*fps;
			}else{
				waiting = Double.parseDouble(arg);
			}
				
			time = 0;
		}else{
			time++;
		}
	}
	@Override
	public boolean done(){
		if(waiting != 0 && time >= waiting){
			waiting = 0;
			time = -1;
			return true;
		}
		return false;
	}

}
