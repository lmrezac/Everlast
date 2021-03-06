package com.ombda;
import static java.lang.Math.PI;
public enum Facing{
	N (PI/2.0), NE (PI/4.0), E (0), SE (-PI/4.0), S (3.0*PI/2.0), SW (PI+PI/4.0), W (PI), NW (3.0*PI/4.0);
	private String name;
	public final double angle;
	private Facing(double angle){
		name = super.toString();//.toLowerCase().replace("n", "NORTH").replace("e", "EAST").replace("s", "SOUTH").replace("w", "WEST");
		this.angle = angle;
	}
	public String toString(){
		return name;
	}
	public static Facing fromString(String facing){
		if(facing.equals("N") || facing.equals("NORTH"))
			return Facing.N;
		else if(facing.equals("S") || facing.equals("SOUTH"))
			return Facing.S;
		else if(facing.equals("E") || facing.equals("EAST"))
			return Facing.E;
		else if(facing.equals("W") || facing.equals("WEST"))
			return Facing.W;
		else if(facing.equals("NW") || facing.equals("NORTHWEST"))
			return Facing.NW;
		else if(facing.equals("SW") || facing.equals("SOUTHWEST"))
			return Facing.SW;
		else if(facing.equals("NE") || facing.equals("NORTHEAST"))
			return Facing.NE;
		else if(facing.equals("SE") || facing.equals("SOUTHEAST"))
			return Facing.SE;
		else throw new RuntimeException("Invalid direction : "+facing);
	}
}
