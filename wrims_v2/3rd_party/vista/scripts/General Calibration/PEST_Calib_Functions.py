# Various calibration functions for the PEST DSM2 calibration
# pre- and post-processors written in vscript.
import re, os, glob, datetime, random, math
from vtimeseries import *
from vdss import *
from vutils import *
from vista.time import TimeFactory, TimeFormat, DefaultTimeFormat
from gov.ca.dsm2.input.parser import Parser
from gov.ca.dsm2.input.parser import Tables
from gov.ca.dsm2.input.model import *
#
def obsDataBParts(str):
    return str.upper().split('/')[2]
def obsDataCParts(str):
    return str.upper().split('/')[3]
def parseInpSects(DSM2InpFile,Section):
    '''
    Parse the given DSM2 Input file for Section,
    return as a list of lists (the rows/columns)
    '''
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
    '''
    Shorten the long name longName using the following rules:
    * If longName is less than or equal to maxChars, just
      return longName
    * Else, create a short name using optional Prefix, or nothing if empty or False,
      and a counting integer. Try the resulting short name in nameList. If it doesn't
      exist yet, store it in the dictionary and return the short name as the value.
      If it exists, increment the counter and try again until a unique short name is
      produced.
    '''
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
def setGroups(file):
    """
    Read from input text file name the tied, fixed, and none (estimable) channel
    or node information. File format is:
    ParameterGroupName ObjectNumber TIED_FIXED TIED_TO  
    MANN 248 TIED 249  
    MANN 249 NONE 0  
    MANN 250 TIED 249  
    MANN 251 TIED 119  
    MANN 252 FIXED 0  
    MANN 253 FIXED 0
         ...
    The first line is a header line and ignored. 
    Subsequent lines are parameter group name; channel or node number; 
    'TIED', 'FIXED', or 'NONE';
    and if TIED, a second channel number that this parameter is tied to.
    A 2nd channel number for FIXED and NONE channels is ignored.
    Individual parameter names are constructed by ParameterGroupName+ObjectNumber.
    A dictionary of the input is returned.
    """
    try: fid = open(file,'r')
    except:
        print 'Cannot open tie/fix file',file
        raise
    lines = fid.readlines()
    fid.close()
    GroupDict = dict()
    for line in lines:
        lineList = line.rstrip().rsplit()
        try: obj3 = "%03d" % int(lineList[1])
        except: continue
        GroupDict[lineList[0]+' '+obj3] = lineList[2:]
    return GroupDict
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
def makeAgDict(PESTInpAgFile):
    '''
    Create a Ag coefficient dictionary by reading the Ag calibration coefficients
    from the PEST file, each line's format:
    'DIV-FLOW|DRAIN-FLOW|DRAIN-EC<3-digit-node-number>' <whitespace> CoeffValue
    '''
    if os.path.exists(PESTInpAgFile):
        FID = open(PESTInpAgFile,'r')
        coeffDict = []
        for line in FID:
            nodeAgGrp = line.split()[0]
            Coeff = line.split()[1]
            # node number is last 3 chars
            node = int(nodeAgGrp[len(nodeAgGrp)-3:])
            # Ag group name is first several chars
            agGrp = nodeAgGrp[0:len(nodeAgGrp)-3]
            coeffDict.append([str(node) + agGrp,float(Coeff)])
        FID.close()
        coeffDict = dict(coeffDict)
        return coeffDict
    else:
        return None
#
def makeXCDict(PESTInpXCFile):
    '''
    Channel cross-section widths and elevations
    read the x-sect calibration coefficients, each line's format:
    'WIDTH|ELEV<3-digit-chanIn-number>' <whitespace> CoeffValue
    '''
    if os.path.exists(PESTInpXCFile):
        PIXFID = open(PESTInpXCFile,'r')
        coeffDict = []
        for line in PIXFID:
            # Parameter name, channel number
            PCF = line.split()[0]
            Coeff = line.split()[1]
            # chanIn number always 3 chars
            chanIn = int(PCF[len(PCF)-3:])
            # param name is first several chars
            param = PCF[0:len(PCF)-3]
            coeffDict.append([PCF,float(Coeff)])
        PIXFID.close()
        coeffDict = dict(coeffDict)
        return coeffDict
    else:
        return None
    
