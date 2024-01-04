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
import java.io.*;
//import java.util.*;
import java.awt.Color;
import vista.app.*;
import vista.graph.*;
import vista.set.*;
import vista.db.dss.*;
//import vista.time.*;
/**
 * Class that produces WSI-DI function from input WSI-DI data based on least squares approx
 * @author Armin Munevar
 * @version $Id: WsiDiGenerator.java,v 1.1.2.8 2001/07/16 20:50:17 amunevar Exp $
 */

public class WsiDiGenerator {
  public static boolean DEBUG = true;
  public static double STEP =500.0;
  public static double LOOKAHEAD = 1.0*STEP;
  public static double MULT = 1.00; // multiplier for std deviation

  /**
   * constructor
   */
  public WsiDiGenerator(String name, String wsiVar, String diVar, double wsiMax) {
		double wsiMin = 0.0;
		double diMin = STEP;
		double diMax = 0.0;
		setName(name);
		setWsiVariable(wsiVar);
		setDiVariable(diVar);
		setDiRange(diMin,diMax);
		setWsiRange(wsiMin,wsiMax);
		setNumberOfSegments();
    setDefaultData();
  }
  /**
   * constructor
   */
  public WsiDiGenerator() {
  }

  /**
   * set name
   */
  public void setName(String name) {
		_name = name;
  }
  /**
   * get name
   */
  public String getName() {
		return _name;
  }
  /**
   * set wsi variable
   */
  public void setWsiVariable(String wsiVar) {
		_wsiVar = wsiVar;
  }
  /**
   * get wsi variable
   */
  public String getWsiVariable() {
		return _wsiVar;
  }
  /**
   * set di variable
   */
  public void setDiVariable(String diVar) {
		_diVar = diVar;
  }
  /**
   * get di variable
   */
  public String getDiVariable() {
		return _diVar;
  }
  /**
   * set wsi range
   */
  public void setWsiRange(double vmin, double vmax) {
		_wsiMin = vmin;
		_wsiMax = vmax;
  }
  /**
   * get wsi range
   */
  public double[] getWsiRange() {
		double range[] = {_wsiMin, _wsiMax};
		return range;
  }
  /**
   * set di range
   */
  public void setDiRange(double vmin, double vmax) {
		_diMin = vmin;
		_diMax = vmax;
  }
  /**
   * get di range
   */
  public double[] getDiRange() {
		double range[] = {_diMin, _diMax};
		return range;
  }
  /**
   * set number of segments
   */
  public void setNumberOfSegments() {
		Double fseg = new Double(_wsiMax/STEP);
		_nseg = fseg.intValue();
  }
  /**
   * get number of segments
   */
  public int getNumberOfSegments() {
		return _nseg;
  }

  /**
   * count data points
   */
  public int countDataPoints(double[] array) {
		int ndata=0;
		for ( int i=0;i<array.length;i++ ) {
			if (array[i] > 0.1 ) ndata++;
		}
		return ndata;
  }
  /**
   * set number of data points
   */
  public void setNumberOfDataPoints(int ndata) {
		_ndata=ndata;
  }

  /**
   * get number of data points
   */
  public int getNumberOfDataPoints() {
		return _ndata;
  }

  /**
   * set run directory
   */
  public void setRunDirectory(String fname) {
		File file = new File(fname);
		File dir = new File(file.getParent());
	  _dir = dir.getParent()+File.separator+"run";
  }

