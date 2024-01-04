package gov.ca.dwr.callite;

import gov.ca.dsm2.input.parser.InputTable;
import gov.ca.dsm2.input.parser.Parser;
import gov.ca.dsm2.input.parser.Tables;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;

import vista.set.DataReference;
import vista.set.Group;
import vista.set.RegularTimeSeries;
import vista.time.TimeFactory;
import vista.time.TimeWindow;

/**
 * Generates a report based on the template file instructions
 * 
 * @author psandhu
 * 
 */
public class Report {
	/**
	 * Externalizes the format for output. This allows the flexibility of
	 * defining a writer to output the report to a PDF file vs an HTML file.
	 * 
	 * @author psandhu
	 * 
	 */
	public static interface Writer {
		static final int BOLD = 100;
		static final int NORMAL = 1;

		void startDocument(String outputFile);

		void endDocument();
		
		void setTableFontSize(String tableFontSize);

		void addTableTitle(String string);

		void addTableHeader(ArrayList<String> headerRow, int[] columnSpans);

		void addTableRow(List<String> rowData, int[] columnSpans, int style,
				boolean centered);

		void endTable();

		void addTimeSeriesPlot(ArrayList<double[]> buildDataArray,
				String title, String[] seriesName, String xAxisLabel,
				String yAxisLabel);

		void addExceedancePlot(ArrayList<double[]> buildDataArray,
				String title, String[] seriesName, String xAxisLabel,
				String yAxisLabel);

		public void setAuthor(String author);

		void addTableSubTitle(String string);

		public void addTitlePage(String compareInfo, String author);
	}

	static final Logger logger = Logger.getLogger("callite.report");
	private ArrayList<ArrayList<String>> twValues;
	private ArrayList<PathnameMap> pathnameMaps;
	private HashMap<String, String> scalars;
	private Writer writer;

	public Report(String templateFile) throws IOException {
		this(new FileInputStream(templateFile));
	}

	public Report(InputStream inputStream) throws IOException {
		generateReport(inputStream);
	}

	void generateReport(InputStream templateContentStream) throws IOException {
		logger.fine("Parsing input template");
		Utils.clearMessages();
		parseTemplateFile(templateContentStream);
		doProcessing();
		logger.fine("Done generating report");
	}

	void parseTemplateFile(InputStream templateFileStream) throws IOException {
		Parser p = new Parser();
		Tables tables = p.parseModel(templateFileStream);
		// load scalars into a map
		InputTable scalarTable = tables.getTableNamed("SCALAR");
		ArrayList<ArrayList<String>> scalarValues = scalarTable.getValues();
		int nscalars = scalarValues.size();
		scalars = new HashMap<String, String>();
		for (int i = 0; i < nscalars; i++) {
			String name = scalarTable.getValue(i, "NAME");
			String value = scalarTable.getValue(i, "VALUE");
			scalars.put(name, value);
		}
		// load pathname mapping into a map
		InputTable pathnameMappingTable = tables
				.getTableNamed("PATHNAME_MAPPING");
		ArrayList<ArrayList<String>> pmap_values = pathnameMappingTable
				.getValues();
		int nvalues = pmap_values.size();
		pathnameMaps = new ArrayList<PathnameMap>();
		for (int i = 0; i < nvalues; i++) {
			String var_name = pathnameMappingTable.getValue(i, "VARIABLE");
			var_name = var_name.replace("\"", "");
			PathnameMap path_map = new PathnameMap(var_name);
			path_map.report_type = pathnameMappingTable.getValue(i,
					"REPORT_TYPE").toLowerCase();
			path_map.pathBase = pathnameMappingTable.getValue(i, "PATH_BASE");
			path_map.pathAlt = pathnameMappingTable.getValue(i, "PATH_ALT");
			path_map.var_category = pathnameMappingTable.getValue(i,
					"VAR_CATEGORY");
			path_map.row_type = pathnameMappingTable.getValue(i, "ROW_TYPE");
			if (path_map.pathAlt == null || path_map.pathAlt.length() == 0) {
				path_map.pathAlt = path_map.pathBase;
			}
			path_map.plot = pathnameMappingTable.getValue(i, "PLOT")
					.equalsIgnoreCase("Y");
			pathnameMaps.add(path_map);
		}
		InputTable timeWindowTable = tables.getTableNamed("TIME_PERIODS");
		twValues = timeWindowTable.getValues();
	}

