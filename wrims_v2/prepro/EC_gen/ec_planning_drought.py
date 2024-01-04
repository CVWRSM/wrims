#2/12/2014: for forecast, must use one month historical data to warm up 
import os, sys, string
import subprocess
import datetime as  dtm
#how to handle missing data?
# merge rsac155 data filtering out values of 9900. as missing
from vtools.datastore.dss.api import *
from vtools.functions.api import *
from vtools.data.timeseries import *
from scipy.ndimage.filters import *
import logging

UNIT="unit"
INST="inst"



# def working_ndo(startdt,enddt):
#     """Retrieves daily NDO from historical and realtime forecasts.
#        Interpolates them and merges them with historical taking precedent
#     """
#     twindow=(startdt,enddt)
#     path1 = "/HIST+CHAN/NDOI/FLOW//1DAY/DWR-DMS/"
#     logging.debug("Loading historical NDOI (net delta outflow) from file %s" % histdss)
#     ts_1day_NDOI = dss_retrieve_ts(histdss,path1,time_window = twindow)   #d:/delta/dsm2_v81/studies/historical//prepro/DSM2working.dss
#     ts_1hr_NDOI_flow = interpolate_ts(ts_1day_NDOI,"1HOUR",method=PREVIOUS)
#     ts_1hr_NDOI_flow = merge(ts_1hr_NDOI_flow,ts_1hour_empty)
#     path1 = "/FILL+CHAN/NDOI/FLOW//1DAY/"+YEAR+"/"
#     logging.debug("Loading predicted NDOI from file %s path %s" % (wkdss,path1))
#     ts_1day_NDOI = dss_retrieve_ts(wkdss,path1,time_window = (startdt,enddt+days(1)))  #'d:/delta/dsm2_v81/studies/Planning/prepro/planning-update.dss'
#     ts_1hr_NDOI_flow_forecast = interpolate_ts(ts_1day_NDOI,"1HOUR",method=PREVIOUS)
#     ts_1hr_NDOI_flow = merge(ts_1hr_NDOI_flow, ts_1hr_NDOI_flow_forecast)
#     ts_1hr_NDOI_flow = ts_1hr_NDOI_flow.window(startdt, enddt)
#     return ts_1hr_NDOI_flow

    
    
