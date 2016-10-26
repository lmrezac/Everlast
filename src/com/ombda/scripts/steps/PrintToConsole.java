package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.gui.MessageBox;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;
import com.ombda.Debug;
public class PrintToConsole extends ScriptStep{
	private List<String> args;
	public PrintToConsole(List<String> args){
		this.args = args;
	}
	@Override
	public void execute(Scope script){
		List<String> newargs = new ArrayList<>(args);
		script.evalArgs(newargs);
		String result = "";
		for(int i = 0; i < newargs.size(); i++){
			String arg = newargs.get(i);
			if(arg.equals("*char")){
				i++;
				String chs = newargs.get(i);
				int c = Script.parseInt(chs);
				result += (char)c;
			}else if(arg.equals("*line")){
				result += '\n';
			}else{
				result += arg;
				
				if(i != newargs.size()-1)
					result += ' ';
			}
		}
		Debug.parent.println(result);
	}

}
