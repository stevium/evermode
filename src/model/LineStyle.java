package model;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
	Ova klasa definiše stilove linija koji mogu biti razlièitih oblika.
*/
public class LineStyle 
{
	private LineStyle() {}

	/**
	    Uzima potez sa kojim crta ovaj stil linije.
	    @return vraæa oblik objekta koji je iscrtan ovim stilom
    */
	public Stroke getStroke() {
		if (this == DOTTED)
			return DOTTED_STROKE;
		return SOLID_STROKE;
	}

	private static Stroke SOLID_STROKE = new BasicStroke();
	private static Stroke DOTTED_STROKE = new BasicStroke(1.0f,
			BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {
					3.0f, 3.0f }, 0.0f);

	public static final LineStyle SOLID = new LineStyle();
	public static final LineStyle DOTTED = new LineStyle();
}
