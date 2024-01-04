LINKER=lf90
LINK_OPTIONS= -winconsole -NOMap -mod .
F90=lf90
F90_OPTIONS=-o3 -winconsole -c
MOD_OPTIONS=-mod .
F90_FILES = mkcode.f90
F90_OBJS = $(F90_FILES:%.f90=%.obj)
TARGET=mkcode.exe

all: $(F90_OBJS) 
	$(LINKER) $(F90_OBJS) -exe $(TARGET) $(LINK_OPTIONS) 

release: clean all
	cp $(TARGET) ../../bin

%.obj:%.f90
	$(F90) $(F90_OPTIONS) $<

clean:
	rm -rf $(F90_OBJS)
	rm -rf mkcode.exe
