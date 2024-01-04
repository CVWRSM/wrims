from vtools.data.api import *
from vtools.functions.api import *
from vtools.datastore.dss.api import *
import datetime as dtm
from datetime import date
import matplotlib.pyplot as plt
from ec_gen.ec_planning_drought import logging, gen_ec, subprocess
from scipy.optimize import brentq
from scipy.ndimage.filters import *
from scipy.stats import nanmean
#alt = "FOR3_DB3-JUN_CU5_DXC0"

import collections

class ResultCache(object):
    def __init__(self):
        self.result_cache = {}
        self.last_simulated = None

    def results_in_cache(self,x,station):
        xx = tuple(x)
        return self.result_cache.has_key((xx,station))
            
    def results_from_cache(self,x,station):
        xx = tuple(x)
        return self.result_cache[(xx,station)]
    
    def cache_results(self,x,station,data):
        xx = tuple(x)
        self.result_cache[(xx,station)] = data
        
    def just_simulated(self,x):
        logging.debug("In just_simulated, x=%s\nlast_simulated=%s" % (x,self.last_simulated))
        is_last_simulated = np.array_equal(x,self.last_simulated)
        logging.debug("In just_simulated, about to return %s" % is_last_simulated)
        return is_last_simulated

    def set_last_simulated(self,x):
        logging.debug("Setting last simulated to %s" % x)
        self.last_simulated = x.copy()
    
class WaterCost(object):
    def __init__(self,reg):
        self.reg = reg

    def __call__(self,x,base_flows,min_flow,control_period,control_step):
        #sac,ndo = ndo_controls_to_flow(x,base_flows,min_flow,\
        #                          control_period,control_step)
        xmean = np.mean(x)
        regular = np.mean(np.abs(np.ediff1d(x)))*self.reg
        rcost = xmean+regular
        import string
        controlstr = string.join(["%6.3f" % val for val in x],",")
        logging.info("Control:\n%s" % controlstr)
        logging.info("Water cost %s and regularized w.c. %s" %(xmean,rcost))
        return rcost

class ControlLowerBound(object):
    def __init__(self,lbound,ndx):
        self.lbound = lbound
        self.ndx = ndx
        
    def __call__(self,x,*args):
        return x[self.ndx] - self.lbound
        
class ComplianceConstraint(object):
    """ Callable that stores some info about regulation and time period"""
    cache = ResultCache()
    
    def __init__(self,standard,span,lower_bound):
        self.standard = standard       # info about the standard
        self.span = span               # info about the time span
        self.lower_bound =lower_bound  # transform so constraint >= 0
                                       # when compliant
        self.station_name = standard["name"]

    def __call__(self,x,base_flows,min_flow,\
                 control_description,control_period,control_step):
        """ Checks for salinity compliance, calling the model if necessary
        """
        logging.debug("Entering constraint evaluation for control %s" % x)
        logging.debug("Last run was for %s" % ComplianceConstraint.cache.last_simulated)
        if ComplianceConstraint.cache.results_in_cache(x,self.station_name):
            # We have run this x before and have its
            # station output stashed
            logging.debug("Retrieving cached salinity for station %s"\
                           % self.station_name)
            data = ComplianceConstraint.cache.results_from_cache(x,self.station_name)
        else:
            is_just_simulated = ComplianceConstraint.cache.just_simulated(x)
            logging.debug("Results not in cache, just_simulated = %s" 
                          % is_just_simulated)
            if not is_just_simulated:
                # not run this x yet
                logging.debug("Inputs never run, simulating control:\n%s" % x)
                sac,ndo = ndo_controls_to_flow(x,base_flows,min_flow,\
                                  control_period,control_step)
                logging.debug("control_description: %s %s" % control_description)
                dss_store_ts(sac,control_description[0],control_description[1])
                ec=gen_ec([None,"./ec_gen/runpara.txt"],ndo)
                logging.debug("Running models")
                run_models()
                logging.debug("Done running models")
                ComplianceConstraint.cache.set_last_simulated(x)
                logging.debug("Set last_simulated to %s" % x)
            file    = self.standard["file"]
            path    = self.standard["path"]
            station = self.standard["name"]
            active  = self.standard["active"]
            # todo: intend to make this more flexible
            #pre_ops = self.standard["pre_ops"]
            assert active
            logging.debug("Retrieving salinity from file %s path %s" 
                          % (file,path))
            output = dss_retrieve_ts(file,path,unique=True)
            data = boxcar(godin(output),days(14),days(0))
            #print "data", data
            ComplianceConstraint.cache.result_cache[(tuple(x),station)] = data
          
        constr_val = self.evaluate(data,self.standard)
        logging.info("Constraint %s, %s: %s" % ( self.station_name, self.span[0].date(),constr_val))
        return constr_val
        
    def evaluate(self,ts,standard):
        std     = self.standard["value"]
        ts_ave_window = ts.window(*self.span)
        viol = ts_ave_window - std
        max_violation = np.max(viol.data[~np.isnan(viol.data)])
        logging.debug("Max violation at %s is %s" % (standard["name"], max_violation))
        return -max_violation if self.lower_bound else violation



