package com.ombda;

public class Point{
	public double x, y;
	public Point(){x = 0; y = 0;}
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	public String toString(){
		return "("+x+","+y+")";
	}
}
