#=====================================================================================
# example: dss export to txt
#=====================================================================================

import vutils as vu
from scripts.tool import DssVista

t = open('transposed.csv','w')

begin_time = "31OCT1921 2400"
end_time =   "31MAR1922 2400"

dssFiles = []
dssFiles.append("DCR2015_Base_U45_I85")
dssFiles.append("DCR2015_Base_U85_I85")

pathnames = ["/CALSIM/UWFE/UWFE//1MON/2020D09E/","/CALSIM/IBU/IBU//1MON/2020D09E/"]

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


tempFile = "./inter.txt"
f = open(tempFile,'w')

for item in dssFiles:

    print "processing..."+ item
    dssFile =  "./"+item+".dss"
    vu.DSSUtil.generateCatalog(dssFile)
    
    g = vu.opendss(dssFile)
    
    tw = vu.timewindow(begin_time + " - " + end_time)  
    
    for pathname in pathnames:
       
        da = getDataRef(g, pathname, tw)
    
        ds = da.getData()
    
        y_arr = ds.getYArray()
    
        f.write(item+',')
        f.write(pathname.split('/')[2])
    
        for number in y_arr:
    
            f.write(','+str(number))
        f.write('\n')
f.close()        

print "transposing..."

f=open(tempFile,'r')

lines = map(str.strip, f.readlines())
    
lis = [x.split(",") for x in lines]

for x in zip(*lis):
    for y in x:
        t.write(y+',')
    t.write('\n')

f.close()   
t.close()
print "completed."    

    
    