package com.ombda;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
@Deprecated
public class BoundingBox{
	private String str;
	private int pos;
	private char ch;
	public Shape shape;
	public BoundingBox(String str){
		this.str = str;
		parse();
	}
	private char nextChar(){
		return ch = (++pos >= str.length())? (char)0 : str.charAt(pos);
	}
	private boolean eat(char c){
		if(ch == c){
			++pos;
			return true;
		}
		return false;
	}
	private boolean eat(String str){
		for(char c : str.toCharArray()){
			if(!eat(c)) return false;
		}
		return true;
	}
	private void assertEat(char c){
		if(!eat(c)) throw new RuntimeException("expected '"+c+"', found '"+ch+"' in string \""+str+"\" at position "+pos);
	}
	private void assertEat(String str){
		int startpos = pos;
		if(!eat(str)) throw new RuntimeException("expected "+str+" at position "+startpos+" in string \""+this.str+"\"");
	}
	private void eatWhite(){
		while(isWhitespace(ch))
			nextChar();
	}
	private int eatNumber(){
		if(!isDigit(ch))
			throw new NumberFormatException("in string \""+str+"\" at position "+pos+" and character '"+ch+"'");
		int startpos = pos;
		while(isDigit(ch)||pos < str.length())
			nextChar();
		return Integer.parseInt(str.substring(startpos, pos));
	}
	private Point eatPoint(){
		assertEat('(');
		eatWhite();
		int x = eatNumber();
		eatWhite();
		assertEat(',');
		eatWhite();
		int y = eatNumber();
		eatWhite();
		assertEat(')');
		return new Point(x,y);
	}
	private Shape eatShape(){
		assertEat('{');
		eatWhite();
		assertEat("type");
		eatWhite();
		assertEat(':');
		eatWhite();
		if(!isLetter(ch))
			throw new RuntimeException("expected either box, circle, quadrilateral, or triangle at position "+pos+" in string \""+str+"\"");
		int startpos = pos;
		while(isLetter(ch))
			nextChar();
		String type = str.substring(startpos,pos);
		eatWhite();
		assertEat(',');
		eatWhite();
		assertEat('[');
		eatWhite();
		Shape result;
		if(type.equals("circle")){
			Point p = eatPoint();
			eatWhite();
			assertEat(',');
			eatWhite();
			int radius = eatNumber();
			result = new Ellipse2D.Double(p.x-radius,p.y-radius,2*radius,2*radius);
		}else if(type.equals("box")){
			Point p = eatPoint();
			eatWhite();
			assertEat(',');
			eatWhite();
			int width = eatNumber();
			eatWhite();
			assertEat(',');
			eatWhite();
			int height = eatNumber();
			result = new Rectangle2D.Double(p.x,p.y,width,height);
		}else if(type.equals("quad")||type.equals("quadrilateral")){
			//Point[] points = new Point[4];
			Polygon poly = new Polygon();
			for(int x = 0; x < 4; x++){
				Point p = eatPoint();
				eatWhite();
				if(x != 3){
					assertEat(',');
					eatWhite();
				}
				poly.addPoint((int)p.x, (int)p.y);
			}
			result = poly;
		}else if(type.equals("triangle")){
			Polygon poly = new Polygon();
			for(int x = 0; x < 3; x++){
				Point p = eatPoint();
				eatWhite();
				if(x != 3){
					assertEat(',');
					eatWhite();
				}
				poly.addPoint((int)p.x, (int)p.y);
			}
			result = poly;
		}else throw new RuntimeException("expected either box, circle, quadrilateral, or triangle at position "+pos+" in string \""+str+"\"");
		eatWhite();
		assertEat(']');
		eatWhite();
		assertEat('}');
		return result;
	}
	public static final Shape FULL = new Rectangle2D.Double(0,0,16,16), TRIANGLE_TL, TRIANGLE_TR, TRIANGLE_BL, TRIANGLE_BR;
	static{
		Polygon t_tl = new Polygon(), t_tr = new Polygon(), t_bl = new Polygon(), t_br = new Polygon();
		t_tl.addPoint(0, 0);
		t_tl.addPoint(16, 0);
		t_tl.addPoint(0, 16);
		
		t_tr.addPoint(0, 0);
		t_tr.addPoint(16, 0);
		t_tr.addPoint(16,16);
		
		t_bl.addPoint(0, 0);
		t_bl.addPoint(0, 16);
		t_bl.addPoint(16, 16);
		
		t_br.addPoint(16, 0);
		t_br.addPoint(16, 16);
		t_br.addPoint(0, 16);
		TRIANGLE_TL = t_tl;
		TRIANGLE_TR = t_tr;
		TRIANGLE_BL = t_bl;
		TRIANGLE_BR = t_br;
	}
	private void parse(){
		this.pos = -1;
		nextChar();
		if(ch == '{'){
			this.shape = eatShape();
		}else if(isLetter(ch)){
			String preDef = str.substring(pos);
			if(preDef.equals("full"))
				this.shape = FULL;
			else if(preDef.equals("tri_tl"))
				this.shape = TRIANGLE_TL;
			else if(preDef.equals("tri_tr"))
				this.shape = TRIANGLE_TR;
			else if(preDef.equals("tri_bl"))
				this.shape = TRIANGLE_BL;
			else if(preDef.equals("tri_br"))
				this.shape = TRIANGLE_BR;
			else throw new RuntimeException("Invalid predefined shape: "+preDef);
		}else{
			assertEat('[');
			eatWhite();
			List<Shape> shapes = new ArrayList<>();
			if(ch == '{') do{
				eatWhite();
				shapes.add(eatShape());
				eatWhite();
			}while(eat(','));
			if(ch == '{')
				shapes.add(eatShape());
			eatWhite();
			assertEat(']');
			this.shape = new MultiShape(shapes);
		}
	}
	
}
