package wrims.schematic;

import hec.heclib.dss.DSSPathname;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import vista.db.dss.DSSUtil;
import vista.set.DataReference;
import vista.set.Group;
import vista.set.Pathname;
import wrims.dss.CondensedReference;

public class PathnameTable extends JScrollPane {

	PathnameTableModel _tableModel = null;
	JTable _jTable = null;
	int[] _partPositions;
	Vector _condensedList = null;
	String[][] _pathnameParts;

	public PathnameTable() {
		super();
		_tableModel = new PathnameTableModel(null, new Vector());
		constructTable();
	}

	// CB poorly named method TODO rename to generateTableModel or
	// createTableModel
	public PathnameTableModel getTableModel(String[][] pathnameParts,
			Vector condensedList) {
		_tableModel = new PathnameTableModel(pathnameParts, condensedList);
		constructTable();
		return _tableModel;
	}

	public PathnameTableModel getTableModel() {
		return _tableModel;
	}

	public JTable getTable() {
		return _jTable;
	}

	private void constructTable() {
		TableSorter sorter = new TableSorter(_tableModel);
		JTable table = new JTable(sorter);
//		JTable table = new JTable(sorter){
//			@Override  //added by LHZ to select/deselect a row by clicking it
//		    public void changeSelection(int rowIndex, int columnIndex,  
//		            boolean toggle, boolean extend) {  
//		        super.changeSelection(rowIndex, columnIndex, true, false);
//		    }
//		};
		/*
		 * // Use of this renderer screws up the setSelectionMode method ????
		 * JTable table = new JTable(sorter) { public TableCellRenderer
		 * getCellRenderer(int row, int column) { return new
		 * PathRenderer(_tableModel); } };
		 */
		_jTable = table;

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		sorter.setTableHeader(table.getTableHeader());
		setViewportView(table);

		addTableListener();
	}

	public int showFilteredRows(String filter, boolean useRegex) {
		int rows = 0;
		if (_tableModel != null) {
			rows = _tableModel.showFilteredRows(filter, useRegex);
			_tableModel.fireTableStructureChanged();
		}
		return rows;
	}

	public int showFilteredRows(Vector variables) {
		if (!isNewVariablesList(variables)) {
			return _jTable.getRowCount();
		}
		if (_jTable == null) {
			return 0;
		}

		int nrows = 0;
		if (_tableModel != null) {
			nrows = _tableModel.showFilteredRows(variables);
			_tableModel.fireTableStructureChanged();
		}

		// select the 'base bpart' variables
		_jTable.clearSelection();

		int rows = 0;
		for (int i = 0; i < nrows; i++) {
			String part = (String) _tableModel.getValueAt(i, 2);
			if (variables.contains(part)) {
				// CB normally, next line must check if already selected but
				// clearSelection() is
				// called above.
				_jTable.addRowSelectionInterval(i, i);
				rows++;
			}
		}
		// returns 'base variable' count
		return rows;
	}

	// CB added to prevent fireTableChanged and updateAllActions infinite loop.
	private boolean isNewVariablesList(Vector variables) {
		if (_tableModel == null) {
			return true;
		}
		Vector existingVariables = new Vector();
		Collections.sort(variables);
		int bPartColumnIndex = -1;
		bPartColumnIndex = getColumnIndex("B part");
		if (bPartColumnIndex == -1) {
			return true;
		}
		for (int row = 0; row < _tableModel.getRowCount(); ++row) {
			existingVariables
					.add(_tableModel.getValueAt(row, bPartColumnIndex));
		}
		Collections.sort(existingVariables);
		if (!variables.equals(existingVariables)) {
			return true; // CB TO DO: sort them first????
		}
		return false;
	}

	/**
	 * CB added
	 * 
	 * @param name
	 * @return
	 */
	public int getColumnIndex(String name) {
		int index = -1;
		for (int column = 0; column < _jTable.getColumnModel().getColumnCount(); ++column) {
			if (_jTable.getColumnName(column).equals("B part")) {
				index = column;
				break;
			}
		}
		return index;
	}

	public int getSelectedRowCount() {
		return _jTable.getSelectedRowCount();
	}

	public int getRowCount() {
		return _jTable.getRowCount();
	}

