from hec.script import *
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

#json parser
from com.xhaus.jyson import JysonCodec as json
 
def open_dss(dssfile):
    """
    opens a dss files only if it exists, else fails
    """
    dss=HecDss.open(dssfile,True)
    return dss
def close_dss(dss):
    """
    Takes a handle and closes it
    """
    dss.close()
def set_working_timewindow(dss, stime,etime):
    """
    Takes a dss handle (previously opened by call to open_dss and 
    a time window defined by strings in ddMMMyyyy HHmm format (start time and end time)  
    """
    dss.setTimeWindow(stime,etime)
def get_matching(dss,pattern):
    """
    Takes a dss handle and
    a pattern in format of "([part letter (A|B|C|D|E|F)]=<string to match> [space])*"
    e.g.
    get_matching(obs,'A=OBS C=MTZ E=15MIN')
    
    Fails by return None and printing message of failure.
    """
    matches=dss.getCatalogedPathnames(pattern)
    if (len(matches) >= 1):
        return dss.get(matches[0])
    else:
        print 'No match for: %s, %s'%(pattern,matches)
        return None
def newPlot(title):
    """
    Creates a blank plot window with title
    """
    plotProp = G2dPanelProp()
    plotProp.hasToolbar=False
    return G2dDialog(None,title,False,Vector(),plotProp)
def save_as_png(p,filename):
    """
    saves plot window (G2Dialog) created by newPlot(title) method as a png graphics file
    """
    from java.awt.image import BufferedImage
    from javax.imageio import ImageIO
    from java.io import File
    bi = BufferedImage(p.size.width, p.size.height, BufferedImage.TYPE_INT_ARGB) 
    g = bi.createGraphics()
    p.invalidate()
    p.validate()
    p.paint(g)
    g.dispose();
    ImageIO.write(bi,"png", File(filename));
def removeToolbar(panel):
    components=panel.getComponents();
    panel.remove(components[-1])
def create_regression_line(drun,dobs,legend):
    drunm=TimeSeriesMath(drun)
    dobsm=TimeSeriesMath(dobs)
    paired=dobsm.generateDataPairs(drunm,False)
    pairedData = paired.data
    pairedData.fullName=legend
    reg=dobsm.multipleRegression([drunm],HecMath.UNDEFINED, HecMath.UNDEFINED)
    regData=reg.data
    a=regData.yOrdinates[0][1]
    b=regData.yOrdinates[0][0]
    regData.fullName="//REGRESSION LINE////GENERATED/"
    maxVal=drunm.max()
    minVal=drunm.min()
    regData.xOrdinates[0]=a*minVal+b
    regData.xOrdinates[1]=a*maxVal+b
    regData.yOrdinates[0][0]=minVal
    regData.yOrdinates[0][1]=maxVal
    regData.yunits = pairedData.yunits
    regData.xunits = pairedData.xunits
    regData.xtype=pairedData.xtype
    regData.ytype=pairedData.ytype
    regData.xparameter=pairedData.xparameter
    regData.yparameter=pairedData.yparameter
    regData.location=pairedData.location
    regData.version='LINEAR REGRESSION'
    return regData,pairedData
def scatter_plot(dobsm, drun1m, drun2m, title, legend1, legend2):
    if dobsm == None:
        return None
    from hec.lang import DSSPathString
    regData1, pairedData1 = create_regression_line(drun1m,dobsm,legend1+" vs "+legend2)
    if drun2m != None:
        path=DSSPathString(drun2m.path)
        path.setFPart(path.getFPart()+":2:"+obspath.getFPart())
        regData2, pairedData2 = create_regression_line(drun2m,dobsm,legend1+" vs "+legend3)
    plots = newPlot(title);
    plots.addData(regData1)
    plots.addData(pairedData1)
    if drun2m != None:
        plots.addData(regData2)
        plots.addData(pairedData2)
    plots.showPlot()
    #plots.getViewport(regData1).getAxis("x1").setViewLimits(0,10000)
    #plots.getViewport(regData1).getAxis("y1").setViewLimits(0,10000)
    pline = plots.getCurve(regData1)
    pline.setLineColor("red")
    pline = plots.getCurve(pairedData1)
    pline.setLineVisible(0)
    pline.setSymbolType(Symbol.SYMBOL_CIRCLE)
    pline.setSymbolsVisible(1)
    pline.setSymbolSize(3)
    pline.setSymbolFillColor(pline.getLineColorString())
    pline.setSymbolLineColor(pline.getLineColorString())
    if drun2m != None:
        pline = plots.getCurve(pairedData2)
        pline.setLineColor("blue")
        pline.setLineVisible(0)
        pline.setSymbolType(Symbol.SYMBOL_SQUARE)
        pline.setSymbolsVisible(1)
        pline.setSymbolSize(3)
        pline.setSymbolFillColor(pline.getLineColorString())
        pline.setSymbolLineColor(pline.getLineColorString())
    g2dPanel = plots.getPlotpanel()
    g2dPanel.revalidate()
    g2dPanel.paintGfx()
    plots.setVisible(False)
    return g2dPanel
