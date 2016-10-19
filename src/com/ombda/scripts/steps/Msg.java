package com.ombda.scripts.steps;

import java.util.List;

import com.ombda.MessageListener;
import com.ombda.Panel;
import com.ombda.gui.MessageBox;
import com.ombda.scripts.Scope;
import com.ombda.scripts.Script;

public class Msg extends ScriptStep implements MessageListener{
	private List<String> args;
	public Msg(List<String> args){
		this.args = args;
		this.executed = false;
	}
	public void execute(Scope script){
		if(executed) return;
		executed = true;
		String result = "";
		for(int i = 0; i < args.size(); i++){
			String arg = args.get(i);
			if(arg.equals("*wait"))
				result += MessageBox.WAIT;
			else if(arg.equals("*char")){
				i++;
				String chs = args.get(i);
				int c = Script.parseInt(script.evalVars(chs));
				result += (char)c;
			}else if(arg.equals("*line")){
				result += '\n';
			}else{
				result += script.evalVars(arg);
				
				if(i != args.size()-1)
					result += ' ';
				
			}
		}
		Panel game = Panel.getInstance();
		//debug("Displaying message: "+result);
		game.msgbox.setMessage(result);
		game.msgbox.addInputListener(this);
		game.setGUI(Panel.getInstance().msgbox);
		game.msgbox.waitingForInput = false;
	}
	public void onMessageFinish(){
		isDone = true;
		executed = false;
		Panel.getInstance().msgbox.removeInputListener(this);
	}
	private boolean isDone = false;
	@Override
	public boolean done(){
		/*System.out.println("called!");
		if(Panel.getInstance().msgbox.isFinished()){
			executed = false;
			System.out.println("finished executing message");
			//MessageBox box = Panel.getInstance().msgbox;
			return true;
		}
		return false;*/
		if(isDone){
			isDone = false;
			return true;
		}
		return false;
	}
}