	public boolean isMonthlyTimeStep() {
		int[] rows = _jTable.getSelectedRows(); // checked if count > 0 above
		for (int i = 0; i < rows.length; i++) {
			// System.out.println("path:" + tableModel.getPlotPath(rows[i]));
			// System.out.println("B:" + tableModel.getValueAt(rows[i],2));
			// System.out.println("timeStep:" +
			// tableModel.getValueAt(rows[i],5));
			if (!_tableModel.getValueAt(rows[i], 5).equals("1MON")) {
				return false;
			}
		}
		return true;
	}

	public Vector<String> getSelectedPaths() {
		Vector<String> paths = new Vector<String>();
		int[] rows = _jTable.getSelectedRows(); // checked if count > 0 above
		for (int row : rows) {
			// System.out.println("path:" + _tableModel.getPlotPath(rows[i]));
			int index = (Integer)_jTable.getValueAt(row, 0);//LZ added
			paths.addElement(_tableModel.getPath(index - 1));//LZ added
//			paths.addElement(_tableModel.getPlotPath(row));
		}
		return paths;
	}

	/**
	 * CB added for obtaining paths for variable names passed to it (when the
	 * table is not updated first (like when a reservoir node is right-clicked
	 * and Inflow is selected, we do not want to filter the table on the inflow
	 * arc name).
	 * 
	 * @return
	 */
	/*
	 * public Vector getMatchingPaths(Vector names) { Hashtable namesHashtable =
	 * new Hashtable(names.size() * 2); Enumeration namesEnum =
	 * names.elements(); int i = 1; while (namesEnum.hasMoreElements()) { Object
	 * name = namesEnum.nextElement(); namesHashtable.put(name, new
	 * Integer(i++)); } Vector paths = new Vector(); int column =
	 * getColumnIndex("B part"); for (int row = 0; row < _jTable.getRowCount();
	 * ++row) { String name = (String)_jTable.getValueAt(row, column); if
	 * (namesHashtable.get(name) != null)
	 * paths.add(_tableModel.getPlotPath(row)); } return paths; }
	 */

	public void resetTable() {
		_jTable = null;
		_tableModel = null;
		_condensedList = null;
		_pathnameParts = null;
		constructTable();
	}

	public void catalog(String dssfn) {
		Group group = DSSUtil.createGroup("local", dssfn);
		int count = group.getNumberOfDataReferences();
		Vector pathNames = new Vector();
		for (int i = 0; i < count; i++) {
			DataReference ref = group.getDataReference(i);
			Pathname pathname = ref.getPathname();
			pathname.setPart(Pathname.D_PART, ref.getTimeWindow().toString());
			pathNames.add(pathname.getFullPath());
		}

		buildCondensedList(pathNames);
		PathnameTableModel pTableModel = new PathnameTableModel(_pathnameParts,
				_condensedList);
		_tableModel = pTableModel;
		constructTable();
		/*
		 * if (DEBUG) System.out.println("= " + _condensedList.firstElement());
		 * if (DEBUG) System.out.println("= " + _condensedList.get(0)); if
		 * (DEBUG) System.out.println("= " + _condensedList.get(3)); if (DEBUG)
		 * System.out.println("= " + _pathnameParts[0][1]); if (DEBUG)
		 * System.out.println("= " + _pathnameParts[3][1]);
		 */
	}

	public void buildCondensedList(Vector pathnameList) {
		String[][] pathnameParts;
		Vector condensedList = new Vector();
		_partPositions = new int[7];

		int[] maxLengths;
		maxLengths = new int[7];

		String lastPathname = new String(" ");
		CondensedReference condensedPathname = null;
		int number = pathnameList.size();
		for (int i = 0; i < number; i++) {
			String currentPathname = pathnameList.get(i).toString();
			if (!compareCondensedPaths(lastPathname, currentPathname)) {
				if (condensedPathname != null) {
					condensedPathname.complete();
				}
				condensedPathname = new CondensedReference();
				condensedList.addElement(condensedPathname);
			}
			if (condensedPathname != null) {
				condensedPathname.addPathname(currentPathname);
			}
			lastPathname = currentPathname;
		}
		if (condensedPathname != null) {
			condensedPathname.complete();
		}
		for (int i = 0; i < 7; i++) {
			maxLengths[i] = 0;
		}
		number = condensedList.size();
		pathnameParts = new String[number][6];

		for (int i = 0; i < number; i++) {
			// _pathnamesDisplayed[i] = i;
			String pathname = condensedList.get(i).toString();
			int stat = DSSPathname.parsePathname(pathname, _partPositions);
			if (stat != 0) {
				DSSPathname.parsePathname("///////", _partPositions);
			}
			for (int j = 0; j < 6; j++) {
				int length = _partPositions[j + 1] - _partPositions[j];
				if (length > 0) {
					pathnameParts[i][j] = pathname.substring(_partPositions[j],
							_partPositions[j + 1] - 1);
				} else {
					pathnameParts[i][j] = "";
				}
				if (length > maxLengths[j + 1]) {
					maxLengths[j + 1] = length;
				}
			}
		}
		_condensedList = condensedList;
		_pathnameParts = pathnameParts;
	}

