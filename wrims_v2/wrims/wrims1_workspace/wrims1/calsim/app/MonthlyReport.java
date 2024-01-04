/*

Copyright (C) 1998, 2000 State of California, Department of Water Resources.

This program is licensed to you under the terms of the GNU General Public
License, version 2, as published by the Free Software Foundation.

You should have received a copy of the GNU General Public License along
with this program; if not, contact Dr. Sushil Arora, below, or the
Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

THIS SOFTWARE AND DOCUMENTATION ARE PROVIDED BY THE CALIFORNIA DEPARTMENT
OF WATER RESOURCES AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE CALIFORNIA DEPARTMENT OF WATER RESOURCES OR ITS
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OR SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

For more information, contact:

Dr. Sushil Arora
California Dept. of Water Resources
Office of State Water Project Planning, Hydrology and Operations Section
1416 Ninth Street
Sacramento, CA  95814
916-653-7921
sushil@water.ca.gov

*/

package calsim.app;
import vista.set.*;
import vista.time.*;
//import java.text.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
//import java.awt.*;
/**
 * Generates formatted text in an array of strings
 * each element of which represents one line of the report
 *
 * @author Nicky Sandhu
 * @version $Id: MonthlyReport.java,v 1.1.2.30 2001/07/12 01:58:29 amunevar Exp $
 */
public class MonthlyReport{
  public static boolean DEBUG = false;
  public static int HTML = 11;
  /**
    * this controls the distance between consecutive values
    */
  public static int desiredSpaceCount = 8;
  static int YEAR_BEGIN=5, YEAR_END=9;
  static int MON_BEGIN=2, MON_END=5;
  public static final String [] months = {"OCT","NOV","DEC","JAN","FEB",
					  "MAR","APR","MAY","JUN","JUL",
					  "AUG","SEP"};
  public boolean _showTotals = true;
  /**
   * creates a monthly report for given data for water year format for years in
   * ascending order
   * @param ds the data set containing the values
   * @param pathname the pathname for the data set
   * @param file the dss file containing the data
   */
  public MonthlyReport(RegularTimeSeries ds, Pathname pathname, String file){
    this(ds,pathname,file,true,false,"Oct",null);
  }
  /**
   * creates a monthly report for given data
   * @param ds the data set containing the values
   * @param pathname the pathname for the data set
   * @param file the dss file containing the data
   * @param _isWaterYear true if water year format is desired
   * @param yearArray An array of integers containing the order in which the years have to
   * be plotted. Each integer is the year value such as 1972.
   */
  public MonthlyReport(RegularTimeSeries ds, Pathname pathname, String file,
		       boolean isWaterYear, boolean isStartMonth, String startMonth, int [] yearArray){
    if ( ds == null )
      throw new IllegalArgumentException("Data set is null");
    if ( ds.getTimeInterval().compare(TimeFactory.getInstance().createTimeInterval("1MON")) != 0 ) {
      throw new IllegalArgumentException("Data set does not have 1 month time interval");
    }
    _ds = ds;
    _pathname = pathname;
    _file = file;
    _isWaterYear = isWaterYear;
    _isStartMonth = isStartMonth;
    _yearArray = yearArray;
    _monInt = monthToInt(startMonth);
    _startMonth = startMonth;

    //
  }

  public int monthToInt(String startMonth) {
    int month = 0;
    for (int i = 0; i < months.length; i++) {
      if (startMonth.equals(months[i])) month = i;
    }
    return month;
  }


