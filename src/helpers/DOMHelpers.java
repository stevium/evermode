package helpers;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.LineArrow;
import model.LineType;
import model.LineStyle;
import model.PointNode;
import model.abstracts.*;
import model.diagram.*;
import view.property.TextArea;


public class DOMHelpers {
	public static Element getEdgeEndLabelElement(Edge edge, Document doc) {
		Element endLabel = doc.createElement("endLabel");
		endLabel.setTextContent(((SegmentedEdge)edge).getEndLabel());
		return endLabel;
	}
	public static Element getEdgeMiddleLabelElement(Edge edge, Document doc) {
		Element middleLabel = doc.createElement("middleLabel");
		middleLabel.setTextContent(((SegmentedEdge)edge).getMiddleLabel());
		return middleLabel;
	}
	public static Element getEdgeStartLabelElement(Edge edge, Document doc) {
		Element startLabel = doc.createElement("startLabel");
		startLabel.setTextContent(((SegmentedEdge)edge).getStartLabel());
		return startLabel;
	}
	public static Element getEdgeBentStyleElement(Edge edge, Document doc) {
		Element bentStyle = doc.createElement("bentStyle");
		ClassRelationshipEdge segEdge = (ClassRelationshipEdge) edge;
		String name = null;
		if ((LineType.HV).equals(segEdge.getBentStyle())) { name = "HV"; }
		else if ((LineType.HVH).equals(segEdge.getBentStyle())) { name = "HVH"; }
		else if ((LineType.STRAIGHT).equals(segEdge.getBentStyle())) { name = "STRAIGHT"; }
		else if ((LineType.VH).equals(segEdge.getBentStyle())) { name = "VH"; }
		else if ((LineType.VHV).equals(segEdge.getBentStyle())) { name = "VHV"; }
		else return null;
		bentStyle.setAttribute("name", name);
		return bentStyle;
	}
	
	public static Element getPointElement(Point2D point, Document doc, String name)
	{
		Element pointElement = doc.createElement(name);
		pointElement.setAttribute("x", Double.toString(point.getX()));
		pointElement.setAttribute("y", Double.toString(point.getY()));
		return pointElement;
	}

	public static Element getEdgeEndElement(Edge edge, Document doc) {
		Element edgeEndElement = doc.createElement("end");
		edgeEndElement.setAttribute("reference", Integer.toString(edge.getEnd().hashCode()));
		return edgeEndElement;
	}
	public static Element getEdgeStartElement(Edge edge, Document doc) {
		Element startEndElement = doc.createElement("start");
		startEndElement.setAttribute("reference", Integer.toString(edge.getStart().hashCode()));
		return startEndElement;
	}
	
	
	public static Element getMultiLineStringElement(TextArea string, Document doc, String name)
	{
		Element mls = doc.createElement(name);
		mls.setAttribute("justification", Integer.toString(string.getJustification()));
		mls.setAttribute("size", Integer.toString(string.getSize()));
		mls.setAttribute("underlined", Boolean.toString(string.isUnderlined()));
		Element text = doc.createElement("text");
		text.setTextContent(string.getText());
		mls.appendChild(text);
		return mls;
	}
	public static Element getNodeTextElement(AbstractNode node, Document doc) {
		return getMultiLineStringElement(((NoteNode)node).getText(), doc, "text");
	}
	public static Element getNodeContentElement(AbstractNode node, Document doc) {
		return getMultiLineStringElement(((PackageNode)node).getContents(), doc, "content");
	}
	public static Element getNodeMethodsElement(AbstractNode node, Document doc) {
		switch(node.getClass().getSimpleName())
		{
			case "ClassNode": return getMultiLineStringElement(((ClassNode)node).getMethods(), doc, "methods");
			case "InterfaceNode": return getMultiLineStringElement(((InterfaceNode)node).getMethods(), doc, "methods");
			default: return null;
		}
	}
	public static Element getNodeAttributesElement(AbstractNode node, Document doc) {
		return getMultiLineStringElement(((ClassNode)node).getAttributes(), doc, "attributes");
	}
	public static Element getNodeNameElement(AbstractNode node, Document doc) {
		switch(node.getClass().getSimpleName())
		{
			case "ClassNode": return getMultiLineStringElement(((ClassNode)node).getName(), doc, "name");
			case "InterfaceNode": return getMultiLineStringElement(((InterfaceNode)node).getName(), doc, "name");
			default: return null;
		}
	}
		public static Element getNodeBoundsElement(AbstractNode node, Document doc) {
			Element bounds = doc.createElement("bounds");
			bounds.setAttribute("x", Double.toString(node.getBounds().getX()));
			bounds.setAttribute("y", Double.toString(node.getBounds().getY()));
			bounds.setAttribute("h", Double.toString(node.getBounds().getHeight()));
			bounds.setAttribute("w", Double.toString(node.getBounds().getWidth()));
			return bounds;
	}
		
	public static Element getColorElement(NoteNode node, Document doc)
	{
		Element color = doc.createElement("color");
		Color RGB = node.getColor();
		color.setAttribute("red", Integer.toString(RGB.getRed()));
		color.setAttribute("green", Integer.toString(RGB.getGreen()));
		color.setAttribute("blue", Integer.toString(RGB.getBlue()));
		color.setAttribute("alpha", Integer.toString(RGB.getAlpha()));
		return color;
	}

	
	public static Element getNodeParentElement(AbstractNode node, Document doc) {
		Element parent = null;
		if(node.getParent()!=null)
		{
			parent = doc.createElement("parent");
			parent.setAttribute("reference", Integer.toString(node.getParent().hashCode()));
		}
		return parent;
	}
	