def run_models():
    """ Runs hydro and qual """
    cmds=["hydro historical_hydro_droughtstudy.inp",
          "qual historical_qual_ec_droughtstudy.inp"]
    logging.info("Running hydro")
    with open("hydro_stdout","w") as std_out:
        ok = subprocess.call(cmds[0],stdout=std_out) 
    logging.debug("Done with hydro, exit code %s " % ok)        
    if not ok==0:
        raise Exception("Hydro run failed with error code: %s" % ok)
    logging.info("Running qual")
    with open("qual_stdout","w") as std_out:
        ok = subprocess.call(cmds[1], stdout=std_out)
    logging.debug("Done with qual, exit code %s " % ok)
    if not ok==0:
        raise Exception("Qual run failed")

        
def max_salinity_exceed(q,base_flows,control_description,\
                          salt_standards,min_flow,\
                          control_period,control_step,
                          plots = False):
    """Given scalar flow at Sac, run model and report biggest salinity violation"""
    logging.info("Evaluating control %s" % q)
    sac,ndo = ndo_control_to_flow(q,base_flows,min_flow,\
                                  control_period,control_step)   
    dss_store_ts(sac,control_description[0],control_description[1])
    ec=gen_ec([None,"./ec_gen/runpara.txt"],ndo)
    
    run_models()
    logging.debug("Checking standards")
    max_so_far = -1e6
    standards_drawn=[]
    fig = plt.figure()
    ax = plt.subplot(111)
    for loc in salt_standards:
        std = loc["value"]
        file = loc["file"]
        path = loc["path"]
        station = loc["name"]
        active = loc["active"]
        logging.debug("Retrieving salinity from file %s path %s" % (file,path))

        s = dss_retrieve_ts(file,path,unique=True)
        eval_period = control_period
        if type(active) == dtm.datetime:
            eval_period=(active,control_period[1])
        s_ave = boxcar(godin(s),days(14),days(0))
        s_ave_window = s_ave.window(*eval_period)
        viol = s_ave_window - std 
        max_violation = np.max(viol.data[~np.isnan(viol.data)])
        logging.info("Max violation for %s in control period: %s" % (station,max_violation))
        print "Max violation for %s in control period: %s" % (station,max_violation)
        if plots:
            plt_start=dtm.datetime(2014,4,15)
            plt_end = dtm.datetime(2014,10,1)
            line, = ax.plot(s_ave.times,s_ave.data,label = station)
        if not std in standards_drawn:
            c = plt.getp(line,'color')
            ax.axhline(y=std,color=c,linestyle=":")
            ax.text(x=plt_end - days(5),y =std+20,s=r"%5.1f $\mu$S" % std,horizontalalignment="right")
            standards_drawn.append(std)
        if active: 
            max_so_far = max(max_so_far,max_violation)

        
    assert max_so_far < 30000., "Practical check on salinity standard exceedence"
    logging.info("NDO Ave: %s Maximum exceedence: %s over standard %s" % (q,max_so_far,std))
    if plots:
        plt.ylabel(r"EC ($\mu$S/cm)")
        plt.ylim(0,1200.)
        plt.xlim(plt_start,plt_end)
        fig.autofmt_xdate()

        box = ax.get_position()
        ax.set_position([box.x0, box.y0 + box.height * 0.25,
                         box.width, box.height * 0.8])
        ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.2),
          ncol=2, fancybox=True, shadow=True,fontsize=10)
        plt.title("Barriers (w/culverts, May 1), Average NDO = %6.0fcfs" % q)
        plt.savefig("opt_plots/barriers_db3_may_control_%6.0f.png" % q)
        #plt.title("No Gates, Average NDO = %6.0fcfs" % q)
        #plt.savefig("opt_plots/no_gates_control_%6.0f.png" % q)
        plt.close()
    return max_so_far
    
def optimize_monotonic():
    control_period = (dtm.datetime(2014,4,1),dtm.datetime(2014,8,1))
    control_step = days(1)    
    base_file = "ndo_echo.dss"
    base_path_ndo = "/DSM2/NDO_BASE/FLOW//1DAY/%s/" % alt.upper()
    base_ndo = dss_retrieve_ts(base_file,base_path_ndo,unique = True)
    
    # Retrieve original Sac flow. We do the optimization in terms of ndo, but
    # achieve the ndo by manipulating the Sacramento inflow.
    base_path_sac = "/DSM2/RSAC155/FLOW_BASE//1DAY/%s/" % alt
    sac = dss_retrieve_ts(base_file,base_path_sac,unique = True)
    do_plot = False
    if do_plot:
        plt.plot(sac.times,sac.data,label="Sac")
        plt.plot(base_ndo.times,base_ndo.data,label="NDO")
        plt.legend()
        plt.show()
    ndo = base_ndo   
    base_flows = {"sac":sac,"ndo":ndo}
    
    # Give a reasonable lower and upper bound for ndo flow. 
    # The lower bound should be low enough to ensure it is non-compliant
    # Similarly, upper value must be surely compliant. 
    # Make the interval on the wide side -- the algorithm will 
    # chop it down quickly.

    min_ndo = 3300
    max_ndo = 3400    
    #min_ndo = 3679
    #max_ndo = 3681
    
    # We will use the min_sac_flow in all the runs, so the optimization is in
    # terms of the flow *addition* to min_sac_flow
    ndo_flow_bracket = (min_ndo,max_ndo)    
    xtol = 10
    control_description = ("timeseries/optimizer.dss",\
                           "/FILL+CHAN/RSAC155/FLOW//1DAY/OPTIMIZER/")
    salt_standards = standards()

    min_flow = 1000.
    q,res = brentq( max_salinity_exceed, *ndo_flow_bracket,\
                    args=(base_flows,control_description,salt_standards,min_flow,control_period,control_step,True))
                        
    print "Minumum sac flow within a tolerance of %s is %s" %(xtol,qmin)

