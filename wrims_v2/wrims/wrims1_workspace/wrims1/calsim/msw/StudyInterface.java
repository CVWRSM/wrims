package calsim.msw;
import vista.set.*;
import vista.time.*;
//not visible import vista.db.dss.DSSDataWriter;
import vista.db.dss.DSSUtil;
import java.util.*;
import javax.swing.*;

/**The StudyInterface class contains the information and methods necessary to retrieve,
distribute or lump, and store output from one study to be used as input to the next.*/

public class StudyInterface {
//	final boolean DEBUG = true;
	private boolean _transferDebug;
    ReferenceOperator[] refOp;
    String[] toFile;
    String[][] pnString;

    public StudyInterface(String dvFile, String dvTS, String intDetailsFile, boolean debug) {
        System.out.println("Generating data transfer interface.");
        Vector siDetails = new Vector();
        Vector loc = new Vector();
        MSWUtil.parseFile(intDetailsFile, siDetails, loc);
        int ls = loc.size();
        toFile = new String[ls];
        pnString = new String[ls][];
        Pathname[][] pn = new Pathname[ls][];
        refOp = new ReferenceOperator[ls];

        loc.addElement(new Integer(siDetails.size()));

        //Variables needed for the following loop.
        Integer index0, index1;
        int j0, j1, numRef;
        String op[], svTS, bPart, cPart, fields[], distBPart[];
        DataReference rIn[];
        Group g = DSSUtil.createGroup("local",dvFile);

        for (int i = 0; i < ls; i++) {
            index0 = (Integer) loc.elementAt(i);
            j0 = index0.intValue();
            index1 = (Integer) loc.elementAt(i+1);
            j1 = index1.intValue();
            op = (String[]) siDetails.elementAt(j0);
            if (!MSWUtil.position || MSWUtil.ndv == 1 || !op[1].toUpperCase().endsWith("DV.DSS")) toFile[i] = op[1];
            else if (MSWUtil.position) {
                String dvname = op[1];
                int dir = dvname.lastIndexOf("\\");
                String dirname = dvname.substring(0,dir+1);
                toFile[i] = new String(dirname + "POSITION" + new Integer(MSWUtil.dvnum+1).toString() + "DV.DSS");
			}
			System.out.println("Interface to File: "+toFile[i]);
            svTS = op[2];

            numRef = j1-j0-1;
            pnString[i] = new String[numRef];
            pn[i] = new Pathname[numRef];
            distBPart = new String[numRef];
            rIn = new DataReference[numRef];
            for (int j = 0; j < numRef; j++) {
              fields = (String[]) siDetails.elementAt(j+j0+1);
              bPart = fields[0];
              try {
                rIn[j] = createDataReference(g, new String("^"+bPart+"$"));
                pn[i][j] = Pathname.createPathname(rIn[j].getPathname());
                bPart = fields[1].substring(0, fields[1].indexOf("/"));
                cPart = fields[1].substring(fields[1].indexOf("/") + 1);
                pn[i][j].setPart(Pathname.B_PART, bPart);
                pn[i][j].setPart(Pathname.C_PART, cPart);
                if (fields.length > 2) distBPart[j] = new String("^" + fields[2] + "$");
              } catch (NullPointerException npe) {
                  JOptionPane.showMessageDialog(null,"B-Part: " + bPart + " not found in "
                      + dvFile, "Transfer problem",JOptionPane.ERROR_MESSAGE);
                  return;
			  }
            }
            if (dvTS.equalsIgnoreCase(svTS)) {
                if (op.length > 3) {
                    if (op[3].equalsIgnoreCase("INIT")) refOp[i] = new InitOperator(rIn);
                } else refOp[i] = new TransferOperator(rIn);
            }
            else if (dvTS.equalsIgnoreCase("1MON") && svTS.equalsIgnoreCase("1DAY")) {
                Group gDist = DSSUtil.createGroup("local", op[3]);
                DataReference[] rDist = new DataReference[numRef];
                for (int j = 0; j < numRef; j++) {
                    rDist[j] = createDataReference(gDist, distBPart[j]);
                    pn[i][j].setPart(Pathname.E_PART, "1DAY");
                }
                refOp[i] = new MonToDayOperator(rIn, rDist);
            }
            else if (dvTS.equalsIgnoreCase("1DAY") && svTS.equalsIgnoreCase("1MON")) {
                if (op[3].equalsIgnoreCase("INIT")) refOp[i] = new InitOperator(rIn);
                else if (op[3].equalsIgnoreCase("LUMP")) refOp[i] = new LumpOperator(rIn);
               else {
                    //may want to place some error handler to take care of text file problems
                }
                for (int j = 0; j < numRef; j++) pn[i][j].setPart(Pathname.E_PART, "1MON");
            }
            else {
                //possibly need errorhandler
            }
            for (int j = 0; j < pn[i].length; j++) pnString[i][j] = pn[i][j].toString();
        }
    }

