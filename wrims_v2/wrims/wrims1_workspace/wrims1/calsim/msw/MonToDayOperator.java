package calsim.msw;
import vista.set.*;
import vista.time.*;

public class MonToDayOperator extends ReferenceOperator {

    DataReference[] rOp;

    protected MonToDayOperator(DataReference[] r0, DataReference[] r1) {
        super(r0);
        rOp = r1;

    }

    //Method required by ReferenceOperator
    protected DataSet[] returnDataSetArray(TimeWindow tw) {

        DataSet dsIn, dsOut;
        DataReference rOut;
        TimeWindow monTW, dayTW;
        int numdays;
        DataSetIterator dSI;

        setTimeWindow(rIn, tw);
        dayTW = MSWUtil.createTimeWindow(tw.getStartTime().__sub__(MSWUtil.monTI).__add__(MSWUtil.dayTI), tw.getEndTime());
        setTimeWindow(rOp, dayTW);


        //For every passed variable
        for (int i = 0; i < ds.length; i++) {

            dsIn = rIn[i].getData();
            ds[i] = rOp[i].getData();
            dSI = ds[i].getIterator();

            //Initialize month time window.
            monTW = MSWUtil.createTimeWindow(dayTW.getStartTime(), tw.getStartTime());

            //For every month
            for (int j = 0; j < dsIn.size(); j++) {

                rOp[i] = DataReference.createExpanded(rOp[i], monTW);
                numdays = rOp[i].getData().size();
                rOut = DataReferenceMath.scalarOperation(rOp[i], (dsIn.getElementAt(j).getY() * numdays / 100), DataReferenceMath.MUL);
                dsOut = rOut.getData();

                //For every day
                for (int k = 0; k < numdays; k++) {
                    dSI.putElement(dsOut.getElementAt(k));
                    dSI.advance();
                }

                monTW = MSWUtil.createTimeWindow(monTW.getStartTime().__add__(MSWUtil.monTI),
                                                 monTW.getEndTime().__add__(MSWUtil.monTI));
            }
        }
        return ds;
    }
}
