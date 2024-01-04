import jarray
#from hec.script import *
from hec.heclib.dss import *
from hec.hecmath import TimeSeriesMath, HecMath
from hec.gfx2d import G2dDialog, G2dLine, Symbol
from hec.io import PairedDataContainer
from hec.gfx2d import G2dPanelProp
from java.util import Vector

from org.w3c.dom import Document
import java
import math
from java.awt import Color
from javax.swing import *
def removeToolbar(panel):
    components = panel.getComponents();
    panel.remove(components[-1])
def newPlot(title):
    plotProp = G2dPanelProp()
    plotProp.hasToolbar = False
    return G2dDialog(None, title, False, Vector(), plotProp)
def saveToPNG(p, filename):
    from java.awt.image import BufferedImage
    from javax.imageio import ImageIO
    from java.io import File
    bi = BufferedImage(p.size.width, p.size.height, BufferedImage.TYPE_INT_ARGB) 
    g = bi.createGraphics()
    p.invalidate()
    p.validate()
    p.paint(g)
    g.dispose();
    ImageIO.write(bi, "png", File(filename));
def plot(data, title):
    plotd = newPlot(title)
    for d in data:
        plotd.addData(d)
    plotd.showPlot()
def get_matching(dss, pattern):
    matches = dss.getCatalogedPathnames(pattern)
    if (len(matches) >= 1):
        return dss.get(matches[0])
    else:
        print 'No match for: %s, %s' % (pattern, matches)
        return None
def open_dss(file): 
    return HecDss.open(file, True)
def average(data, average_interval):
    """
    filter_type is one of
     "INT" - Interpolate at end of interval 
     "MAX" - Maximum over interval 
     "MIN" - Minimum over interval 
     "AVE" - Average over interval 
     "ACC" - Accumulation over interval 
     "ITG" - Integration over interval 
     "NUM" - Number of valid data over interval 
    """
    filter_type = "AVE"
    return TimeSeriesMath(data).transformTimeSeries(average_interval, None, 'AVE', 0).data
def do_compare(paths, dssfiles, title, doAverage=False, diffToFirst=False):
    data = []
    for i in range(len(paths)):
        d = get_matching(dssfiles[i], paths[i])
        if doAverage: d=average(d,"1DAY")
        data.append(d)
    if diffToFirst:
        for i in range(1,len(paths)):
            diff=TimeSeriesMath(data[i]).subtract(TimeSeriesMath(data[0]))
            diff.container.location=d.location+'-DIFF'
            data.append(diff.data)
    plot(data, title)
def run_compare(files, plotSet, stime, etime, doAverage, doDiffToFirst):
    dssfiles = []
    for f in files:
        d = open_dss(f)
        d.setTimeWindow(stime, etime)
        dssfiles.append(d)
    for l in plotSet:
        paths = plotSet[l]
        do_compare(paths,dssfiles,l,doAverage,doDiffToFirst)
    for df in dssfiles:
        HecDss.close(df);
    print 'END'

if __name__ == '__main__':
    # starttime and endtime in HEC Time format
    stime = '05JAN1990 0000'
    etime = '30JAN2005 2400'
    # Files corresponding to the pathnames in plotSet structure
    files = ['Z:/delta/dsm2_v812/studies/extended/output/historical_extended_hist_ext1.dss',
             'Z:/delta/dsm2_v812/studies/extended/output/historical_extended_hist_ext1_original.dss',
             'Z:/delta/dsm2_v812/timeseries/hist_19902012.dss']
    # a plot will be generated for each plotSet identified by a name and an array of pathnames
    plotSet = {"Martinez Stage":["/*/RSAC054/STAGE/*/*/*441/", "/*/RSAC054/STAGE/*/*/*441/","/*/RSAC054/STAGE/*/*/*/"],
                "Martinez EC":["/*/RSAC054/EC/*/*/*/","/*/RSAC054/EC/*/*/*/","/*/RSAC054/EC/*/*/*CORRECTED/"] 
                 }
    #set to True for doing daily averages and False for not
    doAverage=True
    #set to True for differences and False for not
    doDiffToFirst=True
    run_compare(files, plotSet, stime, etime, doAverage, doDiffToFirst)
    
