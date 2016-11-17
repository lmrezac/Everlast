package com.ombda;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import main.Main;
public class ScriptThread extends Thread{
	private static List<ScriptThread> instances = new ArrayList<>();
	private String script;
	private static Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler(){
		@Override
		public void uncaughtException(Thread arg0, Throwable arg1){
			if(arg1 instanceof FatalError)
				Main.fatalError((FatalError)arg1);
			Main.fatalError(new FatalError(arg1));
		}
	};
	public ScriptThread(String script){
		this.script = script;
		instances.add(this);
		this.setUncaughtExceptionHandler(exceptionHandler);
	}
	public void run(){
		try{
			Panel.getInstance().scriptEngine.eval(script);
		}catch(ScriptException e){
			debug("Error running script "+script);
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
