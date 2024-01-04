package calsim.msw;
import calsim.app.*;
import java.util.*;
import vista.time.*;

public class MSWUtil {
    //Generate the TimeFactory.  The TimeFactory object will be used
    //many times and there is only a need for one of them.
    protected static void createTimeFactory() {
        tF = TimeFactory.getInstance();
        dayTI = tF.createTimeInterval(1, TimeInterval.DAY_INTERVAL);
        monTI = tF.createTimeInterval(1, TimeInterval.MONTH_INTERVAL);
    }

    //The following method returns a TimeWindow object.
    protected static TimeWindow createTimeWindow(String st, String et) {
        return createTimeWindow(tF.createTime(st), tF.createTime(et));
    }

    //The following method returns a TimeWindow object.
    protected static TimeWindow createTimeWindow(Time sTime, Time eTime) {
        return tF.createTimeWindow(sTime,eTime);
    }

    protected static void parseFile(String file, Vector details, Vector loc) {

        CSVParser parser = new CSVParser(file);

        //Fill vector and track operation line locations.
        int iL = 0;
        String[] fields;
        while (true) {
            fields = parser.nextLine();
            if (fields == null) break;
            if (fields[0].indexOf("*") == 0) loc.addElement(new Integer(iL));
            details.addElement(fields);
            iL++;
        }
        parser.close();
    }

    private static TimeFactory tF;
    protected static TimeInterval dayTI, monTI;
    public static boolean position = false;
    public static int nperiods = 0;
    public static int posyr = 0;
    public static int dvnum = 0;
    public static int ndv = 1;
    public static boolean firstPass = true;
    public static String positionStartMonth = "OCT";
    public static String positionContinueMonth = "OCT";
    public static int posStartYear = 1921;
    public static MYDate positionStart = new MYDate();
    public static MYDate positionEnd   = new MYDate();
}
