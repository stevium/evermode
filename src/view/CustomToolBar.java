package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import model.AppModel;
import model.Diagram;
import model.ProjectModel;
import controller.Executor;
import controller.MenuBar;
import controller.TBButtonCtl;

public class CustomToolBar extends JPanel implements Observer
{
	private static final long serialVersionUID = 1L;
	public JButton [] newO = new JButton[9];
	//private JTextField txtField = null;
	
	
	TBButtonCtl newDiagram, openDiagram, saveDiagram, newProject,
		cut, copy, paste, delete, undo, redo, zoomin, zoomout, saveAsDiagram,
		rotateLeft, rotateRight, resize;
	MenuBar mb;
	AppModel application;
	
	public CustomToolBar(MenuBar mb, AppModel application)
	{
		this.application = application;
		this.application.addObserver(this);
		this.mb = mb;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JToolBar DocumentTB = new JToolBar();
		JToolBar DiagramTB = new JToolBar();
		JToolBar UndoRedoTB = new JToolBar();
		JToolBar ZoomTB = new JToolBar();
		JToolBar EditTB = new JToolBar();
		
		DocumentTB.setRollover(true); 
		DocumentTB.setBorderPainted(true);
		DocumentTB.setFloatable(true); 
		
		UndoRedoTB.setRollover(true);
		UndoRedoTB.setFloatable(true);
		UndoRedoTB.setBorderPainted(true);
		
		EditTB.setBorderPainted(true);
		EditTB.setRollover(true); 
		EditTB.setFloatable(true);
		
		ZoomTB.setRollover(true); 
		ZoomTB.setBorderPainted(true);
		ZoomTB.setFloatable(true); 
		
		DiagramTB.setRollover(true);
		DiagramTB.setFloatable(true);
		DiagramTB.setBorderPainted(true);
				
		//DocumentTB.setOrientation(JToolBar.HORIZONTAL);
		Dimension dim = new Dimension(40, 40);
		
		newProject = new TBButtonCtl(DocumentTB, null, false, null, "image/general/project.png", dim, null, "New Project", mb.fileNewProject.getWmvcExecutor());
		newDiagram = new TBButtonCtl(DocumentTB, null, false, null, "image/general/new_model.png", dim, null, "New Diagram", mb.fileNewClassDiagram.getWmvcExecutor());
		openDiagram = new TBButtonCtl(DocumentTB, null, false, null, "image/general/open.png", dim, null, "Open Diagram", mb.fileOpen.getWmvcExecutor());
		saveDiagram = new TBButtonCtl(DocumentTB, null, false, null, "image/general/save.png", dim, null, "Save Diagram", mb.fileSave.getWmvcExecutor());
		saveAsDiagram = new TBButtonCtl(DocumentTB, null, false, null, "image/general/save_as.png", dim, null, "Save Diagram As", mb.fileSaveAs.getWmvcExecutor());
		
		newDiagram.setEnabled(false);
		saveDiagram.setEnabled(false);
		
		cut = new TBButtonCtl(DiagramTB, null, false, null, "image/general/cut.png", dim, null, "Cut", mb.cutItem.getWmvcExecutor());
	    copy = new TBButtonCtl(DiagramTB, null, false, null, "image/general/copy.png", dim, null, "Copy", mb.copyItem.getWmvcExecutor());
		paste = new TBButtonCtl(DiagramTB, null, false, null, "image/general/paste.png", dim, null, "Paste", mb.pasteItem.getWmvcExecutor());
		delete = new TBButtonCtl(DiagramTB, null, false, null, "image/general/delete.png", dim, null, "Delete", mb.deleteItem.getWmvcExecutor());
		
		cut.setEnabled(false);
		copy.setEnabled(false);
		paste.setEnabled(false);
		delete.setEnabled(false);
		
		undo = new TBButtonCtl(UndoRedoTB, null, false, null, "image/general/undo.png", dim, null, "Undo", mb.undoItem.getWmvcExecutor());
		redo = new TBButtonCtl(UndoRedoTB, null, false, null, "image/general/redo.png", dim, null, "Redo", mb.redoItem.getWmvcExecutor());
		
		undo.setEnabled(false);
		redo.setEnabled(false);
		
		zoomin = new TBButtonCtl(ZoomTB, null, false, null, "image/general/zoomout.png", dim, null, "Zoom In", mb.viewZoomIn.getWmvcExecutor());
		zoomout = new TBButtonCtl(ZoomTB, null, false, null, "image/general/zoomin.png", dim, null, "Zoom Out", mb.viewZoomOut.getWmvcExecutor());
		rotateLeft = new TBButtonCtl(EditTB, null, false, null, "image/general/rotate_left.png", dim, null, "Rotate Left", new Executor());
		rotateRight = new TBButtonCtl(EditTB, null, false, null, "image/general/rotate_right.png", dim, null, "Rotate Right", new Executor());
		resize = new TBButtonCtl(EditTB, null, false, null, "image/general/resize.png", dim, null, "Resize", new Executor());
		
		
		zoomin.setEnabled(false);
		zoomout.setEnabled(false);
		
		this.add(DocumentTB);
		this.add(DiagramTB);
		this.add(UndoRedoTB);
		this.add(ZoomTB);
		this.add(EditTB);
		
		}
	@Override
	public void update(Observable o, Object arg) {
	
		ProjectModel project = application.getSelectedProject();
		newDiagram.setEnabled(project!=null);
		
		Diagram diagram = application.getSelectedDiagram();
		
		
		zoomin.setEnabled(diagram != null);
	    zoomout.setEnabled(diagram!=null);
		saveDiagram.setEnabled(diagram!= null && diagram.isModified());
		
		
		undo.setEnabled(diagram!= null && diagram.graphHistoryCursor > 0);
		redo.setEnabled(diagram!= null &&  (diagram.graphHistory.size() - diagram.graphHistoryCursor) > 1);
		
		copy.setEnabled(diagram!= null && !diagram.selectedItems.isEmpty());
	    cut.setEnabled(diagram!= null &&  !diagram.selectedItems.isEmpty());
	    delete.setEnabled(diagram!= null && !diagram.selectedItems.isEmpty());
	    paste.setEnabled(mb.pasteItem.getEnabled());
	}
		
}