def ec_doc(ndo,startdt,enddt):
    if not ndo.interval in (days(1), hours(1)):
        print "NDO interval should be 1 day or 1 hour"
    elif ndo.interval == days(1):
        ndo = interpolate_ts(ndo,hours(1), method = PREVIOUS)
        ndo.data = gaussian_filter1d(ndo.data,sigma = 24)
    ndo = ndo.window(startdt,enddt)


    twindow=(startdt,enddt)
    #martinez
    #export to file ec.txt in /prepro/temp
    #the file has 7 columns
    # RSAC054 1hr ec, RSAC054 1hr EC, RSAC075 1hr EC, RSAC045 1 hr stage
    #RSAC054 1 hour stage # RSAc075 1 hour stage, /smooth/data/ 1 hour
    #for planning columns 1, 2, and 3 are all empty
    logging.debug("MRZ MAL EC = %s time window = %s %s " % (histdss,startdt,enddt))
    logging.debug("Loading MRZ EC") 
    path1 = "/HIST+CHAN/RSAC054/EC//1HOUR/DWR-DMS/"
    path2 = "/HIST+CHAN/RSAC054/EC//15MIN/DWR-DMS/"
    ts_1hr_rsac054_ec = dss_retrieve_ts(histdss,path1,time_window = twindow)
    props = ts_1hr_rsac054_ec.props
    ts_1hr_rsac054_ec = merge(ts_1hr_rsac054_ec,ts_1hour_empty) #column 1
    
    ts_15min = dss_retrieve_ts(histdss,path2,time_window = twindow)
    ts_15min = merge(ts_15min,ts_15min_empty)         #force time stamp
    ts_1hr_avg_rsac054_ec = resample(ts_15min,"1hour")       
    ts_1hr_rsac054_ec = merge(ts_1hr_rsac054_ec,ts_1hr_avg_rsac054_ec)  #column #2

    logging.debug("Loading MAL EC")
    path1 = "/HIST+CHAN/RSAC075/EC//1DAY/DWR-DMS/"
    path2 = "/HIST+CHAN/RSAC075/EC//1HOUR/DWR-DMS/"
    ts_1day_rsac075 = dss_retrieve_ts(histdss,path1,time_window = twindow)
    ts_1hr_rsac075 = dss_retrieve_ts(histdss,path2,time_window = twindow)
    ts_1hr_int_rsac075 = interpolate_ts(ts_1day_rsac075,"1HOUR", method=LINEAR)
    ts_1hr_rsac075_ec = merge(ts_1hr_rsac075,ts_1hr_int_rsac075,ts_1hour_empty)
    logging.debug("Loaded MAL EC")
    

    props={UNIT:"UMHOS/CM",INST:"INST-VAL"}
    if not startdt:
        logging.warning("No start date")
        logging.debug("No start date")
    sdt = increment(startdt,-minutes(195),1)
    edt = increment(enddt,-minutes(195),1)
    twindow = (sdt,edt)
    logging.debug("Carquinez, MRZ, MAL stage, astrodss = %s time window = %s %s " % (astrodss,sdt,edt))

    logging.debug("Loading Carquinez stage")
    path1 = "/DELTA/RSAC045/STAGE//15MIN/DWR_ASTRO_88_98/" 
    ts_15min = dss_retrieve_ts(astrodss,path1,time_window =twindow )
    ts_15min_shift = shift(ts_15min,minutes(195))  
    #the original code is supposed to move 4 hour
    ts_1hr_rsac045_stg = resample(ts_15min_shift,hours(1))   #column #4
    logging.debug("Loaded Carquinez Stage")

    #    ts_1hr_rsac045_stg = ts_1hr_rsac045_stg.copy(start=sdatetime, end=edatetime,left=TRUE,right=TRUE)  #column#4
    
    logging.debug("Loading MRZ Stage")
    path1 = "/DELTA/RSAC054/STAGE//15MIN/DWR_ASTRO_90_08/"
    ts_15min = dss_retrieve_ts(astrodss,path1,time_window = twindow)
    ts_15min_shift = shift(ts_15min,minutes(195))   
    ts_1hr_rsac054_stg = resample(ts_15min_shift,hours(1))  #column #5
    logging.debug("Loaded MRZ Stage")
    
        
    logging.debug("Loading MAL Stage")
    path1 = "/DELTA/RSAC075/STAGE//15MIN/DWR_ASTRO_90_08/"
    ts_15min = dss_retrieve_ts(astrodss,path1,time_window = twindow)
    ts_15min_shift = shift(ts_15min,minutes(195))
    ts_1hr_rsac075_stg = resample(ts_15min_shift,hours(1))  #column #6
    logging.debug("Loaded MAL Stage")

    
    #write to tmp/ec.txt
    logging.debug("Writing input file for ECKalman")
    lines=[]
    ts_times = ts_1hour_empty.times
    tslen = len(ts_times)
    for i in range(tslen):
        '''line="%14s\t%12s\t%12s\t%12s\t%12s\t%12s\t%12s\t%12s\n" % (ts_times[i],
        ts_1hr_rsac054_ec.data[i],ts_1hr_avg_rsac054_ec.data[i],ts_1hr_rsac075_ec.data[i],
        ts_1hr_rsac045_stg.data[i],ts_1hr_rsac054_stg.data[i],ts_1hr_rsac075_stg.data[i],
        ndo.data[i])'''
        a = "%12.6f" % ts_1hr_rsac045_stg.data[i]
        b = "%12.6f" % ts_1hr_rsac054_stg.data[i]
        c = "%12.6f" % ts_1hr_rsac075_stg.data[i]
        d = "%12.4f" % ndo.data[i]
        #to consistent with vplotter, use somewhat different for columns 1,2,3
        #line="%14s\t%12s\t%12s\t%12s\t%%s\t%%12.6f\t%12.6f\t%12.4f\n" % (ts_times[i],
        #ts_1hour_empty.data[i],ts_1hour_empty.data[i],ts_1hour_empty.data[i],
        #ts_1hr_rsac045_stg.data[i],ts_1hr_rsac054_stg.data[i],ts_1hr_rsac075_stg.data[i],
        #ndo.data[i])
        line="%14s\t%12s\t%12s\t%12s\t%s\t%s\t%s\t%s\n" % (ts_times[i],
                      ts_1hr_rsac054_ec.data[i],ts_1hr_rsac054_ec.data[i],\
                      ts_1hr_rsac075_ec.data[i],a,b,c,d)
        lines.append(line)
    fout = open("ec.txt","w")
    for i in range(tslen):
        fout.write("%s" % lines[i])
    fout.close()
    logging.debug("Wrote ec.txt input file for ECKalman")
