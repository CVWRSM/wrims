import os,subprocess
import os.path
from sets import Set
from collections import defaultdict

for icycle in range(1, 99):

	if not os.path.exists('rccfile_cycle_'+str(icycle)+'.txt'):
		break
		
	rccfile=open('rccfile_cycle_'+str(icycle)+'.txt','r') # read only
	outfile=open('rcc_reformatted_'+str(icycle)+'.txt','w') # write


	data = rccfile.readlines()


	dvar = defaultdict(list)
	sign_rhs  = {}
	var_coef  = defaultdict(list)
	nameSet  = set()
	nameList = []

	di = 0

	for line in data:

		s=""
		v=""
		
		e = line.strip().split()
		
		if ("@endfile" in e[0] ):
			break

		
		elif ( e[0] == "MAX" or e[0] =="MIN"):
			dvar[di].append(e[0])
			dvar[di].append(s)
			di = di + 1
			
		else: 
			
			if not (e[0] in nameSet):
		
				nameSet.add(e[0])
				nameList.append(e[0])

			if (e[1] == "Max"):
				s = " < " + "; " + e[2]	
				sign_rhs[e[0]] = s	
			
			elif (e[1] == "Min"):
				s = " > " + "; " + e[2]		
				sign_rhs[e[0]] = s	
				
			elif ( (e[1] == "Fix") or (e[1] == "FIX")):
				s = " = " + "; " + e[2]	
				sign_rhs[e[0]] = s	
				
			else:
				v =  e[1] + " | " + e[2]
				var_coef[e[0]].append(v)

				
			

		
	print "\nwriting reformatted rcc file...\n"

	for label in nameList:
		
		s = var_coef[label][0]
		
		#print s
		
		for e in var_coef[label][1:]:
			
		#	print e
			
			s = s + " ; " + e
		
		if sign_rhs.has_key(label):
			s = s + " : " + sign_rhs[label]
		
		outfile.writelines( label +" : "+ s +"\n\n")

	rccfile.close()
	outfile.close()
	
print "finished writing reformatted rcc file.\n"