def save_plots_to_images(mainPanel, imageFileName, imageDir='z:/temp'):
    """
    Saves the panel used to draw plots to images
    """
    save_as_png(mainPanel,imageDir+imageFileName+".png")
def ts_period_operation(data, interval="1DAY", operation_type="AVE"):
    """
    transforms the time series using a period operation with
    given interval (1DAY (default), 1HOUR, etc) and
    given operation type (AVE (default), MAX, MIN)
    """
    tdata = TimeSeriesMath(data).transformTimeSeries(interval, None, operation_type, 0)
    tdata.data.fullName = tdata.data.fullName+operation_type
    return tdata.data
def ts_normalize(data):
    """
    Normalize time series by dividing by data's mean
    """
    datan=data.divide(TimeSeriesMath(data).mean())
    datan.fullName = datan.fullName+"-NORMED"
    return datan.data
def calculate_weighted_rms(weights=None):
    """
    calculate the weighted rms (if weights not given assumed to be equal to 1)
    """
    rms1=0
    rms1_min,rms1_max=0,0
    rms2=0
    rms2_min,rms2_max=0,0
    rmsmap={}
    #run2=None
    sumwts=0
    if weights != None:
        sumwts=sumwts+weights[l]
        lrms1 = calculate_rms(drun1m.data, dobsm.data)*weights[l]
        lrms1_min=calculate_rms(drun1m_min.data,dobsm_min.data)*weights[l]
        lrms1_max=calculate_rms(drun1m_max.data,dobsm_max.data)*weights[l]
        rms1=rms1+lrms1
        rms1_min=rms1_min+lrms1_min
        rms1_max=rms1_max+lrms1_max
        lrms2 = calculate_rms(drun2m.data,dobsm.data)*weights[l]
        lrms2_min=calculate_rms(drun2m_min.data,dobsm_min.data)*weights[l]
        lrms2_max=calculate_rms(drun2m_max.data,dobsm_max.data)*weights[l]
        rmsmap[l] = lrms1,lrms2,lrms1_min,lrms2_min,lrms1_max,lrms2_max
        rms2=rms2+lrms2
        rms2_min=rms2_min+lrms2_min
        rms2_max=rms2_max+lrms2_max
    if weights != None:
        rms1=(rms1+rms1_min+rms1_max)/sumwts
        rms2=(rms2+rms2_min+rms2_max)/sumwts
        print 'RMS Run 1: %f'%rms1
        print 'RMS Run 2: %f'%rms2
        for loc in rmsmap.keys():
            print loc, rmsmap[loc] 
def calculate_rms(run,obs):
    runt = TimeSeriesMath(run)
    obst = TimeSeriesMath(obs)
    tavg = obst.abs().sum()/obst.numberValidValues()
    diff=runt.subtract(obst)
    return math.fabs(math.sqrt(diff.multiply(diff).sum()/diff.numberValidValues())/tavg)*math.log(tavg)
    #return Stats.sdev(run-obs)/Stats.avg(obs)
def arrange_plots(regplot, plotm, plotd):
    """
    arranges the plots in a certain order, 
    regplot and plotm in first row next to each other
    plotd in second row 
    """
    from javax.swing import JPanel,JFrame
    from java.awt import GridBagLayout, GridBagConstraints
    mainPanel = JPanel()
    mainPanel.setLayout(GridBagLayout())
    c=GridBagConstraints()
    c.fill=c.BOTH
    c.weightx,c.weighty=0.5,1
    c.gridx,c.gridy,c.gridwidth,c.gridheight=0,0,10,4
    c.weightx,c.weighty=0.5,1
    c.gridx,c.gridy,c.gridwidth,c.gridheight=0,0,10,4
    mainPanel.add(regplot)
    c.gridx,c.gridy,c.gridwidth,c.gridheight=0,0,10,4
    c.weightx,c.weighty=1,1
    mainPanel.add(plotm,c)
    c.gridx,c.gridy,c.gridwidth,c.gridheight=0,4,10,6
    mainPanel.add(plotd,c)
    return mainPanel
