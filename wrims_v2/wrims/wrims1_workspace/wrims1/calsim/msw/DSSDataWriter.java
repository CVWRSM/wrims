package calsim.msw;

//import vista.db.dss.DSSUtil;
import vista.set.DataSet;
import vista.set.DataSetAttr;
import vista.set.SetUtils;

class DSSDataWriter {
//    private static final boolean DEBUG = false;

    public DSSDataWriter() {}

    public void storeData(String filename, String pathname, long startJulmin,
			  long endJulmin, DataSet ds, boolean storeFlags) {
	DataSetAttr attr = ds.getAttributes();
	DSSData data = new DSSData();
	int dataType = attr.getType();
	data._dataType = dataType;
	data._xType = attr.getXType();
	data._yType = attr.getYType();
	data._xUnits = attr.getXUnits();
	data._yUnits = attr.getYUnits();
	data._numberRead = ds.size();
	data._yValues = SetUtils.createYArray(ds);
	if (pathname.length() >= 1800)
	    throw new IllegalArgumentException("Pathname: " + pathname
					       + " is too long (>1800chars)");
	if (dataType == 100) {
	    if (storeFlags)
		data._flags = SetUtils.createFlagArray(ds);
	    storeTimeSeriesData(filename, pathname, startJulmin, endJulmin,
				data, storeFlags);
	} else if (dataType == 110) {
	    data._xValues = SetUtils.createXArray(ds);
	    if (storeFlags)
		data._flags = SetUtils.createFlagArray(ds);
	    storeIrregularTimeSeriesData(filename, pathname, 0L, 0L, data,
					 storeFlags);
	} else if (dataType == 200) {
	    data._xValues = SetUtils.createXArray(ds);
	    storePairedData(filename, pathname, data, storeFlags);
	} else
	    throw new IllegalArgumentException("Data type: " + dataType
					       + " is invalid");
    }

    private synchronized native void storeTimeSeriesData(String string, String string_0_, long l,
    	long l_1_, DSSData dssdata, boolean bool);

    private synchronized native void storeIrregularTimeSeriesData(String string, String string_2_,
    	long l, long l_3_, DSSData dssdata, boolean bool);

    private synchronized native void storePairedData(String string, String string_4_,
    	DSSData dssdata, boolean bool);

    static {
//		DSSUtil.loadDSSLibrary();
    	try {
//    	    String vh = System.getProperty("vista.home");
//CB    		String vh = System.getProperty("calsim.home");
			String vh = System.getenv("calsim.home");  //CB
    	    if (vh == null)
    		System.loadLibrary("dss");
    	    else {
    		String osname = System.getProperty("os.name");
    		String fs = System.getProperty("file.separator");
//    		if (osname.indexOf("Sun") >= 0
//    		    || osname.indexOf("olaris") >= 0)
//    		    System.load(vh + fs + "lib" + fs + "libdss.so");
//    		else
    			if (osname.indexOf("Win") >= 0) {
    				System.load(vh + fs + "bin" + fs + "DSS.dll");
//    				String map = System.mapLibraryName(vh + fs + "bin" + fs + "DSS.dll");
//    				map = System.mapLibraryName("dss");
    				System.out.print("");
    			} else
    				System.loadLibrary("dss");
    	    }
    	} catch (Exception e) {
    	    System.err.println("Could not load dss library");
    	    System.err.println(e.getMessage());
    	}
    }
}