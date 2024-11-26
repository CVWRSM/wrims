#     Name: WsiDiGen.py
#   Author: Ben Tustison
#   E-mail: tustison@mbkengineers.com
#    Phone: 916.456.4400
# Last Rev: 2024.11.06 - zachary.roy@water.ca.gov
#  Purpose: Mimics WsiDiGenerator class from CALSMIM

# python class imports
from math import sqrt
import os
import csv

# java class imports
# from java.awt.Color import *
from java.io import File, PrintWriter, BufferedWriter, FileWriter

import xml.etree.ElementTree as ET

tab = "   "

def read_csv(src, variables=None):
   """Read a CSV formatted CalSim3 output file to a dict[str, list[float]]

   Parameters
   ----------
   src : str
      The source file for the CSV, must exist
   variables : (list, tuple)[str], optional
      The variables to read, acts as a filter, by default None

   Returns
   -------
   dict[str, list[float]]
      The data read from the CSV file.
   """
   print("reading csv: %s" % (src))
   print("reading variables: " + str(variables))
   if not isinstance(src, str):
      raise ValueError("src is type %s, expected str" % type(src))
   if not os.path.exists(src):
      raise ValueError("%s doesn't exist in cwd (%s)" % (src, os.getcwd()))
   if variables is None:
      # This is a hack because we are in Python 2
      # Create an object that always returns true when compared to other objects
      class AlwaysTrueEq(object):
         def __eq__(self, other):
               return True

      # When we do tests like "if x in variables" with the object below,
      # it will always be True
      variables = (AlwaysTrueEq(),)
   if not isinstance(variables, (list, tuple)):
      raise ValueError(
         "variables is type %s, expected list or tuple" % (type(variables))
      )
   # This is going to be slow because we can't use numpy
   # The CSV is assumed to contain data we do not need, so we must check the variable
   # name before storing the data.
   # The format will look something like:
   # CSV
   # id,PartA,PartF,Timestep,Units,Date_Time,Variable,Kind,Value\n
   # 0,CALSIM,L2020A,1MON,cfs,10/31/1921 0:00,C_CLR011,channel,169.5895738\n
   # 0,CALSIM,L2020A,1MON,cfs,11/30/1921 0:00,C_CLR011,channel,228.3671231\n
   # 0,CALSIM,L2020A,1MON,cfs,12/31/1921 0:00,C_CLR011,channel,350.09929\n
   collected_data = dict()
   try:
      SRC = open(src, "r")
      reader = csv.DictReader(SRC)
      for row in reader:
         var = row["Variable"]
         if var not in variables:
            continue
         if var not in collected_data:
            collected_data[var] = list()
         try:
            val = float(row["Value"])
         except Exception:
            continue  # Do not store values that cannot be cast to float
         collected_data[var].append(val)
   finally:
      SRC.close()
   print("done reading csv")
   return collected_data


