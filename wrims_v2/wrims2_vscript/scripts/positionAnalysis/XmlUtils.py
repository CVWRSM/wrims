import xml.etree.ElementTree as ET

_xmlTree = None

def openXML(xmlPath):
    
    xmlfile = open(xmlPath,'r')
    result = xmlfile.read()
    _xmlTree = ET.fromstring(result)    

def getValue(xmlNode): 

    return _xmlTree.findtext(xmlNode) #"./info//age"

def getProp(xmlNode, key):

    return _xmlTree.find(xmlNode).attrib[key]