package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import controller.MenuBarController;
import model.AppModel;
import model.Diagram;
import model.DiagramNode;
import model.ProjectModel;
import model.ProjectNode;

public class ProjectExplorer extends JTabbedPane implements Observer {
	AppModel model;
	MenuBarController mbc;
	private JTree tree;
	DefaultMutableTreeNode root = null;
	DefaultTreeModel treeModel;
	ProjectExplorerController controller;
	
	public ProjectExplorer(AppModel model) {
		this.model = model;
		this.model.addObserver(this);
		initialize();
	}

	private void initialize() {
		JPanel container = new JPanel();
		
		
		
		root = new DefaultMutableTreeNode("Projects");
		treeModel = new DefaultTreeModel(root);
		
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	//	CustomTreeModel treeModel = new CustomTreeModel(model);
		tree.setCellRenderer(new CustomTreeCellRenderer());
	//	tree.putClientProperty("JTree.lineStyle", "None");
	
		
		controller = new ProjectExplorerController(tree, model);
		
		container.setLayout(new BorderLayout());
		container.add(tree, BorderLayout.CENTER);
		container.setBorder(BorderFactory.createMatteBorder(5, 10, 10, 10, Color.white));
		JScrollPane jsb = new JScrollPane(container);
		jsb.getVerticalScrollBar().setUnitIncrement(15);
		this.add(jsb);
	}

	public void addModel(AppModel model) {
		this.model = model;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
	//	root = new AppNode(model);
		for (ProjectModel project : model.getProjects()) {
			ProjectNode pNode= (ProjectNode) find(root, project.toString());
			if(pNode == null)
			{
				pNode = new ProjectNode(project);
				treeModel.insertNodeInto(pNode, root, root.getChildCount());
			}
			for (Diagram diagram : project.getDiagrams()) {
				DiagramNode dNode= (DiagramNode) find(pNode, diagram.toString());
				if(dNode == null)
				{
					dNode = new DiagramNode(diagram);
					treeModel.insertNodeInto(dNode, pNode, pNode.getChildCount());
				}
				
				for (int i = 0; i < diagram.getNodes().size(); i++) {
					DefaultMutableTreeNode nNode = (DefaultMutableTreeNode) find(dNode, diagram.getNodes().get(i).toString() + " " + diagram.getNodes().get(i).hashCode());
					if(nNode == null)
					{
						nNode = new DefaultMutableTreeNode(diagram.getNodes().get(i).toString() + " " + diagram.getNodes().get(i).hashCode());
						treeModel.insertNodeInto(nNode, dNode, dNode.getChildCount());
					}
				
				}
			}
		}
		
		tree.expandRow(0);
		//treeModel.reload();
		
		tree.updateUI();
		//((DefaultTreeModel) tree.getModel()).reload(root);
		this.setTitleAt(this.getSelectedIndex(), model.toString());
	}
	
	
	private DefaultMutableTreeNode find(DefaultMutableTreeNode root, String s) {
	    @SuppressWarnings("unchecked")
	    Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	        DefaultMutableTreeNode node = e.nextElement();
	        if (node.toString().equalsIgnoreCase(s)) {
	            return node;
	        }
	    }
	    return null;
	}
	
}
