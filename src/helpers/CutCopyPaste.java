package helpers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.Diagram;
import model.abstracts.Edge;
import model.abstracts.Node;
import model.diagram.ClassDiagram;

public class CutCopyPaste {
	/**
     * Vrši isjecanje Cut-ovanje selektovanih elemenata
     */
    public static void cut(Diagram model)
    {
        copy(model);
        model.removeSelected();
        model.saveGraphIntoHistory();
    }

	
	/**
	 * Kopira selektovane elemente dijagrama u system cliboard
     */
    public static void copy(Diagram model)
    {
        ClassDiagram newDiagram = new ClassDiagram();
        List<Node> selectedNodes = new ArrayList<Node>();
    	List<Edge> selectedEdges = new ArrayList<Edge>();
    	Iterator iter = model.selectedItems.iterator();
		while (iter.hasNext()) {
			Object selected = iter.next();
			if (selected instanceof Node) {
				selectedNodes.add((Node) selected);
			} else if (selected instanceof Edge) {
				selectedEdges.add((Edge) selected);
			}
		}
		
        // Koristimo mapper za Id-ove jer se mijenjaju svkai put kada se čvor doda u newDiagram
		Map<Integer, Integer> idMapper = new HashMap<Integer, Integer>();
        for (Node aSelectedNode : selectedNodes)
        {
        	if (isAncestorInCollection(aSelectedNode, selectedNodes)) {
        		// U ovom slučaju, id se ne mijenja. Samo se mijenja za newDiagram.addNode()
        		Integer currentId = aSelectedNode.hashCode();
				idMapper.put(currentId, currentId);
        		continue;
        	}
        	Node clone = (Node)aSelectedNode.clone();
            int oldId = aSelectedNode.hashCode();
            Point2D locationOnGraph = new Point2D.Double(aSelectedNode.getBounds().getX(), aSelectedNode.getBounds().getY());
            newDiagram.addNode(clone, locationOnGraph);
            int newId = clone.hashCode();
            idMapper.put(oldId, newId);
        }
        for (Edge aSelectedEdge : selectedEdges)
        {
            Edge clone = (Edge)aSelectedEdge.clone();
            int oldStartId = clone.getStart().hashCode();
            int oldEndId = clone.getEnd().hashCode();
            int newStartId = idMapper.get(oldStartId);
            int newEndId = idMapper.get(oldEndId);
			Node startNode = newDiagram.findNode(newStartId);
			Node endNode = newDiagram.findNode(newEndId);
            if (startNode != null && endNode != null)
            {
                newDiagram.connect(clone, startNode, endNode);
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DOMParser.writeDiagram(newDiagram, byteArrayOutputStream);
        byteArrayOutputStream.toString();
        String xmlContent = byteArrayOutputStream.toString();
        pushContentToSystemClipboard(xmlContent);
    }
    
    /**
     * Radi sa system Cliboard-om
     * 
     * @return
     */
    private static String getContentFromSystemClipboard()
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipData = clipboard.getContents(clipboard);
        if (clipData != null)
        {
            try
            {
                if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor))
                {
                    String s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
                    return s;
                }
            }
            catch (UnsupportedFlavorException ufe)
            {
                return null;
            }
            catch (IOException ioe)
            {
                return null;
            }
        }
        return null;
    }
    
    /**
     * Pomjera sve čvorovoe dijagrama na odgovarajuću lokaciju
     * 
     * @param graph
     * @param mouseLocation
     * @return the modified graph
     */
    private static Diagram translateToMouseLocation(Diagram diagram, Point2D mouseLocation)
    {
        Rectangle2D clipBounds = diagram.getClipBounds();
        double dx = mouseLocation.getX() - clipBounds.getX();
        double dy = mouseLocation.getY() - clipBounds.getY();
        Collection<Node> nodes = diagram.getNodes();
        for (Node aNode : nodes)
        {
        	
            boolean hasParent = (aNode.getParent() != null);
            if (!hasParent)
            {
                aNode.translate(dx, dy); 
            }
        }
        return diagram;
    }
    
    
    
    /**
     * Vrši paste elementa sa Clipboarda na dijagram
     */
    public static void paste(Diagram model)
    {
     //   IGraph graph = this.editorPart.getGraph();
        String xmlContent = getContentFromSystemClipboard();
        if (xmlContent == null)
        {
            return; // If no content, we stop here
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlContent.getBytes());
        Diagram diagram = DOMParser.readDiagram(byteArrayInputStream);
        diagram = translateToMouseLocation(diagram, model.mouseDownPoint);

        Collection<Node> nodesFromClipboard = diagram.getNodes();
        for (Node aNode : nodesFromClipboard)
        {
            if (isAncestorInCollection(aNode, nodesFromClipboard)) continue;
            model.addNode(aNode, new Point2D.Double(aNode.getBounds().getX(), aNode.getBounds().getY()));
            model.saveGraphIntoHistory();
        }

        Collection<Edge> edgesFromClipboard = diagram.getEdges();
        for (Edge anEdge : edgesFromClipboard)
        {
            Node startNode = model.findNode(anEdge.getStart().hashCode());
            Node endNode = model.findNode(anEdge.getEnd().hashCode());
            if (startNode != null && endNode != null)
            {
            	model.connect(anEdge, startNode, endNode);
            }
        }

    	model.setModified(true);
    }
    
    
    
    
    /**
     * Radi sa system Clipboard-om
     * 
     * @param content
     */
    private static void pushContentToSystemClipboard(String content)
    {
        StringSelection dataToClip = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(dataToClip, dataToClip);
    }
    
    /**
     * Provjerava da li data lista sadrži roditelja datog childNode-a
     * 
     * @param childNode
     * @param ancestorList
     * @return b
     */
    private static boolean isAncestorInCollection(Node childNode, Collection<Node> ancestorList)
    {
        for (Node anAncestorNode : ancestorList)
        {
            boolean ancestorRelationship = isAncestorRelationship(childNode, anAncestorNode);
            if (ancestorRelationship) return true;
        }
        return false;
    }

    /**
     * Provjerava da li je ancestorNode roditeljski čvor od childNode
     * 
     * @param childNode
     * @param ancestorNode
     * @return b
     */
    private static boolean isAncestorRelationship(Node childNode, Node ancestorNode)
    {
        Node parent = childNode.getParent();
        if (parent == null)
        {
            return false;
        }
        List<Node> fifo = new ArrayList<Node>();
        fifo.add(parent);
        while (!fifo.isEmpty())
        {
            Node aParentNode = fifo.get(0);
            fifo.remove(0);
            if (aParentNode.equals(ancestorNode))
            {
                return true;
            }
            Node aGranParent = aParentNode.getParent();
            if (aGranParent != null)
            {
                fifo.add(aGranParent);
            }
        }
        return false;
    }
	
	
}
