package com.ombda.scripts;

import com.ombda.Panel;

public interface ScriptStep{
	void execute(Panel game, Script script);
	boolean done();
}
