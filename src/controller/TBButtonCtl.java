package controller;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class TBButtonCtl extends Controller {
	private AbstractButton myButton;
	private Icon theIcon;

	// Constructor for JToolBar item: JButton
	public TBButtonCtl(JToolBar tb, ButtonGroup bg, Boolean selected,
			String text, // Button's text
			String icon, // the icon's name
			Dimension dim,
			String rolloverIcon,
			String tip, // tool tip
			Executor wExec) {
		super(bg!=null ? (JComponent) new JToggleButton() : (JComponent) new JButton(), tip, wExec);
		
		
		myButton = (AbstractButton) myComponent;
		//Dimension dim = new Dimension(40, 40);
		myButton.setPreferredSize(dim);

		if (text != null)
			myButton.setText(text);
		if (icon != null) {
			theIcon = new ImageIcon(icon);
			myButton.setIcon(theIcon);
		}
		if (rolloverIcon != null)
		{
			theIcon = new ImageIcon(rolloverIcon);
			myButton.setRolloverIcon(theIcon);
		}

		myButton.addActionListener(this); // add listener
		myButton.setSelected(selected);
		tb.add(myButton); // add to bar
		if(bg != null)
			bg.add(myButton);
		

	}

	public void setEnabled(boolean enable) {
		myButton.setEnabled(enable);
	}

	public void setSelected(boolean selected) {
		myButton.setSelected(selected);
	}

	public AbstractButton getJToggleButton() {
		return myButton;
	}
}
