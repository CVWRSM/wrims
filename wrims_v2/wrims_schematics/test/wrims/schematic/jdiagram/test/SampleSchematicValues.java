package wrims.schematic.jdiagram.test;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.mindfusion.diagramming.Align;
import com.mindfusion.diagramming.AttachToNode;
import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramAdapter;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.Group;
import com.mindfusion.diagramming.LinkEvent;
import com.mindfusion.diagramming.NodeEvent;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.TextFormat;

public class SampleSchematicValues {
	public static void main(String[] args) {
		DiagramView view = new DiagramView();
		final Diagram diagram = new Diagram();
		view.setBehavior(Behavior.DoNothing);
		view.setDiagram(diagram);

		diagram.addDiagramListener(new DiagramAdapter() {
			private Group textGroup;

			@Override
			public void linkClicked(LinkEvent e) {
				System.out.println("Clicked link: " + e.getLink());
			}

			@Override
			public void linkDoubleClicked(LinkEvent e) {
				System.out.println("Double clicked link: " + e.getLink());
			}

			@Override
			public void nodeClicked(NodeEvent e) {
				System.out.println("Clicked node: " + e.getNode());
				System.out.println("Text to edit: "
						+ e.getNode().getTextToEdit());
				System.out.println("Color: " + e.getNode().getPen().getColor());
				Shape s = ((ShapeNode) e.getNode()).getShape();
				System.out.println("Shape: " + s);
				System.out.println("Shape type: " + s.getId());
				System.out.println("Shape outline: " + s.getOutline());

				//
				if (textGroup != null) {
					if (textGroup.getVisible()) {
						textGroup.setVisible(false);
						return;
					} else {
						textGroup.setVisible(true);
					}
				}
				// attach node and display value
				Float r = e.getNode().getBounds();
				Rectangle2D.Float r2 = new Rectangle2D.Float(r.x, r.y
						+ r.height / 2, r.width / 2, r.height / 2);
				ShapeNode transparentTextNode = diagram.getFactory()
						.createShapeNode(r2);
				transparentTextNode.setTransparent(true);
				transparentTextNode.setText("Bottom left");
				transparentTextNode.setFont(e.getNode().getFont());
				TextFormat tf = new TextFormat(Align.Near, Align.Far);
				transparentTextNode.setTextFormat(tf);

				if (textGroup == null) {
					textGroup = diagram.getFactory().createGroup(
							transparentTextNode);
					textGroup.setFollowMasterContainment(false);
				} else {
					boolean attachToCorner = textGroup.attachToCorner(
							transparentTextNode, AttachToNode.BottomCenter);
					System.out.println("AttachToCorner return value: "
							+ attachToCorner);
					System.out.println("Number of attached nodes: "
							+ textGroup.getAttachedNodes().size());
				}
			}

			@Override
			public void nodeDoubleClicked(NodeEvent e) {
				System.out.println("Double clicked link: " + e.getNode());
			}

		});

		try {
			diagram
					.loadFromXml("resources/wrims/schematic/CS3_NetworkSchematic.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}

		DiagramNode findNode = diagram.findNode("YBP002");
		System.out.println("Found node: " + findNode);

		JFrame fr = new JFrame();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.getContentPane().add(new JScrollPane(view));
		fr.pack();
		fr.setSize(800, 600);
		fr.setVisible(true);
	}
}