    private DataReference createDataReference(Group g, String bpart) {
        Group temp = Group.createGroup(g);
        temp.filterBy(true,new PathPartPredicate(bpart,Pathname.B_PART));
        return temp.getDataReference(0);
    }

    //The following method is only for testing purposes.
    private TimeWindow createTimeWindow(String st, String et) {
        TimeFactory tf = TimeFactory.getInstance();
        Time stime = tf.createTime(st);
        Time etime = tf.createTime(et);
        return tf.createTimeWindow(stime,etime);
    }

   //The following method is only for testing purposes.
    private void outputData (DataSet ds, String path) {
        System.out.println("To: " + path);
        System.out.println("Y = " + ds.getElementAt(0).getY());
    }

    private void storeData(DataSet ds, Pathname path) {}

    public void activate(TimeWindow tw) {
        DataSet[] ds;
        if (_transferDebug) System.out.println("In activate, TimeWindow = " + tw);  // to isolate Leaf's error
        if (_transferDebug) System.out.println("Number of reference operators = " + refOp.length);  // to isolate Leaf's error
        for (int i = 0; i < refOp.length; i++) {
        	if (_transferDebug) System.out.println("For reference operator #" + i
        			+ " before dataset retrieval");  // to isolate Leaf's error
            ds = refOp[i].returnDataSetArray(tw);
        	if (_transferDebug) System.out.println("Number of items is returned dataset = " + ds.length);  // to isolate Leaf's error
        	System.out.flush();
            for(int j = 0; j < ds.length; j++) {
            	if (_transferDebug) System.out.println("For dataset #" + j
            			+ " before writing " + ds[j] + " dataset");  // to isolate Leaf's error
            	System.out.flush();
            	if (!_transferDebug)
            		DSSUtil.writeData(toFile[i], pnString[i][j], ds[j]);
            	else {
	            	long smin = 0L;
	            	long emin = 0L;
	            	if (ds[j] instanceof TimeSeries) {
	            		TimeSeries ts = (TimeSeries) ds[j];
	            		smin = ts.getStartTime().getTimeInMinutes();
	       				System.out.println("smin = " + smin);
	       				System.out.flush();
	            		emin = ts.getEndTime().getTimeInMinutes();
	       				System.out.println("emin = " + emin);
	       				System.out.flush();
	            	}
	            	Pathname p = Pathname.createPathname(pnString[i][j]);
//       				System.out.println("Created pathname = " + p);
//       				System.out.flush();
	            	if (ds[j] instanceof RegularTimeSeries) {
	            		TimeInterval ti = ((RegularTimeSeries) ds[j]).getTimeInterval();
	            		p.setPart(4, ti.toString());
	            	}
	            	// CB next line does not work - library know the difference in types based on package
//	            	new DSSDataWriter().storeData(filename, p.toString(), smin, emin, ds,
//	       				      ds.isFlagged());

	       			DataSetAttr attr = ds[j].getAttributes();
	       			DSSData data = new DSSData();
	       			int dataType = attr.getType();
	       			System.out.println("In data TO BE transferred: dataType = " + dataType);
	       			System.out.flush();
	       			data._dataType = dataType;
	       			data._xType = attr.getXType();
	       			data._yType = attr.getYType();
	       			System.out.println("In data TO BE transferred: y type = " + attr.getYType());
	       			System.out.flush();
	       			data._xUnits = attr.getXUnits();
	       			data._yUnits = attr.getYUnits();
	       			System.out.println("In data TO BE transferred: y units = " + attr.getYUnits());
	       			System.out.flush();
	       			data._numberRead = ds[j].size();
       				System.out.println("In data TO BE transferred: dataset size = " + ds[j].size());
       				System.out.flush();
	       			data._yValues = SetUtils.createYArray(ds[j]);
	       			System.out.println("In data TO BE transferred:");
	       			for (int k = 0; k < data._yValues.length; ++k) {
	       				System.out.println("\t + yValues[" + k + "] = " + data._yValues[k]);
	       			}
	       			System.out.flush();
	       			if (p.toString().length() >= 1800)
	       				throw new IllegalArgumentException("Pathname: " + p.toString()
	       						+ " is too long (>1800chars)");
	       			if (dataType == 100) {
	       				System.out.println("Calling method createFlagArray");
	       				if (ds[j].isFlagged()) data._flags = SetUtils.createFlagArray(ds[j]);
	       				System.out.println("Calling native method storeTimeSeriesData");
	       				System.out.flush();
	       			} else if (dataType == 110) {
            			data._xValues = SetUtils.createXArray(ds[j]);
            			if (ds[j].isFlagged())
            				data._flags = SetUtils.createFlagArray(ds[j]);
	       				System.out.println("Calling native method storeIrregularTimeSeriesData");
	       				System.out.flush();
            		} else if (dataType == 200) {
	       				data._xValues = SetUtils.createXArray(ds[j]);
       					System.out.println("Calling native method storePairedData");
       					System.out.flush();
            		} else {
       					System.out.println("Throwing IllegalArgumentException");
       					System.out.flush();
 	       				throw new IllegalArgumentException("Data type: " + dataType + " is invalid");
            		}

	       			DSSUtil.writeData(toFile[i], pnString[i][j], ds[j]);
	       			System.out.println("Successful data write");
   					System.out.flush();
/*
   					if (pnString[i][j].indexOf("c9__z1") > -1 || pnString[i][j].indexOf("C9__Z1") > -1) {
	            		Pathname p2 = Pathname.createPathname(ds[j].getName());
   		            	if (ds[j] instanceof RegularTimeSeries) {
   		            		TimeInterval ti = ((RegularTimeSeries) ds[j]).getTimeInterval();
//   		            		p.setPart(4, ti.toString());

   		            		Pathname p1 = Pathname.createPathname(pnString[i][j]);
   		            		p2.setPart(1, p1.getPart(1));
//   		            		Group g = DSSUtil.createGroup("local",dvFile);
//   		            		DataReference ref = createDataReference(g, new String("^" + p1.getPart(1) + "$"));
//   		            		Pathname pn = Pathname.createPathname(ref.getPathname());
//   		            		pn.setPart(Pathname.B_PART, p1.getPart(1));
//   		            		pn.setPart(Pathname.C_PART, cPart);
   		            	}
//   						DataSet set = DSSUtil.readData(toFile[i], p2.toString(), true); // not working
   		            	DataSet set = DSSUtil.readData(toFile[i], pnString[i][j], true);
   						DataSetAttr setAttr = set.getAttributes();
   		       			System.out.println("In data THAT WAS transferred: y type = " + setAttr.getYType());
   		       			System.out.println("In data THAT WAS transferred: y units = " + setAttr.getYUnits());
   		       			double[] yValues = SetUtils.createYArray(set);
   		       			System.out.println("In data THAT WAS transferred:");
   		       			for (int k = 0; k < yValues.length; ++k) {
   		       				System.out.println("\t + yValues[" + k + "] = " + yValues[k]);
   		       			}
   		       			System.out.flush();
   					}
*/

//	       			DSSDataWriter writer = new DSSDataWriter(); //CB Does not work; native methods want original class object as arg
//	       			writer.storeData(toFile[i], p.toString(), smin, emin, ds[j], ds[j].isFlagged());
            	}
               	if (_transferDebug) System.out.println("   after writing dataset values");  // to isolate Leaf's error
            }
        }
    }

    public static void main(String args[]) {
        StudyInterface si = new StudyInterface("D:\\TestDss\\Test1.dss", "1MON",
        		"D:\\TestDss\\Test1.txt", false);
        MSWUtil.createTimeFactory();
        TimeWindow tw = MSWUtil.createTimeWindow("31OCT1921 2400", "30NOV1921 2400");
        si.activate(tw);
        System.exit(0);
    }
}
