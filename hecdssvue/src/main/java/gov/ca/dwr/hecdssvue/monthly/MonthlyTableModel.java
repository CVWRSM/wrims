package gov.ca.dwr.hecdssvue.monthly;

import hec.dssgui.TableFrame;
import hec.gfx2d.G2dDialog;
import hec.heclib.util.HecDoubleArray;
import hec.heclib.util.HecTime;
import hec.heclib.util.HecTimeArray;
import hec.io.TimeSeriesContainer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import rma.swing.table.CellLocation;

/**
 * TableModel used by the AllLayerWindow and AllProjectWindow InternalFrame
 * classes for displaying data in the GUI.
 * 
 * @author Clay Booher
 */
public final class MonthlyTableModel extends AbstractTableModel { // extends
																	// TimeSeriesDataModel
																	// {
	private Vector _columnName = new Vector(1);
	private Vector _dataSets;
	private HecTimeArray _time;
	private boolean _showCommas = false;
	private int _numberHeaderRows;
	private boolean[] _visibleColumns;

	/**
	 *
	 */
	public MonthlyTableModel() {
		_columnName.add("Monthly Tables");
		HecTime.setAlwaysShowTimeAsBeginningOfDay(false); // not
															// working!!!!!!!!!!!!
	}

	public MonthlyTableModel(Vector dataSets, int firstMonth) {
		this();
		setData(dataSets, firstMonth, false, false, 0);
		// CB TO DO: read preferences for commas, average column, and decimals
	}

	public int setData(java.util.List dataSets, int firstMonth,
			boolean showCommas, boolean showTotalColumn, int precision) {
		if (_dataSets == null)
			_dataSets = new Vector(dataSets.size());
		_showCommas = showCommas;
		_time = new HecTimeArray();
		boolean haveData = false;
		String timeZoneID = null;
		int timeZoneRawOffset = 0;
		for (int i = 0; i < dataSets.size(); i++) {
			TimeSeriesContainer timeSeriesContainer = (TimeSeriesContainer) dataSets
					.get(i);
			
			if (timeSeriesContainer !=null){
				// CB added hasTotalColumn code (must consider each data set
				// separately)
				boolean hasTotalColumn;
				if ((showTotalColumn && timeSeriesContainer.units.trim()
						.equalsIgnoreCase("CFS"))
						|| timeSeriesContainer.units.trim().equalsIgnoreCase("TAF") || timeSeriesContainer.units.trim().equalsIgnoreCase("INCH") ||timeSeriesContainer.units.trim().equalsIgnoreCase("IN"))
					hasTotalColumn = true;
				else
					hasTotalColumn = false;
				_dataSets.add(new SingleMonthlyTable(timeSeriesContainer,
						firstMonth, hasTotalColumn, precision));
				if (i == 0) {
					if (timeSeriesContainer.numberValues > 0) // CB check this
															// !!!!!!!!!!!!!!!!!!!
						haveData = true;
					timeZoneID = timeSeriesContainer.timeZoneID;
					timeZoneRawOffset = timeSeriesContainer.timeZoneRawOffset;
				} else if (timeZoneID != null
						&& timeSeriesContainer.timeZoneID != null) {
					if (!timeZoneID.equals(timeSeriesContainer.timeZoneID))
						timeZoneID = null;
				} else
					timeZoneID = null;
			}
		}
		if (timeZoneID != null) {
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
			if (timeZone != null) {
				timeZone.setRawOffset(timeZoneRawOffset);
				if (!timeZoneID.equals(timeZone.getID()))
					timeZone.setID(timeZoneID);
				_time.setTimeZone(timeZone);
			}
		}
		if (haveData == true)
			return 0;
		return -1;
	}

	public int addData(java.util.List dataSets, int firstMonth,
			boolean showCommas, boolean showTotalColumn, int precision) {
		if (_dataSets == null)
			_dataSets = new Vector(dataSets.size());
		_showCommas = showCommas;
		_time = new HecTimeArray();
		boolean haveData = false;
		String timeZoneID = null;
		int timeZoneRawOffset = 0;
		for (int i = 0; i < dataSets.size(); i++) {
			TimeSeriesContainer timeSeriesContainer = (TimeSeriesContainer) dataSets
					.get(i);
			// CB added hasTotalColumn code (must consider each data set
			// separately)
			boolean hasTotalColumn;
			if ((timeSeriesContainer.units.trim().equalsIgnoreCase("TAF") || (showTotalColumn && timeSeriesContainer.units
					.trim().equalsIgnoreCase("CFS") || timeSeriesContainer.units.trim().equalsIgnoreCase("INCH") || timeSeriesContainer.units.trim().equalsIgnoreCase("IN"))))
				hasTotalColumn = true;
			else
				hasTotalColumn = false;
			_dataSets.add(new SingleMonthlyTable(timeSeriesContainer,
					firstMonth, hasTotalColumn, precision));
			if (i == 0) {
				if (timeSeriesContainer.numberValues > 0) // CB check this
															// !!!!!!!!!!!!!!!!!!!
					haveData = true;
				timeZoneID = timeSeriesContainer.timeZoneID;
				timeZoneRawOffset = timeSeriesContainer.timeZoneRawOffset;
			} else if (timeZoneID != null
					&& timeSeriesContainer.timeZoneID != null) {
				if (!timeZoneID.equals(timeSeriesContainer.timeZoneID))
					timeZoneID = null;
			} else
				timeZoneID = null;
		}
		if (timeZoneID != null) {
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
			if (timeZone != null) {
				timeZone.setRawOffset(timeZoneRawOffset);
				if (!timeZoneID.equals(timeZone.getID()))
					timeZone.setID(timeZoneID);
				_time.setTimeZone(timeZone);
			}
		}
		if (haveData == true)
			return 0;
		return -1;
	}

	public Vector getDataSets() {
		return _dataSets;
	}

	public void setShowCommas(boolean showCommas) {
		_showCommas = showCommas;
	}

	public boolean showCommas() {
		return _showCommas;
	}

	public void setShowTotalColumn(boolean showTotalColumn) {
		if (_dataSets == null)
			return;
		for (int i = 0; i < _dataSets.size(); ++i) {
			if (_dataSets.get(i) instanceof SingleMonthlyTable) {
				if (((SingleMonthlyTable) _dataSets.get(i)).getModel() instanceof SingleMonthlyTable.SingleMonthlyTableModel) {
					((SingleMonthlyTable.SingleMonthlyTableModel) ((SingleMonthlyTable) _dataSets
							.get(i)).getModel())
							.setShowTotalColumn(showTotalColumn);
					((SingleMonthlyTable.SingleMonthlyTableModel) ((SingleMonthlyTable) _dataSets
							.get(i)).getModel()).fireTableStructureChanged();
					// reset header and renderers
					((SingleMonthlyTable) _dataSets.get(i)).buildHeader();
					((SingleMonthlyTable) _dataSets.get(i)).setCellRenderers();
				}
			}
		}
	}

