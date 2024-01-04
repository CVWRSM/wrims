package calsim.msw;
import vista.set.*;
import vista.time.*;

public class InitOperator extends ReferenceOperator {

    protected InitOperator(DataReference[] r) {
        super(r);
    }

    //Method required by ReferenceOperator
    protected DataSet[] returnDataSetArray(TimeWindow tw) {

        TimeWindow endTW = MSWUtil.createTimeWindow(tw.getEndTime(), tw.getEndTime());

        setTimeWindow(rIn, endTW);

        DataSet[] ds = new DataSet[rIn.length];
        TimeFactory tf = TimeFactory.getInstance();
        for (int i = 0; i < rIn.length; i++) {
            DataSet tDS = rIn[i].getData();
            DataSetAttr tDSA = tDS.getAttributes();
            double[] Y = {tDS.getElementAt(0).getY()};
            int[] flag = {tDS.getElementAt(0).getFlag()};
            ds[i] = new RegularTimeSeries( tDS.getName(),
                                    endTW.getStartTime(),
                                    tf.createTimeInterval(1, TimeInterval.MONTH_INTERVAL),
                                    Y,
                                    flag,
                                    tDSA);
        }
        for (int i = 0; i < ds.length; i++) {
        }
        return ds;
    }
}
