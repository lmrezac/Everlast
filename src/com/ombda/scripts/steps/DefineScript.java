package com.ombda.scripts.steps;

import java.util.List;

import com.ombda.scripts.ClassFunctionTemplate;
import com.ombda.scripts.Scope;
import com.ombda.scripts.StructTemplate;

public class DefineScript extends SetScript{
	private boolean isStatic = false, isPublic = false, finalVar;
	public DefineScript(boolean finalVar,boolean isStatic, boolean isPublic,List<String> args, List<ScriptStep> steps){
		super(finalVar,args,steps);
		this.isStatic = isStatic;
		this.isPublic = isPublic;
		this.finalVar = finalVar;
	}
	public void execute(Scope script){
		if(!(script instanceof StructTemplate))
			throw new RuntimeException("Can only use define within a Struct template");
		StructTemplate template = (StructTemplate)script;
		ClassFunctionTemplate thing;
		if(function) thing = new ClassFunctionTemplate(args,steps);
		else throw new RuntimeException("must be a function");
		if(isStatic){
			String id = thing.asFunction().getIdStr();
			if(this.finalVar)
				template.setFinalVar(varname,id,script);
			else template.setVar(varname,id,script);
			if(!template.isClass && !isPublic) throw new RuntimeException("Cannot set private variable in struct.");
			template.staticPublicVars.put(varname,isPublic);
		}else{
			if(finalvar) template.defineFinalFunc(varname,isPublic,thing);
			else template.defineFunc(varname,isPublic,thing);
		}
	}
}
