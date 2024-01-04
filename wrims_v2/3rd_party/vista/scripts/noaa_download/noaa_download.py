#Use their web service to download data a month at time :(
#To see which stations have data use this http://tidesandcurrents.noaa.gov/gmap3/index.shtml?type=VerifiedData&region= to get station ID
# Then checkout their web service availability here
#http://opendap.co-ops.nos.noaa.gov/axis/
#Example retrieving data for Port Chicago: Station ID 9415144
#http://opendap.co-ops.nos.noaa.gov/axis/webservices/waterlevelverifiedsixmin/response.jsp?stationId=9415144&beginDate=19960101&endDate=19960131&datum=NAVD&unit=1&timeZone=1&format=text&Submit=Submit
from java.util import Calendar, Date
from java.text import SimpleDateFormat
from hec.script import *
from hec.heclib.dss import *
from hec.heclib.util import *
from hec.io import *
import java
sdf = SimpleDateFormat("yyyyMMdd")
def get_noaa_data_as_array_of_lines(stationId, beginDate, endDate):
    import urllib
    url = "http://opendap.co-ops.nos.noaa.gov/axis/webservices/waterlevelverifiedsixmin/response.jsp?stationId=%s&beginDate=%s&endDate=%s&datum=NAVD&unit=1&timeZone=1&format=text&Submit=Submit"%(stationId, beginDate, endDate)
    response = urllib.urlopen(url)
    result = response.readlines()
    response.close()
    return result
def parse_noaa_data(result):
    find_8th_equals=False
    find_equals_counter=0
    line_counter=0
    while find_equals_counter < 8:
        if result[line_counter].startswith("========="):
            find_equals_counter=find_equals_counter+1
        line_counter=line_counter+1
    data=[]
    while line_counter < len(result):
        fields = result[line_counter].split()
        line_counter=line_counter+1
        if len(fields) > 4:
            data.append(fields[1:4])
    return data
def incr_by_month(date):
    c=Calendar.getInstance()
    c.setTime(sdf.parse(date))
    c.add(Calendar.MONTH,1)
    return sdf.format(c.getTime())
def month_end_date(date):
    c=Calendar.getInstance()
    c.setTime(sdf.parse(date))
    days=c.getActualMaximum(Calendar.DAY_OF_MONTH)
    c.set(Calendar.DAY_OF_MONTH,days)
    return sdf.format(c.getTime())
def get_noaa_data(stationId, sdate, edate, fileh):
    #do one month at time
    fetch_date = sdate
    while fetch_date != edate:
        beginDate=fetch_date
        endDate=month_end_date(beginDate)
        print 'Fetching starting: %s'%fetch_date
        result=get_noaa_data_as_array_of_lines(stationId, beginDate, endDate)
        data=parse_noaa_data(result)
        for line in data:
            if len(line) >= 3:
                fileh.write("%s,%s,%s\n"%(line[0],line[1],line[2]))
        fetch_date=incr_by_month(fetch_date)
        fileh.flush()
def test_get_1_month():
    stationId='9415144'
    beginDate='19960101'
    endDate='19960131'
    result=get_noaa_data_as_array_of_lines(stationId, beginDate, endDate)
    data = parse_noaa_data(result)
    return data
def test_get_1_year():
    stationId='9415144'
    beginDate='19960101'
    endDate='20090101'
    return get_noaa_data(stationId, beginDate, endDate)
def to_hectime(datestr, pattern="MM/dd/yyyy HH:mm"):
    informat = SimpleDateFormat(pattern)
    hecformat = SimpleDateFormat("ddMMMyyyy HHmm")
    date = informat.parse(datestr)
    hecdatestr = hecformat.format(date)
    return HecTime(hecdatestr)
def load_ts(file, fullName):
    fh=open(file)
    lines=fh.readlines()
    tsc=TimeSeriesContainer()
    tsc.fullName=fullName
    tsc.interval=6
    values=[]
    times=[]
    start=None
    for line in lines:
        fields=line.split(",")
        if not start:
            start=to_hectime(fields[0]+" "+fields[1])
        times.append(start.value())
        values.append(float(fields[2]))
        start.add(tsc.interval)
    fh.close()
    tsc.times=times
    tsc.values=values
    tsc.numberValues=len(values)
    tsc.units="FT"
    tsc.type="INST-VAL"
    return tsc
def download_to_file(stationId,filename):
    stationId='9415144'
    beginDate='19960101'
    endDate='20090101'
    fileh=open('z:/temp/port_chicago_noaa_stage.csv','w')
    try:
        get_noaa_data(stationId, beginDate, endDate, fileh)
    finally:
        fileh.close();
if __name__=='__main__':
    filename='z:/temp/port_chicago_noaa_stage.csv'
    fullName="/NOAA/PORT CHICAGO/STAGE//6MIN/NAVD/"
    tsc=load_ts(filename,fullName)
    dss=HecDss.open('z:/temp/noaa.dss')
    dss.put(tsc)
    dss.done()
#
