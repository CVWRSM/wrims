import cdec
import vdisplay
if __name__=='__main__':
    station_name='GGS'
    sensor_number=1902
    start_date="10/01/2000"
    end_date="05/01/2001"
    rts=cdec.retrieve(station_name,sensor_number,start_date,end_date,verbose=1)
    if rts: vdisplay.plot(rts)