	public void setNumberHeaderRows(int number) {
		_numberHeaderRows = number;
	}

	public int getNumberHeaderRows() {
		return _numberHeaderRows;
	}

	public String getToolTipText(int row, int column) {
		row -= _numberHeaderRows;
		column -= 1;
		return ""; // CB TO DO?
	}

	/**
	 * 
	 * @return
	 */
	public int getColumnCount() {
		return 1;
	}

	/**
	 * 
	 * @return
	 */
	public int getRowCount() {
		return _dataSets.size();
	}

	/**
	 * 
	 * @param column
	 * @return
	 */
	public String getColumnName(int column) {
		return (String) _columnName.get(column);
	}

	/**
	 * Returns false; table is not editable.
	 * 
	 * @param row
	 * @param col
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @return
	 */

	public Object getValueAt(int row, int column) {
		return _dataSets.get(row);
	}

	/**
	 * 
	 * @param column
	 * @return
	 */
	public Class<?> getColumnClass(int column) {
		if (getRowCount() > 0) {
			if (getValueAt(0, column) == null) {
				return null;
			} else {
				return getValueAt(0, column).getClass();
			}
		} else {
			return null;
		}
	}

	/**
	 * Allows each set to have its own precision, but not (yet?) implemented in
	 * menuitems.
	 */
	public void setPrecision(int precision) {
		if (_dataSets == null)
			return;
		for (int i = 0; i < _dataSets.size(); ++i) {
			if (_dataSets.get(i) instanceof TimeSeriesContainer) { // TO DO:
																	// need to
																	// remove???
				((TimeSeriesContainer) _dataSets.get(i)).precision = precision; // TO
																				// DO:
																				// works??
			} else if (_dataSets.get(i) instanceof SingleMonthlyTable) {
				if (((SingleMonthlyTable) _dataSets.get(i)).getModel() instanceof SingleMonthlyTable.SingleMonthlyTableModel) {
					((SingleMonthlyTable.SingleMonthlyTableModel) ((SingleMonthlyTable) _dataSets
							.get(i)).getModel()).setPrecision(precision);
				}
			}
		}
	}

	public int getPrecision() {
		if (_dataSets == null)
			return -1;
		int number = -1;
		if (_dataSets.size() > 0) { // TO DO: Determine if all sets should have
									// the same precision
			if (_dataSets.get(0) instanceof TimeSeriesContainer) { // TO DO:
																	// need to
																	// remove???
				return ((TimeSeriesContainer) _dataSets.get(0)).precision;
			} else if (_dataSets.get(0) instanceof SingleMonthlyTable) {
				if (((SingleMonthlyTable) _dataSets.get(0)).getModel() instanceof SingleMonthlyTable.SingleMonthlyTableModel) {
					number = ((SingleMonthlyTable.SingleMonthlyTableModel) ((SingleMonthlyTable) _dataSets
							.get(0)).getModel()).getPrecision();
				}
			}
		}
		return number;
	}

	public String allDataString() {
		StringBuffer dataString = new StringBuffer();
		dataString.append(getColumnHeaderString());
		dataString.append('\n');
		for (int k=0; k<_dataSets.size(); k++){
			SingleMonthlyTable dataSet = (SingleMonthlyTable)_dataSets.get(k);
			dataString.append(dataSet.getHeader());
			int nRows = dataSet.getRowCount();
			int nColumns = dataSet.getColumnCount();
			for (int i = 0; i < nRows; i++) {
				for (int j = 0; j < nColumns; j++) {
					dataString.append(dataSet.getValueAt(i, j));
					if (j < nColumns - 1)
						dataString.append(" \t");
				}
				if (i < nRows - 1)
					dataString.append('\n');
			}
		}
		return dataString.toString();
	}

	public String getSelectedDataString(int minRow, int maxRow, int minColumn,
			int maxColumn) {
		int nRows = maxRow - minRow + 1;
		int nColumns = maxColumn - minColumn + 1;
		StringBuffer dataString = new StringBuffer(nRows * nColumns * 8);
		if (minRow == 0)
			dataString.append(getColumnHeaderString());
		for (int i = minRow; i < maxRow + 1; i++) {
			for (int j = minColumn; j < maxColumn + 1; j++) {
				dataString.append(getValueAt(i, j));
				if (j < maxColumn)
					dataString.append(" \t");
			}
			if (i < maxRow)
				dataString.append('\n');
		}
		return dataString.toString();
	}

	protected String getColumnHeaderString() {
		StringBuffer headerString = new StringBuffer();
		int nColumns = getColumnCount();
		Vector[] headers = new Vector[nColumns];
		int maxLines = 0;
		for (int j = 0; j < nColumns; j++) {
			headers[j] = new Vector();
			String header = getColumnName(j) + "\n";
			int k = 0;
			int max = 0;
			int position = 0;
			while (k >= 0) {
				k = header.indexOf("\n", position);
				if (k > 0) {
					if (k - position > 0)
						headers[j].add(header.substring(position, k));
					else
						headers[j].add("");
					position = k + 1;
					max++;
				}
			}
			if (max > maxLines)
				maxLines = max;
		}
		for (int i = 0; i < maxLines; i++) {
			for (int j = 0; j < nColumns; j++) {
				if (headers[j].size() > i)
					headerString.append((String) headers[j].elementAt(i));
				if (j < nColumns - 1)
					headerString.append("\t");
				else
					headerString.append("\n");
			}
		}
		return headerString.toString();
	}

	public void plot(JFrame parent) {
		Vector dataContainers = getDataSets();
		if (dataContainers.size() != 0) {
			G2dDialog gfxdlg = new G2dDialog(parent, "", false, dataContainers);
			gfxdlg.setVisible(true);
		}
	}

	public void tabulate(JFrame parent) {
		Vector dataContainers = getDataSets();
		if (dataContainers.size() != 0) {
			TableFrame table = new TableFrame(parent); // , "", false,
														// dataContainers);
			table.setData(dataContainers);
			table.showTable();
		}
	}

