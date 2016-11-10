package com.ombda.scripts;

import java.util.ArrayList;
import java.util.List;

import com.ombda.Tile;


public class Script{
	
	//private static Pattern number = Pattern.compile("-?((\\d+\\.\\d*)|(\\d*\\.\\d+))");

	public static List<String> scanLine(String line){
		
		List<String> result = new ArrayList<>();
		for(int i = 0; i < line.length(); i++){
			if(line.charAt(i) == '"'){
				int j = line.indexOf('"',i+1);
				if(j == -1) throw new RuntimeException("Unclosed string literal at index "+i+" in string "+line);
				if(j+1 < line.length() && line.charAt(j+1) != ' ') throw new RuntimeException("Expected space after closing \"");
				result.add(line.substring(i,j+1));
				i = j+1;
			}else{
				int depth = 0; int j;
				for(j = i; j < line.length() && (line.charAt(j) != ' ' || depth != 0); j++){
					if(j != 0 && line.charAt(j) == '{' && line.charAt(j-1) == '$') depth++;
					else if(line.charAt(j) == '}') depth--;
					if(depth < 0) depth = 0;
				}
				if(depth > 0) throw new RuntimeException("Unclosed variable in string "+line);
				result.add(line.substring(i,j).trim());
				i = j;
				
			}
		}
		return result;
	}
	/*public String evalVars(String line){
		Scope currentScope = this.getCurrentScope();
		String str = line;
		if(!isString(str) && !require_brackets && !number.matcher(str).find()){
			if(currentScope.vars.containsKey(str) || ((!str.startsWith("npc ") && !str.startsWith("sprite ") && (str.startsWith("class ") || (!require_class && str.indexOf(".") > 0)) && !str.contains("${")))){

				if(str.indexOf(".") > 0 && !str.startsWith("class ") && !str.startsWith(".") && !require_class && !str.startsWith("npc ") && !str.startsWith("sprite "))
					str = "class " + str;
				return currentScope.getVar(str, currentScope);
			}
		}
		str = evalString(str);
		int i;
		while((i = str.indexOf("${")) != -1){
			int j = str.indexOf('}', i + 2);
			if(j == -1)
				throw new RuntimeException("Unclosed variable at index " + i + " in string " + line);
			String varname = str.substring(i + 2, j);
			str = str.substring(0, i) + currentScope.getVar(varname, currentScope) + (j + 1 < str.length()? str.substring(j + 1) : "");
		}
		return str;
	}*/

	/*public void evalArgs(List<String> args){
		//debug("before: "+args);
		Scope currentScope = getCurrentScope();
		for(int i = 0; i < args.size(); i++){
			String arg = args.get(i);
			if(!arg.contains("${") && !require_brackets && !require_class && arg.indexOf(".") > 0 && ((i > 0 && !args.get(i - 1).equals("class") && !args.get(i - 1).equals("npc") && !args.get(i - 1).equals("sprite")) || i == 0)){
				args.add(i, "class");
			}
		}
	//	debug("args after = "+args);
		for(int i = 0; i < args.size(); i++){
			String arg = args.get(i);
			if(!require_brackets && (arg.endsWith("class") || arg.endsWith("operator"))){
				if(i + 1 >= args.size())
					throw new RuntimeException("Keyword class expects a variable name!");
				arg += " " + evalString(args.remove(i + 1));
				while(arg.endsWith("class") || arg.endsWith("operator")){
					if(i + 1 >= args.size())
						throw new RuntimeException("Keyword class/operator expects a variable/operator name!");
					arg += " " + evalString(args.remove(i + 1));
				}
				try{
					arg = currentScope.getVar(arg, currentScope);
					args.set(i, arg);
				}catch(VarNotExists ex){
					args.set(i,arg);
				}
			}else if(!require_brackets && (arg.equals("npc") || arg.equals("sprite"))){
				args.set(i, currentScope.getVar(arg + " " + args.remove(i + 1), currentScope));
			}
			
		}
		evalMath(args);
		for(int i = 0; i < args.size() - 1; i++){
			String arg = evalVars(args.get(i));
			if(arg.startsWith(Script.REF)){
				Scope scope = Scope.getId(arg);
				if(scope instanceof Struct)
					arg = scope.toString();
				else if(scope instanceof StructTemplate)
					arg = ((StructTemplate) scope).getName();
			}
			args.set(i, arg);
		}
	}*/

	public static String toString(double d){
		String str = Double.toString(d);
		if(str.endsWith(".0"))
			return str.substring(0, str.length() - 2);
		return str;
	}

