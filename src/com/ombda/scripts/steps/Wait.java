package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;
import com.ombda.scripts.VarNotExists;
import static com.ombda.Debug.debug;
public class Wait extends ScriptStep{
	private List<String> args;
	private double waiting = 0, time = -1;
	private boolean until = false, done = false;
	public Wait(List<String> args){
		if(args.isEmpty()) throw new RuntimeException("Script step: wait needs at least 1 argument");
		if(args.get(0).equals("until")){
			until = true;
			args.remove(0);
		}
		this.args = args;
	}
	@Override
	public void execute(Scope script) {
		if(until){
			List<String> newargs = new ArrayList<>(args);
			try{
			script.evalArgs(newargs);
			}catch(VarNotExists ex){
				throw new VarNotExists(ex.getMessage()+" args = "+newargs,ex);
			}
		
			if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step: wait do not evaluate into a single value");
			if(args.size() == 1)
			debug("args = "+args+" newargs  = "+newargs);
			if(!newargs.get(0).equals("0")){
				done = true;
			}
			return;
		}
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
		if(until){
			if(done){
				done = false;
				return true;
			}
			return false;
		}
		if(waiting != 0 && time >= waiting){
			waiting = 0;
			time = -1;
			return true;
		}
		return false;
	}

}
