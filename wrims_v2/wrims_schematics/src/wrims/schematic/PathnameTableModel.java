package wrims.schematic;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

public class PathnameTableModel extends AbstractTableModel {
	public static boolean DEBUG = false;
	private String[] _columnNames = { "Number", "A part", "B part", "C part",
			"D part", "E part", "F part" };
	private Object[][] _data = {};
	private boolean[][] _match = {};
	// private Hashtable _matchColumns = new Hashtable();
	private List _matchColumnList = new ArrayList();
	private List _matchColumnNameList = new ArrayList();
	private List _userFilters = new ArrayList();

	private String[][] _pathnameParts;
	private Vector _condensedList;

	/*
	 * visible column/row ideas from
	 * http://www.codeguru.com/java/articles/660.shtml
	 */
	private boolean[] _columnsVisible;
	private boolean[] _rowsVisible;

	public PathnameTableModel(String[][] pathnameParts, Vector condensedList) {
		_pathnameParts = pathnameParts;
		_condensedList = condensedList;

		// setColumnNames();
		// parseResults(results, var);
		setData();
		initVisibleColumns();
		initVisibleRows();
	}

	/**
	 * CB added to help with reservoir inflow and outflow dss display without
	 * filtering on them.
	 */
	public Object clone() {
		PathnameTableModel newPTM = new PathnameTableModel(_pathnameParts,
				_condensedList);
		newPTM.setData();
		// newPTM.initVisibleColumns();
		newPTM._columnsVisible = _columnsVisible;
		// newPTM.initVisibleRows();
		newPTM._rowsVisible = _rowsVisible;
		return newPTM;
	}

	/**
	 * CB added for obtaining paths for variable names passed to it (when the
	 * table is not updated first (like when a reservoir node is right-clicked
	 * and Inflow is selected, we do not want to filter the table on the inflow
	 * arc name).
	 * 
	 * @return
	 */
	public Vector<String> getMatchingPaths(Vector<String> names) {
		Enumeration<String> namesEnum = names.elements();
		Vector<String> paths = new Vector<String>();
		while (namesEnum.hasMoreElements()) {
			String name = (String) namesEnum.nextElement();
			for (int row = 0; row < getRowCount(); ++row) {
				String name2 = (String) getValueAt(row, 2); // hard coded column
															// index for now
				if (name.equals((name2))) // changed to equals to speed up and i
											// think it is OK
					paths.add(getPlotPath(row));
			}
		}
		return paths;
	}

	private void initVisibleColumns() {
		_columnsVisible = new boolean[_columnNames.length];
		for (int i = 0; i < _columnsVisible.length; i++) {
			setColumnTrue(i);
		}
	}

	private void initVisibleRows() {
		int rows = _condensedList.size();
		_rowsVisible = new boolean[rows];
		showAllRows();
	}

	public void setColumnFalse(int col) {
		_columnsVisible[col] = false;
	}

	public void setColumnTrue(int col) {
		_columnsVisible[col] = true;
	}

	/*
	 * Displaying rows methods
	 */
	public void showAllRows() {
		for (int i = 0; i < _rowsVisible.length; i++) {
			_rowsVisible[i] = true;
		}
	}

