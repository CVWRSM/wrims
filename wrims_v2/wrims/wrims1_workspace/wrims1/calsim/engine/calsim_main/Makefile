LINKER=lf90
LINK_OPTIONS= -winconsole -NOMap -mod .
F90=lf90
F90_OPTIONS=-win -c -o3 -dll -ml msvc -tp -i ../simsolver -lib "../lib/heclib/heclib42.lib"
MOD_OPTIONS=-mod .
F90_MODS = code_ut0.mod global.mod
F90_FILES = calsim.f90 
F90_OBJS = $(F90_FILES:%.f90=%.obj)

all: #$(F90_MODS) $(F90_OBJS) 
	(cmd /c make_dos)
#	$(LINKER) $(F90_OBJS) -exe $(TARGET) $(LINK_OPTIONS) 

release: clean all
	cp calsim.obj ../../lib
	cp global.mod ../../lib
	cp global.lib ../../lib
	cp code_ut0.mod ../../lib
	cp modtable.txt ../../lib

code_ut0.mod: code_utils.f90
	$(F90) $(F90_OPTIONS) $<

global.mod: global.f90
	$(F90) $(F90_OPTIONS) $<

messeng0.mod: messenger.f90
	$(F90) $(F90_OPTIONS) $<

%.obj:%.f90
	$(F90) $(F90_OPTIONS) $<

clean:
	rm -rf $(F90_OBJS)
	rm -rf *.mod *.lib modtable.txt
