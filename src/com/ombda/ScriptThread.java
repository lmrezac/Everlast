package com.ombda;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;
public class ScriptThread extends Thread{
	private static List<ScriptThread> instances = new ArrayList<>();
	private String script;
	public ScriptThread(String script){
		this.script = script;
		instances.add(this);
	}
	public void run(){
		try{
			Panel.getInstance().scriptEngine.eval(script);
		}catch(ScriptException e){
			debug("Error running script");
			if(printStackTrace)
				e.printStackTrace();
			throw new FatalError();
		}
	}
	public static void stopAll(){
		for(ScriptThread thread : instances){
			thread.interrupt();
			
		}
	}
}
