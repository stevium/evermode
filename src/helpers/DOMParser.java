package helpers;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.AppModel;
import model.ProjectModel;
import model.LineArrow;
import model.LineType;
import model.Diagram;
import model.LineStyle;
import model.PointNode;
import model.abstracts.*;
import model.diagram.*;
import view.property.TextArea;

public class DOMParser {
	private static Diagram diagram; // Graph to get and set
	private static HashMap<Integer, AbstractNode> nodeMap; // map for diagram
	private static AppModel application;							// nodes
	private static List<Edge> edgeList; // list of diagram edges
	
	public static void addModel(AppModel app)
	{
		application = app;
	}
	
	public static void fillWorkspace(AppModel model, File xmlFile,
			String wsLocation) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList windowFields = doc.getDocumentElement().getChildNodes();

			Element currElement = null;

			// Parsing workspace
			for (int j = 0; j < windowFields.getLength(); j++) {
				if (windowFields.item(j).getNodeType() == Node.ELEMENT_NODE) {
					currElement = (Element) windowFields.item(j);
					switch (windowFields.item(j).getNodeName()) {
					case "workspace": {
						model.setOpenedWorksace(wsLocation);

						NodeList workspaceFields = currElement.getChildNodes();

						for (int i = 0; i < workspaceFields.getLength(); i++) {
							if (workspaceFields.item(i).getNodeType() == Node.ELEMENT_NODE)
							{
								currElement = (Element) workspaceFields.item(i);

								switch (currElement.getNodeName()) 
								{
								case "selectedDiagram": {
									String name = currElement.getAttribute("name");
									if(new File(name).exists())
									model.setOpenedDiagram(name);
									break;
								}
								case "selectedProject": {
									String name = currElement.getAttribute("name");
									if(new File(name).exists())
									model.setSelectedProject(name);
									break;
								}
								case "openedDiagrams": {
									NodeList openDiagrams = currElement.getChildNodes();
									// Parsing Diagrams
									for (int k = 0; k < openDiagrams.getLength(); k++) {
										if (openDiagrams.item(k).getNodeType() == Node.ELEMENT_NODE) {
											currElement = (Element) openDiagrams.item(k);
											String diagramName = (currElement.getAttribute("name"));
											if(new File(diagramName).exists())
												model.addOpenDiagram(diagramName);
										}
									}
									break;
								}
								case "project": {
									String name = currElement.getAttribute("name");
									if(new File(name).exists())
									{
										ProjectModel proj = model.addNewProject(currElement.getAttribute("name"));
								
									NodeList diagramFields = currElement.getChildNodes();

									// Parsing Diagrams
									for (int k = 0; k < diagramFields.getLength(); k++) {
										if (diagramFields.item(k).getNodeType() == Node.ELEMENT_NODE) {
											currElement = (Element) diagramFields.item(k);
											String diagramName = (currElement.getAttribute("name"));
											if(new File(diagramName).exists())
											{Diagram diagram = readDiagram(new File(diagramName));
											diagram.setName(diagramName);
											proj.addDiagram(diagram);
											}
										}
									}
									}
									break;
								}
								}
							}
						}
					}
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void writeInitalSettings(AppModel model) {
		File xmlFile = new File("appProp.xml");	
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// korijeni element
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("window");
			doc.appendChild(rootElement);
			
			Element dimensionElement = doc.createElement("dimension");
			Element positionElement = doc.createElement("position");
			Element localizationElement = doc.createElement("localization");
			Element themeElement = doc.createElement("theme");
			Element openedWorkspaceElement = doc.createElement("openedWorkspace");
			Element workspaceElement = doc.createElement("workspace");
			
			rootElement.appendChild(dimensionElement);
			rootElement.appendChild(positionElement);
			rootElement.appendChild(localizationElement);
			rootElement.appendChild(themeElement);
			rootElement.appendChild(openedWorkspaceElement);
			rootElement.appendChild(workspaceElement);
			
			dimensionElement.setAttribute("w", Double.toString(model.getWindowDimension().getWidth()));
			dimensionElement.setAttribute("h", Double.toString(model.getWindowDimension().getHeight()));
			positionElement.setAttribute("x", Double.toString(model.getWindowPosition().getX()));
			positionElement.setAttribute("y", Double.toString(model.getWindowPosition().getY()));
			localizationElement.setAttribute("value", model.getLocalizatoin());
			themeElement.setAttribute("value", model.getTheme());
			openedWorkspaceElement.setAttribute("loc", model.getOpenedWorkspace());
			
			workspaceElement.setAttribute("loc", model.getOpenedWorkspace());
			
			Element selectedDiagramElement = doc.createElement("selectedDiagram");
			Element selectedProjectElement = doc.createElement("selectedProject");
			Element openedDiagramsElement = doc.createElement("openedDiagrams");
			
			workspaceElement.appendChild(selectedDiagramElement);
			workspaceElement.appendChild(selectedProjectElement);
			workspaceElement.appendChild(openedDiagramsElement);
			
			
			if(model.getSelectedDiagram() != null)
			selectedDiagramElement.setAttribute("name", model.getSelectedDiagram().getName());
			if(model.getSelectedProject()!=null)
			selectedProjectElement.setAttribute("name", model.getSelectedProject().getProjectName());

			for(String diagram : model.getOpenedDiagrams())
			{
				
				Element diagramElement = doc.createElement("diagram");
				diagramElement.setAttribute("name", diagram);
				openedDiagramsElement.appendChild(diagramElement);

			}

			for (ProjectModel project : model.getProjects()) {
				Element projectElement = doc.createElement("project");
				projectElement.setAttribute("name", project.getProjectName());
				
				for (Diagram diagram : project.getDiagrams()) {
					Element diagramElement = doc.createElement("diagram");
					diagramElement.setAttribute("name", diagram.getName());
					projectElement.appendChild(diagramElement);
				}

				workspaceElement.appendChild(projectElement);
			}
			
		
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			// System.out.println(doc.getTextContent());
			

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);

			System.out.println("File saved!");
			
			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			} 
	
	}

	
	
	public static AppModel readInitalSettings(File xmlFile) {
		AppModel model = new AppModel();
		if (xmlFile.exists())
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(xmlFile);
				doc.getDocumentElement().normalize();

				NodeList windowFields = doc.getDocumentElement()
						.getChildNodes();

				Element currElement = null;

				// Parsing window
				for (int j = 0; j < windowFields.getLength(); j++) {
					if (windowFields.item(j).getNodeType() == Node.ELEMENT_NODE) {
						currElement = (Element) windowFields.item(j);
						switch (windowFields.item(j).getNodeName()) {
						case "dimension": {
							model.setWindowDimension(Double
									.parseDouble(currElement.getAttribute("w")),
									Double.parseDouble(currElement
											.getAttribute("h")));
							break;
						}
						case "position": {
							model.setWindowPosition(Double
									.parseDouble(currElement.getAttribute("x")),
									Double.parseDouble(currElement
											.getAttribute("y")));
							break;
						}

						case "localization": {
							model.setLocalization(currElement
									.getAttribute("value"));
							break;
						}
						case "theme": {
							model.setTheme(currElement.getAttribute("value"));
							break;
						}
						case "openedWorkspace": {
							model.setOpenedWorksace(currElement
									.getAttribute("loc"));
							break;
						}
						case "workspace": {
							model.addWorkspaceLocation(currElement
									.getAttribute("loc"));
							break;
						}
						}
					}
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		return model;
	}

	public static Diagram readDiagram(ByteArrayInputStream inputStream) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputStream);
			doc.getDocumentElement().normalize();
			return parseFromDocument(doc);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Diagram readDiagram(File inputFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			return parseFromDocument(doc);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Diagram parseFromDocument(Document doc)
	{
		edgeList = new ArrayList<Edge>();
		nodeMap = new LinkedHashMap<Integer, AbstractNode>();
		diagram = new ClassDiagram(application);

		NodeList nodesAndEdges = doc.getDocumentElement().getChildNodes();
		
		for (int j = 0; j < nodesAndEdges.getLength(); j++) {
			if (nodesAndEdges.item(j).getNodeType() == Node.ELEMENT_NODE)
				if (nodesAndEdges.item(j).getNodeName()
						.equalsIgnoreCase("nodes")) {
					NodeList nodes = nodesAndEdges.item(j).getChildNodes();
					for (int i = 0; i < nodes.getLength(); i++)
						if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE)
							parseNode(nodes.item(i));
				} else if (nodesAndEdges.item(j).getNodeName()
						.equalsIgnoreCase("edges")) {
					NodeList edges = nodesAndEdges.item(j).getChildNodes();
					for (int i = 0; i < edges.getLength(); i++)
						if (edges.item(i).getNodeType() == Node.ELEMENT_NODE)
							parseEdge(edges.item(i));
				}
		}
		
		for (AbstractNode node : nodeMap.values()) {
			diagram.add(node, new Point2D.Double(node.getBounds().getX(),
					node.getBounds().getY()));
		}
		for (Edge edge : edgeList) {
			diagram.connect(edge, edge.getStart(), edge.getEnd());
		}
		
		edgeList = null;
		nodeMap = null;
		return diagram;
	}
	
	public static void writeDiagram(Diagram diagram, ByteArrayOutputStream outputFile){
		// write the content into xml file
		StreamResult result = new StreamResult(outputFile);
		parseFromDiagram(result, diagram);
	}
	
	public static void writeDiagram(Diagram diagram, File outputFile) {
		// write the content into xml file
		StreamResult result = new StreamResult(outputFile);
		parseFromDiagram(result, diagram);
	}
	
	private static void parseFromDiagram(StreamResult result, Diagram diagram){
		try {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// korijeni element
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("ClassDiagramGraph");
		doc.appendChild(rootElement);
		Element nodesElement = doc.createElement("nodes");
		Element edgesElement = doc.createElement("edges");
		rootElement.appendChild(nodesElement);
		rootElement.appendChild(edgesElement);
		for (Object node : diagram.getNodes()) {
			if (getNodeElement((AbstractNode) node, doc, 1) != null)
				nodesElement.appendChild(getNodeElement(
						(AbstractNode) node, doc, 1));
		}
		for (Object edge : diagram.getEdges()) {
			edgesElement.appendChild(getEdgeElement((Edge) edge, doc));
		}

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		// System.out.println(doc.getTextContent());
		

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(source, result);

		System.out.println("File saved!");
		
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		
	}


	// HELPER METHODS
	private static List<AbstractNode> parseChildNode(Node childNode) {
		List<AbstractNode> childrens = new ArrayList<AbstractNode>();
		NodeList childNodeChilds = childNode.getChildNodes();

		for (int i = 0; i < childNodeChilds.getLength(); i++) {
			if (childNodeChilds.item(i).getNodeType() == Node.ELEMENT_NODE) {
				AbstractNode node = parseNode(childNodeChilds.item(i));
				childrens.add(node);
			}
		}
		return childrens;
	}

	private static AbstractNode parseNode(Node nodeNode) {
		int nodeId = Integer.parseInt(((Element) nodeNode).getAttribute("id"));
		AbstractNode node = null;

		NodeList nodeFields = nodeNode.getChildNodes();
		List<AbstractNode> childNodes = null;
		Rectangle2D bounds = null;
		Color noteColor = null;
		TextArea name = null;
		TextArea attributes = null;
		TextArea methods = null;
		TextArea content = null;
		TextArea text = null;

		for (int i = 0; i < nodeFields.getLength(); i++) {
			if (nodeFields.item(i).getNodeType() == Node.ELEMENT_NODE)
				switch (nodeFields.item(i).getNodeName()) {
				case "children": {
					childNodes = parseChildNode(nodeFields.item(i));
					break;
				}
				case "bounds": {
					bounds = DOMHelpers.parseBounds(nodeFields.item(i));
					break;
				}
				case "color": {
					noteColor = DOMHelpers.parseRGB(nodeFields.item(i));
					break;
				}
				case "name": {
					name = DOMHelpers.parseMLS(nodeFields.item(i));
					break;
				}
				case "attributes": {
					attributes = DOMHelpers.parseMLS(nodeFields.item(i));
					break;
				}
				case "methods": {
					methods = DOMHelpers.parseMLS(nodeFields.item(i));
					break;
				}
				case "content": {
					content = DOMHelpers.parseMLS(nodeFields.item(i));
					break;
				}
				case "text": {
					text = DOMHelpers.parseMLS(nodeFields.item(i));
					break;
				}
				default:
					break;
				}

		}

		switch (nodeNode.getNodeName()) {
		case ("ClassNode"): {
			node = (ClassNode)new ClassNode().clone();
			((ClassNode) node).setName(name);
			((ClassNode) node).setAttributes(attributes);
			((ClassNode) node).setMethods(methods);
			break;
		}
		case ("InterfaceNode"): {
			node = new InterfaceNode();
			((InterfaceNode) node).setName(name);
			((InterfaceNode) node).setMethods(methods);
			break;
		}
		case ("PackageNode"): {
			node = new PackageNode();
			((PackageNode) node).setContents(content);
			break;
		}
		case ("NoteNode"): {
			node = new NoteNode();
			((NoteNode) node).setText(text);
			break;
		}
		case ("PointNode"): {
			node = new PointNode();
			break;
		}
		default:
			break;
		}

		DOMHelpers.initNode(node, childNodes, bounds);
		nodeMap.put(nodeId, node);
		return node;
	}

	private static AbstractNode parseEdgeNode(Node edgeNode) {
		Element edgeNodeElement = (Element) edgeNode;
		int reference = Integer.parseInt(edgeNodeElement
				.getAttribute("reference"));
		return nodeMap.get(reference);
	}

	private static Edge parseEdge(Node nodeEdge) {
		Edge edge = null;
		NodeList edgeFields = nodeEdge.getChildNodes();

		AbstractNode start = null;
		AbstractNode end = null;
		LineType bs = null;
		String startLabel = null;
		String middleLabel = null;
		String endLabel = null;
		LineStyle lineStyle = null;
		LineArrow lineArrow = null;

		for (int i = 0; i < edgeFields.getLength(); i++) {
			if (edgeFields.item(i).getNodeType() == Node.ELEMENT_NODE)
				switch (edgeFields.item(i).getNodeName()) {
				case "bentStyle": {
					bs = DOMHelpers.parseBendStyle(edgeFields.item(i));
					break;
				}
				case "startLabel": {
					startLabel = edgeFields.item(i).getTextContent();
					break;
				}
				case "middleLabel": {
					middleLabel = edgeFields.item(i).getTextContent();
					break;
				}
				case "endLabel": {
					endLabel = edgeFields.item(i).getTextContent();
					break;
				}
				case "start": {
					start = parseEdgeNode(edgeFields.item(i));
					break;
				}
				case "end": {
					end = parseEdgeNode(edgeFields.item(i));
					break;
				}
				case "lineStyle": {
					lineStyle = DOMHelpers.parseEdgeLineStyle(edgeFields
							.item(i));
					break;
				}
				case "endArrowHead": {
					lineArrow = DOMHelpers.parseArrowHead(edgeFields.item(i));
					break;
				}
				default:
					break;
				}
		}

		switch (nodeEdge.getNodeName()) {
		case "ClassRelationshipEdge": {
			edge = new ClassRelationshipEdge();
			((ClassRelationshipEdge) edge).setBentStyle(bs);
			((ClassRelationshipEdge) edge).setStartLabel(startLabel);
			((ClassRelationshipEdge) edge).setEndLabel(endLabel);
			((ClassRelationshipEdge) edge).setMiddleLabel(middleLabel);
			((ClassRelationshipEdge) edge).setLineStyle(lineStyle);
			((ClassRelationshipEdge) edge).setEndArrowHead(lineArrow);
			break;
		}
		case "NoteEdge":
			edge = new NoteEdge();
			break;
		default:
			break;
		}
		edge.connect(start, end);
		edgeList.add(edge);
		return edge;
	}

	private static Element getNodeChildrenElement(AbstractNode node,
			Document doc) {
		Element nodeChildren = doc.createElement("children");
		for (Object aNode : node.getChildren()) {
			Element ChildrenElement = getNodeElement((AbstractNode) aNode, doc,
					node.hashCode());
			if (ChildrenElement != null)
				nodeChildren.appendChild(ChildrenElement);
		}
		return nodeChildren;
	}

	private static Element getNodeElement(AbstractNode node, Document doc,
			int callerId) {
		Element nodeElement = doc
				.createElement(node.getClass().getSimpleName());
		nodeElement.setAttribute("id", Integer.toString(node.hashCode()));

		Element nodeChildren = getNodeChildrenElement(node, doc);
		Element nodeParent = DOMHelpers.getNodeParentElement(node, doc);
		Element nodeBounds = DOMHelpers.getNodeBoundsElement(node, doc);

		nodeElement.appendChild(nodeChildren);
		if (nodeParent != null)
			nodeElement.appendChild(nodeParent);
		nodeElement.appendChild(nodeBounds);

		// class node
		switch (node.getClass().getSimpleName()) {
		case "ClassNode": {
			Element nodeName = DOMHelpers.getNodeNameElement(node, doc);
			Element nodeAttributes = DOMHelpers.getNodeAttributesElement(node,
					doc);
			Element nodeMethods = DOMHelpers.getNodeMethodsElement(node, doc);
			nodeElement.appendChild(nodeName);
			nodeElement.appendChild(nodeAttributes);
			nodeElement.appendChild(nodeMethods);
			break;
		}
		// interface node
		case "InterfaceNode": {
			Element nodeName = DOMHelpers.getNodeNameElement(node, doc);
			Element nodeMethods = DOMHelpers.getNodeMethodsElement(node, doc);
			nodeElement.appendChild(nodeName);
			nodeElement.appendChild(nodeMethods);
			break;
		}
		// package node
		case "PackageNode": {
			Element nodeContent = DOMHelpers.getNodeContentElement(node, doc);
			nodeElement.appendChild(nodeContent);
			break;
		}
		// note node
		case "NoteNode": {
			Element nodeText = DOMHelpers.getNodeTextElement(node, doc);
			Element nodeColor = DOMHelpers.getColorElement((NoteNode) node,
					doc);
			nodeElement.appendChild(nodeText);
			nodeElement.appendChild(nodeColor);
			break;
		}
		default:
			break;
		}

		if (node.getParent() != null && callerId != node.getParent().hashCode())
			return null;
		return nodeElement;
	}

	private static Element getEdgeElement(Edge edge, Document doc) {
		Element edgeElement = doc
				.createElement(edge.getClass().getSimpleName());

		if (edge instanceof ClassRelationshipEdge) {
			Element edgeBentStyle = DOMHelpers.getEdgeBentStyleElement(edge,
					doc);
			Element edgeArrowHead = DOMHelpers.getEdgeArrowHead(
					(SegmentedEdge) edge, doc);
			Element edgeLineStyle = DOMHelpers.getLineStyleElement(
					(ClassRelationshipEdge) edge, doc);
			Element edgeStartLabel = DOMHelpers.getEdgeStartLabelElement(edge,
					doc);
			Element edgeMiddleLabel = DOMHelpers.getEdgeMiddleLabelElement(
					edge, doc);
			Element edgeEndLabel = DOMHelpers
					.getEdgeEndLabelElement(edge, doc);

			edgeElement.appendChild(edgeBentStyle);
			edgeElement.appendChild(edgeArrowHead);
			edgeElement.appendChild(edgeLineStyle);
			edgeElement.appendChild(edgeStartLabel);
			edgeElement.appendChild(edgeMiddleLabel);
			edgeElement.appendChild(edgeEndLabel);
		}

		Element edgeStart = DOMHelpers.getEdgeStartElement(edge, doc);
		Element edgeEnd = DOMHelpers.getEdgeEndElement(edge, doc);

		edgeElement.appendChild(edgeStart);
		edgeElement.appendChild(edgeEnd);
		return edgeElement;
	}
}
