package com.ombda.scripts.steps;

import java.util.ArrayList;
import java.util.List;

import com.ombda.scripts.Scope;
import com.ombda.scripts.StructTemplate;

public class Define extends ScriptStep{
	private boolean isStatic = false, isPublic = true, finalVar;
	private String varname;
	private String defaultValue = null;
	private List<String> args;
	public Define(boolean finalVar,boolean isStatic,boolean isPublic,List<String> args){
		this.finalVar = finalVar;
		this.isStatic = isStatic;
		this.isPublic = isPublic;
		varname = evalVarName(args,false);
		this.args = args;
	}
	public void execute(Scope script){
		if(!(script instanceof StructTemplate))
			throw new RuntimeException("Can only use define within a Struct template");
		StructTemplate template = (StructTemplate)script;
		if(defaultValue == null){
			if(args.isEmpty()){
				defaultValue = "0";
			}else{
				List<String> newargs = new ArrayList<String>(args);
				template.evalArgs(newargs);
				if(newargs.size() != 1) throw new RuntimeException("Arguments passed to script step : define do not evaluate into a single argument!");
				defaultValue = newargs.get(0);
			}
		}
		if(isStatic){
			if(finalVar)
				template.setFinalVar(varname,defaultValue,script);
			else{
				template.setVar(varname,defaultValue,script);
			//	System.out.println("Set variable "+varname+" in struct "+template.getName()+" to "+defaultValue);
			}
			if(!template.isClass && !isPublic) throw new RuntimeException("Cannot set private variable in struct.");
			template.staticPublicVars.put(varname,isPublic);
		}else{
			template.defineVar(varname,isPublic);
		}
	}
}
