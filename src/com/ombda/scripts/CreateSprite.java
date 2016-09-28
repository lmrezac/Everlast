package com.ombda.scripts;

import java.util.Arrays;

import javax.swing.ImageIcon;

import com.ombda.Images;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Sprite;
import com.ombda.Tile;

public class CreateSprite implements ScriptStep{
	private int spriteId;
	private String spriteX, spriteY;
	private ImageIcon spriteImage;
	private String mapName;
	public CreateSprite(int spriteId,String imageName,String x, String y,String mapName){
		this.spriteId = spriteId;
		this.spriteX = x;
		this.spriteY = y;
		spriteImage = Images.retrieve(imageName);
		this.mapName = mapName;
	}
	
	@Override
	public void execute(Panel game,Script script){
		Map map;
		if(mapName != null)
			map = Map.get(mapName);
		else map = game.getPlayer().getMap();
		int x = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(spriteX)));
		int y = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(spriteY)));
		Sprite s = new Sprite(x,y,spriteImage,spriteId);
		
		s.setMap(map);
	}

	@Override
	public boolean done(){
		return true;
	}


	//format: sprite <int literal : id> <int : x> <int : y> <string literal : image name> [string literal : map name] 
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("sprite");
		if(args.length != 5 && args.length != 6) throw new RuntimeException("Expected 5 arguments passed to script step: sprite (got:"+Arrays.toString(args)+")");
		int id = Script.parseInt(args[1]);
		String map = null;
		if(args.length == 6)
			map = Script.parseString(args[5]);
		return new CreateSprite(id,Script.parseString(args[4]),Script.parseString(args[2]),Script.parseString(args[3]),map);
	}

}
