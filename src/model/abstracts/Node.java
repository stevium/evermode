package model.abstracts;
/**
Klasa koja poziva metode iz apstraktne klase AbstractNode.
*/
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import model.Diagram;
import model.Direction;
import view.Grid;
/**
	Èvor u dijagramu.
*/
public interface Node extends Serializable, Cloneable {
	/**
	    Iscrtavanje èvora.
	    @param parametar g2 grafièki element.
	 */
	void draw(Graphics2D g2);

	/**
	    Prevodi (pomjera) èvor na datu vrijednost.
	    @param dx iznos za prevod u x-smijeru
	    @param dy iznos za prevod u y-smijeru
	 */
	void translate(double dx, double dy);
	
	/**
	    Testiranje da li èvor sadrži taèku.
	    @param parametar aPoint imenuje taèku za testiranje
	    @return vraæa vrijednost true (istina) ako ovaj èvor sadrži aPoint
	 */
	boolean contains(Point2D aPoint);

	 /**
	    Uzima najbolju taèku povezivanja za povezivanje ovog èvora sa drugim èvorom. 
	    Ovo bi trebalo da bude taèka na granici oblika ovog èvora.
	    @param d smijer od centra ogranièenog pravougaonika prema granicama.
	    @return vraæa preporuèenu taèku povezivanja.
	  */
	Point2D getConnectionPoint(Direction d);

	 /**
	    Nabavlja granièni pravougaonik oblika ovog èvora.
	    @return vraæa ogranièeni pravougaonik
	  */
	Rectangle2D getBounds();

	 /**
	    Dodaje ivicu koja nastaje u ovome èvoru.
	    @param p je taèka koju korisnik uzima za poèetnu taèku.
	    @param e je ivica koja se dodaje
	    @return vraæa true ako je ivica dodata
    */
	boolean addEdge(Edge e, Point2D p1, Point2D p2);

	/**
	    Dodaje èvor kao dijete èvor ovoga èvora.
	    @param n je dijete èvor
	    @param p je taèka od koje je ovaj èvor dodat
	    @return vraæa true ako ovaj èvor prihvata dati èvor kao dijete
	 */
	boolean addNode(Node n, Point2D p);

	/**
	    Obavještava ovaj èvor da je ivica ukonjena
	   	@param g je ambijent dijagram(graf)
	    @param e je ivica koja je uklonjena
	 */
	void removeEdge(Diagram g, Edge e);

	 /**
	    Obavještava ovaj èvor da je èvor uklonjen.Notifies this node that a node is being removed.
	    @param g je ambijent graf
	    @param n je èvor koji je uklonjen
	  */
	void removeNode(Diagram g, Node n);

	/**
	    Postavlja èvor i njegovu djecu.
	    @param g je ambijent graf
	    @param g2 je grafièki sadržaj
	    @param grid je koordinatno mjesto za naglo pomjeranje
	 */
	void layout(Diagram g, Graphics2D g2, Grid grid);

	/**
	    Dobija roditelja ovog èvora.
	    @return vraæa roditeljski èvor, ili null vrijednost ako èvor nema roditelja.
	 */
	Node getParent();

	/**
	    Postavlja roditelja ovog èvora.
	    @param node je èvor roditelj, ili null ako èvor nema roditelja
	 */
	void setParent(Node node);

	/**
	    Uzima potomak ovoga èvora.Gets the children of this node.
	    @return vraæa nemodofikovanu listu potomaka
    */
	List getChildren();

	/**
	    Dodaje èvor potomak(dijete).Adds a child node.
	    @param index predstavlja poziciju na koju dodajemo potomka
	    @param node predstavlja potomak èvor koji se dodaje
   */
	void addChild(int index, Node node);

	/**
	    Uklanjanje potomak èvora.
	    @param node predstavlja dijete(potomka) koji se uklanja.
	 */
	void removeChild(Node node);

	Object clone();
}
