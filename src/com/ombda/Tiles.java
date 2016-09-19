package com.ombda;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;

import static com.ombda.Images.retrieve;

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
	public static void incrementAnimationFrames(){
		for(Tile t : Tile.tiles)
			if(t != null)
				t.incrementFrame();
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
		GRASS_BOTTOM_RIGHT = 0x16,
		BRICKS_LEFT = 0x17,
		BRICKS = 0x18,
		BRICKS_RIGHT = 0x19,
		BRICKS_BOTTOM_LEFT = 0x20,
		BRICKS_BOTTOM = 0x21,
		BRICKS_BOTTOM_RIGHT = 0x22,
		GRAY_FLOOR = 0x23,
		GRAY_FLOOR_DARK = 0x24,
		GRAY_FLOOR_TOP_LEFT = 0x25,
		GRAY_FLOOR_TOP_RIGHT = 0x26,
		GRAY_FLOOR_BOTTOM_LEFT = 0x27,
		GRAY_FLOOR_BOTTOM_RIGHT = 0x28,
		BRICK_WINDOW = 0x29,
		DOOR = 0x30,
		CREAM_WALL = 0x31,
		CREAM_WALL_BOTTOM = 0x32,
		CREAM_WALL_TOP = 0x33,
		CREAM_WALL_CRACK = 0x34,
		CREAM_WALL_MOLD = 0x35,
		SOLID_TILE = 0x36,
		BRICK_WINDOW_CLOSED = 0x37,
		SIDEWALK_TOP_LEFT = 0x38,
		SIDEWALK_TOP = 0x39,
		SIDEWALK_TOP_RIGHT = 0x40,
		SIDEWALK_LEFT = 0x41,
		SIDEWALK = 0x42,
		SIDEWALK_RIGHT = 0x43,
		SIDEWALK_BOTTOM_LEFT = 0x44,
		SIDEWALK_BOTTOM = 0x45,
		SIDEWALK_BOTTOM_RIGHT = 0x46;
	public static void loadTiles(){
		new Tile(new ImageIcon(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB)),EMPTY);
		
		new Tile(BUSH_TOP_LEFT,retrieve("tiles/bush/0"),FULL);
		new Tile(BUSH_TOP,retrieve("tiles/bush/1"),FULL);
		new Tile(BUSH_TOP_RIGHT,retrieve("tiles/bush/2"),FULL);
		new Tile(BUSH_LEFT,retrieve("tiles/bush/3"),FULL);
		new Tile(BUSH_MIDDLE,retrieve("tiles/bush/4"),FULL);
		new Tile(BUSH_RIGHT,retrieve("tiles/bush/5"),FULL);
		new Tile(BUSH_BOTTOM_LEFT,retrieve("tiles/bush/6"),FULL);
		new Tile(BUSH_BOTTOM,retrieve("tiles/bush/7"),FULL);
		new Tile(BUSH_BOTTOM_RIGHT,retrieve("tiles/bush/8"),FULL);
		new Tile(BUSH_CORNER_BOTTOM_RIGHT,retrieve("tiles/bush/9"),FULL);
		new Tile(BUSH_CORNER_BOTTOM_LEFT,retrieve("tiles/bush/10"),FULL);
		new Tile(BUSH_CORNER_TOP_RIGHT,retrieve("tiles/bush/11"),FULL);
		new Tile(BUSH_CORNER_TOP_LEFT,retrieve("tiles/bush/12"),FULL);
		
		new Tile(GRASS_TOP_LEFT,retrieve("tiles/grass/0"),EMPTY);
		new Tile(GRASS_TOP,retrieve("tiles/grass/1"),EMPTY);
		new Tile(GRASS_TOP_RIGHT,retrieve("tiles/grass/2"),EMPTY);
		new Tile(GRASS_LEFT,retrieve("tiles/grass/3"),EMPTY);
		new Tile(GRASS_MIDDLE,retrieve("tiles/grass/4"),EMPTY);
		new Tile(GRASS_RIGHT,retrieve("tiles/grass/5"),EMPTY);
		new Tile(GRASS_BOTTOM_LEFT,retrieve("tiles/grass/6"),EMPTY);
		new Tile(GRASS_BOTTOM,retrieve("tiles/grass/7"),EMPTY);
		new Tile(GRASS_BOTTOM_RIGHT,retrieve("tiles/grass/8"),EMPTY);
		
		new Tile(BRICKS_LEFT,retrieve("intro/bricks_left"),FULL);
		new Tile(BRICKS,retrieve("intro/bricks"),FULL);
		new Tile(BRICKS_RIGHT,retrieve("intro/bricks_right"),FULL);
		new Tile(BRICKS_BOTTOM_LEFT,retrieve("intro/bricks_bottom_left"),FULL);
		new Tile(BRICKS_BOTTOM,retrieve("intro/bricks_bottom"),FULL);
		new Tile(BRICKS_BOTTOM_RIGHT,retrieve("intro/bricks_bottom_right"),FULL);
		
		new Tile(GRAY_FLOOR,retrieve("tiles/intro/gray_floor_light"),EMPTY);
		new Tile(GRAY_FLOOR_DARK,retrieve("tiles/intro/gray_floor_dark"),EMPTY);
		new Tile(GRAY_FLOOR_TOP_LEFT,retrieve("tiles/intro/gray_floor_circle/0"),EMPTY);
		new Tile(GRAY_FLOOR_TOP_RIGHT,retrieve("tiles/intro/gray_floor_circle/1"),EMPTY);
		new Tile(GRAY_FLOOR_BOTTOM_LEFT,retrieve("tiles/intro/gray_floor_circle/2"),EMPTY);
		new Tile(GRAY_FLOOR_BOTTOM_RIGHT,retrieve("tiles/intro/gray_floor_circle/3"),EMPTY);
		
		new Tile(BRICK_WINDOW,retrieve("tiles/intro/city/brick_window"),FULL);
		new Tile(BRICK_WINDOW_CLOSED,retrieve("tiles/intro/city/brick_window_closed"),FULL);
		
		new Tile(DOOR,retrieve("tiles/intro/wood_door"),EMPTY);
		
		new Tile(CREAM_WALL,retrieve("tiles/intro/cream_wall"),FULL);
		new Tile(CREAM_WALL_BOTTOM,retrieve("intro/cream_wall_bottom"),FULL);
		new Tile(CREAM_WALL_TOP,retrieve("intro/cream_wall_top"),FULL);
		new Tile(CREAM_WALL_CRACK,retrieve("intro/cream_wall_bottom_crack"),FULL);
		new Tile(CREAM_WALL_MOLD,retrieve("intro/cream_wall_top_mold"),FULL);
		
		new Tile(SOLID_TILE,new ImageIcon(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB)),FULL);
		
		Shape TOP = new Rectangle2D.Double(0,0,16,8),
				LEFT = new Rectangle2D.Double(0,0,7,16),
				RIGHT = new Rectangle2D.Double(9,0,7,16),
				BOTTOM = new Rectangle2D.Double(0,8,16,8);
		new Tile(SIDEWALK_TOP_LEFT,retrieve("tiles/intro/city/sidewalk/0"),new MultiShape(Arrays.asList(LEFT,TOP)));
		new Tile(SIDEWALK_TOP,retrieve("tiles/intro/city/sidewalk/1"),TOP);
		new Tile(SIDEWALK_TOP_RIGHT,retrieve("tiles/intro/city/sidewalk/2"),new MultiShape(Arrays.asList(TOP,RIGHT)));
		new Tile(SIDEWALK_LEFT,retrieve("tiles/intro/city/sidewalk/3"),LEFT);
		new Tile(SIDEWALK,retrieve("tiles/intro/city/sidewalk/4"),EMPTY);
		new Tile(SIDEWALK_RIGHT,retrieve("tiles/intro/city/sidewalk/5"),RIGHT);
		new Tile(SIDEWALK_BOTTOM_LEFT,retrieve("tiles/intro/city/sidewalk/6"),new MultiShape(Arrays.asList(LEFT,BOTTOM)));
		new Tile(SIDEWALK_BOTTOM,retrieve("tiles/intro/city/sidewalk/7"),BOTTOM);
		new Tile(SIDEWALK_BOTTOM_RIGHT,retrieve("tiles/intro/city/sidewalk/8"),new MultiShape(Arrays.asList(BOTTOM,RIGHT)));

		
	}


}
