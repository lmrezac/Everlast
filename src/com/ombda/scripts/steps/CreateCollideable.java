package com.ombda.scripts.steps;

import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.ImageIcon;

import com.ombda.CollideableSprite;
import com.ombda.Images;
import com.ombda.Map;
import com.ombda.Panel;
import com.ombda.Sprite;
import com.ombda.Tile;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;

public class CreateCollideable extends ScriptStep{
	private int spriteId;
	private String spriteX, spriteY, width, height;
	private ImageIcon spriteImage;
	private String mapName;
	//format: collideable <int literal : id> <int : x> <int : y> <string literal : image name> <int : width> <int : height> [string literal : map name]
	public CreateCollideable(List<String> args){
		if(args.size() != 7 && args.size() != 6) throw new RuntimeException("Expected 7 arguments passed to script step: collideable (got:"+args+")");
		this.spriteId = Script.parseInt(args.get(0));
		this.mapName = null;
		if(args.size() == 7)
			this.mapName = Script.evalString(args.get(6));
		this.spriteX = args.get(1);
		this.spriteY = args.get(2);
		spriteImage = Images.retrieve(Script.evalString(args.get(3)));
		this.width = args.get(4);
		this.height = args.get(5);
	}
	
	@Override
	public void execute(Scope script){
		Map map;
		if(mapName != null)
			map = Map.get(mapName);
		else map = Panel.getInstance().getPlayer().getMap();
		int x = (Tile.SIZE/16) * Script.parseInt(script.evalVars(spriteX));
		int y = (Tile.SIZE/16) * Script.parseInt(script.evalVars(spriteY));
		int width = (Tile.SIZE/16) * Script.parseInt(script.evalVars(this.width));
		int height = (Tile.SIZE/16) * Script.parseInt(script.evalVars(this.height));
		Sprite s = new CollideableSprite(x,y,spriteImage,spriteId,new Rectangle2D.Double(x,y,width,height));
		s.setMap(map);
	}

	@Override
	public boolean done(){
		return true;
	}

}
