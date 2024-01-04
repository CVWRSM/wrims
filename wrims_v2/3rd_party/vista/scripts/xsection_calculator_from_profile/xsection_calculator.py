"""
Calculates the xsection properties from the xsection profile (x,z values along a cross section line) 

The file is the format exported by ArcGIS when drawing a line profile and choosing text format for export
The first line is a header followed by lines containing x and z in the units of the raster dem
Please ensure that the units of x and z are in meters before using this script or change the conversion part of this script

The layers are calculated at elevations of minimum elevation, -10, -5, 0, 5 and 10 by this script. 

It assumes the name of the file to be channel id and last two digits before .txt to be the normalized distance times 10 from the upstream end
For example 44101.txt implies 441 is the name of the channel and 01/10 = 0.1 is the normalized distance (distance/channel length) from upstream end.
"""
from gov.ca.dsm2.input.model import XSectionProfile
from java.util import ArrayList
import sys, glob, os
import jarray
import vdisplay
from gov.ca.dsm2.input.model.calculator import ModelCalculator
def load_profile(filename):
    fh=open(filename,'r')
    lines=fh.readlines()
    profile_points = ArrayList()
    for line in lines[1:]:
        fields=line.split("\t")
        if len(fields) != 2: continue
        x,z=map(lambda x: x.replace(",",""),fields)
        profile_points.add(jarray.array([float(x)/0.3048,float(z)/0.3048], 'd'))
    fh.close()
    return profile_points
def save_profile(filename, profile_points):
    fh=open(filename,'w')
    for i in range(profile_points.size()):
        x,z=profile_points.get(i)
        print >>fh, "%.2f\t%.2f"%(x,z)
    fh.close()
def add_vertical_walls(profile,max_height):
    if (profile[0][1] < max_height):
        profile.add(0,jarray.array([profile[0][0],max_height],'d'))
    if (profile[-1][1] < max_height):
        profile.add(jarray.array([profile[-1][0],max_height],'d'))
    return profile
def get_sorted_ascending_elevations_from_profile(xs):
    pp = xs.getProfilePoints()
    elevations=[]
    for i in range(pp.size()):
        elevations.append(pp.get(i)[1])
    elevations=list(set(elevations))
    elevations.sort()
    return elevations
def print_layer(outf,chan,dist,layer):
    print >>outf, "%d\t%.1f\t%.2f\t%.2f\t%.2f\t%.2f"%(chan,dist,layer.elevation, layer.area, layer.topWidth, layer.wettedPerimeter)
def profileToXY(profile):
    x=[]
    z=[]
    for i in range(profile.size()):
        px,pz=profile.get(i)
        x.append(px)
        z.append(pz)
    return x,z
def profile_plot(original, simplified, chan, dist):
    import vdisplay
    ox,oz=profileToXY(original)
    sx,sz=profileToXY(simplified)
    plot=vdisplay.xyplot(ox,oz,xlabel="Elevation(feet)",ylabel="Distance(feet)",title="Channel %d @ %.1f"%(chan,dist), color="red")
    vdisplay.addXYCurveToPlot(plot,sx,sz,color="green", symbol="square-filled")
    vdisplay.show_plot(plot)
if __name__=='__main__':
    if len(sys.argv) < 2:
        print "Usage: xsection_calculator.py directory_containing_txt_files_of_profiles"
        exit(1)
    dirname=sys.argv[1]
    filenames = glob.glob(dirname+"/*.txt")
    print "Writing out to dsm2_xsections.inp in current directory"
    outf = open("dsm2_xsections.inp","w")
    outf.write("""XSECT_LAYER    CHAN_NO  DIST    ELEV      AREA   WIDTH     WET_PERIM\n""")
    for filename in filenames:
        dist=float(filename.split(".txt")[0][-2:])/10.
        chan=int(os.path.basename(filename).split(".txt")[0][:-2])
        print "Calculating xsection properties for channel: %d @ distance: %f"%(chan,dist)
        xs = XSectionProfile()
        profile=load_profile(filename)
        original_profile=load_profile(filename)
        WALL_HEIGHT=10
        profile=add_vertical_walls(profile,WALL_HEIGHT)
        xs.setProfilePoints(profile)
        # simplify and save profile
        epsilon=2.5
        ModelCalculator().simplifyXSectionProfile(xs,epsilon)
        simplified_profile_filename=filename.split(".txt")[0]+"-%f.simplified"%epsilon
        print "Writing out simplified cross-section to %s"%simplified_profile_filename
        save_profile(simplified_profile_filename,xs.getProfilePoints())
        simplified_profile=xs.getProfilePoints()
        #profile_plot(original_profile,simplified_profile, chan, dist)
        #
        minElevation=xs.getMinimumElevation()
        print "!!!!ASSUMING UNITS OF METERS FOR CROSS-SECTION PROFILE!!!!"
        print "Minimum Elevation: %.2f"%minElevation
        elevation = minElevation
        elevations = get_sorted_ascending_elevations_from_profile(xs)
        for elevation in elevations:
            print_layer(outf, chan, dist, xs.calculateLayer(elevation))
            if elevation >= WALL_HEIGHT:
                break
        #bottom_layer = xs.calculateLayer(minElevation)
        #print_layer(outf,chan,dist,bottom_layer)
        #print_layer(outf,chan,dist,xs.calculateLayer(-10))
        #print_layer(outf,chan,dist,xs.calculateLayer(-5))
        #print_layer(outf,chan,dist,xs.calculateLayer(0))
        #print_layer(outf,chan,dist,xs.calculateLayer(5))
        #print_layer(outf,chan,dist,xs.calculateLayer(10))
    outf.write("END")
    outf.close()