def show_plot_in_frame(mainPanel):
    """
    embeds panel containing plots in a closable frame
    clears the panel background to white 
    """
    fr=JFrame()
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    fr.getContentPane().add(mainPanel)
    fr.setSize(1100,850);
    mainPanel.setSize(1100,850);
    mainPanel.setBackground(Color.WHITE);
    fr.show();
    return fr
def ts_plot(data1, data2=None, data3=None, title="", legend1="", legend2="", legend3=""):
    plotd = newPlot(title)
    if data1 != None:
        plotd.addData(data1)
    if data2 != None:
        plotd.addData(data2)
    if data3 != None:
        plotd.addData(data3)
    plotd.showPlot()
    plotd.setPlotTitleText(title)
    plotd.setPlotTitleVisible(True)
    legend_label = plotd.getLegendLabel(data1)
    legend_label.setText(legend1)
    legend_label = plotd.getLegendLabel(data2)
    legend_label.setText(legend2)
    legend_label = plotd.getLegendLabel(data3)
    legend_label.setText(legend3)
    plotd.setVisible(False)
    xaxis=plotd.getViewport(0).getAxis("x1")
    #vmin =xaxis.getViewMin()+261500. # hardwired to around july 1, 2008 ???
    #xaxis.setViewLimits(vmin,vmin+10000.)
    if data1 != None:
        pline = plotd.getCurve(data1)
        pline.setLineVisible(1)
        pline.setLineColor("blue")
        pline.setSymbolType(Symbol.SYMBOL_CIRCLE)
        pline.setSymbolsVisible(0)
        pline.setSymbolSize(3)
        pline.setSymbolSkipCount(0)
        pline.setSymbolFillColor(pline.getLineColorString())
        pline.setSymbolLineColor(pline.getLineColorString())
    if data3 != None:
        pline = plotd.getCurve(data3)
        pline.setLineVisible(1)
        pline.setLineColor("orange")
    g2dPanel = plotd.getPlotpanel()
    g2dPanel.revalidate();
    g2dPanel.paintGfx();
    dpanel = plotd.getPlotpanel()
    return dpanel
    #removeToolbar(dpanel)
def run_compare(location, datatype, file1, pattern1, file2, pattern2, file3=None, pattern3=None, stime=None, etime=None):
    dss1 = open_dss(file1)
    if stime:
        set_working_timewindow(dss1,stime,etime)
    dss2 = open_dss(file2)
    if stime:
        set_working_timewindow(dss2,stime,etime)
    if file3:
        dss3 = open_dss(file3)
        if stime: set_working_timewindow(dss3,stime,etime)
    try:
        data1 = get_matching(dss1, pattern1)
        data2 = get_matching(dss2, pattern2)
        if dss3:
            data3 = get_matching(dss3, pattern3)
        title = location + ' ' + datatype
        legend1 = 'Observed'
        legend2 = 'Model 1'
        legend3 = 'Model 2'
        period_args='1DAY'
        period_ops='AVE'
        #plot = ts_plot(data1, data2, None, title, legend1, legend2)
        #show_plot_in_frame(plot)
        
        plotd = ts_plot(ts_period_operation(data1,period_args,period_ops), ts_period_operation(data2,period_args,period_ops), ts_period_operation(data3,period_args,period_ops) if file3 else None, title+" DAILY AVERAGE", legend1, legend2, legend3)
        show_plot_in_frame(plotd)
        #plotr = scatter_plot(ts_period_operation(data1,'1MON','AVE'), ts_period_operation(data2,'1MON','AVE'), None, title+" REGRESSION", legend1, legend2)
        #show_plot_in_frame(plotr)
    finally:
        dss1.close()
        dss2.close()
