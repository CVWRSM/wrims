package wrims.schematic;

import java.awt.Container;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import wrims.dss.DssViewer;

/**
 * Define a schematic-fired Action that communicates with the DSS Viewer
 * directly (for popupup menus).
 * 
 * @author Clay Booher
 */
public abstract class DssFrameRelatedAction extends SchematicRelatedAction {

	public final int STORAGE = 0;

	public final int INFLOWS = 1;

	public final int OUTFLOWS = 2;

	public final int EVAP = 3;

	public final int AREA = 4;

	public final int SPILL = 5;

	public final int FLOW = 6;

	public final static String STORAGE_STRING = "Storage (EOP)";

	public final static String INFLOWS_STRING = "Inflows";

	public final static String OUTFLOWS_STRING = "Outflows";

	public final static String EVAP_STRING = "Evaporation";

	public final static String AREA_STRING = "Surface Area";

	public final static String SPILL_STRING = "Non-recoverable Spill";

	public final static String FLOW_STRING = "Flow";

	public final static String STORAGE_ABBREV = "S";

	public final static String EVAP_ABBREV = "E";

	public final static String AREA_ABBREV = "A";

	public final static String SPILL_ABBREV = "F";

	private Hashtable _typeHashtable;

	private Hashtable _abbrevHashtable;

	public DssFrameRelatedAction(String name, Container app) {
		super(name, app);
		createHashtables();
	}

	public DssFrameRelatedAction(String name, Icon icon, Container app) {
		super(name, icon, app);
		createHashtables();
		createAbbrevHashtable();
	}

	private void createHashtables() {
		createTypeHashtable();
		createAbbrevHashtable();
	}

	private void createTypeHashtable() {
		_typeHashtable = new Hashtable(9);
		_typeHashtable.put(STORAGE_STRING, new Integer(STORAGE));
		_typeHashtable.put(INFLOWS_STRING, new Integer(INFLOWS));
		_typeHashtable.put(OUTFLOWS_STRING, new Integer(OUTFLOWS));
		_typeHashtable.put(EVAP_STRING, new Integer(EVAP));
		_typeHashtable.put(AREA_STRING, new Integer(AREA));
		_typeHashtable.put(SPILL_STRING, new Integer(SPILL));
		_typeHashtable.put(FLOW_STRING, new Integer(FLOW));
	}

	private void createAbbrevHashtable() {
		_abbrevHashtable = new Hashtable(4);
		_abbrevHashtable.put(new Integer(STORAGE), STORAGE_ABBREV);
		_abbrevHashtable.put(new Integer(EVAP), EVAP_ABBREV);
		_abbrevHashtable.put(new Integer(AREA), AREA_ABBREV);
		_abbrevHashtable.put(new Integer(SPILL), SPILL_ABBREV);
		//
	}

	public boolean canAct() {
		return getApp() != null && getApp() instanceof MainFrame
				&& ((MainFrame) getApp())._DssFrame != null;
	}

	boolean actionPerfomedHelper(String outputTypeText, String variableTypeText) {
		if (outputTypeText == null || variableTypeText == null
				|| outputTypeText.trim().length() == 0
				|| variableTypeText.trim().length() == 0)
			return false;
		// System.out.println("outputTypeText = " + outputTypeText);
		// System.out.println("variableTypeText = " + variableTypeText);
		if (DssViewer.getOutputType(outputTypeText) == null
				|| _typeHashtable.get(variableTypeText) == null)
			return false;
		int outputType = (Integer) DssViewer.getOutputType(outputTypeText);
		int variableType = (Integer) _typeHashtable.get(variableTypeText);
		boolean isExceedence = false;
		boolean isAnnualTotal = false;
		if (outputType == DssViewer.EXCEEDENCE) {
			isAnnualTotal = false;
			isExceedence = true;
		} else if (outputType == DssViewer.ANNUAL_TOTAL) {
			isExceedence = false;
			isAnnualTotal = true;
		} else if (outputType == DssViewer.ANNUAL_TOTAL_EXCEEDENCE) {
			isExceedence = true;
			isAnnualTotal = true;
		}
		Vector<String> names = ((MainFrame) getApp()).getViewer()
				.getSelectedNames();
		if (names.size() > 0) {
			((MainFrame) getApp())._DssFrame.getFP().retrieve(outputType,
					isExceedence, isAnnualTotal, names);
		} else {
			JOptionPane
					.showMessageDialog(
							((MainFrame) getApp())._DssFrame,
							"No variables in dss files that match any of the selected items",
							"Match Not Found", JOptionPane.WARNING_MESSAGE);
		}
		return true;
	}

}
