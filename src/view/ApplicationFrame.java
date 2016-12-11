package view;

import helpers.DOMParser;
import helpers.ExtensionHelper;
import helpers.FileHelper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.Executor;
import controller.MenuBar;
import controller.MenuBarController;
import controller.MenuBarController.newClassDiagramExecutor;
import model.Diagram;
import model.AppModel;

@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame {

	public ApplicationFrame(Class appClass) {

		// Instanciranje Workspace Modela
		application = DOMParser.readInitalSettings(new File("appProp.xml"));
		DOMParser.addModel(application);
		String appClassName = appClass.getName();
		appResources = ResourceBundle.getBundle(appClassName + "Bundle");
		editorResources = ResourceBundle.getBundle("MenuBundle");

		// Setting Look and Feel
		String laf = application.getTheme();
		if (laf != null)
			changeLookAndFeel(laf);
		
		setTitle(appResources.getString("app.name"));

		setBounds((int)application.getWindowPosition().getX(), (int)application.getWindowPosition().getY(), 
				(int)application.getWindowDimension().getWidth(),
				(int)application.getWindowDimension().getHeight());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				((MenuBarController.fileExitExecutor) ((MenuBar) getJMenuBar()).fileExit
						.getWmvcExecutor()).execute();
				
			}
		});

		ImageIcon icon = new ImageIcon("image/general/wiz16.png");
		setIconImage(icon.getImage());
		// Instanciate and Layout Main Views
		tabPane = new JTabbedPane();
		treePane = new JPanel(new BorderLayout());
		ProjectExplorer pe = new ProjectExplorer(application);
		treePane.add(pe);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane,
				tabPane);
		splitPane.setOneTouchExpandable(true); // details
		splitPane.setDividerLocation(300);
		Dimension minimumSize = new Dimension(100, 50);
		treePane.setMinimumSize(minimumSize);
		tabPane.setMinimumSize(minimumSize);
		
		
		JPanel BorderPanel = new JPanel(new BorderLayout());
		
		menuBar = new MenuBar(application, this);
		setJMenuBar(menuBar);
		
		BorderPanel.setLayout(new BorderLayout());
		BorderPanel.add(splitPane, BorderLayout.CENTER);
		BorderPanel.add(new CustomToolBar(menuBar, application), BorderLayout.NORTH);
		BorderPanel.add(new StatusBar(application), BorderLayout.SOUTH);
		
		
		setContentPane(BorderPanel);
		defaultExtension = appResources.getString("files.extension");

		extensionHelper = new ExtensionHelper(
				appResources.getString("files.name"),
				new String[] { defaultExtension });
		
		
	}

	public void changeLookAndFeel(String lafName) {
		try {
			UIManager.setLookAndFeel(lafName);
			SwingUtilities.updateComponentTreeUI(ApplicationFrame.this);
		} catch (ClassNotFoundException ex) {
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (UnsupportedLookAndFeelException ex) {
		}
	}

	public void addGraphFrame(final JPanel gpanel) {

		String name = new File(((EditorPanel)gpanel).model.getName()).getName();
		tabPane.addTab(name, null, gpanel, ((EditorPanel)gpanel).model.getName());
		int tabCount = tabPane.getTabCount();
		tabPane.setSelectedIndex(tabCount - 1);
		
		setTitle();
		tabPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				try{
				application.setSelectedDiagram(((EditorPanel)tabPane.getSelectedComponent()).model);
				application.notifyViews();
				} catch (Exception E) {application.setSelectedDiagram(null);}
			}
		});
		
		tabPane.setTabComponentAt(tabCount -1, new ButtonTabComponent(tabPane, menuBar.windowClose.getWmvcExecutor()));
	}

	
	private static class ButtonTabComponent extends JPanel {
	    private final JTabbedPane pane;
	    public ButtonTabComponent(final JTabbedPane pane, Executor exec) {
	        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        if (pane == null) {
	            throw new NullPointerException("TabbedPane is null");
	        }
	        this.pane = pane;
	        setOpaque(false);

	        JLabel label = new JLabel() {
	            public String getText() {
	                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
	                if (i != -1) {
	                    return pane.getTitleAt(i);
	                }
	                return null;
	            }
	        };
	        label.setIcon( new ImageIcon("image/general/diagram.png"));
	        label.setPreferredSize(new Dimension(100, 18));
	        add(label);
	 
	        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
	        JButton button = new JButton(new ImageIcon("image/closeTabButton.png"));
	        
	        int size = 12;
            button.setPreferredSize(new Dimension(size, size));
            button.setToolTipText("close this tab");
            button.setUI(new BasicButtonUI());
            button.setContentAreaFilled(false);
            button.setFocusable(false);
            button.setBorder(BorderFactory.createEtchedBorder());
            button.setBorderPainted(false);
            button.setRolloverEnabled(true);
            button.setRolloverIcon(new ImageIcon("image/closeTabButton_hover.png"));
            button.setPressedIcon(new ImageIcon("image/closeTabButton_push.png"));
	       button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					exec.execute(e);
				}
	        });
	        add(button);
	        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	    }
	}
	
	public void setTitle() {
		String appName = appResources.getString("app.name");
		EditorPanel diagramPanel = (EditorPanel) tabPane.getSelectedComponent();
		if (diagramPanel == null)
			setTitle(appName);
		else {
			String fileName = (new File(diagramPanel.model.getName()).getName());
			if (fileName == null)
				setTitle(appName);
			else
				setTitle(appName + " - " + fileName);
		}
	}

	public static Diagram read(String in) {
		return DOMParser.readDiagram(new File(in));
	}

	public static void saveFile(Diagram diagram, String out) {
		DOMParser.writeDiagram(diagram, new File(out));
	}

	public void savePreferences() {
		application.setWindowPosition(this.getBounds().getX(), this.getBounds().getY());
		application.setWindowDimension(this.getBounds().getWidth(), this.getBounds().getHeight());
		DOMParser.writeInitalSettings(application);
	}

	public boolean showWorkspaceChooser() {
		WorkspaceLauncher wsl = new WorkspaceLauncher(application, (MenuBar) getJMenuBar());
		wsl.setVisible(true);
  	    fileHelper = FileHelper.getInstance(new File(application.getOpenedWorkspace()));
		return wsl.isDialogResult();
	}

	
	
	
	private AppModel application;

	public ResourceBundle appResources;
	public ResourceBundle editorResources;
	public JTabbedPane tabPane;
	public MenuBar menuBar;
	public JPanel treePane;
	public JSplitPane splitPane;
	public FileHelper fileHelper;
	public JMenu newMenu;
	public String defaultExtension;
	public JMenu recentFilesMenu;

	public ExtensionHelper extensionHelper;

}
