import java.awt.EventQueue;
import java.io.PrintStream;

import com.ombda.Debug;
import com.ombda.FatalError;
import com.ombda.Frame;
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
			throw ex;
		}catch(Exception e){
			Debug.debug(e.getMessage());
			if(Debug.printStackTrace)
				e.printStackTrace();
			System.exit(0);
		}/**/
	}
}
