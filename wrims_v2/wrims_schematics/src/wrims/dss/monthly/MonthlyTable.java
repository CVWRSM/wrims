package wrims.dss.monthly;

import hec.gfx2d.TimeSeriesDataSet;
import hec.io.TimeSeriesContainer;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import rma.awt.RMACellEditor;
import rma.awt.RmaCellRenderer;
import rma.awt.RmaJTable;
import rma.awt.table.TableExportOptions;

public class MonthlyTable extends RmaJTable implements TableModelListener {
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

	public MonthlyTable(Component parentFrame) {
		super(parentFrame);
		_editMode = false;
		_parentFrame = parentFrame;
		// if (_parentFrame instanceof MonthlyTableFrame)
		// setShowTotalColumn(((MonthlyTableFrame)_parentFrame).showTotalColumn());
		setAddRemoveEnabled(false);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false);
		setCellSelectionEnabled(false);
		TableCellRenderer renderer = new JTableInCellRenderer();
		if (renderer instanceof RmaCellRenderer)
			((RmaCellRenderer) renderer).setHorizontalAlignment(4);
		this.setDefaultRenderer(JTable.class, renderer);
		super.setEditable(false);
		setUseDefaultPrintHeader(false);
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
			return super.getToolTipText(event);
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
		super.exportData(filename, new TableExportOptions());
	}

	public void export(String filename, TableExportOptions exportOptions) {
		super.exportData(filename, exportOptions);
	}

	public void stopEditing() {
		cellEditor = (RMACellEditor) getCellEditor();
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
			super.exportAsXML(bufWriter, title, indent);
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
			super.exportAsHTML(bufWriter, title, indent);
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

	static final Color DISABLED_COLOR = new Color(225, 225, 225);

	protected boolean _editMode;

	private MonthlyTableModel _tableModel;

	private Component _parentFrame;

}
