import shutil
import os


def month2Str(iMonth):
    
    strMonthList=['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC']
    
    return strMonthList[iMonth-1]

def isLeapYear(iYear):
                      
    if iYear % 4 == 0:
        if iYear % 100 != 0:
            return True
            
        elif iYear % 400 == 0:
            return True
            
        else:
            return False
            
    else:
        return False
        
        
def numberOfDays(iMonth, iYear):
                                
    totalDays = -99
        
    month31 = [1,3,5,7,8,10,12]      
    month30 = [4,6,9,11]
        
    if iMonth in month31:            
        totalDays=31
        
    elif iMonth in month30:
        totalDays=30
        
    else:
        if isLeapYear(iYear):
            totalDays=29
                
        else:
            totalDays=28
                
    return totalDays
