import logging

def initLogging(logFilePath):

    # clean old entries
    dummy = open(logFilePath,'w') 
    dummy.close()
    
    logger = logging.getLogger('Position_Analysis_Script')
    hdlr = logging.FileHandler(logFilePath)
    ch  = logging.StreamHandler()
    #formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    formatter = logging.Formatter('%(levelname)s: %(message)s')
    hdlr.setFormatter(formatter)
    ch.setFormatter(formatter)
    logger.addHandler(hdlr) 
    logger.addHandler(ch) 
    logger.setLevel(logging.INFO)
    
    return logger




