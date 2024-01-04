#=====================================================================================
# example: dss export to txt
#=====================================================================================

import vutils as vu
from scripts.tool import DssVista

# parts are exact except for the blank parts
def getDataRef(group, path, timewindow):
    
    p = getExactPartsFromPath(path)    
    data = vu.findparts(group, a=p[0] , b=p[1] , c=p[2] , d=p[3] , e=p[4], f=p[5])[0]
    dataReference = vu.DataReference.create(data,timewindow)
    return dataReference

# parts are exact except for the blank parts
def getExactPartsFromPath(path):
    
    p_in = path.split('/')[1:-1]
    #print p_in
    p = []
    for part in p_in:
        if part:
            part = "^"+part+"$"
        p.append(part)
    #print p
    return p


text_file = open("./dss_to_txt.txt",'w')

dss_file =  "./dss_from_txt_2.dss"
vu.DSSUtil.generateCatalog(dss_file)

g = vu.opendss(dss_file)

begin_time = "31OCT2014 2400"
end_time =   "31MAR2015 2400"
tw = vu.timewindow(begin_time + " - " + end_time)  

pathnames = []

pathnames.append("/CALLITE/X/FLOW//1MON/SCENARIO_201/")
pathnames.append("/CALLITE/Y/STORAGE//1MON/SCENARIO_201/")

for pathname in pathnames:

    print pathname
    da = getDataRef(g, pathname, tw)

    ds = da.getData()

    y_arr = ds.getYArray()

    text_file.write(pathname)

    for number in y_arr:

        text_file.write(','+str(number))
    text_file.write('\n')
        
text_file.close()





    
    

    
    