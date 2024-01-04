import sys, glob, urllib, re, csv, datetime
#from vista.set import *
from vista.time import TimeFactory
from vutils import *
TF = TimeFactory.getInstance()
"""
Reads CSV files of 'continuous'-type data from the DWR
Water Data Library (flow and stage)
Optional inputs are the .csv files to read, and the .dss
file to write to. If not given on command line, defaults
are used in the program.

Typical csv format:
"Time","A02100",
"and",233.00,
"Date","Point","Qual"
10/01/2008 00:00:13,       3.160,      1
10/01/2008 00:15:13,       3.110,      1
10/01/2008 00:30:13,       3.050,      1
"""
if __name__ == '__main__':
    WDLDir = 'Y:/Observed Data/WDL/'
    dssfile = WDLDir + 'wdl_ts.dss'
    csvfiles = WDLDir + '*.csv'
    if len(sys.argv) > 1:  # file(s) on command line
        for arg in sys.argv[1:]:
            pass
    #
    csvfilesList = []
    for f in glob.glob(csvfiles):
        csvfilesList.append(f)
    # loop over csv files, parsing data
    # and writing to DSS file
    APart = 'WDL'
    EPart = 'IR-DAY'
    for csvFile in csvfilesList:
        print 'Reading file',csvFile
        csvReader = csv.reader(open(csvFile, 'rb'))
        yVals = []
        dtObjList = []
        for rowList in csvReader:        
            if rowList[0].upper().find('TIME') > -1:
                station = rowList[1].upper().replace('"','')
                continue
            if re.search('23[23]',rowList[1]):
                CPart = 'STAGE'
                Units = 'FEET'
                FPart = 'OBSERVED'
                perType = 'INST-VAL'
                code = int(float(rowList[1]))
                if code == 232:
                    FPart = 'RAW'
                elif code == 233:
                    FPart = 'NGVD'
                continue
            if re.search('[0-9][0-9]/[0-9][0-9]/[12][0-9]+ [012][0-9:]+$',rowList[0]):
                # date time, data value, data quality line
                dtObj = TF.createTime(rowList[0],'MM/dd/yyyy HH:mm:ss')
                dtObjList.append(dtObj)
                try: dataVal = float(rowList[1])
                except: dataVal = Constants.MISSING_VALUE
                dataQual = int(rowList[2])
                if not (dataQual == 1 or dataQual == 2 or dataQual == 40):
                    dataVal = Constants.MISSING_VALUE
                yVals.append(dataVal)
            #
        #
        pathname = Pathname.createPathname([APart,station,CPart,'',EPart,FPart]).getFullPath()
        ds = IrregularTimeSeries(pathname,dtObjList,yVals)
        ds.getAttributes().setYUnits(Units)
        writedss(dssfile,pathname,ds)
    #
#