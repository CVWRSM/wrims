package gov.ca.dwr.hecdssvue.views;

import gov.ca.dwr.hecdssvue.Activator;
import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.DataOps;
import hec.gfx2d.G2dMouseAdapter;
import hec.gfx2d.G2dObject;
import hec.gfx2d.G2dPanel;
import hec.gfx2d.G2dZoomAdapter;
import hec.gfx2d.PairedDataSet;
import hec.gfx2d.TimeSeriesDataSet;
import hec.gfx2d.Viewport;
import hec.heclib.util.HecTime;
import hec.io.DataContainer;
import hec.io.PairedDataContainer;
import hec.io.TimeSeriesContainer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.statushandlers.StatusManager;

import wrimsv2_plugin.tools.TimeOperation;

/**
 * Displays a plot in a view based on selection on DSS Catalog View
 * 
 * @author psandhu
 * 
 */
public class DSSPlotView extends AbstractDSSView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.ca.dwr.hecdssvue.views.DSSPlotView";
	G2dPanel plot;
	private Action copyAction;
	private Viewport[] viewPorts;

	/**
	 * The constructor.
	 */
	public DSSPlotView() {
		
	}

	public void createPartControl(Composite parent){
		super.createPartControl(parent);
		makeActions();
		contributeToActionBars();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	/**
	 * Show plot for selected 
	 */
	public void showSelected(Vector<DataContainer> dataVector) {
		if (plot != null) {
			//plot.clearPanel();
		}
		if (plot == null) {
			plot = new G2dPanel();
			//plot.setPaintEnabled(false);
			//plot.setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);
			contentPane.add(plot);
		}
		G2dObject g2dObj = null;
		Vector g2dataVector = new Vector();
		for (Iterator iterator = dataVector.iterator(); iterator.hasNext();) {
			DataContainer data = (DataContainer) iterator.next();
			if (data instanceof TimeSeriesContainer) {
				if (DssPluginCore.chartType==0){
					TimeSeriesContainer ntsc = DataOps.getMonthlyData((TimeSeriesContainer)data, DssPluginCore.months);
					TimeSeriesDataSet ts = new TimeSeriesDataSet(
						(TimeSeriesContainer) ntsc);
					if (((TimeSeriesContainer) ntsc).timeZoneID != null) {
						ts.setGmtOffset(((TimeSeriesContainer) ntsc).timeZoneRawOffset
							/ (1000 * 60 * 60));
					}
					g2dObj = ts;
				}else if (DssPluginCore.chartType==1){
					PairedDataContainer ntsc = getExceedence(DataOps.getMonthlyData((TimeSeriesContainer)data, DssPluginCore.months));
					PairedDataSet ts = new PairedDataSet(
						(PairedDataContainer) ntsc);
					g2dObj = ts;
				}else if (DssPluginCore.chartType==2){
					TimeSeriesContainer ntsc = getAnnualTotals(DataOps.getMonthlyData((TimeSeriesContainer)data, DssPluginCore.months));
					TimeSeriesDataSet ts = new TimeSeriesDataSet(
						(TimeSeriesContainer) ntsc);
					if (((TimeSeriesContainer) ntsc).timeZoneID != null) {
						ts.setGmtOffset(((TimeSeriesContainer) ntsc).timeZoneRawOffset
							/ (1000 * 60 * 60));
					}
					g2dObj = ts;
				}else if (DssPluginCore.chartType==3){
					PairedDataContainer ntsc = getExceedence(getAnnualTotals(DataOps.getMonthlyData((TimeSeriesContainer)data, DssPluginCore.months)));
					PairedDataSet ts = new PairedDataSet(
						(PairedDataContainer) ntsc);
					g2dObj = ts;
				}else if (DssPluginCore.chartType==4){
					TimeSeriesContainer ntsc = getMonthlyAverages(DataOps.getMonthlyData((TimeSeriesContainer)data, DssPluginCore.months));
					TimeSeriesDataSet ts = new TimeSeriesDataSet(
						(TimeSeriesContainer) ntsc);
					if (((TimeSeriesContainer) ntsc).timeZoneID != null) {
						ts.setGmtOffset(((TimeSeriesContainer) ntsc).timeZoneRawOffset
							/ (1000 * 60 * 60));
					}
					g2dObj = ts;
				}else{
					return;
				}
			} else if (data instanceof PairedDataContainer) {
				PairedDataSet pd = new PairedDataSet((PairedDataContainer) data);
				g2dObj = pd;
			}
			if (g2dObj == null) {
				return;
			}
			g2dataVector.add(g2dObj);

		}
		plot.buildComponents(g2dataVector, false, true);
		viewPorts = plot.getViewports();
		if (viewPorts.length>0) {
			plot.setMouseAdapter(new G2dZoomAdapter(viewPorts[0], plot));
		};
	}
	
	public PairedDataContainer getExceedence(TimeSeriesContainer tsc) {

		PairedDataContainer pdc = null;
		try {
			double[] values = tsc.values;

			// System.out.println("value"+values[0]);
			Arrays.sort(values);
			// System.out.println("value"+values[0]);

			double[] xvalues = new double[values.length];
			double[][] yvalues = new double[1][values.length];
			for (int i = 0; i < values.length; i++) {
				xvalues[i] = (double) (values.length - i) / values.length;
				yvalues[0][i] = values[i];
				// System.out.println(xvalues[i]+" "+values[i]);
			}

			// make an exceedence container
			pdc = new PairedDataContainer();

			// pdc.parameter = tsc.parameter;
			pdc.fullName = tsc.fullName;
			pdc.version = tsc.version;
			pdc.location = tsc.location;

			pdc.xOrdinates = xvalues;
			pdc.yOrdinates = yvalues;

			pdc.numberOrdinates = xvalues.length;
			pdc.numberCurves = 1;

			pdc.xunits = "Probability";
			pdc.yunits = tsc.units;

			pdc.xparameter = "Exceedence";
			pdc.yparameter = tsc.parameter;

		} catch (Exception e) {
		}

		return pdc;
	}

	public TimeSeriesContainer getAnnualTotals(TimeSeriesContainer tsc) {
		TimeSeriesContainer ntsc = null;
		HecTime ht = null;
		double[] values = null;
		int[] times = null;

		try {
			List<Double> lvalues = new ArrayList<Double>();
			List<Integer> ltimes = new ArrayList<Integer>();

			values = tsc.values;
			times = tsc.times;

			ht = new HecTime();

			int year = 0;
			int year_prev = 0;
			// int time_prev = times[0];
			double total = 0.0;
			// CB added block to handle cases where first month is not first
			// month of year type (i.e., partial years not including first
			// month)
			// CB maximum of 12 periods to check (for monthly data only) TODO
			// fix if daily or other is added
			int firstMonth = 24;
			int firstPossibleMonth;
			if (DssPluginCore.annualType == DssPluginCore.WATERYEAR) {
				firstPossibleMonth = 10;
			} else if (DssPluginCore.annualType == DssPluginCore.CALENDAR_YEAR) {
				firstPossibleMonth = 1;
			} else if (DssPluginCore.annualType == DssPluginCore.FEDERAL_CONTRACT_YEAR) {
				firstPossibleMonth = 3;
			} else {
				return null;
			}

			HecTime htTemp = new HecTime();
			int month;
			for (int j = 0; j < 12; j++) {
				htTemp.set(times[j]);
				// WRONG MONTH FIX: subtract 1 min
				htTemp.add(-1);
				month = htTemp.month();
				if (month < firstPossibleMonth) {
					month += 12;
				}
				if (month < firstMonth) {
					firstMonth = month;
				}
			}
			if (firstMonth > 12) {
				firstMonth -= 12;
			}

			for (int i = 0; i < values.length; i++) {
				ht.set(times[i]);
				// if (DEBUG)
				// System.out.println(ht.year()+" "+ht.month()+" "+values[i]);

				// WRONG MONTH FIX: subtract 1 min
				ht.add(-1);
				if (DssPluginCore.annualType == DssPluginCore.WATERYEAR) { // CB added
					// annual
					// types
					if ((year_prev == 0) && (ht.month() != firstMonth)) {
						continue; // CB added to not use incomplete first
						// wateryear (i.e., data start is NOT Oct.)
					}
					if ((ht.month() == 10) || (ht.month() == 11)
							|| (ht.month() == 12)) {
						year = ht.year() + 1;
					} else {
						year = ht.year();
					}
					if ((year_prev != 0) && (year > year_prev)) {
						lvalues.add(total);
						// End of water year
						ht.set("30 September " + year_prev, "24:00");
						ltimes.add(ht.value());
						total = 0.0;
					}
					total += values[i];
					year_prev = year;
				} else if (DssPluginCore.annualType == DssPluginCore.CALENDAR_YEAR) { // CB
					// added
					// this
					// section
					if ((year_prev == 0) && (ht.month() != firstMonth)) {
						continue; // CB added to not use incomplete first
						// calendar year (i.e., data start is NOT
						// Jan.)
					}
					year = ht.year();
					if ((year_prev != 0) && (year > year_prev)) {
						lvalues.add(total);
						// End of calendar year
						ht.set("31 December " + year_prev, "24:00");
						ltimes.add(ht.value());
						total = 0.0;
					}
					total += values[i];
					year_prev = year;
				} else if (DssPluginCore.annualType == DssPluginCore.FEDERAL_CONTRACT_YEAR) { // CB
					// added
					// this
					// section
					if ((year_prev == 0) && (ht.month() != firstMonth)) {
						continue; // CB added to not use incomplete first
						// wateryear (i.e., data start is NOT March)
					}
					if ((ht.month() == 1) || (ht.month() == 2)) {
						year = ht.year() - 1;
					} else {
						year = ht.year();
					}
					if ((year_prev != 0) && (year > year_prev)) {
						lvalues.add(total);
						// End of contract year
						if (TimeOperation.numberOfDays(2, year) == 29) {
							ht.set("29 February " + year, "24:00");
						} else {
							ht.set("28 February " + year, "24:00");
						}
						ltimes.add(ht.value());
						total = 0.0;
					}
					total += values[i];
					year_prev = year;
				}
			}
			// CB added block to handle cases where last month is not last month
			// of year type (i.e., partial years not including last month)
			// CB because we need to add the last "year" of data if it is a
			// "full" year
			// CB maximum of 12 periods to check (for monthly data only) TODO
			// fix if daily or other is added
			int lastMonth = 0;
			int lastPossibleMonth = firstPossibleMonth - 1;
			if (lastPossibleMonth < 1) {
				lastPossibleMonth += 12;
			}
			htTemp = new HecTime();
			for (int j = values.length - 1; j >= 0; --j) {
				htTemp.set(times[j]);
				// WRONG MONTH FIX: subtract 1 min
				htTemp.add(-1);
				month = htTemp.month();
				if (month > lastPossibleMonth) {
					month -= 12;
				}
				if (month > lastMonth) {
					lastMonth = month;
				}
			}
			if (lastMonth < 1) {
				lastMonth += 12;
			}
			if (ht.month() == lastMonth) { // if a "full" year (actually if the
				// last year's data size is the same
				// size as an interior year's data
				// size)
				lvalues.add(total);
				if (lastPossibleMonth == 9) {
					ht.set("30 September " + year_prev, "24:00");
				} else if (lastPossibleMonth == 12) {
					ht.set("31 December " + year_prev, "24:00");
				} else if (lastPossibleMonth == 2) {
					if (TimeOperation.numberOfDays(2, year) == 29) {
						ht.set("29 February " + year, "24:00");
					} else {
						ht.set("28 February " + year, "24:00");
					}
				}
				ltimes.add(ht.value());
				total = 0.0;
			}

			// Build the annual time-series container
			double[] nvalues = new double[lvalues.size()];
			int[] ntimes = new int[ltimes.size()];

			for (int i = 0; i < lvalues.size(); i++) {
				ntimes[i] = ltimes.get(i).intValue();
				nvalues[i] = lvalues.get(i).doubleValue();
				// System.out.println(ntimes[i]+" "+nvalues[i]);
			}

			ntsc = (TimeSeriesContainer) tsc.clone();
			ntsc.values = nvalues;
			ntsc.numberValues = nvalues.length;
			ntsc.times = ntimes;
			ntsc.startTime = ntimes[0];
			ntsc.endTime = ntimes[ntimes.length - 1];
			ntsc.fullName = tsc.fullName.replace("1MON", "1YEAR");
			ntsc.type = "ANN-TOT";
			ntsc.interval = -1; // irregular time-series

		} catch (Exception e) {
		}

		return ntsc;
	}

	public TimeSeriesContainer getMonthlyAverages(TimeSeriesContainer tsc) {
		TimeSeriesContainer ntsc = null;
		HecTime ht = null;
		double[] values = null;
		int[] times = null;

		try {
			List<Double>[] lvalues; // = new ArrayList<Double>[];
			List<Integer>[] ltimes; // = new ArrayList<Integer>[];

			values = tsc.values;
			times = tsc.times;

			ht = new HecTime();

			// int year = 0;
			// int year_prev = 0;
			// int time_prev = times[0];
			int firstMonthIndex = -1;
			double[] totals;
			// CB added block to handle cases where first month is not first
			// month of year type (i.e., partial years not including first
			// month)
			// CB maximum of 12 periods to check (for monthly data only) TODO
			// fix if daily or other is added
			int firstMonth = 24;
			int firstPossibleMonth=firstPossibleMonth = 10;
			

			HecTime htTemp = new HecTime();
			int month;
			// int monthOrder[];
			Hashtable<Integer, Double> monthlyTotals = new Hashtable<Integer, Double>(); // Hashtable
			// of
			// <index,
			for (int j = 0; j < 12; j++) {
				htTemp.set(times[j]);
				// WRONG MONTH FIX: subtract 1 min
				htTemp.add(-1);
				month = htTemp.month();
				if (month < firstPossibleMonth) {
					month += 12;
				}
				if (month < firstMonth) {
					firstMonth = month;
				}
			}
			if (firstMonth > 12) {
				firstMonth -= 12;
			}

			int numUniqueMonths = 0;
			int firstDataMonth = 0;
			boolean stopCount = false;
			Hashtable<Integer, Integer> monthToIndexMapping = new Hashtable<Integer, Integer>();
			for (int j = 0; j < 12; j++) { // TODO nedd to consider consider
				// less than 12 months ON THIS LINE
				// ???????
				htTemp.set(times[j]);
				// WRONG MONTH FIX: subtract 1 min
				htTemp.add(-1);
				if (j == 0) {
					firstDataMonth = htTemp.month();
				} else if (firstDataMonth == htTemp.month()) {
					break;
				}
				monthToIndexMapping.put(htTemp.month(), j);
				++numUniqueMonths;
				if (htTemp.month() == firstMonth) {
					firstMonthIndex = j;
					// break;
				}
			}

			lvalues = new ArrayList[numUniqueMonths];
			ltimes = new ArrayList[numUniqueMonths];
			for (int i = 0; i < lvalues.length; ++i) {
				lvalues[i] = new ArrayList<Double>();
				ltimes[i] = new ArrayList<Integer>();
			}

			for (int i = 0; i < values.length; ++i) {
				ht.set(times[i]);
				// if (DEBUG)
				// System.out.println(ht.year()+" "+ht.month()+" "+values[i]);
				// WRONG MONTH FIX: subtract 1 min
				ht.add(-1);
				int index = monthToIndexMapping.get(ht.month());
				lvalues[index].add(values[i]);
				ltimes[index].add(ht.value());
			}

			// Build the annual time-series container
			double[] averages = new double[lvalues.length];
			int[] ntimes = new int[ltimes.length];

			for (int i = 0; i < lvalues.length; ++i) {
				for (int j = 0; j < ltimes[i].size(); ++j) {
					// int total = 0;
					if (i >= lvalues.length - numUniqueMonths - 1) {
						;
					}
					ntimes[i] = ltimes[i].get(j);
					averages[i] += lvalues[i].get(j).doubleValue();
					// System.out.println(ntimes[i]+" "+nvalues[i]);
				}
				averages[i] /= lvalues[i].size();
			}

			ntsc = (TimeSeriesContainer) tsc.clone();
			// ntsc.values = nvalues;
			ntsc.values = averages;
			// ntsc.numberValues = nvalues.length;
			ntsc.numberValues = averages.length;
			ntsc.times = ntimes;
			ntsc.startTime = ntimes[0];
			ntsc.endTime = ntimes[ntimes.length - 1];
			// ntsc.fullName = tsc.fullName.replace("1MON", "1YEAR");
			ntsc.type = "MON-AVG";
			ntsc.interval = -1; // irregular time-series

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ntsc;
	}
	
	private void contributeToActionBars() {
		IViewSite vs = getViewSite();
		IActionBars bars = vs.getActionBars();
		//fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		IContributionItem[] items = manager.getItems();
		manager.add(copyAction);
	}
	
	private void makeActions() {
		copyAction = new Action() {
			public void run() {
				BufferedImage bi = new
					    BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics g = bi.getGraphics();
				plot.printAll(g);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				TransferableImage selection = new TransferableImage(bi);
				clipboard.setContents(selection, null);
			}
		};
		copyAction.setText("Copy");
		copyAction.setToolTipText("Copy");
		copyAction.setImageDescriptor(Activator
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	}

    private class TransferableImage implements Transferable {

        Image i;

        public TransferableImage( Image i ) {
            this.i = i;
        }

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) {
                return i;
            }
            else {
                throw new UnsupportedFlavorException( flavor );
            }
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[ 1 ];
            flavors[ 0 ] = DataFlavor.imageFlavor;
            return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for ( int i = 0; i < flavors.length; i++ ) {
                if ( flavor.equals( flavors[ i ] ) ) {
                    return true;
                }
            }

            return false;
		}
    }
}