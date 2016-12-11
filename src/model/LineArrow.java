package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
	Ova klasa definiše strelice razlièitih oblika.
*/
public class LineArrow 
{
	private LineArrow() {}

	/**
	    Iscrtava strelice.
	    @param g2 je grafièki sadržaj
	    @param p je taèka na osi strelice
	    @param q je krajnja taèka strelice
    */
	public void draw(Graphics2D g2, Point2D p, Point2D q) {
		GeneralPath path = getPath(p, q);
		Color oldColor = g2.getColor();
		if (this == BLACK_DIAMOND || this == BLACK_TRIANGLE)
			g2.setColor(Color.BLACK);
		else
			g2.setColor(Color.WHITE);
		g2.fill(path);
		g2.setColor(oldColor);
		g2.draw(path);
	}

	/**
	    Uzima putanju strelice.
	    @param p je taèka na osi strelice
	    @param q je krajnja taèka strelice
	    @return vraæa putanju
    */
	public GeneralPath getPath(Point2D p, Point2D q) {
		GeneralPath path = new GeneralPath();
		if (this == NONE)
			return path;
		final double ARROW_ANGLE = Math.PI / 6;
		final double ARROW_LENGTH = 10;

		double dx = q.getX() - p.getX();
		double dy = q.getY() - p.getY();
		double angle = Math.atan2(dy, dx);
		double x1 = q.getX() - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE);
		double y1 = q.getY() - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE);
		double x2 = q.getX() - ARROW_LENGTH * Math.cos(angle - ARROW_ANGLE);
		double y2 = q.getY() - ARROW_LENGTH * Math.sin(angle - ARROW_ANGLE);

		path.moveTo((float) q.getX(), (float) q.getY());
		path.lineTo((float) x1, (float) y1);
		if (this == V) 
		{
			path.moveTo((float) x2, (float) y2);
			path.lineTo((float) q.getX(), (float) q.getY());
		}
		else if (this == TRIANGLE || this == BLACK_TRIANGLE) 
		{
			path.lineTo((float) x2, (float) y2);
			path.closePath();
		} 
		else if (this == DIAMOND || this == BLACK_DIAMOND) 
		{
			double x3 = x2 - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE);
			double y3 = y2 - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE);
			path.lineTo((float) x3, (float) y3);
			path.lineTo((float) x2, (float) y2);
			path.closePath();
		}
		return path;
	}

	public static final LineArrow NONE = new LineArrow();
	public static final LineArrow TRIANGLE = new LineArrow();
	public static final LineArrow BLACK_TRIANGLE = new LineArrow();
	public static final LineArrow V = new LineArrow();
	public static final LineArrow HALF_V = new LineArrow();
	public static final LineArrow DIAMOND = new LineArrow();
	public static final LineArrow BLACK_DIAMOND = new LineArrow();
}
