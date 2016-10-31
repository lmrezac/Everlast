package com.ombda.scripts;

import java.util.List;

public class ListScope extends Scope{
	private int size = 0;
	private boolean variadicsize = false;
	public ListScope(int size){
		super(false);
		if(size < 0) throw new RuntimeException("Invalid size : "+size);
		variadicsize = false;
		this.size = size;
		super.setVar("size", Scope.toString(size), true, this);
	}
	public ListScope(){
		super(false);
		variadicsize = true;
		this.vars.put("add",new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope scope, List<String> values){
				ListScope.this.addAt(ListScope.this.size,values.get(0));
				return values.get(0);
			}
		}.getIdStr());
		this.vars.put("addAt",new Function(null,null,false){
			public int args_length(){ return 2; }
			public String call(Scope scope, List<String> values){
				ListScope.this.addAt(Integer.parseInt(values.get(0)),values.get(1));
				return values.get(1);
			}
		}.getIdStr());
		this.vars.put("removeAt",new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope scope, List<String> values){
				int index = Integer.parseInt(values.get(0));
				String value = ListScope.this.getVar(values.get(0),ListScope.this);
				ListScope.this.removeAt(index);
				return value;
			}
		}.getIdStr());
		this.vars.put("equals", this.vars.put("operator =", new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope scopeIn, List<String> values){
				String val = values.get(0);
				if(!val.startsWith(Script.REF))return "0";
				Scope scope = Scope.getId(val);
				if(!(scope instanceof ListScope)) return "0";
				ListScope list = (ListScope)scope;
				if(variadicsize != list.variadicsize) return "0";
				if(size != list.size) return "0";
				for(int i = 0; i < size; i++){
					String index = String.valueOf(i);
					if(!getVar(index,this).equals(list.getVar(index,list))) return "0";
				}
				return "1";
			}
		}.getIdStr()));
	}
	public boolean variadic(){ return variadicsize; }
	public void setVar(String varname, String value,boolean isfinal,Scope scopeIn){
		if(isfinal) throw new RuntimeException("A list cannot contain final indexes.");
	//	if(!varname.startsWith("class ") && varname.contains(".")) varname = "class "+varname;
		if(varname.startsWith("class ")){
			varname = varname.substring(6);
			int i = varname.indexOf('.');
			if(i == -1){
				if(!varname.matches("\\d+"))
					throw new RuntimeException("Invalid list index : "+varname);
				int id = Integer.parseInt(varname);
				if(!variadicsize){
					if(id >= this.size)
						throw new IndexOutOfBoundsException("Index "+id+", size "+size);
				}else if(id > this.size){
					if(id >= this.size)
						throw new IndexOutOfBoundsException("Index "+id+", size "+size);
					this.size = id;
				}
				super.setVar("class "+varname, value,false,scopeIn);
			}else{
				varname = varname.substring(0,i);
				if(!varname.matches("\\d+"))
					throw new RuntimeException("Invalid list index : "+varname);
				int id = Integer.parseInt(varname);
				if(!variadicsize){
					if(id >= this.size)
						throw new IndexOutOfBoundsException("index "+id+", size "+size);
				}else if(id > this.size){
					if(id >= this.size)
						throw new IndexOutOfBoundsException("Index "+id+", size "+size);
					this.size = id;
				}
				super.setVar("class "+varname, value,false,scopeIn);
			}
		}else{
			if(!varname.matches("\\d+"))
				throw new RuntimeException("Invalid list index : "+varname);
			int id = Integer.parseInt(varname);
			if(!variadicsize){
				if(id >= this.size)
					throw new IndexOutOfBoundsException("index "+id+", size "+size);
			}else if(id > this.size){
				if(id >= this.size)
					throw new IndexOutOfBoundsException("Index "+id+", size "+size);
				this.size = id;
			}
			super.setVar(varname,value,false,scopeIn);
		}
	}
	public void addAt(int index,String value){
		if(!variadicsize)
			throw new RuntimeException("Cannot add to an array.");
		this.size++;
		for(int i = this.size-2; i >= index; i--){
			this.setVar(Scope.toString(i+1),this.getVar(Scope.toString(i),this),this);	
		}
		this.setVar(Scope.toString(index),value,this);
	}
	public void removeAt(int index){
		if(!variadicsize)
			throw new RuntimeException("Cannot add to an array.");
		for(int i = index; i < this.size-1; i++){
			this.setVar(Scope.toString(i),this.getVar(Scope.toString(i+1),this),this);
		}
		this.size--;
	}
	public String getVar(String varname,Scope scopeIn){
		if(varname.equals("size")) return Integer.toString(this.size);
		if(varname.equals("add") || varname.equals("addAt") || varname.equals("removeAt") || varname.equals("equals") || varname.equals("operator ="))
			return super.getVar(varname,scopeIn);
		if(!varname.startsWith("class ") && !require_class && varname.contains(".")) varname = "class "+varname;
		if(varname.startsWith("class ")){
			varname = varname.substring(6);
			int i = varname.indexOf('.');
			if(i == -1){
				if(!varname.matches("\\d+"))
					throw new RuntimeException("Invalid list index : "+varname);
				int id = Integer.parseInt(varname);
				if(id >= this.size)
					throw new IndexOutOfBoundsException("index "+id+", size "+size);
				try{
					return super.getVar("class "+varname,scopeIn);
				}catch(VarNotExists ex){
					super.setVar("class "+varname, "0",scopeIn);
					return "0";
				}
			}else{
			//	String subvarname = varname.substring(i+1);
				varname = varname.substring(0,i);
				if(!varname.matches("\\d+"))
					throw new RuntimeException("Invalid list index : "+varname);
				int id = Integer.parseInt(varname);
					if(id >= this.size)
						throw new IndexOutOfBoundsException("index "+id+", size "+size);
				//}else if(id > this.size)
				//	this.size = id;
				try{
					return super.getVar("class "+varname,scopeIn);
				}catch(VarNotExists ex){
					super.setVar("class "+varname, "0",scopeIn);
					return "0";
				}
			}
		}else{
			if(!varname.matches("\\d+"))
				throw new RuntimeException("Invalid list index : "+varname);
			int id = Integer.parseInt(varname);
			//if(!variadicsize){
				if(id >= this.size)
					throw new IndexOutOfBoundsException("index "+id+", size "+size);
			//}else if(id > this.size)
			//	this.size = id;
			try{
				return super.getVar(varname,scopeIn);
			}catch(VarNotExists ex){
				super.setVar(varname, "0",scopeIn);
				return "0";
			}
		}
	}
	public String toString(){
		String result = "{";
		for(int i = 0; i < size; i++){
			try{
				result += getVar(Integer.toString(i),this);
			}catch(VarNotExists ex){
				result += "0";
			}
			if(i != size-1)
				result += ' ';
		}
		return result+"}";
	}
	public boolean equals(ListScope list){
		if(size != list.size) return false;
		for(int i = 0; i < size; i++){
			String index = String.valueOf(i);
			if(!getVar(index,this).equals(list.getVar(index,list))) return false;
		}
		return true;
	}
}
