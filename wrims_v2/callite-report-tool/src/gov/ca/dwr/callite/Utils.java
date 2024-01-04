package gov.ca.dwr.callite;

import gov.ca.dwr.callite.Report.PathnameMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import vista.db.dss.DSSUtil;
import vista.report.TSMath;
import vista.set.Constants;
import vista.set.DataReference;
import vista.set.DataSetElement;
import vista.set.ElementFilter;
import vista.set.ElementFilterIterator;
import vista.set.Group;
import vista.set.MultiIterator;
import vista.set.Pathname;
import vista.set.RegularTimeSeries;
import vista.set.Stats;
import vista.set.TimeSeries;
import vista.time.SubTimeFormat;
import vista.time.Time;
import vista.time.TimeFactory;
import vista.time.TimeWindow;

public class Utils {
	static StringBuffer messages = new StringBuffer();

	public static void clearMessages() {
		messages.setLength(0);
	}

	public static void addMessage(String msg) {
		messages.append(msg).append("\n");
	}

	public static String getMessages() {
		return messages.toString();
	}

	/**
	 * Retrieves the contents list for a dss file
	 * 
	 * @param filename
	 * @return a handle to the content listing for a dss file
	 */
	public static Group opendss(String filename) {
		return DSSUtil.createGroup("local", filename);
	}

	/**
	 * findpath(g,path,exact=1): this returns an array of matching data
	 * references g is the group returned from opendss function path is the
	 * dsspathname e.g. '//C6/FLOW-CHANNEL////' exact means that the exact
	 * string is matched as opposed to the reg. exp.
	 * 
	 * @param g
	 * @param path
	 * @param exact
	 * @return
	 */
	public static DataReference[] findpath(Group g, String path, boolean exact) {
		String[] pa = new String[6];
		for (int i = 0; i < 6; i++) {
			pa[i] = "";
		}
		int i = 0;
		for (String p : path.trim().split("/")) {
			if (i == 0) {
				i++;
				continue;
			}
			if (i >= pa.length) {
				break;
			}
			pa[i - 1] = p;
			if (exact) {
				pa[i - 1] = "^" + pa[i - 1] + "$";
			}
			i++;
		}
		return g.find(pa);
	}

	public static PathnameMap getPathMapForVarName(String varname,
			ArrayList<PathnameMap> pathname_maps) {
		for (PathnameMap x : pathname_maps) {
			if (x.var_name.equals(varname)) {
				return x;
			}
		}
		return null;
	}

	public static DataReference getReference(Group group, String path,
			boolean calculate_dts, ArrayList<PathnameMap> pathname_maps,
			int group_no) {
		if (calculate_dts) {
			try {
				// FIXME: add expression parser to enable any expression
				String bpart = path.split("/")[2];
				String[] vars = bpart.split("\\+");
				DataReference ref = null;
				for (String varname : vars) {
					DataReference xref = null;
					String varPath = createPathFromVarname(path, varname);
					xref = getReference(group, varPath, false, pathname_maps,
							group_no);
					if (xref == null) {
						throw new RuntimeException("Aborting calculation of "
								+ path + " due to previous path missing");
					}
					if (ref == null) {
						ref = xref;
					} else {
						ref = ref.__add__(xref);
					}
				}
				return ref;
			} catch (Exception ex) {
				Utils.addMessage(ex.getMessage());
				return null;
			}
		} else {
			try {
				DataReference[] refs = findpath(group, path, false);
				if (refs == null) {
					String msg = "No data found for " + group + " and " + path;
					addMessage(msg);
					System.err.println(msg);
					return null;
				} else {
					return refs[0];
				}
			} catch (Exception ex) {
				String msg = "Exception while trying to retrieve " + path
						+ " from " + group;
				System.err.println(msg);
				addMessage(msg);
				return null;
			}
		}
	}

	private static String createPathFromVarname(String path, String varname) {
		String[] parts = path.split("/");
		if (parts.length > 2) {
			parts[2] = varname;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			builder.append(parts[i]).append("/");
		}
		return builder.toString();
	}

	public static String getUnitsForReference(DataReference ref) {
		if (ref != null) {
			return ref.getData().getAttributes().getYUnits();
		}
		return "";
	}

	public static String getUnits(DataReference ref1, DataReference ref2) {
		if (ref1 == null) {
			if (ref2 == null) {
				return "";
			} else {
				return getUnitsForReference(ref2);
			}
		} else {
			return getUnitsForReference(ref1);
		}
	}

	public static String getTypeOfReference(DataReference ref) {
		if (ref != null) {
			Pathname p = ref.getPathname();
			return p.getPart(Pathname.C_PART);
		}
		return "";
	}

