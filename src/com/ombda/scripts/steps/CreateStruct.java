package com.ombda.scripts.steps;

import java.util.List;

import com.ombda.scripts.Scope;
import com.ombda.scripts.StructTemplate;

public class CreateStruct extends ScriptStep{
	private String structname;
	private List<ScriptStep> steps;
	public CreateStruct(List<String> args,List<ScriptStep> steps){
		structname = evalVarName(args,false);
		if(!args.isEmpty()) throw new RuntimeException("Too many arguments passed to script step : struct");
		this.steps = steps;
	}
	public void execute(Scope script){
		String name = structname;
		if(name.startsWith("class ")){
			name = name.substring(6);
			int i = name.lastIndexOf('.');
			if(i != -1){
				name = name.substring(i+1);
				if(name.startsWith("class ")) name = name.substring(6);
			}
		}
		StructTemplate template = new StructTemplate(name,false);
		for(ScriptStep step : steps){
			step.execute(template);
		}
		script.setFinalVar(structname,template.getIdStr(),script);
		
	}
}