package model.diagram;

import java.awt.geom.Point2D;

import model.AppModel;
import model.Diagram;
import model.LineArrow;
import model.LineType;
import model.LineStyle;
import model.abstracts.Edge;
import model.abstracts.Node;
/**
 	UML klasni dijagram.
*/
public class ClassDiagram extends Diagram 
{
	public ClassDiagram(AppModel application)
	{
		super(application);
	}
	
	public ClassDiagram()
	{
		super();
	}
	
	public boolean connect(Edge e, Point2D p1, Point2D p2) {
		Node n1 = findNode(p1);
		Node n2 = findNode(p2);
		// if (n1 == n2) return false;
		return super.connect(e, p1, p2);
	}

	public Node[] getNodePrototypes() {
		return NODE_PROTOTYPES;
	}

	public Edge[] getEdgePrototypes() {
		return EDGE_PROTOTYPES;
	}

	private static final Node[] NODE_PROTOTYPES = new Node[4];

	private static final Edge[] EDGE_PROTOTYPES = new Edge[7];

	static {
		NODE_PROTOTYPES[0] = new ClassNode();
		NODE_PROTOTYPES[1] = new InterfaceNode();
		NODE_PROTOTYPES[2] = new PackageNode();
		NODE_PROTOTYPES[3] = new NoteNode();

		ClassRelationshipEdge dependency = new ClassRelationshipEdge();
		dependency.setLineStyle(LineStyle.DOTTED);
		dependency.setEndArrowHead(LineArrow.V);
		dependency.setName("Dependency");
		EDGE_PROTOTYPES[0] = dependency;
		ClassRelationshipEdge inheritance = new ClassRelationshipEdge();
		inheritance.setBentStyle(LineType.VHV);
		inheritance.setEndArrowHead(LineArrow.TRIANGLE);
		inheritance.setName("Inheritance");
		EDGE_PROTOTYPES[1] = inheritance;

		ClassRelationshipEdge interfaceInheritance = new ClassRelationshipEdge();
		interfaceInheritance.setBentStyle(LineType.VHV);
		interfaceInheritance.setLineStyle(LineStyle.DOTTED);
		interfaceInheritance.setEndArrowHead(LineArrow.TRIANGLE);
		interfaceInheritance.setName("Realization");
		EDGE_PROTOTYPES[2] = interfaceInheritance;

		ClassRelationshipEdge association = new ClassRelationshipEdge();
		association.setBentStyle(LineType.HVH);
		association.setEndArrowHead(LineArrow.V);
		association.setName("Association");
		EDGE_PROTOTYPES[3] = association;

		ClassRelationshipEdge aggregation = new ClassRelationshipEdge();
		aggregation.setBentStyle(LineType.HVH);
		aggregation.setStartArrowHead(LineArrow.DIAMOND);
		aggregation.setName("Aggregation");
		EDGE_PROTOTYPES[4] = aggregation;

		ClassRelationshipEdge composition = new ClassRelationshipEdge();
		composition.setBentStyle(LineType.HVH);
		composition.setStartArrowHead(LineArrow.BLACK_DIAMOND);
		composition.setName("Composition");
		EDGE_PROTOTYPES[5] = composition;

		EDGE_PROTOTYPES[6] = new NoteEdge();
	}
}
