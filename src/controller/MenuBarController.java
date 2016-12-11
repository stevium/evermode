package controller;

import helpers.CutCopyPaste;
import helpers.DOMParser;
import helpers.FileHelper;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import model.AppModel;
import model.Diagram;
import model.ProjectModel;
import model.abstracts.Edge;
import model.abstracts.Node;
import model.diagram.ClassDiagram;
import view.ApplicationFrame;
import view.EditorPanel;

public class MenuBarController {

	static AppModel application;
	static ProjectModel project;
	static Diagram diagram;
	static ApplicationFrame view;
	public static MenuBar menuBarView;

	public MenuBarController(AppModel application, ApplicationFrame view,
			MenuBar menuBarView) {
		this.application = application;
		this.view = view;
		this.menuBarView = menuBarView;
	}

	public class ViewMenuListener implements MenuListener {
		public void menuSelected(MenuEvent event) {
			EditorPanel diagramPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (diagramPanel == null)
				return;
			DiagramPanel panel = diagramPanel.getGraphPanel();
			menuBarView.hideGridItem.setState(panel.getHideGrid());
		}

		public void menuDeselected(MenuEvent event) {
		}

		public void menuCanceled(MenuEvent event) {
		}
	}

	public class newClassDiagramExecutor extends Executor {
		public void execute(ActionEvent event) {
			try {
				Diagram model = new ClassDiagram(application);
				ProjectModel proj = application.getSelectedProject();
				if(proj == null)
				{
					JOptionPane.showMessageDialog(null, "You must first create a project!", "No Project Selected", JOptionPane.WARNING_MESSAGE, null);
				}
				else
				{
					String name = JOptionPane.showInputDialog("Enter Diagram name\n");
					if(name != null){
					while(name.isEmpty())
						{
							name = JOptionPane.showInputDialog("You must enter a Diagram name!\n ");
						}
						if (!name.isEmpty()) {
						model.setName( proj.getProjectName() + "\\"
							+ name + ".xml");
					    }
						
						application.addDiagram(proj, model);
						EditorPanel diagramPanel = new EditorPanel(model);
						view.addGraphFrame(diagramPanel);
					}
				}
				new fileSaveExecutor().execute(event);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

	}

	public class helpShowAboutExecutor extends Executor {
		JOptionPane jpane = new JOptionPane();
		
		public void execute(ActionEvent event) {
			JOptionPane.showMessageDialog(
					null,
					view.editorResources.getString("dialog.about"),
					null,
					JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getClass().getResource(
							view.appResources.getString("app.icon"))));
		}
	}

	public class fileExitExecutor extends Executor {
		public void execute(ActionEvent event) {
			execute();
		}

		public void execute() {
			int modcount = 0;
			application.getOpenedDiagrams().clear();
			for (int i = 0; i < view.tabPane.getTabCount(); i++) {
				if (view.tabPane.getComponentAt(i) instanceof EditorPanel) {
					EditorPanel diagramPanel = (EditorPanel) view.tabPane
							.getComponentAt(i);
				
					application.addOpenDiagram(diagramPanel.model.getName());
					
					if (diagramPanel.model.isModified())
						modcount++;
				}
			}
			if (modcount > 0) {
				// pitamo se da li je OK da se zatvori
				int result = JOptionPane.showConfirmDialog(view.tabPane,
						MessageFormat.format(view.editorResources
								.getString("dialog.exit.ok"),
								new Object[] { new Integer(modcount) }), null,
						JOptionPane.YES_NO_OPTION);

				// ako nije OK prekida se zatvaranje
				if (result != JOptionPane.YES_OPTION)
					return;
			}
			
			view.savePreferences();
			System.exit(0);
		}
	}

	public class fileOpenExecutor extends Executor {
		public void execute(ActionEvent event) {
			try {
				FileHelper.Open open = view.fileHelper.open(null, null,
						view.extensionHelper);
				InputStream in = open.getInputStream();
				if (in != null) {
					open(open.getName());
				}
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(view.tabPane, exception);
			}
		}

		public void open(String FileName) {
			boolean opened = false;
			EditorPanel component;
			
			for (int i = 0; i < view.tabPane.getTabCount(); i++) {
				component = (EditorPanel) view.tabPane.getComponentAt(i);
					String fileName = component.model.getName();
					if (fileName.equals(FileName)) {
						opened = true;
						view.tabPane.setSelectedIndex(i);
					}
			}
			if (!opened) {
				Diagram diagram = null;
				for (ProjectModel project : application.getProjects()) {
					String parent = new File(FileName).getParent();
					String projectName = new File(project.getProjectName())
							.getAbsolutePath();
					if (projectName.equals(parent)) {
						diagram = project.getDiagram(FileName);
					}
				}
				if (diagram == null) {
					diagram = view.read(FileName);
					diagram.setName(FileName);
					diagram.graphHistory.clear();
					diagram.graphHistoryCursor = 0;
				}
				
				EditorPanel diagramPanel = new EditorPanel(diagram);
				application.setSelectedDiagram(diagram);
				view.addGraphFrame(diagramPanel);
				view.setTitle();
			}
		}
	}

