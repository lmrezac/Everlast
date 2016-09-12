package com.ombda;

import java.awt.Shape;

public interface Collideable{
	boolean doesPointCollide(int x, int y);
	void testCollision();
	void manageCollision(Collideable c);
	Shape getBoundingBox();
}