	public void doProcessing() {
		// open files 1 and file 2 and loop over to plot
		Group dssGroupBase = Utils.opendss(scalars.get("FILE_BASE"));
		Group dssGroupAlt = Utils.opendss(scalars.get("FILE_ALT"));
		ArrayList<TimeWindow> timewindows = new ArrayList<TimeWindow>();
		for (ArrayList<String> values : twValues) {
			String v = values.get(1).replace("\"", "");
			timewindows.add(TimeFactory.getInstance().createTimeWindow(v));
		}
		TimeWindow tw = null;
		if (timewindows.size() > 0) {
			tw = timewindows.get(0);
		}
		String output_file = scalars.get("OUTFILE");
		writer = new ReportPDFWriter();
		writer.startDocument(output_file);
		String author = scalars.get("MODELER").replace("\"", "");
		writer.addTitlePage(String.format("System Water Balance Report: %s vs %s",
				scalars.get("NAME_ALT"), scalars.get("NAME_BASE")), author);
		writer.setAuthor(author);
		if (dssGroupBase == null || dssGroupAlt == null) {
			String msg = "No data available in either : "
					+ scalars.get("FILE_BASE") + " or "
					+ scalars.get("FILE_ALT");
			logger.severe(msg);
			Utils.addMessage(msg);
			return;
		}

		generateSummaryTable();
		int dataIndex = 0;
		for (PathnameMap pathMap : pathnameMaps) {
			dataIndex = dataIndex + 1;
			logger.fine("Working on index: " + dataIndex);
			if (pathMap.pathAlt == null || pathMap.pathAlt == "") {
				pathMap.pathAlt = pathMap.pathBase;
			}
			boolean calculate_dts = false;
			if (pathMap.var_category.equals("HEADER")) {
				logger.fine("Inserting header");
				continue;
			}
			if (pathMap.report_type.endsWith("_post")) {
				calculate_dts = true;
			}
			DataReference refBase = Utils.getReference(dssGroupBase,
					pathMap.pathBase, calculate_dts, pathnameMaps, 1);
			DataReference refAlt = Utils.getReference(dssGroupAlt,
					pathMap.pathAlt, calculate_dts, pathnameMaps, 2);
			if (refBase == null || refAlt == null) {
				continue;
			}
			String[] series_name = new String[] { scalars.get("NAME_BASE"),
					scalars.get("NAME_ALT") };
			String data_units = Utils.getUnits(refBase, refAlt);
			String data_type = Utils.getType(refBase, refAlt);
			if (pathMap.plot) {
				if (pathMap.report_type.startsWith("average")) {
					generatePlot(Utils.buildDataArray(refAlt, refBase, tw),
							dataIndex, "Average "
									+ pathMap.var_name.replace("\"", ""),
							series_name, data_type + "(" + data_units + ")",
							"Time", PlotType.TIME_SERIES);
				} else if (pathMap.report_type.startsWith("exceedance")) {
					generatePlot(Utils.buildExceedanceArray(refAlt, refBase,
							pathMap.var_category == "S_SEPT", tw), dataIndex,
							Utils.getExceedancePlotTitle(pathMap), series_name,
							data_type + "(" + data_units + ")",
							"Percent at or above", PlotType.EXCEEDANCE);
				} else if (pathMap.report_type.startsWith("avg_excd")) {
					generatePlot(Utils.buildDataArray(refAlt, refBase, tw),
							dataIndex, "Average "
									+ pathMap.var_name.replace("\"", ""),
							series_name, data_type + "(" + data_units + ")",
							"Time", PlotType.TIME_SERIES);
					generatePlot(Utils.buildExceedanceArray(refAlt, refBase,
							pathMap.var_category == "S_SEPT", tw), dataIndex,
							Utils.getExceedancePlotTitle(pathMap), series_name,
							data_type + "(" + data_units + ")",
							"Percent at or above", PlotType.EXCEEDANCE);
				} else if (pathMap.report_type.startsWith("timeseries")) {
					generatePlot(Utils.buildDataArray(refAlt, refBase, tw),
							dataIndex, "Average "
									+ pathMap.var_name.replace("\"", ""),
							series_name, data_type + "(" + data_units + ")",
							"Time", PlotType.TIME_SERIES);
				} else if (pathMap.report_type.equals("alloc")) {
					generatePlot(Utils.buildExceedanceArray(refAlt, refBase,
							true, tw), dataIndex, "Exceedance "
							+ pathMap.var_name.replace("\"", ""), series_name,
							"Allocation (%)", "Probability",
							PlotType.EXCEEDANCE);
				}
			}
		}
		writer.endDocument();
	}

