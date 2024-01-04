sys.add_package('calsim.schematic')
from calsim.schematic import LabeledSymbol, LabeledLine, PopupMenuHandler
from javax.swing import JFrame, JScrollPane
# create schematic and show it
from calsim.schematic import CalsimSchematicData, CalsimSchematic, \
     CalsimSchematicFrame, DragHandler, CalsimSchematicCanvas
from calsim.app import AppUtils
AppUtils.viewGraph = 1
AppUtils.viewTable = 0
AppUtils.viewMonthlyTable = 0
prj = AppUtils.getCurrentProject()
prj.setSVFile("../testdata/sim514sv.dss")
prj.setDVFile("../testdata/sim514dv.dss")
#sch_file = "cs.data"
sch_file = "test1.data"
csd = CalsimSchematicData(sch_file)
cs = CalsimSchematic(csd)
from java.awt import Color
cs.backgroundColor = Color.white
#
#fr = JFrame("")
#fr.getContentPane().add(gc)
#fr.setSize(500,500)
#fr.pack()
#fr.show()
#
csc = CalsimSchematicCanvas(cs)
csc.add(PopupMenuHandler())
csc.add(DragHandler(cs))
csf = JFrame("")
csf.getContentPane().add(JScrollPane(csc))
#csf = CalsimSchematicFrame(cs)
#csf.add(PopupMenuHandler())
#csf.add(DragHandler(cs))
csf.setSize(400,800);
cs.setPreferredSize(400,800);
csf.show()
