package model.abstracts;
/**
 Klasa koja poziva metode iz apstraktne klase AbstractEdge.
*/
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
/**
 Ivica u dijagramu.
*/
public interface Edge extends Serializable, Cloneable {
	/**
     Iscrtavanje ivice.
    */
	void draw(Graphics2D g2);
	 /**
      Testiranje da li ivica sadrži taèku.
      @param aPoint imenuje taèku za testiranje.
      @return vraæa vrijednost true (istina) ako ivica sadrži taèku(aPoint).
     */
	boolean contains(Point2D aPoint);
	/**
	    Povezivanje ove ivice na 2 èvora.
	    @param aStart poèetni èvor
	    @param anEnd završni èvor
	 */
	void connect(Node aStart, Node anEnd);
	
	/**
	    Uzima poèetni èvor.
	    @return vraæa poèetni èvor
	  */
	Node getStart();
	
	/**
	    Uzima završni èvor.
	    @return vraæa završni èvor
	 */
	Node getEnd();
	
	/**
	    Uzima taèke koje su definisane kao èvorovi ove ivice. 
	    @return vraæa liniju, tj. spaja 2 prikljuèene taèke linijom a
	 */
	Line2D getConnectionPoints();

	/**
	    Dobija najmanji prvaougaonik koji ogranièava ovu ivicu.
	    Ogranièeni prvaougaonik sadrži sve labele.
	    @return vraæa ogranièeni pravougaonik.
	 */
	Rectangle2D getBounds(Graphics2D g2);

	Object clone();

	public String getName();
	
	public void setName(String name);
}
