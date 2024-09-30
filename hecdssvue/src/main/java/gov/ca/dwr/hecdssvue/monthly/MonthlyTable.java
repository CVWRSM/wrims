package gov.ca.dwr.hecdssvue.monthly;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.TextListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import rma.swing.table.DecimalCellRenderer;
import rma.swing.text.FixedLengthDocument;
import rma.swing.IParameterScale;
import rma.swing.list.RmaListModel;
import rma.swing.table.RmaCellEditor;
import rma.swing.table.RmaCellRenderer;
import rma.swing.table.RmaColorRenderer;
import rma.swing.EditableComponent;
import rma.swing.FormManagementListener;
import rma.swing.InsertDlg;
import rma.swing.RmaJ24HourTimeField;
import rma.swing.RmaJCalendarField;
import rma.swing.RmaJCheckBox;
import rma.swing.RmaJColorComboBox;
import rma.swing.RmaJComboBox;
import rma.swing.RmaJDateField;
import rma.swing.RmaJDecimalField;
import rma.swing.RmaJDssPathPartField;
import rma.swing.RmaJFrame;
import rma.swing.RmaJIntegerField;
import rma.swing.RmaJLabel;
import rma.swing.RmaJTextArea;
import rma.swing.RmaJTextField;
import rma.swing.RmaValidComponent;
import rma.swing.table.RmaTableModel;
import rma.swing.ToggleDocument;
import rma.services.tz.TimeZoneComponent;
import rma.services.units.UnitsComponent;
import rma.swing.event.TableChangeListener;
import rma.swing.event.TableUpdateEvent;
import rma.swing.print.PageText;
import rma.swing.table.*;
import rma.util.RMAIO;
import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.monthly.MonthlyTableModel.SingleMonthlyTable;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import gov.ca.dwr.hecdssvue.views.DSSOpsView;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import hec.data.ParamDouble;
import hec.data.Parameter;
import hec.data.Units;
import hec.gfx2d.TimeSeriesDataSet;
import hec.heclib.util.HecDouble;
import hec.io.TimeSeriesContainer;
/**
 *  extension of the swing JTable that includes specialized editors/renderers
 *  popup menu, row and column headers etc. example of use: <p>
 *
 *  <pre>
 *	    JFrame frame = new JFrame();
 *	    Object[][] data = {
 *              {"Mary", "Campione",
 *               "Snowboarding", new Integer(5), new Boolean(false)},
 *              {"Alison", "Huml",
 *               "Rowing", new Integer(3), new Boolean(true)},
 *              {"Kathy", "Walrath",
 *               "Chasing toddlers", new Integer(2), new Boolean(false)},
 *              {"Mark", "Andrews",
 *               "Speed reading", new Integer(20), new Boolean(true)},
 *              {"Angela", "Lih",
 *               "Teaching high school", new Integer(4), new Boolean(false)}
 *          };
 *
 *          String[] columnNames = {"First\nName",
 *                                  "Last\nName",
 *                                  "Sport",
 *                                  "# of Years",
 *                                  "Vegetarian"};
 *          String[] columnNames2 = {"Name\nFirst",
 *                                   "Name\nLast",
 *                                   "Sport",
 *                                   "# of Years",
 *                                   "Vegetarian"};
 *          boolean[] enabled = { true, true, true, true, true };
 *	    //RmaJTable tbl = new RmaJTable(frame, data, columnNames);
 *	    RmaJTable tbl = new RmaJTable(frame, columnNames);
 *	    tbl.setCellRenderer();
 *	    tbl.setMlHeaderRenderer();
 *	    tbl.setCells(data);
 *      // to enable row headers
 *	    //tbl.setRowHeaderEnabled(true);
 *      // to enable auto row headers
 *	    //tbl.setAutoRowHeaders(true);
 *   	tbl.setCellSelectionEnabled(false);
 *		tbl.setRowSelectionAllowed(true);
 *      tbl.setColumnSelectionAllowed(false);
 *      Vector v = new Vector();
 *      for (int i = 0; i < data.length; i++ )
 *          v.addElement(data[i][2]);
 *      tbl.setComboBoxEditor(2, v);
 *
 *	    frame.getContentPane().add(tbl.getScrollPane(), BorderLayout.NORTH);
 *	    frame.setSize(300,600);
 *
 *
 *	    frame.setVisible(true);
 *	    //tbl.setNumRows(2);
 *	    //tbl.displayNumCol(3);
 *	    //tbl.displayNumCol(4);
 *	    tbl.setColumnLabels(columnNames2);
 *	    //tbl.setColumnEnabled(false, 1);
 * </pre> <p>
 *
 *
 *
 *@author     Mark Ackerman
 *@created    October 10, 2001
 */