	/*
	 * public String print() { if (_dataSets != null) { Enumeration dataSets =
	 * _dataSets.elements(); String printString = null; while
	 * (dataSets.hasMoreElements()) { Object element = dataSets.nextElement();
	 * if (element instanceof SingleMonthlyTable) { if
	 * (((SingleMonthlyTable)element).getModel() instanceof
	 * SingleMonthlyTable.SingleMonthlyTableModel) { printString =
	 * allDataString(); } } } return printString; } else return null; }
	 */

	public class SingleMonthlyTable extends JTable implements TableModelListener {
		// private final int DEFAULT_ROW_HEIGHT = 16;
		// private final int DEFAULT_COLUMN_HEADER_HEIGHT = 16;
		private Object[] _tableHeaders = new Object[4];
		private String _fileName;
		private TimeSeriesContainer _tsc;
		// int _rowHeight = DEFAULT_ROW_HEIGHT; // TO DO: from preferences
		// int _defaultColumnHeaderHeight = DEFAULT_COLUMN_HEADER_HEIGHT; // TO
		// DO: from preferences
		int _maxNumGroupsOfAnyColumn = 0; // the max number of ColumnGroups to
											// which any column belongs.

		public SingleMonthlyTable(TimeSeriesContainer tsc) {
			this(tsc, 0, false, 0);
		}

		public SingleMonthlyTable(TimeSeriesContainer tsc, int firstMonth,
				boolean showTotalColumn, int precision) {
			_tsc = tsc;
			setModel(new SingleMonthlyTableModel(firstMonth, showTotalColumn,
					precision));
			_tableHeaders[0] = " File: " + _tsc.fileName;
			_tableHeaders[1] = " Data: " + _tsc.fullName;
			_tableHeaders[2] = " Units: " + _tsc.units;
			HecTime currentTime = new HecTime();
			TimeZone zone = TimeZone.getTimeZone(TimeZone.getDefault().getID());
			currentTime.setTimeInMillis(System.currentTimeMillis(), zone
					.getRawOffset() / 60000);
			_tableHeaders[3] = " Time: " + currentTime.dateAndTime(0);
			buildHeader();
			setCellRenderers();
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
		
		public String getHeader(){
			return _tableHeaders[0]+"\n"+_tableHeaders[1]+"\n"+_tableHeaders[2]+"\n"+_tableHeaders[3]+"\n";
		}
		
		public void buildHeader() {
			ColumnGroup[] columnGroups = new ColumnGroup[_tableHeaders.length
					+ getModel().getColumnCount()];
			GroupableTableHeader header = (GroupableTableHeader) getTableHeader();
			TableColumnModel tcm = header.getColumnModel();
			// for (int i = 0; i < columnGroups.length; ++i) { // correct for 4
			// rows in column
			for (int i = 0; i < _tableHeaders.length; ++i) {
				columnGroups[i] = new ColumnGroup((String) _tableHeaders[i]);
				for (int j = 0; j < getColumnCount(); ++j) { // exchange inner
																// and outer
																// loops
					columnGroups[i].add(tcm.getColumn(j));
				}
				header.addColumnGroup(columnGroups[i]);
			}
			for (int i = 0; i < getModel().getColumnCount(); ++i) {
				columnGroups[_tableHeaders.length + i] = new ColumnGroup(
						getModel().getColumnName(i));
				columnGroups[_tableHeaders.length + i].add(tcm.getColumn(i));
				header.addColumnGroup(columnGroups[_tableHeaders.length + i]);
			}
			header.revalidate();

			int numColumnGroupsOfColumn = 0;
			for (int i = 0; i < getColumnCount(); ++i) {
				Enumeration cgroupEnum = header.getColumnGroups(tcm
						.getColumn(i));
				numColumnGroupsOfColumn = 0;
				ColumnGroup columnGroup = null;
				while (cgroupEnum.hasMoreElements()) {
					Object next = cgroupEnum.nextElement();
					// TableColumn header renderers are null which cause
					// rendereing problems (in getHeaderHeight())
					// All ColumnGroups use same renderer so need jut one
					// ColumnGroup
					if (columnGroup == null && next instanceof ColumnGroup)
						columnGroup = (ColumnGroup) next;
					++numColumnGroupsOfColumn;
				}
				if (numColumnGroupsOfColumn > _maxNumGroupsOfAnyColumn) {
					_maxNumGroupsOfAnyColumn = numColumnGroupsOfColumn;
				}
				// tcm.getColumn(i).setCellRenderer(new
				// SingleMonthlyTableCellRenderer());
				if (columnGroup != null)
					tcm.getColumn(i).setHeaderRenderer(
							columnGroup.getHeaderRenderer());
			}
		}

		protected JTableHeader createDefaultTableHeader() {
			return new GroupableTableHeader(getColumnModel());
		}

		public TimeSeriesContainer getDataSet() {
			return _tsc;
		}

		public void tableChanged(TableModelEvent e) {
			super.tableChanged(e);
			if (getModel() instanceof SingleMonthlyTableModel)
				((SingleMonthlyTableModel) getModel())
						.updateLowestAndHighestRowValues();
		}

		void setCellRenderers() {
			GroupableTableHeader header = (GroupableTableHeader) getTableHeader();
			TableColumnModel tcm = header.getColumnModel();
			for (int i = 0; i < tcm.getColumnCount(); ++i) {
				TableColumn col = getColumnModel().getColumn(i);
				// Cb handle next part in min and max figuring code
				// if (col.getIdentifier() instanceof String &&
				// !((String)col.getIdentifier()).equalsIgnoreCase("TTL"))
				col.setCellRenderer(new SingleMonthlyTableCellRenderer());
			}
		}

		void setHeaderRenderers() {
			GroupableTableHeader header = (GroupableTableHeader) getTableHeader();
			TableColumnModel tcm = header.getColumnModel();
			for (int i = 0; i < getColumnCount(); ++i) {
				Enumeration cgroupEnum = header.getColumnGroups(tcm
						.getColumn(i));
				ColumnGroup columnGroup = null;
				while (cgroupEnum.hasMoreElements()) { // cgroupEnum is null
					Object next = cgroupEnum.nextElement();
					// All ColumnGroups use same renderer so need jut one
					// ColumnGroup
					if (columnGroup == null && next instanceof ColumnGroup)
						columnGroup = (ColumnGroup) next;
				}
				if (columnGroup != null)
					tcm.getColumn(i).setHeaderRenderer(
							columnGroup.getHeaderRenderer());
			}
		}

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
		
		public Vector getSelectedCellRange()
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

			for (r = 0; r < rows.length; r++)
			{
				if (getCellSelectionEnabled())
				{
					for (c = 0; c < cols.length; c++)
					{
						CellLocation cl = new CellLocation(rows[r], cols[c],
								getValueAt(rows[r], cols[c]));
						v.addElement(cl);
					}
				}
				else if (getRowSelectionAllowed())
				{
					// selecting rows

					TableColumnModel tcm = getColumnModel();
					for (c = 0; c < tcm.getColumnCount(); c++)
					{
						CellLocation cl = new CellLocation(rows[r], c,
								getValueAt(rows[r], c));
						v.addElement(cl);
					}
				}
				else if (getColumnSelectionAllowed())
				{
					// todo
				}

			}

			return v;
		}

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
		 * ColumnGroup
		 * 
		 * @version ?? 11/2/06
		 * @author Nobuo Tamemasa
		 * @author Clay Booher
		 */

