## wrimsv2_plugin dependencies
The WRIMS GUI is a plugin to Eclipse's Equinox RCP (Rich Client Platform). Its components include the packages 
listed below, with indicated dependencies on other packages managed here (i.e. excluding external jars from java, 
eclipse, etc.)
### Jar files:
- vista.jar
- WRIMSv2.jar
- hec.jar
- dsm2-input-model.jar
- wrimsv2_plugin_1.0.0.0.jar (i.e. internal dependencies)

### Java Package dependencies
(from jdeps.exe runs against distribution jars linked to HEC-DSS v6 or v7 libraries)

- Dependencies on vista.jar
  - wrimsv2_plugin.batchrun -> vista.db.dss
  - wrimsv2_plugin.debugger.dialog -> vista.db.dss
  - wrimsv2_plugin.reporttool -> vista.db.dss
  - wrimsv2_plugin.reporttool -> vista.report
  - wrimsv2_plugin.reporttool -> vista.set
  - wrimsv2_plugin.reporttool -> vista.time

- Dependencies on WRIMSv2.jar
  - wrimsv2_plugin.debugger.commanditem -> wrimsv2.commondata.wresldata
  - wrimsv2_plugin.debugger.commanditem -> wrimsv2.debug
  - wrimsv2_plugin.debugger.dialog -> wrimsv2.commondata.wresldata
  - wrimsv2_plugin.debugger.dialog -> wrimsv2.components
  - wrimsv2_plugin.debugger.dialog -> wrimsv2.debug
  - wrimsv2_plugin.tools -> wrimsv2.commondata.wresldata

- Dependencies on hec.jar (HEC-DSS v6) or hec-monolith-3.3.22.jar (HEC-DSS v7)
  - wrimsv2_plugin.debugger.core -> hec.heclib.dss
  - wrimsv2_plugin.debugger.core -> hec.io
  - wrimsv2_plugin.debugger.dialog -> hec.heclib.dss
  - wrimsv2_plugin.debugger.dialog -> hec.io
  - wrimsv2_plugin.debugger.msr -> hec.heclib.dss
  - wrimsv2_plugin.debugger.launcher -> hec.heclib.dss
  - wrimsv2_plugin.debugger.launcher -> hec.hecmath
  - wrimsv2_plugin.debugger.launcher -> hec.io
  - wrimsv2_plugin.debugger.msr -> hec.heclib.dss
  - wrimsv2_plugin.debugger.msr -> hec.hecmath
  - wrimsv2_plugin.debugger.msr -> hec.io
  - wrimsv2_plugin.debugger.pa -> hec.heclib.dss
  - wrimsv2_plugin.debugger.pa -> hec.hecmath
  - wrimsv2_plugin.debugger.pa -> hec.io
  - wrimsv2_plugin.debugger.view -> hec.gfx2d
  - wrimsv2_plugin.debugger.view -> hec.heclib.dss
  - wrimsv2_plugin.debugger.view -> hec.io

- Other external dependencies
  - wrimsv2_plugin.reporttool -> com.lowagie.text
  - wrimsv2_plugin.reporttool -> com.lowagie.text.pdf
  - wrimsv2_plugin.reporttool -> gov.ca.dsm2.input.parser (dsm2-input-model.jar)