	// Filter specs for /A/B/C/D/E/F/ parts
	public int showFilteredRows(String filter, boolean useRegex) {
		String search = filter.trim().toUpperCase();
		if (DEBUG)
			System.out.println("useRegex: " + useRegex);
		if (DEBUG)
			System.out.println("filter:[" + search + "]");
		// if (filter.equals("///////") && !useRegex)
		// filter = "/*/*/*/*/*/*/";
		// filter = "/.*/.*/.*/.*/.*/.*/";

		if (DEBUG)
			System.out.println("filter: " + search);

		String[] parts = search.split("/");
		int i, j;
		boolean b[][] = new boolean[_rowsVisible.length][_columnNames.length];
		Pattern[] pattern = new Pattern[7];

		// Compile the match pattern for each 'part'
		for (i = 1; i < 7; i++) {
			// map interface non-regex wildcards to a regex
			if (!useRegex) {
				parts[i] = parts[i].replaceAll("\\?", ".");
				parts[i] = parts[i].replaceAll("\\*", ".*");
			}

			if (DEBUG)
				System.out.println(i + ":" + parts[i]);
			pattern[i] = Pattern.compile(parts[i]);

		}

		int rows = 0;
		boolean match = true;
		for (i = 0; i < _rowsVisible.length; i++) {

			for (j = 1; j < 7; j++) {
				match = pattern[j].matcher((String) _data[i][j]).matches();
				if (!match)
					break;

				// highlight the fields that matched any specs
				if (match && !parts[j].equals(".*")) {
					b[i][j] = true;
					rows++;
				} else
					b[i][j] = false;

				// if (DEBUG)
				// System.out.println("i:j"+i+":"+j+" -> "+pattern[j].matcher((String)_data[i][j]).matches());
			}
			_rowsVisible[i] = match;
		}
		_match = b;
		return rows;
	}

	public boolean isValidBParts(Vector bparts) {
		int i, j;

		// build regex of bparts from Vector
		if (DEBUG)
			System.out.println("bparts " + bparts);
		int number = bparts.size();

		String glob = "[^0-9].*";
		String Bs = "";
		String part;
		for (j = 0; j < number; j++) {
			part = (String) bparts.get(j);
			part = part + "|" + part + glob;
			if (Bs.equals(""))
				Bs = part;
			else
				Bs = Bs + "|" + part;
		}

		Pattern pattern = Pattern.compile(Bs);

		boolean match = false;
		for (i = 0; i < _rowsVisible.length; i++) {
			for (j = 2; j < 3; j++) {
				match = pattern.matcher((String) _data[i][j]).matches();
				if (match)
					return true;
			}
		}
		return false;
	}

	// Filter specs for B part
	// from Schematic
	public int showFilteredRows(Vector bparts) {
		// String search = filter.trim().toUpperCase();
		int i, j;

		// build regex of bparts from Vector
		if (DEBUG)
			System.out.println("bparts " + bparts);
		int number = bparts.size();

		String glob = "[^0-9].*";
		String Bs = "";
		String part;
		for (j = 0; j < number; j++) {
			part = (String) bparts.get(j);
			part = part + "|" + part + glob;
			if (Bs.equals(""))
				Bs = part;
			else
				Bs = Bs + "|" + part;
		}

		if (DEBUG)
			System.out.println("Bs " + Bs);

		// String Bs = "C3|C5";
		Pattern pattern = Pattern.compile(Bs);

		int rows = 0;
		boolean match = true;
		for (i = 0; i < _rowsVisible.length; i++) {
			for (j = 2; j < 3; j++) {
				match = pattern.matcher((String) _data[i][j]).matches();
				if (!match)
					break;
				rows++;
			}
			_rowsVisible[i] = match;
		}
		return rows;
	}

	private int getColumnIndex(String field) {
		int j = 0;
		for (int i = 0; i < _columnNames.length; i++) {
			if (_columnNames[i].equals(field)) {
				j = i;
				break;
			}
		}
		return j;
	}

	/**
	 * This function converts a column number in the table to the right number
	 * of the datas.
	 */
	protected int getNumber(int index, boolean[] itemVisible) {
		int n = index; // right number to return
		int i = 0;
		do {
			if (!(itemVisible[i]))
				n++;
			i++;
		} while (i < n);
		// If we are on an invisible column/row,
		// we have to go one step further
		while (!(itemVisible[n]))
			n++;
		return n;
	}

