package gov.ca.dwr.wrims;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SldGenerator {
	
	static String footer = 
			  "      </FeatureTypeStyle>\n"
			+ "    </UserStyle>\n"
			+ "  </UserLayer>\n"
			+ "</StyledLayerDescriptor>\n";
	
	public static void main(String[] args) throws IOException {
		int[] zoomBounds = {75000, 250000};
		List<Style> nodeStyles = new ArrayList<Style>();
		nodeStyles.add(new Style("CH", "CH", Shape.CIRCLE, Color.BLUE, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("NP-A", "NP", "A", Shape.SQUARE, Color.GREEN, Color.BLACK, LineStyle.DASHED));
		nodeStyles.add(new Style("NP-R", "NP", "R", Shape.SQUARE, Color.TAN, Color.BLACK, LineStyle.DASHED));
		nodeStyles.add(new Style("NP-U", "NP", "U", Shape.SQUARE, Color.GREY, Color.BLACK, LineStyle.DASHED));
		nodeStyles.add(new Style("OM", "OM", Shape.CIRCLE_CROSS, Color.RED, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("PR-A", "NP", "A", Shape.SQUARE, Color.GREEN, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("PR-R", "NP", "R", Shape.SQUARE, Color.TAN, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("PR-U", "NP", "U", Shape.SQUARE, Color.GREY, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("PS", "PS", Shape.CIRCLE, Color.YELLOW, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("RFS", "RFS", Shape.STAR, Color.GREEN, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("S-A", "S", "A", Shape.SQUARE, Color.GREEN, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("S-U", "S", "U", Shape.SQUARE, Color.GREY, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("WTP", "WTP", Shape.CIRCLE, Color.RED, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("WWTP", "WWTP", Shape.CIRCLE, Color.GREEN, Color.BLACK, LineStyle.SOLID));
		nodeStyles.add(new Style("Default", Collections.<String, String> emptyMap(), Shape.CIRCLE, Color.RED, Color.BLACK, LineStyle.SOLID));
		Writer out = new OutputStreamWriter(System.out);	
		//writeSld(out, zoomBounds, nodeStyles, SldType.NODE);

		List<Style> arcStyles = new ArrayList<Style>();
		arcStyles.add(new Style("Channel", "Channel", Shape.CIRCLE, Color.WHITE, Color.BLUE, LineStyle.SOLID));
		arcStyles.add(new Style("Surface Runoff", "Surface Runoff", Shape.CIRCLE, Color.WHITE, Color.GREY, LineStyle.SOLID));
		arcStyles.add(new Style("Spill", "Spill", Shape.CIRCLE, Color.WHITE, Color.YELLOW, LineStyle.SOLID));
		arcStyles.add(new Style("Inflow", "Inflow", Shape.CIRCLE, Color.WHITE, Color.BLACK, LineStyle.DASHED));
		arcStyles.add(new Style("Return", "Return", Shape.CIRCLE, Color.WHITE, Color.GREEN, LineStyle.SOLID));
		arcStyles.add(new Style("Closure Term", "Closure Term", Shape.CIRCLE, Color.WHITE, Color.BLACK, LineStyle.SOLID));
		arcStyles.add(new Style("Diversion", "Diversion", Shape.CIRCLE, Color.WHITE, Color.RED, LineStyle.SOLID));
		arcStyles.add(new Style("Default", Collections.<String, String> emptyMap(), Shape.CIRCLE, Color.WHITE, Color.RED, LineStyle.DASHED));
		writeSld(out, zoomBounds, arcStyles, SldType.ARC);

		out.flush();
}
	
	public static void writeSld(Writer out, int[] zoomBounds, List<Style> styles, SldType sldType) throws IOException {
		writeHeader(out, sldType);
		for(int z = 0; z <= zoomBounds.length; z++) {
			for(Style style : styles) {
				out.write("        <Rule>\n");
				out.write("          <Name>" + style.name + "</Name>\n");
				style.writeFilterXml(out);
				if(z > 0) {
					out.write("          <MinScaleDenominator>" + zoomBounds[z-1] + "</MinScaleDenominator>\n");
				}
				if(z < zoomBounds.length) {
					out.write("          <MaxScaleDenominator>" + zoomBounds[z] + "</MaxScaleDenominator>\n");				
				}
				switch(sldType) {
				case ARC:
					switch(z) {
					case 0:
						writeLineSymbolizer(out, style, 3);
						writeTextSymbolizer(out, style, 14, true, true);
						break;
					case 1:
						writeLineSymbolizer(out, style, 2);					
						writeTextSymbolizer(out, style, 10, true, false);
						break;
					case 2:
						writeLineSymbolizer(out, style, 1);
						break;
					}
					break;
				case NODE:
					switch(z) {
					case 0:
						writePointSymbolizer(out, style, 16);
						writeTextSymbolizer(out, style, 14, 8, 8);
						break;
					case 1:
						writePointSymbolizer(out, style, 12);					
						writeTextSymbolizer(out, style, 10, 5, 5);
						break;
					case 2:
						writePointSymbolizer(out, style, 8);
						break;
					}
					break;
				}
				out.write("        </Rule>\n");
			}
		}
		out.write(footer);
	}			
	
	public static void writeHeader(Writer out, SldType sldType) throws IOException {
		out.write("<StyledLayerDescriptor\n"
				+ "  xmlns=\"http://www.opengis.net/sld\"\n"
				+ "  xmlns:ogc=\"http://www.opengis.net/ogc\"\n"
				+ "  xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
				+ "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "  version=\"1.0.0\"\n"
				+ "  xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\">\n"	
				+ "  <UserLayer>\n"
				+ "    <LayerFeatureConstraints>\n"
				+ "      <FeatureTypeConstraint/>\n"
				+ "    </LayerFeatureConstraints>\n"
				+ "    <UserStyle>\n"
				+ "      <Name>CalSim " + sldType + " Style</Name>\n"
				+ "      <Title>CalSim " + sldType + " Style</Title>\n"
				+ "      <FeatureTypeStyle>\n"
				+ "        <Name>group 0</Name>\n"
                + "        <FeatureTypeName>Feature</FeatureTypeName>\n"
                + "        <SemanticTypeIdentifier>generic:geometry</SemanticTypeIdentifier>\n"
                + "        <SemanticTypeIdentifier>simple</SemanticTypeIdentifier>\n");
	}

	static void writePointSymbolizer(Writer out, Style style, int size) throws IOException {
		if(style.shape.equals(Shape.CIRCLE_CROSS)) {
			writePointSymbolizer(out, new Style(style.name, style.filters, Shape.CIRCLE, Color.BLACK, style.lineColor, style.lineStyle), size);
			writePointSymbolizer(out, new Style(style.name, style.filters, Shape.CROSS, style.fillColor, Color.BLACK, style.lineStyle), size);
			return;
		}
		out.write("          <PointSymbolizer>\n"
				+ "            <Graphic>\n"
				+ "              <Mark>\n"
				+ "                <WellKnownName>" + style.shape + "</WellKnownName>\n"
				+ "                <Stroke>\n"
				+ "                  <CssParameter name=\"stroke\">" + style.lineColor.html + "</CssParameter>\n"
				+ "                  <CssParameter name=\"stroke-width\">" + style.lineStyle.width + "</CssParameter>\n");
		if(style.lineStyle.equals(LineStyle.DASHED)) {
			out.write("                  <CssParameter name=\"stroke-dasharray\">5 5</CssParameter>\n");
		}
		out.write("                </Stroke>\n"
				+ "                <Fill>\n"
				+ "                  <CssParameter name=\"fill\">" + style.fillColor.html + "</CssParameter>\n"
				+ "                </Fill>\n"
				+ "              </Mark>\n"
				+ "              <Size>" + size + "</Size>\n"
				+ "            </Graphic>\n"
				+ "          </PointSymbolizer>\n");
	}

	static void writeLineSymbolizer(Writer out, Style style, int size) throws IOException {
		out.write("          <LineSymbolizer>\n"
				+ "            <Stroke>\n"
				+ "              <CssParameter name=\"stroke\">" + style.lineColor.html + "</CssParameter>\n"
				+ "              <CssParameter name=\"stroke-width\">" + size + "</CssParameter>\n");
		if(style.lineStyle.equals(LineStyle.DASHED)) {
			out.write("              <CssParameter name=\"stroke-dasharray\">5 5</CssParameter>\n");
		}
		out.write("            </Stroke>\n"
				+ "          </LineSymbolizer>\n");
	}

	static void writeTextSymbolizer(Writer out, Style style, int size,  boolean withBox, boolean withDss) throws IOException {
		writeTextSymbolizer(out, style, size, withBox, withDss, 0, 0);
	}

	static void writeTextSymbolizer(Writer out, Style style, int size, int displacementX, int displacementY) throws IOException {
		writeTextSymbolizer(out, style, size, false, false, displacementX, displacementY);
	}
		
	static void writeTextSymbolizer(Writer out, Style style, int size, boolean withBox, boolean withDss, 
			int displacementX, int displacementY) throws IOException {
		if(withDss) {
			writeTextSymbolizer(out, "dss1", 10, "0", "1", -48, 16);
			writeTextSymbolizer(out, "dss2", 10, "1", "1", 48, 16);
			writeTextSymbolizer(out, "dss3", 10, "1", "0", 48, -16);
			writeTextSymbolizer(out, "dss4", 10, "0", "0", -48, -16);
		}
	    out.write("          <TextSymbolizer>\n"
	    		+ "            <Label>\n"
	    		+ "              <ogc:PropertyName>${idField}</ogc:PropertyName>\n"
	    		+ "            </Label>\n"
	    		+ "            <Font>\n"
	    		+ "              <CssParameter name=\"font-family\">Arial</CssParameter>\n"
	    		+ "              <CssParameter name=\"font-size\">" + size + "</CssParameter>\n"
	    		+ "              <CssParameter name=\"font-style\">normal</CssParameter>\n"
	            + "              <CssParameter name=\"font-weight\">bold</CssParameter>\n"
	    		+ "            </Font>\n"
	    		+ "            <LabelPlacement>\n"
	    		+ "              <PointPlacement>\n"
	    		+ "                <AnchorPoint>\n"
	    		+ "                  <AnchorPointX>" + (withBox ? "0.5" : "0") + "</AnchorPointX>\n"
	    		+ "                  <AnchorPointY>" + (withBox ? "0.5" : "0") + "</AnchorPointY>\n"
	    		+ "                </AnchorPoint>\n"
	    		+ "                <Displacement>\n"
	    		+ "                  <DisplacementX>" + displacementX + "</DisplacementX>\n"
	    		+ "                  <DisplacementY>" + displacementY + "</DisplacementY>\n"
	    		+ "                </Displacement>\n"
	    		+ "              </PointPlacement>\n"
	    		+ "            </LabelPlacement>\n"
	    		+ "            <Fill>\n"
	    		+ "              <CssParameter name=\"fill\">#000000</CssParameter>\n"
	    		+ "            </Fill>\n");
	    if(withBox) {
			out.write("            <Graphic>\n"
					+ "              <Mark>\n"
					+ "                <WellKnownName>wkt://POLYGON((-0.5 -0.2, -0.5 0.2, 0.5 0.2, 0.5 -0.2, -0.5 -0.2))</WellKnownName>\n"
					+ "                <Stroke>\n"
					+ "                  <CssParameter name=\"stroke\">" + style.lineColor.html + "</CssParameter>\n"
					+ "                  <CssParameter name=\"stroke-width\">" + style.lineStyle.width + "</CssParameter>\n");
			if(style.lineStyle.equals(LineStyle.DASHED)) {
				out.write("                  <CssParameter name=\"stroke-dasharray\">5 5</CssParameter>\n");
			}
			out.write("                </Stroke>\n"
					+ "                <Fill>\n"
					+ "                  <CssParameter name=\"fill\">" + style.fillColor.html + "</CssParameter>\n"
					+ "                </Fill>\n"
					+ "              </Mark>\n"
					+ "              <Size>" + (withDss ? size * 3 : size) + "</Size>\n"
					+ "            </Graphic>\n");    	
	    }
	    out.write("          </TextSymbolizer>\n");
	}
		
	static void writeTextSymbolizer(Writer out, String field, int fontSize, String anchorPointX, String anchorPointY,
			int displacementX, int displacementY) throws IOException {
	    out.write("          <TextSymbolizer>\n"
	    		+ "            <Label>\n"
	    		+ "              <ogc:PropertyName>" + field + "</ogc:PropertyName>\n"
	    		+ "            </Label>\n"
	    		+ "            <Font>\n"
	    		+ "              <CssParameter name=\"font-family\">Arial</CssParameter>\n"
	    		+ "              <CssParameter name=\"font-size\">" + fontSize + "</CssParameter>\n"
	    		+ "              <CssParameter name=\"font-style\">normal</CssParameter>\n"
	    		+ "            </Font>\n"
	    		+ "            <LabelPlacement>\n"
	    		+ "              <PointPlacement>\n"
	    		+ "                <AnchorPoint>\n"
	    		+ "                  <AnchorPointX>" + anchorPointX + "</AnchorPointX>\n"
	    		+ "                  <AnchorPointY>" + anchorPointY + "</AnchorPointY>\n"
	    		+ "                </AnchorPoint>\n"
	    		+ "                <Displacement>\n"
	    		+ "                  <DisplacementX>" + displacementX + "</DisplacementX>\n"
	    		+ "                  <DisplacementY>" + displacementY + "</DisplacementY>\n"
	    		+ "                </Displacement>\n"
	    		+ "              </PointPlacement>\n"
	    		+ "            </LabelPlacement>\n"
	    		+ "            <Fill>\n"
	    		+ "              <CssParameter name=\"fill\">#000000</CssParameter>\n"
	    		+ "            </Fill>\n"
	    		+ "          </TextSymbolizer>\n");		
	}

//    <TextSymbolizer>
//      <Label>
//        <ogc:PropertyName>CalSim_ID</ogc:PropertyName>
//      </Label>
//      <Font>
//        <CssParameter name="font-family">Arial</CssParameter>
//        <CssParameter name="font-size">10</CssParameter>
//        <CssParameter name="font-style">normal</CssParameter>
//        <CssParameter name="font-weight">bold</CssParameter>
//      </Font>
//      <LabelPlacement>
//        <PointPlacement>
//          <AnchorPoint>
//            <AnchorPointX>0.5</AnchorPointX>
//            <AnchorPointY>0.5</AnchorPointY>
//          </AnchorPoint>
//          <Displacement>
//            <DisplacementX>0</DisplacementX>
//            <DisplacementY>0</DisplacementY>
//          </Displacement>
//        </PointPlacement>
//      </LabelPlacement>
//      <Fill>
//        <CssParameter name="fill">#000000</CssParameter>
//      </Fill>
//    </TextSymbolizer>
//    <TextSymbolizer>
//      <Label>
//        <ogc:PropertyName>dss1</ogc:PropertyName>
//      </Label>
//      <Font>
//        <CssParameter name="font-family">Arial</CssParameter>
//        <CssParameter name="font-size">10</CssParameter>
//        <CssParameter name="font-style">normal</CssParameter>
//      </Font>
//      <LabelPlacement>
//        <PointPlacement>
//          <AnchorPoint>
//            <AnchorPointX>0.0</AnchorPointX>
//            <AnchorPointY>1.0</AnchorPointY>
//          </AnchorPoint>
//          <Displacement>
//            <DisplacementX>-48</DisplacementX>
//            <DisplacementY>16</DisplacementY>
//          </Displacement>
//        </PointPlacement>
//      </LabelPlacement>
//      <Fill>
//        <CssParameter name="fill">#000000</CssParameter>
//      </Fill>
//    </TextSymbolizer>
//    <TextSymbolizer>
//      <Label>
//        <ogc:PropertyName>dss2</ogc:PropertyName>
//      </Label>
//      <Font>
//        <CssParameter name="font-family">Arial</CssParameter>
//        <CssParameter name="font-size">10</CssParameter>
//        <CssParameter name="font-style">normal</CssParameter>
//      </Font>
//      <LabelPlacement>
//        <PointPlacement>
//          <AnchorPoint>
//            <AnchorPointX>1.0</AnchorPointX>
//            <AnchorPointY>1.0</AnchorPointY>
//          </AnchorPoint>
//          <Displacement>
//            <DisplacementX>48</DisplacementX>
//            <DisplacementY>16</DisplacementY>
//          </Displacement>
//        </PointPlacement>
//      </LabelPlacement>
//      <Fill>
//        <CssParameter name="fill">#000000</CssParameter>
//      </Fill>
//    </TextSymbolizer>

}

enum SldType {
	NODE, ARC;
}

enum Shape {
	CIRCLE, SQUARE, STAR, CROSS, CIRCLE_CROSS;
}

enum LineStyle {
	SOLID(1), DASHED(1);
	
	final int width;
	
	LineStyle(int width) {
		this.width = width;
	}
	
}

enum Color {
	RED("#FF0000"), 
	GREEN("#38761D"), 
	YELLOW("#FFFF00"), 
	GREY("#AAAAAA"), 
	TAN("#FFE599"), 
	BLUE("#3C78D8"),
	LIGHT_GREEN("#B6D7A8"),
	PURPLE("#FF00FF"),
	BLACK("#000000"),
	WHITE("#FFFFFF");
	
	final String html;
	
	Color(String html) {
		this.html = html;
	}
	
}

class Style {
	String name;
	Map<String,String> filters;
	Shape shape;
	Color fillColor;
	Color lineColor;
	LineStyle lineStyle;
	
	public Style(String name, Map<String,String> filters, Shape shape, Color fillColor, Color lineColor, LineStyle lineStyle) {
		this.name = name;
		this.filters = filters;
		this.shape = shape;
		this.fillColor = fillColor;
		this.lineColor = lineColor;
		this.lineStyle = lineStyle;
	}

	public Style(String name, String typeValue, Shape shape, Color fillColor, Color lineColor, LineStyle lineStyle) {
		this.name = name;
		filters = new HashMap<String,String>();
		filters.put("${typeField}", typeValue);
		this.shape = shape;
		this.fillColor = fillColor;
		this.lineColor = lineColor;
		this.lineStyle = lineStyle;		
	}

	public Style(String name, String typeValue, String subTypeValue, Shape shape, Color fillColor, Color lineColor, LineStyle lineStyle) {
		this.name = name;
		filters = new HashMap<String,String>();
		filters.put("${typeField}", typeValue);
		filters.put("${subTypeField}", subTypeValue);
		this.shape = shape;
		this.fillColor = fillColor;
		this.lineColor = lineColor;
		this.lineStyle = lineStyle;		
	}

	public void writeFilterXml(Writer out) throws IOException {
		if(filters.size() == 0) {
			out.write("          <ogc:ElseFilter/>\n");
		} else {
			out.write("          <ogc:Filter>\n");
			if(filters.size() > 1) {
				out.write("          <ogc:And>\n");
			}
			for(Map.Entry<String,String> entry : filters.entrySet()) {
				out.write("            <ogc:PropertyIsEqualTo>\n"
						+ "              <ogc:PropertyName>" + entry.getKey() + "</ogc:PropertyName>\n"
						+ "              <ogc:Literal>" + entry.getValue() + "</ogc:Literal>\n"
						+ "            </ogc:PropertyIsEqualTo>\n");
			}
			if(filters.size() > 1) {
				out.write("          </ogc:And>\n");
			}
			out.write("          </ogc:Filter>\n");
		}
	}
	
}
