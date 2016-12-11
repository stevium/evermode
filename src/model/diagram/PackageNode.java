package model.diagram;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JLabel;

import model.Diagram;
import model.abstracts.Node;
import model.abstracts.RectangularNode;
import view.Grid;
import view.property.TextArea;

/**
	Èvor u UML dijagramu.
*/
public class PackageNode extends RectangularNode 
{

	/**
    	Konstruiše èvor podrazumijevane velièine.
	*/
	public PackageNode() {
		name = "";
		contents = new TextArea();
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		top = new Rectangle2D.Double(0, 0, DEFAULT_TOP_WIDTH,
				DEFAULT_TOP_HEIGHT);
		bot = new Rectangle2D.Double(0, DEFAULT_TOP_HEIGHT, DEFAULT_WIDTH,
				DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT);
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);
		Rectangle2D bounds = getBounds();

		label.setText("<html>" + name + "</html>");
		label.setFont(g2.getFont());
		Dimension d = label.getPreferredSize();
		label.setBounds(0, 0, d.width, d.height);

		g2.draw(top);

		double textX = bounds.getX() + NAME_GAP;
		double textY = bounds.getY() + (top.getHeight() - d.getHeight()) / 2;

		g2.translate(textX, textY);
		label.paint(g2);
		g2.translate(-textX, -textY);

		g2.draw(bot);
		contents.draw(g2, bot);
	}

	public Shape getShape() {
		GeneralPath path = new GeneralPath();
		path.append(top, false);
		path.append(bot, false);
		return path;
	}

	public void layout(Diagram g, Graphics2D g2, Grid grid) {
		Rectangle2D bounds = getBounds();

		label.setText("<html>" + name + "</html>");
		label.setFont(g2.getFont());
		Dimension d = label.getPreferredSize();

		top = new Rectangle2D.Double(bounds.getX(), bounds.getY(), Math.max(
				d.getWidth(), DEFAULT_TOP_WIDTH), Math.max(d.getHeight(),
				DEFAULT_TOP_HEIGHT));

		bot = contents.getBounds(g2);
		Rectangle2D min = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH,
				DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT);
		bot.add(min);
		double width = Math.max(top.getWidth() + DEFAULT_WIDTH
				- DEFAULT_TOP_WIDTH, bot.getWidth());
		double height = top.getHeight() + bot.getHeight();

		List children = getChildren();
		if (children.size() > 0) {
			Rectangle2D childBounds = new Rectangle2D.Double(bounds.getX(),
					bounds.getY(), 0, 0);
			for (int i = 0; i < children.size(); i++) {
				Node child = (Node) children.get(i);
				child.layout(g, g2, grid);
				childBounds.add(child.getBounds());
			}
			width = Math.max(width, childBounds.getWidth() + XGAP);
			height = Math.max(height, childBounds.getHeight() + YGAP);
		}
		Rectangle2D b = new Rectangle2D.Double(bounds.getX(), bounds.getY(),
				width, height);
		grid.snap(b);
		setBounds(b);

		top = new Rectangle2D.Double(bounds.getX(), bounds.getY(), Math.max(
				d.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH), Math.max(
				d.getHeight(), DEFAULT_TOP_HEIGHT));

		bot = new Rectangle2D.Double(bounds.getX(), bounds.getY()
				+ top.getHeight(), bounds.getWidth(), bounds.getHeight()
				- top.getHeight());
	}
	
	/**
	    Postavlja ime vrijednosti imovine.
	    @param newValue je ime klase
	*/
	public void setName(String newValue) {
		name = newValue;
	}

	/**
	    Uzima ime vrijednosti imovine.
	    @return vraæa ime klase
    */
	public String getName() {
		return name;
	}

	/**
	    Postavlja sadržaj vrijednosti imovine.
	    @param newValue je sadržaj ove klase
    */
	public void setContents(TextArea newValue) {
		contents = newValue;
	}

	/**
	    Uzima sadržaj vrijednosti imovine.
	    @return vraæa sadržaj ove klase
    */
	public TextArea getContents() {
		return contents;
	}

	public Object clone() {
		PackageNode cloned = (PackageNode) super.clone();
		cloned.contents = (TextArea) contents.clone();
		return cloned;
	}

	public boolean addNode(Node n, Point2D p) {
		if (n instanceof ClassNode || n instanceof InterfaceNode
				|| n instanceof PackageNode) {
			addChild(n);
			return true;
		} else
			return n instanceof NoteNode;
	}
	
	public String toString()
	{
		return "Package Node";
	}
	
	private String name;
	private TextArea contents;

	private Rectangle2D top;
	private Rectangle2D bot;

	private static int DEFAULT_TOP_WIDTH = 60;
	private static int DEFAULT_TOP_HEIGHT = 20;
	private static int DEFAULT_WIDTH = 100;
	private static int DEFAULT_HEIGHT = 80;
	private static final int NAME_GAP = 3;
	private static final int XGAP = 5;
	private static final int YGAP = 5;

	private static JLabel label = new JLabel();
}