def updateDSSAgVals(DSSFile, coeffDict):
    '''
    Given a DSS file of DICU values, update with new values
    by multiplying existing values with a coefficient from
    coeffDict:
    NodeNumberDICUType: CoeffValue
    e.g.
    123DRN-Q: 1.2345
    '''
    count = 0
#    if not os.path.isfile(In_DSSFile):
#        raise 'Cannot find file',In_DSSFile
    dss_group = opendss(DSSFile)
    for dataref in dss_group.getAllDataReferences():
        dataset = dataref.getData()
        inpath = dataref.getPathname()
        B = inpath.getPart(inpath.B_PART)
        C = inpath.getPart(inpath.C_PART).replace("FLOW","Q").replace("DRAIN","DRN")
        BC = B + C
        if BC in coeffDict:  # skip SEEP and non-nodal sources/sinks
            dataref = dataref * float(coeffDict.get(BC))
            # write updated value to  DSS file
            writedss(DSSFile, inpath.getFullPath(), dataref.getData())
            count += 1
    return count
#
def updateXCVals(GridfileIn, XCfileOut, coeffDict):
    '''
    '''
    p = Parser()
    count = 0
    # coeffDict should be all elevation corrections or all
    # width corrections, not both; find out which one
    if coeffDict.keys()[0].find('ELEV') >= 0:
        XELEV = True
        XTOPW = False
        param = 'ELEV'
    else:
        XELEV = False
        XTOPW = True
        param = 'WIDTH'
    tables = p.parseModel(GridfileIn)
    DSM2Model = tables.toDSM2Model()
    Channels = DSM2Model.getChannels()
    for chan in Channels.getChannels():
        PCF = param + "%03d" % (int(chan.getId()))
        try: coeff = coeffDict.get(PCF)
        except: raise 'Could not find coeff for param ' + PCF
        for xs in chan.getXsections():
            runningArea = 0.0
            Elev = 0.0
            TopWidth = 0.0
            for lyr in xs.getLayers():
                count += 1
                Area = lyr.getArea()
                prevElev = Elev
                Elev = lyr.getElevation()
                prevTW = TopWidth
                TopWidth = lyr.getTopWidth()
                WetPerim = lyr.getWettedPerimeter()
                if XELEV:
                    Elev = round(Elev * coeff, 2)
                    lyr.setElevation(Elev)
                if Area == 0.0:
                    continue
                if XTOPW:
                    TopWidth = round(TopWidth * coeff, 2)
                    lyr.setTopWidth(TopWidth)
                runningArea += (Elev-prevElev)*0.5*(TopWidth+prevTW)
                lyr.setArea(round(runningArea, 1))
                lyr.setWettedPerimeter(round(WetPerim * coeff, 2))
    fid = open(XCfileOut, 'w')
    tables.fromDSM2Model(DSM2Model)
    fid.write(tables.getTableNamed('CHANNEL').toStringRepresentation().expandtabs(4))
    fid.write(tables.getTableNamed('XSECT_LAYER').toStringRepresentation().expandtabs(4))
    fid.close()
    return count
#
def updateGridfileLength(Gridfile):
    # Update the channel gridfile to return Length back to integer values,
    # from PEST's floating point values
    FIDGrid = open(Gridfile,'r')
    FIDTemp = open('temp.txt','w')
    chanLines = False
    for line in FIDGrid:
        if re.search("^ *END *$", line, re.I):
            # channel block end
            chanLines = False
        if chanLines:
            lineArr = line.split()
            lineArr[1] = str(int(float(lineArr[1])+0.5))
            line = ''
            for s in lineArr:
                line += s + ' '
            line += '\n'
        FIDTemp.write(line)
        if re.search("CHAN_NO +LENGTH", line, re.I):
            # channel block header line, channel lines follow
            chanLines = True
    FIDGrid.close()
    FIDTemp.close()
    os.remove(Gridfile)
    os.rename('temp.txt', Gridfile)
    return            
#
