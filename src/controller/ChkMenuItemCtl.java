package controller;

import java.awt.*;
import java.awt.event.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.event.*;

public class ChkMenuItemCtl extends Controller {
	private JMenu myMenu;
	private JCheckBoxMenuItem checkBoxItem;

	// Constructor for JMenu item: JCheckBoxMenuItem
	public ChkMenuItemCtl(JMenu menu, 
			String prefix,
			ResourceBundle bundle,
			String icon,
			Executor wExec) {
		super((JComponent) new JCheckBoxMenuItem(), null, wExec);
		myMenu = menu;
		checkBoxItem = (JCheckBoxMenuItem) myComponent;
		String text = bundle.getString(prefix + ".text");
		checkBoxItem.setText(text);
		try {
			String mnemonic = bundle.getString(prefix + ".mnemonic");
			checkBoxItem.setMnemonic(mnemonic.charAt(0));
		} catch (MissingResourceException exception) {
		}
		try {
			String accelerator = bundle.getString(prefix + ".accelerator");
			checkBoxItem.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		} catch (MissingResourceException exception) {
		}
		try {
			String tooltip = bundle.getString(prefix + ".tooltip");
			checkBoxItem.setToolTipText(tooltip);
		} catch (MissingResourceException exception) {
		}

		try {
			Icon theIcon = new ImageIcon(icon);
			checkBoxItem.setIcon(theIcon);
		} catch (MissingResourceException exception) {
		}

		checkBoxItem.addActionListener(this); // Add listeners
		checkBoxItem.addItemListener(this);
		myMenu.add(checkBoxItem); // Finally, add to menu
	}

	public boolean getState() {
		return checkBoxItem.getState();
	}

	public void setState(boolean checked) {
		checkBoxItem.setState(checked);
	}

	public JMenu getJMenu() {
		return myMenu;
	}

	public JCheckBoxMenuItem getJChecBoxMenuItem() {
		return checkBoxItem;
	}
}
