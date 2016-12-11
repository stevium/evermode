package view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.EventObject;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import model.AppModel;
import model.CustomTreeModel;
import model.Diagram;
import model.DiagramNode;
import model.ProjectModel;
import model.ProjectNode;
import model.diagram.PackageNode;
import controller.Executor;
import controller.MenuBarController;
import controller.MenuItemCtl;
import controller.MenuBarController.fileOpenExecutor;
import controller.MenuBarController.newClassDiagramExecutor;
import controller.MenuBarController.newProjectExecutor;
import controller.MenuBarController.windowCloseExecutor;
	

public class ProjectExplorerController {
	JMenuItem add;
	JMenuItem remove;
	JMenuItem rename;
	JPopupMenu popupMenu;
	JTree tree;
	AppModel model;
	DiagramNode selectedDiagram;
	ProjectNode selectedProject;
	int selRow;
	TreePath path;
	MenuItemCtl newItem, deleteItem, renameItem, newProjectItem;
	boolean deleted;
	
public ProjectExplorerController(JTree tree, AppModel model)
{
	this.model = model;
	
	//Postavljanje popup prozora na desni klik
	popupMenu = new JPopupMenu();
	this.tree = tree;
	MyTreeCellEditor editor = new MyTreeCellEditor(tree,
            (CustomTreeCellRenderer) tree.getCellRenderer());
	tree.setCellEditor(editor);
	tree.setEditable(true);
	
	
    tree.addMouseListener(new MouseHandler());
	
	ResourceBundle bundle = ResourceBundle
			.getBundle("MenuBundle");

	newProjectItem = new MenuItemCtl(popupMenu, "popup.new.project", bundle, "image/project_opened.gif", new newProject());
	newItem = new MenuItemCtl(popupMenu, "popup.new.class_diagram", bundle, "image/newFile.png", new newDiagram());
	deleteItem = new MenuItemCtl(popupMenu, "popup.delete", bundle, "image/cross-script.png", new delete());
	renameItem = new MenuItemCtl(popupMenu, "popup.rename", bundle, "image/rename.png", new renameDiagram());
	
	
}
	
	private static class MyTreeCellEditor extends DefaultTreeCellEditor {

	    public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
	        super(tree, renderer);
	    }
	
	    @Override
	    public Component getTreeCellEditorComponent(JTree tree, Object value,
	            boolean isSelected, boolean expanded, boolean leaf, int row) {
	        if (value instanceof DiagramNode) {
	            value = ((DiagramNode) value).getName();
	        }
	        else if (value instanceof ProjectNode){
	        	value = ((ProjectNode) value).getName();
	        }
	        return super.getTreeCellEditorComponent(tree, value, isSelected, expanded,
	                leaf, row);
	    }

	    @Override
	    public boolean isCellEditable(EventObject e) {
	        return super.isCellEditable(e)
	            && (((TreeNode) lastPath.getLastPathComponent()) instanceof DiagramNode
	            		|| ((TreeNode) lastPath.getLastPathComponent()) instanceof ProjectNode);
	    	
	    }
	}


	public class newProject extends Executor {
		public void execute(ActionEvent event) {
			MenuBarController.menuBarView.fileNewProject.getWmvcExecutor().execute(event);
		}
	}
	
	public class newDiagram extends Executor {
		public void execute(ActionEvent event) {
			MenuBarController.menuBarView.fileNewClassDiagram.getWmvcExecutor().execute(event);
		}
	}


	public class delete extends Executor {
		public void execute(ActionEvent event) {
			if(selectedDiagram != null)
			{
				File file = new File(selectedDiagram.getUserObject().getName());
				((windowCloseExecutor)MenuBarController.menuBarView.windowClose.getWmvcExecutor()).close(
						selectedDiagram.getUserObject());
				model.getSelectedProject().getDiagrams().remove(selectedDiagram.getUserObject());
				((DefaultTreeModel)tree.getModel()).removeNodeFromParent(selectedDiagram);
				file.delete();
			}
			if(selectedProject != null)
			{
				File file = new File(selectedProject.getUserObject().getProjectName());
				for (Diagram diagram : selectedProject.getUserObject().getDiagrams()) {
					((windowCloseExecutor)MenuBarController.menuBarView.windowClose.getWmvcExecutor()).close(
							diagram);
				}
				deleteDirectory(file);
				model.setSelectedProject(null);
				model.getProjects().remove(selectedProject.getUserObject());
				((DefaultTreeModel)tree.getModel()).removeNodeFromParent(selectedProject);
			}
		}
	}

	public class renameDiagram extends Executor {
		public void execute(ActionEvent event) {
			    if (selRow != -1) {
		                tree.startEditingAtPath(path);
		        }
		}
	}
	
	public class MouseHandler extends MouseAdapter
	{
		int lastSelected;
		@Override
		public void mouseClicked(MouseEvent e)
		{
		
			
	        JTree tree = (JTree)e.getSource();
	        selRow = tree.getRowForLocation(e.getX(), e.getY());
	        path = tree.getPathForLocation(e.getX(), e.getY());
	        if(selRow == -1)
	        {
	            tree.clearSelection();
	            lastSelected = -1;
	        }
	        else if(selRow != lastSelected)
	        {
	            tree.setSelectionRow(selRow);
	            lastSelected = selRow;
	        }
	        
	        Object node = tree.getLastSelectedPathComponent();
	        setEnebled(true, false, false, false);
			
			if(node != null)
			if (node instanceof ProjectNode)
			{
				setEnebled(false, true, true, true);
				model.setSelectedProject(((ProjectNode)node).getUserObject().getProjectName());
				selectedProject = ((ProjectNode)node);
				selectedDiagram = null;
			}
			else if (node instanceof DiagramNode)
			{
				setEnebled(false, false, true, true);
				selectedDiagram = ((DiagramNode)node);
				selectedProject = null;
				if(e.getClickCount() == 2)
				{
					fileOpenExecutor fo = ((fileOpenExecutor)(MenuBarController.menuBarView.fileOpen.getWmvcExecutor()));
					fo.open(((DiagramNode)node).getUserObject().getName());
				}
			}
			else {
				selectedProject = null;
				selectedDiagram = null;
			}
	    
			if(SwingUtilities.isRightMouseButton(e))
			{
				showPopUp(e.getX(), e.getY(), true);
		    }
		
			
		}
			   	
	}
	
public static boolean deleteDirectory(File file) {
	
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	        	deleteDirectory(f);
	        }
	    }
	    return file.delete();
	
	}
	
	
public void setEnebled(boolean newP, boolean newI, boolean renameI, boolean deleteI)
{
	newProjectItem.setEnabled(newP);
	newItem.setEnabled(newI);
	renameItem.setEnabled(renameI);
	deleteItem.setEnabled(deleteI);
}
	
public void showPopUp(int x, int y, Boolean file)
{	
	popupMenu.show(tree, x, y);
}
	
}