  /**
   * set input data
   */
  public void setInputData(double[][] datain) {
		_datain = datain;
  }
  /**
   * get input data
   */
  public double[][] getInputData() {
		return _datain;
  }
  /**
   * get input data
   */
  public double[] getInputWsi() {
		double wsi[] = new double[_ndata];
		for (int i=0;i<_ndata;i++) {
			wsi[i] = _datain[i][0];
		}
		return wsi;
  }
  /**
   * get input data
   */
  public double[] getInputDi() {
		double di[] = new double[_ndata];
		for (int i=0;i<_ndata;i++) {
			di[i] = _datain[i][1];
		}
		return di;
  }
  /**
   * loads the WSI and DI data from a DSS file
   */
  public void load(String fname) {
//		String msg = new String();
		double[] wsi, di;
		double[][] tmpdata, data;
		double wsiMax=0.0;
		double wsiMin=_wsiMax; //just some large number
		double diMin=_wsiMax*100.0; //just some large number
		double diMax=0.0;
		int wcount = 0;
		int dcount = 0;
		// create a group of the proper file
		Group g = DSSUtil.createGroup("local",fname);
		Group gWsi = Group.createGroup(g);
		Group gDi = Group.createGroup(g);
		// get data for wsi and di
		gWsi.filterBy(true,new PathPartPredicate("^"+getWsiVariable()+"$",Pathname.B_PART));
    if (gWsi.getNumberOfDataReferences()!=1)
		  throw new RuntimeException("No WSI Variable '"+ getWsiVariable() +"' in DSS File!");
		gDi.filterBy(true,new PathPartPredicate("^"+getDiVariable()+"$",Pathname.B_PART));
    if (gDi.getNumberOfDataReferences()!=1)
		  throw new RuntimeException("No DI Variable '"+ getDiVariable() +"' in DSS File!");
		DataReference drWsi = gWsi.getDataReference(0);
		DataReference drDi = gDi.getDataReference(0);
		DataSet dsWsi = drWsi.getData();
		DataSet dsDi = drDi.getData();
		wsi = SetUtils.createYArray(dsWsi);
		di = SetUtils.createYArray(dsDi);
		int ndata = countDataPoints(wsi);
		// ficticious elements are removed
		// last two wsi and first di elements are removed
		ndata = ndata - 2;
		tmpdata = new double[ndata][2];
		for (int i=0;i<wsi.length;i++) {
			if (wsi[i] > 0.1 && wcount < ndata) {
				tmpdata[wcount][0] = wsi[i];
				wcount++;
				if (wsi[i] > wsiMax ) wsiMax=wsi[i];
				if (wsi[i] < wsiMin ) wsiMin=wsi[i];
			}
			if (di[i] > 0.1 && dcount < ndata ) {
				if (dcount > 0 ) {
					tmpdata[dcount-1][1] = di[i];
					dcount++;
					tmpdata[dcount-1][1] = di[i];
					dcount++;
					if (di[i] < diMin ) diMin=di[i];
					if (di[i] > diMax ) diMax=di[i];
				}	else {
					dcount++;
				}
			}
		}
		dcount=dcount-1;
		if (wcount!=dcount) throw new RuntimeException("Data Mismatch! Wsi Values: "+ wcount +" Di Values: "+ dcount);
		if (wcount!=ndata) throw new RuntimeException("Data Mismatch! Data Values: "+ ndata +" Wsi Values: "+ wcount);
		setWsiRange(wsiMin,wsiMax);
		setDiRange(diMin,diMax);
		setNumberOfDataPoints(ndata);
		data = new double[ndata][2];
		for (int i=0;i<ndata;i++) {
			data[i][0] = tmpdata[i][0];
			data[i][1] = tmpdata[i][1];
		}
		setInputData(data);
  }
  /**
   * save data to a file
   */
	public void save(double[][] data) {
		try {
//			String sep = File.separator;
			String fname = _dir + File.separator + "lookup" + File.separator + "wsi_di_" + getName() + ".table";
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fname)));
			pw.println("wsi_di_" + getName());
			pw.println("wsi     di");
			for (int i = 0; i <= _nseg;i++) {
				pw.println(data[i][0] + " " + data[i][1]);
			}
			pw.close();
		} catch (IOException e) {
			System.out.println("Cannot save wsi-di file:"+e.getMessage());
		}
	}

  /**
   * plot data and generated function
   */
  public void plot(double[][] datain, double[][] dataout) {
		double[] xin = new double[_ndata];
		double[] yin = new double[_ndata];
		double[] xout = new double[_nseg+1];
		double[] yout = new double[_nseg+1];
		for (int i=0; i<_ndata;i++) {
   		xin[i] = datain[i][0];
			yin[i] = datain[i][1];
   	}
		for (int i=0; i<=_nseg;i++) {
   		xout[i] = dataout[i][0];
			yout[i] = dataout[i][1];
   	}
		DefaultDataSet dsin = new DefaultDataSet("Input",xin,yin);
		DefaultDataSet dsout = new DefaultDataSet("Output",xout,yout);
		Curve crvin = CurveFactory.createCurve(dsin,AxisAttr.BOTTOM,AxisAttr.LEFT,"Input");
		Curve crvout = CurveFactory.createCurve(dsout,AxisAttr.BOTTOM,AxisAttr.LEFT,"Output");
		crvout.setDrawLines(true);
		crvout.setLineThickness(15);
		crvout.setForegroundColor(Color.red);
		crvout.setDrawSymbol(false);
		crvin.setSymbol(SymbolFactory.createUprightTriangle(false,Color.green,2));
		Plot pl = new Plot();
		pl.addTitle("WSI-DI Generated Curve "+getName());
		pl.add(crvin);
		pl.add(crvout);
		pl.getAxis(AxisAttr.BOTTOM).setAxisLabel("Water Supply Index - TAF");
		pl.getAxis(AxisAttr.LEFT).setAxisLabel("Demand Index TAF");
		Graph gr = new Graph();
		gr.add(pl);
		DataGraph dg = new DataGraph(gr,"WSI-DI Generated Curve",false);
		dg.setLocation(100,100);
		dg.setVisible(true);
		dg.setSize(600,400);
		// for data tables only
//		DataReference drin = new DefaultReference("local","wsi-di file","/CALSIM/WSI-DI-"+getName()+"/INPUT////",dsin);
//		DataReference drout = new DefaultReference("local","wsi-di file","/CALSIM/WSI-DI-"+getName()+"/OUTPUT////",dsout);
//		DataTable dtin = new DataTable(drin);
//		DataTable dtout = new DataTable(drout);
  }
  /**
   * sets initial WSI and DI data with 1:1 slope
   */
  public void setDefaultData() {
		double data[][];
		double value = 0.0;
		data = new double[_nseg+1][2];
		for (int i=0;i<=_nseg;i++) {
			data[i][0] = value;
			data[i][1] = value;
			value = value +	STEP;
		}
		setNumberOfDataPoints(_nseg+1);
		setInputData(data);
  }
  /**
   *
   */
  public double getStandardDev(double[][] data) {
		double[] wsiin, diin;
		double wsi0, di0;
		double slope = 0.0;
		double dist = 0.0;
		double sumdist = 0.0;
		double sumdist2 = 0.0;
		double stdev = 0.0;
		int npoints=0;
		wsiin = getInputWsi();
		diin = getInputDi();
		for (int i=0; i<_nseg;i++) {
			wsi0 = data[i][0];
			di0 = data[i][1];
			for (int j=0; j<_ndata;j++) {
				if (wsiin[j] > wsi0 && wsiin[j] < wsi0 + STEP) {
 					npoints++;
 					slope = getSlope(wsi0,di0,0.0);
 					dist = diin[j] - (di0 + slope*(wsiin[j] - wsi0));
 					sumdist = sumdist + dist;
 					sumdist2 = sumdist2 + dist*dist;
				}
			}
    }
		if (npoints > 0) stdev = Math.sqrt((npoints*sumdist2 - sumdist*sumdist)/(npoints*(npoints-1)));
		else stdev = 0.0;
		return stdev;
  }
  /**
   *
   */
  public double getSlope(double wsi0, double di0, double offset) {
		double[] wsiin, diin;
		double slope, delwsi, sumdelwsi, sumdiwsi, sumwsi2; //, diend;
		double wsiend = wsi0 + STEP;
		int npoints=0;
		wsiin = getInputWsi();
		diin = getInputDi();
		slope = 0.0;
		delwsi = 0.0;
		sumdelwsi = 0.0;
		sumdiwsi = 0.0;
		sumwsi2 = 0.0;
		if (wsi0 < (0.33*(_wsiMax-_wsiMin)+_wsiMin)) {
			offset *= 2.0;
		} else if (wsi0 < (0.5*(_wsiMax-_wsiMin)+_wsiMin)) {
			offset *= 1.0;
		} else {
			offset *= 0.0;
		}
		for (int i=0; i<_ndata;i++) {
    	if (wsiin[i] > wsi0 && wsiin[i] <= wsiend + LOOKAHEAD) {
				//System.out.println(di0 + " " + diin[i]);
				//if (offset != 0.0) {
					//if (diin[i] < 2.0*di0) {
						npoints++;
	 					delwsi = wsiin[i] - wsi0;
						sumdelwsi = sumdelwsi + wsiin[i] - wsi0;
						sumdiwsi = sumdiwsi + (diin[i]-offset)*delwsi;
						sumwsi2 = sumwsi2 + delwsi*delwsi;
					//}
				//}
			}
    }
		if (npoints > 1 ) slope = (sumdiwsi - di0*sumdelwsi)/sumwsi2;
		if (npoints <= 1 ) slope = 0.0;
		if (slope < 0.0)	slope = 0.0;
		return slope;
  }
  /**
   *
   */
  public double[] getFunctionPair(double wsi0, double di0, double offset) {
		double[] pair = new double[2];
		double diend, slope;
		double wsiend = wsi0 + STEP;
		slope = getSlope(wsi0,di0,offset);
		diend = di0 + slope*(wsiend - wsi0);
		if (wsi0 < 1.0) diend=_diMin; // set first di equal to the min of the set
		pair[0] = wsiend;
		pair[1] = diend;
		return pair;
  }
  /**
   *
   */
  public double[][] getAllFunctionPairs() {
		double[][] data = new double[_nseg+1][2];
		double wsi0 = 0.0;
		double di0 = 0.0;
//		double slope, offset, stdev;
		double offset, stdev;
		data[0][0] = wsi0;
		data[0][1] = di0;
		for (int i=1;i<=_nseg;i++) {
			data[i] = getFunctionPair(wsi0,di0,0.0);
		 	wsi0 = data[i][0];
		 	di0 = data[i][1];
		}
		stdev = getStandardDev(data);
		offset = MULT*stdev;

		wsi0 = 0.0;
		di0 = 0.0;
		data[0][0] = wsi0;
		data[0][1] = di0;
		for (int i=1;i<=_nseg;i++) {
			data[i] = getFunctionPair(wsi0,di0,offset);
		 	wsi0 = data[i][0];
		 	di0 = data[i][1];
		}
		return data;
  }

  /**
   * execute
   */
  public void execute() {
		double[][] output;
		double[][] input;
		input	= getInputData();
		output = getAllFunctionPairs();
		save(output);
//J		plot(input,output);

  }

  /**
   * main method
   */
  public static void main(String args[]) {
//		if (args.length > 0) {
		WsiDiGenerator wdg = new WsiDiGenerator("cvp_s","wsi_act_cvp_s","di_act_cvp_s",15000.0);
		wdg.setRunDirectory("d:\\studies\\d1485_062001b6\\dss\\d1485dv.dss");
		wdg.execute();
		wdg.load("d:\\studies\\d1485_062001b6\\dss\\d1485dv.dss");
		wdg.execute();
//		} else {
//    	WsiDiGenerator wdg = new WsiDiGenerator("xxx");
//			wdg.load(args[0]);
//			wdg.execute();
//		}
  }

  /**
   *
   */
//	private String _fname, _name, _dir, _diVar, _wsiVar;
	private String _name, _dir, _diVar, _wsiVar;
	private	double[][] _datain;
	private int _ndata;
	private int _nseg;
  private double _diMin, _diMax, _wsiMin, _wsiMax;
}