		public class ColumnGroup {
			protected TableCellRenderer renderer;
			protected Vector v;
			protected String text;
			protected int margin = 0;

			public ColumnGroup(String text) {
				this(null, text);
			}

			public ColumnGroup(TableCellRenderer renderer, String text) {
				if (renderer == null) {
					this.renderer = new DefaultTableCellRenderer() { // Header
																		// Renderer
						public Component getTableCellRendererComponent(
								JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {
							JTableHeader header = table.getTableHeader();
							if (header != null) {
								setForeground(header.getForeground());
								setBackground(header.getBackground()); // gray
																		// usually
								// setForeground(table.getForeground());
								// setBackground(table.getBackground()); //
								// white usually
								if (!header.getFont().isBold()) {
									setFont(new Font(header.getFont()
											.getFontName(), header.getFont()
											.getStyle()
											+ Font.BOLD, header.getFont()
											.getSize()));
								}
							}
							if (value.toString().trim().length() <= 4) {
								setHorizontalAlignment(SwingConstants.CENTER);
							} else {
								setHorizontalAlignment(SwingConstants.LEFT);
							}
							setText((value == null) ? "" : value.toString());
							setBorder(UIManager
									.getBorder("TableHeader.cellBorder"));
							return this;
						}
					};
				} else {
					this.renderer = renderer;
				}
				this.text = text;
				v = new Vector();
			}

			/**
			 * @param obj
			 *            TableColumn or ColumnGroup
			 */
			public void add(Object obj) {
				if (obj == null) {
					return;
				}
				v.addElement(obj);
			}

			/**
			 * @param c
			 *            TableColumn
			 * @param g
			 *            ColumnGroups
			 */
			public Vector getColumnGroups(TableColumn c, Vector g) {
				g.addElement(this);
				if (v.contains(c))
					return g;
				Enumeration e = v.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					if (obj instanceof ColumnGroup) {
						Vector groups = (Vector) ((ColumnGroup) obj)
								.getColumnGroups(c, (Vector) g.clone());
						if (groups != null)
							return groups;
					}
				}
				return new Vector(); // empty Vector
			}

			public TableCellRenderer getHeaderRenderer() {
				return renderer;
			}

			public void setHeaderRenderer(TableCellRenderer renderer) {
				if (renderer != null) {
					this.renderer = renderer;
				}
			}

			public Object getHeaderValue() {
				return text;
			}

			public Dimension getSize(JTable table) {
				Component comp = renderer.getTableCellRendererComponent(table,
						getHeaderValue(), false, false, -1, -1);
				int height = comp.getPreferredSize().height;
				int width = 0;
				Enumeration e = v.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					if (obj instanceof TableColumn) {
						TableColumn aColumn = (TableColumn) obj;
						width += aColumn.getWidth();
						width += margin;
					} else {
						width += ((ColumnGroup) obj).getSize(table).width;
					}
				}
				return new Dimension(width, height);
			}

			public void setColumnMargin(int margin) {
				this.margin = margin;
				Enumeration e = v.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					if (obj instanceof ColumnGroup) {
						((ColumnGroup) obj).setColumnMargin(margin);
					}
				}
			}
			
