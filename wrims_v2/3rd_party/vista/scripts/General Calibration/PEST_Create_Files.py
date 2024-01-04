import re, os, glob, datetime, random, math
from vtimeseries import *
from vdss import *
#from vista.db.dss import *
from vutils import *
from vista.time import TimeFactory, TimeFormat, DefaultTimeFormat
from gov.ca.dsm2.input.parser import Parser
from gov.ca.dsm2.input.parser import Tables
from gov.ca.dsm2.input.model import *

def elAbs(el):
    # absolute value of element if acceptable value
    if filter.isAcceptable(el):
        el.setY(abs(el.getY()))
def elMul1000(el):
    # Multiply element by 1000. to convert from Milli to Micro
    if filter.isAcceptable(el):
        el.setY(el.getY()*1000.)
def obsDataBParts(str):
    return str.upper().split('/')[2]
def obsDataCParts(str):
    return str.upper().split('/')[3]
def parseInpSects(DSM2InpFile,Section):
    # parse the given DSM2 Input file for Section,
    # return as a list of lists (the rows/columns)
    try: EID = open(DSM2InpFile,'r')
    except: 
        print 'Unable to open file', DSM2InpFile
        return None
    inSection = False
    sectList = []
    for line in EID:
        if line.upper().strip() == Section.upper():
            inSection = True
            continue
        if inSection:
            if line.upper().strip() == 'END':
                break
            if not re.search('^#',line.lstrip()):
                # not a comment or blank line, use it
                sectList.append(line.split())
    EID.close()
    return(sectList)
def countDevParams(devList,coeffList):
    '''
    Count the number of device parameters for PEST in devList,
    using the flow coefficient headers in coeffList. Don't count
    flow coefficients==0
    '''
    count = 0
    # find fields with the flow coeffs
    for rowNo in range(1,len(devList)):
        for name in coeffList:
            loc = devList[0].index(name)
            if float(devList[rowNo][loc]) != 0.0:
                count += 1
    return count
def shortenName(nameList,longName,maxChars,Prefix=False):
    """
    Shorten the long name longName using the following rules:
    * If longName is less than or equal to maxChars, just
      return longName
    * Else, create a short name using optional Prefix, or nothing if empty or False,
      and a counting integer. Try the resulting short name in nameList. If it doesn't
      exist yet, store it in the dictionary and return the short name as the value.
      If it exists, increment the counter and try again until a unique short name is
      produced.
    """
    if len(longName) <= maxChars:
        return longName
    if Prefix:
        prefx = Prefix
    else:
        prefx = ''
    if not longName in nameList:
        shortName = prefx + str(len(nameList))
        nameList.append(longName)
    else:
        shortName = prefx + str(nameList.index(longName))
    if len(shortName) > maxChars:
        raise "Short name for",longName,"is too long, try shorter prefix"
    return shortName
#
def tieFixNone(file):
    """
    Read from input text file name the tied, fixed, and none (estimable) channel
    information. File format is:
    ChannelNumber TIED_FIXED TIED_TO  
    248 TIED 249  
    249 NONE 0  
    250 TIED 249  
    251 TIED 119  
    252 FIXED 0  
    253 FIXED 0
         ...
    All channels should be listed in the file. The first line is a header line
    and ignored. 
    Subsequent lines are channel number; 'TIED', 'FIXED', or 'NONE';
    and if TIED, a second channel number that this channel is tied to.
    A 2nd channel number for FIXED and NONE channels is ignored.
    A dictionary of the input is returned.
    """
    try: fid = open(file,'r')
    except:
        print 'Cannot open tie/fix file',file
        raise
    lines = fid.readlines()
    fid.close()
    TieFixDict = dict()
    for line in lines:
        lineList = line.rstrip().rsplit()
        TieFixDict[lineList[0]] = lineList[1:3]
    return TieFixDict
#
def getObsGroup(pathname):
    """
    Create the observed group name from the pathname (input);
    this can be either a simple group name from the data type
    (C Part of path) only, or a more complex group name from
    the C Part and B Part (location) together. Comment out
    whichever line below you don't want it to be.
    """
    obsGrp = obsDataBParts(pathname).upper()+':'+obsDataCParts(pathname).upper()
    return obsGrp.replace('STAGE','STG').replace('FLOW','Q')