public class MonthlyTable extends JTable
		 implements ActionListener, CellEditorListener, TextListener,
		KeyListener, MouseListener, DocumentListener, ItemListener,
		javax.swing.event.TableModelListener, Printable,
		rma.lang.Modifiable, FormManagementListener, EditableComponent, UnitsComponent, TimeZoneComponent,
		RmaValidComponent
{
	TablePrintManager _printManager;
	Component _parent = null;
	JPopupMenu _popup;
	boolean _modified = false;
	char kd, kp, kr, curr_char;
	JScrollPane _scrollPane;
	boolean addRemoveEnabled = true;
	boolean _popupEnabled = true;
	boolean _tabToEditCell = true;
//	RmaTableModelInterface _tableModel = null;
	RmaSelectionListener _colSL;
	RmaSelectionListener _rowSL;
	Vector _cellEditorListeners = new Vector();
	Vector _tableChangeListeners = new Vector();
	Vector _tableMinMaxEntries = new Vector();
	Hashtable _rowForeground = new Hashtable();
	/*
	 *  foreground color for rows
	 */
	Hashtable _rowBackground = new Hashtable();
	/*
	 *  background color for rows
	 */
	Hashtable _cellForeground = new Hashtable();
	// foreground color for cells
	Hashtable _cellBackground = new Hashtable();
	// font for cells
	Hashtable _cellFonts = new Hashtable();
	// background color for cells
	Hashtable _columnForeground = new Hashtable();
	// foreground color for columns
	Hashtable _columnBackground = new Hashtable();
	boolean _hasMultilineRenderer = false;   //boolean marking the fact that there is one multiline renderer
	// background color for columns
	boolean _rowHeaderEnabled = false;
	boolean _autoRowHeaders = false;
	int _autoRowHeaderOffset = 0;
	/** is the first row fixed? */
	protected boolean _firstFixedRow  = false;
	UnitsHeaderRenderer _unitsHeadRend = null;
	MleHeadRenderer _mleheadrend = null;
	CurrencyCellRenderer _currencyCellRend;
	RmaCellRenderer _rmaCellRend;
	DecimalCellRenderer _decCellRend;
	MultiLineCellRenderer _mlCellRend;
	boolean _useRmaCellRenderer = false;
	boolean _addedToScrollPane = false;
	/** whether the consume the enter and escape keys */
	boolean _processKeyEvents = true;
	// has this table been added to it's scrollpane?
	Color _pasteBackground, _pasteForeground;
	Color _modForegroundColor,  _errorForegroundColor;
	int _precision = 1;
	// precision used for filling columns
	MonthlyTable _rowheaderTbl;
	Border _defaultBorder;
	/* this actually gets set in the createDefaultEditors() */
	int _clickCountToStart = 2;
	// number of mouse clicks to invoke editor
	int _oneClickToStart = 1;
	//
	/**
	 *  Description
	 */
	protected int _maxNumPage = 1;
	private PageText _titleInfo = null;
	// title for 1st page of printing
	private PageText _otherInfo = null;
	// other info printed before table
	private String _printDateStr = null;
	// date/time print was performed
	private JMenuItem insertMi, appendMi, deleteMi, fillMi;
	private boolean _defaultPopup = true;
	private boolean _editable = true;
	private boolean _isEditable = true;

	private RmaModJTableExportDialog _exportOptionsDialog;
	private RmaModJTableFillDialog _fillDialog;

	private Hashtable _parameterScaleTable = new Hashtable();
	/**
	 * flag indicating whether pasting automatically appends rows to the table
	 */
	private boolean _pasteAddsRows = true;
	private boolean _useDefaultPrintHeader = true;
	/**
	 * point where the popup menu was displayed
	 */
	private Point _popupPoint;
	
	static final Color DISABLED_COLOR = new Color(225, 225, 225);

	protected boolean _editMode;

	private MonthlyTableModel _tableModel;

	private Component _parentFrame;
	
	private MonthlyTable thisTable = this;
	
	class JTableInCellRenderer extends JScrollPane implements TableCellRenderer {

		public JTableInCellRenderer() {
		}

		/**
		 * 
		 * @param table
		 * @param value
		 * @param isSelected
		 * @param row
		 * @param column
		 * @return
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			MonthlyTableModel.SingleMonthlyTable innerTable = (MonthlyTableModel.SingleMonthlyTable) value;
			// JList rowHeaderList =
			// ((MonthlyTableModel.SingleMonthlyTable.SingleMonthlyTableModel)
			// innerTable.getModel()).rowList;
			// rowHeaderList.setCellRenderer(new RowHeaderRenderer(innerTable));
			// rowHeaderList.setFixedCellWidth(75);
			// rowHeaderList.setFixedCellHeight(getRowHeight());
			JScrollPane innerTableScrollPane = new JScrollPane(innerTable);
			// innerTableScrollPane.setRowHeaderView(rowHeaderList);
			// VERTICAL_SCROLLBAR_AS_NEEDED shows unnecessary inner scrollbar!
			innerTableScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			return innerTableScrollPane;
		}
	}

	
	/**
	 *  Class Description
	 *
	 *@author     Mark Ackerman
	 *@created    October 10, 2001
	 */
	static class ParameterScale implements IParameterScale
	{
		/**
		 *  Constructor for the ParameterScale object
		 *
		 *@param  paramId  Description
		 *@param  scale    Description
		 */
		public ParameterScale(int paramId, double scale)
		{
			this.paramId = paramId;
			this.scale = scale;
		}

		int paramId;
		double scale;

		public int getParamId()
		{
			return paramId;
		}

		public double getScale()
		{
			return scale;
		}
	}

	static boolean _pasteDebug = false;
	static
	{
		_pasteDebug = Boolean.getBoolean("Table.pasteDebug");
		UIManager.put("Table.disabledCellColor", Color.lightGray);
//        UIManager.put(uiClassID, "rma.awt.table.FixedRowTableUI");
	}

	/**
	 */
	public MonthlyTable()
	{
		super();
//		_tableModel = (RmaTableModelInterface) createDefaultDataModel();
		buildControls();
	}

	/**
	 *  create RmaJTable with default data model
	 *
	 *@param  parent  Description
	 */
	public MonthlyTable(Component parent)
	{
		this();
		setParent(parent);
		
		_editMode = false;
		_parentFrame = parent;
		// if (_parentFrame instanceof MonthlyTableFrame)
		// setShowTotalColumn(((MonthlyTableFrame)_parentFrame).showTotalColumn());
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setAddRemoveEnabled(false);
		setColumnSelectionAllowed(true);
		setRowSelectionAllowed(true);
		setCellSelectionEnabled(true);
		TableCellRenderer renderer = new JTableInCellRenderer();
		if (renderer instanceof RmaCellRenderer)
			((RmaCellRenderer) renderer).setHorizontalAlignment(4);
		this.setDefaultRenderer(JTable.class, renderer);
		setEditable(false);
		setUseDefaultPrintHeader(false);
	}

	/**
	 *  create RmaJTable with column title array
	 *
	 *@param  parent       Description
	 *@param  columnNames  Description
	 */
	public MonthlyTable(Component parent, Object[] columnNames)
	{
		super(new RmaTableModel((String[]) columnNames, new Object[1][columnNames.length], new boolean[columnNames.length]));
		//super(new RmaJTableSorter(new RmaTableModel((String[])columnNames, new Object[1][columnNames.length],  new boolean[columnNames.length] )));
//		_tableModel = (RmaTableModelInterface) getModel();
		for (int i = 0; i < columnNames.length; i++)
		{
			setColumnEnabled(true, i);
		}
		//see if we need to used a Units Header Renderer



		super.revalidate();
		setParent(parent);
		buildControls();
	}

	/**
	 *  create RmaJTable with rowdata array and columntitles array
	 *
	 *@param  parent       Description
	 *@param  rowData      Description
	 *@param  columnNames  Description
	 */
	public MonthlyTable(Component parent, Object[][] rowData, Object[] columnNames)
	{

		super(new RmaTableModel((String[]) columnNames, rowData, new boolean[columnNames.length]));
		//super(new RmaJTableSorter(new RmaTableModel((String[])columnNames, rowData, new boolean[columnNames.length] )));
//		_tableModel = (RmaTableModelInterface) getModel();
		for (int i = 0; i < columnNames.length; i++)
		{
			setColumnEnabled(true, i);
		}

		super.revalidate();
		setParent(parent);
		buildControls();
	}

	/**
	 *  create new RmaJTable with rowData vector and ColumnNames vector
	 *
	 *@param  parent       Description
	 *@param  rowData      Description
	 *@param  columnNames  Description
	 */
	public MonthlyTable(Component parent, Vector rowData, Vector columnNames)
	{
		super(new RmaTableModel(columnNames, rowData, new Vector(columnNames.size())));
		//super ( new RmaJTableSorter(new RmaTableModel(columnNames, rowData, new Vector(columnNames.size()))));
//		_tableModel = (RmaTableModelInterface) getModel();

		super.revalidate();
		setParent(parent);
		buildControls();
	}


	public Map getDisplayScaleMap()
	{
		return _parameterScaleTable;
	}
	int _scaleFactor = 1;

	/**
	 *  Sets the DisplayScaleFactor attribute of the RmaJTable object
	 *
	 *@param  paramId      The new DisplayScaleFactor value
	 *@param  scaleFactor  The new DisplayScaleFactor value
	 */
	public void setDisplayScaleFactor(int paramId, double scaleFactor)
	{
		commitEdit(true);

		TableColumnModel tcm = getColumnModel();

		TableModel tm = getModel();
		if (tm instanceof RmaTableModelInterface)
		{
			for (int i = 0; i < tm.getColumnCount(); i++)
			{
				if (((RmaTableModelInterface) tm).getColumnParameter(i) != paramId)
				{
					continue;
				}

				_parameterScaleTable.put(new Integer(i), new ParameterScale(paramId, scaleFactor));
			}
		}

//		ColumnGroup cg = null;
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			ParameterScale ps = (ParameterScale) _parameterScaleTable.get(new Integer(convertColumnIndexToModel(i)));
			if (ps == null)
			{
				continue;
			}

			TableCellRenderer tcr = tcm.getColumn(i).getCellRenderer();

			if (tcr instanceof RmaCellRenderer)
			{
				((RmaCellRenderer) tcr).setDisplayScaleFactor(i, paramId, scaleFactor);
			}
			TableCellEditor tce = tcm.getColumn(i).getCellEditor();
			if (tce instanceof RmaCellEditor)
			{
				((RmaCellEditor)tce).setDisplayScaleFactor(paramId, scaleFactor);
			}
		}

		if (_rowheaderTbl != null)
		{
			_rowheaderTbl.setDisplayScaleFactor(paramId, scaleFactor);
		}

		getTableHeader().repaint();
		this.repaint();
		return;
	}

	int _displayUnitSystem = hec.data.Units.ENGLISH_ID;
	private JMenuItem _clearMi;
	private JMenuItem _pasteMi;
	private JMenuItem _copyMi;
	private JMenuItem _cutMi;
	private boolean _alternatingReportBackground;
	private int _origRowHeight;


	/**
	 *  Sets the DisplayUnitsSystem attribute of the RmaJTable object
	 *
	 *@param  unitSystem  The new DisplayUnitsSystem value
	 */
	public void setDisplayUnitsSystem(int unitSystem)
	{
		if (!hec.data.Units.isValidUnitsSystem(unitSystem))
		{
			System.out.println("ERROR <RmaJTable.setDisplayUnitsSystem()> : Invalid Unit System being Set " + unitSystem);
			System.out.println("ERROR <RmaJTable.setDisplayUnitsSystem()> : " + hec.data.Units.ERROR_VALID_UNIT_SYSTEMS);
			return;
		}
		commitEdit(true);
		_displayUnitSystem = unitSystem;

		Iterator iter = defaultEditorsByColumnClass.values().iterator();
		while (iter.hasNext()){
			Object o = iter.next();
			if (!(o instanceof RmaCellEditor)) continue;
			RmaCellEditor editor = (RmaCellEditor)o;
			editor.setDisplayUnitSystem(unitSystem);
		}

		iter = defaultRenderersByColumnClass.values().iterator();
		while (iter.hasNext()){
			Object o = iter.next();
			if (!(o instanceof UnitsCellRenderer)) continue;
			UnitsCellRenderer renderer = (UnitsCellRenderer)o;
			renderer.setDisplayUnitsSystem(unitSystem);
		}

		TableColumnModel tcm = getColumnModel();
		ColumnGroup cg = null;
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableCellRenderer tcr = tcm.getColumn(i).getHeaderRenderer();
			if (tcr instanceof UnitsCellRenderer)
			{
				((UnitsCellRenderer) tcr).setDisplayUnitsSystem(unitSystem);
			}
			tcr = tcm.getColumn(i).getCellRenderer();
			if (tcr instanceof UnitsCellRenderer)
			{
				((UnitsCellRenderer) tcr).setDisplayUnitsSystem(unitSystem);
			}
			TableCellEditor tce = tcm.getColumn(i).getCellEditor();
			if (tce instanceof RmaCellEditor)
			{
				((RmaCellEditor)tce).setDisplayUnitSystem(unitSystem);
			}
			//see if there are column groups
			JTableHeader header = getTableHeader();
			if (header instanceof GroupableTableHeader)
			{
				Enumeration e = ((GroupableTableHeader) header).getColumnGroups(tcm.getColumn(i));

				while (e != null && e.hasMoreElements())
				{
					cg = (ColumnGroup) e.nextElement();
					if (cg.getHeaderRenderer() instanceof UnitsCellRenderer)
					{
						((UnitsCellRenderer) cg.getHeaderRenderer()).setDisplayUnitsSystem(unitSystem);
					}
				}
			}
		}
		if (_rowheaderTbl != null)
		{
			_rowheaderTbl.setDisplayUnitsSystem(unitSystem);
		}
		TableModel model = getModel();
		if (model instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) model).setDisplayUnitsSystem(unitSystem);
		}
		getTableHeader().repaint();
		this.repaint();
		return;
	}


	/**
	 *  Gets the DisplayUnitSystem attribute of the RmaJTable object
	 *
	 *@return    The DisplayUnitSystem value
	 */
	public int getDisplayUnitSystem()
	{
		return _displayUnitSystem;
	}

	/**
	 *  Sets the TimeZone attribute of the RmaJTable object
	 *
	 *@param  tz  The new TimeZone value
	 */
	public void setTimeZone(TimeZone tz)
	{
		commitEdit(true);
		TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableCellRenderer tcr = tcm.getColumn(i).getCellRenderer();
			if (tcr instanceof RmaDateTimeRenderer)
			{
				((RmaDateTimeRenderer) tcr).setTimeZone(tz);
			}
			TableCellEditor tce = tcm.getColumn(i).getCellEditor();
			if (tce instanceof RmaDateTimeEditor)
			{
				((RmaDateTimeEditor) tce).setTimeZone(tz);
			}
			TableCellRenderer hdr = tcm.getColumn(i).getHeaderRenderer();
			if (hdr instanceof TimeZoneHeaderRenderer)
			{
				((TimeZoneHeaderRenderer) hdr).setTimeZone(tz);
			}
		}

		if (_rowheaderTbl != null)
		{
			_rowheaderTbl.setTimeZone(tz);
		}

		getTableHeader().repaint();
		this.repaint();
	}

	/**
	 *  Gets the TimeZone attribute of the RmaJTable object
	 *
	 *@return    The TimeZone value
	 */
	public TimeZone getTimeZone()
	{
		return null;
	}

	/**
	 *  Sets the ColumnParameters attribute of the RmaJTable object
	 *
	 *@param  params  The new ColumnParameters value
	 */
	public void setColumnParameters(int[] params)
	{
		TableModel tm = getModel();
		if (tm instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) tm).setColumnParameters(params);
		}
	}

	/**
	 *  Gets the DisplayUnitsString attribute of the RmaJTable object
	 *
	 *@param  system  Description
	 *@return         The DisplayUnitsString value
	 */
	public String getDisplayUnitsString(int system)
	{
		return "";
	}

	/**
	 *  Gets the DisplayUnitsString attribute of the RmaJTable object
	 *
	 *@param  system  Description
	 *@param  column  Description
	 *@return         The DisplayUnitsString value
	 */
	public String getDisplayUnitsString(int system, int column)
	{
		TableModel tm = getModel();
		if (tm instanceof RmaTableModelInterface)
		{
			int paramId = ((RmaTableModelInterface) tm).getColumnParameter(column);
			if (paramId == RmaTableModelInterface.UNDEF_COLUMN_PARAM_ID)
			{
				return "";
			}
			String label = hec.data.Parameter.getUnitsStringForSystem(paramId, system);
			if (paramId == hec.data.Parameter.PARAMID_CURENCY)
			{
				ParameterScale ps = (ParameterScale) _parameterScaleTable.get(new Integer(column));
				if (ps == null)
				{
					label += "1";
				}
				else
				{
					label += java.lang.Math.round(1 / ps.scale);
				}

			}
			return label;
		}
		return "";
	}

	/**
	 *  set the table cell renderer with an RmaCellRenderer
	 */
	public void setCellRenderer()
	{
		_rmaCellRend = new RmaCellRenderer(new JLabel());

		// Now set up the renderer to draw all headers.
		TableColumnModel tcm = getTableHeader().getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableColumn tc = tcm.getColumn(i);
			tc.setCellRenderer(_rmaCellRend);
			_useRmaCellRenderer = true;
		}
	}

	/**
	 *@param  columnClass  Description
	 *@return              The DefaultRenderer value
	 */
	
	public TableCellRenderer getDefaultRenderer(Class columnClass)
	{
		TableCellRenderer renderer = super.getDefaultRenderer(columnClass);
		if (renderer == null)
		{
			renderer = new RmaCellRenderer();
		}
		if(renderer instanceof UnitsCellRenderer) {
			((UnitsCellRenderer)renderer).setDisplayUnitsSystem(this.getDisplayUnitSystem());
		}
		return renderer;
	}

	/**
	 *  create the default table model, initialize the table sets the table up :
	 *  setCellSelectionEnabled(true); setRowSelectionAllowed(false);
	 *  setColumnSelectionAllowed(false);
	 *
	 *@return    Description
	 */
	
	protected TableModel createDefaultDataModel()
	{
		RmaTableModel model = new RmaTableModel(new String[1], new Object[1][1], new boolean[1]);
		//RmaJTableSorter model = new RmaJTableSorter(new RmaTableModel(new String[1], new Object[1][1], new boolean[1]));
		model.setColEnabled(true, 0);
		return model;
	}

	/**
	 *  Method Description
	 */
	
	public void createDefaultEditors()
	{
		// this forces the click count to 2.
		_clickCountToStart = 2;

		defaultEditorsByColumnClass = new Hashtable(4);

		// Objects
		RmaJTextField textField = new RmaJTextField();
		if (_defaultBorder == null)
		{
			createDefaultBorder();
		}

		textField.setBorder(_defaultBorder);
		RmaCellEditor dce;
		dce = new RmaCellEditor(textField);
		dce.setClickCountToStart(_clickCountToStart);
		setDefaultEditor(getClassForName("java.lang.Object"), dce);
		setDefaultEditor(getClassForName("java.lang.String"), dce);

		// Numbers
		RmaJDecimalField rightAlignedTextField = new RmaJDecimalField();
		rightAlignedTextField.setHorizontalAlignment(JTextField.RIGHT);
		rightAlignedTextField.setBorder(_defaultBorder);
		dce = new RmaCellEditor(rightAlignedTextField);
		dce.setClickCountToStart(_clickCountToStart);
		setDefaultEditor(java.lang.Number.class, dce);
		setDefaultEditor(getClassForName("hec.data.ParamDouble"), dce);

		// Booleans
////        JCheckBox centeredCheckBox = new JCheckBox();
		RmaJCheckBox centeredCheckBox = new RmaJCheckBox();
		//for modification Alerts
		centeredCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
		//setDefaultEditor(getClassForName("java.lang.Boolean"), new OneClickCheckBoxEditor(centeredCheckBox));
	}

	/**
	 *  Method Description
	 */
	
	protected void createDefaultRenderers()
	{

		defaultRenderersByColumnClass = new Hashtable();

		// Objects
		RmaCellRenderer label1 = new RmaCellRenderer(new javax.swing.JLabel());
		setDefaultRenderer(getClassForName("java.lang.Object"), label1);

		// Strings
		setDefaultRenderer(getClassForName("java.lang.String"), label1);

		// Numbers
		RmaCellRenderer rightAlignedLabel = new RmaCellRenderer(new JLabel());
		rightAlignedLabel.setHorizontalAlignment(JLabel.RIGHT);
		setDefaultRenderer(getClassForName("java.lang.Number"), rightAlignedLabel);
		setDefaultRenderer(getClassForName("hec.data.ParamDouble"), rightAlignedLabel);

		// Icons
		RmaCellRenderer centeredLabel =
			new RmaCellRenderer(new JLabel())
			{
				public void setValue(Object value)
				{
					setIcon((Icon) value);
				}
			};
		centeredLabel.setHorizontalAlignment(JLabel.CENTER);
		setDefaultRenderer(getClassForName("javax.swing.ImageIcon"), centeredLabel);

		// Booleans
		if (false)
		{
			DefaultTableCellRenderer booleanRenderer =
				new DefaultTableCellRenderer()
				{
					Icon trueIcon = UIManager.getIcon("CheckBox.icon");

					
					public void setValue(Object value)
					{
						setIcon((value != null && ((Boolean) value).booleanValue()) ? trueIcon : null);
					}
				};
			booleanRenderer.setHorizontalAlignment(JLabel.CENTER);
			setDefaultRenderer(getClassForName("java.lang.Boolean"), booleanRenderer);
		}

		CheckBoxRenderer booleanRenderer = new CheckBoxRenderer();
		booleanRenderer.setHorizontalAlignment(JLabel.CENTER);
		setDefaultRenderer(getClassForName("java.lang.Boolean"), booleanRenderer);
	}

	
	protected JTableHeader createDefaultTableHeader()
	{
		return new ToolTipHeader(getColumnModel());
	}
	/**
	 *  Method Description
	 */
	protected void createDefaultBorder()
	{
		//_defaultBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
		_defaultBorder = BorderFactory.createLineBorder(Color.black);
	}

	/**
	 *  build the table controls, setup the default cell editor
	 */
	protected void buildControls()
	{

		if ( Boolean.getBoolean("RmaJTable.AllowsFontResizing"))
		{
			setAllowsFontResizing(true);
		}
		buildPopup();

		_modForegroundColor = Color.getColor("Table.editedForeground", Color.blue);
		_errorForegroundColor  =  Color.getColor("Table.errorForeground", Color.red);
		if (_defaultBorder == null)
		{
			createDefaultBorder();
		}

		_scrollPane = createScrollPane();
		addMouseListener(this);
		tableHeader.setReorderingAllowed(false);
		ListSelectionModel csm = getColumnModel().getSelectionModel();
		_colSL = new RmaSelectionListener(csm, 1);
		csm.addListSelectionListener(_colSL);
		ListSelectionModel rsm = getSelectionModel();

		_rowSL = new RmaSelectionListener(rsm, 2);
		rsm.addListSelectionListener(_rowSL);

		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(true);
		// set up the popup for the cell editor
		TableColumn tc;
		RmaJTextField tf = new RmaJTextField();
		tf.setBorder(_defaultBorder);

		for (int i = 0; i < getColumnCount(); i++)
		{
			tc = getColumnModel().getColumn(i);
			if (tc == null)
			{
				return;
			}
			tf.addMouseListener(this);
			RmaCellEditor dcf = new RmaCellEditor(tf);
			dcf.setClickCountToStart(_clickCountToStart);
			//dfc.addCellEditorListener(this);
			tc.setCellEditor(dcf);
		}
		//initRowBackgrounds();
		//initRowForegrounds();
		setCellRenderer();
		setUpTabKeys();

	}
	protected JScrollPane createScrollPane()
	{
		return new JScrollPane();
	}
	/**
	 *  allow the user to set their own popup menu
	 *
	 *@param  popup  The new PopupMenu value
	 */
	public void setPopupMenu(JPopupMenu popup)
	{
		_popup = popup;
		_defaultPopup = false;
	}

	/**
	 *  build the popup menu
	 */
	protected void buildPopup()
	{
		String[] labels = new String[]{"Cut", "Copy", "Paste", "Clear"};
		String[] commands = new String[]{"cut", "copy", "paste", "clear"};

		addKeyListener(this);
		_popup = new JPopupMenu();
		// Construct the popup menu
		Font f = UIManager.getFont("TextField.font");
		JMenuItem mi = new JMenuItem("Undo");
		mi.setActionCommand("undo");
		mi.addActionListener(this);
		mi.setVisible(false);
		mi.setFont(f);
		_popup.add(mi);
		Component c = new JPopupMenu.Separator();
		c.setVisible(false);
		_popup.add(c);
		int i=0;
		_cutMi = new JMenuItem(labels[i]);
		_cutMi.setActionCommand(commands[i]);
		_cutMi.addActionListener(this);
		_cutMi.setFont(f);
		_popup.add(_cutMi);
		i++;
		_copyMi = new JMenuItem(labels[i]);
		_copyMi.setActionCommand(commands[i]);
		_copyMi.addActionListener(this);
		_copyMi.setFont(f);
		_popup.add(_copyMi);
		i++;
		_pasteMi = new JMenuItem(labels[i]);
		_pasteMi.setActionCommand(commands[i]);
		_pasteMi.addActionListener(this);
		_pasteMi.setFont(f);
		_popup.add(_pasteMi);
		i++;
		_clearMi = new JMenuItem(labels[i]);
		_clearMi.setActionCommand(commands[i]);
		_clearMi.addActionListener(this);
		_clearMi.setFont(f);
		_popup.add(_clearMi);
		/*
		for (int i = 0; i < labels.length; i++)
		{

			mi = new JMenuItem(labels[i]);
			mi.setActionCommand(commands[i]);
			mi.addActionListener(this);
			// add listener to
			mi.setFont(f);
			_popup.add(mi);
		}
		*/
		// removed popup for a 1.3 bug where you get a null pointer exception when
		// the menu displays partly off of the dialog.
		_popup.addSeparator();
		/*
		 *  JMenu m = new JMenu("Fill");
		 *  m.setFont(f);
		 *  _popup.add(m);
		 */
		fillMi = new JMenuItem("Fill...");
		fillMi.setFont(f);
		fillMi.setActionCommand("fill");
		fillMi.addActionListener(this);
		_popup.add(fillMi);
		//m.add(mi);
		/*
		 *  mi = new JMenuItem("Fill - Repeat");
		 *  mi.setFont(f);
		 *  mi.setActionCommand("repeatFill");
		 *  mi.addActionListener(this);
		 *  _popup.add(mi);
		 */
		//m.add(mi);
		_popup.addSeparator();

		mi = new JMenuItem("Select All");
		mi.setFont(f);
		mi.setActionCommand("selectall");
		mi.addActionListener(this);
		mi.setEnabled(true);
		_popup.add(mi);

		insertMi = new JMenuItem("Insert Row(s)...");
		insertMi.setFont(f);
		insertMi.setActionCommand("insertrow");
		insertMi.addActionListener(this);
		_popup.add(insertMi);

		appendMi = new JMenuItem("Append Row");
		appendMi.setFont(f);
		appendMi.setActionCommand("appendrow");
		appendMi.addActionListener(this);
		_popup.add(appendMi);

		deleteMi = new JMenuItem("Delete Row(s)");
		deleteMi.setFont(f);
		deleteMi.setActionCommand("deleterow");
		deleteMi.addActionListener(this);
		_popup.add(deleteMi);

		_popup.addSeparator();
		mi = new JMenuItem("Print...");
		mi.setFont(f);
		mi.setActionCommand("print");
		mi.addActionListener(this);
		_popup.add(mi);

		mi = new JMenuItem("Print Preview...");
		mi.setFont(f);
		mi.setActionCommand("printPreview");
		mi.addActionListener(this);
		_popup.add(mi);

		mi = new JMenuItem("Export...");
		mi.setFont(f);
		mi.setActionCommand("export");
		mi.addActionListener(this);
		_popup.add(mi);

		_popup.addSeparator();
		mi = new JMenuItem("Sum Selected Cells");
		mi.setFont(f);
		mi.setActionCommand("sumselected");
		mi.addActionListener(this);
		_popup.add(mi);

		mi = new JMenuItem("Sum Entire Column");
		mi.setFont(f);
		mi.setActionCommand("sumcolumn");
		mi.addActionListener(this);
		_popup.add(mi);

		//this.add(popup);
	}


	/**
	 *  set up so that the TAB and SHIFT-TAB are managed by the table.
	 */
	protected void setUpTabKeys()
	{
		// make sure focus manager does not "eat" TAB events.
		//javax.swing.FocusManager.getCurrentManager().disableSwingFocusManager();

		// Tell the table what action to take when the user
		// hits the TAB key.
		Action a1 =	new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					requestFocus();
					requestFocus();
					int ancRow;
					int ancCol;
					ancRow = getSelectedRow();
					ancCol = getSelectedColumn();

					ancRow = (ancRow < 0) ? 0 : ancRow;
					ancCol = (ancCol < 0) ? 0 : ancCol;

					if ((ancCol + 1) >= getColumnCount())
					{
//						Component comp = null;
						if ((ancRow + 1) >= getRowCount())
						{
							javax.swing.FocusManager.getCurrentManager().focusNextComponent(MonthlyTable.this);
							return;
							/*
							 *  comp = getNextFocusableComponent();
							 *  if ( comp != null )
							 *  {   comp.requestFocus();
							 *  return;
							 *  }
							 *  ancRow = 0;
							 *  ancCol = 0;
							 */
						}
						else
						{
							ancRow++;
							ancCol = 0;
						}
					}
					else
					{
						ancCol++;
					}

					commitEdit(true);

					if (_tabToEditCell)
					{
						//  Should we tab to the next editable cell in this row?
						if (isCellEditable(ancRow, ancCol) == false)
						{
							int numb = getColumnCount();
							boolean ifound = false;
							for (int i = ancCol; i < numb; i++)
							{
								if (isCellEditable(ancRow, i))
								{
									ancCol = i;
									ifound = true;
									break;
								}
							}
							// if no other editable cell found in row go to next row and look
							if (!ifound && ancRow < getRowCount() - 1)
							{
								for (int i = 0; i < numb; i++)
								{
									if (isCellEditable(ancRow + 1, i))
									{
										ancCol = i;
										ancRow++;
										break;
									}
								}
							}
						}
					}

					setRowSelectionInterval(ancRow, ancRow);
					setColumnSelectionInterval(ancCol, ancCol);
					setRowSelectionInterval(ancRow, ancRow);
					setColumnSelectionInterval(ancCol, ancCol);

					scrollRectToVisible(getCellRect(ancRow, ancCol, true));

				}
			};

		InputMap imap = getInputMap();
		ActionMap amap = getActionMap();
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "tabForward");
		amap.put("tabForward", a1);
		//JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


		// Tell the table what action to take when the user
		// hits the SHIFT-TAB key.
		Action a2= 	new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					requestFocus();
					requestFocus();
					int ancRow = getSelectedRow();
					int ancCol = getSelectedColumn();

					if ((ancCol - 1) < 0)
					{
						if (ancRow - 1 < 0)
						{
							javax.swing.FocusManager.getCurrentManager().focusPreviousComponent(MonthlyTable.this);
							return;
						}
						else
						{
							ancCol = getColumnCount() - 1;
							ancRow--;
						}
					}
					else
					{
						ancCol--;
					}
					commitEdit(true);

					if (_tabToEditCell)
					{
						//  Should we tab to the next editable cell in this row?
						if (isCellEditable(ancRow, ancCol) == false)
						{
							int numb = getColumnCount();
							boolean ifound = false;
							for (int i = ancCol; i >= 0; i--)
							{
								if (isCellEditable(ancRow, i))
								{
									ancCol = i;
									ifound = true;
									break;
								}
							}
							// if no other editable cell found in row go to next row and look
							if (!ifound && ancRow > 0)
							{
								for (int i = numb - 1; i >= 0; i--)
								{
									if (isCellEditable(ancRow - 1, i))
									{
										ancCol = i;
										ancRow--;
										break;
									}
								}
							}
						}
					}

					setRowSelectionInterval(ancRow, ancRow);
					setColumnSelectionInterval(ancCol, ancCol);
					setRowSelectionInterval(ancRow, ancRow);
					setColumnSelectionInterval(ancCol, ancCol);

					scrollRectToVisible(getCellRect(ancRow, ancCol, true));

				}
			};

		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, ActionEvent.SHIFT_MASK), "tabBack");
		amap.put("tabBack", a2);
		/*
		 *  registerKeyboardAction( new AbstractAction() {
		 *  public void actionPerformed (ActionEvent e)
		 *  {
		 *  requestFocus();
		 *  requestFocus();
		 *  int ancRow = getSelectedRow();
		 *  int ancCol = getSelectedColumn();
		 *  if ((ancRow +1 ) >= getRowCount())
		 *  {
		 *  ancRow = 0;
		 *  }
		 *  else ancRow++;
		 *  commitEdit( true );
		 *  setRowSelectionInterval(ancRow, ancRow);
		 *  setColumnSelectionInterval(ancCol, ancCol);
		 *  setRowSelectionInterval(ancRow, ancRow);
		 *  setColumnSelectionInterval(ancCol, ancCol);
		 *  scrollRectToVisible(getCellRect(ancRow,ancCol,true));
		 *  }}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
		 *  /JComponent.WHEN_FOCUSED);
		 *  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		 */
	}

	/**
	 *  Method Description
	 *
	 *@param  e  Description
	 */
	
	protected void processComponentKeyEvent(KeyEvent e)
	{
		if ( !_processKeyEvents ) return;

		super.processComponentKeyEvent(e);
		if (e.isConsumed())
		{
			return;
		}

		if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyChar() == '\n'))
		{
			/*
			if ( editCellOnKeyStroke())
			{
				e.consume();
			}
			*/
			if (e.getID() == KeyEvent.KEY_RELEASED)
			{
				//e.consume();
			}
		}
		/*else if ( e.getKeyCode() == KeyEvent.VK_TAB )
		{
			if ( editCellOnKeyStroke())
			{
				//e.consume();
			}
		}*/
		else if ((e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyChar() == 0x1b))
		{

			if (e.getID() == KeyEvent.KEY_RELEASED)
			{
				// && isEditing()) // since the processKeyBinding already
				// deactiveated the editor by the time we get
				e.consume();
				// this event we just have to say we're going
			}
			// to consume it anyway
		}
		/*
		else if ( e.getKeyCode() == KeyEvent.VK_DOWN
			   || e.getKeyCode() == KeyEvent.VK_UP
			   || e.getKeyCode() == KeyEvent.VK_RIGHT
			   || e.getKeyCode() == KeyEvent.VK_LEFT )
		{
			if ( editCellOnKeyStroke())
			{
				e.consume();
			}
		}*/


	}


	private boolean editCellOnKeyStroke()
	{
		int anchorRow = getSelectionModel().getAnchorSelectionIndex();
		int anchorColumn = getColumnModel().getSelectionModel().
						   getAnchorSelectionIndex();
		if (anchorRow != -1 && anchorColumn != -1 && !isEditing())
		{
			if (!editCellAt(anchorRow, anchorColumn, null))
			{
				return false;
			}
		}
		return true;
	}
	/**
	 *  add a table model listener to the table model
	 *
	 *@param  l  The feature to be added to the TableModelListener attribute
	 */
	public void addTableModelListener(TableModelListener l)
	{
		getModel().addTableModelListener(l);
	}

	/**
	 *  make sure the color vectors are large enough
	 *
	 *@param  size  Description
	 */
	private void sizeColorVector(int size) { }

	/**
	 *  add a table change listener
	 *
	 *@param  t  The feature to be added to the TableChangeListener attribute
	 */
	public void addTableChangeListener(TableChangeListener t)
	{
		if (t == null || _tableChangeListeners.contains(t))
		{
			return;
		}
		_tableChangeListeners.addElement(t);
	}

	/**
	 *  Method Description
	 *
	 *@param  t  Description
	 */
	public void removeTableChangeListener(TableChangeListener t)
	{
		if (t == null)
		{
			return;
		}
		_tableChangeListeners.removeElement(t);
	}

	/**
	 *  add a TableMinMaxTracker for column col
	 *
	 *@param  col  The feature to be added to the TableMinMaxTracker attribute
	 */
	public void addTableMinMaxTracker(int col)
	{
		if (col < 0 || col >= getColumnCount())
		{
			return;
		}
		MinMaxEntry mme = new MinMaxEntry(col);
		if (!_tableMinMaxEntries.contains(mme))
		{
			setMinMax(mme);
			_tableMinMaxEntries.addElement(mme);
		}

	}

	/**
	 *@param  l  Description
	 */
	public void removeCellEditorListener(TableChangeListener l)
	{
		_tableChangeListeners.removeElement(l);
	}

	/**
	 *  add a cell editor listener
	 *
	 *@param  l  The feature to be added to the CellEditorListener attribute
	 */
	public void addCellEditorListener(CellEditorListener l)
	{
		for (int i = 0; i < _cellEditorListeners.size(); i++)
		{
			if (_cellEditorListeners.contains(l))
			{
				// don't add the same listener twice
				return;
			}
		}

		_cellEditorListeners.addElement(l);
	}

	/**
	 *  remove a cell editor listener
	 *
	 *@param  l  Description
	 */
	public void removeCellEditorListener(CellEditorListener l)
	{
		_cellEditorListeners.removeElement(l);
	}

	/**
	 *  Method Description
	 */
	protected void removeEditorListeners()
	{
		Component comp = getEditorComponent();
		if (comp != null && _modForegroundColor != null)
		{
			if (comp instanceof javax.swing.text.JTextComponent)
			{
				Document doc = ((javax.swing.text.JTextComponent) comp).getDocument();
				doc.removeDocumentListener(this);
			}
			else if (comp instanceof javax.swing.AbstractButton)
			{
				AbstractButton ab = (AbstractButton) comp;
				ab.removeActionListener(this);
			}
			else if (comp instanceof javax.swing.JComboBox)
			{
				JComboBox cb = (JComboBox) comp;
				cb.removeItemListener(this);
			}
		}
	}

	/**
	 *  called when editing has been canceled. Pass on this call to any registered
	 *  listeners.
	 *
	 *@param  e  Description
	 */
	
	public void editingCanceled(ChangeEvent e)
	{
		removeEditorListeners();
		super.editingCanceled(e);
		for (int i = 0; i < _cellEditorListeners.size(); i++)
		{
			Object obj = _cellEditorListeners.elementAt(i);
			if (obj != null && obj instanceof CellEditorListener)
			{
				CellEditorListener cel = (CellEditorListener) obj;
				cel.editingCanceled(e);
			}
		}
	}

	/**
	 *  Checks to see if editing should be stopped. Returns true if editing has
	 *  been stopped otherwise false. This method check to see if the edited field
	 *  is valid and if the whole table is valid. If both are true, editing is
	 *  stopped and the editor is removed.
	 *
	 *@param  e  Description
	 *@return    Description
	 */
	public boolean _editingStopped(ChangeEvent e)
	{
		// Take in the new value
		final TableCellEditor editor = getCellEditor();
		if (editor != null)
		{

			Component comp = null;
			if (editor instanceof RmaCellEditor)
			{
				comp = ((RmaCellEditor) editor).getComponent();
			}
			/*
			 *  else if ( editor instanceof RMACellEditor )
			 *  comp = ((RMACellEditor)editor).getComponent();
			 */
			final Object value = editor.getCellEditorValue();
			if (comp instanceof RmaValidComponent)
			{
				if (!((RmaValidComponent) comp).isValid(true) || !isValid(true))
				{
					final int row = editingRow;
					final int col = editingColumn;

					SwingUtilities.invokeLater(
						new Runnable()
						{
							public void run()
							{

								if (getEditingRow() != row || getEditingColumn() != col)
								{
									editCellAt(row, col);
									editor.removeCellEditorListener(MonthlyTable.this);
								}
								setRowSelectionInterval(row, row);
								setColumnSelectionInterval(col, col);
								setRowSelectionInterval(row, row);
								setColumnSelectionInterval(col, col);
								getEditorComponent().requestFocus();

								/*
								 *  this causes a the JVM to lock up.
								 *  Component ecomp = getEditorComponent();
								 *  if ( ecomp instanceof javax.swing.text.JTextComponent )
								 *  {
								 *  ((javax.swing.text.JTextComponent)ecomp).setText(value.toString());
								 *  }
								 */
							}
						});
					return false;
				}
			}
//            if(!isValid(true)) {
//                return false;
//            }
			setValueAt(value, editingRow, editingColumn);
			if ( comp instanceof rma.lang.Modifiable )
			{
				if ( ((rma.lang.Modifiable)comp).isModified() )
				{
					setCellForeground(editingRow, editingColumn, _modForegroundColor);
				}
			}
			removeEditor();
		}
		return true;
	}

	/**
	 *  called when editing has stopped. Pass on call to any registered listeners.
	 *
	 *@param  e  Description
	 */
	
	public void editingStopped(ChangeEvent e)
	{
		removeEditorListeners();

		//super.editingStopped(e);
		if (!_editingStopped(e))
		{
			return;
		}
		//setModified(true);
//System.out.println("editingStopped fired");
		for (int i = 0; i < _cellEditorListeners.size(); i++)
		{
			Object obj = _cellEditorListeners.elementAt(i);
			if (obj != null && obj instanceof CellEditorListener)
			{
//System.out.println("editingStopped fired " + i );
				CellEditorListener cel = (CellEditorListener) obj;
				cel.editingStopped(e);
			}
		}
	}

	/**
	 *  Method Description
	 *
	 *@param  row     Description
	 *@param  column  Description
	 *@param  e       Description
	 *@return         Description
	 */
	
	public boolean editCellAt(int row, int column, EventObject e)
	{
		boolean b = super.editCellAt(row, column, e);
		if (!b)
		{
			return b;
		}
		Component comp = getEditorComponent();
		if (comp != null && _modForegroundColor != null)
		{
			if(comp instanceof JScrollPane)
			{
				comp = ((JScrollPane)comp).getViewport().getView();
			}
			if (comp instanceof javax.swing.text.JTextComponent)
			{
				final javax.swing.text.JTextComponent textComp = (javax.swing.text.JTextComponent)comp;
				textComp.setBorder(_defaultBorder);
				Document doc = ((javax.swing.text.JTextComponent) comp).getDocument();
				doc.removeDocumentListener(this);
				doc.addDocumentListener(this);
				if ( e instanceof MouseEvent )
				{ // only on a mouse click
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							textComp.selectAll();
						}
					});
				}
			}
			else if (comp instanceof javax.swing.AbstractButton)
			{
				AbstractButton ab = (AbstractButton) comp;
				ab.removeActionListener(this);
				ab.addActionListener(this);
			}
			else if (comp instanceof javax.swing.JComboBox)
			{
				JComboBox cb = (JComboBox) comp;
				cb.removeItemListener(this);
				cb.addItemListener(this);
			}
		}
		return (b);
	}

	/**
	 *  table model data has changed
	 *
	 *@param  e  Description
	 */
	
	public void tableChanged(javax.swing.event.TableModelEvent e)
	{
		super.tableChanged(e);
		//event for model.fireTableStructureChanged
		//if the table's structure was changed, clear
		//the modified flag.
		if (e.getLastRow() == TableModelEvent.HEADER_ROW)
		{
			this.setModified(false);
		}
		if (_tableMinMaxEntries == null)
		{
			return;
		}
		int size = _tableMinMaxEntries.size();
		if (size < 1)
		{
			return;
		}
		int col = e.getColumn();
		int row = e.getFirstRow();

		getValueAt(row, col);
		MinMaxEntry mme;
//		boolean minMaxChange = false;

		for (int i = 0; i < size; i++)
		{
			mme = (MinMaxEntry) _tableMinMaxEntries.elementAt(i);
			if (mme._col == col)
			{
				mme._isCurrent = false;
//System.out.println("column " + col +" changed isCurrent="+mme._isCurrent+" value="+getValueAt(row,col));
			}
		}
	}

	/**
	 *  editor listener methods
	 *
	 *@param  e  Description
	 */
	public void itemStateChanged(java.awt.event.ItemEvent e)
	{
		if (e.getStateChange() != java.awt.event.ItemEvent.SELECTED)
		{
			return;
		}
		setModified(true);
	}

	/**
	 *  Method Description
	 *
	 *@param  e  Description
	 */
	public void changedUpdate(javax.swing.event.DocumentEvent e) { }

	/**
	 *  Method Description
	 *
	 *@param  e  Description
	 */
	public void insertUpdate(javax.swing.event.DocumentEvent e)
	{
		setModified(true);
	}

	/**
	 *  Method Description
	 *
	 *@param  e  Description
	 */
	public void removeUpdate(javax.swing.event.DocumentEvent e)
	{
		setModified(true);
	}

	/**
	 *  get the min and max values for a column
	 *
	 *@param  col  Description
	 *@return      The MinMax value
	 */
	public Object[] getMinMax(int col)
	{

		int size = _tableMinMaxEntries.size();
		Object[] minMax = new Object[2];
		for (int i = 0; i < size; i++)
		{
			MinMaxEntry mme = (MinMaxEntry) _tableMinMaxEntries.elementAt(i);
			if (mme._col == col)
			{
				if (!mme._isCurrent)
				{
					Class type = getModel().getColumnClass(col);

					int rows = getRowCount();
					Object obj1;
//					Object obj2;
					int comp;
					// not current so recompute
					mme._min = getValueAt(0, col);
					mme._max = getValueAt(0, col);
					for (int ii = 1; ii < rows; ii++)
					{
						obj1 = getValueAt(ii, col);
						if (obj1.toString().length() < 1)
						{
							continue;
						}
						comp = compareValues(mme._min, obj1, type);
						if (comp > 0)
						{
							mme._min = obj1;
						}
						comp = compareValues(mme._max, obj1, type);
						if (comp < 0)
						{
							mme._max = obj1;
						}
					}
					mme._isCurrent = true;
				}
				if (mme._isCurrent)
				{
					minMax[0] = mme._min;
					minMax[1] = mme._max;
					return minMax;
				}
			}
		}
		return null;
	}

	/**
	 *  Method Description
	 *
	 *@param  newVal  Description
	 *@param  oldVal  Description
	 *@param  type    Description
	 *@return         Description
	 */
	private int compareValues(Object newVal, Object oldVal, Class type)
	{
		if (newVal == null)
		{
			return 0;
		}
		String sNewVal = newVal.toString();
		String sOldVal = oldVal.toString();
		Class numClass = getClassForName("java.lang.Number");
		if (type == numClass || type.getSuperclass() == numClass)
		{
			double d1;
			double d2;
			try
			{
				d1 = new Double(sNewVal).doubleValue();
			}
			catch (NumberFormatException nfe)
			{
				d1 = 0.;
			}
			try
			{
				d2 = new Double(sOldVal).doubleValue();
			}
			catch (NumberFormatException nfe)
			{
				d2 = 0.;
			}

			if (d1 < d2)
			{
				return -1;
			}
			else if (d1 > d2)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else if (type == getClassForName("java.util.Date"))
		{

			long n1 = new Date(sNewVal).getTime();
			long n2 = new Date(sOldVal).getTime();

			if (n1 < n2)
			{
				return -1;
			}
			else if (n1 > n2)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else if (type == getClassForName("java.lang.String"))
		{
			int result = sNewVal.compareTo(sOldVal);

			if (result < 0)
			{
				return -1;
			}
			else if (result > 0)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else if (type == getClassForName("java.lang.Boolean"))
		{
			boolean b1 = new Boolean(sNewVal).booleanValue();
			boolean b2 = new Boolean(sOldVal).booleanValue();

			if (b1 == b2)
			{
				return 0;
			}
			else if (b1)
			{
				// Define false < true
				return 1;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			int result = sNewVal.compareTo(sOldVal);

			if (result < 0)
			{
				return -1;
			}
			else if (result > 0)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}

	/**
	 *@param  editor  Description
	 *@param  row     Description
	 *@param  column  Description
	 *@return         Description
	 */
	/**
	 *  private void setUpDecimalEditor(JTable table) { /Set up the editor for the
	 *  integer cells. final RMADecimalField decimalField = new RMADecimalField(0,
	 *  5); decimalField.setHorizontalAlignment(decimalField.RIGHT);
	 *  DefaultCellEditor decimalEditor = new DefaultCellEditor(decimalField) {
	 *  /Override DefaultCellEditor's getCellEditorValue method /to return an
	 *  Integer, not a String: public Object getCellEditorValue() { return new
	 *  Double(decimalField.getValue()); } }; table.setDefaultEditor(Double.class,
	 *  decimalEditor); }
	 *
	 *@param  editor  Description
	 *@param  row     Description
	 *@param  column  Description
	 *@return         Description
	 */
	/**
	 *  private void setUpDecimalEditor(JTable table) { /Set up the editor for the
	 *  integer cells. final RMADecimalField decimalField = new RMADecimalField(0,
	 *  5); decimalField.setHorizontalAlignment(decimalField.RIGHT);
	 *  DefaultCellEditor decimalEditor = new DefaultCellEditor(decimalField) {
	 *  /Override DefaultCellEditor's getCellEditorValue method /to return an
	 *  Integer, not a String: public Object getCellEditorValue() { return new
	 *  Double(decimalField.getValue()); } }; table.setDefaultEditor(Double.class,
	 *  decimalEditor); }
	 *
	 *  Prepares the specified editor using the value at the specified cell.
	 *
	 *@param  editor  Description
	 *@param  row     Description
	 *@param  column  Description
	 *@return         Description
	 */
	
	public Component prepareEditor(TableCellEditor editor, int row, int column)
	{
		Object value = getValueAt(row, column);
		boolean isSelected = isCellSelected(row, column);
		Component comp = editor.getTableCellEditorComponent(this, value, isSelected,
				row, column);
		Component originalComponent = comp;
		if(comp instanceof JScrollPane)
		{
			comp = ((JScrollPane)comp).getViewport().getView();
		}

		if ( comp instanceof JComponent )
		{
			((JComponent)comp).setNextFocusableComponent(this);

		}
		if ((comp != null) && (comp.getFont() == null))
		{
			comp.setFont(getFont());
		}

		if ((comp != null) && comp instanceof javax.swing.text.JTextComponent)
		{
			/*
			final JTextComponent textComp = (JTextComponent)comp;

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					textComp.requestFocus();
					textComp.setCaretPosition(0);
					textComp.selectAll();
				}
			});
			*/
			((javax.swing.text.JTextComponent) comp).selectAll();
		}
		return originalComponent;
	}


	/**
	 *  return the number of rows in the table model
	 *
	 *@return    The NumRows value
	 */
	public int getNumRows()
	{
		return getRowCount();
	}

	/**
	 *  return a Vector from the table model for row rowNum
	 *
	 *@param  rowNum  Description
	 *@return         The Row value
	 */
	public Vector getRow(int rowNum)
	{
		if (getModel() instanceof RmaTableModelInterface)
		{
			return ((RmaTableModelInterface) getModel()).getRow(rowNum);
		}
		return null;
	}

	/**
	 *  return the scroll pane that this table is residing in this is the component
	 *  that needs added to the screen
	 *
	 *@return    The ScrollPane value
	 */
	public JScrollPane getScrollPane()
	{
		if (!_addedToScrollPane)
		{
			_scrollPane.setViewportView(this);
			_addedToScrollPane = true;
		}
		return _scrollPane;
	}

	public void setRowSelectionInterval(int index0, int index1, boolean scrollToSelection) {
		super.setRowSelectionInterval(index0,index1);
		if (scrollToSelection && _addedToScrollPane && _scrollPane != null)
		{
			JScrollBar vertBar = _scrollPane.getVerticalScrollBar();
			int min = vertBar.getMinimum();
			int max = vertBar.getMaximum();
			if (index0 < 1)
			{
				vertBar.setValue(min);
			}
			else if (index0 > this.getRowCount())
			{
				vertBar.setValue(max);
			}
			else
			{
				float rowPercent = ((float)index0)/this.getRowCount();
				int scrollVal = (int)(((min)*rowPercent) + ((max)*rowPercent));
				//now check to see if the scrollValue is already visible..
				int barVal = vertBar.getValue();
				int vis = vertBar.getVisibleAmount();
				if (scrollVal < barVal || scrollVal > (barVal+vis))
				{
					vertBar.setValue(scrollVal);
				}
			}
		}
	}


	/**
	 *  returns whether this table has been modified
	 *
	 *@return    The Modified value
	 */
	public boolean isModified()
	{
		return _modified;
	}

	/**
	 *  sets whether this table has been modified
	 *
	 *@param  modified  The new Modified value
	 */
	public void setModified(boolean modified)
	{
		_modified = modified;

		if (_modForegroundColor != null)
		{
			if (modified)
			{
				//setForeground(_modForegroundColor);
			}
			else
			{
				setForeground(UIManager.getColor("Table.foreground"));
				Enumeration e = _cellForeground.keys();
				Color c;
				Object key;
				boolean aChange = false;
				// remove modified cell colors
				while ( e.hasMoreElements() )
				{
					key = e.nextElement();
					c = (Color)_cellForeground.get(key);
					if ( c == _modForegroundColor )
					{
						_cellForeground.remove(key);
						aChange = true;
					}
				}
				if ( aChange ) repaint();

			}
		}
		if (modified)
		{
			rma.util.RMAUtil.setParentModified(this);
		}
	}

	/**
	 *  sets the parent for this dialog
	 *
	 *@param  parent  The new Parent value
	 */
	public void setParent(Component parent)
	{
		_parent = parent;
	}

	/**
	 *  set the column widths to the array of widths.
	 *
	 *@param  widths  The new ColumnWidths value
	 */
	public void setColumnWidths(int[] widths)
	{
		if (widths == null || widths.length == 0)
		{
			return;
		}
		for (int i = 0; i < widths.length; i++)
		{
			setColumnWidth(i, widths[i]);
		}
	}

	/**
	 *  Sets the MinMax attribute of the RmaJTable object
	 *
	 *@param  mme  The new MinMax value
	 */
	private void setMinMax(MinMaxEntry mme)
	{
		if (mme == null)
		{
			return;
		}
		int col = mme._col;
		if (col >= getColumnCount())
		{
			return;
		}
		int rows = getRowCount();
		for (int i = 0; i < rows; i++)
		{
			Object obj = getValueAt(i, col);
			mme.checkMin(obj, i);
			mme.checkMax(obj, i);
		}
	}

	/**
	 *  set tooltips for the headers to display whats in the header cell
	 */
	public void setDefaultHeaderToolTipText()
	{
//		int column;

		if (getColumnModel() == null)
		{
			return;
		}

		for (int i = 0; i < getColumnModel().getColumnCount(); i++)
		{
			TableColumn aColumn = getColumnModel().getColumn(i);
			TableCellRenderer renderer = aColumn.getHeaderRenderer();
			if (renderer == null)
			{
				continue;
			}
			Component component = renderer.getTableCellRendererComponent(
					this, aColumn.getHeaderValue(), false, false,
					-1, i);
			if (component instanceof JLabel)
			{
				JLabel jlabel = (JLabel) component;
				jlabel.setToolTipText(jlabel.getText());
			}
		}
	}

	/**
	 *  set the column width for a given column
	 *
	 *@param  colNum  The new ColumnWidth value
	 *@param  width   The new ColumnWidth value
	 */
	public void setColumnWidth(int colNum, int width)
	{
		if (colNum >= getColumnCount())
		{
			return;
		}

		TableColumn tc = getColumnModel().getColumn(colNum);

		if (tc == null)
		{
			System.out.println("setColumnWidth(): TableColumn at col " + colNum + " is null");
			return;
		}
		if (width == -1)
		{
			Object obj = tc.getHeaderValue();
			if (obj instanceof String)
			{
				String st = (String) obj;
				int newWidth = st.length() * 5;
				tc.setPreferredWidth(newWidth);
			}
		}
		else
		{
			tc.setPreferredWidth(width);
		}
	}

	/**
	 *  popup mouse selection has occurred
	 *
	 *@param  event  Description
	 */
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
/*
		if (event.getSource() instanceof AbstractButton)
		{
			setModified(true);
			//return;
		}
*/
		String command = event.getActionCommand();

		if ("copy".equals(command))
		{
			copy(true);
		}
		else if ("paste".equals(command))
		{
			paste();
		}
		else if ("cut".equals(command))
		{
			cut();
		}
		else if ("undo".equals(command))
		{
			undo();
		}
		else if ("clear".equals(command))
		{
			clear(true);
		}
		else if ("selectall".equals(command))
		{
			selectAll();
		}
		else if ("insertrow".equals(command))
		{
			insertRow();
		}
		else if ("deleterow".equals(command))
		{
			deleteRow();
		}
		else if ("appendrow".equals(command))
		{
			appendRow();
		}
		else if ("fill".equals(command))
		{
			fill();
		}
		else if ("linearFill".equals(command))
		{
			linearFill();
		}
		else if ("repeatFill".equals(command))
		{
			repeatFill();
		}
		else if ("print".equals(command))
		{
			printData();
		}
		else if ("printPreview".equals(command))
		{
			printPreview();
		}
		else if ("export".equals(command))
		{
			exportData();
		}
		else if ("sumselected".equals(command))
		{
			sumColumn (false);
		}
		else if ("sumcolumn".equals(command))
		{
			sumColumn (true);
		}

	}

	/**
	 *  when RowSelectionAllowed is set it will return a vector of vectors that
	 *  contain the selected rows
	 *
	 *@return    The SelectedRowsVector value
	 */
	public Vector getSelectedRowsVector()
	{
		Vector selectedData = new Vector();

		if (!getRowSelectionAllowed())
		{
			return selectedData;
		}
		int rows[] = getSelectedRows();
		Vector tableData = getCells();
		for (int i = 0; i < rows.length; i++)
		{
			if (i > tableData.size())
			{
				break;
			}
			selectedData.addElement(tableData.elementAt(rows[i]));
		}
		return selectedData;
	}

	/**
	 *  returns the first selected cell's data
	 *
	 *@return    The SelectedCellData value
	 */
	public Object getSelectedCellData()
	{
		int row = getSelectedRow();
		int col = getSelectedColumn();
		if (row > -1 && col > -1)
		{
			return (getValueAt(row, col));
		}
		return (null);
	}

	/**
	 *  set the number of columns
	 *
	 *@param  columns  The new NumColumns value
	 */
	public void setNumColumns(int columns)
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}
		String colName = "";
		int colCount = getColumnCount();

		//add columns if necessary
		while (columns > getColumnCount())
		{
			((RmaTableModelInterface) getModel()).addColumn(colName);
			revalidate();
			// add popup to cell editor
			TableColumn tc;
			for (int i = colCount; i < columns; i++)
			{
				if (i >= getColumnModel().getColumnCount())
				{
					break;
				}
				tc = getColumnModel().getColumn(i);
				if (tc == null)
				{
					return;
				}
				RmaJTextField tf = new RmaJTextField();
				tf.setBorder(_defaultBorder);
				tf.addMouseListener(this);
				RmaCellEditor dcf = new RmaCellEditor(tf);
				dcf.setClickCountToStart(_clickCountToStart);
				//dcf.addCellEditorListener(this);
				tc.setCellEditor(dcf);
			}

		}
	}

	public int getClickCountToStart()
	{
		return _clickCountToStart;
	}
	/**
	 *  initialize the row backgrounds
	 */
	private void initRowBackgrounds()
	{
		Color color = this.getBackground();
		for (int i = 0; i < getModel().getRowCount(); i++)
		{
			_rowBackground.put(new Integer(i), color);
		}
	}

	/**
	 *  initialize the row foregrounds
	 */
	private void initRowForegrounds()
	{
		Color color = this.getForeground();
		for (int i = 0; i < getModel().getRowCount(); i++)
		{
			_rowForeground.put(new Integer(i), color);
		}
	}

	/**
	 *  set the foreground color for the row at rowNum
	 *
	 *@param  rowNum   The new RowForeground value
	 *@param  rfColor  The new RowForeground value
	 */
	public void setRowForeground(int rowNum, Color rfColor)
	{
		if (rfColor == null)
		{
			_rowForeground.remove(new Integer(rowNum));
			return;
		}
		_rowForeground.put(new Integer(rowNum), rfColor);
	}

	/**
	 *  set the background color for the row at rowNum
	 *
	 *@param  rowNum   The new RowBackground value
	 *@param  rbColor  The new RowBackground value
	 */
	public void setRowBackground(int rowNum, Color rbColor)
	{
		if (rbColor == null)
		{
			_rowBackground.remove(new Integer(rowNum));
			return;
		}
		_rowBackground.put(new Integer(rowNum), rbColor);
	}

	/**
	 *  Clears all Row backgrounds set by setRowBackground()
	 */
	public void clearRowBackgrounds()
	{
		_rowBackground.clear();
	}

	/**
	 *  get the background color for the row at rowNum
	 *
	 *@param  rowNum  Description
	 *@return         The RowBackground value
	 */
	public Color getRowBackground(int rowNum)
	{
		Color color = (Color) _rowBackground.get(new Integer(rowNum));
		if (color == null)
		{
			return this.getBackground();
		}
		return color;
	}

	/**
	 *  Gets the RowBackground attribute of the RmaJTable object
	 *
	 *@param  rowNum  Description
	 *@param  colNum  Description
	 *@return         The RowBackground value
	 */
	public Color getRowBackground(int rowNum, int colNum)
	{
		Color color = (Color) _rowBackground.get(new Integer(rowNum));
		if (color == null)
		{
			return getColumnBackground(colNum);
		}
		return color;
		/*
		 *  if ( getModel().isCellEditable(rowNum, colNum))
		 *  {
		 *  Color color =  (Color)_rowBackground.get(new Integer(rowNum));
		 *  if ( color == null )
		 *  return getColumnBackground(colNum);
		 *  return color;
		 *  }
		 *  else
		 *  {
		 *  return UIManager.getColor("Table.disabledCellColor");
		 *  }
		 */
	}

	/**
	 *  Sets the ColumnBackground attribute of the RmaJTable object
	 *
	 *@param  colNum  The new ColumnBackground value
	 *@param  color   The new ColumnBackground value
	 */
	public void setColumnBackground(int colNum, Color color)
	{
		_columnBackground.put(new Integer(colNum), color);
	}

	/**
	 *  Sets the ColumnForeground attribute of the RmaJTable object
	 *
	 *@param  colNum  The new ColumnForeground value
	 *@param  color   The new ColumnForeground value
	 */
	public void setColumnForeground(int colNum, Color color)
	{
		_columnForeground.put(new Integer(colNum), color);
	}

	/**
	 *  get the background color for the
	 *
	 *@param  colNum  Description
	 *@return         The ColumnBackground value
	 */
	public Color getColumnBackground(int colNum)
	{
		Color color = (Color) _columnBackground.get(new Integer(colNum));
		if (color == null)
		{
			return getBackground();
		}
		return color;
	}

	/**
	 *  Gets the ColumnForeground attribute of the RmaJTable object
	 *
	 *@param  colNum  Description
	 *@return         The ColumnForeground value
	 */
	public Color getColumnForeground(int colNum)
	{
		Color color = (Color) _columnForeground.get(new Integer(colNum));
		if (color == null)
		{
			return super.getForeground();
		}
		return color;
	}

	/**
	 *  Sets the ModifiedForegroundColor attribute of the RmaJTable object
	 *
	 *@param  color  The new ModifiedForegroundColor value
	 */
	public void setModifiedForegroundColor(Color color)
	{
		_modForegroundColor = color;
	}

	/**
	 *  Gets the ModifiedForegroundColor attribute of the RmaJTable object
	 *
	 *@return    The ModifiedForegroundColor value
	 */
	public Color getModifiedForegroundColor()
	{
		return _modForegroundColor;
	}

	/**
	 *  set the background for cells that get pasted into
	 *
	 *@param  color  The new PasteBackground value
	 */
	public void setPasteBackground(Color color)
	{
		_pasteBackground = color;
	}

	/**
	 *  set the foreground for cells that get pasted into
	 *
	 *@param  color  The new PasteForeground value
	 */
	public void setPasteForeground(Color color)
	{
		_pasteForeground = color;
	}

	/**
	 *@return    The PasteBackground value
	 */
	public Color getPasteBackground()
	{
		return _pasteBackground;
	}

	/**
	 *@return    The PasteForeground value
	 */
	public Color getPasteForeground()
	{
		return _pasteForeground;
	}

	/**
	 *@param  row      The new CellBackground value
	 *@param  column   The new CellBackground value
	 *@param  rbColor  The new CellBackground value
	 */
	public void setCellBackground(int row, int column, Color rbColor)
	{
		_cellBackground.put("" + row + "," + column, rbColor);
	}

	/**
	 *@param  row      The new CellForeground value
	 *@param  column   The new CellForeground value
	 *@param  rbColor  The new CellForeground value
	 */
	public void setCellForeground(int row, int column, Color rbColor)
	{
		_cellForeground.put("" + row + "," + column, rbColor);
	}

	/**
	 *@param  row      The new CellBackground value
	 *@param  column   The new CellBackground value
	 *@param  font  The new CellBackground value
	 */
	public void setCellFont(int row, int column, Font font)
	{
		_cellFonts.put("" + row + "," + column, font);
	}

	public Font getCellFont(int row, int column)
	{
		Font f = (Font)_cellFonts.get(""+row+","+column);
		return f;
	}
	/**
	 *@param  row     Description
	 *@param  column  Description
	 *@return         The CellBackground value
	 */
	public Color getCellBackground(int row, int column)
	{
		Color col;
		col = (Color) _cellBackground.get("" + row + "," + column);
		if (col == null)
		{
			return getRowBackground(row, column);
		}
		return col;
	}
	/**
	 * get the Color to use for disabled cell backgrounds
	 * @param row the cell's row
	 * @param col the cell's column
	 * @return UIManager's <b>TextField.disabledBackground</b> color by default
	 */
	public Color getDisabledBackground(int row, int col)
	{
		return UIManager.getDefaults().getColor( "TextField.disabledBackground" );
	}
	/**
	 *  Gets the CellForeground attribute of the RmaJTable object
	 *
	 *@param  row     Description
	 *@param  column  Description
	 *@return         The CellForeground value
	 */
	public Color getCellForeground(int row, int column)
	{
		Color col;
		col = (Color) _cellForeground.get("" + row + "," + column);
		if (col == null)
		{
			return getRowForeground(row, column);
		}
		return col;
	}

	/**
	 *  get the foreground color for the row at rowNum
	 *
	 *@param  rowNum  Description
	 *@return         The RowForeground value
	 */
	public Color getRowForeground(int rowNum)
	{
		Color color = (Color) _rowForeground.get(new Integer(rowNum));

		if (color == null)
		{
			return this.getForeground();
		}
		return color;
	}

	/**
	 *@param  rowNum  Description
	 *@param  colNum  Description
	 *@return         The RowForeground value
	 */
	public Color getRowForeground(int rowNum, int colNum)
	{
		Color color = (Color) _rowForeground.get(new Integer(rowNum));

		if (color == null)
		{
			return getColumnForeground(colNum);
		}
		return color;
	}

	/**
	 *  Sets the Precision attribute of the RmaJTable object
	 *
	 *@param  p  The new Precision value
	 */
	public void setPrecision0(int p)
	{
		if (p < 0)
		{
			p = 0;
		}
		_precision = p;
		if (_decCellRend != null)
		{
			_decCellRend.setPrecision(p);
		}

	}

	public void setAlternatingReportBackground(boolean b)
	{
		_alternatingReportBackground = b;
	}
	public boolean getAlternatingReportBackground()
	{
		return _alternatingReportBackground;
	}

	/**
	 *  Gets the Precision attribute of the RmaJTable object
	 *
	 *@return    The Precision value
	 */
	public int getPrecision0()
	{
		return _precision;
	}

	/**
	 * Gets a column label (header).
	 *
	 * @param colNum The column for which to retrieve the label.
	 *
	 * @return The column label.
	 */
	public String getColumnLabel(int colNum)
	{
		String label = null;
		TableColumnModel tcm = getColumnModel();
		if (colNum >= 0 && colNum < tcm.getColumnCount()) {
			label = (String)tcm.getColumn(colNum).getHeaderValue();
		}
		return label;
	}

	/**
	 *  Gets the column headers
	 *
	 *@return    The ColumnLabels value
	 */
	public String[] getColumnLabels()
	{
		TableColumnModel tcm = getColumnModel();
		String[] colLabels = new String[tcm.getColumnCount()];
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			colLabels[i] = (String) tcm.getColumn(i).getHeaderValue();
		}
		return colLabels;
	}

	/**
	 * Sets a column label (header).
	 *
	 * @param colNum The column for which to set the label.
	 * @param label  The label text.
	 */
	public void setColumnLabel(int colNum, String label) {

		String[] labels = getColumnLabels();
		if (colNum >=0 && colNum < labels.length) {
			labels[colNum] = label;
			setColumnLabels(labels);
		}
	}

	/**
	 *  sets the column headers
	 *
	 *@param  colNames  The new ColumnLabels value
	 */
	public void setColumnLabels(String[] colNames)
	{
		TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < colNames.length; i++)
		{
			if (i > getColumnCount() || i >= tcm.getColumnCount())
			{
				return;
			}
			tcm.getColumn(i).setHeaderValue(colNames[i]);

		}
		// now set the column headers
		if (getModel() instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) getModel()).setColumnNames(colNames);
		}

		revalidate();
	}

	/**
	 * Gets the foreground color for a column header.
	 *
	 * @param colNum The column number.
	 *
	 * @return The foreground color of the column header.
	 */
	public Color getColumnHeaderForeground(int colNum) {

		Color color = null;
		TableColumnModel tcm = getColumnModel();
		if (colNum >= 0 && colNum < tcm.getColumnCount()) {
			TableCellRenderer tcr = tcm.getColumn(colNum).getHeaderRenderer();
			if (tcr == null) tcr = getTableHeader().getDefaultRenderer();
			color = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum).getForeground();
		}
		return color;
	}

	/**
	 * Sets the foreground color for a column header.
	 *
	 * @param colNum The column number.
	 * @param color  The foreground color.
	 */
	public void setColumnHeaderForeground(int colNum, Color color) {

		//--------------------------------------------------------------------------------------//
		// WARNING: This implementation depends on being able to use the default (no parameter) //
		// 	    constructor of the current TableCellRenderer in order to duplicate it.      //
		//          This assumption may need to be re-visited if the things start flaking out.  //
		//--------------------------------------------------------------------------------------//

		if (color != null) {
			TableColumnModel tcm = getColumnModel();
			if (colNum >= 0 && colNum < tcm.getColumnCount()) {
				boolean makeNew = false;
				TableCellRenderer tcr = tcm.getColumn(colNum).getHeaderRenderer();
				if (tcr == null) tcr = getTableHeader().getDefaultRenderer();
				Component component = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum);
				Color background = component.getBackground();
				Font font = component.getFont();
				Class tcrClass = ((Object)tcr).getClass();
				try {
					tcr = (TableCellRenderer)tcrClass.getConstructors()[0].newInstance();
					if (tcr instanceof MleHeadRenderer) {
						((MleHeadRenderer)tcr).setAllowDefaultFont(true);
						((MleHeadRenderer)tcr).setFont(font);
					}
					component = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum);
					component.setForeground(color);
					component.setBackground(background);
					component.setFont(font);
					tcm.getColumn(colNum).setHeaderRenderer(tcr);
				}
				catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Gets the background color for a column header.
	 *
	 * @param colNum The column number.
	 *
	 * @return The background color of the column header.
	 */
	public Color getColumnHeaderBackground(int colNum) {

		Color color = null;
		TableColumnModel tcm = getColumnModel();
		if (colNum >= 0 && colNum < tcm.getColumnCount()) {
			TableCellRenderer tcr = tcm.getColumn(colNum).getHeaderRenderer();
			if (tcr == null) tcr = getTableHeader().getDefaultRenderer();
			color = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum).getBackground();
		}
		return color;
	}

	/**
	 * Sets the background color for a column header.
	 *
	 * @param colNum The column number.
	 * @param color The background color.
	 */
	public void setColumnHeaderBackground(int colNum, Color color) {

		//--------------------------------------------------------------------------------------//
		// WARNING: This implementation depends on being able to use the default (no parameter) //
		// 	    constructor of the current TableCellRenderer in order to duplicate it.      //
		//          This assumption may need to be re-visited if the things start flaking out.  //
		//--------------------------------------------------------------------------------------//

		if (color != null) {
			TableColumnModel tcm = getColumnModel();
			if (colNum >= 0 && colNum < tcm.getColumnCount()) {
				boolean makeNew = false;
				TableCellRenderer tcr = tcm.getColumn(colNum).getHeaderRenderer();
				if (tcr == null) tcr = getTableHeader().getDefaultRenderer();
				Component component = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum);
				Color foreground = component.getForeground();
				Font font = component.getFont();
				Class tcrClass = ((Object)tcr).getClass();
				try {
					tcr = (TableCellRenderer)tcrClass.getConstructors()[0].newInstance();
					if (tcr instanceof MleHeadRenderer) {
						((MleHeadRenderer)tcr).setAllowDefaultFont(true);
						((MleHeadRenderer)tcr).setFont(font);
					}
					component = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum);
					component.setForeground(foreground);
					component.setBackground(color);
					component.setFont(font);
					tcm.getColumn(colNum).setHeaderRenderer(tcr);
				}
				catch (Exception e) {
				}
			}
		}
	}


	/**
	 * Gets the font for a column header.
	 *
	 * @param colNum The column number.
	 *
	 * @return The font of the column header.
	 */
	public Font getColumnHeaderFont(int colNum) {

		Font font = null;
		TableColumnModel tcm = getColumnModel();
		if (colNum >= 0 && colNum < tcm.getColumnCount()) {
			TableCellRenderer tcr = tcm.getColumn(colNum).getHeaderRenderer();
			if (tcr == null) tcr = getTableHeader().getDefaultRenderer();
			font = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum).getFont();
		}
		return font;
	}

	/**
	 * Sets the foreground color for a column header.
	 *
	 * @param colNum The column number.
	 * @param font   The font.
	 */
	public void setColumnHeaderFont(int colNum, Font font) {

		//--------------------------------------------------------------------------------------//
		// WARNING: This implementation depends on being able to use the default (no parameter) //
		// 	    constructor of the current TableCellRenderer in order to duplicate it.      //
		//          This assumption may need to be re-visited if the things start flaking out.  //
		//--------------------------------------------------------------------------------------//

		if (font != null) {
			TableColumnModel tcm = getColumnModel();
			if (colNum >= 0 && colNum < tcm.getColumnCount()) {
				boolean makeNew = false;
				TableCellRenderer tcr = tcm.getColumn(colNum).getHeaderRenderer();
				if (tcr == null) tcr = getTableHeader().getDefaultRenderer();
				Component component = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum);
				Color foreground = component.getForeground();
				Color background = component.getBackground();
				Class tcrClass = ((Object)tcr).getClass();
				try {
					tcr = (TableCellRenderer)tcrClass.getConstructors()[0].newInstance();
					if (tcr instanceof MleHeadRenderer) {
						((MleHeadRenderer)tcr).setAllowDefaultFont(true);
						((MleHeadRenderer)tcr).setFont(font);
					}
					component = tcr.getTableCellRendererComponent(this, "abc", false, false, -1, colNum);
					component.setForeground(foreground);
					component.setBackground(background);
					component.setFont(font);
					tcm.getColumn(colNum).setHeaderRenderer(tcr);
				}
				catch (Exception e) {
				}
			}
		}
	}

	/**
	 *  show or hide column colNum. If showing set the column width to preferred
	 *  size hiding a column is based on the TableColumnModel showing a column is
	 *  based on the TableModel
	 *
	 *@param  colNum                        The new ColumnVisible value
	 *@param  show                          The new ColumnVisible value
	 *@param  preferedWidth                 The new ColumnVisible value
	 *@return                               Description
	 *@exception  IllegalArgumentException  Description
	 */
	public TableColumn setColumnVisible(int colNum, boolean show, int preferedWidth) throws IllegalArgumentException
	{
		return setColumnVisible(colNum, colNum, show, preferedWidth);
	}

	/**
	 *  show or hide a column.
	 *
	 *@param  tmColNum                      The new ColumnVisible value
	 *@param  tcmColNum                     The new ColumnVisible value
	 *@param  show                          The new ColumnVisible value
	 *@param  preferedWidth                 The new ColumnVisible value
	 *@return                               Description
	 *@exception  IllegalArgumentException  Description
	 */
	public TableColumn setColumnVisible(int tmColNum, int tcmColNum, boolean show, int preferedWidth) throws IllegalArgumentException
	{

		int modelColNum = getModel().getColumnCount();
		if (tmColNum < 0)
		{
			throw (new IllegalArgumentException("Table Model Column " + tmColNum + " less than 0"));
		}
		if (tmColNum > modelColNum)
		{
			throw (new IllegalArgumentException("Table Model Column " + tmColNum + " greater than number of columns in Table model " + modelColNum));
		}
		if (tcmColNum < 0)
		{
			throw (new IllegalArgumentException("Table Column Model Column " + tcmColNum + " less than 0"));
		}
		TableColumn tc;
		TableColumnModel tcm = getColumnModel();

		if (!show)
		{
			tc = tcm.getColumn(tcmColNum);
			removeColumn(tc);
		}
		else
		{
			// show column

			tc = new TableColumn(tmColNum, preferedWidth);
			if (_mleheadrend != null)
			{
				tc.setHeaderRenderer(_mleheadrend);
			}
			else if (_unitsHeadRend != null)
			{
				tc.setHeaderRenderer(_unitsHeadRend);
			}
			tc.setCellRenderer(_rmaCellRend);
			Class cls = getModel().getColumnClass(tcmColNum);
			_rmaCellRend.setDisplayUnitsSystem(getDisplayUnitSystem());

			ParameterScale ps = (ParameterScale) _parameterScaleTable.get(new Integer(tcmColNum));
			if (ps != null)
			{
				_rmaCellRend.setDisplayScaleFactor(tcmColNum, ps.paramId, ps.scale);
			}

			//tc = tcm.getColumn(colNum);
			addColumn(tc);

			if (tcmColNum < tcm.getColumnCount())
			{
				tcm.moveColumn(tcm.getColumnCount() - 1, tcmColNum);
			}
			// make sure that the mouselistener get set so that the popup menu works
			TableCellEditor tce = getDefaultEditor(cls);
			tc.setCellEditor(tce);
			if (tce instanceof RmaCellEditor)
			{
				RmaCellEditor dce = (RmaCellEditor) tce;
				dce.setClickCountToStart(_clickCountToStart);
				Component c = dce.getComponent();
				if (c != null)
				{
					c.removeMouseListener(this);
					c.addMouseListener(this);
				}
			}

			//sizeColumnsToFit(false);
		}
		revalidate();
		//sizeColumnsToFit(false);
		return tc;
	}

	/**
	 *  change the column model to display colNum number of columns
	 *
	 *@param  colNum  Description
	 */
	public void displayNumCol(int colNum)
	{
		if (colNum < 0)
		{
			return;
		}

		int modelColNum = getModel().getColumnCount();
		TableColumnModel tcm = getColumnModel();

		if (colNum >= getColumnCount())
		{
			// make column visible

			TableColumn newCol;
			for (int i = getColumnCount(); i < colNum; i++)
			{
				if (i > modelColNum)
				{
					System.out.println("columns " + i + " greater than model columns " + modelColNum);
					break;
				}
				newCol = new TableColumn(i);
				addColumn(newCol);
			}
			revalidate();
			sizeColumnsToFit(false);

			return;
		}

		// make column invisible

		TableColumn tc;
		for (int i = getColumnCount() - 1; i >= colNum; i--)
		{
			tc = tcm.getColumn(i);
			removeColumn(tc);
		}
		revalidate();
		sizeColumnsToFit(false);

	}
	public int getHorizontalAlignment(int col)
	{
		TableColumn tc =  getColumnModel().getColumn(col);
		TableCellRenderer tcr = tc.getCellRenderer();
		if ( tcr instanceof AlignTableCellRender )
		{
			AlignTableCellRender ar = (AlignTableCellRender)tc.getCellRenderer();
			return ar.getHorizontalAlignment();
		}
		else if ( tcr instanceof RmaCellRenderer )
		{
			RmaCellRenderer rcr = (RmaCellRenderer)tcr;
			return rcr.getHorizontalAlignment();
		}
		else
		{
			if (java.lang.Number.class.isAssignableFrom(getColumnClass(col)))
			{
				return SwingConstants.RIGHT;
			}
		}
		return SwingConstants.LEFT;
	}
	/**
	 *  sets the horizontal alignment for all table columns
	 *
	 *@param  align  The new HorizontalAlignment value
	 */
	public void setHorizontalAlignment(int align)
	{
		TableColumn tc;

		AlignTableCellRender rtcr = new AlignTableCellRender(align);
		rtcr.setDisplayUnitsSystem(getDisplayUnitSystem());

		for (int i = 0; i < getColumnCount(); i++)
		{
			tc = getColumnModel().getColumn(i);
			if (tc == null)
			{
				continue;
			}

			ParameterScale ps = (ParameterScale) _parameterScaleTable.get(new Integer(this.convertColumnIndexToModel(i)));
			if (ps != null)
			{
				rtcr.setDisplayScaleFactor(i, ps.paramId, ps.scale);
			}

			tc.setCellRenderer(rtcr);
		}
	}

	/**
	 *  sets the horizontal alignment for a given table column
	 *
	 *@param  align  The new HorizontalAlignment value
	 *@param  col    The column
	 */
	public void setHorizontalAlignment(int align, int col)
	{
		if (col >= getColumnCount())
		{
			return;
		}

		TableColumn tc;

		AlignTableCellRender rtcr = new AlignTableCellRender(align);
		rtcr.setDisplayUnitsSystem(getDisplayUnitSystem());
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return;
		}

		ParameterScale ps = (ParameterScale) _parameterScaleTable.get(new Integer(this.convertColumnIndexToModel(col)));
		if (ps != null)
		{
			rtcr.setDisplayScaleFactor(col, ps.paramId, ps.scale);
		}

		tc.setCellRenderer(rtcr);

	}

	/**
	 *  Set if a tab key should select the next editable cell in the row (if no
	 *  more editable cells, the tab just moves to the next cell)
	 *
	 *@param  tabToEditCell  The new TabToEditCell value
	 */

	public void setTabToEditCell(boolean tabToEditCell)
	{
		_tabToEditCell = tabToEditCell;
	}

	public RmaJDecimalField setCurrencyCellEditor(int col)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (_currencyCellRend == null)
		{
			_currencyCellRend = new CurrencyCellRenderer();
		}

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

		RmaJDecimalField ta = new RmaJDecimalField();
		ta.setPrecision(2);
		ta.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(ta);
		setHorizontalAlignment(SwingConstants.RIGHT, col);
		//dcf.addCellEditorListener(this);
		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);
		if (getModel() instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) getModel()).setColumnClass(col, java.lang.Number.class);
		}

		tc.setCellRenderer(_currencyCellRend);
		return ta;
	}
	/**
	 *  set the cell editor and renderer for multiple lines of text
	 *
	 *@param  col  The new TextAreaCellEditor value
	 *@return      Description
	 */
	public JTextArea setTextAreaCellEditor(int col,boolean addSrcollPane)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;
		if (_mlCellRend == null)
		{
			_mlCellRend = new MultiLineCellRenderer();
		}

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

		RmaJTextArea ta = new RmaJTextArea();
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		ta.setToolTipText("");
		ta.setFont(getFont());
		ta.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(ta,addSrcollPane);
		//dcf.addCellEditorListener(this);
		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);

		tc.setCellRenderer(_mlCellRend);
		_hasMultilineRenderer = true;
		return ta;
	}

	public JTextArea setTextAreaCellEditor(int col) {
		return setTextAreaCellEditor(col,true);
	}
	/**
	 *  set the editor to toggle between 2 key values.
	 *
	 *@param  col        The new ToggleCellEditor value
	 *@param  key1       The new ToggleCellEditor value
	 *@param  key2       The new ToggleCellEditor value
	 *@param  toggleKey  The new ToggleCellEditor value
	 */
	public void setToggleCellEditor(int col, char key1, char key2, char toggleKey)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return;
		}
		ToggleDocument td = new ToggleDocument();
		td.setToggleValues(key1, key2);
		td.setToggleKey(toggleKey);

		RmaJTextField tf = new RmaJTextField(td, "" + key1, 5);
		tf.setBorder(_defaultBorder);
		tf.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(tf);
		//dcf.addCellEditorListener(this);
		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);
	}

	/**
	 * Sets a button editor on the specified column. The button is returned to add
	 * ActionListeners or change its appearance.
	 */
	public JButton setButtonCellEditor(int col)
	{
		JButton button = new JButton();
		//button.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
		button.setFocusPainted(false);
		//button.setBorderPainted(true);
		//button.setBackground(this.getColumnBackground(col));
		RmaCellEditor ce = new RmaCellEditor(button);
		getColumnModel().getColumn(col).setCellEditor(ce);
		getColumnModel().getColumn(col).setCellRenderer(new ButtonRenderer());
		return button;
	}

	/**
	 *  set the cell editor for all the columns to be a RMADecimalField
	 */
	public void setDoubleCellEditor()
	{
		setDoubleCellEditor(false);
	}

	/**
	 *  set the cell editor for all the columns to be a RMADecimalField specifying
	 *  whether to show commas.
	 *
	 *@param  showFormatting  The new DoubleCellEditor value
	 */
	public void setDoubleCellEditor(boolean showFormatting)
	{
//		TableColumn tc;
		for (int i = 0; i < getColumnCount(); i++)
		{
			setDoubleCellEditor(i, showFormatting);
		}
	}

	/**
	 *  set the cell editor from beginCol to EndCol to be a RMADecimalField
	 *
	 *@param  beginCol  The new DoubleCellEditor value
	 *@param  endCol    The new DoubleCellEditor value
	 */
	public void setDoubleCellEditor(int beginCol, int endCol)
	{
		setDoubleCellEditor(beginCol, endCol, false);
	}

	/**
	 *  set the double cell editor from beginCol to endCol to be a RMADecimalField
	 *  if showFormatting is true then show with embedded commas.
	 *
	 *@param  beginCol        The new DoubleCellEditor value
	 *@param  endCol          The new DoubleCellEditor value
	 *@param  showFormatting  The new DoubleCellEditor value
	 */
	public void setDoubleCellEditor(int beginCol, int endCol, boolean showFormatting)
	{
		for (int i = beginCol; i <= endCol; i++)
		{
			setDoubleCellEditor(i, showFormatting);
		}
	}



	/**
	 *  set a cell editor for column col to be a RMADecimalField
	 *
	 *@param  col  The new DoubleCellEditor value
	 *@return      Description
	 */
	public RmaJDecimalField setDoubleCellEditor(int col)
	{
		return (setDoubleCellEditor(col, false));
	}

	/**
	 *  set a cell editor for column col to be a RMADecimalField
	 *
	 *@param  col             The new DoubleCellEditor value
	 *@param  showFormatting  The new DoubleCellEditor value
	 *@return                 Description
	 */
	public RmaJDecimalField setDoubleCellEditor(int col, boolean showFormatting)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;
		if (showFormatting && _decCellRend == null)
		{
			_decCellRend = new DecimalCellRenderer(_precision);
		}

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

		RmaJDecimalField df = new RmaJDecimalField(0,5);
		df.setPrecision(_precision);
		//df.setShowErrorMsg(false);
		df.setHorizontalAlignment(RmaJDecimalField.RIGHT);
		df.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(df);
		dcf.setDisplayUnitSystem(getDisplayUnitSystem());
		ParameterScale ps = (ParameterScale) _parameterScaleTable.get(new Integer(convertColumnIndexToModel(col)));
		if (ps != null)
		{
			dcf.setDisplayScaleFactor(ps.paramId, ps.scale);
		}

		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);

		if (showFormatting)
		{
			tc.setCellRenderer(_decCellRend);
		}
		else
		{
			setHorizontalAlignment(SwingConstants.RIGHT, col);
		}
		if (getModel() instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) getModel()).setColumnClass(col, java.lang.Number.class);
		}
		return df;
	}

	public RmaJIntegerField setIntegerCellEditor(int col)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

		RmaJIntegerField df = new RmaJIntegerField(0, 5);
		df.setHorizontalAlignment(RmaJDecimalField.RIGHT);
		df.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(df);
		dcf.setDisplayUnitSystem(getDisplayUnitSystem());
		ParameterScale ps = (ParameterScale) _parameterScaleTable.get(new Integer(convertColumnIndexToModel(col)));
		if (ps != null)
		{
			dcf.setDisplayScaleFactor(ps.paramId, ps.scale);
		}

		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);

		setHorizontalAlignment(SwingConstants.RIGHT, col);

		if (getModel() instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) getModel()).setColumnClass(col, java.lang.Number.class);
		}
		return df;
	}
	/**
	 *  set the cell editor to be Military time
	 *
	 *@param  col  The new TimeCellEditor value
	 *@return      Description
	 */
	public RmaJ24HourTimeField setTimeCellEditor(int col)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;
		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}
		RmaJ24HourTimeField mf = new RmaJ24HourTimeField();
		//mf.setShowErrorMsg(false);
		mf.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(mf);
		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);

		setHorizontalAlignment(SwingConstants.CENTER, col);
		if (getModel() instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) getModel()).setColumnClass(col, java.lang.Number.class);
		}
		return mf;
	}

	/**
	 *  set the cell editor to be a date
	 *
	 *@param  col     The new DateCellEditor value
	 *@param  format  The new DateCellEditor value
	 *@return         Description
	 */
	public RmaJCalendarField setDateCellEditor(int col, String format)
	{
		return (RmaJCalendarField)setDateCellEditor(col,format,true);
	}

	public RmaJDateField setDateCellEditor(int col, String format, boolean showCalendar)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;
		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}
		RmaJDateField df = null;
		if (showCalendar)
		{
			df = new RmaJCalendarField(format, "");
		}
		else
		{
			df = new RmaJDateField(format, "");
		}
		//df.setShowErrorMsg(false);
		df.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(df);
		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);

		setHorizontalAlignment(SwingConstants.LEFT, col);
		if (getModel() instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) getModel()).setColumnClass(col, java.lang.Number.class);
		}
		return df;
	}

	/**
	 *  set the cell editor to be a date
	 *
	 *@param  col  The new DssPathPartCellEditor value
	 *@return      Description
	 */
	public RmaJDssPathPartField setDssPathPartCellEditor(int col)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;
		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}
		RmaJDssPathPartField df = new RmaJDssPathPartField();
		//df.setShowErrorMsg(false);
		df.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(df);
		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(dcf);

		if (getModel() instanceof RmaTableModelInterface)
		{
			((RmaTableModelInterface) getModel()).setColumnClass(col, java.lang.Number.class);
		}
		return df;
	}
	/**
	 * clear out all the values in the JComboBoxes for the column col
	 * @param col the table column
	 */
	public void clearAllComboBoxRowEditorValues(int col)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return ;
		}

		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return ;
		}
		TableCellEditor tce = tc.getCellEditor();
		if ( tce instanceof RowCellEditor )
		{
			RowCellEditor rce = (RowCellEditor)tce;
			Map map = rce.getCellEditorTable();
			Collection values = map.values();
			Iterator iter =  values.iterator();
			Object obj;
			while (iter.hasNext())
			{
				obj = iter.next();
				if ( obj instanceof JComboBox )
				{
					((JComboBox)obj).removeAllItems();
				}
			}
		}
	}
	/**
	 *  Sets the component Editor for a specific row in a column
	 *
	 *@param  row   The new ComboBoxRowEditor value
	 *@param  col   The new ComboBoxRowEditor value
	 *@param  data  The new ComboBoxRowEditor value
	 */
	public JComboBox setComboBoxRowEditor(int row, int col, Vector data)
	{
		if (row < 0 || row >= this.getNumRows())
		{
			return null;
		}

		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}

		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

