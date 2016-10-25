package com.ombda.scripts.steps;

import java.util.ArrayList;
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
		List<String> newargs = new ArrayList<>(args);
		int index;
		while((index = newargs.indexOf("(")) != -1){
			int depth = 1;
			List<String> subargs = new ArrayList<>();
			while(!newargs.isEmpty()){
				if(newargs.get(index+1).equals("(")){
					depth++;
				}
				else if(newargs.get(index+1).equals(")")){
					depth--;
				}
				if(depth == 0) break;
				subargs.add(script.evalVars(newargs.remove(index+1)));
			}
			newargs.remove(index+1);
			newargs.remove(index);
			script.evalArgs(subargs);
			
			newargs.addAll(index,subargs);
		}
		for(int i = 0; i < newargs.size(); i++){
			String arg = newargs.get(i);
			if(arg.equals("*wait"))
				result += MessageBox.WAIT;
			else if(arg.equals("*char")){
				i++;
				String chs = newargs.get(i);
				int c = Script.parseInt(script.evalVars(chs));
				result += (char)c;
			}else if(arg.equals("*line")){
				result += '\n';
			}else{
				result += script.evalVars(arg);
				
				if(i != newargs.size()-1)
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