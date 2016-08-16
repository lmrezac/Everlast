package com.ombda;

import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TilemapOld{
	private BufferedImage image;
	private Shape[] boundingBoxes;
	private Tile[] tiles;
	public final int width, height;
	public TilemapOld(int id){
		this.image = Images.load("images/tiles/texturemap"+id+".png");
		width = (image.getWidth()-1)/17;
		height = (image.getHeight()-1)/17;
		tiles = new Tile[width*height];
		loadBoundingBoxes("images/tiles/boundingboxes"+id+".csv");
		loadTiles();
	}
	private static String linePattern;
	static{
		String point = "\\(\\d+,\\d+\\)";
		String pointList = "\\["+point+"(,"+point+")*\\]";
		String shapePattern = "\\{(type:(box|(quad(rilateral)?)|triangle),"+pointList+")|(type:circle,\\["+point+",\\d+\\])\\}";
		linePattern = "\\d+,\\d+,\\[("+shapePattern+")?(,"+shapePattern+")*\\]";
	}
	private void loadBoundingBoxes(String file){
		List<String> lines = Files.read(file);
		boundingBoxes = new Shape[tiles.length];
		Shape basic = new MultiShape(new ArrayList<Shape>());
		for(int i = 0; i < boundingBoxes.length; i++)
			boundingBoxes[i] = basic;
		for(int line = 0; line < lines.size(); line++){
			String s = lines.get(line);
			//values read in form of:   TILEX,TILEY,[PATTERN,...]
			//if(!s.matches(linePattern))
			//	throw new RuntimeException("Error: reading collision data for file "+file+": at line "+line);
			
			BoundingBox box = new BoundingBox(s);
			boundingBoxes[box.tileY*width+box.tileX] = box.shape;
		}
	}
	private void loadTiles(){
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int pixelX = 18+17*(x-1);
				int pixelY = 18+17*(y-1);
				tiles[y*width+x] = new Tile(Images.crop(image, pixelX, pixelY, 16, 16),boundingBoxes[y*width+x]);
			}
		}
	}
	public Tile getTile(int tileX, int tileY){
		return tiles[tileY*width+tileX];
	}
	public Tile getTile(int id){
		return tiles[id];
	}
}
