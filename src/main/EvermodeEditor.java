package main;

import view.ApplicationFrame;

public class EvermodeEditor
{
   public static void main(String[] args)
   {

	  ApplicationFrame diagramPanel = makeFrame();
      if(diagramPanel.showWorkspaceChooser())
      {
    	  diagramPanel.setVisible(true);
      }
   }
   
   public static ApplicationFrame makeFrame()
   {
      ApplicationFrame diagramPanel = new ApplicationFrame(EvermodeEditor.class);
      return diagramPanel;
   }
   
}