	public boolean compareCondensedPaths(String pathname1, String pathname2) {
		if (pathname1.length() != pathname2.length()) {
			return false;
		}
		int stat = DSSPathname.parsePathname(pathname1, _partPositions);
		if (stat != 0) {
			DSSPathname.parsePathname("///////", _partPositions);
		}
		if (_partPositions[4] - _partPositions[3] != 10) {
			return false;
		}
		if (pathname1.substring(0, _partPositions[3]).compareTo(
				pathname2.substring(0, _partPositions[3])) != 0) {
			return false;
		}
		if ((pathname1.substring(_partPositions[4], _partPositions[6])
				.compareTo(pathname2.substring(_partPositions[4],
						_partPositions[6]))) != 0) {
			return false;
		}
		int n = _partPositions[3] + 5;
		try {
			Integer.parseInt(pathname1.substring(n, n + 4));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * CB added.
	 * 
	 * @param pathname1
	 * @param pathname2
	 * @param ignoreParts
	 * @return
	 */
	public boolean comparePaths(String pathname1, String pathname2,
			int[] ignoreParts) {
		String[] path1parts = pathname1.split("/");
		String[] path2parts = pathname2.split("/");
		if (path1parts.length != path2parts.length) {
			return false;
		}
		// CB currently built to ignore A parts
		for (int i = 2; i < path1parts.length; ++i) {
			boolean ignoreI = false;
			for (int j = 0; j < ignoreParts.length; ++j) {
				if (i == ignoreParts[j]) {
					ignoreI = true;
					break;
				}
			}
			if (ignoreI) {
				continue;
			}
			if (!path1parts[i].equalsIgnoreCase(path2parts[i])) {
				return false;
			}
		}
		return true;
	}

	private void addTableListener() {
		// Ask to be notified of selection changes.
		ListSelectionModel rowSM = _jTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					AppAction.updateAllActions();
				} else {
					// CB causes StackOverflowError when from repeated
					// tablemodel change then actiona upated
					AppAction.updateAllActions();
				}
			}
		});
	}

	class PathRenderer extends DefaultTableCellRenderer {
		PathnameTableModel _tableModel;

		public PathRenderer(PathnameTableModel tableModel) {
			super();
			_tableModel = tableModel;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			Color bgcolor = Color.white;
			Color fgcolor = Color.black;

			// matching cell
			if (_tableModel.isCellColored(row, column)) {
				bgcolor = Color.yellow;
				// fgcolor = Color.blue;
			}

			// Boolean
			if (value instanceof Boolean) {
				JCheckBox checkBox = new JCheckBox();
				checkBox.setSelected(((Boolean) value).booleanValue());
				checkBox.setHorizontalAlignment(JLabel.CENTER);
				checkBox.setBackground(bgcolor);
				return checkBox;
			}

			String str;
			if (value instanceof Double) {
				NumberFormat formatter = NumberFormat.getInstance();
				str = (value == null) ? "" : formatter.format(value);
				// System.out.println("Double: "+"["+str+"]");
			} else {
				str = (value == null) ? "" : value.toString();
			}

			if (str.equals("non-Existent")) {
				// bgcolor = Color.blue;
				bgcolor = Color.lightGray;
				str = "";
			}

			JLabel cell = (JLabel) super.getTableCellRendererComponent(table,
					str, isSelected, hasFocus, row, column);

			if ((value instanceof Double) || (value instanceof Integer)) {
				cell.setHorizontalAlignment(JLabel.RIGHT);
			}

			cell.setBackground(bgcolor);
			cell.setForeground(fgcolor);
			return cell;
		}
	}
	
	

}
