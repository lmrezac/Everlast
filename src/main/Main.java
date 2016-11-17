package main;
import java.awt.EventQueue;
import java.io.PrintStream;

import com.ombda.Debug;
import com.ombda.FatalError;
import com.ombda.Frame;
import com.ombda.Panel;

import static com.ombda.Debug.debug;
public class Main{
	private static Frame theFrame;
	public static final PrintStream stream = new com.ombda.Debug();
	public static void main(String[] args){
		try{
		debug("Starting!");
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				theFrame = new Frame();
				theFrame.setVisible(true);
			}
		});
		}catch(FatalError ex){
			fatalError(ex);
		}catch(Exception e){
			Debug.debug(e.getMessage());
			if(Debug.printStackTrace)
				e.printStackTrace();
			forceStopGame();
		}/**/
	}
	public static void fatalError(FatalError ex){
		debug("FATAL ERROR DETECTED");
		ex.printStackTrace();
		forceStopGame();
	}
	public static void forceStopGame(){
		Panel.getInstance().stop();
		theFrame.dispose();
		theFrame.close();
		debug(theFrame.isActive());
		System.exit(0);
	}
}
