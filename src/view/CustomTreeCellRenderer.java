package view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import model.Diagram;
import model.DiagramNode;
import model.ProjectModel;
import model.ProjectNode;
import model.diagram.ClassNode;
import model.diagram.InterfaceNode;
import model.diagram.NoteNode;
import model.diagram.PackageNode;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean isExpanded, boolean isLeaf, int row,
			boolean hasFocus) {
		JLabel labela = new JLabel();
		labela.setOpaque(true);
		labela.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		labela.setBackground(Color.white);


		if (isSelected) {
			labela.setBackground(new Color(205, 232, 255));
		}
		labela.setText(value.toString());
		if(value.toString() == "Projects")
		{
			labela.setIcon(new ImageIcon("image/general/brick.png"));
			if(isExpanded)
				labela.setIcon(new ImageIcon("image/project.png"));
		}
	
		if (value instanceof ProjectNode)
		{
			if (isExpanded) {
	
					labela.setIcon(new ImageIcon("image/project_opened.gif"));
			} else {
	
					labela.setIcon(new ImageIcon("image/project_closed.gif"));
			}
		}
		
		
		if (value instanceof DiagramNode) {
			labela.setIcon(new ImageIcon("image/general/diagram.png"));
			if(((DiagramNode)value).getUserObject().isModified())
				labela.setIcon(new ImageIcon("image/general/diagram-modified.png"));
	
		}
		if((
				(tree.getLastSelectedPathComponent() instanceof ProjectNode ) 
				|| (tree.getLastSelectedPathComponent() instanceof DiagramNode )
		  ) &&
		  (tree.getLastSelectedPathComponent() == value)
		  
		)
		{
			super.setClosedIcon(labela.getIcon());
			super.setOpenIcon(labela.getIcon());
			super.setLeafIcon(labela.getIcon());
		}
		/*else 
		{
			super.setClosedIcon(new ImageIcon("image/general/diagram.png"));
			super.setOpenIcon(new ImageIcon("image/general/diagram.png"));
		}*/
		
		if (value.toString().startsWith("Class Node"))
		{
			labela.setIcon(new ImageIcon("image/class/Class.png"));
		}
		else if (value.toString().startsWith("Interface Node"))
		{
			labela.setIcon(new ImageIcon("image/class/Interface.png"));
		}
		else if (value.toString().startsWith("Package Node"))
		{
			labela.setIcon(new ImageIcon("image/class/Package.png"));
		}
		else if (value.toString().startsWith("Note Node"))
		{
			labela.setIcon(new ImageIcon("image/class/Note.png"));
		}

		return labela;
	}

}
