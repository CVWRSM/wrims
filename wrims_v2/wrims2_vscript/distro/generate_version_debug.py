import os

Version_Main    = "1.0"  
Version_Status  = "beta"   

setupTemplate = open("version.template",'r')
antTemplate = open("build_zip.template",'r')


(dummy, Version_SVN) = os.popen4("svnversion ../../calsim_pa ")


Version_SVN = Version_SVN.readlines()[0]

Version_SVN = Version_SVN.strip()


print ' SVN version of ../../calsim_pa:                 '+ Version_SVN

Version_SVN = "debug"

try:
	#test = int(Version_SVN)

	setupTemplateTxt = setupTemplate.read()
	antTemplateTxt = antTemplate.read()
	
	antTemplateTxt = antTemplateTxt.replace("{Version_SVN}",    Version_SVN)

	antfile = open("build_zip.xml","w")
	antfile.write(antTemplateTxt)
	antfile.close()
	
	setupTemplateTxt = setupTemplateTxt.replace("@{Version_Main}",   Version_Main)
	setupTemplateTxt = setupTemplateTxt.replace("@{Version_SVN}",    Version_SVN)	
	setupTemplateTxt = setupTemplateTxt.replace("@{Version_Status}", Version_Status)
	
	setupfile = open("../../out/CalSimPA/version/version.xml","w")
	setupfile.write(setupTemplateTxt)
	setupfile.close()
        

except:
	print 'Abort.... Try SVN update and commit before generating setup script.'
    
