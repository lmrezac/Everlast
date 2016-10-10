package com.ombda.scripts;

public class VarNotExists extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VarNotExists(String msg) {
		super(msg);
	}

	public VarNotExists() {
	}
}