	private void setData() {
		int rows = _condensedList.size();
		Object d[][] = new Object[rows][_columnNames.length];
		boolean b[][] = new boolean[rows][_columnNames.length];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < _columnNames.length; j++) {
				if (j == 0)
					d[i][j] = i + 1;
				else
					d[i][j] = _pathnameParts[i][j - 1];
				b[i][j] = false;
			}
		}
		_data = d;
		_match = b;
	}

	public List getFilterLabels() {
		return _userFilters;
	}

	public List getMatchColumns() {
		return _matchColumnList;
	}

	public List getMatchColumnNames() {
		return _matchColumnNameList;
	}

	public boolean isCellColored(int row, int col) {
		return _match[getNumber(row, _rowsVisible)][getNumber(col,
				_columnsVisible)];
	}

	public boolean isNameColumn(int col) {
		return (col == 0);
	}

	// *** TABLE MODEL METHODS ***
	public int getColumnCount() {
		int n = 0;
		for (int i = 0; i < _columnNames.length; i++)
			if (_columnsVisible[i])
				n++;
		return n;
	}

	public int getRowCount() {
		int n = 0;
		for (int i = 0; i < _data.length; i++)
			if (_rowsVisible[i])
				n++;
		return n;
	}

	public String getColumnName(int col) {
		return _columnNames[getNumber(col, _columnsVisible)];
	}

	public Object getValueAt(int row, int col) {
		return _data[getNumber(row, _rowsVisible)][getNumber(col,
				_columnsVisible)];
	}

	public String getPlotPath(int row) { // CB changed return type to String
		// String path = _condensedList.get(getNumber(row,_rowsVisible));
		// CB to speed up String p[] =
		// _pathnameParts[getNumber(row,_rowsVisible)];
		int index = getNumber(row, _rowsVisible); // CB to speed up
		// CB to speed up String p[] =
		// _pathnameParts[getNumber(row,_rowsVisible)];
		// CB to speed up String path =
		// "/"+p[0]+"/"+p[1]+"/"+p[2]+"//"+p[4]+"/"+p[5]+"/";
		StringBuffer path = new StringBuffer();// CB to speed up
		path.append("/");
		path.append(_pathnameParts[index][0]);// CB to speed up
		path.append("/");
		path.append(_pathnameParts[index][1]);// CB to speed up
		path.append("/");
		path.append(_pathnameParts[index][2]);// CB to speed up
		path.append("//");
		path.append(_pathnameParts[index][4]);// CB to speed up
		path.append("/");
		path.append(_pathnameParts[index][5]);// CB to speed up
		path.append("/");
		// CB to speed up return path;
		return path.toString();
	}
	
	public String getPath(int index) { // Added by Liheng Zhong to fix the problem of retrieving after sorting
		StringBuffer path = new StringBuffer();
		path.append("/");
		path.append(_pathnameParts[index][0]);
		path.append("/");
		path.append(_pathnameParts[index][1]);
		path.append("/");
		path.append(_pathnameParts[index][2]);
		path.append("//");
		path.append(_pathnameParts[index][4]);
		path.append("/");
		path.append(_pathnameParts[index][5]);
		path.append("/");
		return path.toString();
	}

	/**
	 * CB added.
	 * 
	 * @param row
	 * @return
	 */
	public String getBpart(int row) {
		// System.out.println(_pathnameParts[getNumber(row,_rowsVisible)][1]);
		return _pathnameParts[getNumber(row, _rowsVisible)][1];
	}

	private HashMap<String, String> nameToPathMap;
	public List<String> getPathsForNames(Hashtable<String, Object> names){
		if (nameToPathMap == null){
			nameToPathMap = new HashMap<String, String>();
			PathnameTableModel m = (PathnameTableModel) this.clone();
			m.showAllRows();
			for (int row = 0; row < m.getRowCount(); ++row){
				nameToPathMap.put(m.getBpart(row),m.getPlotPath(row));
			}
		}
		ArrayList<String> paths = new ArrayList<String>();
		for(String name: names.keySet()){
			String path = nameToPathMap.get(name);
			if (path != null){	
				paths.add(path);
			}
		}
		return paths;
	}
	
	public String getPathForName(String name){
		return nameToPathMap.get(name);
	}

}
