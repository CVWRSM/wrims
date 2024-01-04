package wrims.schematic.jdiagram;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mindfusion.diagramming.DiagramItem;
import com.mindfusion.diagramming.DiagramLink;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;

public class EditSchematicElementPanel extends JPanel{
	private JLabel colorLabel;
	private JLabel shapeLabel;
	private JComboBox shapeBox;
	private JComboBox colorBox;
	public EditSchematicElementPanel(){
		setLayout(new GridLayout(2,2));
		add(colorLabel = new JLabel("Color: "));
		add(colorBox = new JComboBox());
		colorBox.addItem(Color.red);
		colorBox.addItem(Color.blue);
		colorBox.addItem(Color.green);
		colorBox.addItem(Color.magenta);
		add(shapeLabel=new JLabel("Shape: "));
		add(shapeBox = new JComboBox());
		shapeBox.addItem("Rectangle");
		shapeBox.addItem("Circle");
		shapeBox.addItem("Triangle");
	}
	
	public void setDiagramItem(DiagramItem item){
		if (item instanceof ShapeNode){
			ShapeNode shapeNode = (ShapeNode) item;
			shapeBox.setVisible(true);
			shapeLabel.setVisible(true);
			Shape shape = shapeNode.getShape();
			setShapeSelected(shape);
			Color color = shapeNode.getPen().getColor();
			setColorSelected(color);
		} else if (item instanceof DiagramLink){
			DiagramLink link = (DiagramLink) item;
			shapeBox.setVisible(false);
			shapeLabel.setVisible(false);
			Color color = link.getPen().getColor();
			setColorSelected(color);
		}
	}

	private void setColorSelected(Color color) {
		colorBox.setSelectedItem(color);
	}

	private void setShapeSelected(Shape shape) {
		String id = shape.getId();
		System.out.println("Shape id: "+id);
		String shapeName = "Rectangle";
		if (id.equals("Alternative")){
			shapeName.equals("Triangle");
		} else if (id.equals("Ellipse")){
			shapeName.equals("Circle");
		}
		shapeBox.setSelectedItem(shapeName);
	}

	public String getShape() {
		String shapeName = (String) shapeBox.getSelectedItem();
		String shapeId = shapeName;
		if (shapeName.equals("Triangle")){
			shapeId = "Alternative";
		} else if (shapeName.equals("Circle")){
			shapeId = "Ellipse";
		}
		return shapeId;
	}
	
	public Color getColor(){
		return (Color) colorBox.getSelectedItem();
	}
}
