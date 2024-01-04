from calsim.app import MonthlyReport, AppUtils
sys.add_package('javax.swing.plaf.metal')
from javax.swing.plaf.metal import MetalLookAndFeel,DefaultMetalTheme
MetalLookAndFeel.setCurrentTheme(DefaultMetalTheme());
from javax.swing import JTextPane, JFrame, JScrollPane
sys.add_package('javax.swing.text')
from javax.swing.text import DefaultStyledDocument, SimpleAttributeSet, StyleConstants, StyleContext
from calsim.gui import TextDisplay, DefaultFrame
#
g=opendss('../testdata/sim514dv.dss')
ref=AppUtils.getDataReference(g,"C6","FLOW-channel",None)
ds = ref.getData()
path = ref.getPathname()
file = ref.getFilename()
#
smt1 = MonthlyReport(ds,path,file)
td1 = TextDisplay(smt1.getStyledDocument())
df1 = DefaultFrame(td1)
df1.setSize(900,600)
df1.show()
#
years = [1972,1986,1993,1933,1945]
smt2 = MonthlyReport(ds,path,file,0,years)
td2 = TextDisplay(smt2.getStyledDocument())
df2 = DefaultFrame(td2)
df2.setSize(900,600)
df2.show()
#
# should display in water year
smt3 = MonthlyReport(ds,path,file, 0, None)
doc3 = smt3.getStyledDocument()
td3 = TextDisplay(doc3)
df3 = DefaultFrame(td3)
df3.setSize(900,600)
df3.show()
#
# should display in water year
x = range(50)
ts = RegularTimeSeries('/A/B/C/D/E/F/','31may1924 2400','1mon',x)
smt4 = MonthlyReport(ts,path,file, 0, None)
doc4 = smt4.getStyledDocument()
td4 = TextDisplay(doc4)
df4 = DefaultFrame(td4)
df4.setSize(900,600)
df4.show()
from calsim.app import AppUtils
prj = AppUtils.getCurrentProject()
prj.setTimeWindow("31MAY1928 2400 - 30JUN1944 2400")
smt5 = MonthlyReport(ts,path,file, 0, None)
doc5 = smt5.getStyledDocument()
td5 = TextDisplay(doc5)
df5 = DefaultFrame(td5)
df5.setSize(900,600)
df5.show()
