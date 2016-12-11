package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JOptionPane;

import view.EditorPanel;
import view.Grid;
import view.property.PropertyPanel;

import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import model.Diagram;

/**
 	Panel za crtanje grafikona.
 */
public class DiagramPanel extends JPanel implements Observer 
{

	 /**
	    Konstruiše dijagram(grafikon).
	 */
	public DiagramPanel(Diagram m) 
	{
		model = m;
		model.addObserver(this);
		popup = new PopupMenu(m);
		grid = new Grid();
		gridSize = GRID;
		grid.setGrid((int) gridSize, (int) gridSize);
		setBackground(Color.WHITE);

		controller = new DiagramController(this, model);

		// Graphics2D list = (Graphics2D) getGraphics();
		addMouseListener(controller.getMouseAdapter());
		addMouseMotionListener(controller.getMouseMotionAdapter());
		//addKeyListener(controller.getKeyAdapter());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		controller.paintComponent(g);
	}

	public Dimension getPreferredSize() {
		Rectangle2D bounds = model.getBounds((Graphics2D) getGraphics());
		return new Dimension((int) (model.zoom * bounds.getMaxX()),
				(int) (model.zoom * bounds.getMaxY()));

		// return getMaximumSize();
	}

	public void showPropertyEditor() {
		Object edited = model.lastSelected;
		if (model.lastSelected == null) {
			if (model.selectedItems.size() == 1)
				edited = model.selectedItems.iterator().next();
			else
				return;
		}

		PropertyPanel sheet = new PropertyPanel(edited, this);

		sheet.addChangeListener(controller.getPropertyChangeListener());

		JOptionPane.showMessageDialog(
				this,
				sheet,
				ResourceBundle.getBundle("MenuBundle").getString(
						"dialog.properties"), JOptionPane.QUESTION_MESSAGE);
	}

	/**
	    Mijenja velièinu mreže ovoga panela. Zumiranje je standardne velièine 10 
	    i pomnožen sa korijenom iz 2 za svaki pozitavan korak ili podijeljeno sa
	    korijenom iz 2 za svaki negativan korak. 
	    @param steps su brojevi koraka koliko se mijenja zumiranje. Pozitivna vrijednost je zoom in
	*/
	public void changeGridSize(int steps) {
		final double FACTOR = Math.sqrt(2);
		for (int i = 1; i <= steps; i++)
			gridSize *= FACTOR;
		for (int i = 1; i <= -steps; i++)
			gridSize /= FACTOR;
		grid.setGrid((int) gridSize, (int) gridSize);
		model.layout();
		repaint();
	}

	/**
	    Posatvlja vrijednost podruèja skrivene mreže.
	    @param newValue ima vrijednost true ako je mreža skrivena
	*/
	public void setHideGrid(boolean newValue) {
		hideGrid = newValue;
		repaint();
	}

	/**
	    Uzima vrijednost podruèja sakrivene mreže
	    @return vraæa true ako je mreža skrivena
	*/
	public boolean getHideGrid() {
		return hideGrid;
	}

	@Override
	public void update(Observable o, Object arg) {

		if (diagramPanel == null) {
			Component parent = this;
			do {
				parent = parent.getParent();
			} while (parent != null && !(parent instanceof EditorPanel));
			if (parent != null)
				diagramPanel = (EditorPanel) parent;
		}
		if (diagramPanel != null) {
			String title = diagramPanel.model.getName();
			if (title != null) {
				title = new File(title).getName();
				if (model.isModified()) {
					if (!diagramPanel.getTitle().endsWith("*"))
						diagramPanel.setTitle("*" + title);
				} else
					diagramPanel.setTitle(title);
			}
		}

		revalidate();
		repaint();
	
	}

	public Diagram model;
	DiagramController controller = null;

	public PopupMenu popup;
	public Grid grid;
	public EditorPanel diagramPanel;

	public double gridSize;
	public boolean hideGrid;

	public static final int GRID = 10;

}