from calsim.app import DerivedTimeSeries, MultipleTimeSeries
from calsim.gui import DefaultFrame, DTSTable, MTSTable
dts = DerivedTimeSeries.load("test/dts1-demo.csv")
dtable = DTSTable(dts)
mts = MultipleTimeSeries.load("test/mts1-demo.csv")
mtable = MTSTable(mts)
mts.save(FileOutputStream("test/mts1-demo-out.csv"))
dts.save(FileOutputStream("test/dts1-demo-out.csv"))
dts2 = DerivedTimeSeries.load("test/dts1-demo-out.csv")
dtable2 = DTSTable(dts2)
mts2 = MultipleTimeSeries.load("test/mts1-demo-out.csv")
mtable2 = MTSTable(mts2)
frames = []
frames.append(DefaultFrame(dtable))
frames.append(DefaultFrame(dtable2))
frames.append(DefaultFrame(mtable))
frames.append(DefaultFrame(mtable2))
for frame in frames:
    frame.pack()
    frame.setVisible(1)
#