///        JComboBox cb = new JComboBox(data);
		RmaJComboBox cb = new RmaJComboBox(data);
		cb.setBorder(null);
		//for modification events

		cb.addItemListener(this);
		cb.addMouseListener(this);
		cb.getEditor().getEditorComponent().addMouseListener(this);
		TableCellEditor tce = tc.getCellEditor();

		RmaCellEditor dcf = new RmaCellEditor(cb);
		dcf.setClickCountToStart(1);//_clickCountToStart);

		if (tce instanceof RowCellEditor)
		{

			((RowCellEditor) tce).add(row, dcf);
		}
		else
		{
			RowCellEditor rce = new RowCellEditor();
			rce.add(row, dcf);
			rce.setClickCountToStart(1);//_clickCountToStart);
			tc.setCellEditor(rce);
		}
		dcf.setClickCountToStart(1);//_clickCountToStart);
		tc.setCellRenderer(new ComboBoxRenderer());

		return cb;
	}

	/**
	 *  set the column col to have a Color combo box editor
	 *
	 *@param  col  The new ColorComboBoxEditor value
	 *@return      Description
	 */
	public RmaJColorComboBox setColorComboBoxEditor(int col)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

		RmaJColorComboBox cb = new RmaJColorComboBox();

		cb.addMouseListener(this);
		cb.getEditor().getEditorComponent().addMouseListener(this);
		//cb.setBackground(getColumnBackground(col));
		RmaCellEditor dcf = new RmaCellEditor(cb);
		dcf.setClickCountToStart(_clickCountToStart);
		tc.setCellRenderer(new RmaColorRenderer());
		//dcf.addCellEditorListener(this);
		tc.setCellEditor(dcf);
		return cb;
	}

	/**
	 *  set the column col to have a combo box editor with the dropdown filled with
	 *  sorted data
	 *
	 *@param  col   The new ComboBoxEditor value
	 *@param  data  The new ComboBoxEditor value
	 *@return       Description
	 */
	public JComboBox setComboBoxEditor(int col, Object[] data)
	{
		return setComboBoxEditor(col, data, true);
	}

	/**
	 *  set the column col to have a combo box editor with the dropdown filled with
	 *  data
	 *
	 *@param  col       The new ComboBoxEditor value
	 *@param  data      The new ComboBoxEditor value
	 *@param  sortData  The new ComboBoxEditor value
	 *@return           Description
	 */
	public JComboBox setComboBoxEditor(int col, Object[] data, boolean sortData)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