	public static Element getEdgeArrowHead(SegmentedEdge edge, Document doc){
		Element arrowHead = doc.createElement("endArrowHead");
		String type = null;
		if (edge.getEndArrowHead().equals(LineArrow.BLACK_DIAMOND))
			type = "BLACK_DIAMOND";
		else if (edge.getEndArrowHead().equals(LineArrow.BLACK_TRIANGLE))
			type = "BLACK_TRIANGLE";
		else if (edge.getEndArrowHead().equals(LineArrow.DIAMOND))
			type = "DIAMOND";
		else if (edge.getEndArrowHead().equals(LineArrow.HALF_V))
			type = "HALF_V";
		else if (edge.getEndArrowHead().equals(LineArrow.NONE))
			type = "NONE";
		else if (edge.getEndArrowHead().equals(LineArrow.TRIANGLE))
			type = "TRIANGLE";
		else if (edge.getEndArrowHead().equals(LineArrow.V))
			type = "V";
		arrowHead.setAttribute("endArrowHead", type);
		return arrowHead;
	}
	
	public static Element getLineStyleElement(SegmentedEdge edge, Document doc)
	{
		Element lineStyle = doc.createElement("lineStyle");
		String style = null;
		if (edge.getLineStyle().equals(LineStyle.DOTTED))
			style = "DOTTED";
		else if (edge.getLineStyle().equals(LineStyle.SOLID))
			style = "SOLID";
		lineStyle.setAttribute("lineStyle", style);
		return lineStyle;
	}
	
	public static LineStyle parseEdgeLineStyle(Node node)
	{
		Element lineStyleElement = (Element) node;
		switch(lineStyleElement.getAttribute("lineStyle"))
		{
			case "DOTTED": return LineStyle.DOTTED;
			case "SOLID": return LineStyle.SOLID;
			default : return null;
		}
	}
	public static Color parseRGB(Node RGB)
	{
		Element RGBElement = (Element) RGB;
		int red = 0, green = 0, blue = 0, alpha = 0;
		red = Integer.parseInt(RGBElement.getAttribute("red"));
		green = Integer.parseInt(RGBElement.getAttribute("green"));
		blue  = Integer.parseInt(RGBElement.getAttribute("blue"));
		alpha = Integer.parseInt(RGBElement.getAttribute("alpha"));
		Color color = new Color(red, green, blue, alpha);
		return color;
	}
	
	public static LineArrow parseArrowHead(Node arrowHead)
	{
		Element arrowHeadElement = (Element) arrowHead;
		String type = arrowHeadElement.getAttribute("endArrowHead");
		switch(type)
		{
			case "BLACK_DIAMOND": return LineArrow.BLACK_DIAMOND;
			case "BLACK_TRIANGLE": return LineArrow.BLACK_TRIANGLE;
			case "DIAMOND": return LineArrow.DIAMOND;
			case "HALF_V": return LineArrow.HALF_V;
			case "NONE": return LineArrow.NONE;
			case "TRIANGLE": return LineArrow.TRIANGLE;
			case "V": return LineArrow.V;
			default : return null;
		}
	}
	
	public static TextArea parseMLS(Node MLS)
	{
		Element elMLS = (Element) MLS;
		TextArea mls = new TextArea();
		mls.setJustification(Integer.parseInt(elMLS.getAttribute("justification")));
		mls.setSize(Integer.parseInt(elMLS.getAttribute("size")));
		mls.setUnderlined(Boolean.parseBoolean(elMLS.getAttribute("underlined")));
		for (int i = 0; i < elMLS.getChildNodes().getLength(); i++) {
			if(elMLS.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)
				mls.setText(elMLS.getChildNodes().item(i).getTextContent());
		}
		return mls;
	}

	
	public static void initNode(AbstractNode Node, List<AbstractNode> childNodes, Rectangle2D bounds)
	{
		for (int i = 0; i < childNodes.size(); i++)
		{
			(Node).addChild(childNodes.get(i));
			(childNodes.get(i)).setParent(Node);
		}		
		if(Node instanceof RectangularNode)
			((RectangularNode)Node).setBounds(bounds);
		else 
		{
			((PointNode)Node).translate(bounds.getX(), bounds.getY());
		}
	}
	
	public static LineType parseBendStyle(Node nodeBentStyle)
	{
		Element bentStyleElement = (Element) nodeBentStyle;
		String style = bentStyleElement.getAttribute("name");
		switch(style)
		{
			case "HV": return LineType.HV;
			case "HVH": return LineType.HVH;
			case "STRAIGHT": return LineType.STRAIGHT;
			case "VH": return LineType.VH;
			case "VHV": return LineType.VHV;
			default: return null;
		}
	}
	
	public static Rectangle2D parseBounds(Node boundsNode) {
		Rectangle2D rect = new Rectangle2D.Double();
		Element boundsElement = (Element) boundsNode;
		double x = Double.parseDouble(boundsElement.getAttribute("x"));
		double y = Double.parseDouble(boundsElement.getAttribute("y"));
		double w = Double.parseDouble(boundsElement.getAttribute("w"));
		double h = Double.parseDouble(boundsElement.getAttribute("h"));
		rect.setRect(x, y, w, h);
		return rect;
	}
}