def config_data_ec():
    return """
    { 
    "file1" : "Z:/DSM2_v81_Beta_Release/observed_data/Observed_EC.dss",
    "file2" : "Z:/DSM2_v81_Beta_Release/studies/historical_qual_ec_v81/output/historical_v81.dss",
    "file3": "Z:/DSM2_v81_Beta_Release/studies/historical_qual_ec_v81/output/historical_v81-sacm.dss",
    "stime" : "01JAN2000 0000",
    "etime" : "01JAN2002 0000",
    "locations": [ 
      {"location": "Martinez" , "datatype" : "EC", "pattern1": "A=RSAC054 B=MTZ C=EC E=1HOUR", "pattern2": "B=RSAC054 C=EC"}
      ,{"location": "Mallard" , "datatype" : "EC", "pattern1": "A=RSAC075 B=MAL C=EC E=IR-MON", "pattern2": "B=RSAC075 C=EC"}
      ,{"location": "Collinsville" , "datatype" : "EC", "pattern1": "A=RSAC081 B=CLL C=EC E=IR-DAY", "pattern2": "B=RSAC081 C=EC"}
      ,{"location": "Anitoch" , "datatype" : "EC", "pattern1": "A=RSAN007 B=ANH C=EC E=IR-MON", "pattern2": "B=RSAN007 C=EC"}
      ,{"location": "Emmaton" , "datatype" : "EC", "pattern1": "A=RSAC092 B=EMM C=EC E=IR-DAY", "pattern2": "B=RSAC092 C=EC"}
      ,{"location": "Jersey Pt" , "datatype" : "EC", "pattern1": "A=RSAN018 B=JER C=EC E=15MIN", "pattern2": "B=RSAN018 C=EC"}
      ,{"location": "3 Mile Slough", "datatype": "EC", "pattern1": "A=3MILE_SL C=EC", "pattern2": "B=SLTRM004 C=EC" }
      ,{"location": "Old River @ Bacon Island", "datatype": "EC", "pattern1": "A=ROLD024 B=BAC C=EC", "pattern2": "B=ROLD024 C=EC" }
     ]
    }
    """
def config_data_flow():
    return """
    { 
    "file1" : "Z:/DSM2_v81_Beta_Release/observed_data/Observed_EC.dss",
    "file2" : "Z:/DSM2_v81_Beta_Release/studies/historical_qual_ec_v81/output/historical_v81.dss",
    "stime" : "01JAN2000 0000",
    "etime" : "01JAN2001 0000",
    "locations": [ 
      {"location": "Martinez" , "datatype" : "EC", "pattern1": "A=RSAC054 B=MTZ C=EC E=1HOUR", "pattern2": "B=RSAC054 C=EC"}
      ,{"location": "Mallard" , "datatype" : "EC", "pattern1": "A=RSAC075 B=MAL C=EC E=IR-MON", "pattern2": "B=RSAC075 C=EC"}
      ,{"location": "Collinsville" , "datatype" : "EC", "pattern1": "A=RSAC081 B=CLL C=EC E=IR-DAY", "pattern2": "B=RSAC081 C=EC"}
      ,{"location": "Anitoch" , "datatype" : "EC", "pattern1": "A=RSAN007 B=ANH C=EC E=IR-MON", "pattern2": "B=RSAN007 C=EC"}
      ,{"location": "Emmaton" , "datatype" : "EC", "pattern1": "A=RSAC092 B=EMM C=EC E=IR-DAY", "pattern2": "B=RSAC092 C=EC"}
      ,{"location": "Jersey Pt" , "datatype" : "EC", "pattern1": "A=RSAN018 B=JER C=EC E=15MIN", "pattern2": "B=RSAN018 C=EC"}
      ,{"location": "3 Mile Slough", "datatype": "EC", "pattern1": "A=3MILE_SL C=EC", "pattern2": "B=SLTRM004 C=EC" }
     ]
    }
    """

if __name__ == '__main__':
    config=json.loads(config_data_ec())
    file1=config['file1']
    file2=config['file2']
    if config['file3']: file3=config['file3']   
    stime = config['stime']
    etime = config['etime']
    for loc in config['locations']:
        location=loc['location']
        datatype=loc['datatype']
        pattern1=loc['pattern1']
        pattern2=loc['pattern2']
        #FIXME: pattern3 is same as pattern2 for right now
        if file3: pattern3=pattern2
        run_compare(location, datatype, file1, pattern1, file2, pattern2, file3, pattern3, stime, etime)
    print 'DONE!!!!'