	private void generateSummaryTable() {
		
		writer.setTableFontSize(scalars.get("TABLE_FONT_SIZE"));
		
		
		writer.addTableTitle(String.format("System Flow Comparision: %s vs %s",
				scalars.get("NAME_ALT"), scalars.get("NAME_BASE")));
		writer.addTableSubTitle(scalars.get("NOTE").replace("\"", ""));
		writer.addTableSubTitle(scalars.get("ASSUMPTIONS").replace("\"", ""));
		writer.addTableSubTitle(" ");  // add empty line to increase space between title and table
		Group dssGroupBase = Utils.opendss(scalars.get("FILE_BASE"));
		Group dssGroupAlt = Utils.opendss(scalars.get("FILE_ALT"));
		ArrayList<TimeWindow> timewindows = new ArrayList<TimeWindow>();
		for (ArrayList<String> values : twValues) {
			String v = values.get(1).replace("\"", "");
			timewindows.add(TimeFactory.getInstance().createTimeWindow(v));
		}
		ArrayList<String> headerRow = new ArrayList<String>();
		headerRow.add("");
		ArrayList<String> headerRow2 = new ArrayList<String>();
		headerRow2.add("");

		for (TimeWindow tw : timewindows) {
			headerRow.add(Utils.formatTimeWindowAsWaterYear(tw));
			headerRow2.addAll(Arrays.asList(scalars.get("NAME_ALT"), scalars
					.get("NAME_BASE"), "Diff", "% Diff"));
		}
		int[] columnSpans = new int[timewindows.size() + 1];
		columnSpans[0] = 1;
		for (int i = 1; i < columnSpans.length; i++) {
			columnSpans[i] = 4;
		}
		writer.addTableHeader(headerRow, columnSpans);
		writer.addTableHeader(headerRow2, null);
		List<String> categoryList = Arrays.asList("RF", "DI", "DO", "DE",
				"SWPSOD", "CVPSOD");
		boolean firstDataRow = true;
		for (PathnameMap pathMap : pathnameMaps) {
			if (!categoryList.contains(pathMap.var_category)) {
				continue;
			}
			ArrayList<String> rowData = new ArrayList<String>();
			rowData.add(pathMap.var_name);
			boolean calculate_dts = false;
			if (pathMap.report_type.toLowerCase().endsWith("_post")) {
				calculate_dts = true;
			}
			DataReference refBase = null, refAlt = null;
			if (!pathMap.pathBase.equalsIgnoreCase("ignore")) {
				refBase = Utils.getReference(dssGroupBase, pathMap.pathBase,
						calculate_dts, pathnameMaps, 1);
			}
			if (!pathMap.pathAlt.equalsIgnoreCase("ignore")) {
				refAlt = Utils.getReference(dssGroupAlt, pathMap.pathAlt,
						calculate_dts, pathnameMaps, 2);
			}
			for (TimeWindow tw : timewindows) {
				double avgBase = 0, avgAlt = 0;
				if (refAlt != null) {
					avgAlt = Utils.avg(Utils.cfs2taf((RegularTimeSeries) refAlt
							.getData()), tw);
					rowData.add(formatDoubleValue(avgAlt));
				} else {
					rowData.add("");
				}
				if (refBase != null) {
					avgBase = Utils
							.avg(Utils.cfs2taf((RegularTimeSeries) refBase
									.getData()), tw);
					rowData.add(formatDoubleValue(avgBase));
				} else {
					rowData.add("");
				}
				if (refBase == null || refAlt == null) {
					rowData.add("");
					rowData.add("");
				} else {
					double diff = avgAlt - avgBase;
					double pctDiff = Double.NaN;
					if (avgBase != 0) {
						pctDiff = diff / avgBase * 100;
					}
					rowData.add(formatDoubleValue(diff));
					rowData.add(formatDoubleValue(pctDiff));
				}
			}
			if ("B".equals(pathMap.row_type)) {
				if (!firstDataRow) {
					ArrayList<String> blankRow = new ArrayList<String>();
					for (int i = 0; i < rowData.size(); i++) {
						blankRow.add(" ");
					}
					writer.addTableRow(blankRow, null, Writer.NORMAL, false);
				}
				writer.addTableRow(rowData, null, Writer.BOLD, false);
			} else {
				writer.addTableRow(rowData, null, Writer.NORMAL, false);
			}
			firstDataRow = false;
		}
		writer.endTable();
	}

	private String formatDoubleValue(double val) {
		return Double.isNaN(val) ? "" : String.format("%3d", Math.round(val));
	}

	public void generatePlot(ArrayList<double[]> buildDataArray, int dataIndex,
			String title, String[] seriesName, String yAxisLabel,
			String xAxisLabel, String plotType) {
		if (plotType.equals(PlotType.TIME_SERIES)) {
			writer.addTimeSeriesPlot(buildDataArray, title, seriesName,
					xAxisLabel, yAxisLabel);
		} else if (plotType.equals(PlotType.EXCEEDANCE)) {
			writer.addExceedancePlot(buildDataArray, title, seriesName,
					xAxisLabel, yAxisLabel);
		} else {
			String msg = "Requested unknown plot type: " + plotType
					+ " for title: " + title + " seriesName: " + seriesName[0]
					+ ",..";
			logger.warning(msg);
			Utils.addMessage(msg);
		}
	}

	public static interface PlotType {

		String TIME_SERIES = "timeseries";
		String EXCEEDANCE = "exceedance";

	}

	public static class PathnameMap {
		String report_type;
		String pathBase;
		String pathAlt;
		String var_category;
		String var_name;
		String row_type;
		boolean plot;

		public PathnameMap(String var_name) {
			this.var_name = var_name;
		}
	}

	public String getOutputFile() {
		return scalars.get("OUTFILE");
	}
}
