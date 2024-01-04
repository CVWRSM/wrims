import os
 
setupTemplate = open("example.in",'r')



setupTemplateTxt = setupTemplate.read()

setupTemplateTxt = setupTemplateTxt.replace("@{toBeChanged}",   "999")

setupfile = open("example.out","w")
setupfile.write(setupTemplateTxt)
setupfile.close()
    
