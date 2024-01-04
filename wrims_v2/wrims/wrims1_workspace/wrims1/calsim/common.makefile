#
CLASS_DIR=${CALSIM_HOME}${PATH_SEP}..
CLASS_LOC=$(CLASS_DIR)${PATH_SEP}$(PACKAGE_LOC)
CLASS_FILES=$(JAVA_FILES:%.java=${CLASS_LOC}${PATH_SEP}%.class)

#.KEEP_STATE:

#PRECIOUS: $(JAVA_FILES) 

# java related stuff ( change this to suit yourself)
JAVA = $(JAVA_HOME)${PATH_SEP}bin${PATH_SEP}java -mx32m
JAVAC = $(JAVA_HOME)${PATH_SEP}bin${PATH_SEP}javac -J"-mx44m"
#SWINGJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}swingall.jar
COLJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}collections.jar
VISTAJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}vista.jar
JGLJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}COM.jar
TESTJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}test.jar
HELPJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}jhall.jar
XMLJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}xml.jar
JGOJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}JGo.jar
JGOSVGJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}JGoSVG.jar
HECJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}hec.jar
HECLIBJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}heclib.jar
RMAJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}rma.jar
SCHEMATICJAR=$(CALSIM_HOME)${PATH_SEP}lib${PATH_SEP}WrimsSchematic.jar

#
JAVAC_OPTS= -g -d "$(CLASS_DIR)" -deprecation

CLASSPATH_DEF= ".$(CPATH_SEP)${CLASS_DIR}$(CPATH_SEP)$(JAVA_HOME)${PATH_SEP}lib${PATH_SEP}classes.zip$(CPATH_SEP)$(VISTAJAR)$(CPATH_SEP)$(JGLJAR)$(CPATH_SEP)$(TESTJAR)$(CPATH_SEP)$(HELPJAR)$(CPATH_SEP)$(COLJAR)$(CPATH_SEP)$(XMLJAR)$(CPATH_SEP)$(JGOJAR)$(CPATH_SEP)$(SCHEMATICJAR)"

#CB added then changed mindSCHEMATIC_CP_DEF= ".$(CPATH_SEP)$(JGOJAR)$(CPATH_SEP)$(JGOSVGJAR)$(CPATH_SEP)$(HECJAR)$(CPATH_SEP)$(HECLIBJAR)$(CPATH_SEP)$(RMAJAR)$(CPATH_SEP)$(SCHEMATICJAR)"

#${TESTS} :=JAVAC_OPT= -g -d $(CLASS_DIR)
#${TESTS}:= CLASSPATH_DEF=..${PATH_SEP}${CLASS_DIR}:${PATH_SEP}site${PATH_SEP}lib${PATH_SEP}java${PATH_SEP}classes.zip

all dbg: setversion $(CLASS_FILES)
	@echo "All done"

setversion:
	echo "version=1.3.4 Beta (XA16) Build Date: " `date` > $(CALSIM_HOME)${PATH_SEP}version

test: dbg
	(cd test $(CMD_SEP)  make test )

$(CLASS_LOC)${PATH_SEP}%.class: %.java $?
	$(JAVAC) $(JAVAC_OPTS) -classpath $(CLASSPATH_DEF)$(SCHEMATIC_CP_DEF) $< 
# CB - I created SCHEMATIC_CP_DEF to use for the schematic files, but I am unfamiliar with makefiles; it should be separate

clean: 
	cd $(CLASS_LOC) $(CMD_SEP) $(DELETE) *.class $(CMD_SEP) $(DELETE) *~

cleandocs:
	${DELETE} $(CLASS_DIR)${PATH_SEP}docs${PATH_SEP}*.html
