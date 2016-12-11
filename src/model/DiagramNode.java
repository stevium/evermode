package model;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class DiagramNode extends DefaultMutableTreeNode {

    /**
     * @param resource
     */
    public DiagramNode(Diagram diagram) {
        super(diagram);
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject instanceof String) {
            setName((String) userObject);
        } else if (userObject instanceof Diagram) {
            super.setUserObject(userObject);
        }    
    }

    public void setName(String name) {
        if (getUserObject() != null) {
        	
        	File oldFile = new File(getUserObject().getName());
        	String dir = oldFile.getParent();
        	
        	File newFile = new File(dir + "\\" + name);
        	
        	if (newFile.exists())
        		  JOptionPane.showMessageDialog(null, "file exists");
        	else 
        	{
        		oldFile.renameTo(newFile);
        		getUserObject().setName(dir + "\\" + name);
        	}
        }
    }

    public String getName() {
        if (getUserObject() != null) {
            return getUserObject().toString();
        }
        return null;
    }

    @Override
    public Diagram getUserObject() {
        return (Diagram) super.getUserObject();
    }
}
