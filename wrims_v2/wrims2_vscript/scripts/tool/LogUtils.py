import logging
import Param
import os.path


def initLogging():

    Param.logFile = os.path.basename(Param.mainScriptPath) + '.log'
    logFilePath = os.path.join(os.path.dirname(Param.mainScriptPath),Param.logFile) 
    
    # clean old entries
    dummy = open(logFilePath,'w') 
    dummy.close()
    
    logger = logging.getLogger(Param.loggerName)
    hdlr = logging.FileHandler(logFilePath)
    ch  = logging.StreamHandler()
    #formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    formatter = logging.Formatter('%(levelname)s: %(message)s')
    hdlr.setFormatter(formatter)
    ch.setFormatter(formatter)
    logger.addHandler(hdlr) 
    logger.addHandler(ch) 
    logger.setLevel(logging.INFO)
    
    
    Param.logger = logger




