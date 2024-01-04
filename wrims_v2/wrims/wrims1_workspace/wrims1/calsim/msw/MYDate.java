package calsim.msw;
import java.util.*;

/**
 *
 * @author Joel Fenolio
 * @author Clay Booher (added compareTo)
 *
 */
public class MYDate { // for jdk 1.5+ implements Comparable<MYDate> {

    private String month;
    private Integer year;
    private static Hashtable monthIndex, monthName;

    protected MYDate() {
        if (monthIndex == null || monthName == null) createMonthTables();
        month = null;
        year = null;
    }

    protected MYDate(String m, int y) {
        if (monthIndex == null || monthName == null) createMonthTables();
        month = m.toUpperCase();
        year = new Integer(y);
    }

    protected MYDate (String m, Integer y) {
        if (monthIndex == null || monthName == null) createMonthTables();
        month = m.toUpperCase();
        year = y;
    }

    protected MYDate (int m, int y) {
        if (monthIndex == null || monthName == null) createMonthTables();
        month = monthName.get(new Integer(m)).toString();
        year = new Integer(y);
    }

    protected MYDate (Integer m, Integer y) {
        if (monthIndex == null || monthName == null) createMonthTables();
        month = monthName.get(m).toString();
        year = y;
    }

    private void createMonthTables() {
        monthIndex = new Hashtable(12);
        monthIndex.put("JAN", new Integer(1));
        monthIndex.put("FEB", new Integer(2));
        monthIndex.put("MAR", new Integer(3));
        monthIndex.put("APR", new Integer(4));
        monthIndex.put("MAY", new Integer(5));
        monthIndex.put("JUN", new Integer(6));
        monthIndex.put("JUL", new Integer(7));
        monthIndex.put("AUG", new Integer(8));
        monthIndex.put("SEP", new Integer(9));
        monthIndex.put("OCT", new Integer(10));
        monthIndex.put("NOV", new Integer(11));
        monthIndex.put("DEC", new Integer(12));

        monthName = new Hashtable(12);
        monthName.put(new Integer(1), "JAN");
        monthName.put(new Integer(2), "FEB");
        monthName.put(new Integer(3), "MAR");
        monthName.put(new Integer(4), "APR");
        monthName.put(new Integer(5), "MAY");
        monthName.put(new Integer(6), "JUN");
        monthName.put(new Integer(7), "JUL");
        monthName.put(new Integer(8), "AUG");
        monthName.put(new Integer(9), "SEP");
        monthName.put(new Integer(10), "OCT");
        monthName.put(new Integer(11), "NOV");
        monthName.put(new Integer(12), "DEC");
    }

	protected String getMonth() {
        return month;
    }

    protected int getMonthIndex() {
        Integer index = (Integer) monthIndex.get(month);
        return index.intValue();
    }

    protected int getYearIndex() {
        return year.intValue();
    }

    public Integer getYear() {
        return year;
    }

    public String toString() {
        return new String(month + year.toString());
    }

    void setMonth(String m) {
        month = m.toUpperCase();
    }

    void setMonth(int m) {
        month = monthName.get(new Integer(m)).toString();
    }

    void setYear(Integer y) {
        year = y;
    }

    void setYear(int y) {
        year = new Integer(y);
    }

    /**
     * Compares this with other (month and year only) to determine if they differ, and which is 
     * earlier if they differ.
     * @param other
     * @return -1 is date is earlier than other's date; 0 if
     */
    public int compareTo(MYDate other) {
	    if (getYearIndex() < other.getYearIndex()) {
	    	return -1;
	    } else if (getYearIndex() > other.getYearIndex()) {
	    	return 1;
	    } else {
		    if (getMonthIndex() < other.getMonthIndex()) {
		    	return -1;
		    } else if (getMonthIndex() > other.getMonthIndex()) {
		    	return 1;
		    } else {
		    	return 0;
		    }
	    }
    }
}

