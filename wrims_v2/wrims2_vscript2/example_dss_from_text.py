#=====================================================================================
# example: read txt into dss
#=====================================================================================

import vutils as vu
from scripts.tool import DssVista

text_file = open("./data.txt", "r")
dss_file ="./dss_from_txt.dss"

lines = text_file.readlines()

pathname  = lines[0].strip()
begin_time = lines[1].strip()
units = lines[2].strip()

print pathname, begin_time, units

array = map(float, lines[3:])

print array

# open new dss file
vu.opendss(dss_file)
DssVista.array2dss(dss_file, array, begin_time, pathname, "CFS")

text_file.close()

print 'completed.'





    
    

    
    