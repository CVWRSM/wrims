package wrims.schematic.jdiagram;

import java.util.HashMap;

import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramNodeList;

public class SchematicDiff {
	public static void main(String[] args) throws Exception {
		Diagram diagram1 = new Diagram();
		diagram1.loadFromXml("d:/temp/CS3_NetworkSchematic_KF_original_New.xml");
		Diagram diagram2 = new Diagram();
		diagram2.loadFromXml("d:/temp/CS3_NetworkSchematic405_New.xml");

		//
		DiagramNodeList list1 = diagram1.getNodes();
		DiagramNodeList list2 = diagram2.getNodes();
		HashMap<String, DiagramNode> nodeMap1 = new HashMap<String, DiagramNode>();
		for(DiagramNode node: list1){
			nodeMap1.put(node.getTextToEdit().toString(), node);
		}
		HashMap<String, DiagramNode> nodeZindexMap1 = new HashMap<String, DiagramNode>();
		for(DiagramNode node: list1){
			nodeZindexMap1.put(node.getZIndex()+"", node);
		}

		
		HashMap<String, DiagramNode> nodeMap2 = new HashMap<String, DiagramNode>();
		for(DiagramNode node: list2){
			nodeMap2.put(node.getTextToEdit().toString(), node);
		}
		HashMap<String, DiagramNode> nodeZindexMap2 = new HashMap<String, DiagramNode>();
		for(DiagramNode node: list2){
			nodeZindexMap2.put(node.getZIndex()+"", node);
		}

		System.out.println("--- What's in LIST 1 thats not in LIST 2 ---");
		for(DiagramNode node1: list1){
			String id = node1.getTextToEdit().toString();
			DiagramNode node2 = nodeMap2.get(id);
			String zIndex = node1.getZIndex()+"";
			DiagramNode nodeZ2 = nodeZindexMap2.remove(zIndex);
			if (node2==null){
				if (nodeZ2 != null){
					System.out.println("Renamed Node : "+ node1.getTextToEdit()+" -> "+nodeZ2.getTextToEdit());
				} else {
					System.out.println("Deleted Node : "+id);
				}
			}else{
				
			}
		}
		
		for(String zIndex: nodeZindexMap2.keySet()){
			DiagramNode node2 = nodeZindexMap2.get(zIndex);
			String textToEdit = node2.getTextToEdit();
			DiagramNode node1 = nodeMap1.get(textToEdit);
			if (node1 != null){
				if (node1.getZIndex() != node2.getZIndex()){
					//System.out.println("Node exists for text: "+textToEdit+" but zindex has changed: "+node1.getZIndex()+" -> "+node2.getZIndex());
				}
			} else {
				System.out.println("Added Node : "+node2.getTextToEdit());
			}
		}


	}
}
