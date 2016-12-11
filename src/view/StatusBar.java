package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JSlider;
import javax.swing.border.Border;

import model.AppModel;
import model.Diagram;
import model.ProjectModel;
import model.abstracts.Edge;

public class StatusBar extends JPanel implements Observer
{

	private static final long serialVersionUID = 1L;
	private StatusBarCell westCell = null;
	private StatusBarCell centerCell = null;
	private StatusBarCell eastCell = null;

	/**
	 * Panel koji predstavlja statusnu liniju.
	 */
	private JPanel statusBar;
	
	public StatusBar(AppModel application)
	{
		setLayout(new BorderLayout());
		this.application = application;
		this.application.addObserver(this);
		westCell = new StatusBarCell("Selected: ");
		centerCell = new StatusBarCell("X: 0 Y: 0");
		eastCell = new StatusBarCell("Tool: ");
		
		add(westCell, BorderLayout.WEST);
		add(centerCell, BorderLayout.CENTER);
		add(eastCell, BorderLayout.EAST);
	}
	
	/**
	 * Metoda postavlja poruku na status bar u okviru indeksirane labele.
	 * 
	 * @param message
	 *            - String koji predstavlja poruku
	 * @param index
	 *            - indeks indeksirane labele {0 - general message(levo),1 -
	 *            (srednje),2 - state(desno)}
	 */
	public void setStatusBarMessage(String message, int index) {
		if (index == 1)
			westCell.setText(message); 
		if (index == 2)
			centerCell.setText(message); 
		else if (index == 3)
			eastCell.setText(message); 
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		try{
		//project = application.getSelectedProject();
		diagram = application.getSelectedDiagram();
		centerCell.setText(String.format("X : %.0f  Y : %.0f", diagram.lastMousePoint.getX(), diagram.lastMousePoint.getY()));
		if(diagram.getSelectedTool()!=null)
		{
			if(diagram.getSelectedTool() instanceof Edge)
				eastCell.setText("Tool: Insert " + ((Edge)diagram.getSelectedTool()).getName());
			else 
				eastCell.setText("Tool: Insert " + diagram.getSelectedTool().toString());
		}
			else 
			eastCell.setText("Tool: Select");
		
		if (diagram.getSelectedItem().size() == 1)
			for (Object item : diagram.getSelectedItem()) {
				westCell.setText("Selected: " + item.toString());
			}
		else 
			westCell.setText("Selected: ");
			
		}catch(Exception e) {};
	}
	
	ProjectModel project;
	Diagram diagram;
	AppModel application;
	
}
