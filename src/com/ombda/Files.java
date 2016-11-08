package com.ombda;

import static com.ombda.Debug.debug;
import static com.ombda.Debug.printStackTrace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Files{
	public static String dir = System.getProperty("user.dir")+"\\resources\\";
	static{
		if(System.getProperty("os.name").toLowerCase().contains("windows")){
			while(dir.contains("/"))
				dir = dir.replace("/","\\");
		}else{
			while(dir.contains("\\"))
				dir = dir.replace("\\", "/");
		}
	}
	public static String localize(String file){
		
		if(isWindowsOS){
			while(file.contains("/"))
				file = file.replace("/","\\");
		}else{
			while(file.contains("\\"))
				file = file.replace("\\", "/");
		}
		if(!file.startsWith(dir))
			if(file.charAt(1) == ':')
				throw new RuntimeException("Invalid file path: "+file+"; can only be contained within "+dir+".");
			else
				file = dir+file;
		return file;
	}
	public static final boolean isWindowsOS = System.getProperty("os.name").toLowerCase().contains("windows");
	public static List<String> readLines(String path){
		return readLines(new File(localize(path)));
	}
	public static List<String> readLines(File file){
		try{
			Scanner scan = new Scanner(file);
			List<String> lines = new ArrayList<>();
			while(scan.hasNext())
				lines.add(scan.nextLine());
			scan.close();
			return lines;
		}catch(IOException e){
			debug("Error reading "+file.getAbsolutePath());
			if(printStackTrace)
				e.printStackTrace();
			throw new FatalError();
		}
	}
	public static String read(String path){
		return read(new File(localize(path)));
	}
	public static String read(File file){
		try{
			Scanner scan = new Scanner(file);
			String str = "";
			while(scan.hasNext())
					str += scan.nextLine();
			scan.close();
			return str;
		}catch(IOException e){
			debug("Error reading "+file.getAbsolutePath());
			if(printStackTrace)
				e.printStackTrace();
			throw new FatalError();
		}
	}
	public static byte[] readBytes(String str){
		str = localize(str);
		try{
			return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(str));
		}catch(IOException e){
			debug("Error reading "+str);
			if(printStackTrace)
				e.printStackTrace();
			throw new FatalError();
		}
	}
	
	public static void write(String filename,List<String> list){
		FileOutputStream out = null;
		PrintStream print = null;
		filename = localize(filename);
		try{
			out = new FileOutputStream(filename);
		}catch(FileNotFoundException e){
			debug("Error writing to file "+new File(filename).getAbsolutePath());
			if(printStackTrace)
				e.printStackTrace();
			throw new FatalError();
		}
		print = new PrintStream(out);
		for(String s : list)
			print.println(s);
		print.close();
	}
	public static void writeBytes(String filename, byte[] bytes){
		filename = localize(filename);
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(filename);
		}catch(FileNotFoundException e){
			debug(e.getMessage());
			if(printStackTrace)
				e.printStackTrace();
		}
		try{
			fos.write(bytes);
		}catch(IOException e){
			debug(e.getMessage());
			if(printStackTrace)
				e.printStackTrace();
		}
		try{
			fos.close();
		}catch(IOException e){
			debug(e.getMessage());
			if(printStackTrace)
				e.printStackTrace();
		}
	}
}
