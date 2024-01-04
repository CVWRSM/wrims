"""
Converts CSDP Irregular Geometry Output to DSM2 irregular geometry input format
"""
import sys,os
if __name__=='__main__':
    if len(sys.argv) < 2:
        print """ Usage: csdp_geom_converter directory_containing_csdp_output"""
        exit(1)
    dir=sys.argv[1]
    of=open(dir+"/"+'irregular_xsections_dsm2.inp','w')
    of.write("XSECT_LAYER\n")
    of.write("CHAN_NO  DIST  ELEV  AREA  WIDTH  WET_PERIM\n")
    for f in os.listdir(dir):
        if not f.endswith(".txt"): continue
        channel,dist=f.split(".txt")[0].rsplit("_",1)
        #print channel,dist
        gf = open(dir+"/"+f,'r')
        for i in range(3): gf.readline()
        line=gf.readline()
        while( not line.startswith("station")):
            fields=line.split()
            if len(fields) < 4: break
            #print line, fields
            elev,area,perimeter,width=fields[0:4]
            of.write("%s\t%s\t%s\t%s\t%s\t%s\t\n"%(channel,dist,elev,area,width,perimeter))
            line=gf.readline()
        gf.close()
    of.write("END");
    of.close();
        
        
    