package com.ombda.scripts;

import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import com.ombda.Images;
import com.ombda.NPC;
import com.ombda.Panel;
import com.ombda.Tile;

public class CreateNPC implements ScriptStep{
	private ImageIcon[] images;
	private int hash,yminus;
	private String strX, strY;
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
	public void execute(Panel game, Script script){
		int x = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(strX)));
		int y = (Tile.SIZE/16) * Script.parseInt(script.evalVar(Script.parseString(strY)));
		new NPC(0,0,hash,yminus,images,new Rectangle2D.Double(0,0,x,y));
	}

	@Override
	public boolean done(){
		return true;
	}
	//format: npc <int literal : id> <int : width> <int : height> <int : yminus> [N still, NE still, E still, SE still, S still, SW still, W still, NW still, N walk, NE walk, E walk, SE walk, S walk, SW walk, W walk, NW walk]
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("npc");
		if(args.length != 21) throw new RuntimeException("Expected 20 arguments passed to script step: msg");
		ImageIcon[] images = new ImageIcon[16];
		int id = Script.parseInt(args[1]);
		for(int i = 5; i < 21; i++){
			String loc = Script.parseString(args[i]);
			images[i-5] = Images.retrieve(loc);
		}
		return new CreateNPC(id,Script.parseInt(args[4]),Script.parseString(args[2]),Script.parseString(args[3]),images);
	}
}