			/**
			 * return the real table columns that this ColumnGroup holds
			 */
			public Vector getTableColumns()
			{
				int size = v.size();
				Object obj;
				Vector tc = new Vector(v.size());
				for (int i = 0; i < size; i++ )
				{
					obj = v.get(i);
					if ( obj instanceof TableColumn )
					{
						tc.add(obj);
					}
				}
				return tc;
			}
		}

		/**
		 * GroupableTableHeader
		 * 
		 * @version 1.0 10/20/98
		 * @author Nobuo Tamemasa
		 */
		public class GroupableTableHeader extends JTableHeader {
			private static final String uiClassID = "GroupableTableHeaderUI";
			protected Vector columnGroups = null;

			public GroupableTableHeader(TableColumnModel model) {
				super(model);
				setUI(new GroupableTableHeaderUI());
				setReorderingAllowed(false);
			}

			public void updateUI() {
				setUI(new GroupableTableHeaderUI());
			}

			public void setReorderingAllowed(boolean b) {
				reorderingAllowed = false;
			}

			public void addColumnGroup(ColumnGroup g) {
				if (columnGroups == null) {
					columnGroups = new Vector();
				}
				columnGroups.addElement(g);
			}

			public Enumeration getColumnGroups(TableColumn col) {
				if (columnGroups == null)
					return null;
				Enumeration e = columnGroups.elements();
				Vector v_ret = null; // CB moved this outside while loop to
										// allow return of more than one
										// ColumnGroup
				// CB changed loop to allow return of more than one ColumnGroup
				// for more than one header row
				while (e.hasMoreElements()) {
					ColumnGroup cGroup = (ColumnGroup) e.nextElement();
					Vector cGroup_v_ret = (Vector) cGroup.getColumnGroups(col,
							new Vector()); // CB altered from v_ret
					if (cGroup_v_ret.size() > 0) {
						if (v_ret == null)
							v_ret = new Vector();
						Enumeration cGroupsIn_v_ret = cGroup_v_ret.elements(); // CB
																				// added
						while (cGroupsIn_v_ret.hasMoreElements()) { // CB added
							v_ret.add(cGroupsIn_v_ret.nextElement()); // CB
																		// added
						} // CB added
					}
				}
				// CB moved this outside while loop to allow return of more than
				// one ColumnGroup
				if (v_ret != null) {
					return v_ret.elements();
				}
				return null;
			}

			public void setColumnMargin() {
				if (columnGroups == null)
					return;
				int columnMargin = getColumnModel().getColumnMargin();
				Enumeration e = columnGroups.elements();
				while (e.hasMoreElements()) {
					ColumnGroup cGroup = (ColumnGroup) e.nextElement();
					cGroup.setColumnMargin(columnMargin);
				}
			}
		}

		class GroupableTableHeaderUI extends BasicTableHeaderUI {

			int _maxGroupHeight = 0;

			public void paint(Graphics g, JComponent c) {
				Rectangle clipBounds = g.getClipBounds();
				if (header.getColumnModel() == null)
					return;
				((GroupableTableHeader) header).setColumnMargin();
				int column = 0;
				Dimension size = header.getSize();
				Rectangle cellRect = new Rectangle(0, 0, size.width,
						size.height);
				Hashtable h = new Hashtable();
				// CB int columnMargin =
				// header.getColumnModel().getColumnMargin();

				Enumeration enumeration = header.getColumnModel().getColumns();
				while (enumeration.hasMoreElements()) {
					cellRect.height = size.height;
					cellRect.y = 0;
					TableColumn aColumn = (TableColumn) enumeration
							.nextElement();
					Enumeration cGroups = ((GroupableTableHeader) header)
							.getColumnGroups(aColumn);
					if (cGroups != null) {
						int groupHeight = 0;
						while (cGroups.hasMoreElements()) {
							ColumnGroup cGroup = (ColumnGroup) cGroups
									.nextElement();
							Rectangle groupRect = (Rectangle) h.get(cGroup);
							if (groupRect == null) {
								groupRect = new Rectangle(cellRect);
								Dimension d = cGroup.getSize(header.getTable());
								groupRect.width = d.width;
								groupRect.height = d.height;
								h.put(cGroup, groupRect);
							}
							paintCell(g, groupRect, cGroup);
							groupHeight += groupRect.height;
							cellRect.height = size.height - groupHeight;
							cellRect.y = groupHeight;
						}
					}
					// cellRect.width = aColumn.getWidth() + columnMargin; // CB
					// - no! it makes the header sections too wide!
					cellRect.width = aColumn.getWidth(); // CB - correct
					if (cellRect.intersects(clipBounds)) {
						paintCell(g, cellRect, column);
					}
					cellRect.x += cellRect.width;
					column++;
				}
			}

			private void paintCell(Graphics g, Rectangle cellRect,
					int columnIndex) {
				TableColumn aColumn = header.getColumnModel().getColumn(
						columnIndex);
				TableCellRenderer renderer = aColumn.getHeaderRenderer(); // CB
																			// why??
				renderer = new DefaultTableCellRenderer() {
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						JLabel headerLabel = new JLabel();
						headerLabel.setForeground(table.getTableHeader()
								.getForeground());
						headerLabel.setBackground(table.getTableHeader()
								.getBackground());
						headerLabel.setFont(table.getTableHeader().getFont());
						headerLabel
								.setHorizontalAlignment(SwingConstants.CENTER);
						headerLabel.setText(value.toString());
						headerLabel.setBorder(UIManager
								.getBorder("TableHeader.cellBorder"));
						return headerLabel;
					}

				};
				Component c = renderer.getTableCellRendererComponent(header
						.getTable(), aColumn.getHeaderValue(), false, false,
						-1, columnIndex);
				rendererPane.add(c);
				rendererPane.paintComponent(g, c, header, cellRect.x,
						cellRect.y, cellRect.width, cellRect.height, true);
			}

			private void paintCell(Graphics g, Rectangle cellRect,
					ColumnGroup cGroup) {
				TableCellRenderer renderer = cGroup.getHeaderRenderer();
				Component component = renderer.getTableCellRendererComponent(
						header.getTable(), cGroup.getHeaderValue(), false,
						false, -1, -1);
				rendererPane.add(component);
				rendererPane.paintComponent(g, component, header, cellRect.x,
						cellRect.y, cellRect.width, cellRect.height, true);
			}

			private int getHeaderHeight() {
				int height = 0;
				columnModel = header.getColumnModel();
				for (int column = 0; column < columnModel.getColumnCount(); column++) {
					TableColumn aColumn = columnModel.getColumn(column);
					TableCellRenderer renderer = aColumn.getHeaderRenderer();
					if (renderer == null) {
						aColumn.setHeaderRenderer(header.getDefaultRenderer());
						renderer = aColumn.getHeaderRenderer();
					}
					// CB Component comp =
					// renderer.getTableCellRendererComponent(header.getTable(),
					// CB aColumn.getHeaderValue(), false, false,-1, column);
					int cHeight = 0; // = comp.getPreferredSize().height; // CB
										// - NO! Makes an extra header row!
					Enumeration e = ((GroupableTableHeader) header)
							.getColumnGroups(aColumn);
					if (e != null) {
						while (e.hasMoreElements()) {
							ColumnGroup cGroup = (ColumnGroup) e.nextElement();
							cHeight += cGroup.getSize(header.getTable()).height;
						}
					}
					height = Math.max(height, cHeight);
				}
				return height;
			}

			private Dimension createHeaderSize(long width) {
				TableColumnModel headerColumnModel = header.getColumnModel();
				width += headerColumnModel.getColumnMargin()
						* headerColumnModel.getColumnCount();
				if (width > Integer.MAX_VALUE) {
					width = Integer.MAX_VALUE;
				}
				return new Dimension((int) width, getHeaderHeight());
			}

			public Dimension getPreferredSize(JComponent c) {
				long width = 0;
				Enumeration enumeration = header.getColumnModel().getColumns();
				while (enumeration.hasMoreElements()) {
					TableColumn aColumn = (TableColumn) enumeration
							.nextElement();
					width = width + aColumn.getPreferredWidth();
				}
				return createHeaderSize(width);
			}
		}

		public class SingleMonthlyTableModel extends AbstractTableModel {
			final String[] months = { "OCT", "NOV", "DEC", "JAN", "FEB", "MAR",
					"APR", "MAY", "JUN", "JUL", "AUG", "SEP" };
			private Vector _columnNames = new Vector(); // do not need this
														// actually
			private Vector _data = new Vector();
			HecDoubleArray _hdaValues;
			double[] lowestRowValues; // outside of renderer for speed
			double[] highestRowValues; // outside of renderer for speed

			public SingleMonthlyTableModel(int firstDisplayMonth,
					boolean showTotalColumn, int precision) {
				HecTime.setAlwaysShowTimeAsBeginningOfDay(false);
				Object[] row = new Object[2];

				// for NO row header
				if (firstDisplayMonth == 0) // wateryear (Oct - Sep)
					_columnNames.add("WY");
				else if (firstDisplayMonth == 5) // federal contract year (Mar -
													// Feb)
					_columnNames.add("CY");
				else
					// calendar year
					_columnNames.add("YEAR");

				// read the first twelve data to determine which months are to
				// be displayed
				Hashtable<Integer, Object> displayMonths = new Hashtable<Integer, Object>();
				for (int j = 0; j < 12; ++j) {
					if (j<_tsc.times.length){
						int time = _tsc.times[j];
						HecTime hecTime = new HecTime();
						hecTime.set(time);
						int dataMonth = hecTime.month() - 10;
						if (dataMonth < 0)
							dataMonth += 12;
						// System.out.println("dataMonth = " + dataMonth);
						displayMonths.put(dataMonth, new Object()); // dummy value -
																// will only
																// check if null
					}
				}

				_numberHeaderRows = 4; // CB to do: eliminate hard-coding
				int startTime = _tsc.startTime; // minutes since Dec 31 1900
												// 24:00
				HecTime hecStartTime = new HecTime();
				hecStartTime.set(startTime);
				int startMonthIndex = getTimeMonthIndex(hecStartTime.toString());
				int endTime = _tsc.endTime;
				HecTime hecEndTime = new HecTime();
				hecEndTime.set(endTime);
				// int startYear = hecStartTime.year();
				int numValues = _tsc.numberValues;

				int numOnFirstRow=0;
				if (firstDisplayMonth < 0 || firstDisplayMonth > 11)
					firstDisplayMonth = 0;
				for (int i = firstDisplayMonth; i < months.length
						+ firstDisplayMonth; ++i) {
					if (displayMonths.get(i % 12) != null){
						_columnNames.add(months[i % 12]);
						if (firstDisplayMonth<=startMonthIndex && i<startMonthIndex){
							numOnFirstRow = numOnFirstRow + 1;
						}else if (firstDisplayMonth>startMonthIndex && i<startMonthIndex+12){
							numOnFirstRow = numOnFirstRow + 1;
						}
					}
				}
				_columnNames.add("TTL"); // the 2nd last column is the line total/#
											// of values on the line
				
				_columnNames.add("AVG");
				
				_hdaValues = new HecDoubleArray(_tsc.values);
				_hdaValues.setPrecision(precision);
				double totalSum = 0;
				int totalNumValid = 0;
				for (int j = 0; j < numValues; ++j) {
					// int column = ((months.length - numOnFirstRow) + j) %
					// months.length;
					int column = (numOnFirstRow + j)
							% displayMonths.size();
					if (column == 0 || j == 0) {
						row = new Object[2]; // NO row header
						row[1] = new HecDoubleArray(); // NO row header
						int time = _tsc.times[j];
						HecTime hecTime = new HecTime();
						hecTime.set(time);
						// System.out.println(hecTime.toString());
						int length = String.valueOf(hecTime.year()).length();
						if (firstDisplayMonth == 0) { // OCT
						// System.out.println(hecTime.month());
							if (hecTime.month() > 9) // OCT, NOV, DEC
								row[0] = String.valueOf(hecTime.year() + 1); // NO
																				// row
																				// header
							else
								// all other months
								row[0] = String.valueOf(hecTime.year()); // NO
																			// row
																			// header
						} else if (firstDisplayMonth == 3) { // JAN
							row[0] = String.valueOf(hecTime.year());
						} else { // CB TODO: test
							// other
							if (String.valueOf(hecTime.year()).endsWith("9")) {
								row[0] = String.valueOf(hecTime.year())
										+ "-"
										+ String.valueOf(hecTime.year() + 1)
												.substring(length - 2, length);
							} else {
								row[0] = String.valueOf(hecTime.year())
										+ "-"
										+ String.valueOf(hecTime.year() + 1)
												.substring(length - 1, length);
							}
						}
						if (j == 0 && column > 0) {
							for (int k = 0; k < column; ++k) {
								((HecDoubleArray) row[1]).element(k)
										.set(-901.0); // -901 or -902????????
								((HecDoubleArray) row[1]).element(k)
										.setUndefined();
							}
						}
					}
					if (row != null) {
						((HecDoubleArray) row[1]).element(column).set(
								_hdaValues.element(j)); // _hdaValues has all
														// time window data and
														// nothing extra
						if (column == displayMonths.size() - 1
								|| j == numValues - 1) {
							// if (column == displayMonths.size() - 1 ) {
							int numValid = 0;
							double sum = 0.0;
							for (int k = 0; k < displayMonths.size(); ++k) {
								if (((HecDoubleArray) row[1]).element(k)
										.isDefined()) {
									++numValid;
									sum += ((HecDoubleArray) row[1]).element(k)
											.value(); // CB TO DO: check
														// (summing year and avg
														// column???
								}
							}
							totalSum += sum;
							totalNumValid += numValid;
							// ((HecDoubleArray)row[1]).element(displayMonths.size()).set(sum/numValid);
							// //average column
							((HecDoubleArray) row[1]).element(
									displayMonths.size()).set(sum); // total
																	// column
							// if (column == displayMonths.size() - 1) { //CB
							// DOES NOT WORK FOR LESS THAN 12 MONTHS IN A ROW.
							// int subtraction = 0;
							// if (!isShowTotalColumn())
							// ++subtraction;
							
							((HecDoubleArray) row[1]).element(
									displayMonths.size()+1).set(sum/displayMonths.size()); 
							
							if (column == _columnNames.size() - 2) { // -
																		// subtraction)
																		// {
								if (j == numValues - 1) { // THIS FIXES EXCEPT
															// FOR LAST ROW for
															// if (column ==
															// displayMonths.size()
															// - 1) as
															// predecessor line
									for (int k = column + 1; k < 12; ++k) {
										// row filler at end if needed
										((HecDoubleArray) row[1]).element(k)
												.set(-901.0); // -901 or -902?
										((HecDoubleArray) row[1]).element(k)
												.setUndefined();
									}
								}
							}
							_data.add(row);
						}
					}
				}
				// add blank row
				row = new Object[2];
				row[0] = "";
				row[1] = new HecDoubleArray();
				_data.add(row);
				// Make the AVG, MIN, and MAX rows and add them
				double[] averageColumnValues = null;
				double[] minColumnValues = null;
				double[] maxColumnValues = null;
				int[] numColumnValues = null;
				Enumeration dataRows = _data.elements();
				while (dataRows.hasMoreElements()) {
					row = (Object[]) dataRows.nextElement(); // no row header
					if (averageColumnValues == null
							&& ((HecDoubleArray) row[1]).numberElements() > 0) {
						averageColumnValues = new double[((HecDoubleArray) row[1])
								.numberElements()];
						minColumnValues = new double[((HecDoubleArray) row[1])
								.numberElements()];
						for (int i = 0; i < minColumnValues.length; ++i) {
							minColumnValues[i] = Integer.MAX_VALUE;
						}
						maxColumnValues = new double[((HecDoubleArray) row[1])
								.numberElements()];
						for (int i = 0; i < maxColumnValues.length; ++i) {
							maxColumnValues[i] = Integer.MIN_VALUE;
						}
						numColumnValues = new int[((HecDoubleArray) row[1])
								.numberElements()];
					}
					for (int i = 0; i < ((HecDoubleArray) row[1])
							.numberElements(); ++i) {
						if (((HecDoubleArray) row[1]).element(i).isDefined()) {
							averageColumnValues[i] += ((HecDoubleArray) row[1])
									.element(i).value();
							if (((HecDoubleArray) row[1]).element(i).value() < minColumnValues[i])
								minColumnValues[i] = ((HecDoubleArray) row[1])
										.element(i).value();
							if (((HecDoubleArray) row[1]).element(i).value() > maxColumnValues[i])
								maxColumnValues[i] = ((HecDoubleArray) row[1])
										.element(i).value();
							++numColumnValues[i];
						}
					}
				}
				if (averageColumnValues!=null){
					for (int i = 0; i < averageColumnValues.length; ++i) {
						averageColumnValues[i] = averageColumnValues[i]
							/ numColumnValues[i];
					}

					// replace the avgColumnValues row's avg column (last column)
					// value with actual average, cause columns can have different
					// number of data)
					// avgColumnValues[avgColumnValues.length - 1] =
					// totalSum/totalNumValid;
					averageColumnValues[averageColumnValues.length - 1] = totalSum/numColumnValues[numColumnValues.length-1]/(numColumnValues.length-2);
					row = new Object[2];
					row[0] = "AVG";
					row[1] = new HecDoubleArray(averageColumnValues);
					((HecDoubleArray) row[1]).setPrecision(precision);
					_data.add(row);
					row = new Object[2];
					row[0] = "MIN";
					row[1] = new HecDoubleArray(minColumnValues);
					((HecDoubleArray) row[1]).setPrecision(precision);
					_data.add(row);
					row = new Object[2];
					row[0] = "MAX";
					row[1] = new HecDoubleArray(maxColumnValues);
					((HecDoubleArray) row[1]).setPrecision(precision);
					_data.add(row);
					initVisibleColumns();
					if (!showTotalColumn){
						setColumnVisibility(_columnNames.size() - 2,
								showTotalColumn);
						setColumnVisibility(_columnNames.size() - 1,
							showTotalColumn);
					}
				}
				
			}

			/**
			 * Originally intended to allow column visibility, but only the
			 * needed data is used, so only need this method for the row average
			 * column.
			 */
			private void initVisibleColumns() {
				_visibleColumns = new boolean[_columnNames.size()];
				for (int i = 0; i < _visibleColumns.length; ++i) {
					setColumnVisibility(i, true);
				}
			}

			public boolean getColumnVisibility(int index) {
				if (_visibleColumns!=null && index < _visibleColumns.length && index > 0)
					return _visibleColumns[index];
				else
					return false;
			}

			/**
			 * Originally intended to allow column visibility, but only the
			 * needed data is used, so only need this method for the row average
			 * column.
			 * 
			 * @param index
			 * @param isVisible
			 */
			private void setColumnVisibility(int index, boolean isVisible) {
				if (index < _visibleColumns.length)
					_visibleColumns[index] = isVisible;
			}

			void updateLowestAndHighestRowValues() {
				updateLowestRowValues();
				updateHighestRowValues();
			}

			void updateLowestRowValues() {
				if (lowestRowValues == null) {
					lowestRowValues = new double[_data.size()];
				}
				Enumeration rows = _data.elements();
				int i = 0;
				while (rows.hasMoreElements()) {
					// next 8 lines added to handle column visibility
					HecDoubleArray rowOfVisibles = new HecDoubleArray();
					Object[] rowArray = (Object[]) rows.nextElement(); // NO row
																		// header
					int subtraction = 0;
					if (!isShowTotalColumn())
						++subtraction;
					for (int j = 0; j < ((HecDoubleArray) rowArray[1])
							.numberElements()
							- 2 - subtraction; ++j) { // 13 elements
						if (_visibleColumns[j]) { // 14 length
							rowOfVisibles.element(j).set(
									((HecDoubleArray) rowArray[1]).element(j));
							if (!((HecDoubleArray) rowArray[1]).element(j)
									.isDefined())
								rowOfVisibles.element(j).setUndefined();
						}
					}
					lowestRowValues[i++] = ((HecDoubleArray) rowOfVisibles)
							.minimum().value();
				}
			}

			void updateHighestRowValues() {
				if (highestRowValues == null) {
					highestRowValues = new double[_data.size()];
				}
				Enumeration rows = _data.elements();
				int i = 0;
				while (rows.hasMoreElements()) {
					// next 8 lines added to handle column visibility
					HecDoubleArray rowOfVisibles = new HecDoubleArray();
					Object[] rowArray = (Object[]) rows.nextElement(); // NO row
																		// header
					int subtraction = 0;
					if (!isShowTotalColumn())
						++subtraction;
					for (int j = 0; j < ((HecDoubleArray) rowArray[1])
							.numberElements()
							- 2 - subtraction; ++j) {
						if (_visibleColumns[j]) {
							rowOfVisibles.element(j).set(
									((HecDoubleArray) rowArray[1]).element(j));
							if (!((HecDoubleArray) rowArray[1]).element(j)
									.isDefined())
								rowOfVisibles.element(j).setUndefined();
						}
					}
					highestRowValues[i++] = ((HecDoubleArray) rowOfVisibles)
							.maximum().value();
				}
			}

			// CB: before considering precision, I wrote this method. Now I
			// think I should have
			// not used a Vector of HecDoubleArrays, but instead one
			// HecDoubleArray (_hdaValues).
			// Sure getValueAt would have been more complicated, but changing
			// the precision would
			// have been cleaner.
			void setPrecision(int precision) {
				// should check to make sure position is in proper range
				// (whatever it is)
				if (_hdaValues != null && precision != _hdaValues.precision()
						&& precision >= -1 && precision <= 6) {
					_hdaValues.setPrecision(precision);
					Enumeration rows = _data.elements();
					while (rows.hasMoreElements()) {
						Object[] row = (Object[]) rows.nextElement();
						((HecDoubleArray) row[1]).setPrecision(precision);
					}
				}
			}

			public int getPrecision() {
				if (_hdaValues != null)
					return _hdaValues.precision();
				else
					return -1;
			}

			public void setShowTotalColumn(boolean showTotalColumn) {
				setColumnVisibility(_columnNames.size() - 1, showTotalColumn);
			}

			boolean isShowTotalColumn() {
				return getColumnVisibility(_columnNames.size() - 1);
			}

			/**
			 * CB - copied here from PathnameTableModel This function converts a
			 * column number in the table to the right number of the datas.
			 */
			private int getColumnNumber(int index) {
				if (index >= _visibleColumns.length)
					return -1; // CB added check
				int colNum = index; // right number to return
				int i = 0;
				do {
					if (!(_visibleColumns[i]))
						++colNum;
					i++;
				} while (i < colNum);
				// If we are on an invisible column, we have to go one step
				// further
				while (!(_visibleColumns[colNum]))
					++colNum;
				return colNum;
			}

			public String getColumnName(int index) {
				return (String) _columnNames.get(getColumnNumber(index));
			}

			public int getColumnCount() {
				// return _columnNames.size(); // ALL columns visible
				int count = 0;
				for (int i = 0; i < _columnNames.size(); ++i)
					if (_visibleColumns!=null && _visibleColumns[i])
						++count;
				return count;
			}

			public int getRowCount() {
				return _data.size();
			}

			// CB: before considering precision, I wrote this method. Now I
			// think I should have
			// not used a Vector of HecDoubleArrays, but instead one
			// HecDoubleArray (_hdaValues).
			// Sure getValueAt would have been more complicated, but changing
			// the precision would
			// have been cleaner.
			public Object getValueAt(int row, int column) {
				// System.out.println("row = " + row + " and column = " +
				// column); // debugging
				/*
				 * rowheader ver if (_data.get(row) instanceof HecDoubleArray) {
				 * if
				 * (((HecDoubleArray)_data.get(row)).element(getColumnNumber(column
				 * )) instanceof HecDouble) { return ((HecDouble)
				 * ((HecDoubleArray) _data.get(row))
				 * .element(getColumnNumber(column))).string( ((HecDoubleArray)
				 * _data.get(row)).precision(), _showCommas); } else { return
				 * ((Vector)_data.get(row)).get(getColumnNumber(column)); } }
				 * else if (_data.get(row) instanceof String) { return
				 * (String)_data.get(row); } else { return null; }
				 */
				Object[] rowArray = (Object[]) _data.get(row);
				if (column == 0) {
					return rowArray[0];
				} else {
					return ((HecDoubleArray) rowArray[1]).element(
							getColumnNumber(column - 1)).string(
							((HecDoubleArray) rowArray[1]).precision(),
							_showCommas);
				}
			}

			private int getTimeMonthIndex(String month) {
				for (int i = 0; i < months.length; ++i) {
					if (month.toLowerCase().indexOf(months[i].toLowerCase()) > -1) {
						return i;
					}
				}
				return -1;
			}

			/*
			 * public String print() { int rows = getRowCount(); int columns =
			 * getColumnCount(); String line = '\t' + "BOB"; //
			 * getNominalName(); // debugging String s = fillTabs(line) + '\n';
			 * line = ""; for (int j = 0; j < columns; j++) line += '\t' +
			 * getColumnName(j); s += fillTabs(line) + '\n'; for (int i = 0; i <
			 * rows; i++) { line = ""; for (int j = 0; j < columns; j++) line +=
			 * '\t' + getValueAt(i, j).toString(); s += fillTabs(line) + '\n'; }
			 * return s; }
			 */
			public String fillTabs(String line) {
				String answer = "";
				StringTokenizer parser = new StringTokenizer(line, "\t", true);
				int number = parser.countTokens();
				try {
					for (int i = 0; i < number; i++) {
						String token = parser.nextToken();
						if (token.equals("\t"))
							answer += tabPos(answer.length());
						else
							answer += (String) token;
					}
				} catch (NoSuchElementException nosuchelementexception) {
				}
				return answer;
			}

			public String tabPos(int position) {
				String blanks = "                  ";
				if (position < 3) {
					int diff = 3 - position;
					return blanks.substring(0, diff);
				}
				int loc = position / 15;
				loc *= 15;
				int diff = 15 - (position - loc);
				return blanks.substring(0, diff);
			}
		}

		class SingleMonthlyTableCellRenderer extends JLabel implements
				TableCellRenderer {
			Font normal = null;
			Font bold = null;
			Font boldItalic = null;

			/**
			 *
			 */
			public SingleMonthlyTableCellRenderer() {
				setOpaque(true); // MUST do this for background to show up.
				if (normal == null) {
					normal = getFont();
					bold = new Font(normal.getName(), Font.BOLD, normal
							.getSize());
					boldItalic = new Font(normal.getName(), Font.BOLD
							+ Font.ITALIC, normal.getSize());
				}
			}

			/**
			 * 
			 * @return
			 */
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Object[] rowArray = (Object[]) ((SingleMonthlyTableModel) table
						.getModel())._data.get(row);

				// Enumeration columns = table.getColumnModel().getColumns();
				if (column > 0) { // && column <
									// table.getColumnModel().getColumnCount() -
									// 1) {
					if (((HecDoubleArray) rowArray[1]).element(column - 1)
							.value() == ((SingleMonthlyTableModel) table
							.getModel()).lowestRowValues[row]) {
						setForeground(Color.RED);
						setBackground(Color.WHITE);
						setFont(boldItalic);
						setHorizontalAlignment(SwingConstants.RIGHT);
					} else if (((HecDoubleArray) rowArray[1]).element(
							column - 1).value() == ((SingleMonthlyTableModel) table
							.getModel()).highestRowValues[row]) {
						setForeground(Color.BLUE);
						setBackground(Color.WHITE);
						setFont(bold);
						setHorizontalAlignment(SwingConstants.RIGHT);
					} else {
						setForeground(Color.BLACK);
						setBackground(Color.WHITE);
						setFont(bold);
						setHorizontalAlignment(SwingConstants.RIGHT);
					}
				} else {
					setBackground(table.getTableHeader().getBackground());
					setForeground(Color.BLACK);
					setFont(bold);
					setHorizontalAlignment(SwingConstants.CENTER);
				}
				if (value != null) {
					setText(value.toString());
				} else
					setText(null);

				if (value != null)
					setText(value.toString());
				else
					setText(null);
				return this;
			}
		}
	}
}
