package gov.ca.dwr.hecdssvue.views;

import gov.ca.dwr.hecdssvue.panel.OpsPanel;
import hec.gfx2d.G2dMouseAdapter;
import hec.gfx2d.G2dObject;
import hec.gfx2d.G2dPanel;
import hec.gfx2d.G2dZoomAdapter;
import hec.gfx2d.PairedDataSet;
import hec.gfx2d.TimeSeriesDataSet;
import hec.gfx2d.Viewport;
import hec.io.DataContainer;
import hec.io.PairedDataContainer;
import hec.io.TimeSeriesContainer;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

/**
 * Displays a plot in a view based on selection on DSS Catalog View
 * 
 * @author psandhu
 * 
 */
public class DSSOpsView extends AbstractDSSView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.ca.dwr.hecdssvue.views.DSSOpsView";
	private OpsPanel options;

	/**
	 * The constructor.
	 */
	public DSSOpsView() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	/**
	 * Show plot for selected 
	 */
	public void showSelected(Vector<DataContainer> dataVector) {

	}
	
	public void createPartControl(Composite parent){
		super.createPartControl(parent);
		options = new OpsPanel();
		contentPane.add(options);
	}
	
	public OpsPanel getOpsPanel(){
		return options;
	}
}