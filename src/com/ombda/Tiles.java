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
	public static final Shape FULL = new Rectangle2D.Double(0,0,Tile.SIZE,Tile.SIZE), TRIANGLE_TL, TRIANGLE_TR, TRIANGLE_BL, TRIANGLE_BR, EMPTY = new MultiShape(new ArrayList<Shape>());
	static{
		Polygon t_tl = new Polygon(), t_tr = new Polygon(), t_bl = new Polygon(), t_br = new Polygon();

		t_tl.addPoint(0, 0);
		t_tl.addPoint(Tile.SIZE, 0);
		t_tl.addPoint(0, Tile.SIZE);
		
		t_tr.addPoint(0, 0);
		t_tr.addPoint(Tile.SIZE, 0);
		t_tr.addPoint(Tile.SIZE,Tile.SIZE);
		
		t_bl.addPoint(0, 0);
		t_bl.addPoint(0, Tile.SIZE);
		t_bl.addPoint(Tile.SIZE, Tile.SIZE);
		
		t_br.addPoint(Tile.SIZE, 0);
		t_br.addPoint(Tile.SIZE, Tile.SIZE);
		t_br.addPoint(0, Tile.SIZE);
		
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
		DOOR_GLASS = 0x58,
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
		SIDEWALK_BOTTOM_RIGHT = 0x46,
		SIDEWALK_CROSS_LEFT_1 = 0x1A,
		SIDEWALK_CROSS_LEFT_2 = 0x1B,
		SIDEWALK_CROSS_LEFT_3 = 0x50,
		SIDEWALK_CROSS_LEFT_4 = 0x51,
		SIDEWALK_CROSS_RIGHT_1 = 0x1C,
		SIDEWALK_CROSS_RIGHT_2 = 0x1D,
		SIDEWALK_CROSS_RIGHT_3 = 0x52,
		SIDEWALK_CROSS_RIGHT_4 = 0x53,
		SIDEWALK_CROSS_BOTTOM_1 = 0x48,
		SIDEWALK_CROSS_BOTTOM_2 = 0x49,
		SIDEWALK_CROSS_BOTTOM_3 = 0x4A,
		SIDEWALK_CROSS_BOTTOM_4 = 0x4B,
		SIDEWALK_CROSS_TOP_1 = 0x4C,
		SIDEWALK_CROSS_TOP_2 = 0x4D,
		SIDEWALK_CROSS_TOP_3 = 0x4E,
		SIDEWALK_CROSS_TOP_4 = 0x4F,
		ROAD = 0x2A,
		ROAD_LINES_VERT = 0x2B,
		ROAD_LINES_HORIZ = 0x2C,
		STOPLIGHT_POLE_TOP_LB = 0x2D,
		STOPLIGHT_POLE_TOP_RT = 0x2E,
		STOPLIGHT = 0x2F,
		STOPLIGHT_TOP = 0x3A,
		STOPLIGHT_LEFT = 0x3B,
		STOPLIGHT_RIGHT = 0x3C,
		STOPLIGHT_SUPPORT_VERT = 0x3D,
		STOPLIGHT_SUPPORT_HORIZ = 0x3E,
		CROSSWALK_LEFT = 0x3F,
		CROSSWALK_RIGHT = 0x47,
		CROSSWALK_TOP = 0x1E,
		CROSSWALK_BOTTOM = 0x1F,
		SIDEWALK_CROSS_CORNER_LT = 0x54,
		SIDEWALK_CROSS_CORNER_LB = 0x55,
		SIDEWALK_CROSS_CORNER_RT = 0x56,
		SIDEWALK_CROSS_CORNER_RB = 0x57,
		LINE_TOP_LEFT = 0x59,
		LINE_TOP = 0x5A,
		LINE_TOP_RIGHT = 0x5B,
		LINE_LEFT = 0x5C,
		LINE_MIDDLE = 0x5D,
		LINE_RIGHT = 0x5E,
		LINE_BOTTOM_LEFT = 0x5F,
		LINE_BOTTOM = 0x60,
		LINE_BOTTOM_RIGHT = 0x61,
		MARBLE = 0x62,
		MARBLE_BRICKS = 0x63,
		MARBLE_BRICKS_LEFT = 0x64,
		MARBLE_BRICKS_RIGHT = 0x65,
		MARBLE_BOTTOM = 0x66,
		MARBLE_ROOF_TOP_LEFT = 0x67,
		MARBLE_ROOF_BOTTOM_LEFT = 0x68,
		MARBLE_ROOF_TOP = 0x69,
		MARBLE_ROOF_TOP_DECORATED = 0x6A,
		MARBLE_ROOF_BOTTOM = 0x6B,
		MARBLE_ROOF_TOP_RIGHT = 0x6C,
		MARBLE_ROOF_BOTTOM_RIGHT = 0x6D,
		MARBLE_SEPARATOR_1 = 0x6E,
		MARBLE_SEPARATOR_2 = 0x6F,
		MARBLE_SEPARATOR_3 = 0x70,
		MARBLE_WINDOW = 0x71,
		MARBLE_WINDOW_SM = 0x72,
		WINDOW_SM_1 = 0x73,
		WINDOW_SM_2 = 0x74,
		WINDOW_SM_3 = 0x75,
		MARBLE_WINDOW_LG = 0x76,
		MARBLE_WINDOW_TOP_LEFT_1 = 0x77,
		MARBLE_WINDOW_TOP_LEFT_2 = 0x78,
		MARBLE_WINDOW_TOP_RIGHT_1 = 0x79,
		MARBLE_WINDOW_TOP_RIGHT_2 = 0x7A;
	public static void loadTiles(){
		new Tile(new ImageIcon(new BufferedImage(Tile.SIZE,Tile.SIZE,BufferedImage.TYPE_INT_ARGB)),EMPTY);
		
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
		new Tile(DOOR_GLASS,retrieve("tiles/intro/door_glass"),EMPTY);
		
		new Tile(CREAM_WALL,retrieve("tiles/intro/cream_wall"),FULL);
		new Tile(CREAM_WALL_BOTTOM,retrieve("intro/cream_wall_bottom"),FULL);
		new Tile(CREAM_WALL_TOP,retrieve("intro/cream_wall_top"),FULL);
		new Tile(CREAM_WALL_CRACK,retrieve("intro/cream_wall_bottom_crack"),FULL);
		new Tile(CREAM_WALL_MOLD,retrieve("intro/cream_wall_top_mold"),FULL);
		
		new Tile(SOLID_TILE,new ImageIcon(new BufferedImage(Tile.SIZE,Tile.SIZE,BufferedImage.TYPE_INT_ARGB)),FULL);
		
		Shape TOP = new Rectangle2D.Double(0,0,Tile.SIZE,Tile.SIZE/2),
				LEFT = new Rectangle2D.Double(0,0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE),
				RIGHT = new Rectangle2D.Double((Tile.SIZE/2)+(Tile.SIZE/16),0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE),
				BOTTOM = new Rectangle2D.Double(0,Tile.SIZE/2,Tile.SIZE,Tile.SIZE/2);
		new Tile(SIDEWALK_TOP_LEFT,retrieve("tiles/intro/city/sidewalk/0"),new MultiShape(Arrays.asList(LEFT,TOP)));
		new Tile(SIDEWALK_TOP,retrieve("tiles/intro/city/sidewalk/1"),TOP);
		new Tile(SIDEWALK_TOP_RIGHT,retrieve("tiles/intro/city/sidewalk/2"),new MultiShape(Arrays.asList(TOP,RIGHT)));
		new Tile(SIDEWALK_LEFT,retrieve("tiles/intro/city/sidewalk/3"),LEFT);
		new Tile(SIDEWALK,retrieve("tiles/intro/city/sidewalk/4"),EMPTY);
		new Tile(SIDEWALK_RIGHT,retrieve("tiles/intro/city/sidewalk/5"),RIGHT);
		new Tile(SIDEWALK_BOTTOM_LEFT,retrieve("tiles/intro/city/sidewalk/6"),new MultiShape(Arrays.asList(LEFT,BOTTOM)));
		new Tile(SIDEWALK_BOTTOM,retrieve("tiles/intro/city/sidewalk/7"),BOTTOM);
		new Tile(SIDEWALK_BOTTOM_RIGHT,retrieve("tiles/intro/city/sidewalk/8"),new MultiShape(Arrays.asList(BOTTOM,RIGHT)));
		new Tile(SIDEWALK_CROSS_LEFT_1,retrieve("tiles/intro/city/sidewalk_cross_left/0"),new Rectangle2D.Double(0,0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/16));
		new Tile(SIDEWALK_CROSS_LEFT_2,retrieve("tiles/intro/city/sidewalk_cross_left/1"),BOTTOM);
		new Tile(SIDEWALK_CROSS_LEFT_3,retrieve("tiles/intro/city/sidewalk_cross_left/2"),TOP);
		new Tile(SIDEWALK_CROSS_LEFT_4,retrieve("tiles/intro/city/sidewalk_cross_left/3"),new Rectangle2D.Double(0,Tile.SIZE/2,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_RIGHT_1,retrieve("tiles/intro/city/sidewalk_cross_right/0"),new Rectangle2D.Double((Tile.SIZE/2)+(Tile.SIZE/16),0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/16));
		new Tile(SIDEWALK_CROSS_RIGHT_2,retrieve("tiles/intro/city/sidewalk_cross_right/1"),BOTTOM);
		new Tile(SIDEWALK_CROSS_RIGHT_3,retrieve("tiles/intro/city/sidewalk_cross_right/2"),TOP);
		new Tile(SIDEWALK_CROSS_RIGHT_4,retrieve("tiles/intro/city/sidewalk_cross_right/3"),new Rectangle2D.Double((Tile.SIZE/2)+(Tile.SIZE/16),Tile.SIZE/2,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_1,retrieve("tiles/intro/city/sidewalk_cross_bottom/0"),new Rectangle2D.Double(0,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_2,retrieve("tiles/intro/city/sidewalk_cross_bottom/1"),new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_3,retrieve("tiles/intro/city/sidewalk_cross_bottom/2"),new Rectangle2D.Double(0,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_4,retrieve("tiles/intro/city/sidewalk_cross_bottom/3"),new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_1,retrieve("tiles/intro/city/sidewalk_cross_top/0"),new Rectangle2D.Double(0,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_2,retrieve("tiles/intro/city/sidewalk_cross_top/1"),new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_3,retrieve("tiles/intro/city/sidewalk_cross_top/2"),new Rectangle2D.Double(0,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_4,retrieve("tiles/intro/city/sidewalk_cross_top/3"),new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_CORNER_LT,retrieve("tiles/intro/city/sidewalk_cross_all/0"),EMPTY);
		new Tile(SIDEWALK_CROSS_CORNER_RT,retrieve("tiles/intro/city/sidewalk_cross_all/1"),EMPTY);
		new Tile(SIDEWALK_CROSS_CORNER_LB,retrieve("tiles/intro/city/sidewalk_cross_all/2"),EMPTY);
		new Tile(SIDEWALK_CROSS_CORNER_RB,retrieve("tiles/intro/city/sidewalk_cross_all/3"),EMPTY);
		
		new Tile(ROAD,retrieve("tiles/intro/city/asphalt"),EMPTY);
		new Tile(ROAD_LINES_VERT,retrieve("tiles/intro/city/yellow_lines_vertical"),EMPTY);
		new Tile(ROAD_LINES_HORIZ,retrieve("tiles/intro/city/yellow_lines_horizontal"),EMPTY);
		
		new Tile(CROSSWALK_TOP,retrieve("intro/city/crosswalk_top"),new Rectangle2D.Double(0,0,Tile.SIZE,Tile.SIZE/16));
		new Tile(CROSSWALK_BOTTOM,retrieve("intro/city/crosswalk_bottom"),BOTTOM);
		new Tile(CROSSWALK_LEFT,retrieve("intro/city/crosswalk_left"),new Rectangle2D.Double(0,0,Tile.SIZE/16,Tile.SIZE));
		new Tile(CROSSWALK_RIGHT,retrieve("intro/city/crosswalk_right"),new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,0,Tile.SIZE/16,Tile.SIZE));
		
		new Tile(STOPLIGHT_POLE_TOP_LB,retrieve("tiles/intro/city/pole_top_lb"),EMPTY);
		new Tile(STOPLIGHT_POLE_TOP_RT,retrieve("tiles/intro/city/pole_top_rt"),EMPTY);
		new Tile(STOPLIGHT,retrieve("tiles/intro/city/stoplight"),EMPTY);
		new Tile(STOPLIGHT_TOP,retrieve("tiles/intro/city/stoplight_top"),EMPTY);
		new Tile(STOPLIGHT_LEFT,retrieve("tiles/intro/city/stoplight_left"),EMPTY);
		new Tile(STOPLIGHT_RIGHT,retrieve("tiles/intro/city/stoplight_right"),EMPTY);
		new Tile(STOPLIGHT_SUPPORT_VERT,retrieve("tiles/intro/city/support_vertical"),EMPTY);
		new Tile(STOPLIGHT_SUPPORT_HORIZ,retrieve("tiles/intro/city/support_horizontal"),EMPTY);
	
		new Tile(LINE_TOP_LEFT,retrieve("tiles/lines/0"),EMPTY);
		new Tile(LINE_TOP,retrieve("tiles/lines/1"),EMPTY);
		new Tile(LINE_TOP_RIGHT,retrieve("tiles/lines/2"),EMPTY);
		new Tile(LINE_LEFT,retrieve("tiles/lines/3"),EMPTY);
		new Tile(LINE_MIDDLE,retrieve("tiles/lines/4"),EMPTY);
		new Tile(LINE_RIGHT,retrieve("tiles/lines/5"),EMPTY);
		new Tile(LINE_BOTTOM_LEFT,retrieve("tiles/lines/6"),EMPTY);
		new Tile(LINE_BOTTOM,retrieve("tiles/lines/7"),EMPTY);
		new Tile(LINE_BOTTOM_RIGHT,retrieve("tiles/lines/8"),EMPTY);
		
		new Tile(MARBLE,retrieve("tiles/intro/city/marble"),FULL);
		new Tile(MARBLE_BRICKS,retrieve("tiles/intro/city/marble_bricks"),FULL);
		new Tile(MARBLE_BRICKS_LEFT,retrieve("tiles/intro/city/marble_bricks_end/0"),FULL);
		new Tile(MARBLE_BRICKS_RIGHT,retrieve("tiles/intro/city/marble_bricks_end/1"),FULL);
		new Tile(MARBLE_BOTTOM,retrieve("tiles/intro/city/marble_bottom"),FULL);
		new Tile(MARBLE_ROOF_TOP_LEFT,retrieve("tiles/intro/city/marble_rooftop/0"),RIGHT);
		new Tile(MARBLE_ROOF_BOTTOM_LEFT,retrieve("tiles/intro/city/marble_rooftop/4"),RIGHT);
		new Tile(MARBLE_ROOF_TOP,retrieve("tiles/intro/city/marble_rooftop/2"),FULL);
		new Tile(MARBLE_ROOF_TOP_DECORATED,retrieve("tiles/intro/city/marble_rooftop/1"),FULL);
		new Tile(MARBLE_ROOF_BOTTOM,retrieve("tiles/intro/city/marble_rooftop/5"),FULL);
		new Tile(MARBLE_ROOF_TOP_RIGHT,retrieve("tiles/intro/city/marble_rooftop/3"),LEFT);
		new Tile(MARBLE_ROOF_BOTTOM_RIGHT,retrieve("tiles/intro/city/marble_rooftop/7"),LEFT);
		new Tile(MARBLE_SEPARATOR_1,retrieve("tiles/intro/city/marble_separator"),FULL);
		new Tile(MARBLE_SEPARATOR_2,retrieve("tiles/intro/city/marble_separator2"),FULL);
		new Tile(MARBLE_SEPARATOR_3,retrieve("tiles/intro/city/marble_separator3"),FULL);
		
		new Tile(MARBLE_WINDOW,retrieve("tiles/intro/city/marble_window"),EMPTY);
		new Tile(MARBLE_WINDOW_SM,retrieve("tiles/intro/city/marble_window_small"),EMPTY);
		new Tile(WINDOW_SM_1,retrieve("tiles/intro/city/smallest_window"),FULL);
		new Tile(WINDOW_SM_2,retrieve("tiles/intro/city/smallest_window2"),FULL);
		new Tile(WINDOW_SM_3,retrieve("tiles/intro/city/smallest_window3"),FULL);
		new Tile(MARBLE_WINDOW_LG,retrieve("tiles/intro/city/marble_window_large"),EMPTY);
		new Tile(MARBLE_WINDOW_TOP_LEFT_1,retrieve("tiles/intro/city/marble_window_top/0"),FULL);
		new Tile(MARBLE_WINDOW_TOP_LEFT_2,retrieve("tiles/intro/city/marble_window_top/1"),FULL);
		new Tile(MARBLE_WINDOW_TOP_RIGHT_1,retrieve("tiles/intro/city/marble_window_top/2"),FULL);
		new Tile(MARBLE_WINDOW_TOP_RIGHT_2,retrieve("tiles/intro/city/marble_window_top/3"),FULL);
	}


}
