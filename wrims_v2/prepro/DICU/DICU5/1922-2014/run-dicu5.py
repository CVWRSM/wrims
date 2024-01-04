#Script to run dicu5

#1922-1997 from CALSIM. Updated: mmierzwa 2005.11.02 
#Translated to python, Jim Wilde 9/24/04
#Last Updated 01/03/2008 lliang

#from jnios import os
import os,sys  #siqing modified so do not have to use vscript

def main(argv=None):
    if argv is None:
        argv = sys.argv
    logfile = open(".\dicu5.log","w")    
    if os.path.isfile('DICU5.1') : os.remove('DICU5.1')
    if os.path.isfile('DICU5.2') : os.remove('DICU5.2')
    if os.path.isfile('DICU5.3') : os.remove('DICU5.3')
    if os.path.isfile('DICU5.4') : os.remove('DICU5.4')
    #siqing modified the following  because of  run error message
    if os.path.isfile('DICU5.10') : os.remove('DICU5.10')
    os.system('del DICU5.10 DICU5.12 DICU5.13 DICU5.14 ')
    if os.path.isfile('DICU5.12') : os.remove('DICU5.12')
    os.system('del DICU5.17 DICU5.27 DICU5.41')
    if os.path.isfile('DICU5.13') : os.remove('DICU5.13')
    if os.path.isfile('DICU5.14') : os.remove('DICU5.14')
    if os.path.isfile('DICU5.17') : os.remove('DICU5.17')
    if os.path.isfile('DICU5.27') : os.remove('DICU5.27')
    if os.path.isfile('DICU5.41') : os.remove('DICU5.41')

    os.environ['DICU5.1']='../../DICU5IN1/1922-2014/dicu5.1' #update folder name
    os.environ['DICU5.2']='../../DICU5IN1/1922-2014/DICU5.2' #update folder name
    os.environ['DICU5.3']='../../DICU5IN1/1922-2014/WYTYPES' #update folder name
    os.environ['DICU5.4']='../1922-2000/DICU5.4'

    status=os.system('dicu5.exe')
    if status!=0:
        logfile.write("Failed to run dicu5.exe\n")
        logfile.write("check if files dicu5.1, dicu5.2, and WYTYPES in folder \n")
        logfile.write("DICU5IN1/1922-XXXX are created correctly")
        logfile.close()
        return 1

if __name__ == "__main__":
    sys.exit(main())
