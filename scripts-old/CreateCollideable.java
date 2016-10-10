package com.ombda.scripts;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.ImageIcon;

import com.ombda.CollideableSprite;
import com.ombda.Images;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Sprite;
import com.ombda.Tile;

public class CreateCollideable implements ScriptStep{
	private int spriteId;
	private String spriteX, spriteY, width, height;
	private ImageIcon spriteImage;
	private String mapName;
	public CreateCollideable(int spriteId,String imageName,String x, String y,String mapName,String width, String height){
		this.spriteId = spriteId;
		this.spriteX = x;
		this.spriteY = y;
		spriteImage = Images.retrieve(imageName);
		this.mapName = mapName;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void execute(Panel game,Script script){
		Map map;
		if(mapName != null)
			map = Map.get(mapName);
		else map = game.getPlayer().getMap();
		int x = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(spriteX)));
		int y = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(spriteY)));
		int width = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(this.width)));
		int height = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(this.height)));
		Sprite s = new CollideableSprite(x,y,spriteImage,spriteId,new Rectangle2D.Double(x,y,width,height));
		
		s.setMap(map);
	}

	@Override
	public boolean done(){
		return true;
	}


	//format: collideable <int literal : id> <int : x> <int : y> <string literal : image name> <int : width> <int : height> [string literal : map name] 
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("sprite");
		if(args.length != 8 && args.length != 7) throw new RuntimeException("Expected 8 arguments passed to script step: collideable (got:"+Arrays.toString(args)+")");
		int id = Script.parseInt(args[1]);
		String map = null;
		if(args.length == 8)
			map = Script.parseString(args[7]);
		return new CreateCollideable(id,Script.parseString(args[4]),Script.parseString(args[2]),Script.parseString(args[3]),map,Script.parseString(args[5]),Script.parseString(args[6]));
	}

}
