#Script to run nodcu14

#1922-1997 from CALSIM. Updated: mmierzwa 2005.11.02 
#Translated to python, Jim Wilde 9/24/04
#Last updated 01/03/2008 Lan Liang

#from jnios import os
import os,sys  #siqing modified to avoid using vscript
from vtools.datastore.dss.api import *
from vtools.functions.api import *
from vtools.data.timeseries import *
import vtools.data.constants as cons

def dssts(junkfile):
	#open file junk.txt
	infile = open(junkfile,"r")
	dssfile = infile.readline().strip()  #dss file
	icont = 1
	while icont==1:
		data=[]
		line = infile.readline()
		if line.lower().find("finish")>-1:
			break
		else:
			dssparts,interval = dsspath(line)  #dss file
		#print "processing %s" % dssparts
		intvl = parse_interval(interval)
		#intvl = time_interval(months=1)
		unit = infile.readline().strip()
		inst = infile.readline().strip()
		 #31OCT1921 2400   vtools has to use the first day as starting time for 1 month
		start = parse_time(infile.readline().split()[0].strip())+days(1) 
		props={cons.AGGREGATION:cons.MEAN, cons.UNIT:"CFS"}
		line = infile.readline().strip()
		while line.lower().find("end")==-1 and line.lower().find("finish")==-1 :
			data.append(line)
			line= infile.readline().strip()
		#create time series
		myrts=rts(data,start,intvl,props)
		#store timeseries
		dss_store_ts(myrts,dssfile,dssparts)
	infile.close()
	return 0

def dsspath(pathstr):
	#A=DICU-HIST+NODE  B=   1  C=DRAIN-FLOW  D=01JAN1920  E=1MONTH  F=DWR-BDO
	aloc = pathstr.find("A=")
	bloc = pathstr.find("B=")
	cloc = pathstr.find("C=")
	dloc = pathstr.find("D=")
	eloc = pathstr.find("E=")
	floc = pathstr.find("F=")
	apart = pathstr[aloc+2:bloc].strip()
	bpart = pathstr[bloc+2:cloc].strip()
	cpart = pathstr[cloc+2:dloc].strip()
	dpart = pathstr[dloc+2:eloc].strip()
	epart = pathstr[eloc+2:floc].strip()
	fpart = pathstr[floc+2:].strip()
	return "/"+apart+"/"+bpart+"/"+cpart+"/"+dpart+"/"+epart+"/"+fpart+"/",epart

# files 12, 14, 17 and 27 OK
def main(argv=None):
	if argv is None:
		argv = sys.argv
	logfile = open("bat1922-201X.log","w")	

	os.environ['DICU5.14']='../../../DICU5/1922-2014/DICU5.14' #update folder name
	os.environ['DICU5.17']='../../../DICU5/1922-2014/DICU5.17' #update folder name
	os.environ['DICU5.12']='../../../DICU5/1922-2014/DICU5.12' #update folder name
	os.environ['DICU5.27']='../../../DICU5/1922-2014/DICU5.27' #update folder name

	os.environ['DIVFCTR.RMA']='../../../NODCU/DIVFCTR.DSM.2-92'
	os.environ['DRNFCTR.RMA']='../../../NODCU/DRNFCTR.DSM.2-92'
	os.environ['LEACHAPL.DAT']='../../../NODCU/LEACHAPL.DAT'
	os.environ['LEACHDRN.DAT']='../../../NODCU/LEACHDRN.DAT'
	os.environ['IDRNTDS.DAT']='../../../NODCU/IDRNTDS.DAT'
	os.environ['DRNCL.123']='../../../NODCU/DRNCL.123'
	os.environ['GEOM-NODES']='../../../NODCU/GEOM-NODES-1.5'

	os.environ['IRREFF.DAT']='../../../NODCU/NODCU12/Irreff/IRREFF-3MWQIregions'
	os.environ['subarea-info']='../../../NODCU/NODCU12/subarea-info'


	# Runtime variables
	# The years assumed are incorrect, so 'N'
	os.environ['years_ok']='N'
	# The correct beginning year to run is
	os.environ['begwy']='1922'
	# The correct last year to run is
	os.environ['endwy']='2014'							 #update year 
	# Type of drainage concentration data (1 for TDS, 2 for chloride)
	os.environ['datatype']='1'
	# Do you want to creat an ascii file?
	os.environ['ascii']='Y'
	# The dss file to save output
	os.environ['dssfile']='../../../dicu_201406.dss'   # update year  and month!!!
	status=os.system('nodcu14.exe')

	if status!=0:
		logfile.write("Failed to run nodcu14.exe\n")
		logfile.write("check if file DICU5.12, DICU5.14, DICU5.17, DICU5.27 were created correctly\n")
		logfile.close()
		return 1

	#siqing: it is annoying to use dssits, instead use vtools
	#print "\nEnter \'dssts < junk.txt\'\n"
	#status = os.system ('dssts < junk.txt')  #siqing combine this
	print "Wrtie to dss, please wait..."
	ok = dssts("junk.txt")
	if ok!=0:
		logfile.write("Failed to create dicu dss file\n")
		logfile.write("check if file junk.txt was created correctly\n")
		logfile.close()
		return 1
	print 'finish cleaning up'

if __name__ == "__main__":
	sys.exit(main())
