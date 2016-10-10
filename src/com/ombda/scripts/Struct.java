package com.ombda.scripts;

import java.util.ArrayList;
import java.util.List;

public class Struct extends Scope{
	protected List<String> definedVars;
	protected StructTemplate type;
	public Struct(StructTemplate type,List<String> definedVars){
		super(false);
		this.type = type;
		this.definedVars = definedVars;
	}
	public StructTemplate getType(){ return this.type; }
	public boolean hasVar(String varname){
		String var = varname;
		if(var.startsWith("class ")){
			var = var.substring(6);
			int i = var.indexOf('.');
			if(i != -1) var = var.substring(0,i);
		}
		return definedVars.contains(var);
	}
	public void setVar(String varname, String value,boolean isfinal,Scope scopeIn){
		if(isfinal && !value.startsWith(Script.REF)) throw new RuntimeException("Cannot set a final variable in a struct instance. (trying to set var "+varname+")");
		if(!hasVar(varname)) throw new RuntimeException("Variable "+varname+" is not defined in struct!");
		super.setVar(varname,value,isfinal,scopeIn);
	}
	public void deleteVar(String varname){
		if(!hasVar(varname)) return;
		deleteVar(varname);
		setVar(varname,"0",false,this);
	}
	public String toString(){
		try{
			String str = getVar("tostring",this);
			if(str.startsWith(Script.REF)){
				Scope scope = Scope.getId(str);
				if(scope instanceof Function){
					Function func = (Function)scope;
					if(func.args_length() == 0)
						return func.call(this,new ArrayList<String>());
				}
			}
		}catch(VarNotExists ex){}
		String str = "[";
		for(String var : definedVars){
			String val = getVar(var,this);
			if(val.startsWith(Script.REF) && !(Scope.getId(val) instanceof Function)) str += Scope.getId(val).toString();
			else str += val+" ";
		}
		return str.substring(0,str.length()-1)+"]";
	}
}
