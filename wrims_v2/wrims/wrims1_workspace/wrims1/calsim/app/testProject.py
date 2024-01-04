from calsim.app import Project, DerivedTimeSeries, MultipleTimeSeries
dts = DerivedTimeSeries.load("test/dts1-demo.csv")
mts = MultipleTimeSeries.load("test/mts1-demo.csv")
mts.save(FileOutputStream("test/mts1-demo-out.csv"))
dts.save(FileOutputStream("test/dts1-demo-out.csv"))
dts2 = DerivedTimeSeries.load("test/dts1-demo-out.csv")
mts2 = MultipleTimeSeries.load("test/mts1-demo-out.csv")
prj = Project()
prj.add(dts);
prj.add(mts);
prj.save("prj.bin");
prj2 = Project()
prj2.load("prj.bin");
