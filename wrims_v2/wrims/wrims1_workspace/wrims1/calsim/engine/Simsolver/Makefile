# CB NOTE - the xavxx.lib file NEEDS to be xavxx.LIB (uppercase LIB) so it is NOT deleted.

LINKER=lf90
MODULE=.
#".;c:\lf9045\lib"
LINK_OPTIONS= -dll -mod $(MODULE) -ml msvc -win -wisk -trace -lib "..\lib\heclib\heclib42.lib,..\lib\hecNT\Debug\hecNT.lib"
F90=lf90
F90_OPTIONS= -ml msvc -wisk -win -trace -o3 -c
MOD_OPTIONS=-mod $(MODULE)
F90_MOD = hec_lib0.mod dss_cac0.mod dssio.mod tablemg2.mod tablemg0.mod tablemg1.mod 
F90_FILES = dss_interface.f90 tablemgr.f90 
F90_OBJS = $(F90_FILES:%.f90=%.obj)
TARGET=simsolver.dll

all: #$(F90_MOD) $(F90_OBJS) 
	(cmd /c make_dos)
#	(export PATH;PATH="../libstuff;c:/lf9045/bin/;$$PATH"; \
#	 $(LINKER) $(F90_OBJS) -exe $(TARGET) $(LINK_OPTIONS))

release: clean all
	cp $(TARGET) ../../bin
	cp simsolver.lib ../../lib

dss_cac0.mod: dss_cache.f90
	$(F90) $(F90_OPTIONS) $(MOD_OPTIONS) $<

dssio.mod: dssio.f90
	$(F90) $(F90_OPTIONS) $(MOD_OPTIONS) $<

hec_lib0.mod: hec_lib_dss.f90
	$(F90) $(F90_OPTIONS) $(MOD_OPTIONS) $<

tablemg0.mod tablemg1.mod tablemg2.mod : tablemgr_io.f90 tablemgr_query.f90 tablemgr_utils.f90
	$(F90) $(F90_OPTIONS) $(MOD_OPTIONS) tablemgr_utils.f90
	$(F90) $(F90_OPTIONS) $(MOD_OPTIONS) tablemgr_io.f90 
	$(F90) $(F90_OPTIONS) $(MOD_OPTIONS) tablemgr_query.f90 


dss_interface.obj:dss_interface.f90
	$(F90) $(F90_OPTIONS) $<

%.obj:%.f90
	$(F90) $(F90_OPTIONS) $<

%.obj:%.f90
	$(F90) $(F90_OPTIONS) $<

clean:
	rm -rf $(F90_OBJS)
	rm -rf simsolver.def simsolver.dll simsolver.lib simsolver.map simsolver.exp
	rm -rf *.obj
	rm -rf *.de* *.xp*
	rm -rf *.mod *.lib modtable.txt
