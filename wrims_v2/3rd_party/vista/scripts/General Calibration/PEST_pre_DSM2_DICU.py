import os, glob, datetime, math
from vtimeseries import *
#from vista.time import *
from vdss import *
from vista.set import *
#from vista.set import Group
from vista.set import PathnamePredicate
from vista.db.dss import *
from vutils import *
from vista.time import TimeFactory, TimeFormat, DefaultTimeFormat
from gov.ca.dsm2.input.parser import Parser
from gov.ca.dsm2.input.parser import Tables
from gov.ca.dsm2.input.model import *
# Pre-processor for DSM2 Hydro and Qual runs for PEST calibration.
# Since DIV, DRAIN (both flows), and DRAIN-EC are considered calibration
# parameters, their values will be adjusted during the PEST calibration
# with this pre-processor before each pair of Hydro/Qual runs.
#
def updateXCVals(GridfileIn, XCfileOut, coeffDict):
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
        for xs in chan.getXsections():
            if float(xs.getDistance()) < 1.0:
                fmtStr = "%03d"
            else:
                fmtStr = "%04d"                
            PCF = param + "%03d" % (int(xs.getChannelId())) + ':' + fmtStr % (int(xs.getDistance() * 1000.))
            coeff = coeffDict.get(PCF)
            if not coeff:
                print 'Could not find coeff for param', param, 'chan', xs.getChannelId(), 'dist', xs.getDistance()
                continue
            runningArea = 0.0
            elev = 0.0
            TopWidth = 0.0
            for lyr in xs.getLayers():
                count += 1
                area = lyr.getArea()
                prevElev = elev
                elev = lyr.getElevation()
                prevTW = TopWidth
                TopWidth = lyr.getTopWidth()
                WetPerim = lyr.getWettedPerimeter()
                if XELEV:
                    elev = round(elev * coeff, 2)
                    lyr.setElevation(elev)
                if area == 0.0:
                    continue
                if XTOPW:
                    TopWidth = round(TopWidth * coeff, 2)
                    lyr.setTopWidth(TopWidth)
                runningArea += (elev-prevElev)*0.5*(TopWidth+prevTW)
                lyr.setArea(round(runningArea, 1))
                lyr.setWettedPerimeter(round(WetPerim * coeff, 2))
    fid = open(XCfileOut, 'w')
    tables.fromDSM2Model(DSM2Model)
    fid.write(tables.getTableNamed('XSECT_LAYER').toStringRepresentation().expandtabs(4))
    fid.close()
    return count
#
def updateDSSAgVals(In_DSSFile, Out_DSSFile, coeffDict):
    count = 0
    try: os.remove(Out_DSSFile)
    except: pass
    dss_group = opendss(In_DSSFile)
    for dataref in dss_group.getAllDataReferences():
        dataset = dataref.getData()
        inpath = dataref.getPathname()
        B = inpath.getPart(inpath.B_PART)
        C = inpath.getPart(inpath.C_PART)
        BC = B + C
        if BC in coeffDict:  # skip SEEP and non-nodal sources/sinks
            dataref = dataref * float(coeffDict.get(BC))
            # write updated value to new DSS file
            writedss(Out_DSSFile, inpath.getFullPath(), dataref.getData())
            count+=1
    return count
#
if __name__ == '__main__':
    TF = TimeFactory.getInstance()
    filter = Constants.DEFAULT_FLAG_FILTER
    # Pre-processor for DSM2 Hydro and Qual runs for PEST calibration.
    # Since DIV, DRAIN (both flows), and DRAIN-EC are considered calibration
    # parameters, their values will be adjusted during the PEST calibration
    # with this pre-processor before each pair of Hydro/Qual runs.
    # This also adjusts coefficients for the width and elevation of each
    # channel cross-section.
    #
    BaseDir = 'D:/delta/models/Historical_v81_Beta_Release/'
    CommonDir = BaseDir + 'common_input/NAVD/'
    TSDir = BaseDir + 'timeseries/'
    CalibDir = BaseDir + '201X-Calibration/'
    PESTDir = CalibDir + 'PEST/Calib/'
    #
    # Ag diversions, drainages, and drain water qualities.
    #
    # this file must be the same as in PEST_Create_Files
    PESTInpAgFile = 'AgCalibCoeffs.inp'
    # DICU-related names must be the same as the DSM2 calibration run
    In_DICUfile = 'dicu_201203.dss'
    In_DICUECfile = 'dicuwq_200611_expand.dss'
    Out_DICUfile = 'dicu_201203-calib.dss'
    Out_DICUECfile = 'dicuwq_200611_expand-calib.dss'
    # read the Ag calibration coefficients, each line's format:
    # 'DIV-FLOW|DRAIN-FLOW|DRAIN-EC<3-digit-node-number>' <whitespace> CoeffValue
    FID = open(PESTDir + PESTInpAgFile,'r')
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
    countQ = updateDSSAgVals(TSDir + In_DICUfile, TSDir + Out_DICUfile, coeffDict)
    countEC = updateDSSAgVals(TSDir + In_DICUECfile, TSDir + Out_DICUECfile, coeffDict)
    print 'Updated', countQ, 'Ag diversion/drainage flow values to file', TSDir + Out_DICUfile
    print 'Updated', countEC, 'Ag return quality values to file', TSDir + In_DICUECfile
    #
    # Channel cross-section widths and elevations
    #
    # these two files must be the same as in PEST_Create_Files
    Gridfile = 'channel_std_delta_grid_NAVD_20121214.inp'
    PESTInpXCFile = 'XCCalibCoeffs.inp'
    # this file is the DSM2 cross-sections data modified by PEST
    # calibration coefficients
    DSM2XCFile = 'DSM2-Calib-XSects.inp'
    # read the x-sect calibration coefficients, each line's format:
    # 'WIDTH|ELEV<3-digit-chanIn-number>:<3/4-digit whole fraction>' <whitespace> CoeffValue
    PIXFID = open(PESTDir+PESTInpXCFile,'r')
    coeffDict = []
    for line in PIXFID:
        # Parameter name, channel number, fractional distance
        PCF = line.split()[0]
        Coeff = line.split()[1]
        # fractional dist * 1000 is after ':'
        PC = PCF.split(':')[0]
        fracDist = PCF.split(':')[1]
        # chanIn number always 3 chars
        chanIn = int(PC[len(PC)-3:])
        # param name is first several chars
        param = PC[0:len(PC)-3]
        coeffDict.append([PCF,float(Coeff)])
    FID.close()
    coeffDict = dict(coeffDict)
    countXC = updateXCVals(PESTDir+Gridfile, PESTDir+DSM2XCFile, coeffDict)
    print 'Updated', countXC, 'channel x-sect values to file', PESTDir+Gridfile
    #
    sys.exit()
    