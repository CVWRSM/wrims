


inFile=open('test.table','r') # read only

inData = inFile.readlines()

outData = [] # this is a list

for line in inData:
	line = line.strip()
	line = line.split()
	#print line
	print line[0], line[1] # print the first item and the second item
	outData.append(line)


print outData

 
outFile=open('out.table','w') # write access


for item in outData:
	print item
	year = int(item[0])+2000
	year = str(year)
	outFile.writelines( year + ' ' + item[1] + '\n')



