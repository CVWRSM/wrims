
randomFirstYear = 2002
randomSecondYear = 2003

tableFile_before=open('wytypes_example_before_writing.table','r') # read only
tableFile_after=open('wytypes_example_after_writing.table','w') # write

data = tableFile_before.readlines()

tableFile_after.writelines( data)

dataWY = {} # this will map water year type to water year. 

for line in data:
	element = line.strip().split()
	try:
		WY = int(element[0])
		if WY >= 1920:
			line = line.replace('\n', '')
			dataWY[WY] = line
			
	except:
		continue

tableFile_after.writelines( '\n' )	
tableFile_after.writelines( '2010' + dataWY[randomFirstYear][5:]+'\n')
tableFile_after.writelines( '2011' + dataWY[randomSecondYear][5:]+'\n')

	



