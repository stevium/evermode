package model.abstracts;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
	Klasa koja pretpostavlja da ivica može dati svoj oblik i onda uzima prednost èinjenice 
	da se zadržavanje testiranja može izvršiti udaranjem oblika jaèim udarom.
	U idealnom sluèaju, trebalo bi da možete povuci isti oblik koji se koristi za testiranje zaštitnog. 
*/
public abstract class ShapeEdge extends AbstractEdge 
{

	/**
	    Vraæa putanju koja bi mogla biti znaèajna za crtanje ove ivice.
	    Putanja ne ukljuèuje tipove strelice ili etikete. 
	    @return vraæa putanju duž ivice
    */
	public abstract Shape getShape();

	public Rectangle2D getBounds(Graphics2D g2) {
		return getShape().getBounds();
	}

	public boolean contains(Point2D aPoint) {
		final double MAX_DIST = 3;

		Line2D conn = getConnectionPoints();
		if (aPoint.distance(conn.getP1()) <= MAX_DIST
				|| aPoint.distance(conn.getP2()) <= MAX_DIST)
			return false;

		Shape p = getShape();
		BasicStroke fatStroke = new BasicStroke((float) (2 * MAX_DIST));
		Shape fatPath = fatStroke.createStrokedShape(p);
		return fatPath.contains(aPoint);
	}
}
