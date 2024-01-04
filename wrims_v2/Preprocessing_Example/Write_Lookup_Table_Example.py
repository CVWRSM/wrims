
tableFile_before=open('wytypes_example_before_writing.table','r') # read only
tableFile_after=open('wytypes_example_after_writing.table','w') # write

data = tableFile_before.readlines()

tableFile_after.writelines( data ) # copy original table to new table
tableFile_after.writelines( '\n' ) # write a new line

SACindex = {} # this is a dictionary, map index to water year
SJRindex = {}

SACindex[2010]= 0
SACindex[2011]= 1
SACindex[2012]= 2
SACindex[2013]= 3
SACindex[2014]= 4

SJRindex[2010]= 4
SJRindex[2011]= 3
SJRindex[2012]= 2
SJRindex[2013]= 1
SJRindex[2014]= 0

for year in range(2010,2015): # the last number, 2015, is not included. This is Python's rule.

	print year, SACindex[year], SJRindex[year] # print to screen

	tableFile_after.writelines( str(year)+'  '+str(SACindex[year])+'  '+str(SACindex[year])+'\n')



