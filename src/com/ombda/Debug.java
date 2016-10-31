package com.ombda;

import java.io.PrintStream;
import java.util.Locale;

public class Debug extends PrintStream{
	public static boolean debug = true;
	public static boolean printStackTrace = true;
	public static PrintStream parent = System.err;
	public Debug(){
		super(System.err);
		//System.setOut(this);
		//System.setErr(this);
		
	}
	private static void printInfo(){
		StackTraceElement elem = Thread.currentThread().getStackTrace()[3];
		System.err.print("("+elem.getClassName()+".java:"+elem.getLineNumber()+") "+elem.getMethodName()+"(): ");
	}
	//Appends the specified character to this output stream.
	public PrintStream append(char c){
		if(debug)
			return System.err.append(c);
		return this;
	}
	//Appends the specified character sequence to this output stream.
	public PrintStream append(CharSequence csq){
		if(debug)
			return System.err.append(csq);
		return this;
	}
	//Appends a subsequence of the specified character sequence to this output stream.
	public PrintStream append(CharSequence csq, int start, int end){
		if(debug)
			return System.err.append(csq,start,end);
		return this;
	}

	//Flushes the stream and checks its error state.
	public boolean checkError(){
		return System.err.checkError();
	}
	//Clears the internal error state of this stream.
	protected void clearError(){
		super.clearError();
	}
	//Closes the stream.
	public void close(){
		System.err.close();
	}
	//Flushes the stream.
	public void flush(){
		System.err.flush();
	}
	//Writes a formatted string to this output stream
	//using the specified format string and arguments.
	public PrintStream format(Locale l, String format, Object... args){
		if(debug)
			return System.err.format(l, format,args);
		return this;
	}
	//Writes a formatted string to this output stream using the specified format string and arguments.
	public PrintStream format(String format, Object... args){
		if(debug)
			return System.err.format(format, args);
		return this;
	}
	//Prints a boolean value.
	public void print(boolean b){
		if(debug)
			System.err.print(b);
	}
	//Prints a character.
	public void	print(char c){
		if(debug)
			System.err.print(c);
	}
	//Prints an array of characters.
	public void	print(char[] s){
		if(debug)
			System.err.print(s);
	}
	//Prints a double-precision floating-point number.
	public void	print(double d){
		if(debug)
			System.err.print(d);
	}
	//Prints a floating-point number.
	public void	print(float f){
		if(debug)
			System.err.print(f);
	}
	//Prints an integer.
	public void	print(int i){
		if(debug)
			System.err.print(i);
	}
	//Prints a long integer.
	public void	print(long l){
		if(debug)
			System.err.print(l);
	}
	//Prints an object.
	public void	print(Object obj){
		if(debug){
			String str = obj.toString();
			if(str.startsWith("@"))
				str = str.substring(1);
			else
				printInfo();
			System.err.print(str);
		}
	}
	//Prints a string.
	public void	print(String s){
		if(debug){
			if(s.startsWith("@"))
				s = s.substring(1);
			else printInfo();
			System.err.print(s);
		}
	}
	//A convenience method to write a formatted string to this output stream
	//using the specified format string and arguments.
	public PrintStream printf(Locale l, String format, Object... args){
		if(debug)
			return System.err.printf(l, format,args);
		return this;
	}
	//A convenience method to write a formatted string to this output stream
	//using the specified format string and arguments.
	public PrintStream printf(String format, Object... args){
		if(debug)
			return System.err.printf(format, args);
		return this;
	}
	//Terminates the current line by writing the line separator string.
	public void	println(){
		if(debug){
			printInfo();
			System.err.println();
		}
	}
	//Prints a boolean and then terminate the line.
	public void	println(boolean x){
		if(debug){
			printInfo();
			System.err.println(x);
		}
	}
	//Prints a character and then terminate the line.
	public void	println(char x){
		if(debug){
			printInfo();
			System.err.println(x);
		}
	}
	//Prints an array of characters and then terminate the line.
	public void	println(char[] x){
		if(debug){
			printInfo();
			System.err.println(x);
		}
	}
	//Prints a double and then terminate the line.
	public void	println(double x){
		if(debug){
			printInfo();
			System.err.println(x);
		}
	}
	//Prints a float and then terminate the line.
	public void	println(float x){
		if(debug){
			printInfo();
			System.err.println(x);
		}
	}
	//Prints an integer and then terminate the line.
	public void	println(int x){
		if(debug){
			printInfo();
			System.err.println(x);
		}
	}
	//Prints a long and then terminate the line.
	public void	println(long x){
		if(debug){
			printInfo();
			System.err.println(x);
		}
	}
	//Prints an Object and then terminate the line.
	public void	println(Object x){
		if(debug){
			if(x == null){
				printInfo();
				System.err.println("null");
				return;
			}
			String s = x.toString();
			if(s.startsWith("@"))
				s = s.substring(1);
			else printInfo();
			System.err.println(s);
		}
	}
	//Prints a String and then terminate the line.
	public void	println(String x){
		if(debug){
			if(x.startsWith("@"))
				x = x.substring(1);
			else printInfo();
			System.err.println(x);
		}
	}
	//Sets the error state of the stream to true.
	protected void	setError(){
		super.setError();
	}
	//Writes len bytes from the specified byte array
	//starting at offset off to this stream.
	public void	write(byte[] buf, int off, int len){
		if(debug)
			System.err.write(buf,off,len);
	}
	//Writes the specified byte to this stream.
	public void	write(int b){
		if(debug)
			System.err.write(b);
	}
	
	public static void debug(Object o){
		if(debug){
			if(o == null){
				System.err.print("null");
				return;
			}
			String s = o.toString();
			if(s.startsWith("@"))
				s = s.substring(1);
			else printInfo();
			if(s.startsWith("#")){
				s = s.substring(1);
				System.err.print(s);
			}else System.err.println(s);
		}
	}
	
}
