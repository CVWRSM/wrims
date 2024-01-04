#=====================================================================================
# example: read txt into dss
#=====================================================================================

import vutils as vu
from scripts.tool import DssVista

text_file = open("./data_2.txt", "r")
dss_file ="./dss_from_txt_2.dss"

# open new dss file
vu.opendss(dss_file)

per_row = []
for line in text_file:
    per_row.append(line.strip().split(','))

per_column = zip(*per_row)

for col in per_column:

    begin_time = col[0].strip()
    pathname  = col[1].strip()
    units = col[2].strip()

    print pathname, begin_time, units


    array = map(float, col[3:])

    DssVista.array2dss(dss_file, array, begin_time, pathname, units)


text_file.close()

vu.DSSUtil.generateCatalog(dss_file)

print 'completed.'





    
    

    
    