def control(x,sac0,ndo0,sac_min):
    ndofilt = ndo0.copy()
    ndofilt.data = gaussian_filter1d(ndofilt.data,sigma=6)
    sac1 = (x - ndofilt) + sac0
    sac1.data[0:30] = sac0.data[0:30] + 1.25*(x - ndofilt.data[0:30])
    sac1.data[70:] = sac0.data[70:] + 0.5*(x - ndofilt.data[70:])    
    
    #sac1.data[0:30] = sac0.data[0:30] + 1.2*(x - ndofilt.data[0:30])
    #sac1.data[70:] = sac0.data[70:] + 0.75*(x - ndofilt.data[70:])    

    sac1.data = np.maximum(sac1.data,sac_min)
    # sac1.data = gaussian_filter1d(sac1.data,sigma=6)
    sac1.data = gaussian_filter1d(sac1.data,sigma=6)
    ndo1 = sac1 - sac0 + ndo0
    return sac1,ndo1
    
def control_misfit(x,q_target,ndo0,sac0,sac_min):
    assert ndo0.start == sac0.start
    assert ndo0.end == sac0.end
    assert sac0.interval == days(1) # because of fixup on next line
    sac1,ndo1=control(x,sac0,ndo0,sac_min)
    qave = np.mean(ndo1.data)
    logging.debug( "x = %s qtarget = %s qave = %s" % (x,q_target,qave))
    return qave - q_target    
    
def ndo_control_to_flow(qcontrol,base_flows,min_flow,\
                      control_period,control_step):
    """ Convert controls phrased in terms of ndo into a sac inflow.
    """
    if type(qcontrol) == int: qcontrol = float(qcontrol)
    sac = base_flows["sac"].copy()
    ndo = base_flows["ndo"].copy()
    sac_min = min_flow
    sac_window = sac.window(control_period[0],control_period[1])
    ndo_window = ndo.window(control_period[0],control_period[1])

    q,r = brentq( control_misfit,0.5*qcontrol,2*qcontrol,
           args=(qcontrol,ndo_window,sac_window,sac_min),full_output=True)
    sac1,ndo1 = control(qcontrol,sac_window,ndo_window,sac_min)   
    ndo_window.data[:] = ndo1.data[:]
    sac_window.data[:] = sac1.data[:]   
    return sac,ndo

def ndo_controls_to_flow(qcontrol,base_flows,min_flow,\
                         control_period,control_step):
    """ Convert controls to inflows and exports
        Right now the parameterization is flat lines with no smoothing.
    """
    sac_copy = base_flows["sac"].copy()
    ndo_copy = base_flows["ndo"].copy()
    ndo = base_flows["ndo"]
    n = number_intervals(control_period[0],\
                         control_period[1],\
                         control_step)

    seq = [ticks_to_time(tck) for tck 
           in time_sequence(control_period[0],control_step,n+1)]
    if not (n) == len(qcontrol):
        raise ValueError("Control vector length %s doesn't match period and time step" % n)
    control_ts=rts(qcontrol,control_period[0],control_step)
    plt.plot(ndo_copy.times,ndo_copy.data,label="ndo_copy")
    plt.plot(sac_copy.times,sac_copy.data,label="sac_copy")
    
    for i in range(len(seq)-1):
        period = (seq[i],seq[i+1])
        val = qcontrol[i]
        sac0 = sac_copy.window(*period)
        ndo0 = ndo_copy.window(*period)
        sacnew = sac0 - (ndo0 - val)
        sacnew.data[:] = np.maximum(sacnew.data,min_flow)
        dflow = sacnew - sac0
        sac0.data[:] = (sac0 + dflow).data
        ndo0.data[:] = (ndo0 + dflow).data
        #plt.plot(sac0.times,sac0.data,label="sac0")
        #plt.plot(ndo0.times,ndo0.data,label="ndo0")
        #plt.legend()
        #plt.show()
    return sac_copy,ndo_copy
    
  
    
# if __name__ == "__main__":
#     logfilename = os.path.join(os.path.dirname("."),"log.txt")
#     logging.basicConfig(filename=logfilename,filemode='w',level=logging.INFO)
#     logging.info("logging enabled")
#     #test_controls_to_flow()
#     run_models()
#     optimize_monotonic()
#     #test_ndo_control_to_flow()
  

    