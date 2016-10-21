package com.ombda.scripts;

import java.io.PrintStream;

public class VarNotExists extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Exception ex = null;
	public VarNotExists(String msg) {
		super(msg);
	}
	public VarNotExists(String msg, Exception ex){
		super(msg);
		this.ex = ex;
	}

	public VarNotExists() {}
	
	@Override
	public void printStackTrace(PrintStream ps){
		if(ex != null)
			ex.printStackTrace(ps);
		else super.printStackTrace(ps);
	}
}