# WsiDiCl class
class WsiDiGenCl:

   # constructor: initialize class parameters
   def __init__(self,name,wsiVar,diVar,wsiMax,dvName,lookupName,launchName,offset0):
       # assign passed constructor arguments
       self.name = name   # wsi-di profile name
       self.wsiVar = wsiVar   # wsi variable
       self.diVar = diVar   # di variable
       self.wsiMax_ub = wsiMax
       self.wsiMax = wsiMax   # maximum wsi variable value
       self.stdyDvName = dvName   # study DV name
       self.lookupName = lookupName  # lookup folder name
       self.launchName = launchName # launch file name
    
       # set path to run directory based on location of dv file
       file = File(dvName)
       dir = File(file.getParent())
       self.runDir = dir.getParent()+File.separator+"run"   # run directory
              
       # set other class variables
       value = 0.0
       self.report = True
       self.step = 500.0   # Distance between data points on the WSI axis
       self.lookahead = 1.0*self.step   # Distance to look ahead for data beyond segment endpoint to determine coordinates of segment endpoint
       self.mult = 1.0   # standard deviation multiplier for wsidi pair search
       self.wsiMin = 0.0   # minimum wsi value
       self.diMin = self.step   # minimum di value
       self.diMax = 0.0   # maximum di value, set to actual value when WSI-DI data is analyzed
       self.threshold = 0.1   # threshold above which to count wsi-di data pts
       self.nseg = int(wsiMax/self.step)   #  number of wsi-di segments
       self.ndata = 0   # number of wsi-di data points, set to actual value when WSI-DI data is analyzed
       self.data = []   # wsi-di data array
       self.offset1=offset0 #default offset 1.2

   ### FUNCTIONS

   # count data points above threshold
   def countDataPoints(self, array):
      return sum(1 if v >= self.threshold else 0 for v in array)

   # load data
   def load(self, fname):
      if not isinstance(fname, str):
         raise ValueError("fname is type {}. expected str" %(type(fname)))
      if not os.path.exists(fname):
         raise ValueError("%s does not exist in cwd (%s)" % (fname, os.getcwd()))
      suffix = fname.rsplit('.', 1)[-1] 
      if suffix == 'dss':
         self.load_from_dss(fname)
      elif suffix == "csv":
         self.load_from_csv(fname)
      else:
         raise NotImplementedError("file type not supported: %s" % (suffix))
   
   def load_from_csv(self, fname):
      print("loading " + self.wsiVar + " and " + self.diVar)
      variables = [self.wsiVar, self.diVar]
      data = read_csv(fname, variables)
      # Check for exceptions
      for var in variables:
         if var not in data:
            raise KeyError("The variable %s was not found in the CSV %s" % (var, fname))
      nd = self.countDataPoints(data[self.wsiVar])
      # filter out all the -1 values from months where these variables aren't present. 
      # note: WSI is calculated in twice as many months as DI
      array_wsi = filter(lambda x: x >= 0, data[self.wsiVar])
      array_di = filter(lambda x: x >= 0, data[self.diVar])
      # there are 2 WSI value for each DI value by default. We need to duplicate the DI
      # points, according to the following pattern:
      # wsi before -> W1 W2 W3 W4 W5 W6
      # di before  -> D1    D2    D3
      # di after   -> D1 D1 D2 D2 D3 D3
      array_di = [v for v in array_di for _ in range(2)]
      # last two WSI, first two DI values are removed
      # wsi before -> W1 W2 W3 W4 W5 W6
      # di before  -> D1 D1 D2 D2 D3 D3
      # wsi after  -> W1 W2 W3 W4
      # di after   -> D2 D2 D3 D3 
      # note: we do this at a different time that the load_from_dss version. 
      # The dss version truncates THEN duplicates, so only 1 DI value is removed in that
      # method. The two methods result in the same arrays.
      nd = nd - 2
      # truncate
      if len(array_wsi) < 3:
         raise ValueError("Simulations too short, must have at least 3 valid wsi values, given "+ str(data[self.wsiVar]))
      array_wsi = array_wsi[:-2]
      if len(array_di) < 3:
         raise ValueError("Simulation too short, must have at least 2 valid di values, given " + str(data[self.diVar]))
      array_di = array_di[2:]
      # check size
      wcount = len(array_wsi)
      dcount = len(array_di)
      # Set minimums and maximums of WSI and DI data series
      self.wsiMax = max(array_wsi)
      self.wsiMin = min(array_wsi)
      self.diMax = max(array_di)
      self.diMin = min(array_di)
      # Check for data exceptions
      if(wcount!=dcount):
         print("data read=" + str(data))
         print("array_wsi=" + str(array_wsi))
         print("array_di=" + str(array_di))
         raise ValueError("Data Mismatch! Wsi Values: %d, Di Values: %d" % (wcount, dcount))
      if(wcount!=nd):
         print("data read=" + str(data))
         print("array_wsi=" + str(array_wsi))
         print("array_di=" + str(array_di))
         raise ValueError("Data Mismatch! Data Values: %d, Wsi Values: %d" % (nd, wcount))
      #Set number of data points
      self.ndata = nd
      # combine separate arrays, and assign to attribute
      self.data = [(wsi, di) for wsi, di in zip(array_wsi[:nd], array_di[:nd])]
      # UPDATE: Do not read from config, just use the values set at runtime from Main.py
      # self.loadLaunchConfig()

   def load_from_dss(self, fname):
      # The below imports are depriciated
      # from vista.app import * 
      # from vista.graph import *
      from vista.set import PathPartPredicate, Pathname, SetUtils
      from vista.db.dss import DSSUtil

      print(tab + "Loading " + self.wsiVar + " and " + self.diVar)

      # initilization
      wsiMax=0.0
      wsiMin=self.wsiMax_ub
      diMin = self.wsiMax_ub*100.0
      diMax=0.0
      wcount = 0
      dcount = 0

      # create a group of the proper file
      g = DSSUtil.createGroup("local",fname)
      gWsi = g
      gDi = g.clone()
      
      # get data for wsi and di, check for exceptions
      gWsi.filterBy(PathPartPredicate("^"+self.wsiVar+"$",Pathname.B_PART),True)
      if (gWsi.getNumberOfDataReferences()!=1):
         raise Exception("No WSI Variable '"+ self.wsiVar +"' in DSS File!")
      gDi.filterBy(PathPartPredicate("^"+self.diVar+"$",Pathname.B_PART),True)
      if (gDi.getNumberOfDataReferences()!=1):
         raise Exception("No DI Variable '"+ self.diVar +"' in DSS File!")
      drWsi = gWsi.getDataReference(0)
      drDi = gDi.getDataReference(0)
      dsWsi = drWsi.getData()
      dsDi = drDi.getData()
      wsi = SetUtils.createYArray(dsWsi)
      di = SetUtils.createYArray(dsDi)
      nd = self.countDataPoints(wsi)

      # last two wsi and first di elements are removed
      nd = nd - 2

      #Define dummy arrays for holdind WSI and DI variables
      tmpWSI = []
      tmpDI = []
      tmpData = []

      #load arrays removing first DI value and last two WSI values
      for i in range(0,len(wsi)):
         if(wsi[i] >= 0.0 and wcount < nd):
            tmpWSI.append(wsi[i])
            wcount += 1
            if(wsi[i] > wsiMax ):
               wsiMax=wsi[i]
            if(wsi[i] < wsiMin ):
               wsiMin=wsi[i]
         if(di[i] >= 0.0 and dcount < nd):
            if(dcount > 0 ):
               tmpDI.append(di[i])
               dcount += 1
            tmpDI.append(di[i])
            dcount += 1
            if(di[i] < diMin ):
               diMin=di[i]
            if(di[i] > diMax ):
               diMax=di[i]

      #Correct dcount
      dcount-=1

      #Set minimums and maximums of WSI and DI data series
      self.wsiMax = wsiMax
      self.wsiMin = wsiMin
      self.diMax = diMax
      self.diMin = diMin

      # Check for data exceptions
      if(wcount!=dcount):
         raise Exception("Data Mismatch! Wsi Values: "+ str(wcount) +" Di Values: "+ str(dcount))
      if(wcount!=nd):
         raise Exception("Data Mismatch! Data Values: "+ str(nd) +" Wsi Values: "+ str(wcount))

      #Set number of data points
      self.ndata = nd

      # load data array with wsi-di data series
      for i in range(0,self.ndata):
         tmpData.append((tmpWSI[i],tmpDI[i]))
      self.data = tmpData[:]
      # UPDATE: Do not read from config, just use the values set at runtime from Main.py
      #self.loadLaunchConfig()

   # extract array of wsi values from wsi-di array
   def getInputWsi(self):
      wsi = []
      for i in range(0,self.ndata):
         wsi.append(self.data[i][0])
      return wsi

   # extracts array of di values from wsi-di array
   def getInputDi(self):
      di = []
      for i in range(0,self.ndata):
         di.append(self.data[i][1])
      return di

   # calculates slope
   def getSlope(self, wsi0, di0, offset):
      wsiend = wsi0 + self.step
      npoints=0
      wsiin = self.getInputWsi()
      diin = self.getInputDi()
      slope = 0.0
      delwsi = 0.0
      sumdelwsi = 0.0
      sumdiwsi = 0.0
      sumwsi2 = 0.0
      if (wsi0 < (0.33*(self.wsiMax-self.wsiMin)+self.wsiMin)):
         offset *= self.offset1
      elif (wsi0 < (0.5*(self.wsiMax-self.wsiMin)+self.wsiMin)):
         offset *= 1.0
      else:
	 offset *= 0.0
      for i in range(self.ndata):
    	 if (wsiin[i] > wsi0 and wsiin[i] <= (wsiend + self.lookahead)):
            npoints += 1
	    delwsi = wsiin[i] - wsi0
	    sumdelwsi = sumdelwsi + wsiin[i] - wsi0
	    sumdiwsi = sumdiwsi + (diin[i]-offset)*delwsi
	    sumwsi2 = sumwsi2 + delwsi*delwsi
      if (npoints > 1 ):
         slope = (sumdiwsi - di0*sumdelwsi)/sumwsi2
      else:
         slope = 0.0
      if (slope < 0.0):
         slope = 0.0
      return slope

   #Get function pair returns a single point on the wsi-di curve
   def getFunctionPair(self, wsi0, di0, offset):
      wsiend = wsi0 + self.step
      slope = self.getSlope(wsi0,di0,offset)
      diend = di0 + slope*(wsiend - wsi0)
      # if wsi0 minimal, set first di equal to the min of the set
      if (wsi0 < 1.0):
         diend=self.diMin
      pair = (wsiend,diend)   # assign wsi/di variables for return
      return pair

   # gets cumulative step-wise standard deviation for input data set as projected from initial wsi-di curve
   def getStandardDev(self,data):
      slope = 0.0
      dist = 0.0
      sumdist = 0.0
      sumdist2 = 0.0
      stdev = 0.0
      npoints=0
      wsiin = self.getInputWsi()
      diin = self.getInputDi()
      for i in range(0,self.nseg):
         wsi0 = data[i][0]
	 di0 = data[i][1]
         for j in range(0,self.ndata):
            if (wsiin[j] > wsi0 and wsiin[j] < (wsi0 + self.step)):
               npoints += 1
 	       slope = self.getSlope(wsi0,di0,0.0)
 	       dist = diin[j] - (di0 + slope*(wsiin[j] - wsi0))
 	       sumdist = sumdist + dist
 	       sumdist2 = sumdist2 + dist*dist
      if (npoints > 0):
         stdev = sqrt((npoints*sumdist2 - sumdist*sumdist)/(npoints*(npoints-1)))
      else:
         stdev = 0.0
      return stdev

   #getAllFunctionPairs returns the final wsi-di curve
   def getAllFunctionPairs(self):
      data = []
      wsi0 = 0.0
      di0 = 0.0
      data.append((wsi0,di0))
      for i in range (1,self.nseg+1):
         data.append(self.getFunctionPair(wsi0,di0,0.0))
	 wsi0 = data[i][0]
	 di0 = data[i][1]
      stdev = self.getStandardDev(data)
      offset = self.mult*stdev
      wsi0 = 0.0
      di0 = 0.0
      data[0] = (wsi0,di0)
      for i in range (1,self.nseg+1):
         data[i] = self.getFunctionPair(wsi0,di0,offset)
	 wsi0 = data[i][0]
	 di0 = data[i][1]
      return data

   #save puts the wsi-di curve in the user specified *.table file
   def save(self,data):
      print("Saving WSI_DI_" + self.name)
      sep = File.separator
      fname = self.lookupName + File.separator + "wsi_di_" + self.name + ".table"
      pw = PrintWriter(BufferedWriter(FileWriter(fname)))
      pw.println("wsi_di_" + self.name)
      pw.println("wsi                   di")
      for i in range(0,self.nseg+1):
	 #pw.println(str(int(round(data[i][0]))) + "        " + str(int(round(data[i][1]))))
	 pw.println(str(data[i][0]) + "        " + str(data[i][1]))
      pw.close()

   #execute is the function call to generate a wsi-di table
   def execute(self):
      print tab + "Generating WSI_DI_" + self.name
      output = self.getAllFunctionPairs()
      self.save(output)

   #load launch configuration
   def loadLaunchConfig(self):
      tree = ET.parse(self.launchName)
      root = tree.getroot()
      for stringAttrib in root.findall('stringAttribute'):
         if stringAttrib.attrib['key']=='wpp.debugModel.ATTR_WPP_WSIDIOFFSET':
            self.offset1=float(stringAttrib.attrib['value'])
