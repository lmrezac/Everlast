package com.ombda;

import java.awt.Shape;

public interface Collideable{
	boolean doesPointCollide(int x, int y);
	Shape getBoundingBox();
	void manageCollision(Collideable c);
}
