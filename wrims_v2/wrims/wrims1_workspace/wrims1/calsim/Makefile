# makefile

include ./local.makefile
include ./common.makefile

# document generation options
#JAVADOC = ${JAVA_HOME}/bin/javadoc
#JAVADOC = ../../../jdk1.1.8/bin/javadoc
JAVADOC = ${JAVA_HOME}
JAVADOC_OPT= -classpath ..\${CPATH_SEP}${CLASSPATH_DEF} -d docs -version -author

complete:
#	(cp -u ../hec_dlls/*.dll  bin/)
#	(cp -u libs/* lib/)
	cd wreslcoder$(PATH_SEP)wresl $(CMD_SEP) $(MAKE) all
	cd wreslcoder $(CMD_SEP) $(MAKE) all
	cd app $(CMD_SEP) $(MAKE) all
	cd gui $(CMD_SEP) $(MAKE) all
	cd debug $(CMD_SEP) $(MAKE) all	
#        cd .$(PATH_SEP)gui $(CMD_SEP) $(MAKE) all  #CB does not work - cygwin bug
	cd gym $(CMD_SEP) $(MAKE) all
	cd schematic $(CMD_SEP) $(MAKE) all
#	cd engine $(CMD_SEP) make --unix release
	cd msw $(CMD_SEP) $(MAKE) all
#	cd ../wrims/dss $(CMD_SEP) $(MAKE) all          #CB not needed
#	cd ../wrims/schematic $(CMD_SEP) $(MAKE) all    #CB for building WRIMS Schematic as part of - now it is standalone
	$(MAKE) jar

release: top-clean complete
	$(MAKE) pack

fullrelease: top-clean complete
	$(MAKE) fullpack

jar:
#	(rm -rf classes; mkdir classes; mkdir classes/calsim; mkdir classes/wrims) # CB amended
	(rm -rf classes; mkdir classes; mkdir classes/calsim) # original
	(cp -P app/data/*.table classes/calsim)
	(cp -P gui/*.class classes/calsim)
#	(cp -P gui/*.java classes/calsim)
	(cp -P app/*.class classes/calsim)
	(cp -P debug/*.class classes/calsim)	
#	(cp -P app/*.java classes/calsim)
	(cp -P msw/*.class classes/calsim)
#	(cp -P msw/*.java classes/calsim)
	(cp -P wreslcoder/*.class classes/calsim)
#	(cp -P wreslcoder/*.java classes/calsim)
	(cp -P wreslcoder/wresl/*.class classes/calsim)
#	(cp -P wreslcoder/wresl/*.java classes/calsim)
	(cp -P schematic/*.class classes/calsim)
#	(cp -P schematic/*.java classes/calsim)
	(cp -P schematic/*.gif classes/calsim)
	(cp -P gym/*.class classes/calsim)
#	(cp -P gym/*.java classes/calsim)
	(cp -P app/*.props classes/calsim)
	(cp -P gui/*.gif classes/calsim)
#	(cp -P ../wrims/schematic/*.class classes/wrims) # CB added - then later replaced with a .jar file
#	(cp -P ../wrims/schematic/images/*.gif classes/wrims) # CB added - then later replaced with a .jar file
#	(cp -P ../wrims/schematic/svg/*.svg classes/wrims) # CB added - then later replaced with a .jar file
#	(cp -P ../wrims/dss/*.class classes/wrims) # CB added - then later replaced with a .jar file
	(cp -P version classes/calsim)
	
	(cd classes; $(JAR) -cf calsim.jar calsim wrims; cp calsim.jar ../lib)
	(cd help; rm -rf calsim-help.jar; $(JAR) -cf calsim-help.jar *; cp calsim-help.jar ../lib;)

pack:
#	(cp -u ../hec_dlls/*.dll  bin/)
#	(cp -u libs/* lib/)
#	(cp -u svgs/* svg/)
#	rm -rf calsim.zip
#	cd ${CALSIM_HOME}/installer; $(MAKE) installer

fullpack: pack
#	zip -r calsim.zip jre

top-clean:
	cd gui $(CMD_SEP) $(MAKE) clean
	cd app $(CMD_SEP) $(MAKE) clean
	cd msw $(CMD_SEP) $(MAKE) clean
	cd wreslcoder $(CMD_SEP) $(MAKE) clean
	cd wreslcoder$(PATH_SEP)wresl $(CMD_SEP) #$(DELETE) WreslParser.java
	cd wreslcoder$(PATH_SEP)wresl $(CMD_SEP) $(MAKE) clean
	cd gym $(CMD_SEP) $(MAKE) clean
#	cd schematic $(CMD_SEP) $(MAKE) clean
#	cd ../wrims/schematic $(CMD_SEP) $(MAKE) clean
#	cd ../wrims/dss $(CMD_SEP) $(MAKE) clean

docs: FORCE
	${JAVADOC} ${JAVADOC_OPT} calsim.app calsim.gui calsim.wreslcoder calsim.wreslcoder.wresl calsim.gui 
#	wrims.schematic wrims.dss #CB added last 3 - then later replaced with a .jar file

update:
	cvs update

FORCE:


