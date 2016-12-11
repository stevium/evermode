package model.abstracts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import model.Diagram;
import view.Grid;

/**
Klasa koja podržava implementaciju za više metoda u Node(èvor) interfejsu.
*/
public abstract class AbstractNode implements Node {

/**
    Konstruiše èvor(node)bez roditelja ili djece.
 */
	public AbstractNode() {
		children = new ArrayList();
		parent = null;
	}

	public Object clone() {
		try {
			AbstractNode cloned = (AbstractNode) super.clone();
			cloned.children = new ArrayList(children.size());
			for (int i = 0; i < children.size(); i++) {
				Node n = (Node) children.get(i);
			//	cloned.children.set(i, n.clone());
				cloned.children.add(n.clone());
				n.setParent(cloned);
			}
			return cloned;
		} catch (CloneNotSupportedException exception) {
			return null;
		}
	}

	public void translate(double dx, double dy) {
		for (int i = 0; i < children.size(); i++) {
			Node n = (Node) children.get(i);
			n.translate(dx, dy);
		}
	}

	public boolean addEdge(Edge e, Point2D p1, Point2D p2) {
		return e.getEnd() != null;
	}

	public void removeEdge(Diagram g, Edge e) {
	}

	public void removeNode(Diagram g, Node e) {
		if (e == parent)
			parent = null;
		if (e.getParent() == this)
			children.remove(e);
	}

	public void layout(Diagram g, Graphics2D g2, Grid grid) {
	}

	public boolean addNode(Node n, Point2D p) {
		return false;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node node) {
		parent = node;
	}

	public List getChildren() {
		return children;
	}

	public void addChild(int index, Node node) {
		Node oldParent = node.getParent();
		if (oldParent != null)
			oldParent.removeChild(node);
		children.add(index, node);
		node.setParent(this);
	}

	public void addChild(Node node) {
		addChild(children.size(), node);
	}

	public void removeChild(Node node) {
		if (node.getParent() != this)
			return;
		children.remove(node);
		node.setParent(null);
	}

	public void draw(Graphics2D g2) {
		Shape shape = getShape();
		if (shape == null)
			return;

		Color oldColor = g2.getColor();
		g2.translate(SHADOW_GAP, SHADOW_GAP);
		g2.setColor(SHADOW_COLOR);
		g2.fill(shape);
		g2.translate(-SHADOW_GAP, -SHADOW_GAP);
		g2.setColor(g2.getBackground());
		g2.fill(shape);
		g2.setColor(oldColor);
	}

	private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
	public static final int SHADOW_GAP = 4;
	/**
    @return vraæa oblik koji se koristi za obraèun sjenke
 */
	public Shape getShape() {
		return null;
	}

	private ArrayList children;
	private Node parent;
}
