import os
from min_cost_compliance2 import np, dtm, days, alt, dss_retrieve_ts, plt, number_intervals, ticks_to_time, logging, ComplianceConstraint, time_sequence, WaterCost, ControlLowerBound

def standards():
    # "file "output/for1_%s_cu4_dxc0.dss"
    # "path": "/QUAL8.1.2/RSAC092/EC//15MIN/FOR1_%s_CU4_DXC0+FROM-ALL/
    outfile = "output/%s.dss" % alt
    fpart = "%s+FROM-ALL" % alt.upper()
    return [
      {"name": "CCC Rock Slough",
      "file": outfile,\
       "path": "/QUAL8.1.2/CHCCC006/EC//15MIN/%s/" % fpart,\
       "value": 1000.,\
       "active":True},
      {"name": "CVP Intake",
       "file": outfile,\
       "path": "/QUAL8.1.2/CHDMC004/EC//15MIN/%s/" % fpart,\
       "value": 1000.,
       "active":True},
      {"name": "SF Mokelumne at Terminous",
       "file": outfile,\
       "path": "/QUAL8.1.2/RSMKL008/EC//15MIN/%s/" % fpart,\
       "value": 540.,
       "active": True},       
       {"name": "SJR @ San Andreas",
        "file": outfile,\
        "path": "/QUAL8.1.2/SJR_SAN_ANDREAS/EC//15MIN/%s/" % fpart,\
       "value": 870.,\
       "active":True},
      {"name": "Northbay/Barker",
       "file": outfile,\
       "path": "/QUAL8.1.2/SLBAR002/EC//15MIN/%s/" % fpart,\
       "value": 1000.,\
       "active":True},        
      {"name": "SWP",
      "file": outfile,\
       "path": "/QUAL8.1.2/CHSWP003/EC//15MIN/%s/" % fpart,\
       "value": 500.,\
       "active":dtm.datetime(2014,6,15)},
      #{"name": "Threemile",
      #"file": outfile,\
      #"path": "/QUAL8.1.2/3MILE_SL/EC//15MIN/%s/" % fpart,\
      #"value": 2780.,\
      #"active": True},
      {"name": "Emmaton",
      "file": outfile,\
       "path": "/QUAL8.1.2/RSAC092/EC//15MIN/%s/" % fpart,\
       "value": 2780.,\
       "active": True},       
      ]  
    
def min_cost_compliance():
    from scipy.optimize import fmin_cobyla
    
    no_of_weeks = 2    
    d0 = dtm.datetime(2014,4,27)
    d1 = d0 + dtm.timedelta(days=no_of_weeks*7)
    
    print "d1", d1   #d1 2014-05-11
    
    control_period = (d0,d1)
    #control_period = (dtm.datetime(2014,4,27),dtm.datetime(2014,10,5))

    
    control_step = days(7)
    x0 = np.ones(no_of_weeks)*3500.
    qmin = [2925.] + 1*[4157]
    #qmin = [2925.] + 4*[4157]+4*[2800]+14*[2800.]
    assert len(x0) == len(qmin)
    #x0 = np.array([2925.000,4157.000,4159.286,5041.825,4157.000,4000.000,4047.321,4602.426,4000.000,3975.054,4245.750,4751.072,3603.672,3570.053,4386.907,4650.922,3344.813,3577.872,3764.932,3572.622,3322.971,3934.002,7410.723])
    x0 = np.array([2925.000, 4157.000])
    x0 = np.maximum(x0,qmin)+1000.
    control_description = ("timeseries/optimizer.dss",\
                           "/FILL+CHAN/RSAC155/FLOW//1DAY/OPTIMIZER/")
    salt_standards = standards()
    #constraint_period = (m.dtm.datetime(2014,4,27),m.dtm.datetime(2014,11,1))
    constraint_period = control_period
    
    min_flow = 1000.

    # Retrieve original Sac flow and NDO.
    base_file = "ndo_echo.dss"
    base_path_ndo = "/DSM2/NDO_BASE/FLOW//1DAY/%s/" % alt.upper()
    base_ndo = dss_retrieve_ts(base_file,base_path_ndo,unique = True)
    base_path_sac = "/DSM2/RSAC155/FLOW_BASE//1DAY/%s/" % alt
    sac = dss_retrieve_ts(base_file,base_path_sac,unique = True)
    ndo = base_ndo
    
    do_plot = True
    if do_plot:
        plt.plot(sac.times,sac.data,label="Sac")
        plt.plot(base_ndo.times,base_ndo.data,label="NDO")
        plt.legend()
        plt.show()
   
    base_flows = {"sac":sac,"ndo":ndo}
    
    # Give a reasonable lower and upper bound for ndo flow. 
    # The lower bound should be low enough to ensure it is non-compliant
    # Similarly, upper value must be surely compliant. 
    # Make the interval on the wide side -- the algorithm will 
    # chop it down VERY quickly.
    is_lower_bound = True
    constraint_step = control_step
    n = number_intervals(constraint_period[0],\
                         constraint_period[1],\
                         constraint_step)

    seq = [ticks_to_time(tck) for tck 
           in time_sequence(constraint_period[0],control_step,n+1)]
        
    water_cost = WaterCost(0.1)
    constraints= []
    for s in standards():
        active = s["active"]
        if not(active):
            print "Station inactive: %s" % s["name"]
        for i in range(len(seq)-1):
            period = (seq[i],seq[i+1])
            if type(active) == dtm.datetime:
                if period[1] <= active:
                    print "Discarded constraint"
                    print s["name"]
                    print seq[i]
                    continue                    
            c = ComplianceConstraint(s,period,is_lower_bound)    
            constraints.append(c)
    
    for i in range(len(x0)):
        constraints.append(ControlLowerBound(qmin[i],i))
    
    x = fmin_cobyla(water_cost, 
                    x0, 
                    constraints, 
                    args=(base_flows,min_flow,control_period,control_step), 
                    consargs=(base_flows,min_flow,control_description,control_period,control_step),
                    rhobeg=400.0, 
                    rhoend=1.0,
                    maxfun=1000, 
                    disp=2)

    
if __name__ == '__main__':
    #test_ndo_control_to_flow()
    logfilename = os.path.join(os.path.dirname("."),"cobyla_log.txt")
    logging.basicConfig(filename=logfilename,filemode='w',level=logging.DEBUG)
    logging.info("logging enabled")    
    min_cost_compliance()
    
    
    