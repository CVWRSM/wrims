package wrims.schematic.element;

import java.util.HashMap;

public class Element {
	public static final int RESERVOIR = 100;
	public static final int CHANNEL = 200;
	public static final int DEMAND = 300;
	public static final int INFLOW = 400;
	public static final int NODE = 500;

	private static HashMap<Integer, String> typeToStringMap;

	static {
		typeToStringMap = new HashMap<Integer, String>();
		typeToStringMap.put(new Integer(RESERVOIR), "Reservoir");
		typeToStringMap.put(new Integer(CHANNEL), "Channel");
		typeToStringMap.put(new Integer(DEMAND), "Demand");
		typeToStringMap.put(new Integer(INFLOW), "Inflow");
		typeToStringMap.put(new Integer(NODE), "Node");
	}

	private int type;
	private String name;

	public Element(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public String getTypeString() {
		String typeName = typeToStringMap.get(type);
		return typeName == null ? "" : typeName;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getTypeString() + " " + getName();
	}
}
