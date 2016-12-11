package controller;

import helpers.DOMParser;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;

import model.AppModel;
import view.WorkspaceLauncher;

public class WorkspaceLauncherController
{
	public static WorkspaceLauncher view;
	public static AppModel model;
	
	public WorkspaceLauncherController(WorkspaceLauncher view, AppModel application) {
		this.view = view;
		model = application;
	}
	
	public static class BrowseActionListener extends MouseAdapter {
		
		public void mouseReleased(MouseEvent event)  {
	
			JFileChooser workspaceChooser;
			if (new File(view.currentWorkspace).exists()) {
				File w = new File(view.currentWorkspace);
				workspaceChooser = new JFileChooser(w.getParent());
			}
			else {
				workspaceChooser = new JFileChooser(new File(System.getProperty("user.home"))); //$NON-NLS-1$
			}
			workspaceChooser.setDialogTitle("Choose Workspace"); 
			workspaceChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (workspaceChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				String workspace = workspaceChooser.getSelectedFile().getAbsolutePath();
				view.currentWorkspace = workspace;
				view.txtWorkspace.setSelectedItem(view.currentWorkspace);
			}
		}
	}
	
	public static class OkActionListener extends MouseAdapter  {
		public void mouseReleased(MouseEvent event) {
			view.setDialogResult(true);
			view.currentWorkspace = (String)view.txtWorkspace.getSelectedItem();
			model.setOpenedWorksace(view.currentWorkspace);
	
			
			boolean contains = false;
			for (String ws : model.getWorkspaceLocations()) {
	
				if (view.currentWorkspace.equals(ws)) {
					contains = true;
				}
			}
			if (contains) {
				DOMParser.fillWorkspace(model, new File("appProp.xml"),
						view.currentWorkspace);
				for (String diagram : model.getOpenedDiagrams()) {
					
					((MenuBarController.fileOpenExecutor) view.menuBar.fileOpen
							.getWmvcExecutor()).open(diagram);
					
				}
				if(model.getOpenedDiagram()!=null && new File(model.getOpenedDiagram()).exists())
				((MenuBarController.fileOpenExecutor) view.menuBar.fileOpen
						.getWmvcExecutor()).open(model.getOpenedDiagram());
			}
			view.setVisible(false);
		}
	}

	public static class CancelActionListener extends MouseAdapter  {
		public void mouseReleased(MouseEvent event) {
			view.setDialogResult(false);
			view.setVisible(false);
		}
	}

}