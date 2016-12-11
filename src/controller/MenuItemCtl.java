package controller;

import java.awt.*;
import java.awt.event.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.event.*;

public class MenuItemCtl extends Controller
{
	
	private JMenuItem menuItem;
	
		// Constructor for JMenu item
	public MenuItemCtl(JMenu menu,		
						   String prefix, 	// Prefix for resources
						   ResourceBundle bundle,
						   String icon,
						   Executor wExec)
	{
		super((JComponent) new JMenuItem(), null, wExec);
		menu.add(initMenuItem(prefix, bundle, icon, wExec));					// Finally, add to menu
	}
	
	// Constructor for JMenu item
	public MenuItemCtl(JPopupMenu menu,		
						   String prefix, 	// Prefix for resources
						   ResourceBundle bundle,
						   String icon,
						   Executor wExec)
	{
		super((JComponent) new JMenuItem(), null, wExec);
		menu.add(initMenuItem(prefix, bundle, icon, wExec));					// Finally, add to menu
	}
	
	public JMenuItem initMenuItem( String prefix, 	// Prefix for resources
			   ResourceBundle bundle,
			   String icon,
			   Executor wExec)
	{
		
		menuItem = (JMenuItem) myComponent;
		Icon theIcon = new ImageIcon(icon);
		menuItem.setIcon(theIcon);
		
		String text = bundle.getString(prefix + ".text");
		menuItem.setText(text);
		try {
			String m = bundle.getString(prefix + ".mnemonic");
			menuItem.setMnemonic(m.charAt(0));
		} catch (MissingResourceException exception) {
			// ok not to set mnemonic
		}

		try {
			String accelerator = bundle.getString(prefix + ".accelerator");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		} catch (MissingResourceException exception) {
			// ok not to set accelerator
		}

		try {
			String tooltip = bundle.getString(prefix + ".tooltip");
			menuItem.setToolTipText(tooltip);
		} catch (MissingResourceException exception) {
			// ok not to set tooltip
		}

		menuItem.addActionListener(this);	// Add listeners
		menuItem.addItemListener(this);
		
		return menuItem;
	}
	
	public void setEnabled(boolean enable)
	{
		menuItem.setEnabled(enable);
	}
	
	public JMenuItem getJMenuItem() { return menuItem; }

	public boolean getEnabled() {
		return menuItem.isEnabled();
	}
}
