import os

logger = None
logFile = '=MultiStudyRunner=.log'
loggerName = 'MultiStudyRunner'
templateDir = os.path.dirname(os.path.split(__file__)[0])
templateDir = os.path.normpath(templateDir)



def month2Str(iMonth):
    
    strMonthList=['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC']
    
    return strMonthList[iMonth-1]
        
    