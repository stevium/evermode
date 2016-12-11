package view;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import controller.DiagramPanel;
import controller.DiagramToolBar;
import controller.ZoomBar;
import model.Diagram;

public class EditorPanel extends JPanel {

	public Diagram model;
	public EditorPanel(Diagram model) {
		this.model = model;
		toolBar = new DiagramToolBar(model);
		zoomBar = new ZoomBar(model);
		panel = new DiagramPanel(model);
		this.setLayout(new BorderLayout());
		this.add(toolBar, BorderLayout.NORTH);
		this.add(zoomBar, BorderLayout.SOUTH);
		JScrollPane sp = new JScrollPane(panel);
		sp.getVerticalScrollBar().setUnitIncrement(10);
		sp.getHorizontalScrollBar().setUnitIncrement(10);
		this.add(sp, BorderLayout.CENTER);

		// add listener to confirm diagramPanel closing
		addVetoableChangeListener(new VetoableChangeListener() {
			public void vetoableChange(PropertyChangeEvent event)
					throws PropertyVetoException {
				String name = event.getPropertyName();
				Object value = event.getNewValue();

				// we only want to check attempts to close a diagramPanel
				if (name.equals("closed") && value.equals(Boolean.TRUE)
						&& model.isModified()) {
					ResourceBundle editorResources = ResourceBundle
							.getBundle("EditorStrings");

					// ask user if it is ok to close
					int result = JOptionPane.showInternalConfirmDialog(
							EditorPanel.this,
							editorResources.getString("dialog.close.ok"), null,
							JOptionPane.YES_NO_OPTION);

					// if the user doesn't agree,cancel the close
					if (result != JOptionPane.YES_OPTION)
						throw new PropertyVetoException("User canceled close",
								event);
				}
			}
		});

	}

	public DiagramPanel getGraphPanel() {
		return panel;
	}

	public void setTitle(String title) {
		((JTabbedPane) getParent()).setTitleAt(
				((JTabbedPane) getParent()).getSelectedIndex(), title);
	}

	public String getTitle() {
		return ((JTabbedPane) getParent())
				.getTitleAt(((JTabbedPane) getParent()).getSelectedIndex());
	}

	public DiagramPanel panel;
	private DiagramToolBar toolBar;
	public ZoomBar zoomBar;
	private String fileName;
}
