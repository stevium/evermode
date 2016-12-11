package view.property;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
	Karakteristike(osobine) editora za TextArea tip.
*/
public class TextAreaEditor extends PropertyEditorSupport {
	public boolean supportsCustomEditor() {
		return true;
	}

	public Component getCustomEditor() {
		final TextArea value = (TextArea) getValue();
		final JTextArea textArea = new JTextArea(ROWS, COLUMNS);

		textArea.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
		textArea.setFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shiftTab);

		textArea.setText(value.getText());
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				value.setText(textArea.getText());
				firePropertyChange();
			}

			public void removeUpdate(DocumentEvent e) {
				value.setText(textArea.getText());
				firePropertyChange();
			}

			public void changedUpdate(DocumentEvent e) {
			}
		});
		return new JScrollPane(textArea);
	}

	// The actions
	private static Action nextFocusAction = new AbstractAction(
			"Move Focus Forward") {
		public void actionPerformed(ActionEvent evt) {
			((Component) evt.getSource()).transferFocus();
		}
	};
	private static Action prevFocusAction = new AbstractAction(
			"Move Focus Backwards") {
		public void actionPerformed(ActionEvent evt) {
			((Component) evt.getSource()).transferFocusBackward();
		}
	};

	private static final int ROWS = 5;
	private static final int COLUMNS = 30;

	private static Set tab = new HashSet(1);
	private static Set shiftTab = new HashSet(1);
	static {
		tab.add(KeyStroke.getKeyStroke("TAB"));
		shiftTab.add(KeyStroke.getKeyStroke("shift TAB"));
	}
}
