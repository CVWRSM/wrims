import re
from vtools.datastore.dss.api import *
from vtools.data.api import *
from vtools.functions.api import *
import matplotlib.pyplot as plt
import datetime as dtm
nreport = 20  # number of files at which progress is reported


def dsm2_total_inflow(echofile,outfile,alt,start_date):
    total = None
    dicu = None
    with open(echofile) as f:
        lines = f.read()
        inflow_blocks = [("BOUNDARY_FLOW",2),("SOURCE_FLOW",3),("SOURCE_FLOW_RESERVOIR",3)]
        for block in inflow_blocks:
            blockname = block[0]
            sources = re.search(r"%s\n(.*?)END" % blockname,lines,flags=re.MULTILINE | re.DOTALL)
            assert sources
            for i,line in enumerate(sources.group(1).split("\n")[1:]):
                if not line or len(line) < 4: continue
                sign,fillin,file,path = line.strip().split()[2:6]
                sign = float(sign)
                if file == "constant":
                    total = total + float(path)
                    continue
                end_date=dtm.datetime(2014,12,1) # todo: hardwire
                ts = dss_retrieve_ts(file,path,time_window=(start_date,end_date))
                if "RSAC155" in path:
                    print "Sacramento flow identified as %s" % path
                    if ("optimizer" in path.lower()):
                        ValueError("Found OPTIMIZER in the echo path. This means the Sacramento flow is the product of an old optimizer run, not the base flow for the scenario")
                    sac_ts = ts
                if ts.interval == months(1):
                    ts = interpolate_ts(ts,days(1),method = PREVIOUS)
                if ts.interval == minutes(15):
                    ts = period_ave(ts,days(1))
                if "dicu" in path.lower():
                    dicu = ts*sign if not dicu else dicu + sign*ts
                
                if i%nreport == 0 or blockname=="BOUNDARY_FLOW":
                    print "%s %s %s (progress reported every %s files for DICU)" %\
                          (file,path,sign,nreport)
                total = ts*sign if not total else total+sign*ts
    print "Writing data"
    total.props[TIMESTAMP]=PERIOD_START
    total.props[AGGREGATION] = MEAN    
    total.props[UNIT]="CFS"
    dss_store_ts(total,outfile,"/DSM2/NDO_BASE/FLOW//1DAY/%s/" % alt.upper())
    sac_ts.props[TIMESTAMP]=PERIOD_START
    sac_ts.props[AGGREGATION] = MEAN    
    sac_ts.props[UNIT]="CFS"
    dss_store_ts(sac_ts,outfile,"/DSM2/RSAC155/FLOW_BASE//1DAY/%s/" % alt)
    dicu.props[TIMESTAMP]=PERIOD_START
    dicu.props[AGGREGATION] = MEAN    
    dicu.props[UNIT]="CFS"
    dss_store_ts(-dicu,outfile,"/DSM2/CU/FLOW_BASE//1DAY/%s/" % alt)
    
if __name__=='__main__':
    import sys
    infile = sys.argv[1]
    print "Input file: %s" % infile
    outfile = sys.argv[2]
    alt = re.search(r""".*hydro_echo_(.*)\.inp""",infile)
    if alt:
        print "Alternative: %s" % alt.group(1)
        alt = alt.group(1)
    else:
        raise ValueError("Alternative not determined from filename")
    print "Alternative: %s" % alt
    sdate = sys.argv[3]
    print "Start date (yyyy-mm-dd): %s" % sdate
    date_parts = [int(x) for x in sdate.split("-")]
    start_date=dtm.datetime(*date_parts)
    dsm2_total_inflow(sys.argv[1],sys.argv[2],alt,start_date)