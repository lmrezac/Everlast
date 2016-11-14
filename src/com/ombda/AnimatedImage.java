package com.ombda;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.Undefined;
public class AnimatedImage extends ImageIcon implements JSObject{
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
	@Override
	public Object call(Object arg0, Object... arg1){
		throw new RuntimeException("Type AnimatedImage is not a function");
	}
	@Override
	public Object eval(String arg0){
		return null;
	}
	@Override
	public String getClassName(){
		return "AnimatedImage";
	}
	@Override
	public Object getMember(String name){
		if(name.equals("isAnimated"))
			return true;
		else if(name.equals("animationFrame"))
			return index;
		else return null;
	}
	@Override
	public Object getSlot(int arg0){
		return null;
	}
	@Override
	public boolean hasMember(String name){
		return name.equals("animationFrame") || name.equals("isAnimated");
	}
	@Override
	public boolean hasSlot(int arg0){
		return false;
	}
	@Override
	public boolean isArray(){
		return false;
	}
	@Override
	public boolean isFunction(){
		return false;
	}
	@Override
	public boolean isInstance(Object arg0){
		return arg0 instanceof AnimatedImage;
	}
	@Override
	public boolean isInstanceOf(Object arg0){
		return false;
	}
	@Override
	public boolean isStrictFunction(){
		return false;
	}
	@Override
	public Set<String> keySet(){
		HashSet<String> set = new HashSet<>();
		set.add("isAnimated");
		set.add("animationFrame");
		return set;
	}
	@Override
	public Object newObject(Object... arg0){
		try{
			return AnimatedImage.class.getConstructor(BufferedImage[].class,int.class).newInstance(arg0);
		}catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e){
			debug(e.getMessage());
			if(printStackTrace)
				e.printStackTrace();
			throw new FatalError();
		}
	}
	@Override
	public void removeMember(String arg0){
		throw new RuntimeException("Cannot remove members from type AnimatedImage");
	}
	@Override
	public void setMember(String name, Object obj){
		if(name.equals("isAnimated")){
			animate(obj == null? false : obj instanceof Boolean? (Boolean)obj : obj instanceof Number? ((Number)obj).doubleValue() != 0.0 : obj instanceof Undefined? false : true);
		}else if(name.equals("animationFrame")){
			index = ((Number)obj).intValue();
		}
	}
	@Override
	public void setSlot(int arg0, Object arg1){
		
	}
	@Override
	@Deprecated
	public double toNumber(){
		return 0;
	}
	@Override
	public Collection<Object> values(){
		Collection<Object> c = new ArrayList<>();
		c.add(this.animate);
		c.add(this.index);
		return c;
	}
	
}
