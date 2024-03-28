package wrimsv2_plugin.reporttool;

import gov.ca.dsm2.input.parser.InputTable;
import gov.ca.dsm2.input.parser.Parser;
import gov.ca.dsm2.input.parser.Tables;
import hec.data.TimeWindow;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecTimeSeries;
import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.model.WPPValue;
import wrimsv2_plugin.debugger.view.WPPAllGoalView;
import wrimsv2_plugin.tools.DataProcess;

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

		public void addTitlePage(String compareInfo, String author, String fileBase, String fileAlt);
	}

	//static final Logger logger = Logger.getLogger("callite.report");
	private ArrayList<ArrayList<String>> twValues;
	private ArrayList<PathnameMap> pathnameMaps;
	private HashMap<String, String> scalars;
	private Writer writer;

	public Report(String templateFile, String outputFileName) throws IOException {
		this(new FileInputStream(templateFile), outputFileName);
	}

	public Report(InputStream inputStream, String outputFileName) throws IOException {
		generateReport(inputStream, outputFileName);
	}

	void generateReport(final InputStream templateContentStream, final String outputFilename) throws IOException {
		
		final IWorkbench workbench=PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable(){
			public void run(){
				Shell shell=workbench.getActiveWorkbenchWindow().getShell();
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);  
				try {
					dialog.run(true,false, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Generate PDF Report", 100);
							//logger.fine("Parsing input template");
							Utils.clearMessages();
							try {
								parseTemplateFile(templateContentStream, monitor);
							} catch (IOException e) {
								WPPException.handleException(e);
							}
							doProcessing(monitor);
							//logger.fine("Done generating report");
							openOutputFile(outputFilename);
							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	protected void openOutputFile(String outputFilename) {

		try {
			Runtime.getRuntime().exec("cmd /c start " + outputFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;

	}
	
	void parseTemplateFile(InputStream templateFileStream, IProgressMonitor monitor) throws IOException {
		monitor.subTask("Parsing template file.");
		monitor.worked(0);
		
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
			path_map.units = pathnameMappingTable.getValue(i, "UNIT");
			pathnameMaps.add(path_map);
		}
		InputTable timeWindowTable = tables.getTableNamed("TIME_PERIODS");
		twValues = timeWindowTable.getValues();
	}

	public void doProcessing(IProgressMonitor monitor) {
		
		monitor.subTask("Processing template file.");
		monitor.worked(20);
		
		// open files 1 and file 2 and loop over to plot
		HecTimeSeries htsBase = new HecTimeSeries(scalars.get("FILE_BASE"));
		htsBase.setRetrieveAllTimes(true);
		String[] basePaths = htsBase.getCatalog(false);
		if (basePaths.length == 0){
			String msg = "No data available in "
					+ scalars.get("FILE_BASE");
			Utils.addMessage(msg);
			return;}
		String baseAPart = basePaths[0].split("/")[1];
		String baseFPart = basePaths[0].split("/")[6];
		DSSPathname.setDefaultDPart(basePaths[0].split("/")[4]);

		HecTimeSeries htsAlt = new HecTimeSeries(scalars.get("FILE_ALT"));
		htsAlt.setRetrieveAllTimes(true);
		String[] altPaths = htsAlt.getCatalog(false);
		if (altPaths.length == 0){
			String msg = "No data available in "
					+ scalars.get("FILE_ALT");
			Utils.addMessage(msg);
			return;}
		String altAPart = altPaths[0].split("/")[1];
		String altFPart = altPaths[0].split("/")[6];

		ArrayList<TimeWindow> timewindows = new ArrayList<TimeWindow>();
		HecTime startTime = new HecTime();
		HecTime endTime = new HecTime();	
		for (ArrayList<String> values : twValues) {
			String v = values.get(1).replace("\"", "");
			String[] dateStrings = v.split("-");
			startTime.set(dateStrings[0]);
			endTime.set(dateStrings[1]);
			timewindows.add(new TimeWindow(
					startTime.getJavaDate(TimeZone.getDefault().getRawOffset()/60000), true,
					endTime.getJavaDate(TimeZone.getDefault().getRawOffset()/60000),true));
		}
		TimeWindow tw = null;
		if (timewindows.size() > 0) {
			tw = timewindows.get(0);
		}
		String output_file = scalars.get("OUTFILE");
		writer = new ReportPDFWriter();
		writer.startDocument(output_file);
		String author = scalars.get("MODELER").replace("\"", "");
		writer.addTitlePage(
		        String.format("System Report: %s vs %s", scalars.get("NAME_ALT"), scalars.get("NAME_BASE")), author,
		        scalars.get("FILE_BASE"), scalars.get("FILE_ALT"));
		writer.setAuthor(author);

		monitor.worked(20);
		
		generateSummaryTable(monitor, htsBase, baseAPart, baseFPart,
				htsAlt, altAPart, altFPart, timewindows);

		int dataIndex = 0;
		TimeSeriesContainer tscBase = null;
		TimeSeriesContainer tscAlt = null;
		String searchPathBase = "";
		String searchPathAlt = "";
		int size = pathnameMaps.size();
		for (PathnameMap pathMap : pathnameMaps) {
			dataIndex = dataIndex + 1;
			int progress=Math.round(dataIndex*30.0f/size)-Math.round((dataIndex-1)*30.0f/size);
			monitor.subTask("Generating plot " + dataIndex + " of " + size + ".");
			monitor.worked(progress);
			
			//logger.fine("Working on index: " + dataIndex);
			if (pathMap.pathAlt == null || pathMap.pathAlt == "") {
				pathMap.pathAlt = pathMap.pathBase;
			}
			boolean calculate_dts = false;
			if (pathMap.var_category.equals("HEADER")) {
				//logger.fine("Inserting header");
				continue;
			}
			if (pathMap.report_type.endsWith("_post")) {
				calculate_dts = true;
			}
			tscBase = null;
			tscAlt = null;
			if (!pathMap.pathAlt.equalsIgnoreCase("ignore")) {
				searchPathBase = Utils.substitutePartIntoPath(pathMap.pathBase, baseAPart, 1);
				searchPathBase = Utils.substitutePartIntoPath(searchPathBase, baseFPart, 6);
				tscBase = Utils.getTSContainer(htsBase, searchPathBase, calculate_dts);
			}
			if (!pathMap.pathAlt.equalsIgnoreCase("ignore")) {
				searchPathAlt = Utils.substitutePartIntoPath(pathMap.pathAlt, altAPart, 1);
				searchPathAlt = Utils.substitutePartIntoPath(searchPathAlt, altFPart, 6);
				tscAlt = Utils.getTSContainer(htsAlt, searchPathAlt, calculate_dts);
			}			if (tscBase == null || tscAlt == null) {
				continue;
			}
			String[] series_name = new String[] { scalars.get("NAME_ALT"), scalars.get("NAME_BASE") };
			if (pathMap.units.equalsIgnoreCase("CFS2TAF")) {
				tscBase=Utils.cfs2taf(tscBase);
				tscAlt=Utils.cfs2taf(tscAlt);
			} else if (pathMap.units.equalsIgnoreCase("TAF2CFS")) {
				tscBase=Utils.taf2cfs(tscBase);
				tscAlt=Utils.taf2cfs(tscAlt);
			}
			String data_units = tscBase.units;
			String data_type = tscBase.type;
			
			if (pathMap.plot) {
				if (pathMap.report_type.startsWith("average")) {
					generatePlot(Utils.buildDataArray(tscAlt, tscBase, tw),
							dataIndex, "Average "
									+ pathMap.var_name.replace("\"", ""),
							series_name, data_type + "(" + data_units + ")",
							"Time", PlotType.TIME_SERIES);
				} else if (pathMap.report_type.startsWith("exceedance")) {
					generatePlot(Utils.buildExceedanceArray(tscAlt, tscBase,
							getMonth(pathMap.var_category), tw), dataIndex,
							Utils.getExceedancePlotTitle(pathMap), series_name,
							data_type + "(" + data_units + ")",
							"Percent at or above", PlotType.EXCEEDANCE);
				} else if (pathMap.report_type.startsWith("avg_excd")) {
					generatePlot(Utils.buildDataArray(tscAlt, tscBase, tw),
							dataIndex, "Average "
									+ pathMap.var_name.replace("\"", ""),
							series_name, data_type + "(" + data_units + ")",
							"Time", PlotType.TIME_SERIES);
					generatePlot(Utils.buildExceedanceArray(tscAlt, tscBase,
							getMonth(pathMap.var_category), tw), dataIndex,
							Utils.getExceedancePlotTitle(pathMap), series_name,
							data_type + "(" + data_units + ")",
							"Percent at or above", PlotType.EXCEEDANCE);
				} else if (pathMap.report_type.startsWith("timeseries")) {
					generatePlot(Utils.buildDataArray(tscAlt, tscBase, tw),
							dataIndex, "Average "
									+ pathMap.var_name.replace("\"", ""),
							series_name, data_type + "(" + data_units + ")",
							"Time", PlotType.TIME_SERIES);
				} else if (pathMap.report_type.equals("alloc")) {
					generatePlot(Utils.buildExceedanceArray(tscAlt, tscBase,
							9, tw), dataIndex, "Exceedance "
							+ pathMap.var_name.replace("\"", ""), series_name,
							"Allocation (%)", "Probability",
							PlotType.EXCEEDANCE);
				} else if (pathMap.report_type.equals("month_avg")){
					generatePlot(Utils.buildMonthlyAverages(tscAlt, tscBase, tw),
							dataIndex, "Monthly Average "
									+ pathMap.var_name.replace("\"", ""),
							series_name, data_type + "(" + data_units + ")",
							"Time", PlotType.TIME_SERIES);
				}
			}
		}
		writer.endDocument();
	}

	private void generateSummaryTable(
			IProgressMonitor monitor, 
			HecTimeSeries htsBase,
			String baseAPart,
			String baseFPart,
			HecTimeSeries htsAlt,
			String altAPart,
			String altFPart,
			ArrayList<TimeWindow> timewindows) {
		
		writer.setTableFontSize(scalars.get("TABLE_FONT_SIZE"));
		
		
		writer.addTableTitle(String.format("System Flow Comparision: %s vs %s",
				scalars.get("NAME_ALT"), scalars.get("NAME_BASE")));
		writer.addTableSubTitle(scalars.get("NOTE").replace("\"", ""));
		writer.addTableSubTitle(scalars.get("ASSUMPTIONS").replace("\"", ""));
		writer.addTableSubTitle(" ");  // add empty line to increase space between title and table
		
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
		int size = pathnameMaps.size();
		int dataIndex = 0;
		for (PathnameMap pathMap : pathnameMaps) {
			dataIndex++;
			monitor.subTask("Processing dataset " + dataIndex + " of " + size);
			int progress=Math.round(dataIndex*30.0f/size)-Math.round((dataIndex-1)*30.0f/size);
			monitor.worked(progress);
			
			if (!categoryList.contains(pathMap.var_category)) {
				continue;
			}
			ArrayList<String> rowData = new ArrayList<String>();
			rowData.add(pathMap.var_name);
			boolean calculate_dts = false;
			if (pathMap.report_type.toLowerCase().endsWith("_post")) {
				calculate_dts = true;
			}
			
			TimeSeriesContainer tscBase = null, tscAlt = null;
			String searchpath = null;
			if (!pathMap.pathBase.equalsIgnoreCase("ignore")) {
				searchpath = Utils.substitutePartIntoPath(pathMap.pathBase, baseAPart, 1);
				searchpath = Utils.substitutePartIntoPath(searchpath, baseFPart, 6);
				tscBase = Utils.getTSContainer(htsBase, searchpath, calculate_dts);
			}
			if (!pathMap.pathAlt.equalsIgnoreCase("ignore")) {
				searchpath = Utils.substitutePartIntoPath(pathMap.pathAlt, altAPart, 1);
				searchpath = Utils.substitutePartIntoPath(searchpath, altFPart, 6);
				tscAlt = Utils.getTSContainer(htsAlt, searchpath, calculate_dts);
			}
			for (TimeWindow tw : timewindows) {
				double avgBase = 0, avgAlt = 0;
				if (tscAlt != null) {
					if (pathMap.units.equalsIgnoreCase("TAF2CFS")){
						avgAlt = Utils.avg(Utils.taf2cfs(tscAlt), tw)/12.0;
					}else if (pathMap.units.equalsIgnoreCase("CFS2TAF")){
						avgAlt = Utils.avg(Utils.cfs2taf(tscAlt), tw);
					}else if (tscAlt.units.equalsIgnoreCase("TAF")){
						avgAlt = Utils.avg(tscAlt, tw);
					}else{
						avgAlt = Utils.avg(tscAlt, tw)/12.0;
					}
					rowData.add(formatDoubleValue(avgAlt));
				} else {
					rowData.add("");
				}
				if (tscBase != null) {
					if (pathMap.units.equalsIgnoreCase("TAF2CFS")){
						avgBase = Utils
								.avg(Utils.taf2cfs(tscBase), tw)/12.0;
					}else if(pathMap.units.equalsIgnoreCase("CFS2TAF")){
						avgBase = Utils
							.avg(Utils.cfs2taf(tscBase), tw);
					}else if (tscBase.units.equalsIgnoreCase("TAF")){
						avgBase = Utils.avg(tscBase, tw);
					}else{
						avgBase = Utils.avg(tscBase, tw)/12.0;
					}
					rowData.add(formatDoubleValue(avgBase));
				} else {
					rowData.add("");
				}
				if (tscBase == null || tscAlt == null) {
					rowData.add("");
					rowData.add("");
				} else {
					double diff = avgAlt - avgBase;
					double pctDiff = Double.NaN;
					if (avgBase != 0) {
						pctDiff = diff / Math.abs(avgBase) * 100;
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
			//logger.warning(msg);
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
		public String var_category;
		public String var_name;
		String row_type;
		String units;
		boolean plot;

		public PathnameMap(String var_name) {
			this.var_name = var_name;
		}
	}

	public String getOutputFile() {
		return scalars.get("OUTFILE");
	}
	
	public int getMonth(String var_category){
		if (var_category.equalsIgnoreCase("S_Jan")){
			return 1;
		}else if (var_category.equalsIgnoreCase("S_Feb")){
			return 2;
		}else if (var_category.equalsIgnoreCase("S_Mar")){
			return 3;
		}else if (var_category.equalsIgnoreCase("S_Apr")){
			return 4;
		}else if (var_category.equalsIgnoreCase("S_May")){
			return 5;
		}else if (var_category.equalsIgnoreCase("S_Jun")){
			return 6;
		}else if (var_category.equalsIgnoreCase("S_Jul")){
			return 7;
		}else if (var_category.equalsIgnoreCase("S_Aug")){
			return 8;
		}else if (var_category.equalsIgnoreCase("S_Sep")){
			return 9;
		}else if (var_category.equalsIgnoreCase("S_Sept")){
			return 9;
		}else if (var_category.equalsIgnoreCase("S_Oct")){
			return 10;
		}else if (var_category.equalsIgnoreCase("S_Nov")){
			return 11;
		}else if (var_category.equalsIgnoreCase("S_Dec")){
			return 12;
		}
		return 0;
	}
}
