package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;

import model.Diagram;

public class PopupMenu extends JPopupMenu {

	public PopupMenu(Diagram model) {
		this.model = model;
		ResourceBundle editorResources = ResourceBundle
				.getBundle("MenuBundle");
		String tip = editorResources.getString("grabber.tooltip");

		PopupMenuItemCtl item = new PopupMenuItemCtl(popup, tip,
				"image/Select.gif", (char) 0, null, new WmvcPopupExecutor(0));

		ResourceBundle diagramResources = ResourceBundle.getBundle(model
				.getClass().getName()
				+ "Bundle");

		for (int i = 1; i < model.getTools().size(); i++) {
			tip = diagramResources.getString("tool" + i + ".tooltip");
			item = new PopupMenuItemCtl(popup, tip, "image/class/" + tip + ".png",
					(char) 0, null, new WmvcPopupExecutor(i));
		}
	}

	public void showPopup(final DiagramPanel panel, final Point2D p,
			final ActionListener listener) {
		popupListener = listener;
		popup.show(panel, (int) p.getX(), (int) p.getY());
	}

	private class WmvcPopupExecutor extends Executor {
		int i;

		public WmvcPopupExecutor(int i) {
			this.i = i;
		}

		public void execute(ActionEvent event) {
			model.setSelectedTool(i);
			if (popupListener != null)
				popupListener.actionPerformed(event);
		}
	}

	private ButtonGroup group;
	private JPopupMenu popup = new JPopupMenu();
	private ActionListener popupListener;
	private Diagram model;
}
