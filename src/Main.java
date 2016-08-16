import java.awt.EventQueue;
import java.io.PrintStream;

import com.ombda.Debug;
import com.ombda.Frame;

public class Main{
	private static Frame theFrame;
	public static final PrintStream stream = new com.ombda.Debug();
	public static void main(String[] args){
		
		try{
		System.out.println("Starting!");
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				theFrame = new Frame();
				theFrame.setVisible(true);
			}
		});
		}catch(Exception e){
			Debug.debug(e.getMessage());
			if(Debug.printStackTrace)
				e.printStackTrace();
			System.exit(0);
		}/**/
	}
}