	/*public void evalMath(List<String> args){
		int index;
		while((index = args.indexOf("(")) != -1){
			int depth = 1;
			List<String> subargs = new ArrayList<>();
			while(!args.isEmpty()){
				if(args.get(index + 1).equals("(")){
					depth++;
				}else if(args.get(index + 1).equals(")")){
					depth--;
				}
				if(depth == 0)
					break;
				subargs.add(args.remove(index + 1));
			}
			args.remove(index + 1);
			args.remove(index);
			evalMath(subargs);

			args.addAll(index, subargs);
		}
		List<String> lastPass = new ArrayList<String>();
		while(!args.equals(lastPass)){
			lastPass = new ArrayList<String>(args);
			// function calls, new objects, array literals
			for(index = args.size() - 1; index >= 0; index--){
				String arg = evalVars(args.get(index));
				if(arg.startsWith(Script.REF)){
					if((index != 0 && !args.get(index - 1).equals("function")) || index == 0){
						Scope scope = getId(arg);
						if(scope instanceof Function){
							Function func = (Function) scope;
							int size = func.args_length();
							List<String> values = new ArrayList<>();
							for(int j = 0; j < size; j++){
								if(index + 1 >= args.size())
									throw new RuntimeException("Function of id " + id + " expects " + size + " arguments, found " + values.size());
								values.add(evalVars(args.remove(index + 1)));
							}
							args.set(index, func.call(getCurrentScope(), values));
						}
					}else{
						assert args.get(index - 1).equals("function");
						index--;
						String name = evalString(args.remove(index + 1));
						String value = getCurrentScope().evalVars(name);
						if(!value.startsWith(Script.REF))
							throw new RuntimeException("Value after function declaration MUST be a scoped value! Got: " + value);
						Scope scope = Scope.getId(value);
						if(!(scope instanceof Function))
							throw new RuntimeException("Value after function declaration MUST be a function value.");
						args.set(index, scope.getIdStr());
					}
					if(index+1 < args.size() && args.get(index+1).startsWith(".") && args.get(index+1).length()>1 && args.get(index).startsWith(Script.REF)){
					
						Scope scope = Scope.getId(args.get(index));
						String val = args.remove(index+1);
				//		index--;
						args.set(index,arg = scope.getVar(val.substring(1),getCurrentScope()));
		
					}
				}else if(arg.equals("new")){
					if(index >= args.size() - 1)
						throw new RuntimeException("Expected type name after new declaration");
					String name = evalString(args.remove(index + 1));
					if(name.equals("scope")){
						Scope s = new Scope();
						args.set(index, s.getIdStr());
						continue;
					}else if(name.equals("array")){
						if(index + 1 >= args.size())
							throw new RuntimeException("Expected size after new array declaration.");
						String value = args.remove(index + 1);
						int size = parseInt(value);
						ListScope list = new ListScope(size);
						args.set(index, list.getIdStr());
					}else if(name.equals("List")){
						ListScope list = new ListScope();
						args.set(index, list.getIdStr());
					}else if(getVar(name, this).startsWith(Script.REF)){
						Scope scope = Scope.getId(getVar(name, this));
						if(!(scope instanceof StructTemplate))
							throw new RuntimeException("Cannot create new object from non-struct template");
						StructTemplate template = (StructTemplate) scope;
						List<String> values = new ArrayList<String>();
						if(template.args_length() > 0){
							for(int j = 0; j < template.args_length(); j++){
								if(index + 1 >= args.size())
									break;
								values.add(evalVars(args.remove(index + 1)));
							}
						}
						Struct struct = template.createNewStruct(values);
						args.set(index, struct.getIdStr());
					}else
						throw new RuntimeException("Invalid type name: " + name);
				}else if(arg.equals("{")){
					//if(index >= args.size() - 2)
					//	throw new RuntimeException("Expected parenthesis after collect");
					args.remove(index);
					List<String> values = new ArrayList<>();

					while(!args.get(index).equals("}"))
						values.add(evalVars(args.remove(index)));

					ListScope list = new ListScope(values.size());
					for(int j = 0; j < values.size(); j++)
						list.setVar(toString(j), values.get(j), getCurrentScope());

					args.set(index, list.getIdStr());
			
				}else
					args.set(index, arg);
			}
			// power operator '^'
			for(index = 1; index < args.size() - 1; index++){
				String arg = args.get(index);
				if(arg.equals("^")){
					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string)
						try{
							double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
							args.set(index, toString(Math.pow(x, y)));
						}catch(NumberFormatException e){
							string = testOpOverride(args, index, arg1, arg2, "operator ^");
						}
					if(string){
						args.add(index + 1, arg);
						args.add(index + 2, arg2);
					}
				}
			}
			// multiplication, division operators '*' '/'
			for(index = 1; index < args.size() - 1; index++){
				String arg = args.get(index);
				if(arg.equals("*") || arg.equals("/")){
					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string)
						try{
							double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
							args.set(index, toString(arg.equals("*")? (x * y) : (x / y)));
						}catch(NumberFormatException e){
							string = testOpOverride(args, index, arg1, arg2, "operator " + arg);
						}
					if(string){
						args.add(index + 1, arg);
						args.add(index + 2, arg2);
					}
				}
			}
			// addition, subtraction, concatenation operators '+' '-' '##'
			for(index = 1; index < args.size() - 1; index++){
				String arg = args.get(index);
				if(arg.equals("+") || arg.equals("-")){
					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string){
						try{
							double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
							args.set(index, Script.toString(arg.equals("+")? (x + y) : (x - y)));
						}catch(NumberFormatException e){
							string = testOpOverride(args, index, arg1, arg2, "operator " + arg);
						}
					}
					if(string){
						args.set(index, evalVars(arg1) + evalVars(arg2));
					}
				}else if(arg.equals("##")){
					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					args.set(index, evalVars(arg1) + evalVars(arg2));
				}
			}
			// equality, comparison operators '=' 'not=' '>' '>=' '<' '<='
			for(index = 1; index < args.size() - 1; index++){
				
				String arg = args.get(index);
				if(arg.equals("=") || arg.equals("not=")){

					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					//debug("arg1 = "+evalVars(arg1)+" isstring = "+isString(evalVars(arg1))+" arg2 = "+evalVars(arg2)+" isstring = "+isString(evalVars(arg2)));
					boolean string = isString(evalVars(arg1)) || isString(evalVars(arg2));
					
					if(!string)
						try{
							String arg1_ = evalVars(arg1);
							String arg2_ = evalVars(arg2);
							double x = parseDouble(arg1_), y = parseDouble(arg2_);
							
							if(arg1_.endsWith("t"))
								x /= Tile.SIZE / 16;
							if(arg2_.endsWith("t"))
								y /= Tile.SIZE / 16;
							args.set(index, (arg.equals("not=")? !(x == y) : (x == y))? "1" : "0");
						}catch(NumberFormatException e){
							string = testOpOverride(args, index, arg1, arg2, "operator " + arg);
							
							if(string){
								string = testOpOverride(args, index, arg1, arg2, "operator =");
								if(string){
									debug("testing op override equals for "+arg1+" and "+arg2);
									string = testOpOverride(args, index, arg1, arg2, "equals");
									debug("result = "+string);
								}
								if(!string && arg.equals("not="))
									args.set(index, args.get(index).equals("0")? "1" : "0");
							}
						}
					if(string){
						debug("comparing "+arg1+" with "+arg2);
						/*if(arg1.startsWith(Script.REF)|| arg2.startsWith(Script.REF) ){
							Scope sarg1 = Scope.getId(arg1);
							
							if(!(sarg1 instanceof Function)){
								if(arg2.startsWith(Script.REF)){
									Scope sarg2 = Scope.getId(arg2);
									if(!(sarg2 instanceof Function)){
										string = testOpOverride(args, index, arg1, arg2, "operator =");
										if(string) string = testOpOverride(args, index, arg1, arg2, "equals");
										if(!string && arg.equals("not="))
											args.set(index,args.get(index).equals("0")? "1" : "0");
									}
								}else{
									
								}
							}else if(arg2.startsWith(Script.REF)){
								
							}else{
								args.add(index+1,arg);
								args.add(index+2,arg2);
								index+=2;
							}
							
						}
						if(string)
							args.set(index, (arg.equals("not=")? !arg1.equals(arg2) : arg1.equals(arg2))? "1" : "0");
						
					}
				}else if(arg.equals("<") || arg.equals("<=") || arg.equals(">") || arg.equals(">=")){
					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					boolean string = isString(arg1) || isString(arg2);
					if(!string)
						try{
							double x = parseDouble(evalVars(arg1)), y = parseDouble(evalVars(arg2));
							args.set(index, (arg.equals("<")? (x < y) : arg.equals("<=")? (x <= y) : arg.equals(">")? (x > y) : (x >= y))? "1" : "0");
						}catch(NumberFormatException e){
							string = testOpOverride(args, index, arg1, arg2, "operator " + arg);
						}
					if(string){
						args.add(index + 1, arg);
						args.add(index + 2, arg2);
						index = index+2;
					}
				}
			}
			// not operator
			for(index = 0; index < args.size() - 1; index++){
				String arg = args.get(index);
				if(arg.equals("not")){
					String arg1 = args.remove(index + 1);
					if(!isString(arg1))
						arg1 = evalVars(arg1);
					boolean override = false;
					if(arg1.startsWith(Script.REF)){
						override = testOpBool(args, index, arg1);
					}
					if(!override){
						if(arg1.endsWith(".0"))
							arg1 = arg1.substring(0, arg1.length() - 2);
						args.set(index, arg1.equals("0")? "1" : "0");
					}
				}
			}
			// and operator
			for(index = 1; index < args.size() - 1; index++){
				String arg = args.get(index);
				if(arg.equals("and")){
					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					if(!isString(arg1))
						arg1 = evalVars(arg1);
					if(!isString(arg2))
						arg2 = evalVars(arg2);
					boolean no_override = true;
					if(arg1.startsWith(Script.REF) || arg2.startsWith(Script.REF))
						no_override = testOpOverride(args, index, arg1, arg2, "operator and");
					if(no_override){
						if(arg1.endsWith(".0"))
							arg1 = arg1.substring(0, arg1.length() - 2);
						if(arg2.endsWith(".0"))
							arg2 = arg2.substring(0, arg2.length() - 2);
						args.set(index, arg1.equals("0")? "0" : arg2.equals("0")? "0" : arg2);
					}
				}
			}
			// or operator
			for(index = 1; index < args.size() - 1; index++){
				String arg = args.get(index);
				if(arg.equals("or")){
					index--;
					String arg1 = args.get(index);
					args.remove(index + 1);
					String arg2 = args.remove(index + 1);
					if(!isString(arg1))
						arg1 = evalVars(arg1);
					if(!isString(arg2))
						arg2 = evalVars(arg2);
					boolean no_override = true;
					if(arg1.startsWith(Script.REF) || arg2.startsWith(Script.REF))
						no_override = testOpOverride(args, index, arg1, arg2, "operator and");
					if(no_override){
						if(arg1.endsWith(".0"))
							arg1 = arg1.substring(0, arg1.length() - 2);
						if(arg2.endsWith(".0"))
							arg2 = arg2.substring(0, arg2.length() - 2);
						args.set(index, arg1.equals("0")? (arg2.equals("0")? "0" : arg2) : arg1);
					}
				}
			}
		}
	}*/