	public class fileSaveExecutor extends Executor {

		public void execute(ActionEvent event) {
			EditorPanel diagramPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (diagramPanel == null)
				return;
			String fileName = diagramPanel.model.getName();
			if (fileName == null) {
				new fileSaveAsExecutor().execute(event);
				return;
			}
			try {
				view.saveFile(diagramPanel.getGraphPanel().model, fileName);
				diagramPanel.getGraphPanel().model.setModified(false);
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(view.tabPane, exception);
			}
		}

	}

	public class fileSaveAsExecutor extends Executor {
		public void execute(ActionEvent event) {
			EditorPanel diagramPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (diagramPanel == null)
				return;
			Diagram diagram = diagramPanel.getGraphPanel().model;
			try {
				FileHelper.Save save = view.fileHelper.save(
						(new File(diagramPanel.model.getName())).getParent(),
						(new File(diagramPanel.model.getName())).getName(),
						view.extensionHelper, null, view.defaultExtension);

				OutputStream out = save.getOutputStream();
				if (out != null) {
					try {
						view.saveFile(diagram, save.getName());
					} finally {
						out.close();
					}
					diagramPanel.model.setName(save.getName());
					view.setTitle();
					diagram.setModified(false);
				}
			} catch (IOException exception) {
				JOptionPane.showInternalMessageDialog(view.tabPane, exception);
			}
		}
	}

	public class newProjectExecutor extends Executor {
		public void execute(ActionEvent event) {
			String name = JOptionPane.showInputDialog("Enter a Project name\n");
			try {
				while(name.isEmpty())
				{
					name = JOptionPane.showInputDialog("You must enter a Project name!\n");
				}
				File project = new File(application.getOpenedWorkspace()
						+ "/" + name);
				project.mkdir();
				application.addNewProject(application.getOpenedWorkspace()
						+ "/" + name);
			} catch (Exception e1) {
			}
		}
	}
	

	public class editPropertiesExecutor extends Executor {
		public void execute(ActionEvent event) {
			final EditorPanel editorPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (editorPanel == null)
				return;
			DiagramPanel panel = editorPanel.getGraphPanel();
			panel.showPropertyEditor();
		}
	};

	public class editDeleteExecutor extends Executor {
		public void execute(ActionEvent event) {
			//project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			diagram.removeSelected();
			diagram.saveGraphIntoHistory();
		}
	};


	
	public class editSelectNextExecutor extends Executor {
		public void execute(ActionEvent event) {
		//	project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			diagram.selectNext(1);
		}
	}

	public class editSelectPreviousExecutor extends Executor {
		public void execute(ActionEvent event) {
			//project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			diagram.selectNext(-1);
		}
	}

	public class editCutExecutor extends Executor {
		public void execute(ActionEvent event) {
			//project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			menuBarView.pasteItem.setEnabled(true);
			CutCopyPaste.cut(diagram);
		}
	}
	
	public class editCopyExecutor extends Executor {
		public void execute(ActionEvent event) {
		//	project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			menuBarView.pasteItem.setEnabled(true);
			CutCopyPaste.copy(diagram);
		}
	}

	public class editPasteExecutor extends Executor {
		public void execute(ActionEvent event) {
			try{
		//	project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			CutCopyPaste.paste(diagram);
			}
			catch(Exception e){}
		}
	}
	
	public class editUndoExecutor extends Executor {
		public void execute(ActionEvent event) {
		//	project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			undo(diagram);
		}
	}
	
	public class editRedoExecutor extends Executor {
		public void execute(ActionEvent event) {
			//project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			redo(diagram);
		}
	}
	

	
	public class viewZoomOutExecutor extends Executor {
		public void execute(ActionEvent event) {
	//		project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			diagram.changeZoom(-1);
		}
	}

	public class viewZoomInExecutor extends Executor {
		public void execute(ActionEvent event) {
		//	project = application.getSelectedProject();
			diagram = application.getSelectedDiagram();
			diagram.changeZoom(+1);
		}
	}

	public class viewGrowDrawingAreaExecutor extends Executor {
		public void execute(ActionEvent event) {
			EditorPanel editorPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (editorPanel == null)
				return;

			Diagram g = editorPanel.getGraphPanel().model;
			Rectangle2D bounds = g.getBounds((Graphics2D) editorPanel
					.getGraphics());
			bounds.add(editorPanel.getGraphPanel().getBounds());
			g.setMinBounds(new Rectangle2D.Double(0, 0, Math.sqrt(2)
					* bounds.getWidth(), Math.sqrt(2) * bounds.getHeight()));
			editorPanel.getGraphPanel().revalidate();
			editorPanel.repaint();
		}
	}

