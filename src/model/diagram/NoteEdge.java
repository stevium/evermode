package model.diagram;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import model.Direction;
import model.abstracts.ShapeEdge;

/**
	Isprekidana linija koja povezuje poruku svom prilogu.
*/
public class NoteEdge extends ShapeEdge {
	public void draw(Graphics2D g2) {
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(DOTTED_STROKE);
		g2.draw(getConnectionPoints());
		g2.setStroke(oldStroke);
	}

	public Line2D getConnectionPoints() {
		Rectangle2D start = getStart().getBounds();
		Rectangle2D end = getEnd().getBounds();
		Direction d = new Direction(end.getCenterX() - start.getCenterX(),
				end.getCenterY() - start.getCenterY());

		return new Line2D.Double(getStart().getConnectionPoint(d), getEnd()
				.getConnectionPoint(d.turn(180)));
	}

	public Shape getShape() {
		GeneralPath path = new GeneralPath();
		Line2D conn = getConnectionPoints();
		path.moveTo((float) conn.getX1(), (float) conn.getY1());
		path.lineTo((float) conn.getX2(), (float) conn.getY2());
		return path;
	}

	private static Stroke DOTTED_STROKE = new BasicStroke(1.0f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {
					3.0f, 3.0f }, 0.0f);

	@Override
	public String getName() {
		return "NoteConnector";
	}
}
