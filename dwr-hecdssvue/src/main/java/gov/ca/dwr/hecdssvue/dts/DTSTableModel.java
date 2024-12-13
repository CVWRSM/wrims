/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import javax.swing.table.AbstractTableModel;

/**
 * A table model for derived time series
 *
 * @author Nicky Sandhu
 * @version $Id: DTSTableModel.java,v 1.1.4.8 2001/10/23 16:28:19 jfenolio Exp $
 * @see DerivedTimeSeries
 */
public class DTSTableModel extends AbstractTableModel {
    public static String[] tableHeaders = {
        "Operator", "Derived Time Series", "Dvar/Svar", "B part", "C part"
    };

    /**
     *
     */
    public DTSTableModel(DerivedTimeSeries dts) {
        _dts = dts;
    }

    /**
     * Number of data references used in DTS calculations
     */
    public int getRowCount() {
        return _dts.getNumberOfDataReferences();
    }

    /**
     * Number of columns in the table
     */
    public int getColumnCount() {
        return 5;
    }

    /**
     * Returns the name of the column at <i>columnIndex</i>.
     *
     * @param columnIndex the index of column
     * @return the name of the column
     */
    public String getColumnName(int columnIndex) {
        return tableHeaders[columnIndex];
    }

    /**
     * Returns true if the cell at <I>rowIndex</I> and <I>columnIndex</I>
     * is editable.  Otherwise, setValueAt() on the cell will not change
     * the value of that cell.
     *
     * @param rowIndex    the row whose value is to be looked up
     * @param columnIndex the column whose value is to be looked up
     * @return true if the cell is editable.
     * @see #setValueAt
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String dtsname = _dts.getDTSNameAt(rowIndex);
        if (columnIndex > 1) {
            if (dtsname == null) {
                return true;
            } else {
                return dtsname.equals("");
            }
        } else {
            return true;
        }
    }

    /**
     * Returns an attribute value for the cell at <I>columnIndex</I>
     * and <I>rowIndex</I>.
     *
     * @param rowIndex    the row whose value is to be looked up
     * @param columnIndex the column whose value is to be looked up
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return AppUtils.getOperationName(_dts.getOperationIdAt(rowIndex));
        } else if (columnIndex == 1) {
            String dtsname = _dts.getDTSNameAt(rowIndex);
            if (dtsname == null) {
                return "";
            } else {
                return dtsname;
            }
        } else if (columnIndex == 2) {
            String dtsname = _dts.getDTSNameAt(rowIndex);
            if (dtsname != null &&
                !dtsname.equals("")) {
                return "";
            }
            String varType = _dts.getVarTypeAt(rowIndex);
            if (varType == null) {
                return "";
            } else {
                return varType;
            }
        } else if (columnIndex == 3) {
            String dtsname = _dts.getDTSNameAt(rowIndex);
            if (dtsname != null &&
                !dtsname.equals("")) {
                return "";
            }
            String bpart = _dts.getBPartAt(rowIndex);
            if (bpart == null) {
                return "";
            } else {
                return bpart;
            }
        } else if (columnIndex == 4) {
            String dtsname = _dts.getDTSNameAt(rowIndex);
            if (dtsname != null &&
                !dtsname.equals("")) {
                return "";
            }
            String cpart = _dts.getCPartAt(rowIndex);
            if (cpart == null) {
                return "";
            } else {
                return cpart;
            }
        } else {
            throw new InternalError("Invalid column access");
        }
    }

    /**
     * Sets an attribute value for the record in the cell at
     *
     * @param aValue      the new value
     * @param rowIndex    the row whose value is to be changed
     * @param columnIndex the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            int opId = AppUtils.getOperationId((String) aValue);
            _dts.setOperationIdAt(rowIndex, opId);
        } else if (columnIndex == 1) {
            String dtsname = (String) aValue;
            if (dtsname == null) {
                return; // this shouldn't be
            }
            if (dtsname.length() > 0) {
                _dts.setDTSNameAt(rowIndex, dtsname);
            }
        } else if (columnIndex == 2) {
            String varType = (String) aValue;
            _dts.setVarTypeAt(rowIndex, varType);
        } else if (columnIndex == 3) {
            String bpart = (String) aValue;
            _dts.setBPartAt(rowIndex, bpart);
        } else if (columnIndex == 4) {
            String cpart = (String) aValue;
            //System.out.println(cpart);
            _dts.setCPartAt(rowIndex, cpart);
        } else {
            throw new InternalError("Invalid column access");
        }
    }

    /**
     *
     */
    private final DerivedTimeSeries _dts;
}