  /**
   * This method generates a document for the time series in the constructor.
   * The document has a few styles to highlight headings in bold, min and max
   * in different colors, etcetra. As a first shot all the customization is done for
   * the user but these would be options for the user to set as the design
   * becomes more sophisticated.
   */
  public StyledDocument getStyledDocument(){
    return getStyledDocument(0);
  }
  /**
    *
    */
  public StyledDocument getStyledDocument(int type){
    return appendTo(null,type);
  }
  /**
   * This method generates a document for the time series in the constructor.
   * The document has a few styles to highlight headings in bold, min and max
   * in different colors, etcetra. As a first shot all the customization is done for
   * the user but these would be options for the user to set as the design
   * becomes more sophisticated.
   */
  public StyledDocument appendTo(StyledDocument doc, int type){
    // create empty document
    if ( type == HTML ){
      if ( doc == null ) doc = new HTMLDocument(new StyleSheet());
    } else{
      if ( doc == null ) doc = new DefaultStyledDocument(ReportUtils.createDefaultStyles());
    }
    //
    if ( _pathname.getPart(Pathname.C_PART).equals("STORAGE") ) _showTotals = false;
    if ( AppUtils.useCFS ) _showTotals = false;
    // get styles
//    Style smain = doc.getStyle("main");
    Style s = doc.getStyle("normal"); // normal style
//    Style smin = doc.getStyle("min"); // min item style
//    Style smax = doc.getStyle("max");
    Style sheader = doc.getStyle("header");
    Style mainHeaderStyle = doc.getStyle("main header");
    Style srheader = doc.getStyle("right header");
//    Style waterYearStyle = doc.getStyle("water year style");
    Style dateStyle = doc.getStyle("normal");//doc.getStyle("date style");
    //
    String ls = System.getProperty("line.separator");
    int desiredSpaceCount = ReportUtils.getSpaceCount();
    //
//    Time tm = TimeFactory.getInstance().getTimeInstance();
    // get column sizes for data and total
    int scount = desiredSpaceCount;
    int lineLength = _leftMargin + scount*14;
    // shift from top a little by adding a top margin of 1 line
    ReportUtils.addString(doc,0,ls,sheader);
    // 1st line
    String studyName = _pathname.getPart(Pathname.F_PART);
    ReportUtils.addLine(doc,new int [] { _leftMargin, _leftMargin+7, lineLength-1},new String [] { "STUDY:", studyName, "FILE: "+_file },
			new int [] { ReportUtils.BEGIN_AT, ReportUtils.BEGIN_AT, ReportUtils.END_AT },
			new Style [] { sheader, s, srheader });
    // date line
    ReportUtils.addLine(doc,
			new int[] { lineLength-1 },
			new String [] { new Date().toString() },
			new int [] { ReportUtils.END_AT },
			new Style [] { dateStyle });
    // 2nd line
    ReportUtils.addLine(doc,
			new int [] { _leftMargin },
			new String[] { "Data: " + _pathname.toString() },
			new int [] { ReportUtils.BEGIN_AT },
			new Style [] { srheader });
    // 3rd line
    ReportUtils.addLine(doc,
			new int [] { _leftMargin, _leftMargin+7 },
			new String [] { "Units: ", _ds.getAttributes().getYUnits() },
			new int [] { ReportUtils.BEGIN_AT, ReportUtils.BEGIN_AT},
			new Style [] { sheader, s });
    // 4th line: YEAR -- MONTHS -- TOTAL header
    //
    int ncols = 15;
    int [] dataCols = new int[ncols];
    String [] dataStrs = new String[ncols];
    int [] justifications = new int[ncols];
    Style [] dataStyles = new Style[ncols];
    dataCols[0] = _leftMargin;
    dataCols[1] = _leftMargin + 14;
    int dataStart = dataCols[1];
    for(int i=2; i < ncols; i++) {
      dataCols[i] = dataStart + (i-2)*scount;
    }
    for(int i=0; i < ncols; i++) {
      dataStrs[i] = "";
      justifications[i] = ReportUtils.END_AT;
      dataStyles[i]= mainHeaderStyle;
    }
    //
    dataStyles[0] = s;
    dataStrs[0] = "";
    justifications[0] = ReportUtils.BEGIN_AT;
    //
    dataStrs[1] = "YEAR";
    dataCols[1] = _leftMargin;
    dataStyles[1] = mainHeaderStyle;
    justifications[1] = ReportUtils.BEGIN_AT;
    for(int i=0; i < 12; i++) {dataStrs[i+2] = getMonthStr(i);}
    if (_showTotals) {
      dataStrs[dataStrs.length-1] = "TOTAL";
      dataCols[dataCols.length-1]++;
    } else {
      dataStrs[dataStrs.length-1] = "";
      dataCols[dataCols.length-1]++;
    }
    ReportUtils.addLine(doc,dataCols, dataStrs, justifications, dataStyles);
    //
    DataSetIterator dsi = _ds.getIterator();
    if ( dsi.atEnd() ) throw new RuntimeException("No available data for " + _pathname);

    // for each of the following years get min/max and total
    // chop the line into before min/max, between min/max and after min/max and of course
    // min and max itself. Also store these min/max/avg values.
    // Calculate min and max of
    // all the totals and color them accordingly and add them to the document.
    int yearCount = 0;
    int totalMonths = 0;
    double totval = 0.0;
    double [] month_data = new double[12];
    int[] month_cnt = new int[13];
    double []
      month_mins = new double[13], // monthly minimum's over the whole period & the minimum of totals
      month_maxs = new double[13], // monthly maximum's over the whole period & the maximum of totals
      month_avgs = new double[13], // monthly sums's over the whole period & the average of totals
      month_vals = new double[13];

    for(int i=0; i < month_mins.length; i++){
      month_vals[i] = Float.MIN_VALUE;
      month_mins[i] = Float.MAX_VALUE;
      month_maxs[i] = -Float.MAX_VALUE;
      month_avgs[i] = 0.0;
      month_cnt[i] = 0;
    }

    while ( true ){
      // get next 12 elements and figure out the min/max/average
      // if any missing put a blank line and continue.
      double sumVal = 0.0;
      String yearStr = getYear(yearCount);
      if ( yearStr == null ) break; // end of year listing
      yearCount++;
  	  // get month data values
      fillMonthlyValueArray(month_data, yearStr, dsi);
      // get sum of month values
      int numberMissing = 0;
      for(int i=0; i< month_data.length; i++){
				if ( month_data[i] != Float.MIN_VALUE) {
					totalMonths++;
					month_vals[i] = month_data[i];
					month_mins[i] = Math.min(month_mins[i],month_data[i]);
			 		month_maxs[i] = Math.max(month_maxs[i],month_data[i]);
					month_avgs[i] += month_data[i];
	  			sumVal += month_data[i];
			    month_cnt[i]++;
				} else{
          month_vals[i] = Float.MIN_VALUE;
	  			numberMissing++;
				}
      }
      //
      month_vals[12] = sumVal;
      if ( numberMissing == 12 ) continue;
      addValueLine(doc,dataCols, dataStrs, justifications, dataStyles, month_vals, yearStr);
      // also update the min/max/sum for the totals
      month_mins[12] = Math.min(month_mins[12],sumVal);
      month_maxs[12] = Math.max(month_maxs[12],sumVal);
      month_avgs[12] += sumVal;
      totval += sumVal;
    }
    // draw a line before writing out these statistics
    ReportUtils.addLine(doc, new int [] { _leftMargin, lineLength-4 },
			new String [] { "","" },
			new int [] { ReportUtils.BEGIN_AT, ReportUtils.BEGIN_AT},
			new Style[]{ s,mainHeaderStyle});

    // calculate the averages
    for(int i=0; i < month_avgs.length; i++) {
      if ( month_avgs[i] != 0.0) month_avgs[i] /= month_cnt[i];
    }
	Integer monthI = new Integer(totalMonths);
	double monthf = monthI.floatValue();
	double yearf = monthf/12.0f;
    month_avgs[12] = totval/yearf;
    addValueLine(doc,dataCols, dataStrs, justifications, dataStyles, month_avgs, "AVG:");

    // write out mins
    for(int i=0; i < month_mins.length; i++) {
      if (month_mins[i] == Float.MAX_VALUE) month_mins[i] = Float.MIN_VALUE;
    }
    addValueLine(doc,dataCols, dataStrs, justifications, dataStyles, month_mins, "MIN:");

    // write out maxs
    for(int i=0; i < month_maxs.length; i++) {
      if (month_maxs[i] == -Float.MAX_VALUE) month_maxs[i] = Float.MIN_VALUE;
    }
    addValueLine(doc,dataCols, dataStrs, justifications, dataStyles, month_maxs, "MAX:");
    //
    doc.setLogicalStyle(doc.getLength()-1,s);
    return doc;
  }
  /**
    *
    */
  public void fillMonthlyValueArray(double [] month_vals, String yearStr, DataSetIterator dsi){
    //
    for(int i=0; i< month_vals.length; i++) month_vals[i] = Float.MIN_VALUE;
    //
    DataSetIterator dsi2 = positionIteratorAt(Integer.parseInt(yearStr), _isWaterYear, dsi);
    if ( dsi2 == null )
      return;
    else
      dsi = dsi2;
    Time stime = AppUtils.getCurrentProject().getTimeWindow().getStartTime();
    // get the next 12 values and if any missing set the flag so as to skip writing
    // out the values and taking them in min/max/avg calculations
    for(int i=0; i < month_vals.length; i++){
	  //
      if (dsi.atEnd())
		  break;
      // check if time is before start of time window
      if (stime.create(dsi.getElement().getXString()).compare(stime) < 0 ){
				dsi.advance();
				continue;
      }
      // check if it really is at the month in question
      String cmon = dsi.getElement().getXString().substring(MON_BEGIN,MON_END);
      if (!cmon.equals(getMonthStr(i))) 
    	  continue;
      DataSetElement dse = dsi.getElement();
      if (Constants.DEFAULT_FILTER.isAcceptable(dse) ){
			month_vals[i] = dsi.getElement().getY();
			dsi.advance();
      } else {
			month_vals[i] = Float.MIN_VALUE;
			dsi.advance();
      }
    }
  }
  /**
    *
    */
  public String getMonthStr(int i){
    if ( _isWaterYear ){
      return months[i];
     } else if (_isStartMonth) {
	  return months[(i+_monInt)%12];
     } else {
      return months[(i+3)%12];
    }
  }
  /**
    *
    */
  public static String[] getMonths() {
    return months;
  }
  /**
    * gets the year at the i'th position
    */
  public String getYear(int i){
    int yr=0;
    if ( _yearArray == null ){
      TimeWindow tw = AppUtils.getCurrentProject().getTimeWindow();
      int sy = 1921, ey=1998;
      if ( tw != null ){
	    String startYear = tw.getStartTime().toString().substring(YEAR_BEGIN,YEAR_END);
	    sy = Integer.parseInt(startYear);
	    String beginMonth = tw.getStartTime().toString().substring(MON_BEGIN,MON_END);
	    if ( !beginMonth.equals(getMonthStr(0)) && _isWaterYear){
	      sy--; // adjust for partially available water year
	    }
	    String endYear = tw.getEndTime().toString().substring(YEAR_BEGIN,YEAR_END);
	    ey = Integer.parseInt(endYear);
      }
      int year = sy + i;
      if ( year > ey ){
	    yr=-1;
       } else {
	    yr = year;
      }
     } else{
      if ( i < _yearArray.length ) yr = _yearArray[i];
      else yr = -1;
    }
    if ( yr == -1 ) return null; // signals end of years
    if ( _isWaterYear ) {
      yr++; // adjust for water year
    }
    if (_isStartMonth) {
	  if (_startMonth.equals("OCT") || _startMonth.equals("NOV") || _startMonth.equals("DEC")) {
		  yr++;
	  }
    }
    return Integer.toString(yr);
  }
  /**
    *
    */
  void addValueLine(StyledDocument doc, int [] dataCols, String [] dataStrs, int [] justifications, Style [] dataStyles,
		    double [] vals, String yearStr){
    //
    Style snormal = doc.getStyle("normal"),
      smin = doc.getStyle("min"),
      smax = doc.getStyle("max"),
      sheader = doc.getStyle("header"),
      waterYearStyle = doc.getStyle("water year style");
    //
    dataStrs[0] = "";
    dataStyles[0]= snormal;
    dataCols[0] = _leftMargin;
    dataStrs[1] = yearStr;
    dataStyles[1] = waterYearStyle;
    // fill in values & calculate min/max
    double minVal = Float.MAX_VALUE;
    double maxVal = -Float.MAX_VALUE;
    double totVal = 0.0;
    for(int i=0; i<12;i++){
      dataStrs[i+2] = vals[i] == Float.MIN_VALUE ? "----" : ReportUtils.format(vals[i]);
      if ( vals[i] == Float.MIN_VALUE ) continue;
      minVal = Math.min(minVal, vals[i] );
      maxVal = Math.max(maxVal, vals[i] );
      totVal += vals[i];
    }
    if (_showTotals){
	  dataStrs[dataStrs.length-1] = ReportUtils.format(vals[12]);
      dataStyles[dataStyles.length-1] = sheader;
    } else {
      dataStrs[dataStrs.length-1] = "";
      dataStyles[dataStyles.length-1] = sheader;
    }
    // fill in styles
    for(int i=0; i < 12; i++){
      if ( vals[i] == Float.MIN_VALUE) {
	      dataStyles[i+2]=snormal;
	      continue;
      }
      if ( vals[i] == minVal ) dataStyles[i+2] = smin;
      else if ( vals[i] == maxVal ) dataStyles[i+2] = smax;
      else dataStyles[i+2] = snormal;
    }
    ReportUtils.addLine(doc,dataCols, dataStrs, justifications, dataStyles);
  }
  /**
    * returns the data set iterator set to the given year's beginning or null
    * to indicate that the given year was not found
    */
  DataSetIterator positionIteratorAt(int year, boolean isWaterYear, DataSetIterator dsi){
    String yearStr= null;
    String monStr = null;
//    int eqvYear = year;
    // get string
    if ( isWaterYear ){
      yearStr = Integer.toString(year-1);
      monStr = "OCT";
//      eqvYear = year-1;
    } else if (_isStartMonth && _startMonth.equals("OCT") || _startMonth.equals("NOV") || _startMonth.equals("DEC")) {
      yearStr = Integer.toString(year-1);
      monStr = _startMonth;
//      eqvYear = year-1;
    } else {
      yearStr = Integer.toString(year);
      monStr = "JAN";
//      eqvYear = year;
    }
    boolean forwardSearch = true;
    if (dsi.atEnd())
    	dsi.retreat();
    DataSetElement dse = dsi.getElement();
    String tmStr = dse.getXString();
    int yearVal = Integer.parseInt(tmStr.substring(YEAR_BEGIN,YEAR_END));
    if ( yearVal < year ){
      forwardSearch = true;
      if (dsi.atEnd())
    	  return null;
    } else if ( yearVal > year ){
      forwardSearch = false;
      if (dsi.atStart()) 
    	  return null;
    } else { // assume at right position as dsi should have been positioned so before
      if (dsi.atEnd()) 
    	  return null;
      return dsi;
    }
    //
    while(true){
      if ( dsi.atEnd() ) break;
      dse = dsi.getElement();
      tmStr = dse.getXString();
      String ys = tmStr.substring(YEAR_BEGIN,YEAR_END);
      String ms = tmStr.substring(MON_BEGIN,MON_END);
      if (ys.equals(yearStr) && ms.equals(monStr)){
	      return dsi;
      } else {
	      if ( forwardSearch) dsi.advance();
	      else {
	        if ( dsi.atStart() ) break;
	        dsi.retreat();
	      }
      }
    }
    return null;
  }
  /**
   *
   */
  public static void test(String [] args){
    Group g = AppUtils.openDSSFile(args[0]);
    DataReference ref = AppUtils.getDataReference(g,args[1],args[2],null);
    new MonthlyReport((RegularTimeSeries) ref.getData(), ref.getPathname(), ref.getFilename());
  }
  /**
   *
   */
  public int getMonthIndex(String mon){
    mon = mon.toUpperCase();
    int index = 0;
    while(index < months.length){
      if ( mon.startsWith(months[index])) return index;
      index++;
    }
    throw new IllegalArgumentException("Month: " + mon + " is invalid");
  }
  /**
   *
   */
  private RegularTimeSeries _ds;
  private Pathname _pathname;
  private String _file;
  private boolean _isWaterYear, _isStartMonth;
  private int [] _yearArray;
  private int _leftMargin = 4;
  private int _monInt = 0;
  private String _startMonth;
}
