				README
	
=======================================================================
		CALSIM : Water Resources Simulation Model 
=======================================================================

			       CONTENTS

- Introduction
- What this package contains
- What this package needs
- Where to find more information
- Submitting comments, bug reports and feature requests
- Installation Notes


-----------------------------------------------------------------------
			     INTRODUCTION
-----------------------------------------------------------------------

CALSIM is a generalized water resources simulation model for evaluating
operational alternatives of large, complex river basins. Developed by
the Department of Water Resources, CALSIM integrates a simulation
language for flexible operational criteria specification, a linear
programming solver for efficient water allocation decisions, and
graphics capabilities for ease of use. These combined capabilities
provide a comprehensive and powerful modeling tool for water resource
systems simulation.

This is version 1.1.2 of CALSIM.

-----------------------------------------------------------------------
		      WHAT THIS PACKAGE CONTAINS
-----------------------------------------------------------------------

- README.txt
	This file you are currently reading

- COPYRIGHT
	Copyright notice

- FixedBugs.txt
        The list of bugs fixed.

- CALSIM Code

- a shared object or dll for accessing the time-series database ( HEC-DSS)
  and calsim's code
  
- public domain packages (*.jar)

-----------------------------------------------------------------------
		       WHAT THIS PACKAGE NEEDS
-----------------------------------------------------------------------

CALSIM utilizes two additional software programs:

(1) Lahey Fortran90 version 4.5
(2) XA Callable Library solver version 13.40

You must install these programs in order to properly run the CALSIM model. 

- Lahey LF9045 should be installed and "\LF9045\bin" included in your system PATH.
- XAV13.DLL should be added to the calsim\bin directory.

Please contact the vendors if you do not already have these software programs.

XA Callable Library Solver
--------------------------
Sunset Software Technology
1613 Chelsea Road, Suite 153
San Marino, CA 91108 USA
Tel:    626-441-1565
Fax:    626-441-1567
Email:  jim@sunsetsoft.com


Lahey LF9045 Compiler
---------------------
Lahey Computer Systems, Inc.
865 Tahoe Boulevard
P.O. Box 6091
Incline Village, NV 89450-6091
Tel:    702-831-2500
Fax:    702-831-8123
http://www.lahey.com

-----------------------------------------------------------------------
		    WHERE TO FIND MORE INFORMATION
-----------------------------------------------------------------------

CALSIM model documentation is available in PDF format. The complete
documentation consists of three files: a Manual, describing the
theoretical basis and model structure, a User’s Guide which includes
simple step-by-step instructions for getting started as well as sample
water resource systems, and a Language Reference describing the syntax
and structure of the WRESL language.

http://modeling.water.ca.gov/hydro/model/documentation.html
	
-----------------------------------------------------------------------
	SUBMITTING COMMENTS, BUG REPORTS AND FEATURE REQUESTS
-----------------------------------------------------------------------

Comments, bug reports and feature requests may be mailed to:

	calsim-bugs@osp.water.ca.gov
	
or from the site:

	http://wwwca.water.ca.gov/cgi-bin/jitterbug/calsim-bugs

=======================================================================
		     INSTALLING AND RUNNING CALSIM 
=======================================================================

-----------------------------------------------------------------------
			  INSTALLATION NOTES
-----------------------------------------------------------------------

IMPORTANT: Please make sure you understand the Copyright
(in the file named COPYRIGHT) before installing this release.

If you meet this package's requirments then 
	
1) Unzip the calsim.zip file in a temp directory. 
   This should create files: calsim.class and install.bat
   and the directory: jre

2) Execute (double click) the install.bat and follow the instructions.

3) Users running CALSIM under Windows 98 are advised to choose "custom
   installation and install calsim in the c:\calsim directory.