	/*private boolean testOpOverride(List<String> args, int index, String arg1_, String arg2_, String op){
		String arg1 = evalVars(arg1_), arg2 = evalVars(arg2_);
		if(arg1.startsWith(Script.REF)){
			Scope scope = Scope.getId(arg1);
			Function func;
			String var = null;
			try{
				if((scope instanceof Struct) && (var = scope.getVar(op, this)).startsWith(Script.REF) && Scope.getId(var) instanceof Function && (func = (Function) Scope.getId(var)).args_length() == 1)
					args.set(index, func.call(this, Arrays.asList(arg2)));
				else if(scope instanceof ListScope && (op.equals("equals") || op.equals("operator ="))){
					ListScope list1 = (ListScope)scope;
					if(!arg2.startsWith(Script.REF)) return true;
					Scope scope2 = Scope.getId(arg2);
					if(!(scope2 instanceof ListScope)) return true;
					ListScope list2 = (ListScope)scope2;
					args.set(index,list1.equals(list2)? "1" : "0");
				}
				else
					return true;// debug("result op = "+args.get(index)+
								// " var = "+var);
			}catch(VarNotExists ex){
				return true;
			}
			return false;
		}else if(arg2.startsWith(Script.REF)){
			if(op.equals("equals") || op.equals("operator =") || op.equals("operator not="))
				return testOpOverride(args, index, arg2, arg1, op);
		}
		return true;
	}

	private boolean testOpBool(List<String> args, int index, String arg1){
		arg1 = evalVars(arg1);
		if(arg1.startsWith(Script.REF)){
			Scope scope = Scope.getId(arg1);
			Function func;
			String var;
			try{
				if(scope instanceof Struct && (var = scope.getVar("operator bool", this)).startsWith(Script.REF) && Scope.getId(var) instanceof Function && (func = (Function) Scope.getId(var)).args_length() == 0)
					args.set(index, func.call(this, new ArrayList<String>()));
			}catch(VarNotExists ex){
				return true;
			}
			return false;
		}
		return true;
	}*/

	
	public static boolean isString(String str){
		return str.startsWith("\"") && str.endsWith("\"") && str.length() > 1;
	}

	public static String evalString(String str){
		if(isString(str))
			return str.substring(1, str.length() - 1);
		return str;
	}

	public static int parseInt(String str){
		if(str.endsWith("t"))
			return 16 * Integer.decode(str.substring(0, str.length() - 1));
		return Integer.decode(str);
	}

	public static double parseDouble(String str){
		if(str.endsWith("t"))
			return Tile.SIZE * Double.valueOf(str.substring(0, str.length() - 1));
		return Double.valueOf(str);
	}
}
