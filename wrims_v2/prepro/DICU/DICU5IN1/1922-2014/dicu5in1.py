# DICU5IN1 script file
#cd ..
#rm dicu5in1
#f90 -e dicu5in1.f
#mv a.out dicu5in1
#cd 1922-2003

#Script updated for DICU simulation of calendar year 2003 with wy2006 representing 1922-1997 average values
#Jamie Anderson 1/20/04
#Translated to python, Jim Wilde 9/24/04
#Last Updated Lan Liang 01/03/08

#from jnios import os
import os,sys    #siqing modified so do not have to use vscript

def main(argv=None):
   if argv is None:
        argv = sys.argv
   logfile = open(".\dicu5in1.log","w")    

   # **** STATIC DATA
   # Runtime files
   os.environ['dilldat']='../dilldat'
   os.environ['diuldat']='../diuldat'
   os.environ['arealu.00']='../arealu.00'
   os.environ['arealu.77']='../arealu.77'
   os.environ['areaid']='../areaid'
   os.environ['AREAID.00']='../AREAID.00'
   os.environ['AREAID.77']='../AREAID.77'
   os.environ['rwlu.00']='../rwlu.00'
   os.environ['rwlu.77']='../rwlu.77'

   # **** CHANGING DATA
   #Updates of the DICU may require the 'end' and 'ndyr' variables to be changed below

   # Precipitation data
   start = 1922
   end = 2014				# <==== Update year here!
   ndx = start
   while  (ndx <= end):
      name = ('7STAPREC.WY%s') % ndx
      val = ('../../PRECIP/7STATION-DAT-Y2K/7STAPREC.WY%s') % ndx
      os.environ[name]=val
      ndx = ndx + 1

   # Runtime variables
   #   Starting water-year
   os.environ['styr']='1922'
   #   Ending water-year
   os.environ['ndyr']='2014'		# <==== Update year here!
   status=os.system('dicu5in1.exe')

   if status!=0:
        logfile.write("Failed to run dicu5in1.exe\n")
        logfile.write("check if file Precip/7STAPREC.WYXXXX of each year is created correctly\n")
        logfile.close()
        return 1

if __name__ == "__main__":
    sys.exit(main())
