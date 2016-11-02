package com.ombda;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class AnimatedImage extends ImageIcon{
	private static final long serialVersionUID = 7744250593953834113L;
	private BufferedImage[] images;
	public int index;
	private int frametime = 0;
	private int indexTime = 0;
	private int height, width;
	private boolean animate = true;
	public AnimatedImage(BufferedImage[] i,int frametime){
		images = i;
		index = 0;
		height = i[0].getHeight();
		width = i[0].getWidth();
		this.frametime = frametime;
		this.indexTime = frametime;
	}
	@Override
	public Image getImage(){
		return images[incrementIndex()];
	}
	private int incrementIndex(){
		if(animate){
			indexTime--;
			if(indexTime <= 0){
				indexTime = frametime;
				index++;
				if(index >= images.length)
					index = 0;
			}
		}
		return index;
	}
	@Override
	public int getIconHeight(){
		return height;
	}
	@Override
	public int getIconWidth(){
		return width;
	}
	public void animate(boolean b){
		animate = b;
	}
	
}