	public static String getType(DataReference ref1, DataReference ref2) {
		if (ref1 == null) {
			if (ref2 == null) {
				return "";
			} else {
				return getTypeOfReference(ref2);
			}
		} else {
			return getTypeOfReference(ref1);
		}
	}

	public static String getExceedancePlotTitle(PathnameMap path_map) {
		String title = "Exceedance " + path_map.var_name.replace("\"", "");
		if (path_map.var_category.equals("S_SEPT")) {
			title = title + " (Sept)";
		}
		return title;
	}

	public static Date convertToDate(Time time_val) {
		return new Date(time_val.getDate().getTime()
				- TimeZone.getDefault().getRawOffset());
	}

	public static MultiIterator buildMultiIterator(TimeSeries[] dsarray,
			ElementFilter filter) {
		if (filter == null) {
			return new MultiIterator(dsarray);
		} else {
			return new MultiIterator(dsarray, filter);
		}
	}

	public static String extractNameFromReference(DataReference ref) {
		Pathname p = ref.getPathname();
		return p.getPart(Pathname.C_PART) + " @ " + p.getPart(Pathname.B_PART);
	}

	public static ArrayList<double[]> buildDataArray(DataReference ref1,
			DataReference ref2, TimeWindow tw) {
		ArrayList<double[]> dlist = new ArrayList<double[]>();
		if (ref1 == null && ref2 == null) {
			return dlist;
		}
		TimeSeries data1 = (TimeSeries) ref1.getData();
		TimeSeries data2 = (TimeSeries) ref2.getData();
		if (tw != null) {
			data1 = data1.createSlice(tw);
			data2 = data2.createSlice(tw);
		}
		MultiIterator iterator = buildMultiIterator(new TimeSeries[] { data1,
				data2 }, Constants.DEFAULT_FLAG_FILTER);
		while (!iterator.atEnd()) {
			DataSetElement e = iterator.getElement();
			Date date = convertToDate(TimeFactory.getInstance().createTime(
					e.getXString()));
			dlist.add(new double[] { date.getTime(), e.getX(1), e.getX(2) });
			iterator.advance();
		}
		return dlist;
	}

	public static ArrayList<Double> sort(DataReference ref,
			boolean end_of_sept, TimeWindow tw) {
		TimeSeries data = (TimeSeries) ref.getData();
		if (tw != null) {
			data = data.createSlice(tw);
		}
		ArrayList<Double> dx = new ArrayList<Double>();
		ElementFilterIterator iter = new ElementFilterIterator(data
				.getIterator(), Constants.DEFAULT_FLAG_FILTER);
		while (!iter.atEnd()) {
			if (end_of_sept) {
				if (iter.getElement().getXString().indexOf("30SEP") >= 0) {
					dx.add(iter.getElement().getY());
				}
			} else {
				dx.add(iter.getElement().getY());
			}
			iter.advance();
		}
		Collections.sort(dx);
		return dx;
	}

	public static ArrayList<double[]> buildExceedanceArray(DataReference ref1,
			DataReference ref2, boolean end_of_sept, TimeWindow tw) {
		ArrayList<Double> x1 = sort(ref1, end_of_sept, tw);
		ArrayList<Double> x2 = sort(ref2, end_of_sept, tw);
		ArrayList<double[]> darray = new ArrayList<double[]>();
		int i = 0;
		int n = (int) Math.round(Math.min(x1.size(), x2.size()));
		while (i < n) {
			darray.add(new double[] { 100.0 - 100.0 * i / (n + 1), x1.get(i),
					x2.get(i) });
			i = i + 1;
		}
		return darray;
	}

	public static RegularTimeSeries cfs2taf(RegularTimeSeries data) {
		RegularTimeSeries data_taf = (RegularTimeSeries) TSMath
				.createCopy(data);
		TSMath.cfs2taf(data_taf);
		return data_taf;
	}

	public static double avg(RegularTimeSeries data, TimeWindow tw) {
		try {
			return Stats.avg(data.createSlice(tw)) * 12;
		} catch (Exception ex) {
			return Double.NaN;
		}
	}

	public static String formatTimeWindowAsWaterYear(TimeWindow tw) {
		SubTimeFormat year_format = new SubTimeFormat("yyyy");
		return tw.getStartTime().__add__("3MON").format(year_format) + "-"
				+ tw.getEndTime().__add__("3MON").format(year_format);
	}

	public static String formatTimeAsYearMonthDay(Time t) {
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(t.getDate());
		return gmtCal.get(Calendar.YEAR) + "," + gmtCal.get(Calendar.MONTH)
				+ "," + gmtCal.get(Calendar.DATE);
	}

	public static String formatAsOptionValue(TimeWindow tw) {
		return formatTimeAsYearMonthDay(tw.getStartTime()) + "-"
				+ formatTimeAsYearMonthDay(tw.getEndTime());
	}

}
