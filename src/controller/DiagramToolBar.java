package controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import model.Diagram;

public class DiagramToolBar extends JToolBar implements Observer {

	public DiagramToolBar(Diagram model) {
		this.model = model;
		model.addObserver(this);
		group = new ButtonGroup();
		ResourceBundle editorResources = ResourceBundle
				.getBundle("MenuBundle");
		String tip = editorResources.getString("grabber.tooltip");

		Dimension dim = new Dimension(30, 30);
		
		TBButtonCtl button = new TBButtonCtl(this, group, true, null,
				"image/Select.gif", dim, null, tip, new TBBExecutor());

		ResourceBundle diagramResources = ResourceBundle.getBundle(model
				.getClass().getName()
				+ "Bundle");

		for (int i = 1; i < model.getTools().size(); i++) {
			tip = diagramResources.getString("tool" + i + ".tooltip");
			button = new TBButtonCtl(this, group, true, null, "image/class/" + tip
					+ ".png",  dim, null, tip, new TBBExecutor());
		}
		
	}

	private class TBBExecutor extends Executor {
		public void execute(ActionEvent event) {
			for (int i = 0; i < model.getTools().size(); i++) {
				JToggleButton button = (JToggleButton) getComponent(i);
				if (button.isSelected())
					model.setSelectedTool(i);
			}
		}
	}

	private ButtonGroup group;
	private Diagram model;

	public void update(Observable o, Object arg) {
		int i = model.getSelectedToolIndex();
		((JToggleButton) getComponent(i)).setSelected(true);
	}
}
