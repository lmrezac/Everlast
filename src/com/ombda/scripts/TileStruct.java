package com.ombda.scripts;

import java.util.Arrays;
import java.util.List;

import com.ombda.Tile;

public class TileStruct extends Struct{
	private Tile tile;
	public TileStruct(Tile t) {
		super(Scope.tile_type, Arrays.asList("id","isAnimated","getAnimationFrame"));
		this.tile = t;
		this.setFinalVar("isAnimated", new Function(null,null,false){
			public int args_length(){ return 0; }
			public String call(Scope scopeIn, List<String> args){
				return null;
			}
		}.getIdStr(), this);
	}
	public void setVar(String varname, String value, boolean isfinal, Scope scope){
		if(varname.equals("id")){
			throw new RuntimeException("Cannot set variable id, it is final");
		}
		super.setVar(varname,value,isfinal,scope);
	}

}
