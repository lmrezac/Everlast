package com.ombda.scripts.steps;

import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.ImageIcon;

import com.ombda.Facing;
import com.ombda.Images;
import com.ombda.Tile;
import com.ombda.entities.NPC;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;

public class CreateNPC extends ScriptStep{
	private ImageIcon[] images;
	private int hash,yminus;
	private String strX, strY;
	//format: npc <int literal : id> <int : width> <int : height> <int : yminus> [N still, NE still, E still, SE still, S still, SW still, W still, NW still, N walk, NE walk, E walk, SE walk, S walk, SW walk, W walk, NW walk]
	public CreateNPC(List<String> args){
		this(Script.parseInt(args.get(0)),Script.parseInt(args.get(3)),args.get(1),args.get(2),evalImages(args));
	}
	private static ImageIcon[] evalImages(List<String> args){
		//if(args.size() != 20) throw new RuntimeException("Expected 20 arguments passed to script step: npc");
		if(args.size() < 4) throw new RuntimeException("Expected 5 - 20 arguments passed to script step: npc");
		ImageIcon[] images = new ImageIcon[16];
		/*for(int i = 4; i < 20; i++){
			String loc = Script.evalString(args.get(i));
			images[i-4] = Images.retrieve(loc);
		}*/
		if(args.size() > 4){
		args = args.subList(4, args.size());
		int i = 0;
		boolean prefix = false;
		for(String arg : args){
			int index;
			if((index = arg.lastIndexOf(":")) != -1){
				prefix = true;
				String facing = arg.substring(0,index);
				arg = arg.substring(index+1);
				if(facing.startsWith("walk:")){
					facing = facing.substring(5);
					Facing f = Facing.fromString(facing);
					images[f.ordinal()+8] = Images.retrieve(arg);
				}else if(facing.startsWith("every:")){
					facing = facing.substring(6);
					Facing f = Facing.fromString(facing);
					images[f.ordinal()] = Images.retrieve(arg);
					images[f.ordinal()+8] = Images.retrieve(arg);
				}else{
					if(facing.startsWith("still:")) facing = facing.substring(6);
					Facing f = Facing.fromString(facing);
					images[f.ordinal()] = Images.retrieve(arg);
				}
			}else{
				if(prefix) throw new RuntimeException("In script step: npc: When you start using the prefix notation, ever argument after that must be in prefix notation.");
				images[i] = Images.retrieve(arg);
				i++;
			}
		}
		}
		for(int i = 0; i < images.length; i++){
			if(images[i] == null){
				images[i] = Images.getError();
			}
		}
		return images;
	}
	public CreateNPC(int hash, int yminus,String x, String y, ImageIcon[] im){
		if(im.length != 16)
			throw new RuntimeException("Invalid number of images passed to CreateNPC, need 16");
		this.images = im;
		this.hash = hash;
		this.yminus = yminus;
		this.strX = x;
		this.strY = y;
	}
	@Override
	public void execute(Scope scope){
		int x, y;
		if(strX.equals("auto"))
			x = images[0].getIconWidth();
		else	
			x = (Tile.SIZE/16) * Script.parseInt(scope.evalVars(strX));
		if(strY.equals("auto"))
			y = images[0].getIconHeight();
		else
			y = (Tile.SIZE/16) * Script.parseInt(scope.evalVars(strY));
		new NPC(0,0,hash,yminus,images,new Rectangle2D.Double(0,0,x,y));
	}

	@Override
	public boolean done(){
		return true;
	}
	
}
