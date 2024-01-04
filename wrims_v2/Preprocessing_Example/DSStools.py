

from vtools.datastore.transfer import *
from vtools.functions.api import *

def copy_to_future(infile,inPath,year_begin,year_end,outfile,outPath,forecast_year_begin):

	year_shifted = forecast_year_begin - year_begin
	time_shifted=str(year_shifted)+"YEAR"
	
	time_window=("1/2/"+str(year_begin), "12/31/"+str(year_end))
	
	batch_transfer(infile,outfile,inPath,time_window,outPath,shift,interval=time_shifted)

def copy(infile,inPath,time_window,outfile,outPath):

	batch_transfer(infile,outfile,inPath,time_window,outPath)
