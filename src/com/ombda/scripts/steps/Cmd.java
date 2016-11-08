package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.Panel;
import com.ombda.scripts.Scope;

public class Cmd extends ScriptStep{
	private List<String> args;
	public Cmd(List<String> args){
		this.args = args;
	}
	@Override
	public void execute(Scope script){
		List<String> newargs = new ArrayList<String>(args);
		script.evalArgs(newargs);
		Panel panel = Panel.getInstance();
		panel.console.executeCommand(newargs);
	}

}
