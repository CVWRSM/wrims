# Uses tidefile to calculate volume of channels and reservoirs 
# The channels and reservoirs can be specified in an input file
from vtidefile import opentidefile
from vdss import writedss
from vutils import timewindow
import sys
import string
from vista.set import DataReference
def get_data(tidefile, twstr, filter):
    tf = opentidefile(tidefile)
    if twstr != None:
        print 'Timewindow: %s' % twstr
        tw = timewindow(twstr)
    else:
        tw = None
    refs = tf.find(filter)
    if refs and len(refs) == 1:
        print "Getting data..."
        if tw != None:
            ref = DataReference.create(refs[0], tw)
        else:
            ref = refs[0]
        return ref.data
    else:
        print 'No data found for filter: %s' % filter
def get_volume_data(tidefile, chan, twstr):
    return get_data(tidefile, twstr, ['', '^%s$' % chan, 'VOLUME'])
def get_upstream_flow_data(tidefile, chan, twstr):
    return get_data(tidefile, twstr, ['', '^%s_UPSTREAM$' % chan, 'FLOW'])
def get_downstream_flow_data(tidefile, chan, twstr):
    return get_data(tidefile, twstr, ['', '^%s_DOWNSTREAM$' %chan, 'FLOW'])
def plot_vol_calcs(tidefile,chan,tw,run_name):
    volume = get_volume_data(tidefile, chan, tw)
    upflow = get_upstream_flow_data(tidefile, chan, tw)
    downflow = get_downstream_flow_data(tidefile, chan, tw)
    #import vdisplay
    #vdisplay.plot(volume)
    initial_volume = volume[0].y
    final_volume = volume[len(volume) - 1].y
    ndata = len(upflow)
    vol = [initial_volume]
    for i in range(0, ndata):
        vol.append(vol[i] + (upflow[i].y - downflow[i].y) * time_step)
    print 'Initial Volume: %f -> Final Volume: %f' % (initial_volume, final_volume)
    print 'Initial Volume: %f -> Final Volume: %f' % (initial_volume, vol[ndata - 1])
    print 'Difference in Volume %f' % (final_volume-vol[ndata-1])
    from vista.set import RegularTimeSeries, DataSetAttr
    attr = volume.getAttributes().createClone()
    attr.setTypeName("VOLCALC")
    volcalc = RegularTimeSeries("/VOL/%s/VOLCALC//1HOUR/%s/"%(chan,run_name),"01OCT1974 0000","1HOUR",vol[1:])
    volcalc.setAttributes(attr)
    import vdisplay
    vdisplay.plot(volcalc,volume)
    vdisplay.plot(volcalc-volume)
#
if __name__ == '__main__':
    run_name='Exst_NMFS_BO'
    #run_name='Existing'
    #tidefile = 'd:/models/qual_crash_0_parcels/Delta_Barriers/output/BST_%s.h5'
    tidefile = 'd:/models/qual_crash_0_parcels/Delta_Barriers/output/BST_%s.h5'%run_name
    time_step = 60 * 60  # FIXME: Assumption tidefile is at 60MIN
    print 'Calculating volume from tidefile: %s' % tidefile
    #chans = [160,172,54,309]
    chans=[172]
    tw = "01OCT1974 0000 - 01JAN1975 0000"
    for chan in chans:
        plot_vol_calcs(tidefile,chan,tw,run_name)
    #exit(0)
# 
