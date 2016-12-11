package model.diagram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.Diagram;
import model.PointNode;
import model.abstracts.Edge;
import model.abstracts.RectangularNode;
import view.Grid;
import view.property.TextArea;

/**
	Beleška èvor u UML dijagramu.
*/
public class NoteNode extends RectangularNode 
{

	/**
    	Konstruiše beleška èvor sa podrazumijevanom velièinom i bojom
	*/
	public NoteNode() {
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		text = new TextArea();
		text.setJustification(TextArea.LEFT);
		color = DEFAULT_COLOR;
	}

	public boolean addEdge(Edge e, Point2D p1, Point2D p2) {
		PointNode end = new PointNode();
		end.translate(p2.getX(), p2.getY());
		e.connect(this, end);
		return super.addEdge(e, p1, p2);
	}

	public void removeEdge(Diagram g, Edge e) {
		if (e.getStart() == this)
			g.removeNode(e.getEnd());
	}

	public void layout(Diagram g, Graphics2D g2, Grid grid) {
		Rectangle2D b = text.getBounds(g2); // getMultiLineBounds(name, g2);
		Rectangle2D bounds = getBounds();
		b = new Rectangle2D.Double(bounds.getX(), bounds.getY(), Math.max(
				b.getWidth(), DEFAULT_WIDTH), Math.max(b.getHeight(),
				DEFAULT_HEIGHT));
		grid.snap(b);
		setBounds(b);
	}

	/**
	    Uzima vrijednost posjeda teksta.
	    @return vraæa tekst unutar beleške
    */
	public TextArea getText() {
		return text;
	}

	 /**
	    Postavlja vrijednost posjeda teksta.
	    @param newValue je tekst unutar beleške
    */
	public void setText(TextArea newValue) {
		text = newValue;
	}

	/**
	    Uzima vrijednost boje posjeda.
	    @return vraæa boju pozadine beleške
    */
	public Color getColor() {
		return color;
	}

	/**
	    Postavlja vrijednost boje posjeda.
	    @param newValue je boja pozadine beleške
    */
	public void setColor(Color newValue) {
		color = newValue;
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);
		Color oldColor = g2.getColor();
		g2.setColor(color);

		Shape path = getShape();
		g2.fill(path);
		g2.setColor(oldColor);
		g2.draw(path);

		Rectangle2D bounds = getBounds();
		GeneralPath fold = new GeneralPath();
		fold.moveTo((float) (bounds.getMaxX() - FOLD_X), (float) bounds.getY());
		fold.lineTo((float) bounds.getMaxX() - FOLD_X, (float) bounds.getY()
				+ FOLD_X);
		fold.lineTo((float) bounds.getMaxX(), (float) (bounds.getY() + FOLD_Y));
		fold.closePath();
		oldColor = g2.getColor();
		g2.setColor(g2.getBackground());
		g2.fill(fold);
		g2.setColor(oldColor);
		g2.draw(fold);

		text.draw(g2, getBounds());
	}

	public Shape getShape() {
		Rectangle2D bounds = getBounds();
		GeneralPath path = new GeneralPath();
		path.moveTo((float) bounds.getX(), (float) bounds.getY());
		path.lineTo((float) (bounds.getMaxX() - FOLD_X), (float) bounds.getY());
		path.lineTo((float) bounds.getMaxX(), (float) (bounds.getY() + FOLD_Y));
		path.lineTo((float) bounds.getMaxX(), (float) bounds.getMaxY());
		path.lineTo((float) bounds.getX(), (float) bounds.getMaxY());
		path.closePath();
		return path;
	}

	public Object clone() {
		NoteNode cloned = (NoteNode) super.clone();
		cloned.text = (TextArea) text.clone();
		return cloned;
	}
	
	public String toString()
	{
		return "Note Node";
	}

	private TextArea text;
	private Color color;

	private static int DEFAULT_WIDTH = 60;
	private static int DEFAULT_HEIGHT = 40;
	private static Color DEFAULT_COLOR = new Color(0.9f, 0.9f, 0.6f); // pale
																		// yellow
	private static int FOLD_X = 8;
	private static int FOLD_Y = 8;
}
