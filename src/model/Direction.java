package model;

import java.awt.geom.Point2D;

/**
	Ova klasa opisuje pravac u 2D ravni. Pravac je vektor dužine 1 sa uglom izmeðu 0
	(ukljuèujuæi) i 360 stepeni (iskljuèujuæi). Takoðe, postoji "izrod" pravac dužine 0. 
*/
public class Direction 
{

	/**
	    Konstruiše pravac(normalizovan do dužine 1).
	    @param dx je x-vrijednost pravca
	    @param dy je odgovarajuæa y-vrijednost pravca
    */
	public Direction(double dx, double dy) {
		x = dx;
		y = dy;
		double length = Math.sqrt(x * x + y * y);
		if (length == 0)
			return;
		x = x / length;
		y = y / length;
	}

	/**
	    Konstruiše pravac izmeðu 2 taèke.
	    @param p je poèetna taèka
	    @param q je krajnja taèka
    */
	public Direction(Point2D p, Point2D q) 
	{
		this(q.getX() - p.getX(), q.getY() - p.getY());
	}

	
	/**
	    Pretvara ovaj pravac u ugao.
	    @param angle je ugao u stepenima
	*/
	public Direction turn(double angle) 
	{
		double a = Math.toRadians(angle);
		return new Direction(x * Math.cos(a) - y * Math.sin(a), x * Math.sin(a)
				+ y * Math.cos(a));
	}

	
	/**
	    Uzima x-komponentu ovog pravca
	    @return vraæa x-komponentu (izmeðu -1 i 1)
    */
	public double getX() 
	{
		return x;
	}

	
	/**
	    Uzima y-komponentu ovog pravca
	    @return vraæa y-komponentu (izmeðu -1 i 1)
    */
	public double getY() 
	{
		return y;
	}

	private double x;
	private double y;

	public static final Direction NORTH = new Direction(0, -1);
	public static final Direction SOUTH = new Direction(0, 1);
	public static final Direction EAST = new Direction(1, 0);
	public static final Direction WEST = new Direction(-1, 0);
}
