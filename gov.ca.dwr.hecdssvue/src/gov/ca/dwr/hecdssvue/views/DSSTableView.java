package gov.ca.dwr.hecdssvue.views;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.components.DataOps;
import hec.dataTable.HecDataTable;
import hec.dataTable.TimeSeriesDataModel;
import hec.hecmath.DSS;
import hec.hecmath.DSSFile;
import hec.hecmath.HecMathException;
import hec.io.DataContainer;

import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.eclipse.swt.widgets.Composite;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;

/**
 * Displays a table view of data based on selection on DSSCatalogView
 * <p>
 */

public class DSSTableView extends AbstractDSSView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.ca.dwr.hecdssvue.views.DSSTableView";
	JScrollPane pane;
	HecDataTable table;

	/**
	 * The constructor.
	 */
	public DSSTableView() {
	}

	@Override
	/**
	 * Show plot for selected 
	 */
	public void showSelected(Vector<DataContainer> dataVector) {
		if (table !=null) DataOps.saveData(table);
		
		for (int i=dataVector.size()-1; i>=0; i--){
			if (dataVector.get(i)==null){
				dataVector.remove(i);
			}
		}

		if (dataVector != null && dataVector.size() > 0) {
			contentPane.removeAll();
			table = new HecDataTable(contentPane);
			table.setDateTimeAsTwoColumns(false);
			table.setData(dataVector, true, 0);//TODO
			table.setEditable(DssPluginCore.dssEditable);
			table.setDragEnabled(true);
			contentPane.add(new JScrollPane(table));
			contentPane.invalidate();
			contentPane.doLayout();
			contentPane.repaint();
		}
	}

	public void createPartControl(Composite parent){
		super.createPartControl(parent);
		//contentPane.add(comp);
	}
	
	public HecDataTable getTable(){
		return table;
	}
}