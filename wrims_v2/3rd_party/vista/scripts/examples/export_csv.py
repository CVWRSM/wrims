from vtimeseries import time
NaN=float('nan')
def export_csv(ref,file):
    d = ref.data
    f=open(file,'w')
    for e in d:
        f.write("%s,%s\n"%(e.x, "" if e.y == -901.0 or e.y == -902.0 else e.y))
    f.close()
if __name__=='__main__':
    file='Z:/DSM2_v81_Beta_Release/studies/historical_qual_ec_v81/output/historical_v81.dss'
    from vutils import opendss
    import vdss
    ref = vdss.findpath(opendss(file),'//sltrm004/flow')[0]
    print 'Exporting ref: %s'%ref
    export_csv(ref,'Z:/temp/sltrm004_flow.csv')
