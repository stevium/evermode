package view;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class StatusBarCell extends JPanel
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel text = null;

	public StatusBarCell(String cellText)
	{
		Border bordura = BorderFactory.createEtchedBorder();
		
		setBorder(bordura);
		
		text = new JLabel(cellText);
		
		add(text);
		
		setPreferredSize(new Dimension(300, 30));
	}

	public void setText(String text)
	{
		this.text.setText(text);;
	}

	public String getText()
	{
		return this.text.getText();
	}

}
