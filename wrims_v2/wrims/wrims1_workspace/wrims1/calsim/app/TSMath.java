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

/**
	* Timeseries math functionality
 * @author Armin Munevar
 * @version $Id: TSMath.java,v 1.1.2.13 2001/07/12 01:58:32 amunevar Exp $
 */
public class TSMath{
  public static boolean DEBUG = true;
  public static ElementFilter _filter = Constants.DEFAULT_FILTER;
  /**
    * creates a copy of rts and returns it.
    */
  public static TimeSeries createCopy(TimeSeries rts){
    return rts.createSlice(rts.getTimeWindow());
  }
  /**
    * adds the result of the operation between d1 and d2 and stores the
    * result in d1 and returns it.
    * This assumes that d1 and d2 have the same time interval and the same
    * time window. Furthermore no attention is paid to the units, etcetra.
    */
  public static RegularTimeSeries doMath(RegularTimeSeries d1, RegularTimeSeries d2,
		int operationId){
    if ( d1.getTimeInterval().compare(d2.getTimeInterval() ) != 0 )
      throw new RuntimeException("operation between time series of different intervals");
    if ( ! d1.getTimeWindow().isSameAs(d2.getTimeWindow()) )
      throw new RuntimeException("operation between time series of different time windows");
    DataSetIterator dsi1 = d1.getIterator();
    DataSetIterator dsi2 = d2.getIterator();
    while( !dsi1.atEnd() ){
      DataSetElement dse1 = dsi1.getElement();
      DataSetElement dse2 = dsi2.getElement();
      double y = 0.0;
      if ( _filter.isAcceptable(dse1) && _filter.isAcceptable(dse2) ){
	double y1 = dse1.getY(); double y2 = dse2.getY();
	if ( operationId == TimeSeriesMath.ADD )
	  y = y1 + y2;
	else if ( operationId == TimeSeriesMath.SUB )
	  y = y1 - y2;
	else if ( operationId == TimeSeriesMath.MUL )
	  y = y1 * y2;
	else if ( operationId == TimeSeriesMath.DIV )
	  y = y1 / y2;
	else
	  throw new IllegalArgumentException("Unknown operation on time series, Operation Id: " + operationId);
	dse1.setY(y);
	dsi1.putElement(dse1);
      }else {
	dse1.setY(Constants.MISSING_VALUE);
	dsi1.putElement(dse1);
      }
      dsi1.advance(); dsi2.advance();
    }
    boolean unknown = d1.getAttributes().getYUnits().equals("UNKNOWN") ;
    boolean muldivop = operationId == TimeSeriesMath.DIV ||
      operationId == TimeSeriesMath.MUL;

    if ( unknown && !muldivop){
      d1.setAttributes(d2.getAttributes());
    } else if (!unknown && muldivop){
      setYUnits(d1,"NONE");
    }
    return d1;
  }
  /**
    * sets the y units of given data set to yUnits
    */
  public static void setYUnits(DataSet d1, String yUnits){
    DataSetAttr d1attr = d1.getAttributes();
    d1attr.setYUnits(yUnits);
  }
  /**
    * changes a regular time series from CFS to TAF units only if the
    * units in the time series are CFS or UNKNOWN. If units are already in
    * TAF the time series will be returned without changes
    * Note: this changes the time series being passed in.
    */
  public static void cfs2taf(RegularTimeSeries rts){
    String yUnits = rts.getAttributes().getYUnits();
    if ( yUnits.equals("TAF") ) return;
    if ( yUnits.equals("CFS") ) {//|| yUnits.equals("UNKNOWN") ){
      for(DataSetIterator dsi = rts.getIterator();
	  !dsi.atEnd();
	  dsi.advance()){
	DataSetElement dse = dsi.getElement();
	String tmstr = dse.getXString();
	String monstr = tmstr.substring(2,5);
	String yearstr = tmstr.substring(5,9);
	if ( _filter.isAcceptable(dse) ){
	  double fac = getCFS2TAFFactor(monstr,yearstr);
	  dse.setY(fac*dse.getY());
	  dsi.putElement(dse);
	}
      }
      setYUnits(rts,"TAF");
    } else{
      return;
      //      throw new RuntimeException("Unknown Units for cfs2taf conversion: " + yUnits);
    }
  }
  /**
    * changes a regular time series from TAF to CFS units only if the
    * units in the time series are TAF or UNKNOWN. If units are already in
    * CFS the time series will be returned without changes
    * Note: this changes the time series being passed in.
    */
  public static void taf2cfs(RegularTimeSeries rts){
    String yUnits = rts.getAttributes().getYUnits();
    if ( yUnits.equals("CFS") ) return;
    if ( yUnits.equals("TAF") ){// || yUnits.equals("UNKNOWN") ){
      for(DataSetIterator dsi = rts.getIterator();
	  !dsi.atEnd();
	  dsi.advance()){
	DataSetElement dse = dsi.getElement();
	String tmstr = dse.getXString();
	String monstr = tmstr.substring(2,5);
	String yearstr = tmstr.substring(5,9);
	if ( _filter.isAcceptable(dse) ){
	  double fac = getCFS2TAFFactor(monstr,yearstr);
	  dse.setY(dse.getY()/fac);
	  dsi.putElement(dse);
	}
      }
      setYUnits(rts,"CFS");
    } else{
//      throw new RuntimeException("Unknown Units for taf2cfs conversion: " + yUnits);
    }
  }
  /**
    * @param mon the first 3 characters for the month, e.g SEP
    * @param year the year e.g. 1999
    */
  public static double getCFS2TAFFactor(String mon, String year){
    mon = mon.toUpperCase().trim();
    if (mon.length() != 3 ) {
      throw new RuntimeException("Incorrect string for month? : " + mon);
    }
    TimeFactory tf = TimeFactory.getInstance();
    Time tm = tf.createTime("01"+mon+year.trim() + " 0000");
    TimeInterval tiday = tf.createTimeInterval("1day");
    TimeInterval timon = tf.createTimeInterval("1mon");
    Time tm2 = tm.create(tm);
    tm2.incrementBy(timon,1);
    int nvals = (int) tm.getExactNumberOfIntervalsTo(tm2,tiday);
    //if ( mon.equals("FEB") ) nvals = 28; // kludge requested by A. Munevar...:-))
    return nvals/_factor;
  }
  private static double _factor = (1000.0*43560)/(24*60.0*60.0);
}