#
if __name__ == '__main__':
    TF = TimeFactory.getInstance()
    filter = Constants.DEFAULT_FLAG_FILTER
    random.seed()
    # Create a .pst file (PEST Control File), a PEST Template File (.tpl),
    # and a .ins file (PEST Instruction File) for the PEST calibration of DSM2
    #
    sq = "'"
    dq = '"'
    bs = "\\"
    ##
    ## Basic Settings/Most commonly changed settings
    ##
    # DSM2 runs: restart or cold start?
    useRestart = True
    # continue on DSM2 failure, or fail PEST run?
    contDSM2 = True
    # run blocks (run periods); use 1 for calibration, the other for validation
    useB1 = True
    useB2 = False
    #
    NOPTMAX = 0     # Only one model run
    NOPTMAX = -1    # Jacobian (run on all params)
    NOPTMAX = 4     # Calibration
    NOPTMAX = 1     # Sensitivities and one calib iter
    #
    # Number of condor slaves
    NCondor = 45
    ## Allow a few to be read as command line arguments for batch scripts
    for arg in sys.argv[1:]:
        print 'Using argument',arg
        exec(arg)
    ##
    if useB1 and useB2:
        raise 'Use Block 1 or Block 2, not both.'
    if not useB1 and not useB2:
        raise 'Specify Block 1 or Block 2 to use.'
    ##
    # what type of PEST parallel run?
    typePEST = 'beo'
    #typePEST = 'genie'
    ##
    # use tied/fixed parameters?
    useTiedFixed = False
    ##
    # Observed Parameter Groups
    # Use either Width or Elev, not both
    paramGrpDER = [ \
#                    ['MANN',0.001] \
#                    ['DISP',10.0] \
#                    ['LENGTH' ,50.0], \
#                    ['GATECF' ,0.05], \
#                    ['RESERCF' ,0.05], \
#                    ['WIDTH' ,0.01], \
#                    ['ELEV' ,0.01], \
                    ['DIV-FLOW' ,0.1], \
                    ['DRAIN-FLOW' ,0.1], \
                    ['DRAIN-EC' ,0.1] \
        ]
    # compare obs/model data this time back from model run end time 
    cmpDataTimeLength = '112DAY'
    #cmpDataTimeLength = '55DAY'
    #
    paramGroups, paramDERINCLB = zip(*paramGrpDER)
    paramGroups = list(paramGroups)
    paramDERINCLB = list(paramDERINCLB)
    # DSM2 start run dates; these must match the DSM2 BaseRun dates
    # if a restart file is used
    # runLength is years
    B1StartDateObj_Hydro = TF.createTime('01OCT2008 0000')
    B2StartDateObj_Hydro = TF.createTime('01OCT2001 0000')
    if not useRestart:
        B1StartDateObj_Hydro = B1StartDateObj_Hydro - TF.createTimeInterval('1YEAR')
        B2StartDateObj_Hydro = B2StartDateObj_Hydro - TF.createTimeInterval('1YEAR')
    #
    B1EndDateObj_Hydro = TF.createTime('01OCT2009 0000')
    B2EndDateObj_Hydro = TF.createTime('31DEC2002 0000')    
    # Qual start/end dates are one day after/before Hydro
    B1StartDateObj_Qual = B1StartDateObj_Hydro + TF.createTimeInterval('1DAY')
    B1EndDateObj_Qual = B1EndDateObj_Hydro  - TF.createTimeInterval('1DAY')
    B2StartDateObj_Qual = B2StartDateObj_Hydro + TF.createTimeInterval('1DAY')
    B2EndDateObj_Qual = B2EndDateObj_Hydro - TF.createTimeInterval('1DAY')
    # Comparison start and end dates must be within the run dates, 
    # and are used for observed and DSM2 comparison data. 
    # A delayed comparison date allows DSM2 to equilibrate with
    # its new parameters. 
    B1CmpStartDateObj = B1EndDateObj_Qual - TF.createTimeInterval(cmpDataTimeLength)
    B1CmpEndDateObj = B1EndDateObj_Qual - TF.createTimeInterval('1DAY')
    B1CmpTW = TF.createTimeWindow(B1CmpStartDateObj, B1CmpEndDateObj)
    B2CmpStartDateObj = B2EndDateObj_Qual - TF.createTimeInterval(cmpDataTimeLength)
    B2CmpEndDateObj = B2EndDateObj_Qual - TF.createTimeInterval('1DAY')
    B2CmpTW = TF.createTimeWindow(B2CmpStartDateObj, B2CmpEndDateObj)
    # DSM2 directories and files
    DSM2Mod = 'HIST-CLB2K'
    DSM2Run = 'BASE-v812'
    WorkspaceDir = r'D:\workspace\vista\scripts\General Calibration'
    RootDir = 'D:/delta/models/'
    CommonDir = RootDir + 'dsm2_v8/common_input/'
    CalibDir = RootDir + '201X-Calibration/'
    TimeSeriesDir = RootDir + 'dsm2_v8/timeseries/'
    BaseRunDir = CalibDir + 'BaseRun-PEST/Output/'
    HydroEchoFile = BaseRunDir + 'hydro_echo_' + DSM2Mod + '-' + DSM2Run + '-B1.inp'
    QualEchoFile = BaseRunDir + 'qual_echo_' + DSM2Mod + '-' + DSM2Run + '-B1.inp'
    DivRtnQFile = 'dicu_201203.dss'
    RtnECFile = 'dicuwq_3vals_extended.dss'
    ChanInpFile = 'channel_std_delta_grid_NAVD_20121214-calib.inp'
    GateInpFile = 'gate_std_delta_grid_20110418_NAVD.inp'
    ResInpFile = 'reservoir_std_delta_grid_NAVD_20121214.inp'
    ChanCalibFile = 'Calib-channels.inp'
    GateCalibFile = 'Calib-gates.inp'
    ResCalibFile = 'Calib-reservoirs.inp'
    XSectsCalibFile = 'Calib-xsects.inp'
    # 'Dummy' input files for cross-section & Ag div/drainage/EC
    # calibration (multiplier) factors; stores floating-point
    # numbers used to multiply the time-series values
    PESTInpXCFile = 'XCCalibCoeffs.inp'
    PESTInpAgFile = 'AgCalibCoeffs.inp'
    #
    fileSub = 'dsm2-base.sub'
    fileRun4 = 'dsm2-run4' + typePEST.upper() + '.bat'
    # observed data
    obsDataDir = CalibDir + 'Observed Data/'
    obsDataFile = obsDataDir + 'CalibObsData.dss'
    obsDataPaths = obsDataDir + 'CalibObsData.txt'
    obsDSM2ChanLocs = CalibDir + 'DSM2_Locs.txt'
    # PEST outputs for Hydro and Qual runs, these contain output paths
    # matching observed data paths
    DSM2DSSOutHydroFile = 'PEST_Hydro_Out.inp'
    DSM2DSSOutQualFile = 'PEST_Qual_Out.inp'
    # The DSS file containing combined Hydro and Qual output
    CalibDSSOutFile = 'PESTCalib.dss'
    # The text equivalent of the DSS output, necessary for PEST
    DSM2OutFile = 'PESTCalib.out'
    # Pest directories and files
    PESTDir = CalibDir + 'PEST/'
    # PEST control file name
    PESTFile = 'DSM2.pst'
    PESTChanTplFile = ChanCalibFile.split('.')[0] + '.tpl'
    PESTGateTplFile = GateCalibFile.split('.')[0] + '.tpl'
    PESTResTplFile = ResCalibFile.split('.')[0] + '.tpl'
    PESTTplXCFile = PESTInpXCFile.split('.')[0] + '.tpl'
    PESTTplAgFile = PESTInpAgFile.split('.')[0] + '.tpl'
    PESTInsFile = DSM2OutFile.split('.')[0] + '.ins'
    # tied, fixed, and none (if used) for channel groups
    # the text file was produced using ArcMap (export to text file)
    # create a dictionary for tied/fixed/none channels
    if useTiedFixed:
        TieFixChans = tieFixNone(PESTDir + 'TieFix.txt')
    # remove old files
    for f in glob.glob(PESTDir + '*.tpl'):
        os.remove(f)
    ##
    # read DSM2 base run info
    p = Parser()
    tablesHydro = p.parseModel(HydroEchoFile)
    tablesQual = p.parseModel(QualEchoFile)
    Channels = tablesHydro.toChannels()
    bndryInputsHydro = tablesHydro.toBoundaryInputs()
    srcAgInputsHydro = bndryInputsHydro.getSourceFlowInputs()
    bndryInputsQual = tablesQual.toBoundaryInputs()
    #srcAgInputsQual = bndryInputsQual.getSourceFlowInputs()
    tableNodeConc = tablesQual.getTableNamed('NODE_CONCENTRATION')
    # read the gate input file for gate data
    gatePipeList = parseInpSects(CommonDir + GateInpFile,'GATE_PIPE_DEVICE')
    gateWeirList = parseInpSects(CommonDir + GateInpFile,'GATE_WEIR_DEVICE')
    # read the reservoir input file for reservoir connection data
    resCFList = parseInpSects(CommonDir + ResInpFile,'RESERVOIR_CONNECTION')
    ##
    # Observed data files, etc.
    # Observed data paths; the DSM2 output paths are determined from these.
    ##
    ## All observed data should already be in RTS 1HOUR time interval,
    ## checked for errors, units reconciled, etc.
    ##
    obsPaths = []
    fid = open(obsDataPaths, 'r')
    for line in fid.readlines():
        if line[0] == '#':
            continue
        obsPaths.append(line.rstrip())
    fid.close()
    # DSM2 output locations
    # Each observed B part (location) must have a corresponding
    # DSM2 channel/length in this list of tuples
    DSM2ObsLoc = []
    fid = open(obsDSM2ChanLocs,'r')
    for line in fid.readlines():
        if line[0] == '#':
            continue
        tmp = line.rstrip().split()
        sta = tmp[0]
        chanNo = int(tmp[1])
        chanDist = tmp[2]
        if chanDist != 'END':
            chanDist = int(chanDist)
        else:
            chanDist = Channels.getChannel(str(chanNo)).getLength()
        DSM2ObsLoc.append(tuple([sta, chanNo, chanDist]))
    fid.close()
    # sort the paths to reproduce them in the same order
    # from the DSM2 output paths; sorting will be alphabetically
    # by C part (data type), then B part (location)
    obsPaths = sorted(obsPaths, key=obsDataBParts)
    obsPaths = sorted(obsPaths, key=obsDataCParts)
    #
    if 'DIV-FLOW' in paramGroups or 'DRAIN-FLOW' in paramGroups or 'DRAIN-EC' in paramGroups:
        PIAFId = open(PESTDir + PESTInpAgFile,'w')
        PTAFId = open(PESTDir + PESTTplAgFile,'w')
        PTAFId.write('%s\n' % ('ptf @'))
    if 'ELEV' in paramGroups or 'WIDTH' in paramGroups:
        PIXFId = open(PESTDir + PESTInpXCFile,'w')
        PTXFId = open(PESTDir + PESTTplXCFile,'w')
        PTXFId.write('ptf @\n')
    #
    PCFId = open(PESTDir + PESTFile,'w')
    #
    ObsTempFile1 = PESTDir + 'temp1.txt'
    OTF1Id = open(ObsTempFile1,'w')
    #
    # read observed data DSS file at desired locations and date range
    # write observed data to a temporary file for later inclusion in
    # the .pst file.
    #
    # Also produce the DSM2 Hydro and Qual output files for PEST calibration
    dss_group = opendss(obsDataFile)
    nObs = 0
    obsGroups = []
    dataTypes = []
    #
    DSM2HydroId = open(PESTDir + DSM2DSSOutHydroFile,'w')
    DSM2QualId = open(PESTDir + DSM2DSSOutQualFile,'w')
    DSM2HydroId.write('# PEST Calibration output for HYDRO.\n' \
                      'OUTPUT_CHANNEL\n' \
                      'NAME CHAN_NO DISTANCE VARIABLE INTERVAL PERIOD_OP FILE\n')
    DSM2QualId.write('# PEST Calibration output for QUAL.\n' \
                      'OUTPUT_CHANNEL\n' \
                      'NAME CHAN_NO DISTANCE VARIABLE INTERVAL PERIOD_OP FILE\n')
    if useB1:
        print 'Processing observed data, Block 1',B1CmpTW.toString()
        print
        CmpTW = B1CmpTW
        CmpStartDateObj = B1CmpStartDateObj
        CmpEndDateObj = B1CmpEndDateObj
    if useB2:
        print 'Processing observed data, Block 2', B2CmpTW.toString()
        print
        CmpTW = B2CmpTW
        CmpStartDateObj = B2CmpStartDateObj
        CmpEndDateObj = B2CmpEndDateObj
    totalDT= {"EC":0., "STAGE":0., "FLOW":0.}
    countDT= {"EC":0, "STAGE":0, "FLOW":0}
    for obsPath in obsPaths[:]:
        g = find(dss_group,obsPath.replace('+','\+'))
        dataref = g.getAllDataReferences()
        if len(dataref) > 1:
            print 'Error, too many observed DSS paths for',obsPath
            sys.exit()
        if len(dataref) < 1:
            print 'Dropping path', obsPath, 'observed DSS path not found'
            del obsPaths[obsPaths.index(obsPath)]
            continue
        dataref = dataref[0]
        staName = dataref.getPathname().getPart(Pathname.B_PART)
        dataType = dataref.getPathname().getPart(Pathname.C_PART)
        dataTW = dataref.getTimeWindow()
        dataset = dataref.getData()
        perType = dataset.getAttributes().getYType().upper()
        #print staName, dataType
        dataCmpTW = dataTW.intersection(CmpTW)
        if dataCmpTW == None:
            print 'Dropping path', obsPath, 'no observed data in time window'
            del obsPaths[obsPaths.index(obsPath)]
            continue
        dataCmpDays = (dataCmpTW.getEndTime()-dataCmpTW.getStartTime()).getIntervalInMinutes( \
                       dataCmpTW.getStartTime())/24./60.
        if dataCmpDays < 25.:
            print 'Dropping path', obsPath, 'Not enough data in time window (' + \
                str(dataCmpDays) + ' days)'
            del obsPaths[obsPaths.index(obsPath)]
            continue
        # check for a corresponding DSM2 channel output for the observed data
        try: tup = [t for t in DSM2ObsLoc if t[0] == staName][0]
        except:
            print 'Dropping path', obsPath, 'no channel location for station', staName
            del obsPaths[obsPaths.index(obsPath)]
            continue
        # get data and accumulate totals for later weighting calcs
        dataset = dataset.createSlice(CmpTW)
        countPath = Stats.countAcceptable(dataset, filter)
        if countPath <= 0:
            print 'Dropping path', obsPath, 'no valid data in time window'
            del obsPaths[obsPaths.index(obsPath)]
            continue
        if len(staName) > 7:
            print 'Dropping path', obsPath, 'station name', staName, 'too long'
            del obsPaths[obsPaths.index(obsPath)]
            continue
        obsGroup = getObsGroup(dataref.getPathname().toString())
        if obsGroup not in obsGroups:
            obsGroups.append(obsGroup)
        if dataType not in dataTypes:
            dataTypes.append(dataType)
        # use micro mhos/cm (micro siemens/cm) for all ECs
        if dataset.getAttributes().getYUnits().upper() == 'MS/CM' or \
            dataset.getAttributes().getYUnits().upper() == 'MMHOS/CM':
            apply(dataset,elMul1000)
        # accumulate the total absolute value of each data type
        # for PEST weight of observations later
        apply(dataset,abs)
        totalPath = total(dataset)
        totalDT[dataType] += totalPath
        countDT[dataType] += countPath
    # End obsPaths test loop
    for obsPath in obsPaths:
        g = find(dss_group,obsPath.replace('+','\+'))
        dataref = g.getAllDataReferences()[0]
        dfCmp = extendDataRef(dataref,CmpTW)
        staName = dataref.getPathname().getPart(Pathname.B_PART)
        dataType = dataref.getPathname().getPart(Pathname.C_PART)
        dataIntvl = dataref.getPathname().getPart(Pathname.E_PART)
        perType = dataset.getAttributes().getYType().upper()
        obsGroup = getObsGroup(dataref.getPathname().toString())
        dataset = dfCmp.getData()
        # get data, assign 0 weight if missing, correct EC units,
        # write to temp file
        # use micro mhos/cm (micro siemens/cm) for all ECs
        if dataset.getAttributes().getYUnits().upper() == 'MS/CM' or \
            dataset.getAttributes().getYUnits().upper() == 'MMHOS/CM':
            apply(dataset,elMul1000)
        pathWeight = 1./(totalDT[dataType]/countDT[dataType])
        tsi = dataset.getIterator()
        while not tsi.atEnd():
            el = tsi.getElement()
            nObs += 1
            el = tsi.getElement()
            timeObj = TF.createTime(long(el.getX()))
            dateStr = timeObj.format(DefaultTimeFormat('yyyyMMdd'))
            timeStr = timeObj.format(DefaultTimeFormat('HHmm'))
            val = el.getY()
            valStr = '%15.3f' % (val)
            if filter.isAcceptable(el):
                weight = pathWeight
            else:
                weight = 0.0
            dT = dataType.replace('STAGE','STG').replace('FLOW','Q')
            OTF1Id.write("%-30s %s %12.5E %s\n" % \
                         (staName+dT+dateStr+timeStr, valStr, weight, obsGroup))
            tsi.advance()
        # write the corresponding DSM2 output line for the observed data path
        tup = [t for t in DSM2ObsLoc if t[0] == staName][0]
        chanNo = tup[1]
        chanDist = tup[2]
        fmtStr = '%s    %3d %8d   %s     %s      %s  %s\n'
