package model;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class ProjectNode extends DefaultMutableTreeNode {

    /**
     * @param resource
     */
    public ProjectNode(ProjectModel project) {
        super(project);
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject instanceof String) {
            setName((String) userObject);
        } else if (userObject instanceof ProjectModel) {
            super.setUserObject(userObject);
        }    
    }

    public void setName(String name) {
        if (getUserObject() != null) {
   
	        File oldFile = new File(getUserObject().getProjectName());
	    	String dir = oldFile.getParent();
	    	
	    	File newFile = new File(dir + "\\" + name);
	    	
	    	if (newFile.exists())
	    		  JOptionPane.showMessageDialog(null, "file exists");
	    	else 
	    	{
	    		oldFile.renameTo(newFile);
	    		getUserObject().setProjectName(dir + "\\" + name);
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
    public ProjectModel getUserObject() {
        return (ProjectModel) super.getUserObject();
    }
}
