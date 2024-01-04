import sys, vutils
from vutils import *



#attr = DataSetAttr(DataType.REGULAR_TIME_SERIES,'',unit,'TIME','PER-AVER')

def exactPath(dssfile,ds_arr,interval,starttime,path,attr):
    
    rts = RegularTimeSeries(path,starttime,interval,ds_arr,None,attr)
    writedss(dssfile,path,rts)
    return None

def arr2dss(dssfile,ds_arr,interval,starttime,path,attr):
    
    rts = RegularTimeSeries(path,starttime,interval,ds_arr,None,attr)
    writedss(dssfile,path,rts)
    return None

def array2dss(outFile, ds_arr, startTime, pathnameString, unit, interp_type='PER-AVER'):
    
    path = Pathname.createPathname(pathnameString)
    ePart = path.getPart(Pathname.E_PART)
    attr = DataSetAttr(DataType.REGULAR_TIME_SERIES,'',unit,'TIME',interp_type)
    rts = RegularTimeSeries(str(path),startTime,ePart,ds_arr,None,attr)
    writedss(outFile,str(path),rts)
    return None

def copyDssToFuture_calendarYear(inFile, outFile, beginYR, sequentialYR, futureYR=-99, epart="1MON"):
    
    endYR = beginYR + sequentialYR - 1

    tw = timewindow("31JAN"+str(beginYR)+" 2400 - 31DEC"+str(endYR)+" 2400")  
    beginTime_new = "31JAN"+str(futureYR)+" 2400"

    g = opendss(inFile)
    g_all = findparts(g,e=epart)

    for gi in g_all:
        ref = DataReference.create(gi,tw)
        ds =  ref.getData()
        y_arr = ds.getYArray()
        interval = epart
    
        path = ref.getPathname()
        path.setPart(Pathname.F_PART,str(beginYR)+"_"+str(endYR))
        pathname_new = str(path)
        attr = ds.getAttributes()
    
        arr2dss(outFile, y_arr, interval, beginTime_new, pathname_new, attr)
        
def copyDssToFuture_waterYear(inFile, outFile, beginWY, sequentialYR, bufferYR=0, futureWY=-99, outFpart="", epart="1MON"):
    
    beginCY = beginWY - 1
    endCY = beginCY + sequentialYR + bufferYR - 1 + 1
    endWY = beginWY + sequentialYR + bufferYR - 1
    futureCY = futureWY - 1

    tw = timewindow("31OCT"+str(beginCY)+" 2400 - 30SEP"+str(endCY)+" 2400")  
    beginTime_new = "31OCT"+str(futureCY)+" 2400"

    g = opendss(inFile)
    g_all = findparts(g,e=epart)

    for gi in g_all:
        ref = DataReference.create(gi,tw)
        ds =  ref.getData()
        y_arr = ds.getYArray()
        interval = epart
    
        path = ref.getPathname()
        
        if not outFpart:
            outFpart = str(beginWY)+"_"+str(endWY)
            
        path.setPart(Pathname.F_PART,outFpart)
        pathname_new = str(path)
        attr = ds.getAttributes()
    
        arr2dss(outFile, y_arr, interval, beginTime_new, pathname_new, attr)