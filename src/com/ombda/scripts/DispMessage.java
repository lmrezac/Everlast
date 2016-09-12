package com.ombda.scripts;

import com.ombda.Panel;
import com.ombda.gui.MessageBox;
import static com.ombda.Debug.debug;
public class DispMessage implements ScriptStep{

	private String[] args;
	private boolean executed = false;
	public DispMessage(String[] msg){
		this.args = msg;
	}
	
	@Override
	public void execute(Panel game, Script script){
		if(executed) return;
		executed = true;
		Panel.getInstance().msgbox.waitingForInput = false;
		String str = "";
		for(int i = 1; i < args.length; i++){
			String arg = args[i];
			if(arg.equals("*wait"))
				str += MessageBox.WAIT;
			else if(arg.equals("*char")){
				i++;
				String chs = args[i];
				int c = Script.parseInt(script.evalVar(chs));
				str += (char)c;
			}else{
				if(Script.isString(arg) && arg.length() > 1)
					str += arg.substring(1, arg.length()-1);
				else{
					str += script.evalVar(Script.parseString(arg));
				}
				
				if(i != args.length-1)
					str += ' ';
				
			}
		}
		debug("Displaying message: "+str);
		game.msgbox.setMessage(str);
		game.setGUI(game.msgbox);
		Panel.getInstance().msgbox.waitingForInput = false;
	}

	@Override
	public boolean done(){
		if(Panel.getInstance().msgbox.isFinished()){
			executed = false;
			//MessageBox box = Panel.getInstance().msgbox;
			return true;
		}
		return false;
	}


	
	//format: msg <string : message>
	public static ScriptStep loadFromString(String[] args){
		assert args[0].equals("msg");
		if(args.length <= 1) throw new RuntimeException("Expected >1 argument passed to script step: msg");
		return new DispMessage(args);
	}

}
