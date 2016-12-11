package controller;

import java.util.MissingResourceException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import model.AppModel;
import model.Diagram;
import model.ProjectModel;
import view.ApplicationFrame;

public class MenuBar extends JMenuBar implements Observer{

	// set up menus

	private JMenu createMenu(String prefix) {
		ResourceBundle bundle = ResourceBundle
				.getBundle("MenuBundle");
		String text = bundle.getString(prefix + ".text");
		JMenu menu = new JMenu(text);
		try {
			String mnemonic = bundle.getString(prefix + ".mnemonic");
			menu.setMnemonic(mnemonic.charAt(0));
		} catch (MissingResourceException exception) {
			// ok not to set mnemonic
		}

		try {
			String tooltip = bundle.getString(prefix + ".tooltip");
			menu.setToolTipText(tooltip);
		} catch (MissingResourceException exception) {
			// ok not to set tooltip
		}
		return menu;
	}

	public MenuBarController mbController;
	public AppModel application;
	
	public ChkMenuItemCtl hideGridItem;
	public MenuItemCtl undoItem;
	public MenuItemCtl redoItem;
	public MenuItemCtl pasteItem;
	public MenuItemCtl copyItem;
	public MenuItemCtl cutItem;
	public MenuItemCtl deleteItem;
	public MenuItemCtl fileExit;
	public MenuItemCtl fileOpen;
	public MenuItemCtl fileNewClassDiagram;
	public MenuItemCtl fileSave;
	public MenuItemCtl fileSaveAs;
	public MenuItemCtl viewZoomIn;
	public MenuItemCtl viewZoomOut;
	public MenuItemCtl windowClose;
	public MenuItemCtl fileNewProject;

