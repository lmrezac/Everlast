package com.ombda;

public class FatalError extends RuntimeException{
	public FatalError(){
	}
	public FatalError(Throwable arg1){
		super(arg1);
	}

}
