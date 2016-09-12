package com.ombda;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class AnimatedImage extends Image{
	private BufferedImage[] images;
	private int index;
	private int height, width;
	public AnimatedImage(BufferedImage[] i){
		images = i;
		index = 0;
		height = i[0].getHeight();
		width = i[0].getWidth();
	}
	public BufferedImage currentImage(){
		return images[index];
	}
	@Override
	public Graphics getGraphics(){
		return images[index].getGraphics();
	}
	@Override
	public int getHeight(ImageObserver observer){
		return height;
	}
	@Override
	public Object getProperty(String name, ImageObserver observer){
		return images[index].getProperty(name,observer);
	}
	@Override
	public ImageProducer getSource(){
		ImageProducer img = images[index++].getSource();
		if(index >= images.length)
			index = 0;
		return img;
	}
	public Raster getData(){
		return images[index].getData();
	}
	@Override
	public int getWidth(ImageObserver observer){
		return width;
	}
	
}
