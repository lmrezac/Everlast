package com.ombda;

import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tiles{
	public static final Shape FULL = new Rectangle2D.Double(0,0,16,16), TRIANGLE_TL, TRIANGLE_TR, TRIANGLE_BL, TRIANGLE_BR, EMPTY = new MultiShape(new ArrayList<Shape>());
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
	
	public static final short 
		AIR = 0x0,
		BUSH_TOP_LEFT = 0x1, 
		BUSH_TOP = 0x2, 
		BUSH_TOP_RIGHT= 0x3,
		BUSH_LEFT = 0x4,
		BUSH_MIDDLE = 0x5, 
		BUSH_RIGHT = 0x6, 
		BUSH_BOTTOM_LEFT = 0x7, 
		BUSH_BOTTOM = 0x8, 
		BUSH_BOTTOM_RIGHT = 0x9, 
		BUSH_CORNER_BOTTOM_RIGHT = 0xA,
		BUSH_CORNER_BOTTOM_LEFT = 0xB, 
		BUSH_CORNER_TOP_RIGHT = 0xC, 
		BUSH_CORNER_TOP_LEFT = 0xD,
		GRASS_TOP_LEFT = 0xE,
		GRASS_TOP = 0xF,
		GRASS_TOP_RIGHT = 0x10,
		GRASS_LEFT = 0x11,
		GRASS_MIDDLE = 0x12,
		GRASS_RIGHT = 0x13,
		GRASS_BOTTOM_LEFT = 0x14,
		GRASS_BOTTOM = 0x15,
		GRASS_BOTTOM_RIGHT = 0x16;
	public static void loadTiles(){
		new Tile(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB),EMPTY);
		
		//BufferedImage[] images = loadFolder("tiles/bush");
		
		new Tile(BUSH_TOP_LEFT,load("tiles/bush/0"),FULL);
		new Tile(BUSH_TOP,load("tiles/bush/1"),FULL);
		new Tile(BUSH_TOP_RIGHT,load("tiles/bush/2"),FULL);
		new Tile(BUSH_LEFT,load("tiles/bush/3"),FULL);
		new Tile(BUSH_MIDDLE,load("tiles/bush/4"),FULL);
		new Tile(BUSH_RIGHT,load("tiles/bush/5"),FULL);
		new Tile(BUSH_BOTTOM_LEFT,load("tiles/bush/6"),FULL);
		new Tile(BUSH_BOTTOM,load("tiles/bush/7"),FULL);
		new Tile(BUSH_BOTTOM_RIGHT,load("tiles/bush/8"),FULL);
		new Tile(BUSH_CORNER_BOTTOM_RIGHT,load("tiles/bush/9"),FULL);
		new Tile(BUSH_CORNER_BOTTOM_LEFT,load("tiles/bush/10"),FULL);
		new Tile(BUSH_CORNER_TOP_RIGHT,load("tiles/bush/11"),FULL);
		new Tile(BUSH_CORNER_TOP_LEFT,load("tiles/bush/12"),FULL);
		
		//images = loadFolder("tiles/grass");
		
		new Tile(GRASS_TOP_LEFT,load("tiles/grass/0"),EMPTY);
		new Tile(GRASS_TOP,load("tiles/grass/1"),EMPTY);
		new Tile(GRASS_TOP_RIGHT,load("tiles/grass/2"),EMPTY);
		new Tile(GRASS_LEFT,load("tiles/grass/3"),EMPTY);
		new Tile(GRASS_MIDDLE,load("tiles/grass/4"),EMPTY);
		new Tile(GRASS_RIGHT,load("tiles/grass/5"),EMPTY);
		new Tile(GRASS_BOTTOM_LEFT,load("tiles/grass/6"),EMPTY);
		new Tile(GRASS_BOTTOM,load("tiles/grass/7"),EMPTY);
		new Tile(GRASS_BOTTOM_RIGHT,load("tiles/grass/8"),EMPTY);
	}
	
	public static Image load(String name){
		return Images.retrieve(name);
	}

}
