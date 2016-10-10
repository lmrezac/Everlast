package com.ombda.scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ombda.Facing;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Tile;

public class Scope{
	public static final Scope globalScope;
	public static List<Scope> interns;
	public static final Function substring, sqrt, type, isNumber, index, tostring, length;
	public static final Struct player;
	public static Map map;
	public static final FacingStruct facing_N, facing_NE, facing_E, facing_SE, facing_S, facing_SW, facing_W, facing_NW;
	public static final StructTemplate player_type, map_type, facing_type;
	static{
		interns = new ArrayList<>();
		facing_type = new StructTemplate("Facing",false);
		facing_type.setFinalVar("north", (facing_N = new FacingStruct(Facing.N)).getIdStr(), facing_type);
		facing_type.setFinalVar("northeast", (facing_NE = new FacingStruct(Facing.NE)).getIdStr(), facing_type);
		facing_type.setFinalVar("east", (facing_E = new FacingStruct(Facing.E)).getIdStr(), facing_type);
		facing_type.setFinalVar("southeast", (facing_SE = new FacingStruct(Facing.SE)).getIdStr(), facing_type);
		facing_type.setFinalVar("south", (facing_S = new FacingStruct(Facing.S)).getIdStr(), facing_type);
		facing_type.setFinalVar("southwest", (facing_SW = new FacingStruct(Facing.SW)).getIdStr(), facing_type);
		facing_type.setFinalVar("west", (facing_W = new FacingStruct(Facing.W)).getIdStr(), facing_type);
		facing_type.setFinalVar("northwest", (facing_NW = new FacingStruct(Facing.NW)).getIdStr(), facing_type);
		player_type = new StructTemplate("Player",false);
		map_type = new StructTemplate("Map",false);
		player = new Struct(player_type,Arrays.asList("x","y","map","facing")){
			private final Function toString = new Function(null, null,false){
				@Override
				public int args_length(){ return 0; }
				@Override
				public String call(Scope script,List<String> values){
					com.ombda.Player p = Panel.getInstance().getPlayer();
					return "player@("+Scope.toString(p.x)+","+Scope.toString(p.y)+")["+p.getDirection().toString()+"]{"+p.getMap().toString()+"}";
				}
			};
			@Override
			public String getVar(String varname,Scope scopeIn){
				if(varname.equals("x"))
					return Integer.toString((int)Panel.getInstance().getPlayer().x);
				else if(varname.equals("y"))
					return Integer.toString((int)Panel.getInstance().getPlayer().y);
				else if(varname.equals("map"))
					return Panel.getInstance().getPlayer().getMap().getIdStr();
				else if(varname.equals("facing"))
					return Panel.getInstance().getPlayer().getDirection().getStruct().getIdStr();
				else if(varname.equals("tostring"))
					return toString.getIdStr();
				else throw new VarNotExists("Variable "+varname+" does not exist for player.");
			}
			@Override
			public void setVar(String varname, String value, boolean isfinal,Scope scopeIn){
				if(varname.equals("facing")){
					if(!value.startsWith(Script.REF))
						throw new RuntimeException("Expected facing value, got : "+value);
					Scope scope = Scope.getId(value);
					if(!(scope instanceof Struct))
						throw new RuntimeException("Expected facing value, got : "+value);
					Struct struct = (Struct)scope;
					if(struct.getType() != facing_type)
						throw new RuntimeException("Expected facing value, got : "+value);
					FacingStruct facing = (FacingStruct)struct;
					Panel.getInstance().getPlayer().setDirection(facing.facing);
				}else if(varname.equals("x")){
					double d = parseDouble(value);
					Panel.getInstance().getPlayer().x = d;
					System.out.println("Set player x to "+value);
				}else if(varname.equals("y")){
					double d = parseDouble(value);
					Panel.getInstance().getPlayer().y = d;
					System.out.println("Set player y to "+value);
				}else if(varname.equals("map")){
					if(!value.startsWith(Script.REF)){
						Map map = Map.get(value);
						Panel.getInstance().getPlayer().setMap(map);
						Scope.map = map;
					}else{
						Scope scope = Scope.getId(value);
						if(!(scope instanceof com.ombda.Map))
							throw new RuntimeException("Expected map value, got : "+value);
						Panel.getInstance().getPlayer().setMap((com.ombda.Map)scope);
						Scope.map = (com.ombda.Map)scope;
					}
				}else throw new RuntimeException("Variable "+varname+" doesn't exist in player");
			}
		};
		substring = new Function(null,null,false){
			public int args_length(){ return 3; }
			public String call(Scope script, List<String> values){		
				String value = values.get(0);
				int i1 = parseInt(values.get(1)), i2 = parseInt(values.get(2));
				return value.substring(i1,i2);
			}
		};
		sqrt = new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope script, List<String> values){	
				double i1 = parseDouble(values.get(0));
				return Scope.toString(Math.sqrt(i1));
			}
		};
		length = new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope script, List<String> values){
				String value = values.get(0);
				if(value.startsWith(Script.REF))
					throw new RuntimeException("Invalid value passed for function length, expected a string.");
				return String.valueOf(value.length());
			}
		};
		type = new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope script, List<String> values){			
				String value = values.get(0);
				if(value.startsWith(Script.REF)){
					Scope scope = getId(value);
					if(scope instanceof Function)
						return "function";
					else if(scope.getClass() == Script.class)
						return "script";
					else if(scope instanceof ListScope)
						return ((ListScope)scope).variadic()? "List" : "array";
					else if(scope instanceof StructTemplate)
						return ((StructTemplate)scope).getName();
					else if(scope instanceof Struct)
						return ((Struct)scope).getType().getName();
					else return "scope";
				}else{
					try{
						parseDouble(value);
						return "number";
					}catch(NumberFormatException ex){
						return "string";
					}
				}
			}
		};
		isNumber = new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope script, List<String> values){
				String value = values.get(0);
				try{
					parseDouble(value);
					return "1";
				}catch(NumberFormatException e){
					return "0";
				}
			}
		};
		index = new Function(null,null,false){
			public int args_length(){ return 2; }
			public String call(Scope script, List<String> values){
				String value = values.get(0);
				if(!value.startsWith(Script.REF))
					throw new RuntimeException("Expected a scoped value before []");
				Scope scope = Scope.getId(value);
				if(!(scope instanceof ListScope))
					throw new RuntimeException("Can only index a list!");
				return scope.getVar(values.get(1),scope);
			}
		};
		tostring = new Function(null,null,false){
			public int args_length(){ return 1; }
			public String call(Scope script, List<String> values){			
				String value = values.get(0);
				if(value.startsWith(Script.REF)){
					Scope scope = getId(value);
					return scope.toString();
				}else return value;
			}
		};
		globalScope = new Scope(true){
			public void setVar(String name, String value, boolean finalVar,Scope scopeIn){
			//	if(finalVar) throw new RuntimeException("Cannot create a final var in the global scope");
			//	if(value.startsWith(Script.REF) || name.startsWith("class ") || name.contains(".")) throw new RuntimeException("Global scope can only hold string variables.");
				super.setVar(name, value, finalVar, scopeIn);
			}
		};
	}
	protected final int id;
	public Scope(){ this(true); }
	public Scope(boolean functions){
		boolean set = true;
		int id;
		for(id = 0; id < interns.size(); id++){
			if(interns.get(id) == null){
				interns.set(id,this);
				set = false;
				break;
			}
		}
		if(set){
			id = interns.size();
			interns.add(this);
		}
		this.id = id;
		if(functions){
			setFinalVar("substring",Scope.substring.getIdStr(),this);
			setFinalVar("sqrt",Scope.sqrt.getIdStr(),this);
			setFinalVar("typeof",Scope.type.getIdStr(),this);
			setFinalVar("isNumber",Scope.isNumber.getIdStr(),this);
			setFinalVar("[]",Scope.index.getIdStr(),this);
			setFinalVar("tostring",Scope.tostring.getIdStr(),this);
			setFinalVar("lengthof",Scope.length.getIdStr(),this);
			setFinalVar("player",player.getIdStr(),this);
			if(Panel.getInstance().getPlayer() != null && Panel.getInstance().getPlayer().getMap() != null)
				setVar("map",(map = Panel.getInstance().getPlayer().getMap()).getIdStr(),this);
			setFinalVar("Player",player_type.getIdStr(),this);
			setFinalVar("Facing",facing_type.getIdStr(),this);
			setFinalVar("Map",map_type.getIdStr(),this);
			if(this != globalScope && globalScope != null)
				setFinalVar("global",globalScope.getIdStr(),this);
		}
	}
	protected HashMap<String,String> vars = new HashMap<>();
	protected HashMap<String,Boolean> finalvars = new HashMap<>();
	private static boolean isReservedWord(String s){
		return s.equals("class") || s.equals("set") || s.equals("script") || s.equals("msg") || s.equals("struct") || s.equals("scope") || s.equals("function") || s.equals("end") || s.equals("array") || s.equals("List") || s.equals("not") || s.equals("and") || s.equals("or") || s.equals("=") || s.equals("not=") || s.equals("if") || s.equals("else") || s.equals("delete") || s.equals("while") || s.equals("then") || s.equals("do") || s.equals("define") || s.equals("assert");
	}
	public Scope getCurrentScope(){
		Scope currentScope = this;
		if(this instanceof Script)
			currentScope = ((Script)this).currentScope;
		return currentScope;
	}
	public void setVar(String varname,String value,Scope scope){setVar(varname,value,false,scope);}
	public void setVar(String varname,String value,boolean isfinal,Scope scopeIn){
		if(isReservedWord(varname.trim()))
			throw new RuntimeException("Cannot set var of name "+varname+", it is a reserved word.");
		if(varname.equals("map")){
			if(isfinal) throw new RuntimeException("Cannot set final var map");
			String mapname = value;
			if(value.startsWith(Script.REF)){
				Scope scope = Scope.getId(value);
				if(!(scope instanceof Map))
					throw new RuntimeException("Cannot set value of map to non-map object!");
				mapname = scope.toString();
			}
			map = Map.get(mapname);
			Panel.getInstance().getPlayer().setMap(map);
		}
		if(varname.contains("operator"))
			varname = varname.replaceAll("operator(?! )","operator ");
		if(!varname.startsWith("class ") && varname.contains(".")) varname = "class "+varname;
		if(varname.startsWith("class ")){
			varname = varname.substring(6);
			int i = varname.indexOf('.');
			if(i == -1){
				if(finalvars.containsKey(varname) && finalvars.get(varname))
					throw new RuntimeException("Cannot assign variable "+varname+", it is final.");
				if(!value.startsWith(Script.REF))
					throw new RuntimeException("Cannot assign subscope "+varname+" to a non-scope value!");
				//Scope scope = scopes.get(varname);
				Scope scope = getId(value);
				
				vars.put(varname,scope.getIdStr());
				finalvars.put(varname,isfinal);
				//throw new RuntimeException("Sub-scope "+varname+" does not exist!");
			}else{
				String var = varname.substring(i+1);
				varname = varname.substring(0,i);
				String val = vars.get(varname);
				if(val == null)
					throw new RuntimeException("Subscope of name "+varname+" doesn't exist!");
				if(!val.startsWith(Script.REF))
					throw new RuntimeException("Variable "+varname+" is not an object type!");
				Scope scope = getId(val);
				if(scope == null)
					throw new RuntimeException("Subscope of name "+varname+" doesn't exist!");
				if(scope instanceof Function)
					throw new RuntimeException("Cannot set variable of a function.");
				if(!var.startsWith("class ") && var.contains("."))
					var = "class "+var;
				scope.setVar(var,value,isfinal,scopeIn);
			}
		}else{
			if(finalvars.containsKey(varname) && finalvars.get(varname))
				throw new RuntimeException("Cannot assign variable "+varname+", it is final.");
			if(value.startsWith(Script.REF))
				getId(value);
			vars.put(varname,value);
			finalvars.put(varname,isfinal);
		}
	}
	public void setFinalVar(String varname,String value,Scope scopeIn){
		setVar(varname,value,true,scopeIn);
	}
	public String getVar(String varname,Scope scopeIn){
		if(varname.contains("operator"))
			varname = varname.replaceAll("operator(?! )","operator ");
		if(varname.equals("class") || varname.equals("class "))
			throw new RuntimeException("Expected subscope name after class");
		if(!varname.startsWith("class ") && varname.contains(".")) varname = "class "+varname;
		//System.out.println("Get var "+varname);
		if(varname.startsWith("class ")){
			varname = varname.substring(6);
			int i = varname.indexOf('.');
			if(i == -1){
				Scope scope;
				String value = vars.get(varname);
				if(value == null || !value.startsWith(Script.REF))
					throw new VarNotExists("Scoped variable "+varname+" does not exist.");
				scope = getId(value);
				if(scope == null)
					throw new VarNotExists("Subscope of name "+varname+" doesn't exist.");
				return scope.getIdStr();
			}else{
				String var = varname.substring(i+1);
				varname = varname.substring(0,i);
				String value = vars.get(varname);
				if(value == null || !value.startsWith(Script.REF))
					throw new VarNotExists("Scoped variable "+varname+" does not exist.");
				Scope scope = getId(value);
				if(scope == null)
					throw new VarNotExists("Subscope of name "+varname+" doesn't exist.");
				if(scope instanceof Function)
					throw new RuntimeException("Cannot get variable from scope of function.");
				if(!var.startsWith("class ") && var.contains("."))
					var = "class "+var;
				
				return scope.getVar(var,scopeIn);
			}
		}else{
			String value = vars.get(varname);
			if(value == null)
				throw new VarNotExists("Variable "+varname+" doesn't exist!");
			return value;
		}
	}
	public void deleteVar(String varname){
		if(varname.contains("operator"))
			varname = varname.replaceAll("operator(?! )","operator ");
		if(!varname.startsWith("class ") && varname.contains(".")) varname = "class "+varname;
		if(varname.startsWith("class ")){
			varname = varname.substring(6);
			int i = varname.indexOf('.');
			if(i == -1){
				Scope scope;
				String value = vars.get(varname);
				if(finalvars.get(varname))
					throw new RuntimeException("Variable "+varname+" is final. It cannot be deleted.");
				if(value == null) return;
				if(!value.startsWith(Script.REF))
					throw new VarNotExists("Scoped variable "+varname+" does not exist.");
				scope = getId(value);
				if(scope == null)
					return;//	throw new VarNotExists("Subscope of name "+varname+" doesn't exist.");
				Scope.interns.set(scope.getId(),null);
			}else{
				String var = varname.substring(i+1);
				varname = varname.substring(0,i);
				String value = vars.get(varname);
				if(value == null) return;
				if(!value.startsWith(Script.REF))
					throw new VarNotExists("Scoped variable "+varname+" does not exist.");
				Scope scope = getId(value);
				if(scope == null)
					throw new VarNotExists("Subscope of name "+varname+" doesn't exist.");
				if(scope instanceof Function)
					throw new RuntimeException("Cannot get/delete variable from scope of function.");
				if(!var.startsWith("class ") && var.contains("."))
					var = "class "+var;
				scope.deleteVar(var);
			}
		}else{
			if(finalvars.get(varname))
				throw new RuntimeException("Variable "+varname+" is final. It cannot be deleted.");
			vars.remove(varname);
		}
	}
	public String evalVars(String line){
		Scope currentScope = this.getCurrentScope();
		String str = line;
		if(!isString(str)){
			if(currentScope.vars.containsKey(str) || str.startsWith("class ") || str.contains(".")){
					if(!str.startsWith("class ") && str.contains(".")) str = "class "+str;
					return currentScope.getVar(str,currentScope);
			}
		}
		str = evalString(str);
		int i;
		while((i = str.indexOf("${")) != -1){
			int j = str.indexOf('}',i+2);
			if(j == -1)
				throw new RuntimeException("Unclosed variable at index "+i+" in string "+line);
			String varname = str.substring(i+2,j);
			str = str.substring(0,i)+currentScope.getVar(varname,currentScope)+(j+1 < str.length()? str.substring(j+1) : "");
		}
		return str;
	}
	public void evalArgs(List<String> args){
		Scope currentScope = getCurrentScope();
		for(int i = 0; i < args.size(); i++){
			String arg = args.get(i);
			if(arg.contains(".") && ((i > 0 && !args.get(i-1).equals("class")) || i==0)){
				
				args.add(i,"class");
			}
		}
		for(int i = 0; i < args.size(); i++){
			String arg = args.get(i);
			if(arg.endsWith("class") || arg.endsWith("operator")){
				if(i+1 >= args.size())
					throw new RuntimeException("Keyword class expects a variable name!");
				arg += " "+evalString(args.remove(i+1));
				while(arg.endsWith("class") || arg.endsWith("operator")){
					if(i+1 >= args.size())
						throw new RuntimeException("Keyword class/operator expects a variable/operator name!");
					arg += " "+evalString(args.remove(i+1));
				}
				arg = currentScope.getVar(arg,currentScope);
				args.set(i,arg);
			}
		}
		evalMath(args);
		for(int i = 0; i < args.size()-1; i++){
			String arg = evalVars(args.get(i));
			if(arg.startsWith(Script.REF)){
				Scope scope = Scope.getId(arg);
				if(scope instanceof Struct)
					arg = scope.toString();
				else if(scope instanceof StructTemplate)
					arg = ((StructTemplate)scope).getName();
			}
			args.set(i,arg);
		}
	}
	public static String toString(double d){
		String str = Double.toString(d);
		if(str.endsWith(".0"))
			return str.substring(0,str.length()-2);
		return str;
	}
	private void evalMath(List<String> args){
		int index;
		while((index = args.indexOf("(")) != -1){
			int depth = 1;
			List<String> subargs = new ArrayList<>();
			while(!args.isEmpty()){
				if(args.get(index+1).equals("(")){
					depth++;
				}
				else if(args.get(index+1).equals(")")){
					depth--;
				}
				if(depth == 0) break;
				subargs.add(args.remove(index+1));
			}
			args.remove(index+1);
			args.remove(index);
			evalMath(subargs);
			
			args.addAll(index,subargs);
		}
		List<String> lastPass = new ArrayList<String>();
		while(!args.equals(lastPass)){
			for(index = args.size()-1; index >= 0; index--){
				String arg = evalVars(args.get(index));
				if(arg.startsWith(Script.REF)){
					if((index != 0 && !args.get(index-1).equals("function")) || index == 0){
						Scope scope = getId(arg);
						if(scope instanceof Function){
							Function func = (Function)scope;
							int size = func.args_length();
							List<String> values = new ArrayList<>();
							for(int j = 0; j < size; j++){
								if(index+1 >= args.size())
									throw new RuntimeException("Function of id "+id+" expects "+size+" arguments, found "+values.size());
								values.add(evalVars(args.remove(index+1)));
							}
							args.set(index,func.call(getCurrentScope(),values));
						}
					}else{
						assert args.get(index-1).equals("function");
						index--;
						String name = evalString(args.remove(index+1));
						String value = getCurrentScope().evalVars(name);
						if(!value.startsWith(Script.REF)) throw new RuntimeException("Value after function declaration MUST be a scoped value! Got: "+value);
						Scope scope = Scope.getId(value);
						if(!(scope instanceof Function))
							throw new RuntimeException("Value after function declaration MUST be a function value.");
						args.set(index,scope.getIdStr());
					}
				}else if(arg.equals("new")){
					if(index >= args.size()-1)
						throw new RuntimeException("Expected type name after new declaration");
					String name = evalString(args.remove(index+1));
					if(name.equals("scope")){
						Scope s = new Scope();
						args.set(index,s.getIdStr());
						continue;
					}else if(name.equals("array")){
						if(index+1 >= args.size())
							throw new RuntimeException("Expected size after new array declaration.");
						String value = args.remove(index+1);
						int size = parseInt(value);
						ListScope list = new ListScope(size);
						args.set(index,list.getIdStr());
					}else if(name.equals("List")){
						ListScope list = new ListScope();
						args.set(index,list.getIdStr());
					}else if(getVar(name,this).startsWith(Script.REF)){
						Scope scope = Scope.getId(getVar(name,this));
						if(!(scope instanceof StructTemplate))
							throw new RuntimeException("Cannot create new object from non-struct template");
						StructTemplate template = (StructTemplate)scope;
						List<String> values = new ArrayList<String>();
						if(template.args_length() > 0){
							for(int j = 0; j < template.args_length(); j++){
								if(index+1 >= args.size()) break;
								values.add(evalVars(args.remove(index+1)));
							}
						}
						Struct struct = template.createNewStruct(values);
						args.set(index,struct.getIdStr());
					}else throw new RuntimeException("Invalid type name: "+name);
				}else if(arg.equals("{")){
					if(index >= args.size()-2)
						throw new RuntimeException("Expected parenthesis after collect");
					args.remove(index);
					List<String> values = new ArrayList<>();
				
					while(!args.get(index).equals("}"))
						values.add(evalVars(args.remove(index)));
					
					ListScope list = new ListScope(values.size());
					for(int j = 0; j < values.size(); j++)
						list.setVar(toString(j),values.get(j),getCurrentScope());
					
					args.set(index,list.getIdStr());
				}else args.set(index,arg);
			} // functions
			for(index = 1; index < args.size()-1; index++){
				String arg = args.get(index);
				if(arg.equals("^")){
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string)
					try{
						double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
						args.set(index,toString(Math.pow(x,y)));
					}catch(NumberFormatException e){
						string = testOpOverride(args,index,arg1,arg2,"operator ^");
					}
					if(string){
						args.add(index+1,arg);
						args.add(index+2,arg2);
					}
				}
			} // ^
			for(index = 1; index < args.size()-1; index++){
				String arg = args.get(index);
				if(arg.equals("*") || arg.equals("/")){
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string)
					try{
						double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
						args.set(index,toString(arg.equals("*")? (x * y) : (x / y)));
					}catch(NumberFormatException e){
						string = testOpOverride(args,index,arg1,arg2,"operator "+arg);
					}
					if(string){
						args.add(index+1,arg);
						args.add(index+2,arg2);
					}
				}
			} // * /
			for(index = 1; index < args.size()-1; index++){
				String arg = args.get(index);
				if(arg.equals("+") || arg.equals("-")){
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string){
					try{
						double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
						args.set(index,Script.toString(arg.equals("+")? (x + y) : (x - y)));
					}catch(NumberFormatException e){
						string = testOpOverride(args,index,arg1,arg2,"operator "+arg);
					}
					}
					if(string){
						args.set(index,evalVars(arg1)+evalVars(arg2));
					}
				}else if(arg.equals("##")){
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					args.set(index,evalVars(arg1)+evalVars(arg2));
				}
			} // + -
			for(index = 1; index < args.size()-1; index++){ 
				String arg = args.get(index);
				if(arg.equals("=") || arg.equals("not=")){
					
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string)
					try{
						double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
						args.set(index,(arg.equals("not=")? !(x == y) : (x == y))? "1" : "0");
					}catch(NumberFormatException e){
						string = testOpOverride(args,index,arg1,arg2,"operator "+arg);
						if(string){
							string = testOpOverride(args,index,arg1,arg2,"operator =");
							if(string){
								string = testOpOverride(args,index,arg1,arg2,"equals");
							}
							if(!string && arg.equals("not=")) args.set(index,args.get(index).equals("0")? "1" : "0");
						}
					}
					if(string)
						args.set(index,(arg.equals("not=")? !arg1.equals(arg2) : arg1.equals(arg2))? "1" : "0");
				}else if(arg.equals("<") || arg.equals("<=") || arg.equals(">") || arg.equals(">=")){
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string)
					try{
						double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
						args.set(index,(arg.equals("<")? (x < y) : arg.equals("<=")? (x <= y) : arg.equals(">")? (x > y) : (x >= y))? "1" : "0");
					}catch(NumberFormatException e){
						string = testOpOverride(args,index,arg1,arg2,"operator "+arg);
					}
					if(string){
						args.add(index+1,arg);
						args.add(index+2,arg2);
					}
				}
			} // = < > <= >= not=
			for(index = 0; index < args.size()-1; index++){
				String arg = args.get(index);
				if(arg.equals("not")){
					String arg1 = args.remove(index+1);
					if(!isString(arg1))
						arg1 = evalVars(arg1);
					boolean override = false;
					if(arg1.startsWith(Script.REF)){
						override = testOpBool(args,index,arg1);
					}
					if(!override){
						if(arg1.endsWith(".0")) arg1 = arg1.substring(0,arg1.length()-2);
						args.set(index,arg1.equals("0")? "1" : "0");
					}
				}
			} // not
			for(index = 1; index < args.size()-1; index++){
				String arg = args.get(index);
				if(arg.equals("and")){
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					if(!isString(arg1)) arg1 = evalVars(arg1);
					if(!isString(arg2)) arg2 = evalVars(arg2);
					boolean no_override = true;
					if(arg1.startsWith(Script.REF) || arg2.startsWith(Script.REF))
						no_override = testOpOverride(args,index,arg1,arg2,"operator and");
					if(no_override){
						if(arg1.endsWith(".0")) arg1 = arg1.substring(0,arg1.length()-2);
						if(arg2.endsWith(".0")) arg2 = arg2.substring(0,arg2.length()-2);
						args.set(index,arg1.equals("0")? "0" : arg2.equals("0")? "0" : arg2);
					}
				}
			} // and
			for(index = 1; index < args.size()-1; index++){
				String arg = args.get(index);
				if(arg.equals("or")){
					index--;
					String arg1 = args.get(index);
					args.remove(index+1);
					String arg2 = args.remove(index+1);
					if(!isString(arg1)) arg1 = evalVars(arg1);
					if(!isString(arg2)) arg2 = evalVars(arg2);
					boolean no_override = true;
					if(arg1.startsWith(Script.REF) || arg2.startsWith(Script.REF))
						no_override = testOpOverride(args,index,arg1,arg2,"operator and");
					if(no_override){
						if(arg1.endsWith(".0")) arg1 = arg1.substring(0,arg1.length()-2);
						if(arg2.endsWith(".0")) arg2 = arg2.substring(0,arg2.length()-2);
						args.set(index,arg1.equals("0")? (arg2.equals("0")? "0" : arg2) : arg1);
					}
				}
			} // or
			lastPass = new ArrayList<String>(args);
		}
	}
	private boolean testOpOverride(List<String> args, int index, String arg1_, String arg2_, String op){
		String arg1 = evalVars(arg1_), arg2 = evalVars(arg2_);
		if(arg1.startsWith(Script.REF)){
			Scope scope = Scope.getId(arg1);
			Function func; String var = null;
			try{ if(scope instanceof Struct && (var = scope.getVar(op,this)).startsWith(Script.REF) && Scope.getId(var) instanceof Function && (func = (Function)Scope.getId(var)).args_length() == 1)
				args.set(index,var = func.call(this,Arrays.asList(arg2)));
				else return true;//System.out.println("result op = "+args.get(index)+ " var = "+var);
			}catch(VarNotExists ex){ return true; }
			return false;
		}else if(arg2.startsWith(Script.REF)){
			if(op.equals("equals") || op.equals("operator =") || op.equals("operator not="))
				return testOpOverride(args,index,arg2,arg1,op);
		}
		return true;
	}
	private boolean testOpBool(List<String> args, int index, String arg1){
		arg1 = evalVars(arg1);
		if(arg1.startsWith(Script.REF)){
			Scope scope = Scope.getId(arg1);
			Function func; String var;
			try{ if(scope instanceof Struct && (var = scope.getVar("operator bool",this)).startsWith(Script.REF) && Scope.getId(var) instanceof Function && (func = (Function)Scope.getId(var)).args_length() == 0)
				args.set(index,func.call(this,new ArrayList<String>()));
			}catch(VarNotExists ex){ return true; }
			return false;
		}
		return true;
	}
	public final String getIdStr(){return Script.REF+getId();}
	public final int getId(){return id;}
	public String toString(){return "[id="+id+", vars="+vars.toString()+"]";}
	public static boolean isString(String str){return str.startsWith("\"") && str.endsWith("\"") && str.length() > 1;}
	public static String evalString(String str){
		if(isString(str)) return str.substring(1,str.length()-1);
		return str;
	}
	public static Scope getId(String str){
		str = str.substring(1);
		int id = parseInt(str);
		if(id < 0 || id > interns.size())
			throw new RuntimeException("Invalid scope id : "+id);
		return interns.get(id);
	}
	public static int parseInt(String str){
		if(str.endsWith("t"))
			return Tile.SIZE * Integer.decode(str.substring(0,str.length()-1));
		return Integer.decode(str);
	}
	public static double parseDouble(String str){
		if(str.endsWith("t"))
			return Tile.SIZE * Double.valueOf(str.substring(0,str.length()-1));
		return Double.valueOf(str);
	}
}
