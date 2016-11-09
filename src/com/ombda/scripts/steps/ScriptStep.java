package com.ombda.scripts.steps;

import java.util.List;

import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;
@Deprecated
public abstract class ScriptStep {
	protected boolean executed = true;
	public abstract void execute(Scope script);

	public boolean done() {
		return executed;
	}

	protected static String evalVarName(List<String> args, boolean operator) {
		String varname = args.remove(0);
		if (varname.equals("class") || varname.equals("operator")) {
			varname += " " + args.remove(0);
			while (!args.isEmpty() && (varname.endsWith(".class") || (operator && varname.endsWith(".operator")))) {
				if (args.size() == 1)
					throw new RuntimeException(
							"Expected object name following class" + (operator ? "/operator" : "") + ".");
				varname += " " + args.remove(0);
			}
		}else if(varname.equals("npc") || varname.equals("sprite")){
			varname += " "+args.remove(0);
		} else
			varname = Script.evalString(varname);
		return varname;
	}
}
