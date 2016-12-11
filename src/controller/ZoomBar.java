package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Diagram;

public class ZoomBar extends JToolBar implements Observer {

	public ZoomBar(Diagram model) {
		this.model = model;
		model.addObserver(this);
		
		//zoom slider
		zoomSlider = new JSlider(-5, 5, 0);		
		zoomSlider.setMinorTickSpacing(1);
		zoomSlider.setMajorTickSpacing(1);
		//zoomSlider.setPaintLabels(true);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setSnapToTicks(true);
		Border b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY.brighter(), 1));
		zoomSlider.setBorder(b);
	
		zoomSlider.setEnabled(true);
		zoomSlider.setPreferredSize(new Dimension(200,30));
		
		zoomSlider.addChangeListener( new ChangeListener() {


			public void stateChanged(ChangeEvent e) {
				if(!updating)
				{
					if(!zoomSlider.getValueIsAdjusting())
					{
						int value = zoomSlider.getValue();
				
						if(value < zoom)
							model.changeZoom(-1);
						else if (value > zoom)
							model.changeZoom(1);	
						zoom = zoomSlider.getValue();
					}
				}
			}
		});
		//this.setPreferredSize(new Dimension(300,30));
		add(zoomSlider, BorderLayout.EAST);
		
	}


	private Diagram model;
	private boolean updating = false;
	JSlider zoomSlider;
	double zoom = 0;
	public void update(Observable o, Object arg) {
		//zoom = model.zoom;
		updating = true;
		final double FACTOR = 1.5;
		int value = 0;
		if (model.zoom > 1){
			double pom = model.zoom;
			while((pom) > 1){
				pom = (pom / FACTOR) ;
				value++;
			}
			
		}
		else if ((model.zoom) < 1){
			double pom = model.zoom;
			while((pom) < 1){
				pom = (pom* FACTOR) ;
				value--;
			}
			
		}
		zoomSlider.setValue(value);
		updating = false;
		repaint();
	}
}
