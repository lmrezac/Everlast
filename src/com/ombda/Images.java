package com.ombda;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static com.ombda.Debug.*;
import static com.ombda.Files.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Images{
	
	
	private static BufferedImage error;
	static{
		try{
			error = load("error.png",true);
		}catch(RuntimeException e){
			error = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
			Random r = new Random();
			for(int y = 0; y < 16; y++){
				for(int x = 0; x < 16; x++){
					error.setRGB(x, y, r.nextInt(new Color(255,255,255).getRGB()));
				}
			}
		}
	}
	public static BufferedImage getError(){
		return error;
	}
	public static boolean isImageFile(String filename){
		return filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".bmp");
	}
	public static boolean isImageFile(File file){
		return isImageFile(file.getAbsolutePath());
	}
	public static BufferedImage load(String filename){
		return load(filename,false);
		/*if(!isImageFile(filename)){
			int i = filename.lastIndexOf('.');
			if(i != -1){ //if extension was given
				String extension = filename.substring(i);
				throw new RuntimeException(f.getAbsolutePath()+": "+extension+" is not a recognized image format.");
			}else{
				boolean found = false;
				for(String extension : new String[]{".png",".jpg",".bmp"}){
					f = new File(localize(filename+extension));
					if(f.exists()){
						found = true;
						break;
					}
				}
				if(!found){
					String name = filename;
					String postfix = "";
					String separator = Files.isWindowsOS? "\\" : "/";
					if(filename.contains(separator)){
						i = filename.lastIndexOf(separator);
						postfix = "\\"+filename.substring(0, i);
						name = filename.substring(i+1);
					}
					throw new RuntimeException("Could not find an image file in "+Files.dir+"images"+postfix+" with name \""+name+"\\.");
				}
			}
		}*/
	}
	public static BufferedImage load(String filename, boolean throwexception){
		return load(new File(localize("images\\"+filename)),throwexception);
	}
	public static BufferedImage load(File file){
		return load(file,false);
	}
	public static BufferedImage load(File file, boolean throwexception){
		String filename = file.getAbsolutePath();
		int i = filename.lastIndexOf('.');
		if(i != -1){
			String extension = filename.substring(i);
			if(!isImageFile(filename)){
				if(throwexception)
					throw new RuntimeException(file.getAbsolutePath()+": "+extension+" is not a recognized image format.");
				else return getError();
			}
		}
		try {
			BufferedImage bimg = ImageIO.read(file);
			debug("Loaded image:"+file.getAbsolutePath());
			return bimg;
		} catch (IOException e) {
			if(debug){
				debug("Error loading "+file.getAbsolutePath());
				if(printStackTrace)
					e.printStackTrace();
			}
			if(throwexception)
				throw new RuntimeException("Error loading "+file.getAbsolutePath());
			else return getError();
		}
	}
	
	/** Horizontally flips img. */
	public static BufferedImage horizontalFlip(BufferedImage img) {   
        int w = img.getWidth();   
        int h = img.getHeight();   
        BufferedImage dimg = new BufferedImage(w, h, img.getColorModel().getTransparency());     
        Graphics2D g = dimg.createGraphics();   
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);   
        g.dispose();   
        return dimg;   
    }  
	
	/** Vertically flips img. */
	public static BufferedImage verticalFlip(BufferedImage img) {   
        int w = img.getWidth();   
        int h = img.getHeight();   
        BufferedImage dimg = new BufferedImage(w, h, img.getColorModel().getTransparency());   
        Graphics2D g = dimg.createGraphics();   
        g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);   
        g.dispose();   
        return dimg;   
    }  
	public static BufferedImage resize(int newWidth, int newHeight, BufferedImage img){
		BufferedImage newImg = toBufferedImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT));
		return newImg;
	}
	
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	public static BufferedImage rotate(BufferedImage src, int angle) { 

		BufferedImage bufferedImage  = src;
		 AffineTransform tx = new AffineTransform();
		    tx.rotate(0.5*Math.PI, bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);

		    AffineTransformOp op = new AffineTransformOp(tx,
		        AffineTransformOp.TYPE_BILINEAR);
		    bufferedImage = op.filter(bufferedImage, null);
		    return bufferedImage;
	}
	private static int average(int...numbers){
		int total = 0;
		for(int i : numbers)
			total+=i;
		int average = (int)(total/(double)numbers.length);
		return average;
	}
	private static int limit(int i){
		return (i>255)? 255: (i<0)? 0: i;
	}
	
	public static BufferedImage overlay(BufferedImage bimg1, BufferedImage bimg2){
		bimg2 = resize(bimg2,bimg1.getWidth(),bimg1.getHeight());
		
		BufferedImage bOut = new BufferedImage(bimg1.getWidth(),bimg1.getHeight(),BufferedImage.TYPE_INT_ARGB);
		
		for(int y = 0; y < bOut.getHeight(); y++){
			for(int x = 0; x < bOut.getWidth(); x++){
				Color c1 = new Color(bimg1.getRGB(x, y));
				Color c2 = new Color(bimg2.getRGB(x,y));
				int r,g,b;
				r = average(c1.getRed(),c2.getRed());
				g = average(c1.getGreen(),c2.getGreen());
				b = average(c1.getBlue(),c2.getBlue());
				bOut.setRGB(x, y, (new Color(r,g,b)).getRGB());
			}
		}
		
		return bOut;
	}
	public static BufferedImage resize(BufferedImage bimg, int width, int height){
		//scales image
		Image img = bimg.getScaledInstance(width, height, 0);
		//output image
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		
		return bimage;
	}
	public static BufferedImage crop(BufferedImage original,int x, int y,int width, int height){
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(original, -x,-y, original.getWidth(), original.getHeight(), null);
		g.dispose();
		return resizedImage;
	}
	public static BufferedImage highContrast(BufferedImage original, int amount){
		BufferedImage bOut = new BufferedImage(original.getWidth(),original.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < original.getHeight(); y++){
		for(int x = 0; x < original.getWidth(); x++){
			Color c = new Color(original.getRGB(x, y));
			double average = (c.getRed()+c.getGreen()+c.getBlue())/2.0;
			int r,g,b;
			if(average>255/2){
				r = limit(c.getRed()+amount);
				g = limit(c.getGreen()+amount);
				b = limit(c.getBlue()+amount);
			}else{
				r = limit(c.getRed()-amount);
				g = limit(c.getGreen()-amount);
				b = limit(c.getBlue()-amount);
			}
			bOut.setRGB(x,y,(new Color(r,g,b)).getRGB());  
		}
		}
		return bOut;
	}
	public static BufferedImage negative(BufferedImage original){
		BufferedImage bOut = new BufferedImage(original.getWidth(),original.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < original.getHeight(); y++){
		for(int x = 0; x < original.getWidth(); x++){
			Color c = new Color(original.getRGB(x,y));
			int r,g,b;
			r = 255-c.getRed();
			g = 255-c.getGreen();
			b = 255-c.getBlue();
			bOut.setRGB(x,y,(new Color(r,g,b)).getRGB());
		}
		}
		return bOut;
	}
	public static BufferedImage grayscale(BufferedImage original){
		BufferedImage bOut = new BufferedImage(original.getWidth(),original.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < original.getHeight(); y++){
		for(int x = 0; x < original.getWidth(); x++){
			Color c = new Color(original.getRGB(x,y));
			int r,g,b;
			r = c.getRed();
			g = c.getGreen();
			b = c.getBlue();
			int a = (int)((r+g+b)/3.0);
			bOut.setRGB(x,y,(new Color(a,a,a)).getRGB());
		}
		}
		return bOut;
	}
	public static BufferedImage copyTo(BufferedImage base, BufferedImage source, int startx, int starty){
		BufferedImage bOut = new BufferedImage(base.getWidth(),base.getHeight(),BufferedImage.TYPE_INT_ARGB);
		if(base.getWidth()< source.getWidth()){
			source = crop(source,0,0, base.getWidth(), source.getHeight());
		}if(base.getHeight()< source.getHeight()){
			source = crop(source,0,0, source.getWidth(),base.getHeight());
		}
		bOut = new BufferedImage(base.getColorModel(), base.getRaster(), false, null);
		Color sourcePixel;
		for(int x = startx,xi=0; xi < source.getWidth(); x++,xi++){
			  for(int y = starty,yi=0; yi < source.getHeight(); y++,yi++){
				  try{
				  sourcePixel = new Color(source.getRGB(xi,yi));
				  //targetPixel = new Color(base.getRGB(x,y));
				  bOut.setRGB(x,y,sourcePixel.getRGB());
				  //just in case the cropping didn't work
				  }catch(IndexOutOfBoundsException e){}
			  }
		  }
		
		return bOut;
		
	}
	public static void showImage(BufferedImage bimg){
		JFrame frame = new JFrame();
		Container pane = frame.getContentPane();
		ImageIcon ii = new ImageIcon(bimg);
		JLabel label = new JLabel(ii);
		pane.add(label);
		frame.pack();
		frame.setVisible(true);
	}
	public static BufferedImage addStatic(BufferedImage bimg, int potentcy){
		Random rand = new Random();
		BufferedImage bOut = new BufferedImage(bimg.getWidth(),bimg.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < bimg.getHeight();y++){
			for(int x = 0; x < bimg.getWidth(); x++){
				Color c = new Color(bimg.getRGB(x, y));
				int r,g,b;
				r =  c.getRed();
				g = c.getGreen();
				b = c.getBlue();
				r = limit(r+rand.nextInt(potentcy)-(potentcy/2));
				g = limit(g+rand.nextInt(potentcy)-(potentcy/2));
				b = limit(b+rand.nextInt(potentcy)-(potentcy/2));
				bOut.setRGB(x, y, (new Color(r,g,b)).getRGB());
			}
		}
		return bOut;
	}
	public static BufferedImage replaceColors(BufferedImage bimg, Color cl, Color d,int potentcy, boolean blend){
		BufferedImage bOut = new BufferedImage(bimg.getWidth(),bimg.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < bimg.getHeight();y++){
			for(int x = 0; x < bimg.getWidth();x++){
				Color c = new Color(bimg.getRGB(x,y));
				int r,g,b;
				r =  c.getRed();
				g = c.getGreen();
				b = c.getBlue();
				int br = cl.getRed();
				int bg = cl.getGreen();
				int bb = cl.getBlue();
				int dr = d.getRed();
				int dg = d.getGreen();
				int db = d.getBlue();
				
				if(blend){
					if(inRange(r-g,br-bg,potentcy)&&inRange(r-b,br-bb,potentcy)&&inRange(g-b,bg-bb,potentcy)){
						bOut.setRGB(x,y,(new Color(average(r,dr),average(g,dg),average(b,db))).getRGB());
					}
				}else{
					if(inRange(r,br,potentcy)&&inRange(b,bb,potentcy)&&inRange(g,bg,potentcy)){
				  		bOut.setRGB(x,y,d.getRGB());
					}
				}
			}
		}
		return bOut;
	}
	private static boolean inRange(int i1, int i2, int potency){
		int allowance = potency;
		if((i1 >= i2-allowance && i1 <= i2+allowance)||(i2 >= i1-allowance && i2 <= i1+allowance))
			return true;
		return false;
	}
	public static BufferedImage extremes(BufferedImage bimg, int median){
		BufferedImage bOut = new BufferedImage(bimg.getWidth(),bimg.getHeight(),BufferedImage.TYPE_INT_ARGB);
		//first convert picture to grayscale
		bOut = grayscale(bimg);
		for(int x = 0; x < bimg.getWidth(); x++){
			for(int y = 0; y < bimg.getHeight(); y++){
				Color c = new Color(bOut.getRGB(x, y));
				//this gets the color's red value, but since the picture is grayscale,
				//I could use green or blue and still get the same result
				int val = c.getRed();
				//median is the grayscale value that will be the middle, with
				//all values below it being turned to black, and all values
				//greater than it being turned to white
				if(val > median){
					bOut.setRGB(x,y,Color.WHITE.getRGB());
				}else
					bOut.setRGB(x,y,Color.BLACK.getRGB());
			}
		}
		return bOut;
	}
	public static BufferedImage extremes(BufferedImage bimg, int left, int right){
		BufferedImage bOut = new BufferedImage(bimg.getWidth(),bimg.getHeight(),BufferedImage.TYPE_INT_ARGB);
		//first convert picture to grayscale
		bOut = grayscale(bimg);
		for(int x = 0; x < bimg.getWidth(); x++){
			for(int y = 0; y < bimg.getHeight(); y++){
				Color c = new Color(bOut.getRGB(x, y));
				//this gets the color's red value, but since the picture is grayscale,
				//I could use green or blue and still get the same result
				int val = c.getRed();
				//median is the grayscale value that will be the middle, with
				//all values below it being turned to black, and all values
				//greater than it being turned to white
				if(val < left){
					bOut.setRGB(x,y,Color.BLACK.getRGB());
				}else if (val > right)
					bOut.setRGB(x,y,Color.WHITE.getRGB());
				else
					bOut.setRGB(x, y, Color.GRAY.getRGB());
			}
		}
		return bOut;
	}
	public static BufferedImage wavy(BufferedImage bimg){
		BufferedImage bOut = new BufferedImage(bimg.getWidth(),bimg.getHeight(),BufferedImage.TYPE_INT_ARGB);
		//this decides the size of the column section to swap around
		int offset = 0;
		//used to determine whether to add or subtract from 'offset'
		boolean adding = true;
		//top color list and bottom color list
		Color[] top, bottom;
		  
		for(int x = 0; x < bimg.getWidth(); x++){
			if(offset != 0){
				//fills the 'top' list with data
				top = new Color[offset];
				bottom = new Color[bimg.getHeight()-offset];
				//declare y here so its value will be retained after loop exits
				int y = 0;
				for(; y < top.length; y++){
					top[y] = new Color(bimg.getRGB(x,y));
				}
				/* fills the 'bottom' list with data */
				//starty is used so that when setting the value
				//of 'bottom', the first value will be zero and
				//not the last value of y
				int starty = y;
				for(; y < bimg.getHeight(); y++){
					bottom[y-starty] = new Color(bimg.getRGB(x,y));
				}
				//moves the bottom section up to the top,
				for(y = 0; y < bottom.length; y++){
					if(bottom[y]!=null)
						bOut.setRGB(x,y,bottom[y].getRGB());
				}
				//then copies the overwritten data to the bottom 
				//of the 'new' top section
				starty = y;
				for(; y < bimg.getHeight(); y++){
					bOut.setRGB(x,y,top[y-starty].getRGB());
				}
			}
			//updates the offset value
			if(adding){
				offset++;
				if(offset >= 20){
					adding = false;
				}
			}else{
				offset--;
				if(offset <= 0){
					adding = true;
				}
			}	
		}
		return bOut;
	}

}