///       JComboBox cb = new JComboBox(data);
		RmaListModel model = new RmaListModel(sortData, data);
		RmaJComboBox cb = new RmaJComboBox(model);
		//for modification events
		cb.setModifiable(true);
		cb.setBorder(null);
		cb.addMouseListener(this);
		cb.getEditor().getEditorComponent().addMouseListener(this);
		//cb.setBackground(getColumnBackground(col));
		RmaCellEditor dcf = new RmaCellEditor(cb);
		dcf.setClickCountToStart(1);//_clickCountToStart);
		tc.setCellRenderer(new ComboBoxRenderer());
		//dcf.addCellEditorListener(this);
		tc.setCellEditor(dcf);
		return cb;
	}

	/**
	 *  set the column col to have a combo box editor with the dropdown filled with
	 *  data
	 *
	 *@param  col   The new ComboBoxEditor value
	 *@param  data  The new ComboBoxEditor value
	 *@return       Description
	 */
	public JComboBox setComboBoxEditor(int col, Vector data)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}

		RmaJComboBox cb = new RmaJComboBox(data);
		cb.setBorder(null);  //remove the border, because it makes it hard to read the data in the editable cell.
		//for modification events
		cb.setModifiable(true);

		cb.addMouseListener(this);
		cb.getEditor().getEditorComponent().addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(cb);
		dcf.setClickCountToStart(1);//_clickCountToStart);
		tc.setCellRenderer(new ComboBoxRenderer());
		tc.setCellEditor(dcf);
		return cb;
	}

	/**
	 *  set the column col to have the arg editor.
	 *
	 *@param  col     The new ColumnEditor value
	 *@param  editor  The new ColumnEditor value
	 */
	public void setColumnEditor(int col, RmaCellEditor editor)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;
		if (col >= tcm.getColumnCount() || col < 0)
		{
			return;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return;
		}

		Component c = editor.getComponent();
		c.addMouseListener(this);
		editor.setClickCountToStart(_clickCountToStart);
		tc.setCellEditor(editor);
	}

	/**
	 *  set a cell editor for a column to be a fixed length field if case == 1 then
	 *  the field will be all uppercase case == -1 then the field will be all
	 *  lowercase
	 *
	 *@param  col     The new FixedLengthCellEditor value
	 *@param  length  The new FixedLengthCellEditor value
	 *@param  Case    The new FixedLengthCellEditor value
	 */
	public void setFixedLengthCellEditor(int col, int length, int Case)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col >= tcm.getColumnCount() || col < 0)
		{
			return;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return;
		}
		FixedLengthDocument fld = new FixedLengthDocument(length);
		if (Case == 1)
		{
			fld.setUppercase();
		}
		else if (Case == -1)
		{
			fld.setLowercase();
		}
		RmaJTextField tf = new RmaJTextField(fld, "", length);
		tf.setBorder(_defaultBorder);
		tf.addMouseListener(this);
		RmaCellEditor dcf = new RmaCellEditor(tf);
		dcf.setClickCountToStart(_clickCountToStart);

		//dcf.addCellEditorListener(this);
		tc.setCellEditor(dcf);
	}

	/**
	 *  Sets the UnitsHeaderRenderer attribute of the RmaJTable object
	 */
	public void setUnitsHeaderRenderer()
	{
		_unitsHeadRend = new UnitsHeaderRenderer();

		TableColumnModel tcm = getTableHeader().getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableColumn tc = tcm.getColumn(i);
			tc.setHeaderRenderer(_unitsHeadRend);
		}
	}

	/**
	 *  sets table up to display multi line headers
	 */
	public MleHeadRenderer setMlHeaderRenderer()
	{
		// _mleheadrend = new MleHeadRenderer (_tableModel, getTableHeader());
		_mleheadrend = new MleHeadRenderer();
		// Now set up the renderer to draw all headers.
		TableColumnModel tcm = getTableHeader().getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableColumn tc = tcm.getColumn(i);
			tc.setHeaderRenderer(_mleheadrend);
		}
		return _mleheadrend;
	}

	/**
	 *  sets a TextArea editor for column number col
	 *
	 *@param  col  The new CheckBoxCellEditor value
	 *@return      Description
	 */
	/*
	 *  public JTextArea setTextAreaCellEditor(int col)
	 *  {
	 *  TableColumnModel tcm = getColumnModel();
	 *  TableColumn tc;
	 *  if ( col > tcm.getColumnCount() )
	 *  {
	 *  return null;
	 *  }
	 *  tc = getColumnModel().getColumn(col);
	 *  if ( tc == null )
	 *  return null;
	 *  JTextArea ta = new JTextArea();
	 *  JTextArea ta2 = new JTextArea();
	 *  RMACellEditor dcf = new RMACellEditor(ta);
	 *  dcf.setClickCountToStart(_clickCountToStart);
	 *  tc.setCellEditor(dcf);
	 *  RmaCellRenderer dcr = new RmaCellRenderer(ta2);
	 *  tc.setCellRenderer(dcr);
	 *  /tc.sizeWidthToFit();
	 *  setColumnWidth(col, -1 );
	 *  return ta;
	 *  }
	 */
	public JCheckBox setCheckBoxCellEditor(int col)
	{
		return setCheckBoxCellEditor(col, true);
	}
	/**
	 *  sets a TextArea editor for column number col
	 *
	 *  sets a checkbox editor for column number col
	 *
	 *@param  col  The new CheckBoxCellEditor value
	 *@param  useSelectionForegroun true for the CellRenderer to render the selection background when the cell is selection
	 *@return      Description
	 */
	public JCheckBox setCheckBoxCellEditor(int col, boolean useSelectionBackground)
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;

		if (col > tcm.getColumnCount())
		{
			return null;
		}
		tc = getColumnModel().getColumn(col);
		if (tc == null)
		{
			return null;
		}
