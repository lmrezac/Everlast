package com.ombda;

import com.ombda.entities.Player;

public interface Interactable{
	void onInteracted(Player p, int x, int y);
}
