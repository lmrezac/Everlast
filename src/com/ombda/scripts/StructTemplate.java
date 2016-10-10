package com.ombda.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StructTemplate extends Scope{
	private static HashMap<String,StructTemplate> interns = new HashMap<>();
	protected List<String> definedVars = new ArrayList<>();
	protected List<Boolean> publicVars = new ArrayList<>();
	protected HashMap<String,ClassFunctionTemplate> definedFuncs = new HashMap<>();
	protected HashMap<String,Boolean> finalFuncs = new HashMap<>();
	protected HashMap<String,Boolean> publicFuncs = new HashMap<>();
	public HashMap<String,Boolean> staticPublicVars = new HashMap<>();
	private String name;
	public boolean isClass = false;
	public StructTemplate(String name,boolean isClass){
		super(false);
		this.isClass = isClass;
		this.name = name;
		interns.put(name,this);
	}
	public String getName(){ return name; }
	public void defineFunc(String name,boolean isPublic,ClassFunctionTemplate template){
		if(!isClass && !isPublic) throw new RuntimeException("Cannot declare a private variable in a struct!");
		if(definedFuncs.containsKey(name) || definedVars.contains(name))
			throw new RuntimeException("Function "+name+" is already defined!");
		definedFuncs.put(name,template);
		finalFuncs.put(name,false);
		publicFuncs.put(name,isPublic);
		definedVars.add(name);
		publicVars.add(isPublic);
	}
	public void defineFinalFunc(String name,boolean isPublic,ClassFunctionTemplate template){
		if(!isClass && !isPublic) throw new RuntimeException("Cannot declare a private variable in a struct!");
		if(definedFuncs.containsKey(name) || definedVars.contains(name))
			throw new RuntimeException("Function "+name+" is already defined!");
		definedFuncs.put(name,template);
		finalFuncs.put(name,true);
		publicFuncs.put(name,isPublic);
		definedVars.add(name);
		publicVars.add(isPublic);
	}
	public void setVar(String varname, String value, boolean isFinal,Scope scope){
		this.setVar(varname,value,isFinal,Thread.currentThread().getStackTrace()[2].getClassName(),scope);
	}
	public void setFinalVar(String varname, String value,Scope scope){
		this.setVar(varname,value,true,Thread.currentThread().getStackTrace()[2].getClassName(),scope);
	}
	public void setVar(String varname, String value,Scope scope){
		this.setVar(varname,value,false,Thread.currentThread().getStackTrace()[2].getClassName(),scope);
	}
	private void setVar(String varname, String value,boolean isFinal,String className,Scope scopeIn){
		String var = varname;
		if(var.contains(".")) var = var.substring(0,var.indexOf("."));
		if(var.startsWith("class ")) var = var.substring(6);
		if((!className.startsWith("Define") && !this.vars.containsKey(var)) && scopeIn != this)
			throw new RuntimeException("Variable "+var+" does not exist ");
		super.setVar(varname,value,isFinal,scopeIn);
	}
	public void defineVar(String varname,boolean isPublic){
		if(!isClass && !isPublic) throw new RuntimeException("Cannot declare a private variable in a struct!");
		String var = varname;
		if(var.contains(".")) throw new RuntimeException("Cannot define sub-vars");
		definedVars.add(var);
		publicVars.add(isPublic);
	}
	public int args_length(){ return definedVars.size(); }
	public <C extends Struct> C fill(C struct,List<String> values){
		for(int i = 0; i < definedVars.size(); i++)
			if(!definedFuncs.containsKey(definedVars.get(i)))
				struct.setVar(definedVars.get(i),i < values.size()? values.get(i) : "0",struct);
		for(String funcname : definedFuncs.keySet()){
			ClassFunction func = definedFuncs.get(funcname).fill(struct);
			if(finalFuncs.get(funcname))
				struct.setFinalVar(funcname,func.getIdStr(),struct);
			else struct.setVar(funcname,func.getIdStr(),struct);
		}
		return struct;
	}
	public ClassStruct createNewClass(List<String> values){
		if(!isClass) throw new RuntimeException("Tried to call createNewClass on struct!");
		return fill(new ClassStruct(this,this.definedVars,this.publicVars),values);
	}
	public Struct createNewStruct(List<String> values){
		if(isClass) throw new RuntimeException("Tried to call createNewStruct on class!");
		return fill(new Struct(this,this.definedVars),values);
	}
	public String getVar(String varname,Scope scopeIn){
		String var = varname;
	//	String className = Thread.currentThread().getStackTrace()[1].getClassName();
		if(var.contains(".")) var = var.substring(0,var.indexOf("."));
		if(var.startsWith("class ")) var = var.substring(6);
		if((!(scopeIn instanceof ClassFunction) || ((ClassFunction)scopeIn).getObject().getType() != this) && staticPublicVars.containsKey(var) && !staticPublicVars.get(var))
			throw new RuntimeException("Cannot get variable "+var+", it is private.");
		return super.getVar(varname,scopeIn);
	}
}
