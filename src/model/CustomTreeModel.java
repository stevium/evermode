package model;


import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import model.abstracts.AbstractNode;
import model.abstracts.Node;

public class CustomTreeModel extends DefaultTreeModel{
	public CustomTreeModel(AppModel root)
	{
		super(root);
	}
	

	public Object getChild(Object parent, int index) {
		if(parent != null)
			
		if(parent instanceof Node) {
			return null;
		} else if (parent instanceof AppModel) {
			return ((AppModel)parent).getProjects().get(index);
		} else if (parent instanceof ProjectModel) {
			return ((ProjectModel)parent).getDiagrams().get(index);
		}else if (parent instanceof Diagram) {
			return ((Diagram)parent).getNodes().get(index);
		}
		return getRoot(); 
	}

	public int getChildCount(Object parent) {
		if (parent instanceof ProjectModel) {
			return ((ProjectModel)parent).getDiagrams().size();
		} else if (parent instanceof AppModel) {
			return ((AppModel)parent).getProjects().size();
		} else if (parent instanceof Diagram) {
			return ((Diagram)parent).getNodes().size();
		}
		return 0;
	}

	public boolean isLeaf(Object node) {
		return (node instanceof Node);
	}

	public int getIndexOfChild(Object parent, Object child) {
		
		if(parent instanceof Diagram) {
			return 0;
		} else if (parent instanceof AppModel) {
			if(child instanceof ProjectModel)
				return ((AppModel)parent).getProjects().indexOf((ProjectModel)child);
		} else if (parent instanceof ProjectModel) {
			if(child instanceof Diagram)
				return ((ProjectModel)parent).getDiagrams().indexOf((Diagram)child);
		}
		else if(parent instanceof Diagram){
			if(child instanceof Node)
				return ((Diagram)parent).getNodes().indexOf(((Node)child));
		}
	
		return -1;
		
	}


	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}

}
