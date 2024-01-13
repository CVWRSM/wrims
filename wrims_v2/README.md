# WRIMS: Water Resource Integrated Modeling System
a generalized water resources modeling system for evaluating operational alternatives of large, complex river basins. WRIMS integrates a simulation language for flexible operational criteria specification, a linear programming solver for efficient water allocation decisions, and graphics capabilities for ease of use. These combined capabilities provide a comprehensive and powerful modeling tool for water resource systems simulation.
- https://water.ca.gov/Library/Modeling-and-Analysis/Modeling-Platforms/Water-Resource-Integrated-Modeling-System

## WRIMS with HEC-DSS version 7 library
We are modernizing WRIMS and its development. This work updates WRIMS dependencies on HEC-DSS, the US Army Corps of 
Engineers data storage system. This allows WRIMS to read and write files using version 7 of the DSS data format. It 
is also a step toward devOps-style development, automating the integration of HEC-DSS libraries from HEC's artifact 
management system, rather than relying on manual updates or building locally from source.

## An evolving build system
WRIMS can be built from this repository following the procedure described [here](./README.build.md).<br>
As part of the movement toward devOps development, dependency analyses and proposals for re-organization of the 
build have been added as README files within components of this repository.

## WRIMS GUI dependencies
The WRIMS GUI is a plugin to Eclipse's Equinox RCP (Rich Client Platform). Its components include the packages 
listed below, with indicated dependencies on other packages managed here (i.e. excluding external jars from java, 
eclipse, etc.)

![](./README_images/wrims_gui.png)

- [gov.ca.dwr.jdiagram](./gov.ca.dwr.jdiagram/README.md)
- [gov.ca.dwr.hecdssvue](./gov.ca.dwr.hecdssvue/README.md)
- [wrimsv2_plugin](./wrims_v2/wrimsv2_plugin/README.md)
- [WRIMSv2](./wrims_v2/wrims_v2/README.md)