///       JCheckBox cb = new JCheckBox();
		RmaJCheckBox cb = new RmaJCheckBox();
		//for notification of being modified
		cb.setHorizontalAlignment(SwingConstants.CENTER);
		cb.setBorder(_defaultBorder);
		//df.addMouseListener(this);
		//OneClickCheckBoxEditor dcf = new OneClickCheckBoxEditor(cb);
		//DefaultCellEditor dcf = new DefaultCellEditor(cb);
		RmaCellEditor dcf = new RmaCellEditor(cb);
		dcf.setClickCountToStart(1/*_oneClickToStart*/);
		//dcf.addCellEditorListener(this);
		tc.setCellEditor(dcf);
		TableCellRenderer dcr = createBooleanRenderer(useSelectionBackground);
		//RmaCellRenderer dcr = new RmaCellRenderer(cb);
		tc.setCellRenderer(dcr);
		//tc.sizeWidthToFit();
		setColumnWidth(col, -1);
		return cb;
	}

	protected TableCellRenderer createBooleanRenderer(boolean useSelectionBackground)
	{
		return  new BooleanRenderer(useSelectionBackground);
	}
	/**
	 *  enables or disables all columns
	 *
	 *@param  enable  The new AllColumnsEnabled value
	 */
	public void setAllColumnsEnabled(boolean enable)
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}
		for (int i = 0; i < getModel().getColumnCount(); i++)
		{
			((RmaTableModelInterface) getModel()).setColEnabled(enable, i);
		}
	}

	/**
	 *  for EditableComponent interface
	 *
	 *@param  b  The new IsEditable value
	 */
	public void setIsEditable(boolean b)
	{
		_isEditable = b;
		setMenusEnabled(b);
		boolean enabled = b;
		if (addRemoveEnabled == false || b == false)
		{
			enabled = false;
		}
		insertMi.setVisible(enabled);
		insertMi.setEnabled(enabled);
		appendMi.setVisible(enabled);
		appendMi.setEnabled(enabled);
		deleteMi.setVisible(enabled);
		deleteMi.setEnabled(enabled);
		setBackground(b ? Color.white : getDisabledBackground(0,0));//UIManager.getColor("TextField.disabledBackground"));
		super.setEnabled(b);
	}

	/**
	 *  Sets the Enabled attribute of the RmaJTable object
	 *
	 *@param  enable  The new Enabled value
	 */
	
	public void setEnabled(boolean enable)
	{
		setEditable(enable);
	}

	/**
	 *  Sets the MenusEnabled attribute of the RmaJTable object
	 *
	 *@param  editable  The new MenusEnabled value
	 */
	protected void setMenusEnabled(boolean editable)
	{
		if ((_defaultPopup) && (_popup != null))
		{
			Component c;

			c = _popup.getComponent(2);
			//c.setEnabled(editable);
			//c.setVisible(editable);
			//cut

			c = _cutMi;
			c.setEnabled(editable);
			c.setVisible(editable);
			//paste
			c = _pasteMi;
			c.setEnabled(editable);
			c.setVisible(editable);
			//clear
			c = _clearMi;
			c.setEnabled(editable);
			c.setVisible(editable);
			//fill
			c = _popup.getComponent(8);
			c.setEnabled(editable);
			c.setVisible(editable);
			// fill
			c = fillMi;
			c.setEnabled(editable);
			c.setVisible(editable);
			if (addRemoveEnabled)
			{
				insertMi.setEnabled(editable);
				insertMi.setVisible(editable);
			}
			// insert row
			if (addRemoveEnabled)
			{
				appendMi.setEnabled(editable);
				appendMi.setVisible(editable);
			}
			// append row
			if (addRemoveEnabled)
			{
				deleteMi.setEnabled(editable);
				deleteMi.setVisible(editable);
			}
			// delete row
		}
	}

	/**
	 *  set whether this table is editable or not
	 *
	 *@param  editable  The new Editable value
	 */
	public void setEditable(boolean editable)
	{
		//if ( !enable ) this.setPopupMenuEnabled(false);
///        if ( !enable )
		setMenusEnabled(editable);

		setAllColumnsEnabled(editable);
		_editable = editable;
		//super.setEnabled(enable); // causes table to not be selectable in 1.3
	}

	/**
	 *  get whether this table is editable or not
	 *
	 *@return    The Editable value
	 */
	public boolean isEditable()
	{
		return _editable;
	}

	/**
	 *  Sets the RowEnabled attribute of the RmaJTable object
	 *
	 *@param  enable  The new RowEnabled value
	 *@param  row     The new RowEnabled value
	 */
	public void setRowEnabled(boolean enable, int row)
	{
		TableModel model = getModel();
		if (!(model instanceof RmaTableModelInterface))
		{
			return;
		}
		((RmaTableModelInterface) model).setRowEnabled(enable, row);
	}

	// clear the editableRows list to give user a fresh start
	/**
	 *  Method Description
	 */
	public void resetRowEnabled()
	{
		TableModel model = getModel();
		if (!(model instanceof RmaTableModelInterface))
		{
			return;
		}
		((RmaTableModelInterface) model).resetRowEnabled();
	}


	/**
	 *  enable or disable column col
	 *
	 *@param  enable  The new ColumnEnabled value
	 *@param  col     The new ColumnEnabled value
	 */
	public void setColumnEnabled(boolean enable, int col)
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}
		((RmaTableModelInterface) getModel()).setColEnabled(enable, col);
	}

	/**
	 *  Sets the CellEnabled attribute of the RmaJTable object
	 *
	 *@param  enable  The new CellEnabled value
	 *@param  row     The new CellEnabled value
	 *@param  col     The new CellEnabled value
	 */
	public void setCellEnabled(boolean enable, int row, int col)
	{
		if (row < 0 || col < 0)
		{
			return;
		}
		TableModel tm = getModel();
		if (tm instanceof RmaTableModel)
		{
			((RmaTableModel) tm).setCellEnabled(enable, row, col);
		}
		else
		{
			System.out.println("setCellEnabled: tableModel not a RmaTableModel it's a" + tm.getClass());
		}
	}

	/**
	 *  given an array of booleans set the columns to be enabled/disabled according
	 *  to the array
	 *
	 *@param  enabled  The new ColumnsEnabled value
	 */
	public void setColumnsEnabled(boolean[] enabled)
	{
		for (int i = 0; i < enabled.length; i++)
		{
			setColumnEnabled(enabled[i], i);
		}
	}

	/**
	 *  set the number of rows in the table model
	 *
	 *@param  rows  The new NumRows value
	 */
	public void setNumRows(int rows)
	{
		int curRowCnt = getRowCount();
		int i;
		if (rows > curRowCnt)
		{
			for (i = curRowCnt; i < rows; i++)
			{
				appendRow();
			}
		}
		else if (rows < curRowCnt)
		{
			for (i = 0; i < curRowCnt - rows; i++)
			{
				removeLastRow();
			}
		}
	}

	/**
	 *  get a vector of the selected cell data
	 *
	 *@return    The SelectedCells value
	 */
	public Vector getSelectedCells()
	{
		Object obj = getSelectedCellData();

		Vector v = new Vector();
		v.addElement(obj);
		return (v);
	}

	/**
	 *  returns the first selected cell
	 *
	 *@return    The FirstSelectedCell value
	 */
	public CellLocation getFirstSelectedCell()
	{
//		int r;
//		int c;
		int[] cols = getSelectedColumns();
		int[] rows = getSelectedRows();

		if (rows.length == 0 || cols.length == 0)
		{
			System.out.println("No Row or column selected");
			return null;
		}

		CellLocation cl = new CellLocation(rows[0], cols[0],
				getValueAt(rows[0], cols[0]));
		return cl;
	}

	/**
	 *  return the last selected cell
	 *
	 *@return    The LastSelectedCell value
	 */
	public CellLocation getLastSelectedCell()
	{
//		int r;
//		int c;
		int[] cols = getSelectedColumns();
		int[] rows = getSelectedRows();

		if (rows.length == 0 || cols.length == 0)
		{
			return null;
		}

		CellLocation cl = new CellLocation(rows[rows.length - 1], cols[cols.length - 1],
				getValueAt(rows[rows.length - 1], cols[cols.length - 1]));
		return cl;
	}

	/**
	 *  return a vector of CellLocations for the selected cells this functionality
	 *  isn't implemented if getColumnSelectionAllowed(true) is set
	 *
	 *@return    The SelectedCellRange value
	 */
	public Vector getSelectedCellRange()
	{
		int r;
		int c;
		Vector v = new Vector();

		Vector dataSets = _tableModel.getDataSets();
		for (int ii=0; ii<dataSets.size(); ii++){
			SingleMonthlyTable dataSet = (SingleMonthlyTable)dataSets.get(ii);
			
			int[] cols = dataSet.getSelectedColumns();
			int[] rows = dataSet.getSelectedRows();

			if (rows.length == 0 && cols.length == 0)
			{
				return v;
			}

			for (r = 0; r < rows.length; r++)
			{
				if (getCellSelectionEnabled())
				{
					for (c = 0; c < cols.length; c++)
					{
						CellLocation cl = new CellLocation(rows[r], cols[c],
							dataSet.getValueAt(rows[r], cols[c]));
						v.addElement(cl);
					}
				}
				else if (getRowSelectionAllowed())
				{
					// selecting rows

					TableColumnModel tcm = dataSet.getColumnModel();
					for (c = 0; c < tcm.getColumnCount(); c++)
					{
						CellLocation cl = new CellLocation(rows[r], c,
							dataSet.getValueAt(rows[r], c));
						v.addElement(cl);
					}
				}
				else if (getColumnSelectionAllowed())
				{
					// todo
				}

			}
		}
		
		return v;
	}

	/**
	 *  returns a vector of vectors which contain CellLocations
	 *
	 *@return    The SelectedCellRangeVector value
	 */
	public Vector getSelectedCellRangeVector()
	{
		int r;
		int c;
		Vector v = new Vector();
		int[] cols = getSelectedColumns();
		int[] rows = getSelectedRows();

		if (rows.length == 0 && cols.length == 0)
		{
			return v;
		}

		Vector row;
		for (r = 0; r < rows.length; r++)
		{
			row = new Vector();
			for (c = 0; c < cols.length; c++)
			{

				CellLocation cl = new CellLocation(rows[r], cols[c],
						getValueAt(rows[r], cols[c]));
				row.addElement(cl);
			}
			v.addElement(row);
		}

		return v;
	}
	/**
	 *  returns a vector of vectors which contain CellLocations
	 * for the selected columns
	 *
	 *@return    The Selected column CellRangeVector value
	 */
	public Vector getColumnCellRangeVector()
	{
		int r;
		int c;
		Vector v = new Vector();
		int[] cols = getSelectedColumns();

		if (cols.length == 0)
		{
			return v;
		}

		Vector row;
		for (r = 0; r < this.getRowCount(); r++)
		{
			row = new Vector();
			for (c = 0; c < cols.length; c++)
			{
				CellLocation cl = new CellLocation(r, cols[c],	getValueAt(r, cols[c]));
				row.addElement(cl);
			}
			v.addElement(row);
		}

		return v;
	}


	/**
	 *  get a cell in the table model. Unlike the getValueAt() in the JTable this
	 *  does not try to map the col to the Column Model.
	 *
	 *@param  row  Description
	 *@param  col  Description
	 *@return      The Cell value
	 */
	public Object getCell(int row, int col)
	{
		return getModel().getValueAt(row, col);
	}

	/**
	 *  set a cell in the table model. Unlike the setValueAt() in the JTable this
	 *  does not try to map the col to the Column Model.
	 *
	 *@param  obj  The new Cell value
	 *@param  row  The new Cell value
	 *@param  col  The new Cell value
	 */
	public void setCell(Object obj, int row, int col)
	{
		getModel().setValueAt(obj, row, col);
	}

	
	public void setModel(TableModel tableModel)
	{
		super.setModel(tableModel);

		if (tableModel instanceof GroupableColumnTableModel)
		{
			GroupableColumnTableModel gtm = (GroupableColumnTableModel) tableModel;
			GroupableTableHeader header = new GroupableTableHeader(this.getColumnModel());
			this.setTableHeader(header);
			int numGroupLevels = gtm.getNumberColumnGroupLevels();

			java.util.List cgLevels = new java.util.ArrayList();
			for (int zz = 0; zz < numGroupLevels; zz++)
			{
				java.util.List level  = new java.util.ArrayList();
				int numColumnGroups = gtm.getNumberColumnGroups(zz);
				TableColumnModel tcm = this.getColumnModel();
				cgLevels.add(level);
				for (int i = 0; i < numColumnGroups; i++)
				{
					int start = gtm.getStartColumnIndex(zz,i);
					int end = gtm.getEndColumnIndex(zz,i);
					final GroupableColumnTableModel gtm2 = gtm;
					final int clevel = zz;
					final int cgroup = i;
					ColumnGroup cg = new ColumnGroup(gtm.getColumnGroupName(zz,i)) {
						
						public Object getHeaderValue() {
							return gtm2.getColumnGroupName(clevel,cgroup);
						}
					};
					if(zz == 0) {
						for (int z = start; z <= end; z++)
						{
							if (z < 0 || z >= tcm.getColumnCount())continue;
							cg.add(tcm.getColumn(z));
						}
					}
					else {
						java.util.List subLevel = (java.util.List)cgLevels.get(zz-1);
						for (int z = start; z <= end; z++)
						{
							if (z < 0 || z >= subLevel.size())continue;
							cg.add(subLevel.get(z));
						}

					}
					level.add(cg);
				}
			}

			for (int i = cgLevels.size() - 1; i >= 0; i--)
			{
				java.util.List level = (java.util.List) cgLevels.get(i);
				for (int z = 0; z < level.size(); z++)
				{
					header.addColumnGroup((ColumnGroup) level.get(z));
				}
			}
		}
	}
	/**
	 *  set the table model data
	 *
	 *@param  data  The new Cells value
	 */
	public void setCells(Object[][] data)
	{
		if (data == null)
		{
			return;
		}
		int row;
//		int col;

		while (data.length >= getRowCount())
		{
			// make table have correct number of rows
			appendRow();
		}

		for (row = 0; row < data.length; row++)
		{
			setRow(row, data[row]);
		}
	}

	/**
	 *  Sets the Cells attribute of the RmaJTable object
	 *
	 *@param  data  The new Cells value
	 */
	public void setCells(Vector data)
	{
		if (data == null)
		{
			return;
		}
		int row;
//		int col;

		while (data.size() >= getRowCount())
		{
			// make table have correct number of rows
			appendRow();
		}

		for (row = 0; row < data.size(); row++)
		{
			setRow(row, (Vector) data.elementAt(row));
		}
	}

	/**
	 *  set the row rowNum in the table model to the object array rowData
	 *
	 *@param  rowNum   The new Row value
	 *@param  rowData  The new Row value
	 *@return          Description
	 */
	public boolean setRow(int rowNum, Object[] rowData)
	{
		Vector rData = new Vector();
		for (int i = 0; i < rowData.length; i++)
		{
			rData.addElement(rowData[i]);
		}
		return (setRow(rowNum, rData));
	}

	/**
	 *  set the row rowNum in the table model to the vector rowData
	 *
	 *@param  rowNum   The new Row value
	 *@param  rowData  The new Row value
	 *@return          Description
	 */
	public boolean setRow(int rowNum, Vector rowData)
	{
		if (rowNum > getRowCount())
		{
			return false;
		}
		if (rowData.size() < getColumnCount())
		{
			rowData.ensureCapacity(getColumnCount());
		}
		for (int i = 0; i < getColumnCount(); i++)
		{
			if (i >= rowData.size())
			{
				// more columns in table that in Vector
				break;
			}
			Object obj = rowData.elementAt(i);
			String data;
			if (obj == null)
			{
				data = "";
			}
			else if (obj instanceof String)
			{
				data = (String) obj;
			}
			else if (obj instanceof CellLocation)
			{
				data = (String) ((CellLocation) obj).getData();
			}
			else if (obj instanceof hec.io.Identifier)
			{
				//setCell(obj, rowNum, i);
				setValueAt(obj, rowNum, i);
				data = null;
			}
			else
			{
				//data = obj.toString();
				//setCell(obj, rowNum, i);
				setValueAt(obj, rowNum, i);
				data = null;
			}
			if (data != null)
			{
				//setCell(data, rowNum, i);
				setValueAt(data, rowNum, i);
			}
		}
		revalidate();
		return true;
	}

	/**
	 *  return the data models data. A vector of vectors
	 *
	 *@return    The Cells value
	 */
	public Vector getCells()
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return null;
		}
		return ((RmaTableModelInterface) getModel()).getDataVector();
	}

	/**
	 *  Method Description
	 */
	public void clearAll()
	{
		clearColors();
		deleteCells();

		for (int i = 0; i < getModel().getColumnCount() + 1; i++)
		{
			try
			{
				setColumnVisible(0, false, 0);
			}
			catch (Exception e)
			{
				break;
			}
		}
		setColumnLabels(new String[0]);

	}

	/**
	 *  clear out the foreground and background colors
	 */
	public void clearColors()
	{
		_rowForeground.clear();
		_rowBackground.clear();
		_cellForeground.clear();
		_cellBackground.clear();
		_columnForeground.clear();
		_columnBackground.clear();
	}

	/**
	 *  clear all the cells, leaving cells visible on screen
	 */
	public void clearCells()
	{
		int r;
		int c;
		String empty = "";
		for (c = 0; c < getColumnCount(); c++)
		{
			for (r = 0; r < getRowCount(); r++)
			{
				setValueAt(empty, r, c);
			}
		}
		revalidate();
		repaint();
	}

	/**
	 *  remove all data from the table model and remove from display
	 */
	public void deleteCells()
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}
		((RmaTableModelInterface) getModel()).clearAll();
	}

	/**
	 *  if editing is occurring, either commit or cancel it
	 *
	 *@param  commit  Description
	 *@return         Description
	 */
	public boolean commitEdit(boolean commit)
	{
		if (!isEditing())
		{
			return true;
		}
		TableCellEditor dce = getCellEditor();
		if (dce == null)
		{
			return true;
		}
		if (commit)
		{
			return (dce.stopCellEditing());
		}
		else
		{
			dce.cancelCellEditing();
			return true;
		}
	}

	/**
	 *@param  event  Description
	 */
	public void textValueChanged(java.awt.event.TextEvent event) { }

	/**
	 *@param  ke  Description
	 */
	public void keyTyped(java.awt.event.KeyEvent ke)
	{
		ke.getSource();
		//String text = tc.getText();
		char chr = ' ';
		//if (text.length() > 0)
		//{
		//    chr = text.charAt(0);
		//}
		kp = chr;
	}

	/**
	 *@param  ke  Description
	 */
	public void keyPressed(java.awt.event.KeyEvent ke)
	{
//		int dum = 0;

		int keyCode = ke.getKeyCode();
//System.out.println ( "Got key " + ke.getKeyChar() );
		/*
		 *  if (keyCode == KeyEvent.VK_RIGHT) //right arrow
		 *  {
		 *  traverseCellRight();
		 *  ke.consume();
		 *  return;
		 *  }
		 *  else if (keyCode == KeyEvent.VK_DOWN) //Down Arro
		 *  {
		 *  traverseCellDown();
		 *  ke.consume();
		 *  return;
		 *  }
		 *  else if (keyCode == KeyEvent.VK_LEFT)
		 *  {
		 *  traverseCellLeft();
		 *  ke.consume();
		 *  return;
		 *  }
		 *  else if (keyCode == KeyEvent.VK_UP)
		 *  {
		 *  traverseCellUp();
		 *  ke.consume();
		 *  return;
		 *  }
		 *  else if (keyCode == KeyEvent.VK_TAB) //tab key
		 *  {
		 *  /    requestFocus();
		 *  traverseCellRight();
		 *  ke.consume();
		 *  return;
		 *  }
		 *  else if (keyCode == KeyEvent.VK_ENTER)
		 *  {
		 *  traverseCellDown();
		 *  ke.consume();
		 *  return;
		 *  }
		 *  /only want to intercept control-c events etc for the following key strokes
		 *  else
		 */
		if (!ke.isControlDown())
		{
			return;
		}

		switch (keyCode)
		{
			case KeyEvent.VK_X:
				cut();
				break;
			case KeyEvent.VK_C:
				copy(true);
				ke.consume();
				break;
			case KeyEvent.VK_V:
				paste();
				ke.consume();
				break;
			case KeyEvent.VK_A:
				traverseCellRight();
				break;
			case KeyEvent.VK_Z:
				undo();
				break;
		}
		//ke.consume();

	}

	/**
	 *@param  ke  Description
	 */
	public void keyReleased(java.awt.event.KeyEvent ke)
	{
		//char keychr = ke.getKeyChar();
		//char chr = curr_char;
		//char new_char = curr_char;

		// repaint modified if necessary
		if (RmaJTextField.isCursorKey(ke.getKeyCode()))
		{
			return;
		}

		if ( !isEditing() )
		{
			return;
		}
		if (!_modified)
		{
			setModified(true);
		}
		_modified = true;

	}

	/**
	 *  process mouse clicks for the popup menu
	 *
	 *@param  event  Description
	 */
	public void mouseClicked(java.awt.event.MouseEvent event)
	{
		if (_popup == null)
		{
			return;
		}
		if (!event.isMetaDown() || !_popupEnabled)
		{
			return;
		}
		boolean visible = getModel() instanceof RmaTableModelInterface;
		insertMi.setVisible(visible);
		appendMi.setVisible(visible);
		deleteMi.setVisible(visible);

		if (!SwingUtilities.isRightMouseButton(event))
		{
			if (_popup.isVisible())
			{
				_popup.setVisible(false);
			}
			return;
		}
		_popupPoint = event.getPoint();
		int x = event.getX();
		int y = event.getY();
//		Vector cellRange = new Vector();
		//getSelectedCells();
		Object obj = getSelectedCellData();
		Component comp = getEditorComponent();

		if (comp != null && event.getSource() == this)
		{
			comp = null;
		}
//		boolean enabled;
		if (obj == null)
		{
			//nothing selected

//			enabled = false;
		}
		else
		{
//			enabled = true;
		}
		int column = columnAtPoint(event.getPoint());
		int row = rowAtPoint(event.getPoint());
		if ( column == 0 && (_rowHeaderEnabled || _autoRowHeaders))
		{
			return;
		}
		if (_defaultPopup)
		{
			if ( _firstFixedRow && row == 0 )
			{
				insertMi.setEnabled(false);
			}
			else if ( addRemoveEnabled && _editable && isEnabled() )
			{
				insertMi.setEnabled(true);
			}
			/*
			 *  if ( isEditable() )
			 *  {
			 *  _popup.getComponent(5).setEnabled(true);//delete
			 *  _popup.getComponent(2).setEnabled(true);//cut
			 *  }
			 *  _popup.getComponent(3).setEnabled(true);//copy
			 */
		}

		Dimension dim = _popup.getPreferredSize();
		java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
		Dimension scrnsiz = toolkit.getScreenSize();
		//Dimension tablensiz = get;

		Point pt0 = getLocationOnScreen();

		int windowsToolBarHeight = 50;
		int offset = scrnsiz.height - (pt0.y + y + dim.height + windowsToolBarHeight);
		if (offset < 0)
		{
			y += offset;
		}

		//  Determine if the cell contains a number or is a blank
		boolean isaNumber = isCellaNumber(row, column, obj);

		if (_editable) {
		   if (this.isCellEditable(row, column)) {
			  this.setMenusEnabled(true);
			  if (isaNumber) {
				 fillMi.setEnabled(getSelectedRowCount() > 0 );
			   }
			   else
			   {
				 //fillMi.setEnabled(false);
			   }
			}
			else {
				 this.setMenusEnabled(false);
			}
		}
		if ( shouldShowPopup(_popupPoint))
		{
			if (comp != null && isEditing())
			{
				_popup.show(comp, x, y);
			}
			else
			{
				_popup.show(this, x, y);
			}
		}

	}
	/**
	 * @param row
	 * @param column
	 * @param obj
	 * @return
	 */
	private boolean isCellaNumber(int row, int column, Object obj)
	{
		if (obj == null )
		{
			obj = getValueAt(row, column);
		}
		boolean isaNumber = java.lang.Number.class.isAssignableFrom(getColumnClass(column));
		if (!isaNumber && (obj != null) && (obj instanceof String))
		{
			String s = obj.toString().trim();
			if (s.length() == 0)
			{
			   return true;
			}
			else
			{
				Object ob=null;
				try
				{
					ob = new DecimalFormat().parse(s);
					if (ob != null)
					{
						return  true;
					}
				}
				catch (Exception p) {}
			}
			return false;
		}
		return isaNumber;
	}

	protected boolean shouldShowPopup(Point popupPoint)
	{
		return true;
	}

	/**
	 *  does nothing
	 *
	 *@param  event  Description
	 */
	public void mousePressed(java.awt.event.MouseEvent event) { }

	/**
	 *  does nothing
	 *
	 *@param  event  Description
	 */
	public void mouseReleased(java.awt.event.MouseEvent event) { }

	/**
	 *  does nothing
	 *
	 *@param  event  Description
	 */
	public void mouseEntered(java.awt.event.MouseEvent event) { }

	/**
	 *  does nothing
	 *
	 *@param  event  Description
	 */
	public void mouseExited(java.awt.event.MouseEvent event) { }

	/**
	 *  does nothing
	 *
	 *@param  event  Description
	 *@param  x      Description
	 *@param  y      Description
	 *@return        Description
	 */
	
	public boolean mouseDown(Event event, int x, int y)
	{

		return true;
	}

	/**
	 *  Validate text entry
	 *
	 *@param  text  Description
	 *@return       Description
	 */
	/**
	 *  Validate text entry
	 *
	 *@param  text  Description
	 *@return       Description
	 */
	boolean checkEditDouble(String text)
	{
		if (text.length() == 1 && (text.equals(".") || text.equals("-") || text.equals("+")))
		{
			return true;
		}
		if (text.length() == 2 && (text.equals("-.") || text.equals("+.")))
		{
			return true;
		}

		try
		{
			new Double(text);
		}
		catch (NumberFormatException efmt)
		{
			return false;
		}
		return true;
	}

	/**
	 *@param  text  Description
	 *@return       Description
	 */
	boolean checkEditFloat(String text)
	{
		if (text.length() == 1 && (text.equals(".") || text.equals("-") || text.equals("+")))
		{
			return true;
		}
		if (text.length() == 2 && (text.equals("-.") || text.equals("+.")))
		{
			return true;
		}

		try
		{
			new Float(text);
		}
		catch (NumberFormatException efmt)
		{
			return false;
		}
		return true;
	}

	/**
	 *@param  text  Description
	 *@return       Description
	 */
	boolean checkEditInteger(String text)
	{
		try
		{
			new Integer(text);
		}
		catch (NumberFormatException efmt)
		{
			return false;
		}
		return true;
	}

	/**
	 *  first off that the table has changed from a menu command
	 *
	 *@param  changeType  Description
	 *@param  startRow    Description
	 *@param  startCol    Description
	 *@param  endRow      Description
	 *@param  endCol      Description
	 */
	protected void fireTableChangeEvent(int changeType, int startRow, int startCol,
			int endRow, int endCol)
	{
		TableUpdateEvent tue = new TableUpdateEvent(this, startRow, startCol,
				endRow, endCol, changeType);
		for (int i = 0; i < _tableChangeListeners.size(); i++)
		{
			((TableChangeListener) _tableChangeListeners.elementAt(i)).tableDataChanged(tue);
		}
		if ( changeType != TableUpdateEvent.COPY )
		{
			//Set the parent window modified
			setModified(true);
		}

	}


	/**
	 *  Gets called when the form decides to clear itself
	 */
	public void clearPerformed()
	{
		commitEdit(true);
		clearCells();

	}

	// popup commands

	/**
	 *  select all the cells
	 */
	
	public void selectAll()
	{
		Vector dataSets = _tableModel.getDataSets();
		for (int i=0; i<dataSets.size(); i++){
			SingleMonthlyTable dataSet = (SingleMonthlyTable)dataSets.get(i);
    		dataSet.selectAll();
		}
	}

	/**
	 *  does nothing
	 */
	public void undo() { }

	/**
	 *  cut the data from the screen. Uses a mix of copy and clear
	 */
	public void cut()
	{
		commitEdit(true);
		//tableCells = getCells();
		CellLocation ce = getFirstSelectedCell();
		CellLocation ce2 = getLastSelectedCell();
		copy(false);
		clear(false);
		if (ce == null || ce2 == null)
		{
			return;
		}
		fireTableChangeEvent(TableUpdateEvent.CLEAR, ce.getRow(), ce.getCol(),
				ce2.getRow(), ce2.getCol());

	}

	/**
	 *  fill a column with an integer array
	 *
	 *@param  values  Description
	 *@param  col     Description
	 */
	public void fillColumn(int[] values, int col)
	{
		if (col >= getModel().getColumnCount())
		{
			return;
		}
		int rowCnt = getModel().getRowCount();
		int end = (rowCnt > values.length ? values.length : rowCnt);
		for (int i = 0; i < end; i++)
		{
			if (i >= rowCnt)
			{
				return;
			}
			setValueAt(new Integer(values[i]), i, col);
		}
	}

	/**
	 *  fill a the column num col with the object array values
	 *
	 *@param  values  Description
	 *@param  col     Description
	 */
	public void fillColumn(Object[] values, int col)
	{
		if (col >= getModel().getColumnCount())
		{
			return;
		}
		int rowCnt = getModel().getRowCount();
		int end = (rowCnt > values.length ? values.length : rowCnt);
		for (int i = 0; i < end; i++)
		{
			if (i >= rowCnt)
			{
				return;
			}
			setValueAt(values[i], i, col);
		}
	}

	/**
	 *  fill a column for rows number of cells with value
	 *
	 *@param  value     Description
	 *@param  startRow  Description
	 *@param  col       Description
	 *@param  rows      Description
	 */
	private void fillColumn(Object value, int startRow, int col, int rows)
	{
		if (col >= getModel().getColumnCount())
		{
			return;
		}
		for (int i = startRow; i < rows + startRow; i++)
		{
			if (i >= getModel().getRowCount())
			{
				return;
			}
			if (!isCellEditable(i, col))
			{
				continue;
			}
			if (_pasteBackground != null)
			{
				setCellBackground(i, col, _pasteBackground);
			}
			if (_pasteForeground != null)
			{
				setCellForeground(i, col, _pasteForeground);
			}

			if (value instanceof ParamDouble)
			{
				ParamDouble pd = (ParamDouble) value;
				try
				{
					ParamDouble pdnew = (ParamDouble) pd.clone();
					setValueAt(pdnew, i, col);
				}
				catch (Exception e)
				{
					setValueAt(pd.toString(), i, col);
				}
			}
			else
			{
				setValueAt(value, i, col);
			}
		}
	}

	/**
	 *  Method Description
	 */
	public void fill()
	{
		cellEditor = (RmaCellEditor)getCellEditor();
		if (cellEditor != null)
		{
		   cellEditor.stopCellEditing();
		}
		CellLocation firstCell = getFirstSelectedCell();
		if ( firstCell == null )
		{
			return;
		}
		Object obj = getValueAt(firstCell.getRow(), firstCell.getCol());
		if ( isCellaNumber(firstCell.getRow(), firstCell.getCol(), obj))
		{

			if (_fillDialog == null)
			{
				_fillDialog = RmaModJTableFillDialog.getFillDialog(this);
			}
			_fillDialog.setVisible(true);
			if	 (_fillDialog.isCanceled())
			{
				return;
			}
		}
		else
		{
			linearFill();
		}
	}

	/**
	 *  take the first cell selected and the last cell selected and fill the cells
	 *  inbetween with linear values
	 */
	public void linearFill()
	{
		CellLocation lastCell = getLastSelectedCell();
		CellLocation firstCell = getFirstSelectedCell();
		Vector cellTable = this.getSelectedCellRangeVector();
		linearFill(firstCell, lastCell, cellTable);
	}

	/**
	 *  Method Description
	 *
	 *@param  firstCell  Description
	 *@param  lastCell   Description
	 */
	public void linearFill(CellLocation firstCell, CellLocation lastCell)
	{
		// some checking first
		if (firstCell == null || lastCell == null)
		{
			return;
		}
		if (firstCell.getCol() != lastCell.getCol())
		{
			System.out.println("linearFill(): startCell col " + firstCell.getCol() + " != stopCell col " +
					lastCell.getCol());
			return;
		}
// was: if ( firstCell.getRow() > lastCell.getCol() )
		if (firstCell.getRow() > lastCell.getRow())
		{
			System.out.println("linearFill(): startCell row " + firstCell.getRow() + " > stopCell row " +
					lastCell.getRow());
			return;
		}

		Vector cellTable = new Vector(lastCell.getRow() - firstCell.getRow());
		CellLocation cl;
		Vector row;
		int col = firstCell.getCol();
		for (int r = firstCell.getRow(); r <= lastCell.getRow(); r++)
		{
			row = new Vector();
			for (int c = 0; c < 1; c++)
			{

				cl = new CellLocation(r, col,
						getValueAt(r, col));
				row.addElement(cl);
			}
			cellTable.addElement(row);
		}
		linearFill(firstCell, lastCell, cellTable);
	}

	/**
	 *  Method Description
	 *
	 *@param  firstCell  Description
	 *@param  lastCell   Description
	 *@param  cellTable  Description
	 */
	private void linearFill(CellLocation firstCell, CellLocation lastCell, Vector cellTable)
	{

//		CellLocation curSelCell;
		Vector row;
		Object startValue;
		Object endValue;

		int startRow;

		int stopRow;
		int c;
//		int col;
		int numSteps;
		double startVal;
		double endVal;
		double stepValue;
//		double curVal;
		double value;
System.out.println("linearFill: start="+firstCell+" end="+lastCell+" cells="+cellTable.size());
		if (lastCell != null && !lastCell.equals(firstCell))
		{
			row = (Vector) cellTable.elementAt(0);
			for (int i = 0; i < row.size(); i++)
			{
				// loop through rows
				firstCell = (CellLocation) row.elementAt(i);
				startValue = firstCell.getData();
				lastCell = ((CellLocation) ((Vector) cellTable.elementAt(cellTable.size() - 1)).elementAt(i));
				endValue = lastCell.getData();
				startRow = firstCell.getRow();
				c = firstCell.getCol();
				stopRow = lastCell.getRow();
				numSteps = stopRow - startRow;
				if (numSteps == 0)
				{
					numSteps = 1;
				}
				if (endValue.toString().equals(""))
				{
					fillColumn(startValue, startRow, c, cellTable.size());
					continue;
				}
				try
				{
					/*
					 *  endVal = Double.valueOf(endValue.toString()).doubleValue();
					 *  startVal = Double.valueOf(startValue.toString()).doubleValue();
					 *  startVal = Double.valueOf(RMAIO.setPrecision2(startVal, _precision)).doubleValue();
					 */
					endVal = RMAIO.parseDouble(endValue);
					startVal = RMAIO.parseDouble(startValue);
				}
				catch (NumberFormatException nfe)
				{
System.out.println("linearFill: "+nfe);
					fillColumn(startValue, startRow, c, cellTable.size());
					continue;
				}
				//if ( endVal > startVal )
				//{
				//curVal = endVal;
				//}
				//else
				//{
//				curVal = startVal;
				//}
				stepValue = (endVal - startVal) / numSteps;
				for (int j = 1; j < cellTable.size() - 1; j++)
				{
					// loop through columns
					if (isCellEditable(startRow + j, c))
					{
						//if ( endVal < startVal )
						value = startVal + (stepValue * j);
						//else
						//    value = new Double(stepValue*j);
						TableColumn tc = getColumnModel().getColumn(c);
						TableCellEditor tce = tc.getCellEditor();
						if (tce instanceof RmaCellEditor)
						{
							Component cmp = ((RmaCellEditor) tce).getComponent();

							if (cmp instanceof RmaJDecimalField)
							{
								if (getValueAt(startRow + j, c) instanceof hec.data.ParamDouble)
								{
									// cell object is a param double, just modify its value
									ParamDouble pdv = (hec.data.ParamDouble) getValueAt(startRow + j, c);
									int iprecis = pdv.getPrecision();
									// small iprecis may lead to undesired rounding
									if (_precision > iprecis)
									{
										iprecis = _precision;
									}
									pdv.setValue(value);
									pdv.setPrecision(iprecis);
									setValueAt(pdv, startRow + j, c);
								}
								else
								{
									setValueAt(new Double(RMAIO.setPrecision2(value, _precision)), startRow + j, c);
								}
							}
							else if (cmp instanceof RmaJIntegerField)
							{
								setValueAt(new Integer(RMAIO.setPrecision2(value, 0)), startRow + j, c);
							}
							else
							{
								setValueAt(new Double(RMAIO.setPrecision2(value, _precision)), startRow + j, c);
							}
						}
						else
						{
							setValueAt(RMAIO.setPrecision2(value, _precision), startRow + j, c);
						}


						if (_pasteBackground != null)
						{
							setCellBackground(startRow + j, c, _pasteBackground);
						}
						if (_pasteForeground != null)
						{
							setCellForeground(startRow + j, c, _pasteForeground);
						}
					}
				}

			}
		}
		repaint();
		//lastCell = getLastSelectedCell();
		//firstCell = getFirstSelectedCell();
		if (firstCell != null && lastCell != null)
		{
			fireTableChangeEvent(TableUpdateEvent.LINEAR_FILL, firstCell.getRow(), firstCell.getCol(),
					lastCell.getRow(), lastCell.getCol());
		}
	}

	/**
	 *  take the selected cells and add the constantValue to them
	 *
	 *@param  constantValue  Description
	 */
	public void constantFill(double constantValue)
	{
		valueFill(constantValue, true);
	}

	/**
	 *  Method Description
	 *
	 *@param  factorValue  Description
	 */
	public void factorFill(double factorValue)
	{
		valueFill(factorValue, false);
	}

	/**
	 *  Method Description
	 *
	 *@param  fillValue  Description
	 *@param  addIt      Description
	 */
	protected void valueFill(double fillValue, boolean addIt)
	{
		if (rma.util.RMAConst.isUndefinedValue(fillValue))
		{
			return;
		}
		Vector cellTable = getSelectedCellRangeVector();
		CellLocation lastCell = getLastSelectedCell();
		CellLocation firstCell = getFirstSelectedCell();
//		CellLocation curSelCell;
		Vector row;
		Object value;

		int r;
		int c;
		CellLocation cl;
		if (lastCell != null && !lastCell.equals(firstCell))
		{
			for (int i = 0; i < cellTable.size(); i++)
			{
				// loop through rows
				row = (Vector) cellTable.elementAt(i);

				for (int j = 0; j < row.size(); j++)
				{
					// loop through columns
					cl = (CellLocation) row.get(j);
					r = cl.getRow();
					c = cl.getCol();
					value = cl.getData();
					if (isCellEditable(r, c))
					{
						// need to ensure we are putting a completely new object in each cell

						if (value instanceof ParamDouble)
						{
							ParamDouble pd = (ParamDouble) value;
							if (addIt)
							{
								pd.setValue(pd.getValue() + fillValue);
							}
							else
							{
								pd.setValue(pd.getValue() * fillValue);
							}
							/*
							 *  try
							 *  {
							 *  setValueAt(pd, r, c );
							 *  }
							 *  catch (Exception e)
							 *  {
							 *  continue;
							 *  }
							 */
						}
						else if (value instanceof Double)
						{
							Double nd;
							if (addIt)
							{
								nd = new Double(((Double) value).doubleValue() + fillValue);
							}
							else
							{
								nd = new Double(((Double) value).doubleValue() * fillValue);
							}
							setValueAt(nd, r, c);
						}
						else if (value instanceof HecDouble) {
							HecDouble newValue = new HecDouble((HecDouble)value);
							if (addIt) {
								newValue.add(fillValue);
							}
							else {
								newValue.multiply(fillValue);
							}
							setValueAt(newValue, r, c);
						}
						else
						{
							// treat as a string

							double d = RMAIO.parseDouble(value.toString());
							if (rma.util.RMAConst.isValidValue(d))
							{
								if (addIt)
								{
									d += fillValue;
								}
								else
								{
									d *= fillValue;
								}
								setValueAt(RMAIO.toTable(d), r, c);
							}
							else
							{
								continue;
							}
						}
						if (_pasteBackground != null)
						{
							setCellBackground(r, c, _pasteBackground);
						}
						if (_pasteForeground != null)
						{
							setCellForeground(r, c, _pasteForeground);
						}
					}

				}
			}
		}
		repaint();
		lastCell = getLastSelectedCell();
		firstCell = getFirstSelectedCell();
		if (firstCell != null && lastCell != null)
		{
			fireTableChangeEvent(TableUpdateEvent.REPEAT_FILL, firstCell.getRow(), firstCell.getCol(),
					lastCell.getRow(), lastCell.getCol());
		}
	}

	/**
	 *  take the first cell selected and fill all cells in that column with that
	 *  value
	 */
	public void repeatFill()
	{
		Vector cellTable = this.getSelectedCellRangeVector();
		CellLocation lastCell = getLastSelectedCell();
		CellLocation firstCell = getFirstSelectedCell();
//		CellLocation curSelCell;
		Vector row;
		Object value;

		int r;
		int c;

		if (lastCell != null && !lastCell.equals(firstCell))
		{
			row = (Vector) cellTable.elementAt(0);
			for (int i = 0; i < row.size(); i++)
			{
				// loop through rows
				value = ((CellLocation) row.elementAt(i)).getData();
				r = ((CellLocation) row.elementAt(i)).getRow();
				c = ((CellLocation) row.elementAt(i)).getCol();

				for (int j = 1; j < cellTable.size(); j++)
				{
					// loop through columns
					if (isCellEditable(r + j, c))
					{
						// need to ensure we are putting a completely new object in each cell

						if (value instanceof ParamDouble)
						{
							ParamDouble pd = (ParamDouble) value;
							try
							{
								ParamDouble pdnew = (ParamDouble) pd.clone();
								setValueAt(pdnew, r + j, c);
							}
							catch (Exception e)
							{
								setValueAt(pd.toString(), r + j, c);
							}
						}
						else
						{
							setValueAt(value, r + j, c);
						}

						// bad!!!
						// setValueAt(value, r+j, c );

						if (_pasteBackground != null)
						{
							setCellBackground(r + j, c, _pasteBackground);
						}
						if (_pasteForeground != null)
						{
							setCellForeground(r + j, c, _pasteForeground);
						}
					}

				}
			}
		}
		repaint();
		lastCell = getLastSelectedCell();
		firstCell = getFirstSelectedCell();
		if (firstCell != null && lastCell != null)
		{
			fireTableChangeEvent(TableUpdateEvent.REPEAT_FILL, firstCell.getRow(), firstCell.getCol(),
					lastCell.getRow(), lastCell.getCol());
		}

	}

	public void sumColumn(boolean entireColumn)
	{
		Vector cellTable;
		if(entireColumn) {
			cellTable = getColumnCellRangeVector();
		}
		else {
			cellTable = getSelectedCellRangeVector();
		}
		Vector row;
		Object value;

		int r;
		int c;
		CellLocation cl;
		double sum = 0.0;
		boolean found = false;
		for(int i = 0; i < cellTable.size(); i++) {
			// loop through rows
			row = (Vector) cellTable.elementAt(i);
			for(int j = 0; j < row.size(); j++) {
				// loop through columns
				cl = (CellLocation) row.get(j);
				r = cl.getRow();
				c = cl.getCol();
				value = cl.getData();

				if(value instanceof ParamDouble) {
					ParamDouble pd = (ParamDouble) value;
					if (pd.doubleValue() != rma.util.RMAConst.UNDEF_DOUBLE) {
						sum += pd.doubleValue();
						found = true;
					}
				}
				else if(value instanceof Double) {
					if (((Double) value).doubleValue() != rma.util.RMAConst.UNDEF_DOUBLE) {
						sum += ( (Double) value).doubleValue();
						found = true;
					}
				}
				else if(value instanceof HecDouble) {
					if(((HecDouble) value).isDefined())
						sum += ((HecDouble) value).value();
					found = true;
				}
				else {
					// treat as a string
					if(value.toString().trim().length() > 0) {
						double d = RMAIO.parseDouble(value.toString());
						if(rma.util.RMAConst.isValidValue(d)) {
							sum += d;
							found = true;
						}
					}
				}
			}

		}
		if (!found) {
			JOptionPane.showMessageDialog(_parent, "No valid values found for selected column.", "Sum", JOptionPane.WARNING_MESSAGE);
		}
		else {
			HecDouble hecDouble = new HecDouble(sum);
			if (entireColumn) {
				JOptionPane.showMessageDialog(_parent, "       Column sum = " + hecDouble, "Sum", JOptionPane.PLAIN_MESSAGE);
			}
	else {
				JOptionPane.showMessageDialog(_parent, "      Selected cells sum = " + hecDouble, "Sum", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
		/**
		 *  paste implementation
	 */
	public void paste()
	{
		int i = 0;
		Clipboard cb = this.getToolkit().getSystemClipboard();
		// Get its contents
		Transferable t = cb.getContents(this);
		if (t == null)
		{
			this.getToolkit().beep();
			System.out.println("paste:no data in clipboard");
			return;
		}
		commitEdit(true);
		try
		{
			// Ask for clipboard contents to be converted to our data flavor
			// This will throw an exception if our flavor is not supported.
			String cellValues = (String) t.getTransferData(DataFlavor.stringFlavor);
			if (_pasteDebug)
			{
				System.out.println("paste: pasting [" + cellValues + "]");
			}
			// Add all those pasted cells to the table
			//table.setSize(getNumRows()); // make sure there is enough space for all of the rows
			StringTokenizer rows = new StringTokenizer(cellValues, "\r\n", true);
			// vector of vectors
			Vector cellTable = getSelectedCellRangeVector();
			CellLocation lastCell = getLastSelectedCell();
			CellLocation firstCell = getFirstSelectedCell();
			CellLocation curSelCell;

			int r;
			int c;
			int rowCnt = 1;
			getColumnModel();
//			TableColumn tc;
//			TableCellEditor editor;
//			Component comp;
			int invalidCnt = 0;
//			int modelCol;
//			boolean invalidData = false;
			boolean lastRowWasSeparator = false;
			boolean lastWasToken = false;
			boolean firstCellInRow = false;

			getModel();
//			Object objValue;
			String str, rowToken;
			StringTokenizer cells;
			int colCnt = getColumnCount();
			rowCnt = getRowCount();

			if (lastCell != null && lastCell.equals(firstCell))
			{
				//  they didn't select a range so just paste away until end of table
				r = firstCell.getRow();
				c = firstCell.getCol();
				if (_pasteDebug)
				{
					System.out.println("paste:no selection so starting at row="
							+ r + " col=" + c + " rowCnt=" + rows.countTokens());
				}
				int numberTokens = rows.countTokens();
				int count = 0;
				if ( _pasteAddsRows )
				{
					// add additional rows if needed and we're allowed to do so.
					int numOfPasteRows = numberTokens/2+1;
					if ( r+numOfPasteRows > rowCnt )
					{
						int numAddRows = r+numOfPasteRows-rowCnt;
						if (_pasteDebug)
						{
							System.out.println("paste:have " + numOfPasteRows
									+ " rows to add. starting at row " + r
									+ " adding " + numAddRows + " rows.");
						}
						appendRows(numAddRows);
						rowCnt = getRowCount();
					}
				}

				while (rows.hasMoreTokens())
				{

//					if (r > rowCnt) {
//						//  Check to see if the table has been expanded
//						rowCnt = getRowCount();
				//					}
					if (r >= rowCnt){
						if (_pasteDebug){
							System.out.println("paste: at end of rows");
						}
						if (_pasteAddsRows){
							// add additional rows if needed and we're allowed to do so.
							int numOfPasteRows = (numberTokens - count) / 2 + 1;
							if (r + numOfPasteRows > rowCnt){
								int numAddRows = r + numOfPasteRows - rowCnt;
								if (_pasteDebug){
									System.out.println("paste:have " + numOfPasteRows
										+ " rows to add. starting at row " + r
										+ " adding " + numAddRows + " rows.");
								}
								appendRows(numAddRows);
								rowCnt = getRowCount();
							}
						}
						else{

							break;
						}
					}
					rowToken = rows.nextToken();
					count++;

					c = firstCell.getCol();

					if ( rowToken.equals("\r\n") || rowToken.equals("\r")
						|| rowToken.equals("\n"))
					{
						if ( lastRowWasSeparator )
						{
							if ( _pasteDebug )
							{
								System.out.println("paste:missing data row at " + r+
									" using empty string");
							}
							pasteCell("", r, c);
							r++;
						}
						lastRowWasSeparator = true;
						continue;  // token sep char
					}
					else
					{
						lastRowWasSeparator = false;
					}
					if ( _pasteDebug )
					{
						System.out.println("row("+r+")=["+rowToken+"]");
					}
					cells = new StringTokenizer(rowToken, "\t", true);

					if (_pasteDebug)
					{
						System.out.println("paste: have " + cells.countTokens() + " cols for row " + r);
					}
					firstCellInRow = true;
					while (cells.hasMoreTokens())
					{
						str = cells.nextToken();
						if ( str.equals("\t"))
						{
							if ( lastWasToken || firstCellInRow )
							{
								if ( _pasteDebug )
								{
									System.out.println("paste:missing data point at "
										+r+","+c+" using empty string");
								}
								pasteCell("", r, c);
								if ((c+1) < getColumnCount()) {
									c++;
								}
								//  No more columns.  Treat tab like cr
								else {
									r++;
									for (int k=0; k<getColumnCount(); k++) {
										if (isCellEditable(r, k)) {
											c = k;
											continue;
										}
									}
								}
							}
							lastWasToken = true;
							continue;  // token sep char
						}
						else
						{
							lastWasToken = false;
						}
						firstCellInRow = false;
						// paste only into editable cells
						//scale it if we have to.
						if (c >= colCnt)
						{
							if (_pasteDebug)
							{
								System.out.println("paste: at end of cols");
							}
							continue;
						}
						invalidCnt += pasteCell(str, r, c);

						if ( (c + 1) < getColumnCount()) {
							c++;
						}
					}
					r++;
				}
				fireTableChangeEvent(TableUpdateEvent.PASTE, firstCell.getRow(),
						firstCell.getCol(), r, c);
				if (invalidCnt > 0)
				{
					System.out.println("Found " + invalidCnt + " cells with invalid data");
				}
				revalidate();
				repaint();
				return;
			}

			if (cellTable.size() < 1)
			{
				if (_pasteDebug)
				{
					System.out.println("paste: no cellTables selected");
				}
				return;
				// nothing selected!
			}
			Vector rowVec = (Vector) cellTable.elementAt(i);
			//Vector newRow = new Vector();
			//want to fill from left to right top to bottom.
			//int r = (range.start_row < range.end_row) ? range.start_row : range.end_row;

			// try and fill all cells that have been selected.
//			hec.data.ParamDouble pd = new hec.data.ParamDouble();
			rowCnt = getRowCount();
			if ( _pasteDebug )
			{
				System.out.println("paste: filling selected range");
			}
			for (int k = i; k < cellTable.size(); k += rowCnt)
			{
				rowVec = (Vector) cellTable.elementAt(k);
				if (rowVec == null || rowVec.size() == 0)
				{
					continue;
				}

				rows = new StringTokenizer(cellValues, "\r\n", true);

				while (rows.hasMoreTokens())
				{
					// while we have clipboard data

					curSelCell = (CellLocation) rowVec.elementAt(0);
					r = curSelCell.getRow();
					c = curSelCell.getCol();

					if (r >= rowCnt)
					{
						break;
					}
					rowToken = rows.nextToken();

					if ( rowToken.equals("\r\n") || rowToken.equals("\r")
						|| rowToken.equals("\n"))
					{
						if ( lastRowWasSeparator )
						{
							if ( _pasteDebug )
							{
								System.out.println("paste:missing data at row " + r+
									" using empty string");
							}
							pasteCell("", r, c);
							r++;
							i++;
						}
						lastRowWasSeparator = true;
						if (i < cellTable.size())
						{
							rowVec = (Vector) cellTable.elementAt(i);
						}
						else
						{
							break;
						}
						continue;  // token sep char
					}
					else
					{
						lastRowWasSeparator = false;
					}
					if ( _pasteDebug )
					{
						System.out.println("row("+r+")=["+rowToken+"]");
					}
					cells = new StringTokenizer(rowToken,"\t", true);
					c = curSelCell.getCol();
					//int c = (range.start_column < range.end_column) ? range.start_column : range.end_column;
					//newRow.removeAllElements();
					firstCellInRow = true;
					while (cells.hasMoreTokens())
					{
						str = cells.nextToken();
						if ( _pasteDebug )
						{
							System.out.println("paste: next token=["
								+(str.equals("\t")?"{TAB}":str)+"]");
						}
						if ( str.equals("\t") )
						{
							if ( lastWasToken || firstCellInRow)
							{
								if ( _pasteDebug )
								{
									System.out.println("paste:missing data point at "
										+r+","+c+" using empty string");
								}
								pasteCell("", r, c);
								c++;
							}
							lastWasToken = true;
							continue;  // token sep char
						}
						else
						{
							lastWasToken = false;
						}
						firstCellInRow = false;
						if (c >= colCnt)
						{
							continue;
						}
						if (c > lastCell.getCol())
						{
							// greater than the selected region
							continue;
						}
						// paste only into editable cells
						invalidCnt += pasteCell(str, r, c);

						c++;
					}

					r++;
					i++;
					if (i < cellTable.size())
					{
						rowVec = (Vector) cellTable.elementAt(i);
					}
					else
					{
						break;
					}
				}
			}
			fireTableChangeEvent(TableUpdateEvent.REPEAT_FILL, firstCell.getRow(), firstCell.getCol(),
					lastCell.getRow(), lastCell.getCol());
			if (invalidCnt > 0)
			{
				System.out.println("Found " + invalidCnt + " cells with invalid data");
			}

			//setCells(table);
			revalidate();
			repaint();
		}
		catch (UnsupportedFlavorException e)
		{
			// If clipboard has some other type of data
			this.getToolkit().beep();
			if ( _pasteDebug )
			{
				e.printStackTrace(System.out);
			}
		}
		catch (Exception e)
		{
			// Or if anything else goes wrong ...
			System.out.println("paste: error occurred during paste " + e);
			e.printStackTrace(System.out);
			this.getToolkit().beep();
		}
	}

	/**
	 *  Method Description
	 *
	 *@param  str  Description
	 *@param  r    Description
	 *@param  c    Description
	 *@return      Description
	 */
	protected int pasteCell(String str, int r, int c)
	{
		if (_pasteDebug)
		{
			System.out.println("pasteCell: pasting '" + str + "' at row=" + r + " col=" + c);
		}
		int modelCol = convertColumnIndexToModel(c);

		Object objValue = str;
		TableModel tableModel = getModel();
		if (tableModel instanceof RmaTableModelInterface)
		{
			RmaTableModelInterface rtm = (RmaTableModelInterface) tableModel;
			int paramId = rtm.getColumnParameter(modelCol);
			if (paramId != hec.data.Parameter.UNDEF_PARAMETER_ID)
			{

				ParameterScale scale = (ParameterScale) _parameterScaleTable.get(new Integer(modelCol));
				//assume the data being pasted is in the display unit system
				hec.data.ParamDouble pd = new hec.data.ParamDouble();
				pd.setValue(str);
				pd.setParameterId(paramId);
				pd.setUnitSystem(this.getDisplayUnitSystem());
				if (scale != null && pd.getParameterId() == scale.getParamId())
				{
					pd.scaleValue(1 / scale.scale);
				}
				objValue = pd;
				if (getDisplayUnitSystem() != rtm.getUnitSystem())
				{
					try
					{
						if (_pasteDebug)
						{
							System.out.println("pasteCell: attempting conversion: paramId=" + pd.getParameterId() +
									"Table Model unit System " + rtm.getUnitSystem());
						}
						ParamDouble pd2 =
								Units.convertUnits(pd, Parameter.getUnitsStringForSystem(pd.getParameterId(), pd.getUnitSystem()),
								Parameter.getUnitsStringForSystem(pd.getParameterId(), rtm.getUnitSystem()));
						pd2.setUnitSystem(rtm.getUnitSystem());
						objValue = pd2;
					}
					catch (Exception e)
					{
						System.out.println("pasteCell: unit conversion error r=" + r + "c=" + c + " " + e);
					}
				}
				if (_pasteDebug)
				{
					System.out.println("pasteCell: conversion yielded '" + objValue + "'");
				}
			}
		}
		//if this test passes we are now trying to paste beyond the table.
		//break out of loop
		TableCellEditor editor;
		TableColumn tc;
		TableColumnModel tcm = getColumnModel();
		Component comp;
		boolean invalidData = false;
		if (isCellEditable(r, c))
		{
			if (_pasteBackground != null)
			{
				setCellBackground(r, c, _pasteBackground);
			}
			if (_pasteForeground != null)
			{
				setCellForeground(r, c, _pasteForeground);
			}
			tc = tcm.getColumn(c);
			editor = tc.getCellEditor();
			if (editor instanceof RmaCellEditor)
			{
				comp = ((RmaCellEditor) editor).getComponent();
				if (comp instanceof RmaJTextField)
				{
					((RmaJTextField) comp).setText(str);
					if (!((RmaJTextField) comp).isValid(false))
					{
						invalidData = true;
						System.out.println("pasteCell: invalid data at row=" + r + " col=" + c + " data='" + str + "'");
					}
					if (!(comp instanceof UnitsComponent))
					{
						objValue = ((RmaJTextField) comp).getText();
					}
				}
			}
			else
			{
				invalidData = false;
			}
			if ( !invalidData )
			{
				setComboValue(objValue, r, c);
				setValueAt(objValue, r, c);
				if (_pasteDebug)
				{
					System.out.println("pasteCell: setting cell row=" + r + " col=" + c + " to '" + objValue + "'");
				}
				return 0;
			}
			return 1;
		}
		if (_pasteDebug)
		{
			System.out.println("pasteCell: cell not editable at row=" + r + " col=" + c);
		}
		return 0;
	}

	/**
	 *  Sets the ComboValue attribute of the RmaJTable object
	 *
	 *@param  value  The new ComboValue value
	 *@param  row    The new ComboValue value
	 *@param  col    The new ComboValue value
	 */
	private void setComboValue(Object value, int row, int col)
	{
		if (isEditing() && getEditingRow() != row && getEditingColumn() != col)
		{
			return;
		}

		TableColumnModel tcm = getColumnModel();
		TableColumn tc = tcm.getColumn(col);
		TableCellEditor editor = tc.getCellEditor();
		if (editor instanceof RmaCellEditor)
		{
			Component comp = ((RmaCellEditor) editor).getComponent();
			if (comp instanceof JComboBox)
			{
				editCellAt(row, col);
				((JComboBox) comp).setSelectedItem(value);
				commitEdit(true);
			}
		}
	}

	/**
	 *  copyall copies the whole table and puts it in the system clipboard not yet
	 *  implemented
	 */
	public void copyall() { }

	/**
	 *  Method Description
	 */
	public void copy()
	{
		copy(true);
	}

	/**
	 *  Method Description
	 */
	public void clear()
	{
		clear(true);
	}

	/**
	 *  clear clears all selected cells
	 *
	 *@param  postEvent  Description
	 */
	private void clear(boolean postEvent)
	{
		Clipboard cb = this.getToolkit().getSystemClipboard();
		cb.getContents(this);
		// Copy and save the cellValues in a Transferable object
//		StringSelection s = null;


//		Vector table = null;
		int row = 0, col = 0, lastRow = 0, lastCol = 0;
		try
		{
			Vector cellLocations = getSelectedCellRange();
			if (cellLocations.size() < 1)
			{
				return;
				// nothing selected!
			}

			commitEdit(false);
			//table = getCells();
			//table.ensureCapacity(getNumRows());  //make sure table vector has the same number of
			//rows as the real table
			CellLocation location;
			Object obj = "";
//			StringBuffer cellValues = new StringBuffer();
			location = (CellLocation) cellLocations.elementAt(0);
			row = location.getRow();
			col = location.getCol();
//			String current;
			Class cls;
			for (int i = 0; i < cellLocations.size(); i++)
			{
				location = (CellLocation) cellLocations.elementAt(i);
				if ( isCellEditable(location.getRow(), location.getCol()) )
				{
					lastRow = location.getRow();
					lastCol = location.getCol();
					cls = getColumnClass(location.getCol());
					if (cls == null )
					try
					{
						obj = cls.newInstance();
					}
					catch ( Exception e)
					{
						obj = "";
					}
					setValueAt(obj, location.getRow(),location.getCol());
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error in clear " + e.getMessage());
		}
		/*
		int r;
		int c;
		int[] cols = getSelectedColumns();
		int[] rows = getSelectedRows();

		commitEdit(false);

		if (rows.length == 0 && cols.length == 0)
		{
			return;
		}

		for (r = 0; r < rows.length; r++)
		{
			for (c = 0; c < cols.length; c++)
			{
				if (isCellEditable(rows[r], cols[c]))
				{
					setValueAt("", rows[r], cols[c]);
				}
			}
		}
		*/
		revalidate();
		repaint();
		if (postEvent)
		{
			fireTableChangeEvent(TableUpdateEvent.CLEAR, row, col,
					lastRow, lastCol);
		}
	}

	/**
	 *  copy copies all selected cells an puts them in the system clipboard
	 *
	 *@param  postEvent  Description
	 */
	private void copy(boolean postEvent)
	{
		Clipboard cb = this.getToolkit().getSystemClipboard();
//		Transferable t = cb.getContents(this);
		// Copy and save the cellValues in a Transferable object
		StringSelection s = null;

//		Vector table = null;
		try
		{
			Vector cellLocations = getSelectedCellRange();
			if (cellLocations.size() < 1)
			{
				return;
				// nothing selected!
			}

			//table = getCells();
			//table.ensureCapacity(getNumRows());  //make sure table vector has the same number of
			//rows as the real table
			CellLocation location;
			Object obj;
			StringBuffer cellValues = new StringBuffer();
			int row;
			location = (CellLocation) cellLocations.elementAt(0);
			row = location.getRow();
			String current;
			for (int i = 0; i < cellLocations.size(); i++)
			{
				location = (CellLocation) cellLocations.elementAt(i);
				
				obj = location.getData();
				
				if (i > 0)
				{
					if (location.getRow() == row)
					{
						cellValues.append("\t");
					}
					else
					{
						row = location.getRow();
						cellValues.append("\n");
					}
				}

				if (obj != null)
				{
					current = obj.toString();
				}
				else
				{
					current = null;
				}

				if (current != null)
				{
					if (current.length() < 1)
					{
						current = " ";
					}
				}
				else
				{
					current = " ";
				}

				cellValues.append(current);
				/*
				 *  if ( obj instanceof String )
				 *  {
				 *  if ( obj.length()
				 *  cellValues += (String)obj;
				 *  }
				 *  else
				 *  {
				 *  if ( obj != null )
				 *  cellValues += obj.toString();
				 *  }
				 */
			}
//System.out.println("Copied " + cellValues );

			/*
			 *  if(location.start_row > range.end_row)
			 *  {
			 *  int temp = range.start_row;
			 *  range.start_row = range.end_row;
			 *  range.end_row = temp;
			 *  }
			 *  if(range.start_column > range.end_column)
			 *  {
			 *  int temp = range.start_column;
			 *  range.start_column = range.end_column;
			 *  range.end_column = temp;
			 *  }
			 *  for(int r = range.start_row; r < range.end_row+1; r++)
			 *  {
			 *  Vector row = (Vector) table.elementAt(r);
			 *  if (row == null) continue;
			 *  row.setSize(getNumColumns()); //make sure the size of the row Vector
			 *  /holds the same number of columns
			 *  for(int c = range.start_column; c < range.end_column+1; c++)
			 *  {
			 *  if (row.elementAt(c) == null)
			 *  row.setElementAt(new String(""), c);
			 *  if(c == range.start_column)
			 *  {
			 *  cellValues += (row.elementAt(c)).toString();
			 *  }
			 *  else
			 *  {
			 *  cellValues += "\t" + (row.elementAt(c)).toString();
			 *  }
			 *  }
			 *  cellValues += "\n";
			 *  }
			 */
			s = new StringSelection(cellValues.toString());
		}
		catch (Exception e)
		{
			System.out.println("Error in copy " + e.getMessage());
		}
		// Put that object on the clipboard
		cb.setContents(s, s);
		if (postEvent)
		{
			CellLocation cl = getFirstSelectedCell();
			CellLocation cl2 = getLastSelectedCell();
			if(cl == null || cl2 == null) return;

			fireTableChangeEvent(TableUpdateEvent.COPY, cl.getRow(), cl.getCol(),
					cl2.getRow(), cl2.getCol());
		}

	}

	/**
	 *  insert a row at the current row ask user how many rows to insert.
	 */
	public void insertRow()
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}
		InsertDlg dialog = null;
		Window w = SwingUtilities.windowForComponent(this);
		if(w instanceof Dialog) {
			dialog = new InsertDlg((java.awt.Dialog)w,"Insert Rows", true);
		} else if(w instanceof Frame) {
			dialog = new InsertDlg((java.awt.Frame)w,"Insert Rows", true);
		} else {
			//should not get here but lets take care of the problem if we do
			rma.util.RMAIO.postWarning(this, "Can't open Insert Row Dialog, because there is not\na valid window component parent for this"+
											  "table");
			return;
		}
		dialog.setRowsToInsert(1);
		dialog.setBounds(getBounds());
		dialog.setVisible(true);

		if (dialog.isCanceled())
		{
			return;
		}
		int insertRow = dialog.getRowsToInsert();

		commitEdit(false);

		int selRow = getSelectedRow();
		int i = 0;
		if (selRow < 0 || selRow >= getRowCount())
		{
			for (i = 0; i < insertRow; i++)
			{
				//just tack on a row at the end
				appendRow();
			}
			fireTableChangeEvent(TableUpdateEvent.APPEND_ROW, selRow, 0,
					selRow + i, 0);

			return;
		}

//		int start = 1;
		int stop = insertRow;
		Vector v;
		if (getModel() instanceof RmaTableModelInterface)
		{
			v = new Vector(getColumnCount());
			for (int j = 0; j < getColumnCount(); j++)
			{
				Class cls = getModel().getColumnClass(j);
				try
				{
					Object obj = cls.newInstance();
					v.addElement(obj);
				}
				catch (Throwable e)
				{
					v.addElement(new String());
				}
			}
			((RmaTableModelInterface) getModel()).insertRows(selRow, stop, (Vector) v.clone());
			revalidate();

			fireTableChangeEvent(TableUpdateEvent.INSERT_ROW, selRow, 0,
					selRow + i, 0);
		}

	}

	/**
	 *  Appends a row to the end of the table
	 */
	public void appendRow()
	{
		TableModel tm = getModel();
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}

		((RmaTableModelInterface) tm).addRow(new Vector(getColumnCount()));
		revalidate();

		fireTableChangeEvent(TableUpdateEvent.APPEND_ROW, getRowCount() - 1, 0,
				getRowCount(), getColumnCount());

	}

	/**
	 *  append a row of data to the end of the table
	 *
	 *@param  data  Description
	 */
	public void appendRow(Vector data)
	{
		appendRow(data, true);
		/*
		 *  TableModel tm = getModel();
		 *  if ( !(tm instanceof RmaTableModelInterface)) return;
		 *  data.ensureCapacity(getColumnCount());
		 *  ((RmaTableModelInterface)tm).addRow(data);
		 *  fireTableChangeEvent(TableUpdateEvent.APPEND_ROW, getRowCount(), 0,
		 *  getRowCount(), getColumnCount() );
		 *  revalidate();
		 */
	}

	/**
	 *  append a row to the table. allow user to pick whether they want to
	 *  revalidate the table.
	 *
	 *@param  data   Description
	 *@param  reval  Description
	 */
	public void appendRow(Vector data, boolean reval)
	{
		TableModel tm = getModel();
		if (!(tm instanceof RmaTableModelInterface))
		{
			return;
		}

		data.ensureCapacity(getColumnCount());
		((RmaTableModelInterface) tm).addRow(data);

		fireTableChangeEvent(TableUpdateEvent.APPEND_ROW, getRowCount() - 1, 0,
				getRowCount(), getColumnCount());

		if (reval)
		{
			revalidate();
		}
	}

	/**
	 *  append a row of data to the table
	 *
	 *@param  data    Description
	 *@param  reval   Description
	 *@param  bcolor  Description
	 *@param  fcolor  Description
	 */
	public void appendRow(Vector data, boolean reval, Color bcolor, Color fcolor)
	{
		TableModel tm = getModel();
		if (!(tm instanceof RmaTableModelInterface))
		{
			return;
		}

		data.ensureCapacity(getColumnCount());
		((RmaTableModelInterface) tm).addRow(data);
		int rowcnt = tm.getRowCount() - 1;
		setRowBackground(rowcnt, bcolor);
		setRowForeground(rowcnt, fcolor);

		if (reval)
		{
			revalidate();
		}
		fireTableChangeEvent(TableUpdateEvent.APPEND_ROW, getRowCount() - 1, 0,
				getRowCount(), getColumnCount());

	}

	/**
	 *  append rows number of rows to the table
	 *
	 *@param  rows  Description
	 */
	public void appendRows(int rows)
	{
		for (int i = 0; i < rows; i++)
		{
			appendRow();
		}
	}

	/**
	 *  delete the selected rows from the table
	 */
	public void deleteRow()
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}

		commitEdit(false);
		//tableCells = getCells();
		int[] rows = getSelectedRows();
		if (rows == null || rows.length == 0)
		{
			//((DefaultTableModel)getModel()).removeRow(getRowCount());
			return;
		}
		else
		{
			for (int i = rows.length - 1; i >= 0; i--)
			{
				if (rows[i] > getModel().getRowCount())
				{
					continue;
				}
				((RmaTableModelInterface) getModel()).deleteRow(rows[i]);
				if (this._rowheaderTbl != null)
				{
					try
					{
						((RmaTableModelInterface) _rowheaderTbl.getModel()).deleteRow(rows[i]);
					}
					catch (Exception e)
					{
					}
				}
			}
		}
		fireTableChangeEvent(TableUpdateEvent.DELETE_ROW, rows[0], 0,
				rows[rows.length - 1], 0);

		revalidate();
	}

	/**
	 *  remove the last row from the table
	 */
	public void removeLastRow()
	{
		if (!(getModel() instanceof RmaTableModelInterface))
		{
			return;
		}
		int rowcnt = getRowCount() - 1;
		((RmaTableModelInterface) getModel()).deleteRow(rowcnt);
	}

	/**
	 *  does nothing
	 */
	public void traverseCellLeft() { }

	/**
	 *  does nothing
	 */
	public void traverseCellUp() { }

	/**
	 *  does nothing
	 */
	public void traverseCellDown() { }

	/**
	 *  does nothing
	 */
	public void traverseCellRight() { }

	/**
	 *  Sometime you don't want the user to be able to add/delete/append rows this
	 *  turns off that capability. Also turns on/off pasting appending rows if
	 *  more rows are in the cut buffer than the table has.
	 *
	 *@param  enable  false to turn off add/delete/append rows and pasting appending rows
	 */
	public void setAddRemoveEnabled(boolean enable)
	{
		addRemoveEnabled = enable;
		// for Bean Info Descriptor
		insertMi.setEnabled(enable);
		insertMi.setVisible(enable);
		appendMi.setEnabled(enable);
		appendMi.setVisible(enable);
		deleteMi.setEnabled(enable);
		deleteMi.setVisible(enable);
	}
	/**
	 * set the first row fixed.  This disallows inserting before the first row
	 * @param fixed
	 */
	public void setFirstFixedRow(boolean fixed)
	{
		_firstFixedRow = fixed;
	}
	/**
	 *
	 * @return
	 */
	public boolean isFirstFixedRow()
	{
		return _firstFixedRow;
	}
	/**
	 * whether to process key events which causes the Enter and Escape key to be
	 * consumed.
	 *
	 * @param process true to process key events
	 */
	public void setProcessKeyEvents (boolean process)
	{
		_processKeyEvents = process;
	}
	/**
	 *  get the status of the append/insert/delete functionality is turned on/off
	 *
	 *@return    The AddRemoveEnabled value
	 */
	public boolean getAddRemoveEnabled()
	{
		return addRemoveEnabled;
	}

	/**
	 *  set the popup menu enabled or not
	 *
	 *@param  enable  The new PopupMenuEnabled value
	 */
	public void setPopupMenuEnabled(boolean enable)
	{
		_popupEnabled = enable;
	}

	/**
	 *  get whether the popup menu is enabled
	 *
	 *@return    The PopupMenuEnabled value
	 */
	public boolean getPopupMenuEnabled()
	{
		return _popupEnabled;
	}

	/**
	 *  Gets the PopupMenu attribute of the RmaJTable object
	 *
	 *@return    The PopupMenu value
	 */
	public JPopupMenu getPopupMenu()
	{
		return _popup;
	}

	/**
	 *  set the table to have row headers. This only works if the cell renderer is
	 *  an RmaCellRenderer. The first row of the table model becomes the row
	 *  headers
	 *
	 *@param  enable  The new RowHeaderEnabled value
	 */
	public void setRowHeaderEnabled(boolean enable)
	{
		_rowHeaderEnabled = enable;
		if(!enable)
		{
			this.setColumnEnabled(enable, 0);
			TableColumnModel tcm = getColumnModel();
			TableColumn tc = tcm.getColumn(0);
			TableCellRenderer tcr = tc.getCellRenderer();
			if(tcr instanceof RmaRowHeaderRenderer)
			{
				tcr = ((RmaRowHeaderRenderer)tcr).getTableCellRenderer();
				tc.setCellRenderer(tcr);
			}
		}
		else
		{
			setRowHeaderRenderer(0);
		}

	}

	public void setRowHeaderRenderer(int column)
	{
		_rowHeaderEnabled = true;
		this.setColumnEnabled(false, column);
		TableColumnModel tcm = getColumnModel();
		TableColumn tc = tcm.getColumn(column);
		TableCellRenderer tcr = tc.getCellRenderer();
		if(tcr == null) {
			tcr = new RmaCellRenderer();
		}
		tc.setCellRenderer(new RmaRowHeaderRenderer(tcr));
	}

	/**
	 *  get whether row headers are enabled
	 *
	 *@return    The RowHeaderEnabled value
	 */
	public boolean getRowHeaderEnabled()
	{
		return _rowHeaderEnabled;
	}

	/**
	 *  if row headers are enabled and the cell renderer is an RmaCellRenderer make
	 *  the row headers the same as the row number + 1.
	 *
	 *@param  enable  The new AutoRowHeaders value
	 */
	public void setAutoRowHeaders(boolean enable)
	{
		_autoRowHeaders = enable;
		this.setRowHeaderRenderer(0);
	}

	/**
	 *  Sets the AutoRowHeaders attribute of the RmaJTable object
	 *
	 *@param  enable  The new AutoRowHeaders value
	 *@param  offset  The new AutoRowHeaders value
	 */
	public void setAutoRowHeaders(boolean enable, int offset)
	{
		setAutoRowHeaders(enable);
		_autoRowHeaderOffset = offset;
	}

	/**
	 *  Gets the AutoRowHeaderOffset attribute of the RmaJTable object
	 *
	 *@return    The AutoRowHeaderOffset value
	 */
	public int getAutoRowHeaderOffset()
	{
		return _autoRowHeaderOffset;
	}

	/**
	 *  get whether auto row headers are enabled
	 *
	 *@return    The AutoRowHeaders value
	 */
	public boolean getAutoRowHeaders()
	{
		return _autoRowHeaders;
	}

	/**
	 *  prints out the current widths of all the columns useful for setting up the
	 *  table
	 */
	public void columnSizes()
	{
		TableColumnModel tcm = getColumnModel();
		TableColumn tc;
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			tc = tcm.getColumn(i);
			System.out.print("" + tc.getWidth() + ",");
		}
		System.out.println("");
	}

	/**
	 *  returns the tooltip text for a given cell converts the mouse event to a
	 *  row/column position
	 *
	 *@param  event  Description
	 *@return        The ToolTipText value
	 */
	
	public String getToolTipText0(MouseEvent event)
	{
		Point p = event.getPoint();

		// Locate the renderer under the event location
		int column = columnAtPoint(p);
		int row = rowAtPoint(p);
		if (getAutoRowHeaders() && column == 0)
		{
			return super.getToolTipText(event);
		}

		Object obj = getValueAt(row, column);
		if ( event.isControlDown() && event.isShiftDown() && obj != null )
		{
			return "Object at " +row+", "+column+" is a " + obj.getClass().getName();
		}
		if (obj != null && obj.toString() != null)
		{
			TableColumn tc = getColumnModel().getColumn(column);
			TableCellRenderer renderer = tc.getCellRenderer();

			if (renderer == null)
			{
				String text = obj.toString();
				if (text != null && !" ".equals(text) && !"".equals(text))
				{
					return text;
				}
				return super.getToolTipText(event);
			}
			Component component = renderer.getTableCellRendererComponent(
					this, obj, false, false, row, column);
			String compToolTip = null;

			if ( component instanceof JComponent )
			{
				compToolTip = ((JComponent)component).getToolTipText();
			}
			if ( compToolTip != null ) return compToolTip;

			if (component instanceof JLabel)
			{
//   			    JLabel jlabel = (JLabel)component;
//   			    jlabel.setToolTipText(jlabel.getText());

				String labelText = ((JLabel) component).getText();
				String labelToolTip = ((JLabel) component).getToolTipText();
				if (labelToolTip != null && labelToolTip.trim().length() > 0 && (!(labelToolTip.equals(labelText))))
				{
					return labelToolTip;
				}
				else
				{
					return labelText;
				}
			}
			else
			{
				String text = obj.toString();
				if (text != null && !text.equals(" ") && !text.equals(""))
				{
					return text;
				}
			}
		}

		return super.getToolTipText(event);
	}

	/**
	 * Returns true if the cell at <code>row</code> and <code>column</code>
	 * is editable.  Otherwise, invoking <code>setValueAt</code> on the cell
	 * will have no effect.
	 * <p>
	 * <b>Note</b>: The column is specified in the table view's display
	 *              order, and not in the <code>TableModel</code>'s column
	 *            order.  This is an important distinction because as the
	 *            user rearranges the columns in the table,
	 *            the column at a given index in the view will change.
	 *              Meanwhile the user's actions never affect the model's
	 *              column ordering.
	 *
	 *
	 * @param   row      the row whose value is to be queried
	 * @param   column   the column whose value is to be queried
	 * @return  true if the cell is editable
	 * @see #setValueAt
	 */
	
	public boolean isCellEditable(int row, int column)
	{
		if ( getAutoRowHeaders() && column == 0 ) return false;
		return getModel().isCellEditable(row, convertColumnIndexToModel(column));
	}
	/**
	 *  Gets the ManagingFocus attribute of the RmaJTable object
	 *
	 *@return    The ManagingFocus value
	 */
	
	public boolean isManagingFocus()
	{
		return true;
	}

	
	public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
		Rectangle cellRect = super.getCellRect(row, column, includeSpacing);

		if(!_hasMultilineRenderer) return cellRect;

		int rm = includeSpacing ?  0 : getRowMargin();

		// This is not the same as grow(), it rounds differently.
		int rowHeight = this.getRowHeight(row);
		int yPos = 0;
		for(int i=0; i < row; i++) yPos += getRowHeight(i);
		cellRect.setBounds(cellRect.x, yPos + rm/2, cellRect.width, rowHeight - rm);
		return cellRect;
	}

	public int getColumnWidth(int col) {

		return getColumnModel().getColumn(col).getWidth();
	}

	public int[] getColumnWidths() {

		TableColumnModel columnModel = getColumnModel();
		int columnCount = columnModel.getColumnCount();
		int[] widths = new int[columnCount];
		for (int i = 0; i < columnCount; ++i) {
			widths[i] = columnModel.getColumn(i).getWidth();
		}
		return widths;
	}

	public void setColumnPrecision(int colNum, int precision) {

		TableColumnModel tcm = getColumnModel();
		if (colNum >= 0 && colNum < tcm.getColumnCount()) {
			TableCellRenderer tcr = tcm.getColumn(colNum).getCellRenderer();
			if (tcr == null) tcr = getDefaultRenderer(getColumnClass(colNum));
			Component component = tcr.getTableCellRendererComponent(this, "123", false, false, 0, colNum);
			Color foreground = component.getForeground();
			Color background = component.getBackground();
			Font font = component.getFont();
			Class tcrClass = ((Object)tcr).getClass();
			try {
				DecimalCellRenderer dcr = new DecimalCellRenderer(precision);
				dcr.setAllowDefaultFont(true);
				component = dcr.getTableCellRendererComponent(this, "123", false, false, 0, colNum);
				component.setForeground(background);
				component.setBackground(background);
				component.setFont(font);
				tcm.getColumn(colNum).setCellRenderer(dcr);
			}
			catch (Exception e) {
			}
		}
	}

	
	public int getRowHeight(int row) {
		if(row < 0 || row > this.getRowCount() || !_hasMultilineRenderer) return super.getRowHeight(row);


//		rma.awt.table.MultiLineCellRenderer renderer = null;

		int height = -1;
		for(int i=0; i < this.getColumnCount(); i++) {
			TableCellRenderer r = getColumnModel().getColumn(i).getCellRenderer();
			if(r instanceof MultiLineCellRenderer) {
				int width = getColumnModel().getColumn(i).getWidth();
				Insets ins = ((MultiLineCellRenderer)r).getInsets();
				width -= getColumnModel().getColumnMargin();
				width = width - (ins.left+ins.right);
				Object value = getValueAt(row,i);
				if (value == null) continue;
				height = Math.max(height,getPreferredLineHeight(value.toString(),width));//c.getPreferredSize().height);
			}
		}
		if(height != -1) return height;

		return super.getRowHeight(row);
	}

	public int getPreferredLineHeight(String str, int width) {
		if(str == null) return 0;
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);

		if(str == null) return 0;
		int lineCount   = 1;
		int curWidth    = 0;
		int spaceWidth  = fm.charWidth(' ');
		rma.util.PowerfulTokenizer tokenizer = new rma.util.PowerfulTokenizer(str," \n\r",true);
		tokenizer.setTrimTokens(false);
		String token;
		String delim = null;
		String SPACE_DELIM = " ";
		while(tokenizer.hasMoreElements()) {
			token = tokenizer.nextToken();
			if(tokenizer.hasMoreElements()) {
				delim = tokenizer.nextToken();
				if(delim != null && delim.length() == 0) {
					delim = SPACE_DELIM;
				}
			}
			int delimWidth = 0;
			if(delim != null) {
				delimWidth = fm.stringWidth(delim);
			}
			int tokenWidth = fm.stringWidth(token);
			if(tokenWidth+delimWidth+curWidth > width || (delim != null &&delim.indexOf("\n") >= 0)) {
				lineCount++;
				curWidth = tokenWidth+delimWidth;
				continue;
			}
			curWidth += (tokenWidth+delimWidth);
			delim = null;
		}

		return fm.getHeight()*lineCount + fm.getDescent();
	}

	
	public int rowAtPoint(Point point) {
		int y = point.y;
//		int rowSpacing = getIntercellSpacing().height;
		int rowCount = getRowCount();
		int rowHeight = 0;
		for (int row=0; row<rowCount; row++) {
		  rowHeight += getRowHeight(row);// + rowSpacing;
		  if (y < rowHeight) {
			return row;
		  }
		}
		return -1;
   }

	/**
	 *  add a row header to the JScrollPane
	 *
	 * @param  model        The new ViewportRowHeader value
	 * @param  groups       use this if you need to add column groups to the row headers, otherwise null.
	 * @param  columns      A list of columns to add to each group. The rows of the array correspond to each group in the group array,
	 *                      i.e row 1 is the first column group in the group array, and all items in row one represent column numbers
	 *                      of the columns to add to that group.  If grououps are null then columns is ignored.
	 * @param  width        The preferred width of the row headers
	 */
