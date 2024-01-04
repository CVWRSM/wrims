#=====================================================================================
# example: load a study 
#=====================================================================================

import sys, vutils
import vutils as vu
from os import path 
from scripts.wrims2.study import Study
from scripts.wrims2.runGroup import RunGroup
from scripts.tool import LogUtils, Param, LookupTable, FileUtils, DssVista
Param.mainScriptPath = path.abspath(__file__)
Param.mainScriptDir  = path.dirname(Param.mainScriptPath)
LogUtils.initLogging()


# parts are exact except for the blank parts
def getDataRef(group, path, timewindow):
    
    p = getExactPartsFromPath(path)    
    data = vu.findparts(group, a=p[0] , b=p[1] , c=p[2] , d=p[3] , e=p[4], f=p[5])[0]
    dataReference = vu.DataReference.create(data,timewindow)
    return dataReference

# parts are exact except for the blank parts
def getExactPartsFromPath(path):
    
    p_in = path.split('/')[1:-1]
    print p_in
    p = []
    for part in p_in:
        if part:
            part = "^"+part+"$"
        p.append(part)
    print p
    return p

beginYR = 2012
endYR = 2013


dssFile ='D:/cvwrsm/trunk/wrims2_vscript/test.dss'
beginTime = "31OCT"+str(beginYR)+" 2400"
endTime = "30SEP"+str(endYR)+" 2400"


tw = vu.timewindow(beginTime + " - " + endTime)  

pathA = '/CALLITE/S_FOLSM/STORAGE//1MON/1929_1929/'
pathB = '/CALLITE/S_OROVL/STORAGE//1MON/1929_1929/'


# "^"+part+"$"

apart = 'CALLITE'
cpart =  'STORAGE'
dpart = ''
epart = '1MON'
fpart = '1929_1929'


g = vu.opendss(dssFile)

da = getDataRef(g, pathA, tw)


gb = vu.findparts(g,a=apart , b='S_OROVL' , c=cpart , d=dpart , e=epart, f=fpart)[0]
db = vu.DataReference.create(gb,tw)

dc = da + db

ds =  dc.getData()
y_arr = ds.getYArray()
interval = epart
pathname_new = '/CALLITE/S_FOLSM_plus_Orovl/STORAGE//1MON/created/'
attr = ds.getAttributes()
        
DssVista.arr2dss(dssFile, y_arr, interval, beginTime, pathname_new, attr)

print 'completed.'





    
    

    
    