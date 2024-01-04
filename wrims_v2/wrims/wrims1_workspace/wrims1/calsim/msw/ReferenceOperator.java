package calsim.msw;
import vista.set.*;
import vista.time.TimeWindow;

public abstract class ReferenceOperator {

    DataReference[] rIn;
    DataReference[] drIn;
    DataSet[] ds;

    protected ReferenceOperator(DataReference[] r) {
        rIn = r;
        ds = new DataSet[rIn.length];
    }

    protected void setTimeWindow(DataReference[] r, TimeWindow tw) {

        for (int i = 0; i < r.length; i++) {
            r[i] = DataReference.createExpanded(r[i], tw);
        }
    }

    protected abstract DataSet[] returnDataSetArray(TimeWindow tw);
}