#         if perType == "INST-VAL":
#             pT = "INST"
#         elif perType == "PER-AVER":
#             pT = "AVE"
#         else:
#             pT = perType
        pT = "AVE"  # almost all observed data should be 1HOUR averaged
        #print obsPath, perType, chanNo, chanDist
        if dataType.lower() == 'stage' or \
           dataType.lower() == 'flow':
            DSM2HydroId.write(fmtStr % (staName, chanNo, chanDist, \
                                        dataType, dataIntvl, pT, CalibDSSOutFile))
        else:
            DSM2QualId.write( fmtStr % (staName, chanNo, chanDist, \
                                        dataType, dataIntvl, pT, CalibDSSOutFile))
    # End obsPaths write loop
    DSM2HydroId.write('END')
    DSM2QualId.write('END')
    OTF1Id.close()
    DSM2HydroId.close()
    DSM2QualId.close()
    print 'Wrote', DSM2HydroId.name
    print 'Wrote', DSM2QualId.name
    obsGroups.sort()    # ensure the data groups are alphabetical order
    # PEST inputs here
    # number of calibration parameters
    NPAR = 0
    if 'MANN' in paramGroups:
        NPAR += len(Channels.getChannels())
    if 'DISP' in paramGroups:
        NPAR += len(Channels.getChannels())
    if 'LENGTH' in paramGroups:
        NPAR += len(Channels.getChannels())
    if 'GATECF' in paramGroups:
        # count only gates with non-zero flow coefficients
        NPAR += countDevParams(gateWeirList,['CF_FROM_NODE','CF_TO_NODE'])
        NPAR += countDevParams(gatePipeList,['CF_FROM_NODE','CF_TO_NODE'])
    if 'RESERCF' in paramGroups:
        NPAR += countDevParams(resCFList,['COEF_IN','COEF_OUT'])
    if 'ELEV' in paramGroups or \
        'WIDTH' in paramGroups:
        for chan in Channels.getChannels():
                NPAR += len(chan.getXsections())
    paramUp = 'DIV-FLOW'
    if paramUp in paramGroups:
        for srcInput in srcAgInputsHydro:
            try: CPartUp = srcInput.path.upper().split('/')[3]
            except: continue
            if CPartUp == paramUp:
                NPAR += 1
    paramUp = 'DRAIN-FLOW'
    if paramUp in paramGroups:
        for srcInput in srcAgInputsHydro:
            try: CPartUp = srcInput.path.upper().split('/')[3]
            except: continue
            if CPartUp == paramUp:
                NPAR += 1
    paramUp = 'DRAIN-EC'
    if paramUp in paramGroups:
        for row in tableNodeConc.getValues():
            path = row[5]
            if path.find('/') >= 0:
                pathParts = path.split('/')
                CPartUp = pathParts[3]
                try: node3 = "%03d" % int(pathParts[2])
                except: continue
                if CPartUp == paramUp:
                    NPAR += 1
    NOBS = nObs
    NPARGP = len(paramGroups)
    NPRIOR = NPAR # every parameter has prior information
    # but don't use those FIXED or TIED
    if useTiedFixed:
        NPRIOR = 0
        for key, value in sorted(TieFixChans.iteritems(), key=lambda (k,v): (v,k)):
            if value[0].upper() == 'NONE':
                NPRIOR += 1
    #
    NOBSGP = len(obsGroups)+NPARGP
    NTPLFLE = 0
    if 'MANN' in paramGroups or \
        'DISP' in paramGroups or \
        'LENGTH' in paramGroups:
            NTPLFLE += 1
    if 'GATECF' in paramGroups:
            NTPLFLE += 1
    if 'RESERCF' in paramGroups:
            NTPLFLE += 1
    if 'DIV-FLOW' in paramGroups or 'DRAIN-FLOW' in paramGroups or 'DRAIN-EC' in paramGroups:
            NTPLFLE += 1
    if 'ELEV' in paramGroups or 'WIDTH' in paramGroups:
            NTPLFLE += 1
    PESTMODE = 'estimation'
    RSTFLE = 'norestart'
    NINSFLE = 1
    PRECIS = 'single'
    DPOINT = 'point'
    NUMCOM = 1
    JACFILE = 0
    MESSFILE = 0
    PHIRATSUF = 0.3
    PHIREDLAM = 0.01
    RELPARMAX = 5.0
    FACPARMAX = 5.0
    FACORIG = 0.001
    PHIREDSWH = 0.1
    PHIREDSTP = 0.005
    NPHISTP = 4
    NPHINORED = 3
    RELPARSTP = 0.01
    NRELPAR = 3
    ICOV = 0
    ICOR = 0
    IEIG = 0
    # SVD values
    SVDMODE = 1
    MAXSING = 60
    EIGTHRESH = 1.0E-5
    EIGWRITE = 1
    # Least Squares values
    LSQRMODE = 0
    LSQR_ATOL = 1.e-10
    LSQR_BTOL = 1.e-10
    LSQR_CONLIM = 1.e3
    LSQR_ITNLIM = 10000
    LSQRWRITE = 1
    # Marquadt Lambda
    # ignore PEST manual about NUMLAM=0 when using SVD
    RLAMBDA1 = 5.0
    RLAMFAC = 2.0
    NUMLAM = 15
