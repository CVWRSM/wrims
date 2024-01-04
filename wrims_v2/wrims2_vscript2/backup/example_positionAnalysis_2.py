#=====================================================================================
#   Position analysis using CalLite. 
#   Run start time is Oct 2012.
#
## Step 1. Provide initial data for decision variables:  /Run/DSS/CL_INIT_2012.dss
## Step 2. Provide 2012 annual requests data in this table: /Run/Lookup/PA_Base_D1641_Existing/AnnualReqDel_swp.table
## Step 3. Provide 2012 water year type in these tables: wytype, wytypeD1485, wytypeDefic, and wytypeSnow
## Step 4. Enter data in the following "User Input" section
## Step 5. Open a command prompt and type "vscript example_positionAnalysis.py"
## Step 6. A batch file named "CalLitePA_demo.bat" will be generated. Double click this batch file to run the project.
## Step 7. After runs completed, open this dss file to see results: /studies/callite_D1641Existing_PA__2012oct/Run/DSS/CalLitePA_demo_DV.dss to see results
#=====================================================================================

from os import path 
from scripts.wrims2.study import Study
from scripts.wrims2.runGroup import RunGroup
from scripts.tool import LogUtils, Param, LookupTable, FileUtils, DssVista
Param.mainScriptPath = path.abspath(__file__)
Param.mainScriptDir  = path.dirname(Param.mainScriptPath)
LogUtils.initLogging()



# User Input
#=====================================================================================

# projectName will be used to prepend generated files and folders.
projectName ="CalLitePA_demo"

# studyRunDir is the run directory of the CalLite study
studyRunDir = path.join(Param.mainScriptDir, "studies/callite_old/Run")

# svarOriginalFile is the svar dss file path
svarOriginalFile =path.join(studyRunDir, "DSS/CL_EXISTING_BO_081011_SV.dss")

# InitFile
initFile=path.join(studyRunDir, "DSS/CL_INIT_2012.dss")
initFPart = "PA"

# SvarAPart
svarAPart="CalLite"



# lookupOriginalDir is the directory of lookup tables
lookupOriginalDir = path.join(studyRunDir, "Lookup", "PA_Base_D1641_Existing" )

# futureWY is the beginning water year that the historical svar data will be copied to
futureWY = 2013
startYear=2012 #  calendar year 2012 oct is water year 2013 oct
startMonth=10  #  tied initial data


# historyWYs are the historical svars that will be used to simulate future water year svars
#historyWYs = [ x for x in range(1935, 1945)]
historyWYs = [1929, 1930, 1931, 1932, 1933, 1934, 1976, 1977]
#historyWYs = [ 1935, 1940]

# if sequentialYRs is 2, historyWYs is 1935, and futureWY is 2013, 
# then 1935 svar will be copied to 2013 and 1936 svar will be copied to 2014 
sequentialYRs = 1

# specify the number of runs at the same time
simultaneousRuns = 1 

# UARM: Upper American River Model, that is storage in French Meadows, 
# Hell Hole, and Union Valley Reservoirs, which are upstream of Folsom.
# - Tom Fitzhugh 

# in this demo you need to provide UARM data at September 2012
UARM_at_2012_09 = [300] 

# yearly water type tables that will copy historical water year type to future
yearlyTableList = ["wytypes.table",
                   "wytypesjr.table",
                   "wytypeD1485.table",
                   "wytypeDefic.table",
                   "wytypeSnow.table",
                   "AnnualReqDel_swp.table",
                   "SacValleyIndex.table",
                   "eightriver.table",
                   "FWS_BO_A3_Temp.table",
                   "wheelCap.table",
                   "sacramento_runoff_forecast.table",
                   "american_runoff_forecast.table",
                   "febeiratio.table",
                   "delta_index.table",
                ]

# monthly water type tables that will copy historical water year type to future
monthlyTableList = ["feather_runoff_forecast.table",
                    "x2days.table",
                ]

#=====================================================================================




runGroup = RunGroup(projectName)
svarShiftedFile= path.join(path.dirname(svarOriginalFile), projectName + "_SV.dss")
dvarFile= path.join(path.dirname(svarOriginalFile), projectName + "_DV.dss")


for beginWY in historyWYs:

    endWY = beginWY + sequentialYRs - 1
    # dir for outTable, e.g., PA_1945_1947
    outSubDir = projectName+"_"+str(beginWY)+"_"+str(endWY)

# prepare tables

    # delete outSubDir content    
    FileUtils.erase( path.join(studyRunDir, "Lookup", outSubDir))

    # copy all tables from LookupOriginalDir to outSubDir
    FileUtils.copyAll(lookupOriginalDir, path.join(studyRunDir, "Lookup", outSubDir))


    for tableName in yearlyTableList:
        outTablePath = path.join(studyRunDir, "Lookup", outSubDir, tableName)
        inTablePath = path.join(lookupOriginalDir, tableName)
    
        LookupTable.copyYearlyTableToFuture(inTablePath, outTablePath, beginWY, sequentialYRs, futureWY)


    for tableName in monthlyTableList:
        
        outTablePath = path.join(studyRunDir, "Lookup", outSubDir, tableName)
        inTablePath = path.join(lookupOriginalDir, tableName)
    
        LookupTable.copyMonthlyTableToFuture(inTablePath, outTablePath, beginWY, sequentialYRs, futureWY)



# prepare svar file

    outSvarFpart = str(beginWY)+"_"+str(endWY) # e.g., 1945_1947
    studyName = projectName+'_'+outSvarFpart

    DssVista.copyDssToFuture_waterYear(svarOriginalFile, svarShiftedFile, beginWY, sequentialYRs, 0, futureWY, outSvarFpart)    
    DssVista.array2dss(svarShiftedFile, UARM_at_2012_09, "30SEP2012 2400", "/"+svarAPart+"/UARM/STORAGE//1MON/"+ outSvarFpart +"/", "TAF")



# prepare config and batch files

    s = Study(studyName, studyRunDir)
    s.createConfig(
                WreslPlus='Yes',
                MainFile='Run/Main_PA_2012.wresl',
                StartYear=startYear, 
                StartMonth=startMonth,
                InitFile=path.relpath(initFile, path.dirname(studyRunDir)),
                InitFPart=initFPart,  
                SvarFile=path.relpath(svarShiftedFile, path.dirname(studyRunDir)), 
                SvarAPart=svarAPart,
                DvarFile=path.relpath(dvarFile, path.dirname(studyRunDir)), 
                SvarFPart=outSvarFpart, 
                NumberOfSteps=sequentialYRs*12, 
                LookupSubDir=studyName,
                PrefixInitToDvarFile='Yes')
    
    s.writeBatch(pause=False) # set pause=True to inspect error message
    runGroup.add(s)

# write batch for all runs
runGroup.writeBatch(simultaneousRuns)
