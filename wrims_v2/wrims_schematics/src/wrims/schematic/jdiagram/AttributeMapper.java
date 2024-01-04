package wrims.schematic.jdiagram;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import sun.java2d.pipe.ShapeSpanIterator;

import com.mindfusion.diagramming.Brush;
import com.mindfusion.diagramming.CustomDraw;
import com.mindfusion.diagramming.DashStyle;
import com.mindfusion.diagramming.Pen;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.SolidBrush;

public class AttributeMapper {

	public static class Attribute {
		public String name;
		public Pen pen;
		public Brush brush;
		public Shape shape;
		public int customdraw;
	}

	// pen attribute consists of color, dashstyle, width
	private HashMap<String, Attribute> nameToAttributeMap;

	public AttributeMapper() {
		Properties props = new Properties();
		try {
			props.load(getClass().getResourceAsStream("attributemap.props"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		nameToAttributeMap = new HashMap<String, Attribute>();
		for (String name : props.stringPropertyNames()) {
			String value = props.getProperty(name);
			Attribute attr = new Attribute();
			attr.name = name;
			attr.pen = parsePen(value);
			attr.brush = parseBrush(value);
			attr.shape = parseShape(value);
			attr.customdraw = parseCustomDraw(value);
			nameToAttributeMap.put(name, attr);
		}
	}

	public Brush parseBrush(String value) {
		if (value == null || value.trim().equals("")) {
			return null;
		}
		String[] fields = value.trim().split("\\|");
		if (fields == null) {
			return null;
		}
		return new SolidBrush(parseColor(fields[3]));
	}

	public Pen parsePen(String value) {
		if (value == null || value.trim().equals("")) {
			return null;
		}
		String[] fields = value.trim().split("\\|");
		if (fields == null) {
			return null;
		}
		Color c = parseColor(fields[0]);
		DashStyle s = parseStyle(fields[1]);
		float width = Float.parseFloat(fields[2]);
		return new Pen(width, c, s);
	}

	public String formatColor(Color c) {
		if (c == null) {
			return "";
		}
		return String.format("(%03d,%03d,%03d)", c.getRed(), c.getGreen(), c
				.getBlue());
	}

	public Color parseColor(String value) {
		String[] rgb = value.substring(1, value.length() - 1).split(",");
		return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]),
				Integer.parseInt(rgb[2]));
	}

	public String formatStyle(DashStyle s) {
		if (s == null) {
			return "";
		}
		return String.format("(%.2f,%s)", s.getDashPhase(), Arrays.toString(s
				.getDashArray()));
	}

	public DashStyle parseStyle(String value) {
		if (value == null || value.trim().equals("")) {
			return null;
		}
		String[] fields = value.substring(1, value.length() - 1).split(",\\[");
		float dashPhase = Float.parseFloat(fields[0]);
		float[] dashArray = parseFloatArray(fields[1].substring(0,fields[1].length()-1));
		return new DashStyle(dashArray, dashPhase);
	}

	public float[] parseFloatArray(String value) {
		if (value == null || value.trim().equals("")) {
			return null;
		}
		String[] fields = value.split(",");
		float[] array = new float[fields.length];
		for (int i = 0; i < fields.length; i++) {
			array[i] = Float.parseFloat(fields[i]);
		}
		return array;
	}
	
	public Shape parseShape(String value){
		if (value == null || value.trim().equals("")) {
			return null;
		}
		String[] fields = value.trim().split("\\|");
		if (fields == null) {
			return null;
		}
		return Shape.fromId(fields[4]);
	}
	
	public String formatShape(Shape s){
		return s.getId();
	}
	
	public int parseCustomDraw(String value){
		if (value == null || value.trim().equals("")) {
			return CustomDraw.None;
		}
		String[] fields = value.trim().split("\\|");
		if (fields == null) {
			return CustomDraw.None;
		}
		int d=Integer.parseInt(fields[5]);
		
		if(d == CustomDraw.Additional){
			return d;
		}
		return CustomDraw.None;
	}
	
	public String formatCustomDraw(int c){
		return String.format("%d", c);
	}

	public String getAttributeName(Pen p, Brush b, Shape s, int customdraw) {
		for (String name : nameToAttributeMap.keySet()) {
			Attribute attr = nameToAttributeMap.get(name);
			if (isSame(attr.pen, p) && isSame(attr.brush, b) && isSame(attr.shape,s) && attr.customdraw == customdraw) {
				return name;
			}
		}
		return "";
	}

	public boolean isSame(Brush brush1, Brush brush2) {
		if (brush1 == null) {
			return brush2 == null;
		} else {
			return brush2 != null
					&& (brush1 instanceof SolidBrush && brush2 instanceof SolidBrush)
					&& isSame(((SolidBrush) brush1).getColor(),
							((SolidBrush) brush2).getColor());
		}
	}

	public boolean isSame(Pen pen1, Pen pen2) {
		if (pen1 == null) {
			return pen2 == null;
		} else {
			return pen2 != null
					&& (isSame(pen1.getColor(), pen2.getColor()) && isSame(pen1
							.getDashStyle(), pen2.getDashStyle()) && pen1.getWidth() == pen2.getWidth());
		}

	}

	public boolean isSame(Color c1, Color c2) {
		if (c1 == null) {
			return c2 == null;
		} else {
			return c2 != null
					&& (c1.getRed() == c2.getRed()
							&& c1.getGreen() == c2.getGreen() && c1.getBlue() == c2
							.getBlue());
		}
	}

	public boolean isSame(DashStyle s1, DashStyle s2) {
		if (s1 == null) {
			return s2 == null;
		} else {
			return s2 != null
					&& (s1.getDashPhase() == s2.getDashPhase() && isSame(s1
							.getDashArray(), s2.getDashArray()));
		}
	}
	

	public boolean isSame(float[] a1, float[] a2) {
		if (a1 == null) {
			return a2 == null;
		} else {
			if (a2 == null) {
				return false;
			}
			if (a1.length != a2.length) {
				return false;
			}
			boolean isSame = true;
			for (int i = 0; i < a1.length; i++) {
				if (Math.abs(a1[i] - a2[i]) > 1e-03) { // about equal
					isSame = false;
					break;
				}
			}
			return isSame;
		}
	}
	
	public boolean isSame(Shape shape1, Shape shape2) {
		if (shape1 == null) {
			return shape2 == null;
		} else {
			return shape2 != null
					&& (formatShape(shape1).compareTo(formatShape(shape2)) == 0);
		}

	}

	public Attribute getAttribute(String attributeName) {
		return nameToAttributeMap.get(attributeName);
	}

}
