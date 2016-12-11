package view;

import helpers.DOMParser;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;







import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import model.AppModel;
import controller.MenuBar;
import controller.MenuBarController;
import controller.WorkspaceLauncherController;

/**
 * Klasa predstavlja prozor za izbor workspace-a u kome ce se raditi po pokretanju aplikacije.
 */
@SuppressWarnings("serial")
public class WorkspaceLauncher extends JDialog {

	private JLabel lblWorkspace = new JLabel("Workspace: "); 
	public JComboBox<String> txtWorkspace = new JComboBox();
	private JButton btnBrowse = new JButton("Browse"); 
	private JButton btnOK = new JButton("OK"); 
	private JButton btnCancel = new JButton("Cancel"); 
	private JPanel okCancelBox = new JPanel();
	private Box workspaceBox = Box.createHorizontalBox();
	
	/**
	 * Rezultat dijaloga:  false = cancel, true = ok
	 */
	private boolean dialogResult = false;
	private AppModel application;
	public MenuBar menuBar;
	public String currentWorkspace;
	
	public WorkspaceLauncher(AppModel model, MenuBar mb) {
		this.application = model;
		this.menuBar = mb;
		
		currentWorkspace = application.getOpenedWorkspace(); 
		if(currentWorkspace == null || currentWorkspace.isEmpty()) {
			currentWorkspace = System.getProperty("user.home") + File.separator + "Evermode"+ File.separator + "Default Workspace";				
		}
		txtWorkspace.setEditable(true);
		txtWorkspace.setPreferredSize(new Dimension(400, 15));
		txtWorkspace.addItem(currentWorkspace);
		File newWsDir = new File(currentWorkspace);
		if(!newWsDir.exists()){
			if (!newWsDir.mkdirs()){ 
			}
		}
		
		WorkspaceLauncherController.view = this;
		WorkspaceLauncherController.model = model;
		
		btnBrowse.addMouseListener(new WorkspaceLauncherController.BrowseActionListener());
		btnOK.addMouseListener(new WorkspaceLauncherController.OkActionListener());
		btnCancel.addMouseListener(new WorkspaceLauncherController.CancelActionListener());
		
		init();

	}

	private void init() 
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Workspace Launcher");
		setLayout(new GridBagLayout());
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getCenterPoint();

		Container container = getContentPane();

		
		
		GridBagConstraints c00 = new GridBagConstraints();
		c00.gridx = 0;
		c00.gridy = 0;
		c00.gridwidth = 2;
		c00.fill = GridBagConstraints.HORIZONTAL;
		c00.anchor = GridBagConstraints.WEST;
		c00.insets = new Insets(20, 20, 0, 20);
		
		
		GridBagConstraints c02 = new GridBagConstraints();
		c02.gridx = 0;
		c02.gridy = 2;
		c02.gridwidth = 2;
	
		c02.fill = GridBagConstraints.HORIZONTAL;
		c02.anchor = GridBagConstraints.WEST;
		c02.insets = new Insets(5, 20, 0, 20);

		GridBagConstraints c13 = new GridBagConstraints();
		c13.gridx = 1;
		c13.gridy = 3;
		c13.anchor = GridBagConstraints.EAST;
		c13.insets = new Insets(70, 0, 10, 16);

		
		GridBagConstraints c03 = new GridBagConstraints();
		c03.gridx = 0;
		c03.gridy = 3;
		c03.anchor = GridBagConstraints.EAST;
		c03.insets = new Insets(20, 20, 0, 20);
		
		
		GridBagConstraints c01 = new GridBagConstraints();
		c01.gridx = 0;
		c01.gridy = 1;
		c01.gridwidth = 2;
	
		c01.fill = GridBagConstraints.HORIZONTAL;
		c01.anchor = GridBagConstraints.WEST;
		c01.insets = new Insets(5, 2, 5, 2);

		
		
		btnOK.setPreferredSize(new Dimension(80, 25));
		btnCancel.setPreferredSize(new Dimension(80, 25));
		btnBrowse.setPreferredSize(new Dimension(80, 25));
		
		Checkbox defaultCB = new Checkbox("Postavite kao podrazumijevani Workspace");
		JLabel text = new JLabel("<html><b>Izaberize Workspace</b><br/><br/>&nbsp&nbsp&nbsp Evermode smješta vaše projekte u folder zvani Workspace."
				+ "<br/>&nbsp&nbsp&nbsp Izaberite folder za workspace koji ćete koristi pri ovoj sesiji.<br/><br/></html>");
	//	okCancelBox.add(defaultCB);
		
		text.setFont(new Font("Dialog", Font.PLAIN, 12));
		//text.setPreferredSize(new Dimension(400, 100));
		okCancelBox.add(btnOK);
		okCancelBox.add(Box.createHorizontalStrut(5));
		okCancelBox.add(btnCancel);
		
		workspaceBox.add(lblWorkspace);
		workspaceBox.add(Box.createHorizontalStrut(10));
		workspaceBox.add(txtWorkspace);
		workspaceBox.add(Box.createHorizontalStrut(15));
		workspaceBox.add(btnBrowse);
		
		container.add(text, c00);
		container.add(new JSeparator(), c01);
		container.add(workspaceBox, c02);
		container.add(defaultCB, c03);
		container.add(okCancelBox, c13);

		this.pack();
		setLocation(center.x - getSize().width / 2, center.y - getSize().height / 2);		
	}

	public boolean isDialogResult() {
		return dialogResult;
	}
	
	public void setDialogResult(boolean result) {
		dialogResult = result;
	}
}




