import os
import sys
import collections



def copyYearlyTableToFuture(inTablePath, outTablePath, beginYR, sequentialYRs, futureYR):
    
    absInPath = os.path.join(sys.path[0], inTablePath)
    absOutPath = os.path.join(sys.path[0], outTablePath)
    
    if not os.path.exists(os.path.dirname(absOutPath)):
        os.makedirs(os.path.dirname(absOutPath))
    
    outTableFile = open(absOutPath, "w+")
    inTableFile = open(absInPath, "r")
    
    

    data = inTableFile.read()
    
    data = data.split('\n')
    
    # remove empty lines    
    data = [line for line in data if line.strip()]
    
    # map water year type to water year
    dataWY = {} 

    for line in data:

        # copy whole content except empty lines
        outTableFile.write(line+'\n')
        
        # check valid item
        element = line.strip().split()
        try:
            WY = int(element[0])

            if WY >= 1920:
                dataWY[WY] = line.strip()

        except:
            continue

    #outTableFile.writelines( '\n' ) # cause error in wrims2 table reading
    
    for i, year in enumerate(range(beginYR, beginYR + sequentialYRs)):
         
        outTableFile.write( str(futureYR+i) + dataWY[year][4:] + "   ! copied from "+str(year) + "\n")


    inTableFile.close()
    outTableFile.close()
    

def copyMonthlyTableToFuture(inTablePath, outTablePath, beginYR, sequentialYRs, futureYR):
    
    absInPath = os.path.join(sys.path[0], inTablePath)
    absOutPath = os.path.join(sys.path[0], outTablePath)
    
    if not os.path.exists(os.path.dirname(absOutPath)):
        os.makedirs(os.path.dirname(absOutPath))
    
    outTableFile = open(absOutPath, "w+")
    inTableFile = open(absInPath, "r")
    
    

    data = inTableFile.read()
    
    data = data.split('\n')
    
    # remove empty lines    
    data = [line for line in data if line.strip()]
    
    # data_WY_MON [wateryear][month] = line
    data_WY_MON = collections.defaultdict(dict)
    #dataWY = {}

    for line in data:

        # copy whole content except empty lines
        outTableFile.write(line+'\n')
        
        # check valid item
        element = line.strip().split()
        try:
            WY = int(element[0])
            MON = int(element[1])

            if WY >= 1920:
                
                data_WY_MON[WY][MON] = line.strip()
                #dataWY[WY] = line
  
        except:
            continue

    #outTableFile.writelines( '\n' ) # cause error in wrims2 table reading
    
    
    for i, year in enumerate(range(beginYR, beginYR + sequentialYRs)):
         
        for month in sorted(data_WY_MON[year].iterkeys()):

            outTableFile.write( str(futureYR+i) + data_WY_MON[year][month][4:] + "   ! copied from "+str(year) + "\n")
            

    inTableFile.close()
    outTableFile.close() 
    