# print the header and PEST control info to the PEST control (.pst) file
    PCFId.write('pcf\n')
    PCFId.write('* control data\n')
    PCFId.write('%s %s\n' % (RSTFLE, PESTMODE))
    PCFId.write('%d %d %d %d %d\n' % (NPAR, NOBS, NPARGP, NPRIOR, NOBSGP))
    PCFId.write('%d %d %s %s %d %d %d\n' % (NTPLFLE, NINSFLE, PRECIS, DPOINT, NUMCOM, JACFILE, MESSFILE))
    PCFId.write('%f %f %f %f %d\n' % (RLAMBDA1, RLAMFAC, PHIRATSUF, PHIREDLAM, NUMLAM))
    PCFId.write('%f %f %f\n' % (RELPARMAX, FACPARMAX, FACORIG))
    PCFId.write('%f\n' % (PHIREDSWH))
    PCFId.write('%d %f %d %d %f %d\n' % (NOPTMAX, PHIREDSTP, NPHISTP, NPHINORED, RELPARSTP, NRELPAR))
    PCFId.write('%d %d %d\n' % (ICOV, ICOR, IEIG))
    #
    if SVDMODE == 1:
        PCFId.write('* singular value decomposition\n')
        PCFId.write('%d\n' % SVDMODE)
        PCFId.write('%d %10.5E\n' % (MAXSING, EIGTHRESH))
        PCFId.write('%d\n' % (EIGWRITE))
    #
    if LSQRMODE == 1:
        PCFId.write('* lsqr\n')
        PCFId.write('%d\n' % LSQRMODE)
        PCFId.write('%10.5E %10.5E %10.5E %d\n' % (LSQR_ATOL, LSQR_BTOL, LSQR_CONLIM, LSQR_ITNLIM))
        PCFId.write('%d\n' % (LSQRWRITE))
    # parameter groups
    PCFId.write('* parameter groups\n')
    INCTYP = 'relative'
    DERINC = 0.02
    FORCEN = 'switch'
    DERINCMUL = 1.2
    DERMTHD = 'best_fit'
    #
    for param in paramGroups:
        DERINCLB = paramDERINCLB[paramGroups.index(param)]
        paramGrp = param.upper()
        if paramGrp == 'DISP':
            DERINC = 0.40
        if paramGrp == 'DRAIN-FLOW':
            paramGrp = 'DRN-Q'
            DERINC = 0.20
        if paramGrp == 'DIV-FLOW':
            paramGrp = 'DIV-Q'  
            DERINC = 0.20
        if paramGrp == 'DRAIN-EC':
            paramGrp = 'DRN-EC'
            DERINC = 0.20
        PCFId.write('%s %s %4.3f %5.4f %s %3.1f %s\n' % \
                    (paramGrp,INCTYP,DERINC,DERINCLB,FORCEN,DERINCMUL,DERMTHD))
    #
    # parameter data
    PCFId.write('* parameter data\n')
    PARTRANS = 'NONE'
    PARCHGLIM = 'RELATIVE'
    SCALE = 1.0
    OFFSET = 0.0
    DERCOM = 1
    paramInfo=dict([])
    paramTieFix=dict([])
    for param in paramGroups:
        paramUp = param.upper()
        PARGP = paramUp
        if paramUp == 'MANN' or \
            paramUp == 'DISP' or \
            paramUp == 'LENGTH':
            # adjust channel parameters directly
            for chan in Channels.getChannels():
                chan3 = "%03d" % int(chan.getId())
                PARNME = paramUp + chan3
                if useTiedFixed:
                    try:
                        PARTRANS = TieFixChans[chan.getId()][0]
                        # save Tied channels for later section
                        if PARTRANS.upper() == 'TIED':
                            paramTieFix[PARNME] = TieFixChans[chan.getId()][1]
                    except:
                        print 'Could not find channel',chan.getId(),'in Tied/Fixed input'
                        raise
                if paramUp == 'MANN':
                    PARVAL1 = chan.getMannings()
                    PARLBND = PARVAL1/1.5
                    PARUBND = PARVAL1*1.5
                if paramUp == 'DISP':
                    PARVAL1 = chan.getDispersion()
                    PARLBND = PARVAL1/10.0
                    PARUBND = PARVAL1*10.0
                if paramUp == 'LENGTH':
                    PARVAL1 = chan.getLength()
                    PARLBND = PARVAL1 / 1.2   
                    PARUBND = PARVAL1 * 1.2
                PCFId.write('%s %s %s %12.4f %12.4f %12.4f %s %5.2f %5.2f %1d\n' % \
                (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later,
                # if not Tied or Fixed
                if PARTRANS.upper() == 'NONE':
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
            # write Tied section if needed
            if useTiedFixed:
                for key, value in sorted(paramTieFix.iteritems(), key=lambda (k,v): (v,k)):
                    chan3 = "%03d" % int(value)
                    tiedChanName = paramUp + chan3
                    PCFId.write('%s %s\n' % (key,tiedChanName))
            #
        #
        if paramUp == 'GATECF':
            # Adjust gate coefficients directly, similar to channel parameters.
            # Unfortunately gate data is not in the DSM2 Input methods by Nicky,
            # so it has to be read from the Hydro echo file.
            # Since DSM2 gates are split between pipes and weirs,
            # we'll follow the same pattern
            #
            headersList = gateWeirList[0]
            # find fields which combined will give a unique row name
            uniq1 = headersList.index('GATE_NAME')
            uniq2 = headersList.index('DEVICE')
            # find fields with the gate flow coeffs (CF_FROM_NODE and CF_TO_NODE)
            CF_FromLoc = headersList.index('CF_FROM_NODE')
            CF_ToLoc = headersList.index('CF_TO_NODE')
            # now write to PEST .pst file
            gateList = []
            for row in gateWeirList:
                try: PARVAL1 = float(row[CF_FromLoc])
                except: continue    # headers, just continue
                # skip zero-value gate coeffs
                if PARVAL1 != 0.0:
                    PARLBND = PARVAL1 * 0.5
                    PARUBND = PARVAL1 * 1.5
                    fullName = 'WEIRCFFR:' + row[uniq1] + ':' + row[uniq2]
                    # PEST requires the PARNME be less than or equal to 12 chars;
                    # pass through function to ensure that
                    PARNME = shortenName(gateList,fullName,12,'WEIRCFFR:')
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                        (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
                PARVAL1 = float(row[CF_ToLoc])
                if PARVAL1 != 0.0:
                    PARLBND = PARVAL1 * 0.5
                    PARUBND = PARVAL1 * 1.5
                    fullName = 'WEIRCFTO:' + row[uniq1] + ':' + row[uniq2]
                    PARNME = shortenName(gateList,fullName,12,'WEIRCFTO:')
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                        (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
            headersList = gatePipeList[0]
            uniq1 = headersList.index('GATE_NAME')
            uniq2 = headersList.index('DEVICE')
            CF_FromLoc = headersList.index('CF_FROM_NODE')
            CF_ToLoc = headersList.index('CF_TO_NODE')
            for row in gatePipeList:
                try: PARVAL1 = float(row[CF_FromLoc])
                except: continue    # headers, just continue
                if PARVAL1 != 0.0:
                    PARLBND = PARVAL1 * 0.5
                    PARUBND = PARVAL1 * 1.5
                    fullName = 'PIPECFFR:' + row[uniq1] + ':' + row[uniq2]
                    PARNME = shortenName(gateList,fullName,12,'PIPECFFR:')
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                        (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
                PARVAL1 = float(row[CF_ToLoc])
                if PARVAL1 != 0.0:
                    PARLBND = PARVAL1 * 0.5
                    PARUBND = PARVAL1 * 1.5
                    fullName = 'PIPECFTO:' + row[uniq1] + ':' + row[uniq2]
                    PARNME = shortenName(gateList,fullName,12,'PIPECFTO:')
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                        (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
#
        if paramUp == 'RESERCF':
            # adjust reservoir flow coefficients directly, similar to gate flow coeffs
            headersList = resCFList[0]
            # find fields which combined will give a unique row name
            uniq1 = headersList.index('RES_NAME')
            uniq2 = headersList.index('NODE')
            # find fields with the reservoir flow coeffs ( COEF_IN and COEF_OUT)
            CF_InLoc = headersList.index('COEF_IN')
            CF_OutLoc = headersList.index('COEF_OUT')
            # now write to PEST .pst file
            resList = []
            for row in resCFList:
                try: PARVAL1 = float(row[CF_InLoc])
                except: continue    # headers, just continue
                if PARVAL1 != 0.0:
                    PARLBND = PARVAL1 * 0.5
                    PARUBND = PARVAL1 * 1.5
                    fullName = 'RESCFIN:' + row[uniq1] + ':' + row[uniq2]
                    PARNME = shortenName(resList,fullName,12,'RESCFIN:')
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                        (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
                PARVAL1 = float(row[CF_OutLoc])
                if PARVAL1 != 0.0:
                    PARLBND = PARVAL1 * 0.5
                    PARUBND = PARVAL1 * 1.5
                    fullName = 'RESCFOUT:' + row[uniq1] + ':' + row[uniq2]
                    PARNME = shortenName(resList,fullName,12,'RESCFOUT:')
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                        (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
#
        if paramUp == 'ELEV' or paramUp == 'WIDTH':
            # cross-sections in channels;
            # instead of PEST adjusting the elevations or widths directly,
            # PEST will change coefficients for each cross-section which
            # will multiply the elevations or widths for each layer in a
            # pre-processor vscript before each DSM2 run. 
            PARVAL1 = 1.0
            PARLBND = 0.8  
            PARUBND = 1.2
            for chan in Channels.getChannels():
                chan3 = "%03d" % int(chan.getId())
                for xs in chan.getXsections():
                    xsdist3 = "%03d" % int(xs.getDistance()*1000.)
                    PARNME = paramUp + chan3 + ":" + xsdist3
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                    (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # create 'dummy' input/template files for x-sect calibration factors
#                    PIXFId.write('%s %10.4f\n' % (PARNME,random.uniform(PARLBND, PARUBND)))
                    PIXFId.write('%s %10.4f\n' % (PARNME,1.0))
                    PTXFId.write('%s %s\n' % (PARNME,'@' + PARNME + '  @'))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
        if paramUp == 'DIV-FLOW' or \
            paramUp == 'DRAIN-FLOW':
            # these calibration parameters, being timeseries, will be updated by a pre-processor
            # vscript before each DSM2 run. As with channel cross-sections, PEST will calibrate
            # 3 coefficients for each node.
            if paramUp == 'DIV-FLOW':
                stdev3Up = 2.0
                stdev3Lo = 3.0
            if paramUp == 'DRAIN-FLOW':
                stdev3Up = 3.0
                stdev3Lo = 3.0
            PARVAL1 = 1.0
            PARLBND = PARVAL1 / stdev3Lo
            PARUBND = PARVAL1 * stdev3Up
            for srcInput in srcAgInputsHydro:
                try: CPartUp = srcInput.path.upper().split('/')[3]
                except: continue
                if CPartUp == paramUp:
                    node3 = "%03d" % int(srcInput.nodeId)
                    if paramUp == 'DRAIN-FLOW':
                        shortName = 'DRN-Q'
                    else:
                        shortName = 'DIV-Q'  
                    PARGP = shortName  # shorten param group name to be less than 12 chars
                    PARNME = shortName+node3  # shorten param name to be less than 12 chars
                    PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                    (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                    # create 'dummy' input/template files for Ag calibration factors
#                    PIAFId.write('%s %10.4f\n' % (PARNME,random.uniform(PARLBND, PARUBND)))
                    PIAFId.write('%s %10.4f\n' % (PARNME,1.0))
                    PTAFId.write('%s %s\n' % (PARNME,'@' + PARNME + '     @'))
                    # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                    paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
        if paramUp == 'DRAIN-EC':
            # similar to Div/Drain flows, but have to use rows from the input table
            stdev3 = 5.0
            PARVAL1 = 1.0
            PARLBND = PARVAL1 / stdev3
            PARUBND = PARVAL1 * stdev3
            for row in tableNodeConc.getValues():
                path = row[5]
                if path.find('/') >= 0:
                    pathParts = path.split('/')
                    CPartUp = pathParts[3]
                    try: node3 = "%03d" % int(pathParts[2])
                    except: continue
                    if CPartUp == paramUp:
                        shortName = 'DRN-EC'
                        PARGP = shortName  # shorten param group name to be less than 12 chars
                        PARNME = shortName + node3
                        PCFId.write('%s %s %s %10.3f %10.3f %10.3f %s %5.2f %5.2f %1d\n' % \
                        (PARNME,PARTRANS,PARCHGLIM,PARVAL1,PARLBND,PARUBND,PARGP,SCALE,OFFSET,DERCOM))
                        # create 'dummy' input/template files for Ag calibration factors
#                        PIAFId.write('%s %10.4f\n' % (PARNME,random.uniform(PARLBND, PARUBND)))
                        PIAFId.write('%s %10.4f\n' % (PARNME,1.0))
                        PTAFId.write('%s %s\n' % (PARNME,'@' + PARNME + '  @'))
                        # save parameter name, initial value, and upper/lower bounds for Prior Information calcs later
                        paramInfo[PARNME] = [PARVAL1, PARLBND, PARUBND, PARGP]
    #
    if 'DIV-FLOW' in paramGroups or 'DRAIN-FLOW' in paramGroups or 'DRAIN-EC' in paramGroups:
        PIAFId.close()
        print 'Wrote', PIAFId.name
        PTAFId.close()
        print 'Wrote', PTAFId.name
    else:
        try: os.remove(PESTDir + PESTInpAgFile)
        except: pass
    if 'ELEV' in paramGroups or 'WIDTH' in paramGroups:
        PIXFId.close()
        print 'Wrote', PIXFId.name
        PTXFId.close()
        print 'Wrote', PTXFId.name
    else:
        try: os.remove(PESTDir + PESTInpXCFile)
        except: pass
    # Observed data groups
    PCFId.write('* observation groups\n')
    # Observed groups must also list prior information groups
    for obsGroup in obsGroups:
        PCFId.write('%s\n' % (obsGroup))
    for paramGroup in paramGroups:
        paramGrp = paramGroup.upper()
        if paramGrp == 'DRAIN-FLOW':
            paramGrp = 'DRN-Q'
        if paramGrp == 'DIV-FLOW':
            paramGrp = 'DIV-Q'  
        if paramGrp == 'DRAIN-EC':
            paramGrp = 'DRN-EC'
        PCFId.write('%s\n' % (paramGrp+'_PI'))
    #
    # append the temp observed data file previously created
    OTF1Id = open(ObsTempFile1,'r')
    PCFId.write('* observation data\n')
    PCFId.write(OTF1Id.read())
    OTF1Id.close()
    os.remove(ObsTempFile1)
    #
    # Model command line and I/O files
    PCFId.write('%s\n%s\n%s\n' % \
                ('* model command line', 'dsm2run.bat', '* model input/output'))
    if 'MANN' in paramGroups or \
        'DISP' in paramGroups or \
        'LENGTH' in paramGroups:
            PCFId.write('%s %s\n' % (PESTChanTplFile, ChanCalibFile))
    if 'GATECF' in paramGroups:
        PCFId.write('%s %s\n' % (PESTGateTplFile, GateCalibFile))
    if 'RESERCF' in paramGroups:
        PCFId.write('%s %s\n' % (PESTResTplFile, ResCalibFile))
    if 'DIV-FLOW' in paramGroups or 'DRAIN-FLOW' in paramGroups or 'DRAIN-EC' in paramGroups:
        PCFId.write('%s %s\n' % (PESTTplAgFile, PESTInpAgFile))
    if 'ELEV' in paramGroups or 'WIDTH' in paramGroups:
        PCFId.write('%s %s\n' % (PESTTplXCFile, PESTInpXCFile))
    PCFId.write('%s %s\n* prior information\n' % (PESTInsFile, DSM2OutFile))
    # write prior information
    # paramInfo[PARNME] = [value, lower, upper, group name]
    # make a sorted list of the parameter names so the .pst file is more readable
    paramList = []
    for p in paramInfo:
        paramList.append(p)
    paramList.sort()
    PIFAC = 1.0
    for PARNME in paramList:
        PILBL = PARNME + '_PI'
        PIVAL = paramInfo[PARNME][0]
        # for the Prior weight, assume the given lower/upper bounds of the parameters
        # are 3 Std Devs each from the mean.
        stDev = (paramInfo[PARNME][2]-paramInfo[PARNME][1])/6.
        WEIGHT = 1./stDev
        OBGNME = paramInfo[PARNME][3] + '_PI'
        PCFId.write('%s %5.2f * %s = %15.5E %15.5E %s\n' %\
                     (PILBL, PIFAC, PARNME, PIVAL, WEIGHT, OBGNME))
    PCFId.close()
    print 'Wrote',PCFId.name
    ##
    ## Create PEST Template Files (.tpl)
    if 'MANN' in paramGroups or \
        'DISP' in paramGroups or \
        'LENGTH' in paramGroups:
        # DSM2 channel input template
        PTFId = open(PESTDir + PESTChanTplFile,'w')
        DSM2InpId = open(CommonDir + ChanInpFile, 'r')
        PTFId.write('ptf @\n')
        # read each line from the DSM2 grid input file;
        # for channel lines, replace Length, Manning, and Dispersion
        # with PEST placeholder names
        channelLines = False
        for line in DSM2InpId:
            if re.search('^ *END',line,re.I):
                # end of section
                channelLines = False
            if channelLines:
                if re.search('^ *#', line): # comment line, continue
                    continue
                lineParts = line.split()
                # CHAN_NO  LENGTH  MANNING  DISPERSION  UPNODE  DOWNNODE
                chanNo = int(lineParts[0])
                chanLen = int(lineParts[1])
                chanMann = float(lineParts[2])
                chanDisp = float(lineParts[3])
                upNode = int(lineParts[4])
                downNode = int(lineParts[5])
                PTFId.write('%3d ' % (chanNo))
                if 'LENGTH' in paramGroups:
                    PTFId.write('@LENGTH%03d@  ' % (chanNo))
                else:
                    PTFId.write('%10d  ' % (chanLen))
                if 'MANN' in paramGroups:
                    PTFId.write('@MANN%03d     @  ' % (chanNo))
                else:
                    PTFId.write('%10.4f ' % (chanMann))
                if 'DISP' in paramGroups:
                    PTFId.write('@DISP%03d     @  ' % (chanNo))
                else:
                    PTFId.write('%10.4f ' % (chanDisp))
                PTFId.write('%5d %5d\n' % (upNode, downNode))
            else:
                PTFId.write(line)
            if re.search('CHAN_NO +LENGTH +MANNING +DISPERSION',line,re.I):
                # channel block header line, channel lines follow
                channelLines = True
        #            PTFId.write(line)
        PTFId.close()
        DSM2InpId.close()
        print 'Wrote',PTFId.name
    else:
        try: os.remove(PESTDir + ChanCalibFile)
        except: pass
    # DSM2 Gate input template
    if 'GATECF' in paramGroups:
        PTFId = open(PESTDir + PESTGateTplFile,'w')
        DSM2InpId = open(CommonDir + GateInpFile, 'r')
        PTFId.write('ptf |\n')
        # read each line from the DSM2 gate input file;
        # for gate weir & pipe device lines, replace To and From flow coefficients 
        # with PEST placeholder names
        gateLines = False
        for line in DSM2InpId:
            if re.search('^ *GATE_[A-Z]+_DEVICE *$',line,re.I):
                PTFId.write(line)
                # Pipe or Weir section?   
                if re.search('GATE_PIPE_DEVICE', line, re.I):
                    headersList = gatePipeList[0]
                    name = 'PIPE'
                if re.search('GATE_WEIR_DEVICE',line,re.I):
                    headersList = gateWeirList[0]
                    name = 'WEIR'
            if re.search('^ *END',line,re.I):
                # end of section
                if gateLines:
                    PTFId.write(line)
                    gateLines = False
            if gateLines:
                lineParts = line.split()
                # GATE_NAME DEVICE NDUPLICATE (RADIUS|HEIGHT) ELEV CF_FROM_NODE CF_TO_NODE DEFAULT_OP
                gateName = lineParts[headersList.index('GATE_NAME')]
                devName = lineParts[headersList.index('DEVICE')]
                # accept only non-zero coeffs
                if float(lineParts[CF_FromLoc]) != 0.0:
                    # recreate shortened gate name
                    fullName = name+'CFFR:'+gateName+':'+devName
                    shortName = shortenName(gateList,fullName,12,name+'CFFR:')
                    lineParts[CF_FromLoc] = '|' + shortName + '      |' 
                # accept only non-zero coeffs
                if float(lineParts[CF_ToLoc]) != 0.0:
                    fullName = name+'CFTO:'+gateName+':'+devName
                    shortName = shortenName(gateList,fullName,12,name+'CFTO:')
                    lineParts[CF_ToLoc] = '|' + shortName + '      |'
                #
                for i in range(len(lineParts)): 
                    PTFId.write('%s ' % lineParts[i])
                PTFId.write('\n')
            if re.search('GATE_NAME +.*CF_(FROM|TO)_NODE +.*CF_(FROM|TO)_NODE',line,re.I):
                # gate block header line, gate lines follow
                gateLines = True
                PTFId.write(line)
                # find which fields have the gate flow coeffs (CF_FROM_NODE and CF_TO_NODE)
                CF_FromLoc = headersList.index('CF_FROM_NODE')
                CF_ToLoc = headersList.index('CF_TO_NODE')
        PTFId.close()
        DSM2InpId.close()
        print 'Wrote',PTFId.name
    else:
        try: os.remove(PESTDir + PESTGateInpFile)
        except: pass
    if 'RESERCF' in paramGroups:
        # DSM2 Reservoir input template
        PTFId = open(PESTDir + PESTResTplFile,'w')
        DSM2InpId = open(CommonDir + ResInpFile, 'r')
        PTFId.write('ptf |\n')
        # read each line from the DSM2 reservoir input file;
        # for reservoir coefficient lines, replace In and Out flow coefficients 
        # with PEST placeholder names
        resCFLines = False
        for line in DSM2InpId:
            if re.search('^ *RESERVOIR_CONNECTION *$',line,re.I):
                PTFId.write(line)
            if re.search('^ *END',line,re.I):
                if resCFLines:
                    # end of reservoir coefficient lines
                    PTFId.write(line)
                    resCFLines = False
            if resCFLines:
                lineParts = line.split()
                # RES_NAME NODE COEF_IN COEF_OUT
                resName = lineParts[headersList.index('RES_NAME')]
                resNode = lineParts[headersList.index('NODE')]
                # accept only non-zero coeffs
                if float(lineParts[CF_InLoc]) != 0.0:
                    shortName = shortenName(resList,'RESCFIN:' + resName + ':' + resNode,12,'RESCFIN:')
                    lineParts[CF_InLoc] = '|' + shortName + '     |' 
                if float(lineParts[CF_OutLoc]) != 0.0:
                    shortName = shortenName(resList,'RESCFOUT:' + resName + ':' + resNode,12,'RESCFOUT:')
                    lineParts[CF_OutLoc] = '|' + shortName + '     |'
                for i in range(len(lineParts)): 
                    PTFId.write('%s ' % lineParts[i])
                PTFId.write('\n')
            if re.search('RES_NAME +.*COEF_IN',line,re.I):
                # reservoir coefficient block header line, reservoir lines follow
                resCFLines = True
                PTFId.write(line)
                headersList = resCFList[0]
                # find which fields have the reservoir flow coeffs (COEF_IN and COEF_OUT)
                CF_InLoc = headersList.index('COEF_IN')
                CF_OutLoc = headersList.index('COEF_OUT')
        PTFId.close()
        DSM2InpId.close()
        print 'Wrote',PTFId.name
    else:
        try: os.remove(PESTDir + PESTResInpFile)
        except: pass
    ##
    # Create the PEST_post_DSM2Run.py file for post-processing DSM2 calibration runs.
    # The post-processing generates text output of the DSS calibration stations,
    # and the PEST instruction (.ins) file. 
    preProcFile = 'PEST_pre_DSM2Run.py'
    postProcFile = 'PEST_post_DSM2Run.py'
    sq = "'"
    dq = '"'
    dataTypesStr =  dq + '", "'.join(dataTypes) + dq
    WDSM2Id = open(PESTDir + postProcFile, 'w')
    WDSM2Id.write('import sys, os\n')
    WDSM2Id.write('from vtimeseries import *\n')
    WDSM2Id.write('from vdss import *\n')
    WDSM2Id.write('from vista.set import *\n')
    WDSM2Id.write('#from vista.db.dss import *\n')
    WDSM2Id.write('from vutils import *\n')
    WDSM2Id.write('from vista.time import TimeFactory\n')
    WDSM2Id.write('def writeText(filename,ds,outputFlags=False):\n')
    WDSM2Id.write('    """\n')
    WDSM2Id.write('    writeText(filename,ds,outputFlags=False)\n')
    WDSM2Id.write('    Writes the given data set to the given filename,\n')
    WDSM2Id.write('    using full precision available in the DSS file.\n')
    WDSM2Id.write('    """\n')
    WDSM2Id.write('    fid = open(filename,"w")\n')
    WDSM2Id.write('    fid.write(SetUtils.getHeader(ds).toString().replace("\\r",""))\n')
    WDSM2Id.write('    for i in range(len(ds)):\n')
    WDSM2Id.write('        dse = ds.getElementAt(i)\n')
    WDSM2Id.write('        fid.write("%s %15.10E\\n" % (dse.getXString(), dse.getY()))\n')
    WDSM2Id.write('    fid.close()\n')
    WDSM2Id.write('    return\n')
    WDSM2Id.write('#\n')
    WDSM2Id.write('if __name__ == "__main__":\n')
    WDSM2Id.write("    TF = TimeFactory.getInstance()\n")
    WDSM2Id.write("    twList = []\n")
    if useB1:
        WDSM2Id.write("    twList.append(TF.createTimeWindow('" + B1CmpStartDateObj.format() + " - " + \
                      B1CmpEndDateObj.format() + "'))\n")
    if useB2:
        WDSM2Id.write("    twList.append(TF.createTimeWindow('" + B2CmpStartDateObj.format() + " - " + \
                      B2CmpEndDateObj.format() + "'))\n")
    WDSM2Id.write("    # This post-processor was generated by PEST_Create_Files.py\n" + \
                  "    # It translates DSM2 DSS output for calibration to a text file,\n" + \
                  "    # then generates the matching PEST instruction file for the output.\n" + \
                  "    # On initial PEST start, run this using the base-1 dss output.\n")
    WDSM2Id.write("    CalibDSSOutFile = sys.argv[1]\n")
    WDSM2Id.write("    tempfile = 'temp.out'\n")
    WDSM2Id.write("    try: fid = open(" + sq + DSM2OutFile + sq + ", 'w')\n")
    WDSM2Id.write("    except:\n")
    WDSM2Id.write("        print 'Error opening " + DSM2OutFile + "'\n")
    WDSM2Id.write("        raise\n")
    WDSM2Id.write("    for tw in twList:\n")
    WDSM2Id.write("        for dataType in [" + dataTypesStr + "]:\n")
    WDSM2Id.write("            dssgrp = opendss(CalibDSSOutFile)\n")
    WDSM2Id.write("            dssgrp.filterBy('/'+dataType+'/')\n")
    WDSM2Id.write("            for dssdr in dssgrp.getAllDataReferences():\n")
    WDSM2Id.write("                try:\n")
    WDSM2Id.write("                    dssdr = DataReference.create(dssdr,tw)\n")
    WDSM2Id.write("                    writeText(tempfile, dssdr.getData())\n")
    WDSM2Id.write("                except:\n")
    WDSM2Id.write("                    print 'Error with ',dssdr\n")
    WDSM2Id.write("                    raise\n")
    WDSM2Id.write("                tid = open(tempfile, 'r')\n")
    WDSM2Id.write("                fid.write(tid.read().replace('\t','    '))\n")
    WDSM2Id.write("                tid.close()\n")
    WDSM2Id.write("    #\n")
    WDSM2Id.write("    fid.close()\n")
    WDSM2Id.write("    print 'Wrote', fid.name\n")
    WDSM2Id.write("    if os.path.exists(tempfile):\n")
    WDSM2Id.write("       os.remove(tempfile)\n")
    WDSM2Id.write("    # generate PEST instruction (.ins) file\n")
    WDSM2Id.write("    fid = open('" + PESTInsFile + "', 'w')\n")
    WDSM2Id.write("    tid = open('" + DSM2OutFile + "', 'r')\n")
    WDSM2Id.write("    fid.write('pif @\\n')\n")
    WDSM2Id.write("    for line in tid:\n")
    WDSM2Id.write("        if re.search('^$', line):\n")
    WDSM2Id.write("            fid.write('@Units :@\\n')\n")
    WDSM2Id.write("            continue\n")
    WDSM2Id.write("        lineSplit = line.split()\n")
    WDSM2Id.write("        if line.find('Location: ') > -1:\n")
    WDSM2Id.write("            locStr = lineSplit[1].upper()\n")
    WDSM2Id.write("            continue\n")
    WDSM2Id.write("        if line.find('Type: ') > -1:\n")
    WDSM2Id.write("            typeStr = lineSplit[1].upper()\n")
    WDSM2Id.write("            typeStr = typeStr.replace('STAGE','STG').replace('FLOW','Q')\n")
    WDSM2Id.write("            continue\n")
    WDSM2Id.write("        if re.search('^[0-9][0-9][A-Z][A-Z][A-Z][12][90][78901][0-9] [0-2][0-9][0-9][0-9][ \t]+[0-9eE.+-]+$',line) > -1:\n")
    WDSM2Id.write("            timeObj = TF.createTime(lineSplit[0]+' '+lineSplit[1])\n")
    WDSM2Id.write("            dateStr = timeObj.format(DefaultTimeFormat('yyyyMMdd'))\n")
    WDSM2Id.write("            timeStr = timeObj.format(DefaultTimeFormat('HHmm'))\n")
    WDSM2Id.write("            dataID = 'L1 (' + locStr + typeStr + dateStr + timeStr + ')16:80'\n")
    WDSM2Id.write("            fid.write(dataID + '\\n')\n")
    WDSM2Id.write("    fid.close()\n")
    WDSM2Id.write("    tid.close()\n")
    WDSM2Id.write("    print 'Wrote', fid.name\n")
    WDSM2Id.write("    sys.exit(0)\n")
    print 'Wrote', WDSM2Id.name
    WDSM2Id.close()
    #
    ## Create the run-time *.bat files for the Condor runs of PEST;
    ## this varies somewhat depending on type of PEST parallelization
    # Create batch file for Hydro and Qual runs under PEST
    WCONId = open(PESTDir + 'dsm2run.bat', 'w')
    WCONId.write("@echo off\n")
    WCONId.write("set VISTABINDIR=c:\\condor\\vista\\bin\\\n")
    WCONId.write("set PESTBINDIR=c:\\condor\\PEST\\bin\\\n")
    WCONId.write("setlocal\n")
    WCONId.write("for /f \"tokens=2 delims=[]\" %%f in ('ping -4 -n 1 %COMPUTERNAME% " +
                 "^| c:\Windows\system32\\find.exe /i \"pinging\"') do set IP=%%f\n")
    WCONId.write("echo Running on %COMPUTERNAME% (%IP%)\n")
    WCONId.write("echo  in directory %CD%\n")
    WCONId.write("@copy /b/y " + DivRtnQFile + " " + DivRtnQFile.replace(".dss","") + "-calib.dss\n")
    WCONId.write("@copy /b/y " + RtnECFile + " " + RtnECFile.replace(".dss","") + "-calib.dss\n")
    WCONId.write("echo Running Pre-Processor\n")
    WCONId.write("call %VISTABINDIR%vscript.bat " + preProcFile + "\n")
    WCONId.write("if %ERRORLEVEL% GTR 0 (\n")
    WCONId.write("echo Pre-Process ERRORLEVEL %ERRORLEVEL%\n")
    WCONId.write("exit /b 1\n")
    WCONId.write(")\n")
    if useB1:
        WCONId.write("if exist " + CalibDSSOutFile + " del /f/q " + CalibDSSOutFile + "\n")
        WCONId.write("set START_DATE=" + B1StartDateObj_Hydro.format().split()[0] + "\n")
        WCONId.write("set START_TIME=" + B1StartDateObj_Hydro.format().split()[1] + "\n")
        WCONId.write("set END_DATE=" + B1EndDateObj_Hydro.format().split()[0] + "\n")
        WCONId.write("set END_TIME=" + B1EndDateObj_Hydro.format().split()[1] + "\n")
        WCONId.write("set QUAL_START_DATE=" + B1StartDateObj_Qual.format().split()[0] + "\n")
        WCONId.write("set QUAL_END_DATE=" + B1EndDateObj_Qual.format().split()[0] + "\n")
        WCONId.write("set HYDRORSTFILE_IN=" + DSM2Mod + "-" + DSM2Run + "-B1.hrf\n")
        WCONId.write("set QUALRSTFILE_IN=" + DSM2Mod + "-" + DSM2Run + "-B1.qrf\n")
    if useB2:
        WCONId.write("if exist " + CalibDSSOutFile + " del /f/q " + CalibDSSOutFile + "\n")
        WCONId.write("set START_DATE=" + B2StartDateObj_Hydro.format().split()[0] + "\n")
        WCONId.write("set START_TIME=" + B2StartDateObj_Hydro.format().split()[1] + "\n")
        WCONId.write("set END_DATE=" + B2EndDateObj_Hydro.format().split()[0] + "\n")
        WCONId.write("set END_TIME=" + B2EndDateObj_Hydro.format().split()[1] + "\n")
        WCONId.write("set QUAL_START_DATE=" + B2StartDateObj_Qual.format().split()[0] + "\n")
        WCONId.write("set QUAL_END_DATE=" + B2EndDateObj_Qual.format().split()[0] + "\n")
        WCONId.write("set HYDRORSTFILE_IN=" + DSM2Mod + "-" + DSM2Run + "-B2.hrf\n")
        WCONId.write("set QUALRSTFILE_IN=" + DSM2Mod + "-" + DSM2Run + "-B2.qrf\n")
    WCONId.write("call :RUNDSM2\n")
    WCONId.write("set ERRLVL=%ERRORLEVEL%\n")
    WCONId.write("if %ERRLVL% GTR 0 (\n")
    WCONId.write("echo DSM2 ERRORLEVEL %ERRLVL%\n")
    WCONId.write("if exist " + ChanCalibFile + " xcopy /y/c/q " + ChanCalibFile + \
                 " " + bs + bs + "bdomo-002\\condor" + bs + "returnedFiles\n")
    WCONId.write("if exist " + PESTInpAgFile + " xcopy /y/c/q " + PESTInpAgFile + \
                 " " + bs + bs + "bdomo-002\\condor" + bs + "returnedFiles\n")
    WCONId.write("if %ERRLVL% EQU 97 GOTO FIN\n")
    WCONId.write("exit /b 1\n")
    WCONId.write(")\n")
    WCONId.write("rem post-process to prepare output for PEST\n\n")
    WCONId.write("echo Running Post-Processor\n")
    WCONId.write("call %VISTABINDIR%vscript.bat " + postProcFile + \
                  " " + CalibDSSOutFile + "\n")
    WCONId.write("if %ERRORLEVEL% GTR 0 (\n")
    WCONId.write("echo Post-Process ERRORLEVEL %ERRORLEVEL%\n")
    WCONId.write("exit /b 1\n")
    WCONId.write(")\n")
    WCONId.write(":FIN\n")
    WCONId.write("rem Idiotic MS equivalent of touch\n")
    WCONId.write("copy /b dummy.txt +,,\n")
#    WCONId.write("call %PESTBINDIR%pestchek.exe DSM2\n")
#    WCONId.write("if ERRORLEVEL 1 exit /b 1\n")
    WCONId.write("exit /b 0\n")
    WCONId.write("\n")
    WCONId.write(":RUNDSM2\n")
    WCONId.write("echo Starting DSM2 on %START_DATE%\n")
    WCONId.write("ping -n 2 127.0.0.1 > nul\n")
    WCONId.write("time /t\n")
    WCONId.write("hydro.exe hydro.inp\n")
    WCONId.write("if %errorlevel% GTR 0 (\n")
    WCONId.write("echo Hydro ERRORLEVEL %ERRORLEVEL%\n")
    WCONId.write("echo Error in Hydro run.\n")
    WCONId.write("xcopy /y /c /q hydro_echo_*.inp " + bs + bs + \
                 "bdomo-002\\condor" + bs + "returnedFiles\n")
    if contDSM2:
        WCONId.write("copy /y/q " + DSM2OutFile + ".base " + DSM2OutFile + "\n")
        WCONId.write("echo Returning base output file to PEST.\n")
        WCONId.write("exit /b 97\n")
    else:
        WCONId.write("exit /b 1\n")
    WCONId.write(")\n")
    WCONId.write("echo Hydro run OK\n")
    WCONId.write("ping -n 2 127.0.0.1 > nul\n")
    WCONId.write("time /t\n")
    WCONId.write("qual.exe qual_ec.inp\n")
    WCONId.write("if %errorlevel% GTR 0 (\n")
    WCONId.write("echo Qual ERRORLEVEL %ERRORLEVEL%\n")
    WCONId.write("echo Error in Qual run.\n")
    WCONId.write("xcopy /y/c/q qual_ec_echo_*.inp \\bdomo-002\condor" + \
                 bs + "returnedFiles\n")
    if contDSM2:
        WCONId.write("copy /y/q " + DSM2OutFile + ".base " + DSM2OutFile + "\n")
        WCONId.write("echo Returning base output file to PEST.\n")
        WCONId.write("exit /b 97\n")
    else:
        WCONId.write("exit /b 1\n")
    WCONId.write(")\n")
    WCONId.write("echo Qual run OK\n")
    WCONId.write("time /t\n")
    WCONId.write("ping -n 2 127.0.0.1 > nul\n")
    WCONId.write("exit /b 0\n")    
    WCONId.close()
    print 'Wrote', WCONId.name
    # Create the main .bat file to start a parallel PEST calibration.
    # This file is run once by the user for each calibration run; it copies necessary files
    # and starts the HTCondor submit jobs
    WDSM2Id = open(PESTDir + fileRun4, 'w')
    WDSM2Id.write("@echo off\n")
    WDSM2Id.write("Rem This file created by PEST_Create_Files.py\n")
    WDSM2Id.write("Rem Calibrate DSM2 using " + typePEST.upper() + " & HTCondor.\n")
    WDSM2Id.write("setlocal\n")
    WDSM2Id.write("set HYDROEXE=" + RootDir.replace("/","\\") + "hydro.exe\n")
    WDSM2Id.write("set QUALEXE=" + RootDir.replace("/","\\") + "qual.exe\n")
    WDSM2Id.write("set PESTDIR=" + PESTDir.replace("/","\\") + "\n")
    WDSM2Id.write("set CONDORBINDIR=c:\\condor\\bin\\\n")
    WDSM2Id.write("set VISTABINDIR=c:\\condor\\vista\\bin\\\n")
    WDSM2Id.write("set PESTBINDIR=c:\\condor\\PEST\\bin\\\n")
    WDSM2Id.write("set STUDYNAME=" + PESTFile.replace(".pst","") + "\n")
    if typePEST == 'genie':
        WDSM2Id.write("rem get this machine's IP number\n")
        WDSM2Id.write("for /f "+dq+"delims=[] tokens=2"+dq+ \
                      " %%a in ('ping -4 %computername% -n 1 ^| findstr "+dq+"["+dq+"') do (set MYIP=%%a)\n")
    WDSM2Id.write("\n")
    cmnFiles = glob.glob(CommonDir + '*.inp')
    WDSM2Id.write("set CMNFILES=")
    for f in cmnFiles:
        fn = os.path.basename(f)
        WDSM2Id.write(fn + ", ")
    WDSM2Id.write("\n")    
    WDSM2Id.write("set TSFILES=" + DivRtnQFile + ", " + RtnECFile + ", " + \
        "events.dss, gates-v8-06212012.dss, hist_19902012.dss\n")
    writeStr = "set PESTFILES=" + PESTFile + ", " + PESTInsFile
    if 'MANN' in paramGroups or \
        'DISP' in paramGroups or \
        'LENGTH' in paramGroups:
        writeStr += ", " + PESTChanTplFile
    if 'GATECF' in paramGroups:
        writeStr += ", " + PESTGateTplFile
    if 'RESERCF' in paramGroups:
        writeStr += ", " + PESTResTplFile
    if 'DIV-FLOW' in paramGroups or 'DRAIN-FLOW' in paramGroups or 'DRAIN-EC' in paramGroups:
        writeStr += ", " + PESTInpAgFile + ", " + PESTTplAgFile
    if 'ELEV' in paramGroups or 'WIDTH' in paramGroups:
        writeStr += ", " + PESTInpXCFile + ", " + PESTTplXCFile
    WDSM2Id.write(writeStr + "\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("rem create a scratch directory for condor runs\n")
    WDSM2Id.write("set RUNDIR=%PESTDIR%Condor\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("if not exist %RUNDIR% mkdir %RUNDIR%\n")
    WDSM2Id.write("del /q %RUNDIR%\n")
    WDSM2Id.write("if %ERRORLEVEL% GTR 0 (\n")
    WDSM2Id.write("echo Cannot delete all files in Condor dir.\n")
    WDSM2Id.write("exit /b 1\n")
    WDSM2Id.write(")\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("@copy /b %HYDROEXE% %RUNDIR%\\\n")
    WDSM2Id.write("@copy /b %QUALEXE% %RUNDIR%\\\n")
    WDSM2Id.write("@copy /a dsm2run.bat %RUNDIR%\\\n")
    WDSM2Id.write("@xcopy /q " + re.sub("/$","",TimeSeriesDir).replace("/",bs) + " %RUNDIR%\\\n")
    WDSM2Id.write("@xcopy /q " + re.sub("/$", "", CommonDir).replace("/",bs) + " %RUNDIR%\\\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("@copy /y " + preProcFile + " %RUNDIR%\\\n")
    WDSM2Id.write("@copy /y " + postProcFile + " %RUNDIR%\\\n")
    WDSM2Id.write("@copy /y *.rmf %RUNDIR%\\\n")
    WDSM2Id.write("@copy /y *.pst %RUNDIR%\\\n")
    WDSM2Id.write("@copy /y *.tpl %RUNDIR%\\\n")
    WDSM2Id.write("@copy /y *.inp %RUNDIR%\\\n")
    WDSM2Id.write("@copy /y dummy.txt %RUNDIR%\\\n")

    WDSM2Id.write("@copy /y " + fileSub + " %RUNDIR%\\dsm2.sub\n")
    WDSM2Id.write("@copy /y " + BaseRunDir.replace("/","\\") + "*"+DSM2Run+"-B?.?rf %RUNDIR%\\\n")
    WDSM2Id.write("@copy /b/y " + BaseRunDir.replace("/","\\") + CalibDSSOutFile + " %RUNDIR%\\\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("cd %RUNDIR%\n")
    WDSM2Id.write("@ren config_calib.inp config.inp\n")
    if useRestart:
        WDSM2Id.write("@ren hydro_calib_restart.inp hydro.inp\n")
        WDSM2Id.write("@ren qual_ec_calib_restart.inp qual_ec.inp\n")
    else:
        WDSM2Id.write("@ren hydro_calib_cold.inp hydro.inp\n")
        WDSM2Id.write("@ren qual_ec_calib_cold.inp qual_ec.inp\n")
    WDSM2Id.write("Rem add calibration-specific input files\n")
    WDSM2Id.write("echo GRID >> hydro.inp\n")
    if 'MANN' in paramGroups or \
        'DISP' in paramGroups or \
        'LENGTH' in paramGroups:
        WDSM2Id.write("echo " + ChanCalibFile + " >> hydro.inp\n")
        WDSM2Id.write("@copy " + ChanInpFile + " " + ChanCalibFile + "\n")
    if 'ELEV' in paramGroups or 'WIDTH' in paramGroups:
        WDSM2Id.write("echo " + XSectsCalibFile + " >> hydro.inp\n")
#        WDSM2Id.write("@ren " + XSectsInpFile + " " + XSectsCalibFile + "\n")
    if 'GATECF' in paramGroups:
        WDSM2Id.write("echo " + GateCalibFile + " >> hydro.inp\n")
        WDSM2Id.write("@copy " + GateInpFile + " " +GateCalibFile + "\n")
    if 'RESERCF' in paramGroups:
        WDSM2Id.write("echo " + ResCalibFile + " >> hydro.inp\n")
        WDSM2Id.write("@copy " + ResInpFile + " " + ResCalibFile + "\n")
    WDSM2Id.write("echo END >> hydro.inp\n")
    WDSM2Id.write("rem Add calibration output to Hydro and Qual .inp files\n")
    WDSM2Id.write("echo OUTPUT_TIME_SERIES >> hydro.inp\n")
    WDSM2Id.write("echo " + DSM2DSSOutHydroFile + " >> hydro.inp\n")
    WDSM2Id.write("echo END >> hydro.inp\n")
    WDSM2Id.write("echo OUTPUT_TIME_SERIES >> qual_ec.inp\n")
    WDSM2Id.write("echo " + DSM2DSSOutQualFile + " >> qual_ec.inp\n")
    WDSM2Id.write("echo END >> qual_ec.inp\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("rem finish Condor submit file for PEST\n")
    # minimum disk size is about 50MB + 500MB per year for DSM2 + PEST
    # Note: 476 MB is the highest usage I've seen for 1 year.
    #nYearsRun = runTSWin ??
    if useRestart:
        nYearsRun = 1.
    else:
        nYearsRun = 2.
    diskSize = 50 + 500 * nYearsRun 
    WDSM2Id.write("echo request_disk = " + str(int(diskSize+0.5)) + " MB >> %RUNDIR%\\dsm2.sub\n")
    writeStr = "echo transfer_input_files = dsm2run.bat, " + \
                  preProcFile + ", " + postProcFile + ", " + DSM2OutFile + ".base, " + \
                  DSM2DSSOutHydroFile + ", " + DSM2DSSOutQualFile + ", %PESTFILES%, " + \
                  "hydro.exe, qual.exe, hydro.inp, qual_ec.inp, " \
                  + DSM2Mod + "-" + DSM2Run + "-B1.hrf, " + DSM2Mod + "-" + DSM2Run + "-B2.hrf, " \
                  + DSM2Mod + "-" + DSM2Run + "-B1.qrf, " + DSM2Mod + "-" + DSM2Run + "-B2.qrf, " \
                  + " config.inp, dummy.txt"
    if 'MANN' in paramGroups or \
        'DISP' in paramGroups or \
        'LENGTH' in paramGroups:
        writeStr += ", " + ChanCalibFile
    if 'GATECF' in paramGroups:
        writeStr += ", " + GateCalibFile
    if 'RESERCF' in paramGroups:
        writeStr += ", " + ResCalibFile
    writeStr += ", %CMNFILES% %TSFILES% >> %RUNDIR%\\dsm2.sub\n"
    WDSM2Id.write(writeStr)
    WDSM2Id.write("\n")
    # command and arguments depend on which PEST is to be used
    if typePEST == 'genie':
        WDSM2Id.write("echo executable = %PESTBINDIR%GSLAVE64.exe >> %RUNDIR%\\dsm2.sub\n")
        WDSM2Id.write("echo arguments = /host %MYIP%:4004 /interval 1.0 /console off " + \
                      "/name slave-$(Cluster)-$(Process) >> %RUNDIR%\dsm2.sub\n")
    elif typePEST == 'beo':
        WDSM2Id.write("echo executable = %PESTBINDIR%BEOPEST64.exe >> %RUNDIR%\\dsm2.sub\n")
        WDSM2Id.write("echo arguments = %STUDYNAME% /H %COMPUTERNAME%:4004 >> %RUNDIR%\\dsm2.sub\n")
    WDSM2Id.write("echo queue " + str(NCondor) + " >> %RUNDIR%\\dsm2.sub\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("rem do a base run to make sure it works\n")
    WDSM2Id.write("call dsm2run.bat\n")
    WDSM2Id.write("if ERRORLEVEL 1 exit /b 1\n")
    WDSM2Id.write("rem use the base run output as a substitute for a dsm2 run failure\n")
    WDSM2Id.write("@copy /b /y " + DSM2OutFile + " " + DSM2OutFile + ".base\n")
    WDSM2Id.write("\n")
    WDSM2Id.write("%CONDORBINDIR%condor_submit dsm2.sub\n")
    WDSM2Id.write("if ERRORLEVEL 1 exit /b 1\n")
    WDSM2Id.write("echo Submitted " + typePEST + " slave jobs to condor.\n")
    WDSM2Id.write("REM delay a few seconds before running the main PEST programs\n")
    WDSM2Id.write("ping -n 5 127.0.0.1 > nul\n")
    if typePEST == 'genie':
        WDSM2Id.write("popd\n")
        WDSM2Id.write("start /min cmd /c %PESTBINDIR%GMAN64.exe /port 4004 /ip %MYIP% ^> gman.out\n")
        WDSM2Id.write("ping -n 5 127.0.0.1 > nul\n")
        WDSM2Id.write("start /min cmd /c %PESTBINDIR%GPEST64.exe %STUDYNAME% /H %MYIP%:4004 ^> gpest.out\n")
    elif typePEST == 'beo':
        #WDSM2Id.write("start %PESTBINDIR%BEOPEST64.exe %STUDYNAME% /H :4004 ^> beo.out\n")
        WDSM2Id.write("%PESTBINDIR%BEOPEST64.exe %STUDYNAME% /H :4004\n")
    #
    WDSM2Id.close()
    print 'Wrote', WDSM2Id.name
    print 'End processing all files', datetime.today().strftime("%Y-%b-%d %H:%M:%S")
    sys.exit(0)
#