	public class viewClipDrawingAreaExecutor extends Executor {
		public void execute(ActionEvent event) {
			EditorPanel editorPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (editorPanel == null)
				return;
			Diagram g = editorPanel.getGraphPanel().model;
			Rectangle2D bounds = g.getBounds((Graphics2D) editorPanel
					.getGraphics());
			g.setMinBounds(null);
			editorPanel.getGraphPanel().revalidate();
			editorPanel.repaint();
		}
	}

	public class viewSmallerGridExecutor extends Executor {
		public void execute(ActionEvent event) {
			EditorPanel editorPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (editorPanel == null)
				return;
			DiagramPanel panel = editorPanel.getGraphPanel();
			panel.changeGridSize(-1);
		}
	}

	public class viewLargerGridExecutor extends Executor {
		public void execute(ActionEvent event) {
			EditorPanel editorPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (editorPanel == null)
				return;
			DiagramPanel panel = editorPanel.getGraphPanel();
			panel.changeGridSize(1);
		}
	}

	public class viewHideGridExecutor extends Executor {
		public void execute(ActionEvent event) {
			EditorPanel editorPanel = (EditorPanel) view.tabPane
					.getSelectedComponent();
			if (editorPanel == null)
				return;
			DiagramPanel panel = editorPanel.getGraphPanel();
			JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
			panel.setHideGrid(menuItem.isSelected());
		}
	}

	public class lafMenuListener implements ActionListener {
		UIManager.LookAndFeelInfo info;

		public lafMenuListener(UIManager.LookAndFeelInfo info) {
			this.info = info;
		}

		public void actionPerformed(ActionEvent e) {
			String laf = info.getClassName();
			view.changeLookAndFeel(laf);
			application.setTheme(laf);
		}
	}

	public class windowNextExecutor extends Executor {
		public void execute(ActionEvent event) {
			if (view.tabPane.getSelectedIndex() == view.tabPane.getTabCount() - 1)
				view.tabPane.setSelectedIndex(0);
			else
				view.tabPane
						.setSelectedIndex(view.tabPane.getSelectedIndex() + 1);
		}
	}

	public class windowPreviousExecutor extends Executor {
		public void execute(ActionEvent event) {
			if (view.tabPane.getSelectedIndex() == 0)
				view.tabPane.setSelectedIndex(view.tabPane.getTabCount() - 1);
			else
				view.tabPane
						.setSelectedIndex(view.tabPane.getSelectedIndex() - 1);
		}
	}

	public class windowRestoreExecutor extends Executor {
		boolean restore = true;

		public void execute(ActionEvent event) {
			if (restore) {
				view.splitPane
						.setDividerLocation(view.splitPane.getLocation().x);
				restore = !restore;
			} else {
				view.splitPane.setDividerLocation(300);
				restore = !restore;
			}
		}
	}

	public class windowCloseExecutor extends Executor {
		public void execute(ActionEvent event) {
			int modcount = 0;
					EditorPanel diagramPanel = (EditorPanel) view.tabPane.getComponentAt(view.tabPane.getSelectedIndex());
					close(diagramPanel.model);
				}
		
		public void close(Diagram diagram)
		{
			if (diagram.isModified())
			{
				// Is it ok to close?
				int result = JOptionPane.showConfirmDialog(view.tabPane,
						new File(diagram.getName()).getName() + " is unsaved. \nDo you want to close it without saving?", null,
						JOptionPane.YES_NO_OPTION);

				// If no Cancel
				if (result != JOptionPane.YES_OPTION)
					return;
			}
			
			for(int i = 0; i < view.tabPane.getTabCount(); i++)
			{
				EditorPanel diagramPanel = (EditorPanel) view.tabPane.getComponentAt(i);
				if(diagramPanel.model.getName() == diagram.getName())
				{
					diagram.setModified(false);
					view.tabPane.remove(view.tabPane.getSelectedIndex());
					diagram.deleteObserver((Observer)diagramPanel.panel);
				}
			}
			
		}
		}
	


    /**
     * Vraća dijagram iz istorije sa pozicije na koju pokazuje kursor
     * 
     */
    private void restoreGraphFromHistory(Diagram model)
    {
    	ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(
    			((ByteArrayOutputStream)model.graphHistory
    					.get(model.graphHistoryCursor)).toByteArray());
        // clone the graph to avoid changes on old object referenced in hisory
		Diagram d = DOMParser.readDiagram(byteArrayOutputStream);
		model.setDiagram(d);
		model.setModified(true);
		
    }
    


    /**
     * Vraća prethodni dijagram sa lokacije iz istorije na koju pokazuje kursor
     */
    public void undo(Diagram model)
    {
        if (model.graphHistoryCursor > 0)
        {
        	model.graphHistoryCursor--;
        	application.notifyViews();

            restoreGraphFromHistory(model);
           
        }
    }

    /**
     * Vraća sljedeći diagarm sa lokacije iz istorije na koju pokazuje kursor
     */
    public void redo(Diagram model)
    {
        if (model.graphHistoryCursor < model.graphHistory.size() - 1)
        {
        	model.graphHistoryCursor++;
            restoreGraphFromHistory(model);
            application.notifyViews();
        }
    }


}