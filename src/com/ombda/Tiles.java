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
		MARBLE_WINDOW_TOP_RIGHT_2 = 0x7A,
		STOP_BAR_TOP = 0x7B,
		STOP_BAR_VERT = 0x7C,
		STOP_BAR_BOTTOM = 0x7D,
		STOP_BAR_LEFT = 0x7E,
		STOP_BAR_HORIZ = 0x7F,
		STOP_BAR_RIGHT = 0x80,
		WHITE_WALL = 0x81,
		WHITE_WALL_BOTTOM = 0x82;
	public static void loadTiles(){
		new Tile(AIR,new ImageIcon(new BufferedImage(Tile.SIZE,Tile.SIZE,BufferedImage.TYPE_INT_ARGB)));
		
		new Tile(BUSH_TOP_LEFT,"tiles/bush/0",FULL);
		new Tile(BUSH_TOP,"tiles/bush/1",FULL);
		new Tile(BUSH_TOP_RIGHT,"tiles/bush/2",FULL);
		new Tile(BUSH_LEFT,"tiles/bush/3",FULL);
		new Tile(BUSH_MIDDLE,"tiles/bush/4",FULL);
		new Tile(BUSH_RIGHT,"tiles/bush/5",FULL);
		new Tile(BUSH_BOTTOM_LEFT,"tiles/bush/6",FULL);
		new Tile(BUSH_BOTTOM,"tiles/bush/7",FULL);
		new Tile(BUSH_BOTTOM_RIGHT,"tiles/bush/8",FULL);
		new Tile(BUSH_CORNER_BOTTOM_RIGHT,"tiles/bush/9",FULL);
		new Tile(BUSH_CORNER_BOTTOM_LEFT,"tiles/bush/10",FULL);
		new Tile(BUSH_CORNER_TOP_RIGHT,"tiles/bush/11",FULL);
		new Tile(BUSH_CORNER_TOP_LEFT,"tiles/bush/12",FULL);
		
		new Tile(GRASS_TOP_LEFT,"tiles/grass/0");
		new Tile(GRASS_TOP,"tiles/grass/1");
		new Tile(GRASS_TOP_RIGHT,"tiles/grass/2");
		new Tile(GRASS_LEFT,"tiles/grass/3");
		new Tile(GRASS_MIDDLE,"tiles/grass/4");
		new Tile(GRASS_RIGHT,"tiles/grass/5");
		new Tile(GRASS_BOTTOM_LEFT,"tiles/grass/6");
		new Tile(GRASS_BOTTOM,"tiles/grass/7");
		new Tile(GRASS_BOTTOM_RIGHT,"tiles/grass/8");
		
		new Tile(BRICKS_LEFT,"intro/bricks_left",FULL);
		new Tile(BRICKS,"intro/bricks",FULL);
		new Tile(BRICKS_RIGHT,"intro/bricks_right",FULL);
		new Tile(BRICKS_BOTTOM_LEFT,"intro/bricks_bottom_left",FULL);
		new Tile(BRICKS_BOTTOM,"intro/bricks_bottom",FULL);
		new Tile(BRICKS_BOTTOM_RIGHT,"intro/bricks_bottom_right",FULL);
		
		new Tile(GRAY_FLOOR,"tiles/intro/gray_floor_light");
		new Tile(GRAY_FLOOR_DARK,"tiles/intro/gray_floor_dark");
		new Tile(GRAY_FLOOR_TOP_LEFT,"tiles/intro/gray_floor_circle/0");
		new Tile(GRAY_FLOOR_TOP_RIGHT,"tiles/intro/gray_floor_circle/1");
		new Tile(GRAY_FLOOR_BOTTOM_LEFT,"tiles/intro/gray_floor_circle/2");
		new Tile(GRAY_FLOOR_BOTTOM_RIGHT,"tiles/intro/gray_floor_circle/3");
		
		new Tile(BRICK_WINDOW,"tiles/intro/city/brick_window",FULL);
		new Tile(BRICK_WINDOW_CLOSED,"tiles/intro/city/brick_window_closed",FULL);
		
		new Tile(DOOR,"tiles/intro/wood_door");
		new Tile(DOOR_GLASS,"tiles/intro/door_glass");
		
		new Tile(CREAM_WALL,"tiles/intro/cream_wall",FULL);
		new Tile(CREAM_WALL_BOTTOM,"intro/cream_wall_bottom",FULL);
		new Tile(CREAM_WALL_TOP,"intro/cream_wall_top",FULL);
		new Tile(CREAM_WALL_CRACK,"intro/cream_wall_bottom_crack",FULL);
		new Tile(CREAM_WALL_MOLD,"intro/cream_wall_top_mold",FULL);
		
		new Tile(SOLID_TILE,new ImageIcon(new BufferedImage(Tile.SIZE,Tile.SIZE,BufferedImage.TYPE_INT_ARGB)),FULL);
		
		Shape TOP = new Rectangle2D.Double(0,0,Tile.SIZE,Tile.SIZE/2),
				LEFT = new Rectangle2D.Double(0,0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE),
				RIGHT = new Rectangle2D.Double((Tile.SIZE/2)+(Tile.SIZE/16),0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE),
				BOTTOM = new Rectangle2D.Double(0,Tile.SIZE/2,Tile.SIZE,Tile.SIZE/2);
		new Tile(SIDEWALK_TOP_LEFT,"tiles/intro/city/sidewalk/0",new MultiShape(Arrays.asList(LEFT,TOP)));
		new Tile(SIDEWALK_TOP,"tiles/intro/city/sidewalk/1",TOP);
		new Tile(SIDEWALK_TOP_RIGHT,"tiles/intro/city/sidewalk/2",new MultiShape(Arrays.asList(TOP,RIGHT)));
		new Tile(SIDEWALK_LEFT,"tiles/intro/city/sidewalk/3",LEFT);
		new Tile(SIDEWALK,"tiles/intro/city/sidewalk/4");
		new Tile(SIDEWALK_RIGHT,"tiles/intro/city/sidewalk/5",RIGHT);
		new Tile(SIDEWALK_BOTTOM_LEFT,"tiles/intro/city/sidewalk/6",new MultiShape(Arrays.asList(LEFT,BOTTOM)));
		new Tile(SIDEWALK_BOTTOM,"tiles/intro/city/sidewalk/7",BOTTOM);
		new Tile(SIDEWALK_BOTTOM_RIGHT,"tiles/intro/city/sidewalk/8",new MultiShape(Arrays.asList(BOTTOM,RIGHT)));
		new Tile(SIDEWALK_CROSS_LEFT_1,"tiles/intro/city/sidewalk_cross_left/0",new Rectangle2D.Double(0,0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/16));
		new Tile(SIDEWALK_CROSS_LEFT_2,"tiles/intro/city/sidewalk_cross_left/1",BOTTOM);
		new Tile(SIDEWALK_CROSS_LEFT_3,"tiles/intro/city/sidewalk_cross_left/2",TOP);
		new Tile(SIDEWALK_CROSS_LEFT_4,"tiles/intro/city/sidewalk_cross_left/3",new Rectangle2D.Double(0,Tile.SIZE/2,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_RIGHT_1,"tiles/intro/city/sidewalk_cross_right/0",new Rectangle2D.Double((Tile.SIZE/2)+(Tile.SIZE/16),0,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/16));
		new Tile(SIDEWALK_CROSS_RIGHT_2,"tiles/intro/city/sidewalk_cross_right/1",BOTTOM);
		new Tile(SIDEWALK_CROSS_RIGHT_3,"tiles/intro/city/sidewalk_cross_right/2",TOP);
		new Tile(SIDEWALK_CROSS_RIGHT_4,"tiles/intro/city/sidewalk_cross_right/3",new Rectangle2D.Double((Tile.SIZE/2)+(Tile.SIZE/16),Tile.SIZE/2,(Tile.SIZE/2)-(Tile.SIZE/16),Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_1,"tiles/intro/city/sidewalk_cross_bottom/0",new Rectangle2D.Double(0,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_2,"tiles/intro/city/sidewalk_cross_bottom/1",new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_3,"tiles/intro/city/sidewalk_cross_bottom/2",new Rectangle2D.Double(0,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_BOTTOM_4,"tiles/intro/city/sidewalk_cross_bottom/3",new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,Tile.SIZE/2,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_1,"tiles/intro/city/sidewalk_cross_top/0",new Rectangle2D.Double(0,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_2,"tiles/intro/city/sidewalk_cross_top/1",new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_3,"tiles/intro/city/sidewalk_cross_top/2",new Rectangle2D.Double(0,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_TOP_4,"tiles/intro/city/sidewalk_cross_top/3",new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,0,Tile.SIZE/16,Tile.SIZE/2));
		new Tile(SIDEWALK_CROSS_CORNER_LT,"tiles/intro/city/sidewalk_cross_all/0");
		new Tile(SIDEWALK_CROSS_CORNER_RT,"tiles/intro/city/sidewalk_cross_all/1");
		new Tile(SIDEWALK_CROSS_CORNER_LB,"tiles/intro/city/sidewalk_cross_all/2");
		new Tile(SIDEWALK_CROSS_CORNER_RB,"tiles/intro/city/sidewalk_cross_all/3");
		
		new Tile(ROAD,"tiles/intro/city/asphalt");
		new Tile(ROAD_LINES_VERT,"tiles/intro/city/yellow_lines_vertical");
		new Tile(ROAD_LINES_HORIZ,"tiles/intro/city/yellow_lines_horizontal");
		
		new Tile(CROSSWALK_TOP,"intro/city/crosswalk_top",new Rectangle2D.Double(0,0,Tile.SIZE,Tile.SIZE/16));
		new Tile(CROSSWALK_BOTTOM,"intro/city/crosswalk_bottom",BOTTOM);
		new Tile(CROSSWALK_LEFT,"intro/city/crosswalk_left",new Rectangle2D.Double(0,0,Tile.SIZE/16,Tile.SIZE));
		new Tile(CROSSWALK_RIGHT,"intro/city/crosswalk_right",new Rectangle2D.Double(Tile.SIZE-Tile.SIZE/16,0,Tile.SIZE/16,Tile.SIZE));
		
		new Tile(STOPLIGHT_POLE_TOP_LB,"tiles/intro/city/pole_top_lb");
		new Tile(STOPLIGHT_POLE_TOP_RT,"tiles/intro/city/pole_top_rt");
		new Tile(STOPLIGHT,"tiles/intro/city/stoplight");
		new Tile(STOPLIGHT_TOP,"tiles/intro/city/stoplight_top");
		new Tile(STOPLIGHT_LEFT,"tiles/intro/city/stoplight_left");
		new Tile(STOPLIGHT_RIGHT,"tiles/intro/city/stoplight_right");
		new Tile(STOPLIGHT_SUPPORT_VERT,"tiles/intro/city/support_vertical");
		new Tile(STOPLIGHT_SUPPORT_HORIZ,"tiles/intro/city/support_horizontal");
	
		new Tile(LINE_TOP_LEFT,"tiles/lines/0");
		new Tile(LINE_TOP,"tiles/lines/1");
		new Tile(LINE_TOP_RIGHT,"tiles/lines/2");
		new Tile(LINE_LEFT,"tiles/lines/3");
		new Tile(LINE_MIDDLE,"tiles/lines/4");
		new Tile(LINE_RIGHT,"tiles/lines/5");
		new Tile(LINE_BOTTOM_LEFT,"tiles/lines/6");
		new Tile(LINE_BOTTOM,"tiles/lines/7");
		new Tile(LINE_BOTTOM_RIGHT,"tiles/lines/8");
		
		new Tile(MARBLE,"tiles/intro/city/marble",FULL);
		new Tile(MARBLE_BRICKS,"tiles/intro/city/marble_bricks",FULL);
		new Tile(MARBLE_BRICKS_LEFT,"tiles/intro/city/marble_bricks_end/0",FULL);
		new Tile(MARBLE_BRICKS_RIGHT,"tiles/intro/city/marble_bricks_end/1",FULL);
		new Tile(MARBLE_BOTTOM,"tiles/intro/city/marble_bottom",FULL);
		new Tile(MARBLE_ROOF_TOP_LEFT,"tiles/intro/city/marble_rooftop/0",RIGHT);
		new Tile(MARBLE_ROOF_BOTTOM_LEFT,"tiles/intro/city/marble_rooftop/4",RIGHT);
		new Tile(MARBLE_ROOF_TOP,"tiles/intro/city/marble_rooftop/2",FULL);
		new Tile(MARBLE_ROOF_TOP_DECORATED,"tiles/intro/city/marble_rooftop/1",FULL);
		new Tile(MARBLE_ROOF_BOTTOM,"tiles/intro/city/marble_rooftop/5",FULL);
		new Tile(MARBLE_ROOF_TOP_RIGHT,"tiles/intro/city/marble_rooftop/3",LEFT);
		new Tile(MARBLE_ROOF_BOTTOM_RIGHT,"tiles/intro/city/marble_rooftop/7",LEFT);
		new Tile(MARBLE_SEPARATOR_1,"tiles/intro/city/marble_separator",FULL);
		new Tile(MARBLE_SEPARATOR_2,"tiles/intro/city/marble_separator2",FULL);
		new Tile(MARBLE_SEPARATOR_3,"tiles/intro/city/marble_separator3",FULL);
		
		new Tile(MARBLE_WINDOW,"tiles/intro/city/marble_window");
		new Tile(MARBLE_WINDOW_SM,"tiles/intro/city/marble_window_small");
		new Tile(WINDOW_SM_1,"tiles/intro/city/smallest_window",FULL);
		new Tile(WINDOW_SM_2,"tiles/intro/city/smallest_window2",FULL);
		new Tile(WINDOW_SM_3,"tiles/intro/city/smallest_window3",FULL);
		new Tile(MARBLE_WINDOW_LG,"tiles/intro/city/marble_window_large");
		new Tile(MARBLE_WINDOW_TOP_LEFT_1,"tiles/intro/city/marble_window_top/0",FULL);
		new Tile(MARBLE_WINDOW_TOP_LEFT_2,"tiles/intro/city/marble_window_top/1",FULL);
		new Tile(MARBLE_WINDOW_TOP_RIGHT_1,"tiles/intro/city/marble_window_top/2",FULL);
		new Tile(MARBLE_WINDOW_TOP_RIGHT_2,"tiles/intro/city/marble_window_top/3",FULL);
		
		new Tile(STOP_BAR_TOP,"tiles/intro/city/stop_bars/0");
		new Tile(STOP_BAR_VERT,"tiles/intro/city/stop_bars/2");
		new Tile(STOP_BAR_BOTTOM,"tiles/intro/city/stop_bars/4");
		new Tile(STOP_BAR_LEFT,"tiles/intro/city/stop_bars/1");
		new Tile(STOP_BAR_HORIZ,"tiles/intro/city/stop_bars/3");
		new Tile(STOP_BAR_RIGHT,"tiles/intro/city/stop_bars/5");
		
		new Tile(WHITE_WALL,"tiles/intro/lab/white_wall",FULL);
		new Tile(WHITE_WALL_BOTTOM,"tiles/intro/lab/white_wall_bottom",FULL);
	}


}
