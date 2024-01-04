package wrims.schematic.jdiagram;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramLink;
import com.mindfusion.diagramming.DiagramLinkList;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramNodeList;
import com.mindfusion.diagramming.ShapeNode;

public class SchematicContinuityGenerator {
	public static void main(String[] args) throws Exception {
		Diagram diagram = new Diagram();
		diagram
				.loadFromXml("d:/temp/CS3_NetworkSchematic_Combined_12132011_cleaned.xml");
		//
		DiagramNodeList list = diagram.getNodes();
		HashMap<String, DiagramNode> nodeMap = new HashMap<String, DiagramNode>();
		for (DiagramNode node : list) {
			nodeMap.put(node.getTextToEdit().toString(), node);
		}
		HashMap<String, DiagramNode> nodeZindexMap = new HashMap<String, DiagramNode>();
		for (DiagramNode node : list) {
			if (!(node instanceof ShapeNode))
				continue;
			ShapeNode snode = (ShapeNode) node;
			// System.out.println(snode.getText()+" is an "+snode.getShape().getId());
			if (snode.getShape().getId().equals("Ellipse")) {
				printContinuity(snode, null);
			}
			if (snode.getShape().getId().equals("Alternative")) {
				System.out.println("Reservoir: " + snode.getText());
				Rectangle2D.Float bounds = snode.getBounds();
				Point2D.Float point = new Point2D.Float(bounds.x+10,bounds.y+bounds.height+10);
				DiagramNodeList listHit = diagram.getNodesAt(point);
				ShapeNode itemAt = null;
				if (listHit != null && listHit.size()>0){
					itemAt = (ShapeNode) listHit.get(0);
				}
				printContinuity(snode, (ShapeNode) itemAt);
			}
		}

	}

	public static void printContinuity(ShapeNode snode, ShapeNode itemAt) {
		System.out.println("//Continuity Node:" + snode.getText());
		DiagramLinkList allIncomingLinks = snode.getAllIncomingLinks();
		StringBuilder buf = new StringBuilder();
		for (DiagramLink link : allIncomingLinks) {
			buf.append(link.getOrigin().getTextToEdit()).append("+");
		}
		if (itemAt != null){
			DiagramLinkList outgoingLinks = itemAt.getAllOutgoingLinks();
			for(DiagramLink link: outgoingLinks){
				buf.append(link.getDestination().getTextToEdit()+"+");
			}
			buf.append("(").append(itemAt.getText()).append("-").append(itemAt.getText()).append("(-1)").append(")").append("+");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length() - 1);
		}
		buf.append("=");
		DiagramLinkList outgoingLinks = snode.getOutgoingLinks();
		for (DiagramLink link : outgoingLinks) {
			buf.append(link.getDestination().getTextToEdit() + "+");
		}
		buf.deleteCharAt(buf.length() - 1);
		System.out.println(buf);
	}
}