#     ECKalman_in = os.path.join(REAL,"ECKalman.inp")
#     ECKalman_out = os.path.join(REAL,"ECKalman.out")
#     ECKalman_exe = os.path.join(REAL,"ECKalman.exe")
    ECKalman_in =  "ECKalman.inp"
    ECKalman_out = "ECKalman.out"
    ECKalman_exe = "ECKalman.exe"
    OK = transfer_input("ec.txt",ECKalman_in)
    args = [ECKalman_exe,ECKalman_in,ECKalman_out]
    logging.debug("Calling ECKalman: %s" % args)
    OK = subprocess.call(args) 
    logging.debug("Returned")
    if OK ==0:
        logging.debug("ECKalman returned with no error code")
    else:
        logging.error("ECKalman returned with non-zero exit code: %s " % OK)
        raise Exception("ECKalman returned with non-zero exit code")

    logging.debug("Processing ECKalman output into time series")
    #datetime operation
    sdt = increment(startdt,hours(4),1)  #
    mtzecfill = createrts(ECKalman_out,sdt,"1hour",props)
    #ts_1hr_rsac054_ec = merge(ts_1hr_rsac054_ec,mtzecfill)  #fillin
    ts_1hr_rsac054_ec = mtzecfill  #used smoothed data
    path1 = "/FILL+CHAN/RSAC054/EC//1HOUR/OPTIMIZER/"
    logging.info("Generated EC. Writing time series")
    dss_store_ts(mtzecfill,"output/mtz_ec.dss",path1)
    logging.debug("Wrote time series")
    #'d:/delta/dsm2_v81/studies/Planning/prepro/planning-update.dss'


def set_prop(ts_merge,ts_1day):
    ts_merge.props["aggregation"] = ts_1day.props["aggregation"]
    ts_merge.props["timestamp"] = ts_1day.props["timestamp"]
    ts_merge.props["unit"] = ts_1day.props["unit"]

def transfer_input(infilename,outfilename):
    import string
    logging.info("Tranfering input from %s to %s" % (infilename,outfilename))
    infile=open(infilename,"r")
    outfile=open(outfilename,"w")
    firstused=4  #vplotter use 5 but for us it is four, skip 0,1,2,3 four lines
    lines=infile.readlines()[firstused:]
    for line in lines:
        if line:
            line=string.replace(line,"-1.#IND","-9999.0") #for hec dss missing data is -901
            line=string.replace(line,"nan","-9999.0") #for hec dss missing data is -901
            line = line[19:].strip()        #strip tab, date, and time
            outfile.write(line+"\n")
    infile.close()
    outfile.close()
    logging.debug("Input transferred")
    return 0

#create regular time series
def createrts(infilename,start,interval,props):
    data = np.loadtxt(infilename)
    my_rts=rts(data,start,interval,props)
    return my_rts
    
