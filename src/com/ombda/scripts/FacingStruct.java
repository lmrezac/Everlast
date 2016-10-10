package com.ombda.scripts;

import java.util.ArrayList;

import com.ombda.Facing;

public class FacingStruct extends Struct{
	public final Facing facing;
	public FacingStruct(Facing facing) {
		super(Scope.facing_type, new ArrayList<String>());
		this.facing = facing;
		
	}

}
