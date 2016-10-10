package com.ombda.scripts;

import java.util.List;

public class ClassStruct extends Struct{
	private List<Boolean> publicVars;
	public ClassStruct(StructTemplate type,List<String> definedVars, List<Boolean> publicVars){
		super(type,definedVars);
		this.publicVars = publicVars;
	}
	public String getVar(String varname,Scope scope){
		String var = varname;
		if(var.contains(".")) var = var.substring(0,var.indexOf("."));
		if(var.startsWith("class ")) var = var.substring(6);
		if(definedVars.contains(var) && !publicVars.get(definedVars.indexOf(var)) && scope != this)
			throw new RuntimeException("Cannot get var "+var+", it is private.");
		return super.getVar(varname,scope);
	}
	public void setVar(String varname, String value, boolean isfinal, Scope scope){
		String var = varname;
		if(var.contains(".")) var = var.substring(0,var.indexOf("."));
		if(var.startsWith("class ")) var = var.substring(6);
		if(definedVars.contains(var) && !publicVars.get(definedVars.indexOf(var)) && scope != this)
			throw new RuntimeException("Cannot set var "+var+", it is private.");
		super.setVar(varname, value, isfinal, scope);
	}
}