def gen_ec(argv=None,ndo=None):

    global updss,histdss,astrodss
    global REAL,bindir,histdir,tempdir,tsdir
    global dicudir,dicu5file,dicu5in1file,nodcufile,precipfile,panevapfile
    global sdatetime,edatetime,twin
    global OLDHIST,YEAR, logfilename
    #the following inputs are for IEP
    global year,beginmonth,nomonth
    global etyr,etmon, panevapin, dicuevap, panevapmm,et0,ratio,etadjfactor
    #first create some empty timeseries so they can use in the future
    global ts_1hour_empty,ts_15min_empty,mondic

    mondic={"1":"JAN","2":"FEB","3":"MAR","4":"APR","5":"MAY","6":"JUN","7":"JUL","8":"AUG","9":"SEP","10":"OCT","11":"NOV","12":"DEC"}
    
    if argv is None:
        argv = sys.argv
        if len(argv)<2:  #D:/delta/dsm2_v81/studies/planning/prepro/temp/runpara.txt
            print "usuage: python process_planning.py folder/runpara.txt"  #parameters from VBA created or self-created file
            print "not enough parameters for running process_planning.py"
            return 1
    tmpfile = argv[1]
            
    logging.info("Reading from parameter file: %s" % tmpfile)
    infile = open(tmpfile,"r")
    lines = infile.readlines()
    infile.close()
    #seperate keywords and contents
    paradic={}
    for line in lines:
        strline=line.strip()[1:-1]
        #find location of :
        loc=strline.find(":")
        if loc>-1:
            paradic[strline[0:loc]] =  strline[loc+1:].strip()
    logging.debug("Loaded configuration parameters")                
    #dsm2 dir
    Start_Planning_Date  = paradic["Start_Planning_Date"]

    End_Planning_Date = paradic["End_Planning_Date"]
    
    logging.debug("Processing dates")

    
    sdatetime = Start_Planning_Date + " 00:00"
    syear = int(Start_Planning_Date.split("/")[2])
    smon = int(Start_Planning_Date.split("/")[0])
    if smon>9:
        syear=syear + 1
    #edatetime = "1/1/" + str(syear+1) + " 00:00"
    edatetime = End_Planning_Date + " 00:00"
    
    #first create some empty timeseries so they can use in the future months(1)
    startdt = parse_time(sdatetime)
    enddt = parse_time(edatetime)
    logging.info("Start date: %s" % startdt)
    logging.info("End date: %s" % enddt)
    
    #consider 30-day historical data
    histdays = 25   #i.e.720 hours
    startdt = startdt-days(histdays)
    twin=(startdt,enddt)

    logging.debug("Begin retrieving data")
 
    #create empty hour time series
    #startdt = startdt - parse_interval("4hour")
    numinterval = number_intervals(startdt,enddt,hours(1))+1
    arr = []
    for i in range(numinterval):
        arr.append(numpy.NaN)
    ts_1hour_empty = rts(arr,startdt,hours(1))

    #create empty 15min time series
    numinterval = number_intervals(startdt,enddt,minutes(15))+1
    arr = []
    for i in range(numinterval):
        arr.append(numpy.NaN)
    ts_15min_empty = rts(arr,startdt,minutes(15))
    
    logging.info("Processing EC")
    print "processing EC..."

    logging.debug("Calling ec_doc")
    ec_doc(ndo,startdt,enddt)
    logging.debug("Back from ec_doc")

def echo_outflow(echofile,alt):
    path = "/DSM2/NDO_BASE/FLOW//1DAY/%s/" % alt.upper()
    print "Echo file %s path %s" % (echofile,path)
    ndo = dss_retrieve_ts(echofile,path,unique = True)
    return ndo
    
if __name__ == "__main__":

    logfilename = os.path.join(os.path.dirname("."),"ec_gen.log")
    logging.basicConfig(filename=logfilename,filemode='w',level=logging.DEBUG)
    logging.info("logging enabled")   
    
    echo = "./input/ndo_echo.dss"
    alt = "FOR3_DB3-JUN_CU5_DXC0"
    
    astrodss = os.path.join("./input/astro-20years.dss")

    #HIST_DIR_Timeseries = HIST_DIR +"timeseries/"
    # if there exist any data for newrun, delete them first  

    histdss = "./input/DSM2working_extract.dss"  #historical working dss file includes EC and all other data
    ndo = echo_outflow(echo,alt)
    gen_ec([None,"./runpara.txt"],ndo)
    #sys.exit(gen_ec())
