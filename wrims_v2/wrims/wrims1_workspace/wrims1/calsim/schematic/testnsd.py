sys.add_package('calsim.schematic')
sys.add_package('calsim.gym')
from calsim.gym import Network
from calsim.schematic import LabeledSymbol, LabeledLine, PopupMenuHandler
from javax.swing import JFrame, JScrollPane
# create schematic and show it
from calsim.schematic import CalsimSchematicData, CalsimSchematic, \
     CalsimSchematicFrame, DragHandler, CalsimSchematicCanvas
from calsim.schematic import NetworkSchematicData
from calsim.app import AppUtils
AppUtils.viewGraph = 1
AppUtils.viewTable = 0
AppUtils.viewMonthlyTable = 0
prj = AppUtils.getCurrentProject()
prj.setSVFile("../testdata/sim514sv.dss")
prj.setDVFile("../testdata/sim514dv.dss")
#sch_file = "cs.data"
sch_file = "test1.data"
#csd = CalsimSchematicData(sch_file)
#net = Network.read('calsim-connectivity.csv')
#csd = NetworkSchematicData(net,'calsim-node.data')
net = Network.read('sample1.net')
csd = NetworkSchematicData(net,'sample1.xy')
#csd._xmax = 174.0; csd._ymax=64.0
#csd._xmin = 114.0; csd._ymin=24.0
#csd = NetworkSchematicData(net,'calsim-node2.data')
#csd._xmax = 174.0*20; csd._ymax=64.0*20
#csd._xmin = 114.0; csd._ymin=24.0
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
#csc.add(PopupMenuHandler())
csc.add(DragHandler(csc))
csf = JFrame("")
csf.getContentPane().add(JScrollPane(csc))
#csf = CalsimSchematicFrame(cs)
#csf.add(PopupMenuHandler())
#csf.add(DragHandler(cs))
csf.setSize(800,800);
cs.setPreferredSize(2400,2400);
csf.show()
#
net = cs.getModel().getNetwork()
net.write("c-conn1.csv")
cs.getModel().writeXY(net,"c-xy1.csv")
#
def getGE(ht,obj):
  e = ht.keys()
  x = None
  while e.hasMoreElements():
    x = e.nextElement()
    if ht.get(x) == obj : break
  return x
