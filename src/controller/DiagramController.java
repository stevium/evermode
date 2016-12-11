package controller;

import helpers.CutCopyPaste;
import helpers.DOMParser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Diagram;
import model.abstracts.Edge;
import model.abstracts.Node;
import model.diagram.ClassDiagram;

public class DiagramController {

	DiagramPanel view = null;
	Diagram model = null;

	public DiagramController(DiagramPanel view, Diagram model) {
		this.view = view;
		this.model = model;
		model.saveGraphIntoHistory();
	}

	public void addGraphPanel(DiagramPanel diagramPanel) {
		view = diagramPanel;
	}
	
	public class myKeyAdapter  extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
        	
        boolean isCtrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
           if (e.getKeyCode() == KeyEvent.VK_C && isCtrl) {
        	   CutCopyPaste.copy(model);
           }
           
           else if (e.getKeyCode() == KeyEvent.VK_V && isCtrl) {
        	   CutCopyPaste.paste(model);
           }
           else if (e.getKeyCode() == KeyEvent.VK_X && isCtrl) {
        	   CutCopyPaste.cut(model);
           }
        }
     }
	

	public class myMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent event) {
			view.requestFocus();
			final Point2D mousePoint = new Point2D.Double(event.getX()
					/ model.zoom, event.getY() / model.zoom);
			boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
			Node n = model.findNode(mousePoint);
			Edge e = model.findEdge(mousePoint);
			Object tool = model.getSelectedTool();
			if (event.getClickCount() > 1
					|| (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0)
			// double/right-click
			{
				if (e != null) {
					model.setSelectedItem(e);
					view.showPropertyEditor();
				} else if (n != null) {
					model.setSelectedItem(n);
					view.showPropertyEditor();
				} else {
					view.popup.showPopup(view, mousePoint,
							new ActionListener() {
								public void actionPerformed(ActionEvent event) {
									Object tool = model.getSelectedTool();
									if (tool instanceof Node) {
										Node prototype = (Node) tool;
										Node newNode = (Node) prototype.clone();
										boolean added = model.add(newNode,
												mousePoint);
										if (added) {
											model.setModified(true);
											model.setSelectedItem(newNode);
										}
									}
								}
							});
				}
			} else if (tool == null) // select
			{
				if (e != null) {
					model.setSelectedItem(e);
				} else if (n != null) {
					if (isCtrl)
						model.addSelectedItem(n);
					else if (!model.selectedItems.contains(n))
						model.setSelectedItem(n);
					dragMode = DRAG_MOVE;
				} else {
					if (!isCtrl)
						model.clearSelection();
					dragMode = DRAG_LASSO;
				}
			} else if (tool instanceof Node) {
				Node prototype = (Node) tool;
				Node newNode = (Node) prototype.clone();
				boolean added = model.add(newNode, mousePoint);
				if (added) {
					model.setModified(true);
					model.setSelectedItem(newNode);
					dragMode = DRAG_NONE;
				} else if (n != null) {
					if (isCtrl)
						model.addSelectedItem(n);
					else if (!model.selectedItems.contains(n))
						model.setSelectedItem(n);
					dragMode = DRAG_MOVE;
				}
			} 
			else if (tool instanceof Node) {
				if (n != null) {
					if (isCtrl)
						model.addSelectedItem(n);
					else if (!model.selectedItems.contains(n))
						model.setSelectedItem(n);
					dragMode = DRAG_MOVE;
				}
			}
			else if (tool instanceof Edge) {
				if (n != null)
					dragMode = DRAG_RUBBERBAND;
			}

			model.lastMousePoint = mousePoint;
			model.mouseDownPoint = mousePoint;
		//	view.repaint();
		}

		public void mouseReleased(MouseEvent event) {
			Point2D mousePoint = new Point2D.Double(event.getX() / model.zoom,
					event.getY() / model.zoom);
			Object tool = model.getSelectedTool();
			if (dragMode == DRAG_RUBBERBAND) {
				Edge prototype = (Edge) tool;
				Edge newEdge = (Edge) prototype.clone();
				if (mousePoint.distance(model.mouseDownPoint) > CONNECT_THRESHOLD
						&& model.connect(newEdge, model.mouseDownPoint, mousePoint)) {
					model.setSelectedItem(newEdge);
				}
			} else if (dragMode == DRAG_MOVE) {
				if (mousePoint.distance(model.mouseDownPoint) > CONNECT_THRESHOLD){
					model.saveGraphIntoHistory();
				}
			} 
			dragMode = DRAG_NONE;
			model.layout();
			view.repaint();
		}
	}

	public class myMouseMotionAdapter extends MouseMotionAdapter {
		public void mouseDragged(MouseEvent event) {
			Point2D mousePoint = new Point2D.Double(event.getX() / model.zoom,
					event.getY() / model.zoom);
			boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;

			if (dragMode == DRAG_MOVE && model.lastSelected instanceof Node) {
				Node lastNode = (Node) model.lastSelected;
				Rectangle2D bounds = lastNode.getBounds();
				double dx = mousePoint.getX() - model.lastMousePoint.getX();
				double dy = mousePoint.getY() - model.lastMousePoint.getY();

				
				// nemea negativnih koordinata
				Iterator iter = model.selectedItems.iterator();
				while (iter.hasNext()) {
					Object selected = iter.next();
					if (selected instanceof Node) {
						Node n = (Node) selected;
						bounds.add(n.getBounds());
					}
				}
				dx = Math.max(dx, -bounds.getX());
				dy = Math.max(dy, -bounds.getY());

				iter = model.selectedItems.iterator();
				while (iter.hasNext()) {
					Object selected = iter.next();
					if (selected instanceof Node) {
						Node n = (Node) selected;
						n.translate(dx, dy);
					}
				}
				
				model.setModified(true);
				//model.layout();
				
			} else if (dragMode == DRAG_LASSO) {
				double x1 = model.mouseDownPoint.getX();
				double y1 = model.mouseDownPoint.getY();
				double x2 = mousePoint.getX();
				double y2 = mousePoint.getY();
				Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1,
						x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1
						- y2));
				Iterator iter = model.getNodes().iterator();
				while (iter.hasNext()) {
					Node n = (Node) iter.next();
					Rectangle2D bounds = n.getBounds();
					if (!isCtrl && !lasso.contains(n.getBounds())) {
						model.removeSelectedItem(n);
					} else if (lasso.contains(n.getBounds())) {
						model.addSelectedItem(n);
					}
				}
				model.setModified(true);
			}

			model.setLastMousePoint(mousePoint);
		//	model.setModified(true);
			view.repaint();
			//model.setModified(true);
			
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			Point2D mousePoint = new Point2D.Double(e.getX() / model.zoom,
					e.getY() / model.zoom);
			boolean isCtrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;

		
			double dx = mousePoint.getX() - model.lastMousePoint.getX();
			double dy = mousePoint.getY() - model.lastMousePoint.getY();
			
			model.setLastMousePoint(mousePoint);
			super.mouseMoved(e);
		}
	}

	public MouseAdapter getMouseAdapter() {
		return new myMouseAdapter();
	}
	
	public KeyAdapter getKeyAdapter() {
		return new myKeyAdapter();
	}

	public MouseMotionAdapter getMouseMotionAdapter() {
		return new myMouseMotionAdapter();
	}

	
	public void paintComponent(Graphics g) {
		// view.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(model.zoom, model.zoom);
		Rectangle2D bounds = view.getBounds();
		Rectangle2D diagramBounds = model.getBounds(g2);
		if (!view.hideGrid)
			view.grid.draw(
					g2,
					new Rectangle2D.Double(0, 0, Math.max(bounds.getMaxX()
							/ model.zoom, diagramBounds.getMaxX()),
							Math.max(bounds.getMaxY() / model.zoom,
									diagramBounds.getMaxY())));
		model.draw(g2, view.grid);

		Iterator iter = model.selectedItems.iterator();
		Set toBeRemoved = new HashSet();
		while (iter.hasNext()) {
			Object selected = iter.next();

			if (!model.getNodes().contains(selected)
					&& !model.getEdges().contains(selected)) {
				toBeRemoved.add(selected);
			} else if (selected instanceof Node) {
				Rectangle2D grabberBounds = ((Node) selected).getBounds();
				drawGrabber(g2, grabberBounds.getMinX(),
						grabberBounds.getMinY());
				drawGrabber(g2, grabberBounds.getMinX(),
						grabberBounds.getMaxY());
				drawGrabber(g2, grabberBounds.getMaxX(),
						grabberBounds.getMinY());
				drawGrabber(g2, grabberBounds.getMaxX(),
						grabberBounds.getMaxY());
			} else if (selected instanceof Edge) {
				Line2D line = ((Edge) selected).getConnectionPoints();
				drawGrabber(g2, line.getX1(), line.getY1());
				drawGrabber(g2, line.getX2(), line.getY2());
			}
		}

		iter = toBeRemoved.iterator();
		while (iter.hasNext())
			model.removeSelectedItem(iter.next());

		if (dragMode == DRAG_RUBBERBAND) {
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			g2.draw(new Line2D.Double(model.mouseDownPoint, model.lastMousePoint));
			g2.setColor(oldColor);
		} else if (dragMode == DRAG_LASSO) {
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			double x1 = model.mouseDownPoint.getX();
			double y1 = model.mouseDownPoint.getY();
			double x2 = model.lastMousePoint.getX();
			double y2 = model.lastMousePoint.getY();
			Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2),
					Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
			g2.draw(lasso);
			g2.setColor(oldColor);
		}
	}

	public static void drawGrabber(Graphics2D g2, double x, double y) {
		final int SIZE = 5;
		Color oldColor = g2.getColor();
		g2.setColor(PURPLE);
		g2.fill(new Rectangle2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
		g2.setColor(oldColor);
	}

	public ChangeListener getPropertyChangeListener() {
		return new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				model.layout();
				model.setModified(true);
			}
		};
	}
    
	public int dragMode;

	public static final int DRAG_NONE = 0;
	public static final int DRAG_MOVE = 1;
	public static final int DRAG_RUBBERBAND = 2;
	public static final int DRAG_LASSO = 3;

	public static final int CONNECT_THRESHOLD = 8;

	public static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);
}
