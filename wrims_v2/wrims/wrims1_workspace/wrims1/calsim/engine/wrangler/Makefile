LINKER=lf90
MODULE=.
LINK_OPTIONS= -dll -mod $(MODULE) -ml msvc -win -wisk -trace -lib "..\lib\heclib\heclib42.lib,..\lib\hecNT\hecNT.lib"
F90=lf90
F90_OPTIONS= -ml msvc -wisk -win -trace -o3 -c
MOD_OPTIONS=-mod $(MODULE)
F90_MOD = hec_lib0.mod dss_cac0.mod dssio.mod tablemg2.mod tablemg0.mod tablemg1.mod 
F90_FILES = dss_interface.f90 tablemgr.f90 
F90_OBJS = $(F90_FILES:%.f90=%.obj)
TARGET=wrangler.dll

all: #$(F90_MOD) $(F90_OBJS) 
	(cmd /c make_dos)

release: clean all
	cp $(TARGET) ../../bin 
	cp wrangler.lib ../../lib

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
	rm -rf wrangler.def wrangler.dll wrangler.lib wrangler.map wrangler.exp
	rm -rf dss_cac0.mod dssio.mod hec_lib0.mod modtable.txt 
	rm -rf dss_cac0.lib dssio.lib hec_lib0.lib 
	rm -rf tablemg0.lib tablemg0.mod 
	rm -rf tablemg1.lib tablemg1.mod
	rm -rf tablemg2.lib tablemg2.mod
	rm -rf DSS_INTERFACE.de* DSS_INTERFACE.xp\$$ 
	rm -rf tablemgr.xp\$$ tablemgr.de\$$ 