//	public void setViewportRowHeader(RmaTableModelInterface model, String header, Component headerComp, int width)
	public MonthlyTable setViewportRowHeader(RmaTableModelInterface model, ColumnGroup[] groups, int[][] columns, int width)
	{
//		String header = "";
//		Component headerComp = null;
		JViewport jv2 = new JViewport();
		final JScrollPane sp = this.getScrollPane();
		final MonthlyTable parentTable = this;
		_rowheaderTbl = new MonthlyTable(_parent) {
			
			protected void  configureEnclosingScrollPane() {
				sp.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, this.getTableHeader());
			}
			
			public void printData() {
				parentTable.printData();
			}

			
			public void printPreview() {
				parentTable.printPreview();
			}

		};
		_rowheaderTbl.setModel(model);

		_rowheaderTbl.setUnitsHeaderRenderer();
		_rowheaderTbl.addMouseListener(
			new MouseAdapter()
			{
				
				public void mousePressed(MouseEvent e)
				{
					if (!SwingUtilities.isLeftMouseButton(e))
					{
						return;
					}
					Point p = e.getPoint();
					int row = _rowheaderTbl.rowAtPoint(p);
					int column = 0;
					updateSelection(row, column, e.isControlDown(), e.isShiftDown());
				}
			});
		_rowheaderTbl.setBackground(UIManager.getColor("TableHeader.background"));
		//rh.setReorderingAllowed(false);
		_rowheaderTbl.setCellSelectionEnabled(false);
		_rowheaderTbl.setRowSelectionAllowed(false);
		_rowheaderTbl.setColumnSelectionAllowed(false);

		_rowheaderTbl.setEnabled(false);
//        _rowheaderTbl.setTableHeader();
		jv2.setPreferredSize(new Dimension(width, getPreferredSize().height));
		jv2.setView(_rowheaderTbl);
		//jv2.setBackground(Color.lightGray);
		_scrollPane.setRowHeader(jv2);


		if(groups != null) {
			GroupableTableHeader gTableHeader = new GroupableTableHeader(_rowheaderTbl.getColumnModel());
			for(int i=0; i < groups.length; i++) {
				ColumnGroup g = groups[i];
				for(int j=0;j<columns[i].length; j++) {
					g.add(_rowheaderTbl.getColumnModel().getColumn(columns[i][j]));
				}
				gTableHeader.addColumnGroup(g);
			}
			_rowheaderTbl.setTableHeader(gTableHeader);
		}

//SJN
//		if (headerComp == null)
//		{
//			JButton jb = new JButton(header);
//			//jb.setEnabled(false);
//			jb.setFont(getFont());
//			jb.setForeground(UIManager.getColor("TableHeader.foreground"));
//			jb.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//			_scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, jb);
//		}
//		else
//		{
//			_scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, headerComp);
//		}

		UnitsHeaderRenderer uhr = new UnitsHeaderRenderer();

		TableColumnModel tcm = _rowheaderTbl.getTableHeader().getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableColumn tc = tcm.getColumn(i);
			tc.setHeaderRenderer(uhr);
		}

		setAddRemoveEnabled(false);
		// this is until the table in the viewport and the table are tied together
		_rowHeaderEnabled = false;
		_rowheaderTbl.setDisplayUnitsSystem(getDisplayUnitSystem());
		return _rowheaderTbl;
	}

	/**
	 *  Method Description
	 *
	 *@param  sm      Description
	 *@param  index   Description
	 *@param  toggle  Description
	 *@param  extend  Description
	 */
	protected void updateSelectionModel(ListSelectionModel sm, int index,
			boolean toggle, boolean extend)
	{
		if (!extend)
		{
			if (!toggle)
			{
				sm.setSelectionInterval(index, index);
			}
			else
			{
				if (sm.isSelectedIndex(index))
				{
					sm.removeSelectionInterval(index, index);
				}
				else
				{
					sm.addSelectionInterval(index, index);
				}
			}
		}
		else
		{
			sm.setLeadSelectionIndex(index);
		}
	}

	/**
	 *  update the cell selection. will use the selection model to determine if the
	 *  entire row/column should select.
	 *
	 *@param  rowIndex     - the row to select
	 *@param  columnIndex  - the column to select
	 *@param  toggle       - if the selection is currently selected then deselect
	 *@param  extend       - extend to the current select to the new selection.
	 */
	public void updateSelection(int rowIndex, int columnIndex,
			boolean toggle, boolean extend)
	{
		// Autoscrolling support.
		Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
		if (cellRect != null)
		{
			scrollRectToVisible(cellRect);
		}

		ListSelectionModel rsm = getSelectionModel();
		ListSelectionModel csm = getColumnModel().getSelectionModel();

		// Update column selection model
		updateSelectionModel(csm, columnIndex, toggle, extend);

		// Update row selection model
		updateSelectionModel(rsm, rowIndex, toggle, extend);
	}

	/**
	 *  Gets the ViewportRowHeader attribute of the RmaJTable object
	 *
	 *@return    The ViewportRowHeader value
	 */
	public JTable getViewportRowHeader()
	{
		return _rowheaderTbl;
	}

	/**
	 *  Gets the ClassForName attribute of the RmaJTable object
	 *
	 *@param  className  Description
	 *@return            The ClassForName value
	 */
	protected Class getClassForName(String className)
	{
		Class cls = null;
		try
		{
			cls = Class.forName(className);
		}
		catch (Exception e)
		{
			System.out.println("Can't find class " + className);
		}
		return cls;
	}

	/**
	 * Returns a reference to the print manager for this table.
	 */
	public TablePrintManager getPrintManager() {
		if(_printManager == null) _printManager = new TablePrintManager(this);
		return _printManager;
	}

	/**
	 *  create the printjob and tell it to print.
	 */
	public void printData()
	{
		cellEditor = (RmaCellEditor)getCellEditor();
		if (cellEditor != null)
		   cellEditor.stopCellEditing();

		if(_printManager == null) _printManager = new TablePrintManager(this);
		_printManager.getPrintProperties().useDefaultPrintHeader = _useDefaultPrintHeader;
		_printManager.printData();
		return;
//        }
//		try
//		{
//			PrinterJob prnJob = PrinterJob.getPrinterJob();
//			prnJob.setPrintable(this);
//			if (SwingUtilities.windowForComponent(this) != null)
//			{
//				if (!prnJob.printDialog())
//				{
//					return;
//				}
//			}
//			_maxNumPage = 1;
//			_printDateStr = null;
//			prnJob.print();
//		}
//		catch (PrinterException e)
//		{
//			e.printStackTrace();
//			System.err.println("Printing error: " + e.toString());
//		}
	}

	/**
	 *  print this table This method is public as an implementation side effect. do
	 *  not call.
	 *
	 *@param  g                     Description
	 *@param  pageFormat            Description
	 *@param  pageIndex             Description
	 *@return                       Description
	 *@exception  PrinterException  Description
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			 throws PrinterException
	{
		clearSelection();
		// don't want to print the selection.
		commitEdit(true);
		columnModel.getSelectionModel().setAnchorSelectionIndex(-1);
		selectionModel.setAnchorSelectionIndex(-1);

		if (_printDateStr == null)
		{
			_printDateStr = new Date().toString().substring(4);
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		int fontHeight = g2.getFontMetrics().getHeight();
		int fontDesent = g2.getFontMetrics().getDescent();
//		boolean setLandscape = false;
		// did we programatically set layout to LandScape?

		double pageHeight;
		double pageWidth;
		double imageableX = pageFormat.getImageableX();
		;
		double imageableY = pageFormat.getImageableY();

		int titleLinesCnt = 0;
		int titleHeight = 0;
		StringTokenizer titleLines = null;
		PageText tableTitle = null;
		{
			//if ( pageIndex == 0 )

			tableTitle = getPrintTitle();
			if (tableTitle != null)
			{
				titleLines = new StringTokenizer(tableTitle.getText(), "\n");
				titleLinesCnt = titleLines.countTokens();
			}
		}
		PageText otherTitle = null;
		StringTokenizer otherLines = null;
		int otherLinesCnt = 0;
		otherTitle = getPrintOtherInfo();
		if (otherTitle != null)
		{
			otherLines = new StringTokenizer(otherTitle.getText(), "\n");
			otherLinesCnt = otherLines.countTokens();
		}
		if (titleLinesCnt > 0)
		{
			titleHeight = titleLinesCnt * fontHeight;
		}
		if (otherLinesCnt > 0)
		{
			titleHeight += (otherLinesCnt * fontHeight);
		}

		double tableWidth =  getColumnModel().getTotalColumnWidth();

		pageWidth = pageFormat.getImageableWidth();

		// try and force Landscape if the table is wider than the page
		if (tableWidth >= pageWidth)
		{
			pageFormat.setOrientation(PageFormat.LANDSCAPE);
			//setLandscape = true;
		}

		if (pageFormat.getOrientation() == PageFormat.PORTRAIT)
		{
			pageHeight = pageFormat.getImageableHeight() - fontHeight;
			pageWidth = pageFormat.getImageableWidth();
		}
		else
		{
			pageHeight = pageFormat.getImageableHeight() - fontHeight;
			pageWidth = pageFormat.getImageableWidth();
		}
		//if ( pageIndex == 0 ) pageHeight - titleHeight;

		double scale = 1;
		// scale to fit page
		if (tableWidth >= pageWidth)
		{
			scale = pageWidth / tableWidth;
		}
		// height of the table header
		double headerHeightOnPage = getTableHeader().getHeight() * scale;
		// width of the table header
		double tableWidthOnPage = tableWidth * scale;

		// height of 1 row of the table
		double oneRowHeight;
		if (rma.util.RMAIO.getJavaVersion() >= rma.util.RMAConst.JAVA13)
		{
			oneRowHeight = (getRowHeight()) * scale;
		}
		else
		{
			oneRowHeight = (getRowHeight() + getRowMargin()) * scale;
		}

		// number of rows to print on the page
		int firstNumRowsOnAPage = (int) ((pageHeight - headerHeightOnPage - titleHeight) / oneRowHeight);
		int numRowsOnAPage = (int) ((pageHeight - headerHeightOnPage
		/*
		 *  -titleHeight
		 */
				) / oneRowHeight);
