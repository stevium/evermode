package controller;

import java.awt.event.*;

import javax.swing.*;

public class Controller implements ActionListener, ItemListener {
	protected JComponent myComponent;
	private Executor executor; // The Executor object

	// This constructor is use by the subobjects
	public Controller(JComponent comp, // the component
			String tip, Executor wExec) {
		myComponent = comp;
		executor = wExec;

		if (tip != null)
			myComponent.setToolTipText(tip);
	}

	public Executor getWmvcExecutor() {
		return executor;
	}

	// implement the ActionListener
	public void actionPerformed(ActionEvent event) {
		if (executor != null) {
			executor.execute(event);
		}
	}

	// implement ItemListener
	public void itemStateChanged(ItemEvent event) {
		if (executor != null)
			executor.execute(event);
	}
}
