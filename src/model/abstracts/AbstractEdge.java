package model.abstracts;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.Direction;

/**
Klasa koja podržava implementaciju za više metoda u Edge(ivica) interfejsu.
*/
abstract class AbstractEdge implements Edge {
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException exception) {
			return null;
		}
	}

	public void connect(Node s, Node e) {
		start = s;
		end = e;
	}

	public Node getStart() {
		return start;
	}

	public Node getEnd() {
		return end;
	}

	public Rectangle2D getBounds(Graphics2D g2) {
		Line2D conn = getConnectionPoints();
		Rectangle2D r = new Rectangle2D.Double();
		r.setFrameFromDiagonal(conn.getX1(), conn.getY1(), conn.getX2(),
				conn.getY2());
		return r;
	}

	public Line2D getConnectionPoints() {
		Rectangle2D startBounds = start.getBounds();
		Rectangle2D endBounds = end.getBounds();
		Point2D startCenter = new Point2D.Double(startBounds.getCenterX(),
				startBounds.getCenterY());
		Point2D endCenter = new Point2D.Double(endBounds.getCenterX(),
				endBounds.getCenterY());
		Direction toEnd = new Direction(startCenter, endCenter);
		return new Line2D.Double(start.getConnectionPoint(toEnd),
				end.getConnectionPoint(toEnd.turn(180)));
	}

	private Node start;
	private Node end;
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
