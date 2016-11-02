package com.ombda.scripts.steps;

import java.util.List;

import javax.swing.ImageIcon;

import com.ombda.Images;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Tile;
import com.ombda.entities.Sprite;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;

public class CreateSprite extends ScriptStep{
	private int spriteId;
	private String spriteX, spriteY;
	private ImageIcon spriteImage;
	private String mapName;
	//sprite <int literal:id> <int:x> <int:y> <string:image> [string:map name]
	public CreateSprite(List<String> args){
		if(args.size() != 4 && args.size() != 5) throw new RuntimeException("Expected 5 arguments passed to script step: sprite (got:"+args+")");
		this.spriteId = Script.parseInt(args.get(0));
		this.mapName = null;
		if(args.size() == 5)
			this.mapName = Script.evalString(args.get(4));
		this.spriteX = args.get(1);
		this.spriteY = args.get(2);
		spriteImage = Images.retrieve(Script.evalString(args.get(3)));
	}
	
	@Override
	public void execute(Scope script){
		Map map;
		if(mapName != null)
			map = Map.get(mapName);
		else map = Panel.getInstance().getPlayer().getMap();
		int x = (Tile.SIZE/16) * Script.parseInt(script.evalVars(spriteX));
		int y = (Tile.SIZE/16) * Script.parseInt(script.evalVars(spriteY));
		Sprite s = new Sprite(x,y,spriteImage,spriteId);
		s.setMap(map);
	}

	@Override
	public boolean done(){
		return true;
	}

}
