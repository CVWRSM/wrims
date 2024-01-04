import sys;
import re;
try: 
 import fft;
 from fft import *;
except:
 print "no fft available"
sys.add_package("vista.db.dss")
sys.add_package("vista.set")
sys.add_package("vista.app")
sys.add_package("vista.gui")
sys.add_package("vista.time")
sys.add_package('vista.graph')
sys.add_package('calsim.gui')
sys.add_package('calsim.app')
sys.add_package('java.io'); from java.io import FileOutputStream
# java based imports
from jarray import array, zeros
from java.io import PrintStream
from java.awt import Dimension,Rectangle,Insets,Point,Color
from java.text import NumberFormat,FieldPosition
from java.lang import IllegalAccessError, StringBuffer, Math, String
sys.add_package('javax.swing')
from javax.swing import JFrame
# import commonly used classses
from vista.time import TimeFactory, Time, TimeInterval, TimeFormat
from vista.set import Session, Group, DataReference, Pathname, \
PathPartPredicate, TimeSeriesMath, Stats, CompositeFilter
from vista.set import LRFilledTimeSeriesProxy, PeriodAverageProxy, \
MergingProxy
from vista.set import ProxyFactory
from vista.db.dss import DSSUtil, DSSUtil
from vista.app import MainProperties,GraphDataCommand,DataGraph, \
DataTable, MultiDataTable, \
GroupFrame, SessionFrame, DefaultGraphBuilder, CurveFactory
from vista.graph import GraphUtils, SymbolFactory,  \
Graph, GraphAttr, Plot, PlotAttr, Axis, AxisAttr, \
GELineLayout,GEAttr,GEContainer,GECanvas, FontResizeInteractor, \
Legend, LegendAttr, LegendItem, LegendItemAttr, MultiPlot
from vista.set import Constants,ElementFilterIterator, \
RegularTimeSeries, IrregularTimeSeries, DefaultDataSet, DataSetFactory, \
NaNFilter, ElementFilterCachedIterator, FlagUtils, SetUtils, \
TimeTuple, Tuple, TimeElement, DefaultDataSetElement
# predefine a few functions
openSession = DSSUtil.createSession
DSSUtil.setAccessProperties(MainProperties.getProperties())
#
def per_avg(ds,interval='1mon'):
	if hasattr(ds,'getServername'):
		ti = TimeFactory.getInstance().createTimeInterval(interval)
		return ProxyFactory.createPeriodOperationProxy(ProxyFactory.PERIOD_AVERAGE, ds, ti);
	elif hasattr(ds,'getTimeInterval'):
		ti = TimeFactory.getInstance().createTimeInterval(interval)
		return TimeSeriesMath.doPeriodOperation(ds,ti,
							TimeSeriesMath.PERIOD_AVERAGE )
	else:
		return None
#
def per_max(ds,interval='1mon'):
	if hasattr(ds,'getServername'):
		ti = TimeFactory.getInstance().createTimeInterval(interval)
		return ProxyFactory.createPeriodOperationProxy(ProxyFactory.PERIOD_MAX, ds, ti);
	elif hasattr(ds,'getTimeInterval'):
		ti = TimeFactory.getInstance().createTimeInterval(interval)
		return TimeSeriesMath.doPeriodOperation(ds,ti,
							TimeSeriesMath.PERIOD_MAX )
	else:
		return None
#
def per_min(ds,interval='1mon'):
	if hasattr(ds,'getServername'):
		ti = TimeFactory.getInstance().createTimeInterval(interval)
		return ProxyFactory.createPeriodOperationProxy(ProxyFactory.PERIOD_MIN, ds, ti);
	elif hasattr(ds,'getTimeInterval'):
		ti = TimeFactory.getInstance().createTimeInterval(interval)
		return TimeSeriesMath.doPeriodOperation(ds,ti,
							TimeSeriesMath.PERIOD_MIN )
	else:
		return None
#
def wrap_data(ds, filename='data.dss', server='local',pathname='/a/b/c///f/'):
	"wrap_data(dataset, filename='data.dss', server='local', pathname='/a/b/c///f/'\n wraps data set in a filename, servername and pathname"
	return DSSUtil.createDataReference(server,filename,pathname,ds)
#return new DefaultReference(server,filename,pathname,ds)
#
def find(group,filter):
     g=group.clone()
     g.filterBy(filter)
     return g
#
def opendss(filename, server='local'):
	"opendss( filename, server='local') : returns a group of references"
	return DSSUtil.createGroup(server,filename)
#
def writedss(filename, pathname, ds) :
	DSSUtil.writeData(filename,pathname,ds)
#
def writeascii(filename,ds,outputFlags=0) :
	SetUtils.write(ds,filename,outputFlags)
#
def tabulate(*ref) :
	if ( ref == None ) : print 'Empty reference list'; return;
	if len(ref) == 1:
		if hasattr(ref[0],'__len__'):
			MultiDataTable(ref[0])
		else: 
			DataTable(ref[0])
	else:
		MultiDataTable(ref)
#
# define function to graph a given set of data references.
def plot(*reflist) :
	if ( reflist == None ) : print 'Empty reference list'; return;
	gb = DefaultGraphBuilder()
	for ref in reflist: 
		if hasattr(ref,'__len__'):
			for iref in ref :
				gb.addData(iref)
		else:
			gb.addData(ref)
	graphs=gb.createGraphs()
	for graph in graphs: DataGraph(graph,"Graph")
#
#
# define function to graph a given set of data references.
def scatterplot(refx, refy) :
	refxy = ProxyFactory.createPairedTimeSeriesProxy(refx,refy)
	gb = DefaultGraphBuilder; gb.addData(ref);
	graphs = gb.createGraphs();
	DataGraph(graphs[0],'Scatter Plot')
#
def timeWindow(twstr):
	"creates a time window object from a string"
	return TimeFactory.getInstance().createTimeWindow(twstr)
def exit():
	sys.exit()