- Internal dependencies (i.e. on wrimsv2_plugin_1.0.0.0.jar)
  - wrimsv2_plugin.batchrun -> wrimsv2_plugin.tools
  - wrimsv2_plugin.batchrun -> wrimsv2_plugin.debugger.[core|dialog|exception|msr|pa]
  - wrimsv2_plugin.calsimhydro -> wrimsv2_plugin.debugger.exception
  - wrimsv2_plugin.debugger.breakpoint -> wrimsv2_plugin.debugger.[exception|breakpoint|model]
  - wrimsv2_plugin.debugger.commanditem -> wrimsv2_plugin.debugger.[core|dialog|exception|model|view]
  - wrimsv2_plugin.debugger.commanditem -> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.console -> wrimsv2_plugin.debugger.[core|exception|toolbaritem]
  - wrimsv2_plugin.debugger.console -> wrimsv2_plugin.debugger.[exception|toolbaritem]
  - wrimsv2_plugin.debugger.core -> wrimsv2_plugin.calsimhydro
  - wrimsv2_plugin.debugger.core -> wrimsv2_plugin.debugger.[exception|menuitem|model|toolbaritem]
  - wrimsv2_plugin.debugger.core -> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.dialog -> wrimsv2_plugin.batchrun
  - wrimsv2_plugin.debugger.dialog -> wrimsv2_plugin.debugger.[core|exception|goal|listener|menuitem|model|toolbaritem|view]
  - wrimsv2_plugin.debugger.dialog -> wrimsv2_plugin.reporttool
  - wrimsv2_plugin.debugger.dialog -> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.model -> wrimsv2_plugin.tools
  - wrimsv2_plugin.editor ->  wrimsv2_plugin.debugger.[model|exception|breakpoint]
  - wrimsv2_plugin.debugger.exception -> wrimsv2_plugin.debugger.view
  - wrimsv2_plugin.debugger.launcher -> wrimsv2_plugin.batchrun
  - wrimsv2_plugin.debugger.launcher -> wrimsv2_plugin.calsimhydro
  - wrimsv2_plugin.debugger.launcher -> wrimsv2_plugin.debugger.[core|dialog|exception|menuitem|model|msr|pa|toolbaritem]
  - wrimsv2_plugin.debugger.launcher -> wrimsv2_plugin.sensitivity
  - wrimsv2_plugin.debugger.launcher -> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.listener -> wrimsv2_plugin.debugger.core
  - wrimsv2_plugin.debugger.menuitem -> wrimsv2_plugin.debugger.[core|dialog|exception|model|toolbaritem]
  - wrimsv2_plugin.debugger.model -> wrimsv2_plugin.debugger.[breakpoint|core|exception|menuitem|toolbaritem|view]
  - wrimsv2_plugin.debugger.model -> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.msr -> wrimsv2_plugin.batchrun
  - wrimsv2_plugin.debugger.msr -> wrimsv2_plugin.debugger.[core|exception]
  - wrimsv2_plugin.debugger.msr -> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.pa-> wrimsv2_plugin.[batchrun|core|exception]
  - wrimsv2_plugin.debugger.pa-> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.sourcelookup -> wrimsv2_plugin.debugger.model
  - wrimsv2_plugin.debugger.toolbaritem -> wrimsv2_plugin.debugger.[core|dialog|exception|menuitem|model|view]
  - wrimsv2_plugin.debugger.toolbaritem -> wrimsv2_plugin.tools
  - wrimsv2_plugin.debugger.view -> wrimsv2_plugin.batchrun
  - wrimsv2_plugin.debugger.view -> wrimsv2_plugin.debugger.[core|dialog|exception|listener|model]
  - wrimsv2_plugin.debugger.view -> wrimsv2_plugin.tools
  - wrimsv2_plugin.editor -> wrimsv2_plugin.debugger.[breakpoint|core|exception|model]
  - wrimsv2_plugin.presentation -> wrimsv2_plugin.debugger.[breakpoint|exception|model]
  - wrimsv2_plugin.reporttool -> wrimsv2_plugin.debugger.exception
  - wrimsv2_plugin.reporttool -> wrimsv2_plugin.tools
  - wrimsv2_plugin.sensitivity -> wrimsv2_plugin.debugger.exception
  - wrimsv2_plugin.sensitivity -> wrimsv2_plugin.tools
  - wrimsv2_plugin.tools -> wrimsv2_plugin.debugger.core
  - wrimsv2_plugin.tools -> wrimsv2_plugin.debugger.exception
  - wrimsv2_plugin.tools -> wrimsv2_plugin.debugger.model
  - test -> wrimsv2_plugin.debugger