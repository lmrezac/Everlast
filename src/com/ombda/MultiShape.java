package com.ombda;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class MultiShape implements Shape{
	private List<Shape> shapes;
	public MultiShape(List<Shape> shapes){
		this.shapes = shapes;
	}
	
	@Override
	public boolean contains(Point2D arg0){
		//if(shapes.isEmpty()) return fal;
		for(Shape shape : shapes){
			if(shape.contains(arg0)) return true;
		}
		return false;
	}

	@Override
	public boolean contains(Rectangle2D arg0){
		if(shapes.isEmpty()) return false;
		Area area = createArea();
		
		return area.contains(arg0);
	}
	
	public Area createArea(){
		Area area = new Area();
		for(Shape shape : shapes)
			area.add(new Area(shape));
		return area;
	}

	@Override
	public boolean contains(double arg0, double arg1){
		if(shapes.isEmpty()) return false;
		for(Shape shape : shapes){
			if(shape.contains(arg0,arg1)) return true;
		}
		return false;
	}

	@Override
	public boolean contains(double arg0, double arg1, double arg2, double arg3){
		//if(shapes.isEmpty()) return false;
		for(Shape shape : shapes){
			if(shape.contains(arg0,arg1,arg2,arg3)) return true;
		}
		return false;
	}

	@Override
	public Rectangle getBounds(){
		return null;
	}

	@Override
	public Rectangle2D getBounds2D(){
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0){
		return createArea().getPathIterator(arg0);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0, double arg1){
		return createArea().getPathIterator(arg0,arg1);
	}

	@Override
	public boolean intersects(Rectangle2D arg0){
		if(shapes.isEmpty()) return false;
		return createArea().intersects(arg0);
	}

	@Override
	public boolean intersects(double arg0, double arg1, double arg2, double arg3){
		if(shapes.isEmpty()) return false;
		return createArea().intersects(arg0,arg1,arg2,arg3);
	}

}
