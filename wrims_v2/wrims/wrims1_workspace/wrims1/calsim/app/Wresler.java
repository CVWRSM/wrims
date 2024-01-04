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
//import calsim.gui.*;
//import java.awt.*;
//import java.awt.event.*;
import java.io.*;
//import java.util.*;
//import javax.swing.*;
//import javax.swing.border.*;
/**
 * Converts system input tables to equivalent WRESL files
 * @author Armin Munevar
 * @version ID: Wresler.java, v 0.7 beta, 07/13/99
 */

public class Wresler {
  public static boolean DEBUG = true;

  /**
   * constructor
   */
  public Wresler(InputTableData model) {
		_type = model.getTableName();
		_model = model;
		_model.setComment(" 'LOOKUP', 'TIMESERIES', 'WRESL', and 'NONE' are valid options for most fields.");
  }
  /**
   *
   */
	public void setType(String type) {
		_type = type;
	}
  /**
   *
   */
	public void setData(InputTableData model) {
		_model = model;
	}
  /**
   *
   */
	public String[] readRowData(int index) {
		String[] row;
		row = _model.getRowData(index);
		return row;
	}
  /**
   *
   */
	public boolean same(String s1, String s2) {
		return s1.equalsIgnoreCase(s2);
	}
  /**
   *
   */
	public String[] makeChannelWresl(String[] row) {
		String msg = new String();
		String[] s = new String[6];
		String arc = row[0];
		String mincap = row[1];
		String mincapUnits = row[2];
		String maxcap = row[3];
		String maxcapUnits = row[4];
		String mif = row[5];
		String mifUnits = row[6];
		String desc = row[7];

		if (!same(mincapUnits,"TAF") && !same(mincapUnits,"CFS") ||
        !same(maxcapUnits,"TAF") && !same(maxcapUnits,"CFS") ||
				!same(mifUnits,"TAF") && !same(mifUnits,"CFS")   ) {
    	msg="Invalid Units for Arc: "+arc;
			throw new RuntimeException(msg);
		}
	  try {
		  new Float(mincap);
		  new Float(maxcap);
		} catch (NumberFormatException e) {
     	 	msg="Invalid Min/Max Flow Entry for Arc: "+arc+": "+e.getMessage();
				throw new RuntimeException(msg);
		}

		// new line
		s[0] = new String();
		s[0] = "define " + arc + " {lower " + mincap;
		if (same(mincapUnits,"CFS")) s[0] = s[0] + " upper " + maxcap;
		if (same(mincapUnits,"TAF")) s[0] = s[0] + "*taf_cfs upper " + maxcap;
		if (same(maxcapUnits,"CFS")) s[0] = s[0] + " kind 'FLOW-CHANNEL' units 'CFS'} !" + desc;
		if (same(maxcapUnits,"TAF")) s[0] = s[0] + "*taf_cfs kind 'FLOW-CHANNEL' units 'CFS'} !" + desc;
		if (same(mif,"NONE")) return s;
		// new line
		s[1] = new String();
		if (same(mif,"LOOKUP")) {
			s[1] = "define minflow_" + arc + " {";
		  s[1] = s[1] + "select minflow from minflow where C_arc=" + arc.substring(1) + ",month=month}";
		} else if (same(mif,"TIMESERIES")) {
			s[1] = "define minflow_" + arc + " {";
		  s[1] = s[1] + "timeseries kind 'FLOW-MIN-REQUIRED' units ";
			if (same(mifUnits,"CFS")) s[1] = s[1] + "'CFS'}";
			if (same(mifUnits,"TAF")) s[1] = s[1] + "'TAF' convert 'CFS'}";
		}	else if (same(mif,"WRESL")) {
		}	else {
			try {
				if (new Float(mif)!=null) {
					s[1] = "define minflow_" + arc + " {";
			  	s[1] = s[1] + "value " + mif;
					if (same(mifUnits,"CFS")) s[1] = s[1] + "}";
					if (same(mifUnits,"TAF")) s[1] = s[1] + "*taf_cfs}";
				}
			} catch (NumberFormatException e) {
      	 	msg="Invalid Instream Flow Entry for Arc: "+arc+": "+e.getMessage();
					throw new RuntimeException(msg);
			}
		}
		// new line
		s[2] = new String();
		s[2] = "define " + arc + "_MIF {std kind 'FLOW-MIN-INSTREAM' units 'CFS'}";
		// new line
		s[3] = new String();
		s[3] = "define " + arc + "_EXC {std kind 'FLOW-EXCESS-INSTREAM' units 'CFS'}";
		// new line
		s[4] = new String();
		s[4] = "goal " + arc + "total {" + arc + "=" + arc + "_MIF+" + arc + "_EXC}";
		// new line
		if (!same(mif,"WRESL")) {
			s[5] = new String();
			if (same(mif,"LOOKUP") && same(mifUnits,"TAF"))
				s[5] = "goal " + arc + "minflow {" + arc + "_MIF < minflow_" + arc + "*taf_cfs}";
			else s[5] = "goal " + arc + "minflow {" + arc + "_MIF < minflow_" + arc + "}";
		}
		return s;
	}
  /**
   *
   */
	public String[] makeDeliveryWresl(String[] row) {
		String msg = new String();
		String[] s = new String[2];
		String arc = row[0];
		String dem = row[1];
		String demUnits = row[2];
		String desc = row[3];
		if (!same(demUnits,"TAF") && !same(demUnits,"CFS") ) {
    	msg="Invalid Units for Arc: "+arc+": "+demUnits;
			throw new RuntimeException(msg);
		}
		// new line
		s[0] = new String();
		if (same(dem,"LOOKUP")) {
			s[0] = "define demand_" + arc + " {";
		  s[0] = s[0] + "select demand from demand where D_arc=" + arc.substring(1) + ",month=month}";
		} else if (same(dem,"TIMESERIES")) {
			s[0] = "define demand_" + arc + " {";
		  s[0] = s[0] + "timeseries kind 'DEMAND' units ";
			if (same(demUnits,"CFS")) s[0] = s[0] + "'CFS'}";
			if (same(demUnits,"TAF")) s[0] = s[0] + "'TAF' convert 'CFS'}";
		}
		// new line
		s[1] = new String();
		s[1] = "define " + arc + " {";
		if (same(dem,"NONE")) {
		  s[1] = s[1] + "upper 0 kind 'FLOW-DELIVERY' units 'CFS'} !" + desc;
			return s;
		} else if (same(dem,"TIMESERIES")) {
		  s[1] = s[1] + "upper demand_" + arc + " kind 'FLOW-DELIVERY' units 'CFS'} !" + desc;
		} else if (same(dem,"WRESL")) {
		  s[1] = s[1] + "std kind 'FLOW-DELIVERY' units 'CFS'} !" + desc;
		} else if (same(dem,"LOOKUP") && same(demUnits,"CFS")) {
		  s[1] = s[1] + "upper demand_" + arc + " kind 'FLOW-DELIVERY' units 'CFS'} !" + desc;
		} else if (same(dem,"LOOKUP") && same(demUnits,"TAF")) {
		  s[1] = s[1] + "upper demand_" + arc + "*taf_cfs kind 'FLOW-DELIVERY' units 'CFS'} !" + desc;
		} else {
		  try {
			  if (new Float(dem)!=null) {
		  		if (same(demUnits,"TAF")) s[1] = s[1] + "upper " + dem + "*taf_cfs kind 'FLOW-DELIVERY' units 'CFS'} !" + desc;
		  		if (same(demUnits,"CFS")) s[1] = s[1] + "upper " + dem + " kind 'FLOW-DELIVERY' units 'CFS'} !" + desc;
				}
			} catch (NumberFormatException e) {
      	 	msg="Invalid Demand Entry for Arc: "+arc+": "+e.getMessage();
					throw new RuntimeException(msg);
			}
		}
		return s;
	}
  /**
   *
   */
	public String[] makeReturnWresl(String[] row) {
		String msg = new String();
		String[] s = new String[3];
		String arc = row[0];
		String delarc = row[1];
		String fract = row[2];
		String desc = row[3];
		// new line
		s[0] = new String();
		s[0] = "define " + arc + " {std kind 'FLOW-RETURN' units 'CFS'} !" + desc;
		// new line
		s[1] = new String();
		s[1] = "define rfactor_" + arc + " {";
		if (same(fract,"LOOKUP")) {
		  s[1] = s[1] + "select rfactor from rfactor where R_arc=" + arc.substring(1) + ",month=month}";
		} else if (same(fract,"TIMESERIES")) {
		  s[1] = s[1] + "timeseries kind 'RETURN-FLOW-FACTOR' units 'NONE'}";
		} else if (same(fract,"WRESL")) {
			s[1] = null;
		} else {
		  try {
			  if (new Float(fract)!=null) s[1] = s[1] + "value "+ fract + "}";
			} catch (NumberFormatException e) {
      	 	msg="Invalid Return Fraction Entry for Arc: "+arc+": "+e.getMessage();
					throw new RuntimeException(msg);
			}
		}

		// new line
		if (!same(fract,"WRESL")) {
			s[2] = new String();
			s[2] = "goal returnflow" + arc + " {" + arc + "=rfactor_" + arc + "*" + delarc + "}";
		}
		return s;
	}
  /**
   *
   */
	public String[] makeInflowWresl(String[] row) {
		String msg = new String();
		String[] s = new String[2];
		String arc = row[0];
		String inf = row[1];
		String infUnits = row[2];
		String desc = row[3];
		if (!same(infUnits,"TAF") && !same(infUnits,"CFS") ) {
    	msg="Invalid Units for Arc: "+arc+": "+infUnits;
			throw new RuntimeException(msg);
//      JOptionPane.showMessageDialog(null,msg,"Error",JOptionPane.ERROR_MESSAGE);
		}
		// new line
		s[0] = new String();
		if (same(inf,"LOOKUP")) {
			s[0] = "define inflow" + arc + " {";
		  s[0] = s[0] + "select inflow from inflow where I_arc=" + arc.substring(1) + ",month=month} !" + desc;
			//new line
			s[1] = new String();
			if (same(inf,"LOOKUP")) {
				s[1] = "define " + arc + " {value inflow" + arc;
				if (same(infUnits,"CFS")) s[1] = s[1] + "} !" + desc;
				if (same(infUnits,"TAF")) s[1] = s[1] + "*taf_cfs} !" + desc;
			}
		} else if (same(inf,"TIMESERIES")) {
		  s[0] = "define " + arc + " {timeseries kind 'FLOW-INFLOW' units ";
			if (same(infUnits,"CFS")) s[0] = s[0] + "'CFS'} !" + desc;
			if (same(infUnits,"TAF")) s[0] = s[0] + "'TAF' convert 'CFS'} !" + desc;
		} else if (same(inf,"WRESL")) {
		  s[0] = "define " + arc + "{std kind 'FLOW-INFLOW' units 'CFS'} !" + desc;
		} else {
		  try {
			  if (new Float(inf)!=null) {
		  		s[0] = "define " + arc + "{value " + inf;
					if (same(infUnits,"CFS")) s[0] = s[0] + "} !" + desc;
					if (same(infUnits,"TAF")) s[0] = s[0] + "*taf_cfs} !" + desc;
				}
			} catch (NumberFormatException e) {
      	 	msg="Invalid Inflow Entry for Arc: "+arc+": "+e.getMessage();
					throw new RuntimeException(msg);
			}
		}
		return s;
	}
  /**
   *
   */
	public String[] makeWeightWresl(String[] row) {
		String msg = new String();
		String[] s = new String[2];
		String dvar = row[0];
		String wt = row[1];
		String wtUnits = row[2];
//		String desc = row[3];
		if (!same(wtUnits,"TAF") && !same(wtUnits,"CFS") ) {
    	msg="Invalid Units for Dvar: "+dvar+": "+wtUnits;
			throw new RuntimeException(msg);
		}
		// new line
		s[0] = new String();
		try {
		  if (new Float(wt)!=null) {
		  	s[0] = "[" + dvar + "," + wt;
				if (same(wtUnits,"CFS")) s[0] = s[0] + "],";
				if (same(wtUnits,"TAF")) s[0] = s[0] + "*taf_cfs],";
			}
		} catch (NumberFormatException e) {
     	 	msg="Invalid Weight Entry for Dvar: "+dvar+": "+e.getMessage();
				throw new RuntimeException(msg);
		}
		return s;
	}
  /**
   *
   */
	public String[] makeReservoirWresl(String[] row) {
		String msg = new String();
		String[] s = new String[45];
		String node = row[0];
//		String levels = row[1];
		String[] l = new String[10];
		l[0] = row[2];
		l[1] = row[3];
		l[2] = row[4];
		l[3] = row[5];
		l[4] = row[6];
		l[5] = row[7];
		l[6] = row[8];
		l[7] = row[9];
		l[8] = row[10];
		l[9] = row[11];
		String lunits = row[12];
		String disarc = row[13];
		String desc = row[14];
		int j=1;
		String sz = new String();
		if (!same(lunits,"TAF")) {
    	msg="Invalid Level Units for Node(Only TAF currently allowed): "+node+": "+lunits;
			throw new RuntimeException(msg);
		}

		// new line for each storage level
		int ln = 0;
		boolean foundWresl = false;
		for (int i=0;i<10;i++) {
			j = i+1;
			s[ln] = new String();
			if (l[i].equals("")) break;
			if (same(l[i],"LOOKUP")) {
				s[ln] = "define S" + node + "level" + j + " {";
	  		s[ln] = s[ln] + "select target from res_level where res_num=" + node + ",level=" + j + ",month=month}";
			} else if (same(l[i],"TIMESERIES")) {
				s[ln] = "define S" + node + "level" + j + " {";
	  		s[ln] = s[ln] + "timeseries kind 'STORAGE-LEVEL' units 'TAF'}";
			} else if (same(l[i],"WRESL")) {
	  		foundWresl=true;
			} else {
				try {
					if (new Float(l[i])!=null) {
						s[ln] = "define S" + node + "level" + j + " {";
	  				s[ln] = s[ln] + "value " + l[i] + "}";
					}
				} catch (NumberFormatException e) {
     			msg="Invalid Level Entry for Node: "+node+": "+e.getMessage();
					throw new RuntimeException(msg);
				}
			}
			// new line
			ln++;
			sz = "S" + node + "_" + j;
			s[ln] = new String();
			s[ln] = "define "+sz+" {std kind 'STORAGE-ZONE' units 'TAF'}";
			// new line
			if (foundWresl==false) {
				ln++;
				if (j==1) s[ln] = "goal S"+node+"Zone"+j+" {" + sz + " < S"+node+"level"+j+"}";
				else s[ln] = "goal S"+node+"Zone" + j + " {" + sz + " < S" + node + "level" + j +
		  							"-S" + node + "level" + i + "}";
			}
			ln++;
		}
		// new line
		s[ln] = new String();
		s[ln] = "define S" + node + " {std kind 'STORAGE' units 'TAF'} !" + desc;
		// new line
		ln++;
		sz = "S" + node;
		s[ln] = new String();
		s[ln] = "goal storage" + node + " {" + sz + "=" + sz + "_1";
	  for (int i=1; i<10; i++) {
			if(l[i].equals("")) break;
			s[ln] += "+" + sz + "_" + new Integer(i+1).toString()  ;
		}
		s[ln] += "}";
	  // new line
		ln++;
		s[ln] = new String();
		s[ln] = "define F" + node + " {std kind 'FLOW-SPILL-NON-RECOV' units 'CFS'}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define E" + node + " {lower unbounded kind 'EVAPORATION' units 'CFS'}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define A" + node + " {std kind 'SURFACE-AREA' units 'ACRES'}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define evap_S" + node + " {timeseries kind 'EVAPORATION-RATE' units 'IN'}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define A" + node + "last {select area from res_info given storage=1000*S" + node +
						"(-1) use linear where res_num=" + node + "}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define A" + node + "forward {select area from res_info given storage=1050*S" + node +
						"(-1) use linear where res_num=" + node + "}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define A" + node + "back {select area from res_info given storage=950*S" + node +
						"(-1) use linear where res_num=" + node + "}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define coefev" + node + " {value (A" + node + "forward - A" + node + "back)/" +
						"(100*max(0.01,S" + node + "(-1)))}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "goal area" + node + " {A" + node + "=A" + node + "last+1000*coefev" + node + "*S" + node +
						"-1000*coefev" + node + "*S" + node + "(-1)}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "goal evap" + node + " {E" + node + "*cfs_af=(evap_S" + node + "/24)*A" + node +
						"last+(evap_S" + node + "/24)*A" + node + "}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "define relcap" + node + " {select discharge from res_info given storage=1000*S" +
						node + "(-1) use linear where res_num=" + node + "}";
		// new line
		ln++;
		s[ln] = new String();
		s[ln] = "goal maxrelease" + node + " {" + disarc + "<relcap" + node + "}";
		return s;
	}
  /**
   *
   */
	public String[] makeConnectivityWresl() {
		String[] s = new String[1000];
		String[] row;
		String[] arcsIn = new String[10];
		String[] arcsOut = new String[10];
		String node = new String();
		String storage = new String();
		String desc = new String();
		int j=0;
		int ln=0;
		boolean newnode = false;
		for (int i=0;i<_model.getRowCount();i++) {
		  row = _model.getRowData(i);
			if (row[0]!=null && row[0].compareTo("")!=0 ) {
				if (i>0) {
				  s[ln] = writeConnectivityLine(node,arcsIn,arcsOut,storage,desc);
					ln++;
					j=0;
				}
			  newnode = true;
			}
			if (newnode) {
				arcsIn = new String[10];
				arcsOut = new String[10];
        node = row[0];
				arcsIn[j] = row[1];
				arcsOut[j] = row[2];
				storage = row[3];
				desc = row[4];
				newnode = false;
				j++;
			} else {
				arcsIn[j] = row[1];
				arcsOut[j] = row[2];
				j++;
			}
		}
		s[ln] = writeConnectivityLine(node,arcsIn,arcsOut,storage,desc);
		return s;
	}
  /**
   *
   */
	public String writeConnectivityLine(String node, String[] arcsIn, String[] arcsOut, String stor, String desc) {
		String s = new String();
		s = "goal continuity" + node + " {";
		for (int i=0;i<arcsIn.length;i++) {
			if (arcsIn[i]!=null && arcsIn[i].compareTo("")!=0) s = s + arcsIn[i] + "+";
		}
		s = s.substring(0,s.length()-1);
		s = s + "-";
		for (int i=0;i<arcsOut.length;i++) {
			if (arcsOut[i]!=null && arcsOut[i].compareTo("")!=0) s = s + arcsOut[i] + "-";
		}
		s = s.substring(0,s.length()-1);
		if (stor==null || stor.compareTo("")==0) s = s + "=0} !" + desc;
		else s = s + "-F" + node + "-E" + node + "=" + stor + "*taf_cfs-" + stor + "(-1)*taf_cfs} !" + desc;
		return s;
	}
  /**
   *
   */
	public void saveWresl(String dir) {
		String[] s = new String[1];
		int	ln = 0;
    File file = new File(dir+System.getProperty("file.separator")+_type+"-table.wresl");
    try {
      PrintWriter filewriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			if (same(_type,"CONNECTIVITY")) {
				s = makeConnectivityWresl();
				while (s[ln]!=null) {
					filewriter.println(s[ln]);
					ln++;
				}
			} else {
				ln=_model.getRowCount();
				for (int j=0;j<ln;j++) {
					if (same(_type,"CHANNEL")) s = makeChannelWresl(readRowData(j));
					if (same(_type,"DELIVERY")) s = makeDeliveryWresl(readRowData(j));
					if (same(_type,"RETURN")) s = makeReturnWresl(readRowData(j));
					if (same(_type,"INFLOW")) s = makeInflowWresl(readRowData(j));
					if (same(_type,"RESERVOIR")) s = makeReservoirWresl(readRowData(j));
					if (same(_type,"WEIGHT")) {
						s = makeWeightWresl(readRowData(j));
						if (j==0) s[0] = "Objective obj = {" + s[0];
						if (j==ln-1) s[0] = s[0].substring(0,s[0].length()-1)+"}";
					}
					for (int i=0; i< s.length; i++) {
						if (s[i]!=null)	{
						  if (s[i].compareTo("")!=0) filewriter.println(s[i]);
						}
					}
				}
			}
			filewriter.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
  /**
   *
   */
	private String _type;
	private InputTableData _model;
}


