from vdss import *
from vdisplay import *
from vista.set import Pathname
from vtimeseries import *
#calculations on the dicu pathnames
def do_sum(cpart,dicufile):
    g=opendss(dicufile)
    g=findparts(g,c=cpart)
    ts=None
    for ref in g:
        if ts==None:
            ts=ref.data
        else:
            ts+=ref.data
    path=Pathname.createPathname(ts.name)
    path=set_part(path,'ALL',Pathname.B_PART)
    ts.name=str(path)
    return ts
#
def do_scale(cpart,scale,outfile, twstr=None):    
    g=opendss(dicufile)
    g=findparts(g,c=cpart)
    if twstr:
        tw=timewindow(twstr)
    for ref in g:
        if tw:
            ref=DataReference.create(ref,tw)
        ds=ref.data*scale
        writedss(outfile,ds.name,ds)
#
if __name__=='__main__':
    dicufile=r'Z:/studies/drought_studies/boundary_flows_stage_ec_for_rma_compare/dicu_201412_CU4.dss'
    outfile=r'Z:/studies/drought_studies/boundary_flows_stage_ec_for_rma_compare/boundary_flow_stage_ec.dss'
    #outfile=r'D:\models\DSM2v8.1.x\Historical_MiniCalibration_811_MTZ_ts_corrected\timeseries\dicu_201004_minus20.dss'
    cparts=['DIV-FLOW','DRAIN-FLOW','SEEP-FLOW']
    twstr="01JUN2013 0000 - 30NOV2014 2400"
    DO_ADJUSTMENT=False
    if DO_ADJUSTMENT: 
        for cpart in cparts:
            if cpart=='DIV-FLOW':
                do_scale(cpart,1.0,outfile,twstr)
            elif cpart=='DRAIN-FLOW':
                do_scale(cpart,1.0,outfile,twstr)
            elif cpart=='SEEP-FLOW':
                do_scale(cpart,1.0,outfile,twstr)
            print 'Done with C Part: %s'%cpart
    else:
        timeseries={}
        for cpart in cparts:
            ts=do_sum(cpart, dicufile)
            plot(ts)
            writedss(outfile,'/DICU-CAlC/%s-ALL/%s//1MON/DWR-BDO-CALC/'%(cpart,cpart),ts)
            timeseries[cpart]=ts
        netcu=timeseries['DIV-FLOW']-timeseries['DRAIN-FLOW']+timeseries['SEEP-FLOW']
        plot(netcu)
        writedss(outfile,'/DICU-CALC/NET-CU-ALL/FLOW//1MON/DWR-BDO-CALC/',netcu)
