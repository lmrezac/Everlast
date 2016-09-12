package com.ombda.scripts;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.ombda.Images;
import com.ombda.NPC;
import com.ombda.Panel;

public class CreateNPC implements ScriptStep{
	private Image[] images;
	private int hash;
	private String strX, strY;
	public CreateNPC(int hash, String x, String y, Image[] im){
		if(im.length != 16)
			throw new RuntimeException("Invalid number of images passed to CreateNPC, need 16");
		this.images = im;
		this.hash = hash;
		this.strX = x;
		this.strY = y;
	}
	@Override
	public void execute(Panel game, Script script){
		int x = Script.parseInt(script.evalVar(Script.parseString(strX)));
		int y = Script.parseInt(script.evalVar(Script.parseString(strY)));
		new NPC(x,y,hash,images);
	}

	@Override
	public boolean done(){
		return true;
	}
	//format: ncp <int literal : id> <int : x> <int : y> [N still, NE still, E still, SE still, S still, SW still, W still, NW still, N walk, NE walk, E walk, SE walk, S walk, SW walk, W walk, NW walk]
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("npc");
		if(args.length != 20) throw new RuntimeException("Expected 21 arguments passed to script step: msg");
		Image[] images = new BufferedImage[16];
		int id = Script.parseInt(args[1]);
		for(int i = 4; i < 21; i++){
			String loc = Script.parseString(args[i]);
			images[i-5] = Images.retrieve(loc);
		}
		return new CreateNPC(id,Script.parseString(args[2]),Script.parseString(args[3]),images);
	}
}
