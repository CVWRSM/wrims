package gov.ca.dwr.hecdssvue.views;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.panel.MonthlyTablePanel;
import gov.ca.dwr.hecdssvue.panel.OpsPanel;
import hec.gfx2d.G2dMouseAdapter;
import hec.gfx2d.G2dObject;
import hec.gfx2d.G2dPanel;
import hec.gfx2d.G2dZoomAdapter;
import hec.gfx2d.PairedDataSet;
import hec.gfx2d.TimeSeriesDataSet;
import hec.gfx2d.Viewport;
import hec.hecmath.DSSFile;
import hec.hecmath.HecMath;
import hec.io.DataContainer;
import hec.io.PairedDataContainer;
import hec.io.TimeSeriesContainer;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

/**
 * Displays a plot in a view based on selection on DSS Catalog View
 * 
 * @author psandhu
 * 
 */
public class DSSMonthlyView extends AbstractDSSView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.ca.dwr.hecdssvue.views.DSSMonthlyView";
	private MonthlyTablePanel monthly;

	/**
	 * The constructor.
	 */
	public DSSMonthlyView() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	/**
	 * Show plot for selected 
	 */
	public void showSelected(final Vector<DataContainer> dataVector) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadMonthlyTable(DssPluginCore.annualType, dataVector);
				monthly.getTable().setupModel();
				monthly.showTable();
			}});
	}
	
	public void createPartControl(Composite parent){
		super.createPartControl(parent);
		monthly = new MonthlyTablePanel(_frame);
		DssPluginCore.mtp=monthly;
		contentPane.add(monthly);
	}

	public void loadMonthlyTable(int annualType, Vector<DataContainer> dataVector) {

		int firstPossibleMonth;
		
		if (annualType == DssPluginCore.WATERYEAR) {
			firstPossibleMonth = 0;
		} else if (annualType == DssPluginCore.CALENDAR_YEAR) {
			firstPossibleMonth = 3;
		} else if (annualType == DssPluginCore.FEDERAL_CONTRACT_YEAR) {
			firstPossibleMonth = 5;
		} else {
			return;
		}
		
		monthly.setData(dataVector, firstPossibleMonth);
	}
	
	public MonthlyTablePanel getMonthlyTablePanel(){
		return monthly;
	}
}