//System.out.println("number of rows on the page is " + firstNumRowsOnAPage+" "+numRowsOnAPage);

		// the height of the table on the page
		double firstPageHeightForTable = oneRowHeight * firstNumRowsOnAPage;
		double pageHeightForTable = oneRowHeight * numRowsOnAPage;

		// the total number of pages that this table will need
		int totalNumPages = (int) Math.ceil(((double) getRowCount()) / numRowsOnAPage);

		if (pageIndex >= totalNumPages)
		{
			_printDateStr = null;
			return NO_SUCH_PAGE;
		}
		// set the graphics origin to the upper left printable corner
		g2.translate(imageableX, imageableY);
		//
		// FOOTER
		//
		// date at left
		g2.drawString(_printDateStr, 0, (int) (pageHeight + fontHeight - fontDesent));
		//page number at bottom center
		g2.drawString("Page: " + (pageIndex + 1), (int) pageWidth / 2 - 35,
				(int) (pageHeight + fontHeight - fontDesent));
		//
		// TITLE ON FIRST PAGE
		//
		// draw title, if any, on the first page
		if (pageIndex == 0 && titleLines != null)
		{
			FontMetrics fm = g2.getFontMetrics();
			String line;
			int lineWidth;
			int cnt = 0;
			int xTitleLoc;
			int
					yTitleLoc;

			int hLoc = tableTitle.getHorizontalLocation();
			while (titleLines.hasMoreElements())
			{
				cnt++;
				line = titleLines.nextToken();
				lineWidth = fm.stringWidth(line);
				yTitleLoc = (fontHeight * cnt - fontDesent);
				switch (hLoc)
				{
					case PageText.LEFT:
						xTitleLoc = 0;
						//pageWidth/2-(lineWidth/2);
						break;
					case PageText.RIGHT:
						xTitleLoc = (int) pageWidth - lineWidth;
						break;
					default:
						// center
						xTitleLoc = (int) pageWidth / 2 - (lineWidth / 2);
						break;
				}
				g2.drawString(line, xTitleLoc, yTitleLoc);
			}
		}
		// draw other title info, if any, on first page.
		if (pageIndex == 0 && otherTitle != null)
		{
			FontMetrics fm = g2.getFontMetrics();
			String line;
			int lineWidth;
			int cnt = 0;
			int xTitleLoc;
			int
					yTitleLoc;

			int hLoc = otherTitle.getHorizontalLocation();
			while (otherLines.hasMoreElements())
			{
				cnt++;
				line = otherLines.nextToken();
				lineWidth = fm.stringWidth(line);
				yTitleLoc = (fontHeight * cnt - fontDesent) + (titleLinesCnt * fontHeight);
				switch (hLoc)
				{
					case PageText.LEFT:
						xTitleLoc = 0;
						//pageWidth/2-(lineWidth/2);
						break;
					case PageText.RIGHT:
						xTitleLoc = (int) pageWidth - lineWidth;
						break;
					default:
						// center
						xTitleLoc = (int) pageWidth / 2 - (lineWidth / 2);
						break;
				}
				g2.drawString(line, xTitleLoc, yTitleLoc);
			}
		}

		int xpos = 0;
		// if the scale = 1 then center the printout horizontally on the page
		if (scale == 1)
		{
			xpos = (int) ((pageWidth - tableWidthOnPage) / 2);
			if (xpos < 0)
			{
				xpos = 0;
			}
		}
		//
		// PAINT THE TABLE
		//
		if (pageIndex == 0)
		{
			g2.translate(0f, -pageIndex * firstPageHeightForTable);
		}
		else
		{
			g2.translate(0f, -pageIndex * pageHeightForTable + 75);
		}
		g2.translate(xpos, headerHeightOnPage);

		// only move the origin down to make room for the title if we're on the first page
		if (pageIndex == 0)
		{
			g2.translate(0f, (titleLinesCnt + otherLinesCnt) * fontHeight);
		}

		// for the last page, if this piece of the table is smaller than the size available,
		//clip to the appropriate bounds.
		if (pageIndex + 1 == totalNumPages)
		{
			int lastRowPrinted = numRowsOnAPage * (pageIndex - 1) + firstNumRowsOnAPage;
			int numRowsLeft = getRowCount() - lastRowPrinted;
			g2.setClip(0, (int) (pageHeightForTable * (pageIndex - 1) + firstPageHeightForTable),
					(int) Math.ceil(tableWidthOnPage),
					(int) Math.ceil(oneRowHeight * numRowsLeft));
		}
		else
		{
			//else clip to the entire area available.

			if (pageIndex == 0)
			{
				g2.setClip(0, (int) (firstPageHeightForTable * pageIndex),
						(int) Math.ceil(tableWidthOnPage),
						(int) Math.ceil(firstPageHeightForTable));
			}
			else
			{
				g2.setClip(0, (int) (pageHeightForTable * (pageIndex - 1) + firstPageHeightForTable),
						(int) Math.ceil(tableWidthOnPage),
						(int) Math.ceil(pageHeightForTable));
				/*
				 *  g2.setClip(0, (int)(pageHeightForTable*pageIndex),
				 *  (int) Math.ceil(tableWidthOnPage),
				 *  (int) Math.ceil(pageHeightForTable));
				 */
			}
		}

		g2.scale(scale, scale);
		paint(g2);
		// now paint the table header
		g2.scale(1 / scale, 1 / scale);
		// move origin to correct location
		if (pageIndex == 0)
		{
			g2.translate(0f, pageIndex * firstPageHeightForTable);
		}
		else
		{
			g2.translate(0f, ((pageIndex - 1) * pageHeightForTable + firstPageHeightForTable));
		}

		//g2.translate(0f,pageIndex*pageHeightForTable);
		g2.translate(0f, -headerHeightOnPage);

		g2.setClip(0, 0, (int) Math.ceil(tableWidthOnPage),
				(int) Math.ceil(headerHeightOnPage));

		g2.scale(scale, scale);
		getTableHeader().paint(g2);
		//paint header at top

		return Printable.PAGE_EXISTS;
	}

	/**
	 *  Method Description
	 */
	public void printPreview()
	{
		cellEditor = (RmaCellEditor)getCellEditor();
		if (cellEditor != null)
		   cellEditor.stopCellEditing();

		if(_printManager == null) _printManager = new TablePrintManager(this);

		_printManager.printPreview();
		return;

//		Thread runner =
//			new Thread()
//			{
//				public void run()
//				{
//					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//					Window window = SwingUtilities.windowForComponent(RmaJTable.this);
//					Frame frame = null;
//					if (window instanceof Frame)
//					{
//						frame = (Frame) window;
//					}
//					new PrintPreview(frame, RmaJTable.this, (getPrintTitle() == null ? "Print Preview" : getPrintTitle().getText().replace('\n', ' ')) + " preview");
//					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//				}
//			};
//		runner.start();
	}

	/**
	 *  Class Description
	 *
	 *@author     Mark Ackerman
	 *@created    October 10, 2001
	 */
	private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer
	{
		/**
		 *  Gets the TableCellRendererComponent attribute of the CheckBoxRenderer
		 *  object
		 *
		 *@param  table       Description
		 *@param  value       Description
		 *@param  isSelected  Description
		 *@param  hasFocus    Description
		 *@param  row         Description
		 *@param  column      Description
		 *@return             The TableCellRendererComponent value
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column)
		{
			setSelected((value != null && ((Boolean) value).booleanValue()));
			return this;
		}
	}

	/**
	 *@author     Mark Ackerman
	 *@created    October 10, 2001
	 */
	public class AlignTableCellRender extends RmaCellRenderer
	{
		//DefaultTableCellRenderer

		int _align;

		/**
		 *  Constructor for the AlignTableCellRender object
		 */
		public AlignTableCellRender() { }

		/**
		 *  Constructor for the AlignTableCellRender object
		 *
		 *@param  align  Description
		 */
		public AlignTableCellRender(int align)
		{
			_align = align;
		}

		/**
		 *  Gets the TableCellRendererComponent attribute of the AlignTableCellRender
		 *  object
		 *
		 *@param  table       Description
		 *@param  value       Description
		 *@param  isSelected  Description
		 *@param  hasFocus    Description
		 *@param  row         Description
		 *@param  column      Description
		 *@return             The TableCellRendererComponent value
		 */
		
		public java.awt.Component getTableCellRendererComponent(JTable table,
				java.lang.Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column)
		{
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setHorizontalAlignment(_align);

			return label;
		}
		
		public int getHorizontalAlignment()
		{
			return _align;
		}

	}

	/**
	 *  Renderer for currency cells with formatting.
	 *
	 *@author     Mark Ackerman
	 *@created    October 10, 2001
	 */
	public class CurrencyCellRenderer extends RmaCellRenderer
	{
		//DefaultTableCellRenderer

		int _align;
		java.text.DecimalFormat df = (java.text.DecimalFormat) java.text.DecimalFormat.getInstance();

		/**
		 *  Constructor for the DecimalCellRender object
		 */
		public CurrencyCellRenderer()
		{
			df.setDecimalSeparatorAlwaysShown(true);
			df.setGroupingUsed(true);
			setPrecision(2);
		}

		/**
		 *  Sets the Precision attribute of the DecimalCellRender object
		 *
		 *@param  precision  The new Precision value
		 */
		private void setPrecision(int precision)
		{
			df.setMaximumFractionDigits(precision);
			df.setMinimumFractionDigits(precision);
			if (precision == 0)
			{
				df.setDecimalSeparatorAlwaysShown(false);
			}
		}

		/**
		 *  Gets the TableCellRendererComponent attribute of the DecimalCellRender
		 *  object
		 *
		 *@param  table       Description
		 *@param  value       Description
		 *@param  isSelected  Description
		 *@param  hasFocus    Description
		 *@param  row         Description
		 *@param  column      Description
		 *@return             The TableCellRendererComponent value
		 */
		
		public java.awt.Component getTableCellRendererComponent(JTable table,
				java.lang.Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column)
		{
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Font f = label.getFont();
			label.setFont(new Font("monospaced", Font.PLAIN, f.getSize()));
			label.setHorizontalAlignment(JLabel.RIGHT);
			double d = 0;
			String s;
			boolean gotValue = false;

			try
			{
				d = Double.valueOf(label.getText()).doubleValue();
				gotValue = true;
			}
			catch (java.lang.NumberFormatException nfe)
			{
				gotValue = false;
			}

			if (gotValue)
			{
				if (d == 0)
				{
					StringBuffer strBuf = new StringBuffer();
					strBuf.append('0');
					int precision = df.getMaximumFractionDigits();
					if (precision > 0)
					{
						strBuf.append('.');
						for (int i = 0; i < precision; i++)
						{
							strBuf.append('0');
						}
					}
					label.setText(strBuf.toString());
				}
				else
				{
					s = df.format(d);
					s = "$".concat(s);
					label.setText(s);
				}
			}

			return label;
		}
	}
	/**
	 *@author     Mark Ackerman
	 *@created    October 10, 2001
	 */
	public class RmaSelectionListener implements ListSelectionListener
	{
		ListSelectionModel _lsModel;
		ListSelectionModel _colModel;
		ListSelectionModel _rowModel;
		int[] _selections;
		int _modelType;
		/**
		 *  Description
		 */
		public int COL_MODEL = 1;
		/**
		 *  Description
		 */
		public int ROW_MODEL = 2;

		/**
		 *  Constructor for the RmaSelectionListener object
		 *
		 *@param  lsm        Description
		 *@param  modelType  Description
		 */
		public RmaSelectionListener(ListSelectionModel lsm, int modelType)
		{
			if (modelType == COL_MODEL)
			{
				_colModel = lsm;
			}
			else
			{
				_rowModel = lsm;
			}
			_modelType = modelType;
		}

		/**
		 *  Method Description
		 *
		 *@param  lse  Description
		 */
		public void valueChanged(ListSelectionEvent lse)
		{
			_lsModel = (ListSelectionModel) lse.getSource();
			if (!lse.getValueIsAdjusting())
			{
				// skip all intermediate events

//				StringBuffer buf = new StringBuffer();
				_selections = getSelectedIndices(_lsModel.getMinSelectionIndex(),
						_lsModel.getMaxSelectionIndex());
			}
//for ( int i = 0; i< _selections.length; i++)
//    System.out.println((_modelType == ROW_MODEL?"Row ":"Col") + "Selected " + _selections[i]);

		}

		/**
		 *  Gets the Selections attribute of the RmaSelectionListener object
		 *
		 *@return    The Selections value
		 */
		public int[] getSelections()
		{
			return (_selections);
		}

		/**
		 *  Gets the SelectedIndices attribute of the RmaSelectionListener object
		 *
		 *@param  start  Description
		 *@param  stop   Description
		 *@return        The SelectedIndices value
		 */
		public int[] getSelectedIndices(int start, int stop)
		{
			if ((start == -1) || (stop == -1))
			{
				return new int[0];
			}
			int guesses[] = new int[stop - start + 1];
			int index = 0;
			for (int i = start; i <= stop; i++)
			{
				if (_lsModel.isSelectedIndex(i))
				{
					guesses[index++] = i;
				}
			}
			int realthing[] = new int[index];
			System.arraycopy(guesses, 0, realthing, 0, index);
			return realthing;
		}
	}

	/**
	 *  cell editor for check boxes. Not truely a one click editor but much better
	 *  than the default Check box editor doesn't work quite right
	 *
	 *@author     Mark Ackerman
	 *@created    October 10, 2001
	 *
	 *@deprecated use DefaultCellEditor instead
	 */
	@Deprecated
	public class OneClickCheckBoxEditor extends RmaCellEditor
	{
		/**
		 *  Constructor for the OneClickCheckBoxEditor object
		 *
		 *@param  x  Description
		 */
		public OneClickCheckBoxEditor(JCheckBox x)
		{
			super(x);
			setClickCountToStart(1/*_oneClickToStart*/);
		}

		/**
		 *  Method Description
		 *
		 *@param  anEvent  Description
		 *@return          Description
		 */
		
		public boolean shouldSelectCell(EventObject anEvent)
		{
			boolean f = super.shouldSelectCell(anEvent);

			if (editorComponent instanceof JCheckBox)
			{
				// editor is now realized and user already clicked
				// the checkbox to start editing, so fake another
				// click on the checkbox to toggle the state and
				// cause an action event to be fired that stops editing

				final JCheckBox check = (JCheckBox) editorComponent;

				SwingUtilities.invokeLater(
					new Runnable()
					{
						public void run()
						{
							System.out.println("do the click");
							//check.setSelected(!check.isSelected());
							check.doClick(0);
							//check.doClick(0);
							//removeEditor();
							check.revalidate();
						}
					});

				// dont change the selection state

				//return false;
				return f;
			}

			return f;
		}

	}
	// End of class OneClickCheckBoxEditor

	/**
	 *  Class Description
	 *
	 *@author     Mark Ackerman
	 *@created    October 10, 2001
	 */
	public class MinMaxEntry
	{
		/**
		 *  Description
		 */
		public int _col, _minRow, _maxRow;
		/**
		 *  Description
		 */
		public Object _min;
		/**
		 *  Description
		 */
		public Object _max;
		/**
		 *  Description
		 */
		public boolean _isCurrent = false;

		/**
		 *  Constructor for the MinMaxEntry object
		 *
		 *@param  col  Description
		 */
		public MinMaxEntry(int col)
		{
			_col = col;
		}

		/**
		 *  Method Description
		 *
		 *@param  obj  Description
		 *@return      Description
		 */
		
		public boolean equals(Object obj)
		{
			if (!(obj instanceof MinMaxEntry))
			{
				return false;
			}
			MinMaxEntry mme = (MinMaxEntry) obj;
			if (mme._col == _col)
			{
				return true;
			}
			return false;
		}

		/**
		 *  Method Description
		 *
		 *@param  obj  Description
		 *@param  row  Description
		 *@return      Description
		 */
		public boolean checkMin(Object obj, int row)
		{
			if (_min == null)
			{
				_min = obj;
				_minRow = row;
				return true;
			}

			int comp = checkValue(obj, _min);
			if (comp < 0 || (row == _minRow && comp > 0))
			{
				_min = obj;
				_minRow = row;
				return true;
			}
			return false;
		}

		/**
		 *  Method Description
		 *
		 *@param  obj  Description
		 *@param  row  Description
		 *@return      Description
		 */
		public boolean checkMax(Object obj, int row)
		{
			if (_max == null)
			{
				_max = obj;
				_maxRow = row;
				return true;
			}
//			int rows = getRowCount();
			int comp = checkValue(obj, _max);
			if (comp > 0 || (_maxRow == row && comp < 0))
			{
				_max = obj;
				_maxRow = row;
				return true;
			}
			return false;
		}

		/**
		 *  Method Description
		 *
		 *@param  newVal  Description
		 *@param  oldVal  Description
		 *@return         Description
		 */
		private int checkValue(Object newVal, Object oldVal)
		{
			Class type = getModel().getColumnClass(_col);
			if (newVal == null)
			{
				return 0;
			}
			String sNewVal = newVal.toString();
			String sOldVal = oldVal.toString();
			Class numClass = java.lang.Number.class;
			if (type == numClass || type.getSuperclass() == numClass)
			{
				double d1;
				double d2;
				try
				{
					d1 = new Double(sNewVal).doubleValue();
				}
				catch (NumberFormatException nfe)
				{
					d1 = 0.;
				}
				try
				{
					d2 = new Double(sOldVal).doubleValue();
				}
				catch (NumberFormatException nfe)
				{
					d2 = 0.;
				}

				if (d1 < d2)
				{
					return -1;
				}
				else if (d1 > d2)
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
			else if (type == getClassForName("java.util.Date"))
			{

				long n1 = new Date(sNewVal).getTime();
				long n2 = new Date(sOldVal).getTime();

				if (n1 < n2)
				{
					return -1;
				}
				else if (n1 > n2)
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
			else if (type == getClassForName("java.lang.String"))
			{
				int result = sNewVal.compareTo(sOldVal);

				if (result < 0)
				{
					return -1;
				}
				else if (result > 0)
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
			else if (type == getClassForName("java.lang.Boolean"))
			{
				boolean b1 = new Boolean(sNewVal).booleanValue();
				boolean b2 = new Boolean(sOldVal).booleanValue();

				if (b1 == b2)
				{
					return 0;
				}
				else if (b1)
				{
					// Define false < true
					return 1;
				}
				else
				{
					return -1;
				}
			}
			else
			{
				int result = sNewVal.compareTo(sOldVal);

				if (result < 0)
				{
					return -1;
				}
				else if (result > 0)
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
		}

	}

	/**
	 *  title for 1st page of printing
	 *
	 *@param  titleInfo  The new PrintTitle value
	 */
	public void setPrintTitle(PageText titleInfo)
	{
		_titleInfo = titleInfo;
		if(titleInfo != null) {
			this.getPrintManager().getPrintProperties().title = _titleInfo.getText();
		}

	}

	/**
	 *@param  otherInfo  The new PrintOtherInfo value
	 */
	public void setPrintOtherInfo(PageText otherInfo)
	{
		_otherInfo = otherInfo;
		if(otherInfo != null) {
			getPrintManager().getPrintProperties().subTitle = _otherInfo.getText();
		}
	}

	/**
	 *  return the title for 1st page of printing
	 *
	 *@return    The PrintTitle value
	 */
	public PageText getPrintTitle()
	{
		return _titleInfo;
	}

	/**
	 *  return other info for printing before table
	 *
	 *@return    The PrintOtherInfo value
	 */
	public PageText getPrintOtherInfo()
	{
		return _otherInfo;
	}

	/**
	 *  export the table to file in tab delimited format
	 */
	public void exportData()
	{		
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (_exportOptionsDialog == null)
						{
							_exportOptionsDialog = RmaModJTableExportDialog.getExportDialog(thisTable);
						}
						_exportOptionsDialog.setVisible(true);
						
						if (_exportOptionsDialog.isCanceled())
						{
							return;
						}
						
						Frame parent = JOptionPane.getFrameForComponent(thisTable);
						if (parent != null)
						{
							parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						}
						
						JFileChooser fileDialog = new JFileChooser();
						Preferences preferences = Preferences.userNodeForPackage(getClass());
						String exportDirectory = preferences.get("ExportDirectory", "");
						if (exportDirectory.length() == 0)
							exportDirectory = System.getProperty("user.dir");
						fileDialog.setCurrentDirectory(new File(exportDirectory));
						//fileDialog.setTitle("Export...");
						if (parent != null)
						{
							parent.setCursor(Cursor.getDefaultCursor());
						}

						fileDialog.showSaveDialog(null);
						File file = fileDialog.getSelectedFile();
						if (file == null)
						{
							return;
						}
						File f = fileDialog.getCurrentDirectory();
						preferences.put("ExportDirectory", f.getAbsolutePath());
						BufferedWriter writer;
						try
						{
							writer = new BufferedWriter(new FileWriter(file));
						}
						catch (java.io.IOException ioe)
						{
							System.out.println("exportData: failed to open file " + file.getPath() + " error " + ioe);
							JOptionPane.showMessageDialog(thisTable, "Failed to open export file " + file.getPath());
							return;
						}
						exportData(writer, _exportOptionsDialog.getExportOptions());

						try
						{
							writer.close();
						}
						catch (java.io.IOException ioe)
						{
						}
					}
				});

	}

	/**
	 *  export the table to file in delimiterChar delimited format . if
	 *  writeHeaders is true write the table headers. no rowheaders, no fixed width
	 *  columns, no quoted strings, no grid lines, no title
	 * @deprecated. Use exportData(BufferedWriter, TableExportOptions);
	 *
	 *@param  writer              Description
	 *@param  delimiterChar       Description
	 *@param  writeColumnHeaders  Description
	 */
	public void exportData(BufferedWriter writer, char delimiterChar, boolean writeColumnHeaders)
	{
		TableExportOptions teo = new TableExportOptions();
		teo.delimiter = delimiterChar;
		teo.columnHeader = writeColumnHeaders;
		exportData(writer, teo);
	}

	/**
	 *  export the table to file in user specified format format
	 *  Used mainly for scripting
	 */
	public void exportData(String fileName, TableExportOptions options)
	{
		File file = new File(fileName);
		if (file == null) {
			System.out.println("ExportData, Invalid file: " + fileName);
			return;
		}
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
		}
		catch (java.io.IOException ioe) {
			System.out.println("ExportData: failed to open file " + file.getPath() + " error " + ioe);
			return;
		}
		exportData(writer, options);

		try {
			writer.close();
		}
		catch (java.io.IOException ioe)	{}
	}

	/**
	 *  export the table to file in specified by writer with the export options specified by options.
	 *
	 *@param  writer   Description
	 *@param  options  Description
	 */
	public void exportData(BufferedWriter writer, TableExportOptions options)
	{
		if (writer == null)
		{
			return;
		}

		StringBuffer line = new StringBuffer(); /*= null;*/
		//String lineSep = System.getProperty("line.separator");
		Object obj;
		//String delimiterStr = new Character(options.delimiter).toString();


		Frame parent = JOptionPane.getFrameForComponent(this);

		if (parent != null)
		{
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}

		Vector dataSets = _tableModel.getDataSets();
		for (int ii=0; ii<dataSets.size(); ii++){
			SingleMonthlyTable dataSet = (SingleMonthlyTable)dataSets.get(ii);
		
			int row;

			int col;
			int numRows = dataSet.getRowCount();
			int numCols = dataSet.getColumnCount();
		
			JTableHeader header = dataSet.getTableHeader();
			//
			// print the optional title
			//
			if (options.title != null)
			{
				try
				{
					writer.write(options.title);
					writer.newLine();
				}
				catch (java.io.IOException ioe)
				{
					System.out.println("exportData: IOExpception writing title " + ioe);
				}
			}
			//
			// if fixed width columns find the widths
			//
			int[] colWidths = null;
			if ( options.fixedWidthCols )
			{
				colWidths = dataSet.getMaxColumnWidths();
			}
			//
			// print out the table headers
			//
			String horizontalGridline = null;
			if (header != null && options.columnHeader)
			{
				StringBuffer lastLine = new StringBuffer();
				Vector headerLines = new Vector();
				boolean first = true;
//				int lineIdx = 0;
				// GroupableTableHeaders are special and require
				// lots more effort

				// save "real" column headers. These get printed last
				int colNum = 0;
				TableColumn aColumn, bColumn;
				String label;
				int pId, colWidth = 0;
				Enumeration enumerator;
				// line = new StringBuffer();
				if (header instanceof gov.ca.dwr.hecdssvue.monthly.MonthlyTableModel.SingleMonthlyTable.GroupableTableHeader)
				{
					HashMap displayedHeaders = new HashMap();
					TableColumnModel tcm = header.getColumnModel();
					enumerator = tcm.getColumns();
					headerLines.addElement(line);

					// now for the ColumnGroups
					int cnt = 0;

					// now for the ColumnGroups
					int headerLine;
					Enumeration cGroups;
					gov.ca.dwr.hecdssvue.monthly.MonthlyTableModel.SingleMonthlyTable.ColumnGroup cGroup;
					StringBuffer ptr, tmp;
					Vector tblCols;
					while (enumerator.hasMoreElements())
					{
						aColumn = (TableColumn) enumerator.nextElement();
						cnt++;
						cGroups = ((gov.ca.dwr.hecdssvue.monthly.MonthlyTableModel.SingleMonthlyTable.GroupableTableHeader) header).getColumnGroups(aColumn);
						if (cGroups != null)
						{
							first = true;
							headerLine = 0;
							while (cGroups.hasMoreElements())
							{
								colWidth = 0;
								headerLine++;
								cGroup = (gov.ca.dwr.hecdssvue.monthly.MonthlyTableModel.SingleMonthlyTable.ColumnGroup) cGroups.nextElement();
								tblCols = cGroup.getTableColumns();
								if ( options.fixedWidthCols )
								{
									for (int i = 0; i< tcm.getColumnCount(); i++ )
									{
										bColumn = tcm.getColumn(i);
										for (int j = 0; j < tblCols.size(); j++ )
										{
											if ( tblCols.get(j) == bColumn )
											{
												colWidth += colWidths[i];
												if ( j == tblCols.size()-1 )
												{
													int width =cGroup.getHeaderValue().toString().length();
													if ( width > colWidth )
													{
														colWidths[i] += width - colWidths[i];
														colWidth = width;
													}
												}
												break;
											}
										}
									}
									colWidth += tblCols.size()-1; // for delimiter
									if ( options.quotedStrings )
									{
										colWidth += tblCols.size();
									}

								}
								if (!first && !displayedHeaders.containsKey(cGroup))
								{
									if (headerLines.size() < headerLine)
									{
										tmp = new StringBuffer();
										for (int i = 0; i < cnt - 1; i++)
										{
											//tmp.append(options.delimiter);
											if ( colWidths != null )
											{
												tmp.append(RMAIO.fillString(colWidths[i], ' '));
											}
											tmp.append(" ");
										}
										headerLines.addElement(tmp);
										ptr = tmp;
									}
									else
									{
										ptr = (StringBuffer) headerLines.elementAt(headerLine - 1);
										int tabs = RMAIO.getNumChars(ptr.toString(), options.delimiter);
										for (int i = tabs; i < cnt - 1; i++)
										{
											ptr.append(options.delimiter);
										}

									}
								}
								else
								{
									ptr = line;
								}

								if (displayedHeaders.containsKey(cGroup))
								{
									//ptr.append(options.delimiter);
									first = false;
									continue;
								}
								else
								{
									if ( options.quotedStrings )
									{
										ptr.append("\"");
									}
									displayedHeaders.put(cGroup, cGroup);
								}
								if ( colWidths != null )
									ptr.append(RMAIO.center(colWidth, cGroup.getHeaderValue().toString().replace('\n', ' ')));
								else
									ptr.append(cGroup.getHeaderValue().toString().replace('\n', ' '));

								if ( options.quotedStrings )
								{
									ptr.append("\"");
								}
								if (enumerator.hasMoreElements() )
								{
									ptr.append(options.delimiter);
								}
								first = false;
							}
						}
						else
						{ // real header
							if ( colWidths != null && colWidths.length < cnt-1 )
							{
								line.append(RMAIO.rightJustify2(colWidths[cnt-1]," ").toString());
							}
							if ( options.quotedStrings )
							{
								line.append("  ");
							}
							if ( options.gridLines )
							{
								line.append("|");
							}
							//line.append(options.delimiter);
							if ( options.fixedWidthCols )
							{
								line.append(" ");
							}
							else
							{
								line.append(options.delimiter);
							}
						}

					}
					// now print them
					for (int i = 0; i < headerLines.size()-1; i++)
					{
						try
						{
							//System.out.println(headerLines.elementAt(i).toString());
							writer.write(headerLines.elementAt(i).toString());
							writer.newLine();
						}
						catch (java.io.IOException ioe)
						{
							System.out.println("exportData: io error on export " + ioe);
						}
					}
				}

				enumerator = header.getColumnModel().getColumns();
				int length=0;
				while (enumerator.hasMoreElements())
				{
					aColumn = (TableColumn) enumerator.nextElement();

					label = aColumn.getHeaderValue().toString().replace('\n', ' ');

					if (getModel() instanceof RmaTableModelInterface)
					{
						pId = ((RmaTableModelInterface) getModel()).getColumnParameter(colNum);
						label = RmaJLabel.replaceUnitsTemplate(label, hec.data.Parameter.getUnitsStringForSystem(pId, getDisplayUnitSystem()));
					}
					length = label.length();
					if ( options.quotedStrings )
					{
						lastLine.append("\"");
					}
					if ( colWidths != null )
					{
						if ( length > colWidths[colNum] )
						{
							colWidths[colNum] = length;
						}
						lastLine.append(RMAIO.rightJustify2(colWidths[colNum], label).toString());
					}
					else
					{
						lastLine.append(label);
					}
					if ( options.quotedStrings )
					{
						lastLine.append("\"");
					}

					if (enumerator.hasMoreElements())
					{
						if ( options.gridLines )
						{
							lastLine.append("|");
						}
						else
						{
							lastLine.append(options.delimiter);
						}
					}
					colNum++;
				}
				// now print the real column headers as the last line
				try
				{
					//System.out.println(lastLine);
					writer.write(lastLine.toString());
					writer.newLine();
				}
				catch (java.io.IOException ioe)
				{
					System.out.println("exportData: io error on export " + ioe);
				}
				int sepWidth = lastLine.length();
				try
				{
					writer.write(RMAIO.fillString(sepWidth, '-').toString());
					writer.newLine();
				}
				catch ( java.io.IOException ioe )
				{
					System.out.println("exportData: io error on export " + ioe);
				}

			}
			// generate the horizontal grid line if needed
			if ( options.gridLines )
			{
				boolean noColWidths = colWidths == null;
				if ( noColWidths )
				{
					colWidths = dataSet.getMaxColumnWidths();
				}
				int gridWidth = 0;
				for (int i = 0; i < colWidths.length;i++ )
				{
					gridWidth+=colWidths[i];
				}
				if ( options.quotedStrings )
				{
					gridWidth += getColumnCount()*2;
				}
				gridWidth += getColumnCount()-1;
				if ( noColWidths ) colWidths = null;
				horizontalGridline = RMAIO.fillString(gridWidth, '-').toString();
			}
			//
			// now write the data out
			//
			String value;
			TableColumn tc;
			TableCellRenderer renderer;
			Component c;
			line.setLength(0);
//			int rowHeaderRow;
			int rowHeaderCol;
			for (row = 0; row < numRows; row++)
			{
				// print row headers if asked for and we have them
				if (options.rowHeader && _rowheaderTbl != null)
				{
					for (rowHeaderCol = 0; rowHeaderCol < _rowheaderTbl.getColumnCount(); rowHeaderCol++)
					{
						obj = _rowheaderTbl.getValueAt(row, rowHeaderCol);
						if (obj == null || obj.toString().length() < 1)
						{
							line.append(" ");
						}
						else
						{
							line.append(obj.toString());
						}
						if ( options.gridLines )
						{
							line.append("|");
						}
						else
						{
							line.append(options.delimiter);
						}
					}
				}

				for (col = 0; col < numCols; col++)
				{
					obj = dataSet.getValueAt(row, col);
					if (obj == null || obj.toString().length() < 1)
					{
						if (options.quotedStrings)
						{
							line.append("\"");
						}
						if ( colWidths != null )
						{
							line.append(RMAIO.rightJustify2(colWidths[col]," ").toString());
						}
						else
						{
							line.append(" ");
						}
						if (options.quotedStrings)
						{
							line.append("\"");
						}
					}
					else
					{
						value = obj.toString();
						//need to render the parameter values
						tc = dataSet.getColumnModel().getColumn(col);
						if (tc != null)
						{
							renderer = tc.getCellRenderer();
							if (renderer == null)
							{
								renderer = getDefaultRenderer(getColumnClass(col));
							}
							c = renderer.getTableCellRendererComponent(dataSet, obj, false, false, row, col);
							//Component c = tc.getCellRenderer().getTableCellRendererComponent(this,obj,false,false,row,col);
							if (c instanceof JLabel)
							{
								value = ((JLabel) c).getText();
							}
							else
							{
								value = obj.toString();
							}
						}
						if (options.quotedStrings)
						{
							line.append("\"");
						}
						if (value.indexOf('\n') > -1 )
						{ // replace newline characters with spaces
							value = value.replace('\n', ' ');
						}
						if ( colWidths != null )
						{
							String s= getJustifiedValue(value, col, colWidths[col]);
							line.append(s);
						}
						else
						{
							line.append(value);
						}
						if (options.quotedStrings)
						{
							line.append("\"");
						}
					}
					if (col != numCols - 1)
					{
						if ( options.gridLines )
						{
							line.append("|");
						}
						else
						{
							line.append(options.delimiter);
						}
					}
				}
				//line.append(lineSep);
				// now write the row out, the optional gridline and a newline
				try
				{
					//System.out.println(line.toString());
					writer.write(line.toString());
					if ( horizontalGridline  != null )
					{
						writer.newLine();
						writer.write(horizontalGridline);
					}
					writer.newLine();
					writer.flush();
				}
				catch (java.io.IOException ioe)
				{
					System.out.println("exportData: io error on export " + ioe);
				}
				line.setLength(0);
			}
		
		}
		
		if (parent != null)
		{
			parent.setCursor(Cursor.getDefaultCursor());
		}
	}

	public String getExportString(TableExportOptions options) {

		StringWriter strWriter = new StringWriter();
		BufferedWriter bufWriter = new BufferedWriter(strWriter);
		exportData(bufWriter, options);
		try {
			strWriter.close();
		}
		catch (IOException e) {
		}
		return strWriter.toString();
	}

	public void exportAsXML(String fileName) {

		exportAsXML(fileName, null, "\t");
	}

	public void exportAsXML(String fileName, String title, String indent) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			exportAsXML(writer, title, indent);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void exportAsXML(BufferedWriter writer) {

		exportAsXML(writer, null, "\t");
	}

	public void exportAsXML0(BufferedWriter writer, String title, String indent) {

		String delimiter = "\0";
		TableExportOptions expOpt = new TableExportOptions();
		expOpt.delimiter = delimiter.charAt(0);
		expOpt.quotedStrings = false;
		expOpt.title = null;
		expOpt.fixedWidthCols = false;
		expOpt.columnHeader = true;
		expOpt.rowHeader = false;
		expOpt.gridLines = false;

		try {
			//-------------------------------------------------------------------------//
			// This is necessary because exportData() uses BufferedWriter.newLine().   //
			//                                                                         //
			// We could get newLine from line.separator system property, but do this   //
			// instead, just in case the BufferedWriter.newLine() gets its information //
			// from somewhere else in the future.					   //
			//-------------------------------------------------------------------------//
			StringWriter strWriter = new StringWriter();
			BufferedWriter bufWriter = new BufferedWriter(strWriter);
			bufWriter.newLine();
			bufWriter.flush();
			String newLine = strWriter.toString();

			String[] lines = getExportString(expOpt).split(newLine);
			String[] fields;

			writer.write("<?xml version = \"1.0\" encoding = \"UTF-8\"?>");
			writer.newLine();
			writer.write("<Table>");
			writer.newLine();
			if (title != null && title.length() > 0) {
				writer.write(indent + "<Title>" + title + "</Title>");
				writer.newLine();
			}
			//---------//
			// caption //
			//---------//
			writer.write(indent + "<CaptionRow>");
			writer.newLine();
			fields = lines[0].split(delimiter);
			for (int i = 0; i < fields.length; ++i) {
				writer.write(indent + indent + "<CaptionItem>" + fields[i].trim() + "</CaptionItem>");
				writer.newLine();
			}
			writer.write(indent + "</CaptionRow>");
			writer.newLine();
			//--------//
			// values //
			//--------//
			for (int i = 2 /* skip the delimiter line */; i < lines.length; ++i) {
				writer.write(indent + "<ValuesRow>");
				writer.newLine();
				fields = lines[i].split(delimiter);
				for (int j = 0; j < fields.length; ++j) {
					writer.write(indent + indent + "<ValuesItem>" + fields[j].trim() + "</ValuesItem>");
					writer.newLine();
				}
				writer.write(indent + "</ValuesRow>");
				writer.newLine();
			}
			writer.write("</Table>");
			writer.newLine();
			writer.flush();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public String getXMLExportString() {

		return getXMLExportString(null, "\t");
	}

	public String getXMLExportString(String title, String indent) {

		StringWriter strWriter = new StringWriter();
		BufferedWriter bufWriter = new BufferedWriter(strWriter);
		exportAsXML(bufWriter, title, indent);
		try {
			strWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return strWriter.toString();
	}

	public void exportAsHTML(String fileName) {

		exportAsHTML(fileName, null, "\t");
	}

	public void exportAsHTML(String fileName, String title, String indent) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			exportAsHTML(writer, title, indent);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void exportAsHTML(BufferedWriter writer) {

		exportAsHTML(writer, null, "\t");
	}

	public void exportAsHTML0(BufferedWriter writer, String title, String indent) {

		try {
			//-------------------------------------------------------------------------//
			// This is necessary because exportData() uses BufferedWriter.newLine().   //
			//                                                                         //
			// We could get newLine from line.separator system property, but do this   //
			// instead, just in case the BufferedWriter.newLine() gets its information //
			// from somewhere else in the future.					   //
			//-------------------------------------------------------------------------//
			StringWriter strWriter = new StringWriter();
			BufferedWriter bufWriter = new BufferedWriter(strWriter);
			bufWriter.newLine();
			bufWriter.flush();
			String newLine = strWriter.toString();

			String htmlString = getXMLExportString(title, indent);
			htmlString = htmlString.replaceAll("<Table>", "<TABLE BORDER=1 RULES=\"ALL\">");
			htmlString = htmlString.replaceAll("</Table>","</TABLE>");
			htmlString = htmlString.replaceAll("Title>", "CAPTION>");
			htmlString = htmlString.replaceAll("CaptionItem>", "TH ALIGN=\"CENTER\">");
			htmlString = htmlString.replaceAll("UnitsItem>", "TH ALIGN=\"CENTER\">");
			htmlString = htmlString.replaceAll("DataTypeItem>", "TH ALIGN=\"CENTER\">");
			htmlString = htmlString.replaceAll("ValuesItem>", "TD>");
			htmlString = htmlString.replaceAll("<CaptionRow>", "<TR ALIGN=\"RIGHT\">");
			htmlString = htmlString.replaceAll("<ValuesRow>", "<TR ALIGN=\"RIGHT\">");
			htmlString = htmlString.replaceAll("</CaptionRow>", "</TR>");
			htmlString = htmlString.replaceAll("</UnitsRow>", "</TR>");
			htmlString = htmlString.replaceAll("</DataTypeRow>", "</TR>");
			htmlString = htmlString.replaceAll("</ValuesRow>", "</TR>");

			String[] lines = htmlString.split(newLine);
			for (int i = 1; i < lines.length; ++i) {
				writer.write(lines[i]);
				writer.newLine();
			}
			writer.flush();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public String getHTMLExportString() {

		return getHTMLExportString(null, "\t");
	}

	public String getHTMLExportString(String title, String indent) {

		StringWriter strWriter = new StringWriter();
		BufferedWriter bufWriter = new BufferedWriter(strWriter);
		exportAsHTML(bufWriter, title, indent);
		try {
			strWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return strWriter.toString();
	}

	String getJustifiedValue(String value, int col, int colWidth)
	{
		int alignment = getHorizontalAlignment(col);
		switch ( alignment )
		{
			case SwingConstants.RIGHT:
				return RMAIO.leftJustify(colWidth, value).toString();
			case SwingConstants.CENTER:
				return RMAIO.center(colWidth, value).toString();
			default:
				return RMAIO.rightJustify2(colWidth, value).toString();
		}
	}
	int[] getMaxColumnWidths()
	{
		int colCount = getColumnCount();
		int[] widths = new int[colCount];
		for(int i = 0; i < colCount; i++ )
		{
			widths[i] = getMaxColumnCharacterWidth(i);
		}
		return widths;
	}

	int getMaxColumnCharacterWidth(int col)
	{
		int rowCnt = getRowCount();
		Object obj;
		String value;
		TableColumn tc;
		TableCellRenderer renderer;
		Component c;
		// initial width is the column width
		int maxWidth = getColumnModel().getColumn(col).getHeaderValue().toString().length();
		int width;
		for (int row = 0; row < rowCnt; row++ )
		{
			obj = getValueAt(row, col);
			if (obj == null || obj.toString().length() < 1)
			{
				continue;
			}
			else
			{
				value = obj.toString();
				//need to render the parameter values
				tc = getColumnModel().getColumn(col);
				if (tc != null)
				{
					renderer = tc.getCellRenderer();
					if (renderer == null)
					{
						renderer = getDefaultRenderer(getColumnClass(col));
					}
					c = renderer.getTableCellRendererComponent(this, obj, false, false, row, col);

					if (c instanceof JLabel)
					{
						value = ((JLabel) c).getText();
					}
					else
					{
						value = obj.toString();
					}
				}
				if ( value == null ) continue;
				width = value.length();
				if ( width > maxWidth )
				{
					maxWidth = width;
				}
			}
		}
		return maxWidth;
	}
	/**
	 *  Called to check to see if this table has valid data. This method should be
	 *  overridden to provide a more generalized implemenation.
	 *
	 *@param  showError  Description
	 *@return            The Valid value
	 */
	public boolean isValid(boolean showError)
	{
		return true;
	}
	public void setPasteAddsRows(boolean addRows)
	{
		_pasteAddsRows = addRows;
	}
	public boolean getPasteAddsRows()
	{
		return _pasteAddsRows;
	}
	/*
	 *
	 */
	/**
	 *  The main program for the RmaJTable class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		RmaJFrame frame = new RmaJFrame("RmaJTable Example")
		{
			public void setModified(boolean modified)
			{
				if ( modified )
				{
					setTitle("RmaJTable Example*");
				}
				else
				{
					setTitle("RmaJTable Example");
				}
				super.setModified(modified);
			}
		};
		Object[][] data = {
//				{"Mary\nMay\nBeth", "Campione",
//				"Snowboarding", new Integer(5), new Boolean(false), "4.50"},
//				{"Mary\nMay\nBeth", "Campione",
//				"Snowboarding", new Integer(5), new Boolean(false), "4.50"},
//				{"Alison", "Huml",
//				"Rowing", new Integer(3), new Boolean(true), "5.50"},
//				{"Mary\nMay\nBeth", "Campione",
//				"Snowboarding", new Integer(5), new Boolean(false), "4.50"},
//				{"Kathy", "Walrath",
//				"Chasing toddlers", new Integer(2), new Boolean(false),"12.42"},
//				{"Mark", "Andrews",
//				"Speed reading", new Integer(20), new Boolean(true),"1002.65"},
//				{"Mary\nMay\nBeth", "Campione",
//				"Snowboarding", new Integer(5), new Boolean(false), "4.50"},
//				{"Angela", "Lih",
//				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
//                {"Angela", "Lih",
//				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
//                {"Alison", "Huml",
//				"Rowing", new Integer(3), new Boolean(true), "5.50"},
//				{"Kathy", "Walrath",
//				"Chasing toddlers", new Integer(2), new Boolean(false),"12.42"},
//                {"Alison", "Huml",
//				"Rowing", new Integer(3), new Boolean(true), "5.50"},
				{"Kathy", "Walrath",
				"Chasing toddlers", new Integer(2), new Boolean(false),"12.42"},
				{"Alison", "Huml",
				"Rowing", new Integer(3), new Boolean(true), "5.50"},
				{"Kathy", "Walrath",
				"Chasing toddlers", new Integer(2), new Boolean(false),"12.42"},

				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"},
				{"Angela", "Lih",
				"Teaching high school", new Integer(4), new Boolean(false),"112.42"}
				};

		String[] columnNames = {"First\nName",
				"Last\nName",
				"Sport",
				"# of Years",
				"Vegetarian",
				"Hourly\nRate"};
		String[] columnNames2 = {"Name\nFirst",
				"Name\nLast",
				"Sport",
				"# of Years",
				"Vegetarian",
				"Hourly\nRate"};
//		boolean[] enabled = {true, true, true, true, true, true};
		final MonthlyTable tbl = new MonthlyTable(frame, data, columnNames);
		tbl.setName("List of\nnames\nand\ntheir info");
		//tbl.setEnabled(false);
		//RmaJTable tbl = new RmaJTable(frame);
		//RmaJTableSorter sorter = new RmaJTableSorter();
		//sorter.setModel(new RmaTableModel(columnNames, data, enabled));
		//tbl.setModel(sorter);
		//sorter.addMouseListenerToHeaderInTable(tbl);
		tbl.setRowHeight(20);
		tbl.setCellRenderer();
		tbl.setMlHeaderRenderer();
//		tbl.setTextAreaCellEditor(0);
		tbl.setCurrencyCellEditor(5);
		tbl.setRowHeaderEnabled(true);
		//tbl.setBackground(Color.lightGray);
		//tbl.setCells(data);
		//tbl.setRowHeaderEnabled(true);
		//tbl.setAutoRowHeaders(true, 5);
		tbl.setCellForeground(1, 2, Color.red);
		tbl.setCellBackground(2, 1, Color.blue);
		tbl.setRowBackground(2, Color.green);
		tbl.setRowForeground(1, Color.yellow);
		tbl.setCellEnabled(false, 4, 2);
		tbl.setPasteAddsRows(true);
		tbl.setCheckBoxCellEditor(4);
		//((RmaTableModelInterface)tbl.getModel()).setColumnClass(4,Boolean.class);
		tbl.setPasteForeground(Color.red);
		tbl.setCellSelectionEnabled(false);
		tbl.setRowSelectionAllowed(true);
		tbl.setColumnSelectionAllowed(true);
		tbl.setRowEnabled(false, 2);
		Vector v = new Vector();
		for (int i = 0; i < data.length; i++)
		{
			v.addElement(data[i][2]);
		}
		tbl.setComboBoxEditor(2, v);
		RmaJDecimalField df = tbl.setDoubleCellEditor(3);
		df.setMinValue(0);
		df.setMaxValue(20);
		tbl.setPrecision(2);
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());

		JButton h = new JButton("Headers");
		h.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		h.setFont(tbl.getFont());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		p.add(h, gbc);

		h = new JButton(" Row  ");
		h.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		h.setFont(tbl.getFont());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		p.add(h, gbc);

		h = new JButton("  Row ");
		h.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		h.setFont(tbl.getFont());
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		p.add(h, gbc);

//		 tbl.setViewportRowHeader(new RmaTableModel(new String[]{" ", " "},
//		 new String[][]{{"row","1"} , {"row","2"}, {"row", "3"}, {"row", "4"},
//		  {"row", "5"}},  new boolean[]{false, false}), "Header", p, 75);

		//JViewport jv2 = new JViewport();
		//JTable rh = new JTable( );
		//rh.setBackground(Color.lightGray);
		//rh.setReorderingAllowed(false);
		//rh.setCellSelectionEnabled(false);
		//rh.setRowSelectionAllowed(false);
		//rh.setColumnSelectionAllowed(false);

		//rh.setEnabled(false);
		//jv2.setEnabled(false);
		//jv2.setPreferredSize(new Dimension(75, tbl.getPreferredSize().height ));
		//jv2.setView(rh);
		//jv2.setBackground(Color.lightGray);
		//tbl.getScrollPane().setRowHeader(jv2);
		//JButton jb = new JButton("");
		//jb.setEnabled(false);
		//jb.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		//tbl.getScrollPane().setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, jb );
		//tbl.getScrollPane().setBackground(Color.lightGray);


		frame.getContentPane().add(tbl.getScrollPane(), BorderLayout.NORTH);

		frame.setSize(500, 300);
		tbl.setAutoResizeMode(AUTO_RESIZE_OFF);

		JTable tbl2 = new javax.swing.JTable(data, columnNames);
		tbl2.setCellSelectionEnabled(true);
		tbl2.setRowSelectionAllowed(false);
		tbl2.setColumnSelectionAllowed(false);
		tbl2.setBackground(Color.lightGray);

		JScrollPane sbar = new JScrollPane(tbl2);
		frame.getContentPane().add(sbar, BorderLayout.SOUTH);
		tbl.linearFill(new CellLocation(0, 3, ""), new CellLocation(4, 3, ""));
		frame.setModified(false);
		frame.setVisible(true);
		//tbl.setNumRows(2);
		//tbl.displayNumCol(3);
		//tbl.displayNumCol(4);
		tbl.setColumnLabels(columnNames2);
		//tbl.setColumnEnabled(false, 1);
		tbl.exportAsXML("table.export");
	}


//    public void createDefaultColumnsFromModel() {
//        TableModel m = getModel();
//        if (m != null) {
//            // Remove any current columns
//            TableColumnModel cm = getColumnModel();
//            cm.removeColumnModelListener(this);
//            while (cm.getColumnCount() > 0)
//                cm.removeColumn(cm.getColumn(0));
//
//            // Create new columns from the data model info
//            for (int i = 0; i < m.getColumnCount(); i++) {
//                TableColumn newColumn = new TableColumn(i);
//                if(m.getColumnClass(i) != null) {
//                    TableCellRenderer tcr = getDefaultRenderer(m.getColumnClass(i));
//                    if(tcr != null) newColumn.setCellRenderer(tcr);
//                    TableCellEditor tec = getDefaultEditor(m.getColumnClass(i));
//                    if(tec != null) newColumn.setCellEditor(tec);
//                }
//                addColumn(newColumn);
//
//            }
//            cm.addColumnModelListener(this);
//        }
//    }

	// */
		protected static class BooleanRenderer extends JCheckBox implements TableCellRenderer
		{
			private boolean _useSelectionBackground;

			public BooleanRenderer(boolean useSelectionBackground)
			{
				super();
				_useSelectionBackground = useSelectionBackground;
				BooleanRenderer.this.setHorizontalAlignment(JLabel.CENTER);
			}

			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column)
			{
				if (isSelected)
				{
						setForeground(table.getSelectionForeground());
					if ( _useSelectionBackground)
					{
						super.setBackground(table.getSelectionBackground());
					}
				}
				else
				{
					if ( table.isCellEditable( row, column ) )
					{
						Color c = ((MonthlyTable)table).getCellBackground(row,column);
						setBackground(c);//table.getBackground());
					}
					else
					{
						setBackground( ((MonthlyTable)table).getDisabledBackground(row, column));
						//UIManager.getDefaults().getColor( "TextField.disabledBackground" ) );
					}
					setForeground(table.getForeground());
				 }
				setSelected((value != null && "true".equalsIgnoreCase(value.toString())));
				return this;
			}
		}
		static class ButtonRenderer extends JButton implements TableCellRenderer
		{
			public ButtonRenderer()
			{
				super();
			}

			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column)
			{
				if (isSelected)
				{
//					setForeground(table.getSelectionForeground());
//					super.setBackground(table.getSelectionBackground());
				}
				else
				{
					if ( table.isCellEditable( row, column ) )
					{
						ButtonRenderer.this.setEnabled(true);
//						setBackground(table.getBackground());
					}
					else
					{
						ButtonRenderer.this.setEnabled(false);
//						setBackground( UIManager.getDefaults().getColor( "TextField.disabledBackground" ) );
					}
					setForeground(table.getForeground());
				 }
				setText(value != null ? value.toString() : "");
				return this;
			}
		}

	/**
	 * @return Returns the useDefaultPrintHeader.
	 */
	public boolean useDefaultPrintHeader()
	{
		return _useDefaultPrintHeader;
	}

	/**
	 * @param useDefaultPrintHeader The useDefaultPrintHeader to set.
	 */
	public void setUseDefaultPrintHeader(boolean useDefaultPrintHeader)
	{
		_useDefaultPrintHeader = useDefaultPrintHeader;
	}

	/**
	 * return the width that the column should be based on the data
	 * in the column
	 * @param colNum the column index. zero based.
	 * @return the width of the column
	 */
	public int getColumnWidthFromData(int colNum)
	{
		int rowCnt = getRowCount();
		int width = 0;
		if ( colNum >= getColumnCount())
		{
			return width;
		}
		Object obj;
		String str;
		FontMetrics fm = getFontMetrics(getFont());

		for (int row = 0;row < rowCnt;row++)
		{
			obj = getValueAt(row, colNum);
			if (obj == null )
			{
				continue;
			}
			str = obj.toString();
			if ( str == null )
			{
				continue;
			}
			if ( str.toLowerCase().contains("<br>"))
			{
				str = findLongestHtmlString(str);
			}
			else if ( str.toLowerCase().contains("\n"))
			{
				str = findLongestNewlineString(str);
			}
			width = Math.max(width, fm.stringWidth(str));
		}
		width = Math.max( width, getColumnWidth(colNum));
		return width+getColumnModel().getColumnMargin();
	}


	/**
	 * @param str
	 * @return
	 */
	private String findLongestNewlineString(String str)
	{
		if ( str == null )
		{
			return null;
		}
		String[] ss = str.split("\n");
		String longest = "";
		for (int i = 0;i < ss.length; i++ )
		{
			if ( ss[i].length() > longest.length() )
			{
				longest = ss[i];
			}
		}
		return longest;
	}

	/**
	 * @param str
	 * @return
	 */
	private String findLongestHtmlString(String str)
	{
		if ( str == null )
		{
			return null;
		}
		String longest = "";
		String s = str.toLowerCase();
		int idx = s.indexOf("<br>");
		if ( s.startsWith("<html>"))
		{
			s = s.substring(6);
		}
		if ( s.endsWith("<html>"))
		{
			s = s.substring(0,s.length()-6);
		}
		String[] ss = s.split("<br>");
		if ( ss == null )
		{
			return null;
		}
		for (int i = 0;i < ss.length; i++ )
		{
			if ( ss[i].length() > longest.length() )
			{
				longest = ss[i];
			}
		}
		return longest;
	}

	public Point getPopupPoint()
	{
		return _popupPoint;
	}
	public void setAllowsFontResizing(boolean allowResizing)
	{
		if ( allowResizing && getActionMap().get("increaseFont")==null)
		{
			_origRowHeight = getRowHeight();
			Action increaseFontAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					Font f = getFont();
					Font newFont = new Font(f.getName(), f.getStyle(), f.getSize()+1);
					setFont(newFont);
					updateRowHeight();
					revalidate();
				}
			};
			Action decreaseFontAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					Font f = getFont();
					Font newFont = new Font(f.getName(), f.getStyle(), f.getSize()-1);
					setFont(newFont);
					updateRowHeight();
					revalidate();
				}
			};
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK),
				"increaseFont");
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK),
				"decreaseFont");
			getActionMap().put("increaseFont",increaseFontAction);
			getActionMap().put("decreaseFont",decreaseFontAction);
		}
		else
		{
			getActionMap().remove("increaseFont");
			getActionMap().remove("decreaseFont");
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK));
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(
				KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK));
		}
	}




	private void updateRowHeight()
	{
		int rowHeight = getRowHeight();
		FontMetrics f= getFontMetrics(getFont());
		if ( f.getHeight() > rowHeight )
		{
			while( f.getHeight() > rowHeight )
			{
				rowHeight+=2;
			}
			setRowHeight(rowHeight);
		}
		else if ( f.getHeight() < rowHeight )
		{

			while( f.getHeight() < rowHeight && rowHeight >= _origRowHeight)
			{
				rowHeight-=2;
			}
			setRowHeight(rowHeight);
		}
	}
	
	
	public int setData(java.util.List dataSets, int firstMonth,
			boolean showCommas, boolean showTotalColumn, int decimalPlaces) {
		if (dataSets.size() == 0)
			return -1;
		Object dataSet = dataSets.get(0);
		if (dataSet instanceof TimeSeriesContainer) {
			_tableModel = new MonthlyTableModel(); // CB may need if null later
			// CB added hasTotalColumn code (must consider each data set
			// separately)
			boolean hasTotalColumn;
			if (((TimeSeriesContainer) dataSet).units.trim().equalsIgnoreCase(
					"TAF")
					|| (showTotalColumn && ((TimeSeriesContainer) dataSet).units
							.trim().equalsIgnoreCase("CFS")))
				hasTotalColumn = true;
			else
				hasTotalColumn = false;
			((MonthlyTableModel) _tableModel).setData(dataSets, firstMonth,
					showCommas, hasTotalColumn, decimalPlaces);
		} else {
			if (dataSet instanceof TimeSeriesDataSet) {
				Vector v = new Vector();
				for (int i = 0; i < dataSets.size(); i++) {
					Object obj = dataSets.get(i);
					if (obj instanceof TimeSeriesDataSet) {
						TimeSeriesContainer tsc = ((TimeSeriesDataSet) obj)._timeSeries;
						v.addElement(tsc);
					}
				}
				setData(v, firstMonth, showCommas, showTotalColumn,
						decimalPlaces);
			} else {
				System.out.println("Data type not recognized: ");
				return -1;
			}
		}
		setupModel();
		return 0;
	}

	public void setupModel() {
		if (getDataSets() == null)
			return;
		if (_tableModel != null)
			setModel(_tableModel);
		Enumeration dataSets = getDataSets().elements();
		int i = 0;
		while (dataSets.hasMoreElements()) {
			MonthlyTableModel.SingleMonthlyTable table = (MonthlyTableModel.SingleMonthlyTable) dataSets
					.nextElement();
			// The next line is necessary, but if you query getRowHeight() after
			// it, it says 16. Why?
			setRowHeight(i, table.getRowCount() * table.getRowHeight()
					+ table.getTableHeader().getPreferredSize().height + 3); // the
																				// 3
																				// is
																				// correct,
																				// but
																				// why?
			++i;
		}
	}

	public int setData(TimeSeriesContainer timeSeriesContainer, int firstMonth,
			boolean showCommas, boolean showTotalColumn, int decimalPlaces) {
		Vector t = new Vector(1);
		t.add(timeSeriesContainer);
		setData(t, firstMonth, showCommas, showTotalColumn, decimalPlaces);
		setupModel();
		return 0;
	}

	public void setShowCommas(boolean showCommas) {
		if (_tableModel != null)
			_tableModel.setShowCommas(showCommas);
	}

	public void setShowTotalColumn(boolean showTotalColumn) {
		if (_tableModel != null) {
			_tableModel.setShowTotalColumn(showTotalColumn);
		}
	}

	public void setPrecision(int precision) {
		if (_tableModel != null)
			_tableModel.setPrecision(precision);
	}

	public void setNumberHeaderRows(int number) {
		if (_tableModel != null)
			_tableModel.setNumberHeaderRows(number);
	}

	public String getTableType() {
		if (_tableModel != null && _tableModel instanceof MonthlyTableModel)
			return "monthly";
		else
			return "";
	}

	public Vector getDataSets() {
		stopEditing();
		if (_tableModel != null && _tableModel instanceof MonthlyTableModel)
			return ((MonthlyTableModel) _tableModel).getDataSets();
		else
			return null;
	}

	public int getPrecision() {
		if (_tableModel != null)
			return _tableModel.getPrecision();
		else
			return -1;
	}

	public int getRowCount() {
		if (_tableModel == null)
			return 0;
		else
			return _tableModel.getRowCount();
	}

	public int getColumnCount() {
		if (_tableModel == null)
			return 0;
		else
			return _tableModel.getColumnCount();
	}

	public int getNumberHeaderRows() {
		if (_tableModel != null)
			return _tableModel.getNumberHeaderRows();
		else
			return 0;
	}

	public String getToolTipText(MouseEvent event) {
		java.awt.Point p = event.getPoint();
		int column = columnAtPoint(p);
		int row = rowAtPoint(p);
		if (row > 0 && column > 1)
			return ((MonthlyTableModel) _tableModel)
					.getToolTipText(row, column);
		else
			return getToolTipText0(event);
	}

	public String getSelectedDataString() {
		stopEditing();
		ListSelectionModel listModel = getSelectionModel();
		int minRow;
		int maxRow;
		int minColumn;
		int maxColumn;
		if (listModel.isSelectionEmpty()) {
			minRow = 0;
			maxRow = getRowCount() - 1;
			minColumn = 0;
			maxColumn = getColumnCount() - 1;
		} else {
			minRow = listModel.getMinSelectionIndex();
			maxRow = listModel.getMaxSelectionIndex();
			int cols[] = getColumnModel().getSelectedColumns();
			int len = cols.length;
			if (len == 0)
				return "";
			minColumn = cols[0];
			maxColumn = cols[len - 1];
		}
		return _tableModel.getSelectedDataString(minRow, maxRow, minColumn,
				maxColumn);
	}

	public int addData(TimeSeriesContainer timeSeriesContainer, int firstMonth,
			boolean showCommas, boolean showTotalColumn, int decimalPlaces) {
		int status = -1;
		if (_tableModel == null)
			_tableModel = new MonthlyTableModel(); // CB TO DO: need??????
		if (_tableModel instanceof MonthlyTableModel) {
			Vector v = new Vector();
			v.add(timeSeriesContainer);
			status = _tableModel.addData(v, firstMonth, showCommas,
					showTotalColumn, decimalPlaces);
		}
		return status;
	}

	public void export(String filename) {
		exportData(filename, new TableExportOptions());
	}

	public void export(String filename, TableExportOptions exportOptions) {
		exportData(filename, exportOptions);
	}

	public void stopEditing() {
		cellEditor = (RmaCellEditor) getCellEditor();
		if (cellEditor != null)
			cellEditor.stopCellEditing();
	}

	public void plot(JFrame parent) {
		stopEditing();
		if (_tableModel != null)
			_tableModel.plot(parent);
	}

	public boolean print() {
		if (getModel() instanceof MonthlyTableModel) {
			for (int i = 0; i < ((MonthlyTableModel) getModel()).getRowCount(); ++i) {
				if (((MonthlyTableModel) getModel()).getValueAt(i, 0) instanceof MonthlyTableModel.SingleMonthlyTable) {
					try {
						((MonthlyTableModel.SingleMonthlyTable) ((MonthlyTableModel) getModel())
								.getValueAt(i, 0)).print(PrintMode.FIT_WIDTH,
								null, null, false, null, false);
					} catch (PrinterException pe) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/*
	 * public boolean printPreview() { if (getModel() instanceof
	 * MonthlyTableModel) { for (int i = 0; i <
	 * ((MonthlyTableModel)getModel()).getRowCount(); ++i) { if
	 * (((MonthlyTableModel)getModel()).getValueAt(i, 0) instanceof
	 * MonthlyTableModel.SingleMonthlyTable) { try {
	 * ((MonthlyTableModel.SingleMonthlyTable)((MonthlyTableModel)getModel())
	 * .getValueAt(i, 0)).print(); } catch (PrinterException pe) { return false;
	 * } } } return true; } return false; }
	 */
	public String allDataString() {
		if (_tableModel == null)
			return "";
		else
			return _tableModel.allDataString();
	}

	public void exportAsXML(BufferedWriter writer, String title, String indent) {
		try {
			StringWriter strWriter = new StringWriter();
			BufferedWriter bufWriter = new BufferedWriter(strWriter);
			bufWriter.newLine();
			bufWriter.flush();
			String newLine = strWriter.toString();
			bufWriter.close();
			strWriter = new StringWriter();
			bufWriter = new BufferedWriter(strWriter);
			exportAsXML0(bufWriter, title, indent);
			String baseString = strWriter.toString();
			String lines[] = baseString.split(newLine);
			boolean labelsRow = false;
			boolean unitsRow = false;
			boolean typeRow = false;
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].trim().startsWith("<ValuesRow>")
						&& i < lines.length - 1)
					if (lines[i + 1].trim().startsWith("<ValuesItem>Labels<")) {
						labelsRow = true;
						unitsRow = false;
						typeRow = false;
					} else if (lines[i + 1].trim().startsWith(
							"<ValuesItem>Units<")) {
						labelsRow = false;
						unitsRow = true;
						typeRow = false;
					} else if (lines[i + 1].trim().startsWith(
							"<ValuesItem>Type<")) {
						labelsRow = false;
						unitsRow = false;
						typeRow = true;
					} else {
						labelsRow = false;
						unitsRow = false;
						typeRow = false;
					}
				if (labelsRow)
					lines[i] = lines[i].replaceAll("<Values", "<Labels")
							.replaceAll("</Values", "</Labels");
				else if (unitsRow)
					lines[i] = lines[i].replaceAll("<Values", "<Units")
							.replaceAll("</Values", "</Units");
				else if (typeRow)
					lines[i] = lines[i].replaceAll("<Values", "<DataType")
							.replaceAll("</Values", "</DataType");
				writer.write(lines[i]);
				writer.newLine();
			}

			writer.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void exportAsHTML(BufferedWriter writer, String title, String indent) {
		try {
			StringWriter strWriter = new StringWriter();
			BufferedWriter bufWriter = new BufferedWriter(strWriter);
			exportAsHTML0(bufWriter, title, indent);
			String baseString = strWriter.toString();
			baseString = baseString.replaceAll("<LabelsRow>",
					"<TR ALIGN=\"RIGHT\">");
			baseString = baseString.replaceAll("</LabelsRow>", "</TR>");
			baseString = baseString.replaceAll("LabelsItem>", "TH>");
			baseString = baseString.replaceAll("<UnitsRow>",
					"<TR ALIGN=\"RIGHT\">");
			baseString = baseString.replaceAll("</UnitsRow>", "</TR>");
			baseString = baseString.replaceAll("UnitsItem>", "TH>");
			baseString = baseString.replaceAll("<DataTypeRow>",
					"<TR ALIGN=\"RIGHT\">");
			baseString = baseString.replaceAll("</DataTypeRow>", "</TR>");
			baseString = baseString.replaceAll("DataTypeItem>", "TH>");
			writer.write(baseString);
			writer.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	protected void setEditBackground(boolean isEditable) {
		clearColors();
		if (isEditable) {
			int numberColumns = getColumnCount();
			int numberRows = getRowCount();
			setDisableBackground(false, 0, numberRows - 1, 0, numberColumns - 1);
		}
	}

	private void setDisableBackground(boolean clear, int startRow, int endRow,
			int startColumn, int endColumn) {
		for (int j = startColumn; j <= endColumn; j++) {
			for (int i = startRow; i <= endRow; i++) {
				if (!isCellEditable(i, j)) {
					setCellBackground(i, j, DISABLED_COLOR);
					continue;
				}
				if (clear)
					setCellBackground(i, j, Color.WHITE);
			}

		}

	}
}