	public MenuBar(AppModel application, ApplicationFrame diagramPanel) {
		this.application = application;
		mbController = new MenuBarController(application, diagramPanel, this);
		application.addObserver(this);
		
		ResourceBundle bundle = ResourceBundle
				.getBundle("MenuBundle");
		
		JMenu fileMenu = createMenu("file");
		this.add(fileMenu);
		
		JMenu newMenu = createMenu("file.new");
		newMenu.setIcon(new ImageIcon("image/general/new_model.png"));
		fileMenu.add(newMenu);

		fileNewProject  = new MenuItemCtl(newMenu, "file.new.project", bundle, "image/general/project.png", mbController.new newProjectExecutor());
		fileNewClassDiagram = new MenuItemCtl(newMenu, "file.new.class_diagram", bundle, "image/general/new_class.png", mbController. new newClassDiagramExecutor());
		
		fileOpen = new MenuItemCtl(fileMenu, "file.open", bundle, "image/general/open.png", mbController.new fileOpenExecutor());
		fileMenu.addSeparator();
		fileSave = new MenuItemCtl(fileMenu, "file.save", bundle, "image/general/save.png", mbController.new fileSaveExecutor());
		fileSaveAs = new MenuItemCtl(fileMenu, "file.save_as", bundle, "image/general/save_as.png",mbController.new fileSaveAsExecutor());
		fileMenu.addSeparator();
		fileExit  = new MenuItemCtl(fileMenu, "file.exit", bundle, "image/general/exit.png", mbController.new fileExitExecutor());

		JMenu editMenu = createMenu("edit");
		this.add(editMenu);

		new MenuItemCtl(editMenu, "edit.properties", bundle, "image/general/settings.png", mbController.new editPropertiesExecutor());
		copyItem = new MenuItemCtl(editMenu, "edit.copy", bundle, "image/general/copy.png", mbController.new editCopyExecutor());
		pasteItem = new MenuItemCtl(editMenu, "edit.paste", bundle, "image/general/paste.png", mbController.new editPasteExecutor());
		cutItem = new MenuItemCtl(editMenu, "edit.cut", bundle, "image/general/cut.png", mbController.new editCutExecutor());
		undoItem = new MenuItemCtl(editMenu, "edit.undo", bundle, "image/general/undo.png", mbController.new editUndoExecutor());
		redoItem = new MenuItemCtl(editMenu, "edit.redo", bundle, "image/general/redo.png", mbController.new editRedoExecutor());
		deleteItem = new MenuItemCtl(editMenu, "edit.delete", bundle, "image/general/delete.png", mbController.new editDeleteExecutor());
		
		editMenu.addSeparator();
		
		new MenuItemCtl(editMenu, "edit.select_next", bundle, null, mbController.new editSelectNextExecutor());
		new MenuItemCtl(editMenu, "edit.select_previous", bundle, null, mbController.new editSelectPreviousExecutor());
		//editMenu.addMenuListener(mbController.new EditMenuListener());
		JMenu viewMenu = createMenu("view");
		this.add(viewMenu);
		copyItem.setEnabled(false);
		pasteItem.setEnabled(false);
		undoItem.setEnabled(false);
		redoItem.setEnabled(false);
		cutItem.setEnabled(false);
		deleteItem.setEnabled(false);

		viewZoomIn = new MenuItemCtl(viewMenu, "view.zoom_out", bundle, "image/general/zoomin.png", mbController.new viewZoomOutExecutor());
		viewZoomOut = new MenuItemCtl(viewMenu, "view.zoom_in", bundle, "image/general/zoomout.png", mbController.new viewZoomInExecutor());
		viewMenu.addSeparator();
		new MenuItemCtl(viewMenu, "view.grow_drawing_area", bundle, null, mbController.new viewGrowDrawingAreaExecutor());
		new MenuItemCtl(viewMenu, "view.clip_drawing_area", bundle, null, mbController.new viewClipDrawingAreaExecutor());
		new MenuItemCtl(viewMenu, "view.smaller_grid", bundle, "image/general/smaller_grid.png", mbController.new viewSmallerGridExecutor());
		new MenuItemCtl(viewMenu, "view.larger_grid", bundle, "image/general/larger_grid.png", mbController.new viewLargerGridExecutor());
		hideGridItem = new ChkMenuItemCtl(viewMenu, "view.hide_grid", bundle, "image/general/hide_grid.png", mbController.new viewHideGridExecutor());
		viewMenu.addMenuListener(mbController.new ViewMenuListener());
		viewMenu.addSeparator();
		JMenu lafMenu = createMenu("view.change_laf");
		viewMenu.add(lafMenu);

		 UIManager.LookAndFeelInfo[] infos =
	 					UIManager.getInstalledLookAndFeels();
	      for (int i = 0; i < infos.length; i++)
	      {
	         final UIManager.LookAndFeelInfo info = infos[i];
	         JMenuItem item = new JMenuItem(info.getName());
	         lafMenu.add(item);
	         item.addActionListener(mbController.new lafMenuListener(info));
	      }


		JMenu windowMenu = createMenu("window");
		this.add(windowMenu);
		new MenuItemCtl(windowMenu, "window.next", bundle, null, mbController.new windowNextExecutor());
		new MenuItemCtl(windowMenu, "window.previous", bundle, null, mbController.new windowPreviousExecutor());
		windowMenu.addSeparator();
		new MenuItemCtl(windowMenu, "window.restore", bundle, null, mbController.new windowRestoreExecutor());
		windowClose = new MenuItemCtl(windowMenu, "window.close", bundle, "image/general/close.png", mbController.new windowCloseExecutor());


		JMenu helpMenu = createMenu("help");
		this.add(helpMenu);
		
		new MenuItemCtl(helpMenu, "help.about", bundle, "image/general/about.png", mbController.new helpShowAboutExecutor());

	}

	@Override
	public void update(Observable arg0, Object arg1) {
	try{
		//ProjectModel project = application.getSelectedProject();
		Diagram diagram = application.getSelectedDiagram();
		undoItem.setEnabled(diagram.graphHistoryCursor > 0);
		redoItem.setEnabled((diagram.graphHistory.size() - diagram.graphHistoryCursor) > 1);
		
		copyItem.setEnabled(!diagram.selectedItems.isEmpty());
	    cutItem.setEnabled(!diagram.selectedItems.isEmpty());
	    deleteItem.setEnabled(!diagram.selectedItems.isEmpty());
	}
	catch(Exception e) {};
	}
}
