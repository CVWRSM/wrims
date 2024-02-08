## wrimsv2 dependencies
The WRIMSv2 jar provides computing and data functions to the WRIMS compute and the wrims_plugin jar. Its components include the packages
listed below, with indicated dependencies on other packages managed here (i.e. excluding external jars from java,
eclipse, etc.)
<br>
### Genearated source
These antlr grammar files generate java source, which is compiled into the WRIMSv2.jar.
- `wrims_v2/wrims_v2/src/wrimsv2/wreslplus/IncFileFinder.g`
- `wrims_v2/wrims_v2/src/wrimsv2/wreslplus/WreslPlus.g`
- `wrims_v2/wrims_v2/src/wrimsv2/wreslparser/grammar/Wresl.g`
- `wrims_v2/wrims_v2/src/wrimsv2/wreslparser/grammar/WreslTreeWalker.g`
- `wrims_v2/wrims_v2/src/wrimsv2/evaluator/Evaluator.g`
- `wrims_v2/wrims_v2/src/wrimsv2/evaluator/ValueEvaluator.g`
- `wrims_v2/wrims_v2/src/wrimsv2/evaluator/ValueEvaluatorTree.g`
- `wrims_v2/wrims_v2/src/wrimsv2/evaluator/ValueEvaluatorTreeWalker.g`

### Compiled non-Java source<br>
C/C++ source is present in `wrims_v2/wrims_v2/src/wrimsv2/cplusplus`
- `wrimsv2_external_FunctionAnn_X2.[c|h]`
- `wrimsv2_external_FunctionAnnEC.[c|h]`
- `wrimsv2_external_FunctionAnnLineGen.[c|h]`
<br>
The ANN library wrapped by the above C code is compiled from fortran (f90) source. There is source in
`wrims_v2/wrims_v2/src/wrimsv2/linux/Fortran_DLL_Sample/CA` along with a shell script to make it. 
It's not clear from the contents of the repo how the fortran is compiled for the Windows distribution
<br>
Batch file `\wrims_v2\wrims_v2\src\wrimsv2\external\create_c_header.bat` is not not used for builds.
<br>

### Java package dependencies 
(from jdeps.exe runs against distribution jars linked to HEC-DSS v6 or v7 libraries)

- Dependencies on vista.jar:<br>
(There are no dependencies on vista in WRIMSv2 builds links to HEC-DSS v7.<br>
the following dependencies are present in builds linked to HEC-DSS v6)
  - wrimsv2.components                                 -> vista.db.dss
  - wrimsv2.components                                 -> vista.set
  - wrimsv2.debug                                      -> vista.db.dss
  - wrimsv2.debug                                      -> vista.set
  - wrimsv2.evaluator                                  -> vista.db.dss
  - wrimsv2.evaluator                                  -> vista.set
  - wrimsv2.evaluator                                  -> vista.time
  - wrimsv2.hdf5                                       -> vista.db.dss
  - wrimsv2.hdf5                                       -> vista.set
  - wrimsv2.hdf5                                       -> vista.time
  - wrimsv2.sql                                        -> vista.db.dss
  - wrimsv2.sql                                        -> vista.set
  - wrimsv2.sql                                        -> vista.time<br><br>

- Dependencies on hec-monolith jar:<br>
(no monolith dependencies in distribution linked to HEC-DSS v6<br>
the following dependencies are present when linked to HEC-DSS v7)
  - wrimsv2.components                                 -> hec.heclib.dss
  - wrimsv2.components                                 -> hec.heclib.util
  - wrimsv2.debug                                      -> hec.heclib.dss
  - wrimsv2.evaluator                                  -> hec.heclib.dss
  - wrimsv2.evaluator                                  -> hec.heclib.util
  - wrimsv2.evaluator                                  -> hec.io
  - wrimsv2.hdf5                                       -> hec.heclib.dss
  - wrimsv2.hdf5                                       -> hec.heclib.util
  - wrimsv2.hdf5                                       -> hec.io
  - wrimsv2.sql                                        -> hec.heclib.dss
  - wrimsv2.sql                                        -> hec.heclib.util
  - wrimsv2.sql                                        -> hec.io<br>

- Dependencies on 3rd party packages:<br>
(These are only the java dependencies. Some 3rd-party packages have additional dependencies on native libraries as well.)<br>
  - wrimsv2.components                                 -> com.sunsetsoft.xa
  - wrimsv2.components                                 -> gurobi
  - wrimsv2.config                                     -> org.coinor.cbc
  - wrimsv2.external                                   -> jep
  - wrimsv2.external                                   -> org.tensorflow
  - wrimsv2.hdf5                                       -> ncsa.hdf.hdf5lib
  - wrimsv2.hdf5                                       -> ncsa.hdf.hdf5lib.exceptions
  - wrimsv2.ilp                                        -> com.google.ortools.linearsolver
  - wrimsv2.ilp                                        -> com.sunsetsoft.xa
  - wrimsv2.solver                                     -> com.sunsetsoft.xa
  - wrimsv2.solver                                     -> lpsolve
  - wrimsv2.solver                                     -> org.coinor.cbc
  - wrimsv2.solver                                     -> org.coinor.clp
  - wrimsv2.solver.Gurobi                              -> gurobi
  - wrimsv2.solver.ortools                             -> com.google.ortools.linearsolver
  - wrimsv2.tf                                         -> org.tensorflow
  - wrimsv2.tools                                      -> de.danielbechler.diff
  - wrimsv2.tools                                      -> de.danielbechler.diff.node
  - wrimsv2.tools.solutionRangeFinder                  -> com.google.ortools.linearsolver
  - wrimsv2.wreslparser.elements                       -> com.esotericsoftware.kryo
  - wrimsv2.wreslparser.elements                       -> com.esotericsoftware.kryo.io
  - wrimsv2.wreslparser.elements                       -> org.testng  (only dependency on testng; uses assert; is another assert available?)

- Antlr dependencies:<br>
(These are present in the distributed WRIMSv2.jar, but they're 3rd party content, not part of WRIMS development.)
  - org.antlr.runtime.tree                             -> org.antlr.stringtemplate (not actually in the WRIMSv2.jar)
  - org.antlr.runtime                                  -> org.antlr.runtime.misc
  - org.antlr.runtime                                  -> org.antlr.runtime.tree
  - org.antlr.runtime.debug                            -> org.antlr.runtime
  - org.antlr.runtime.debug                            -> org.antlr.runtime.misc
  - org.antlr.runtime.debug                            -> org.antlr.runtime.tree
  - org.antlr.runtime.tree                             -> org.antlr.runtime
  - org.antlr.runtime.tree                             -> org.antlr.runtime.misc
  - wrimsv2.commondata.wresldata                       -> org.antlr.runtime
  - wrimsv2.components                                 -> org.antlr.runtime
  - wrimsv2.config                                     -> org.antlr.runtime
  - wrimsv2.evaluator                                  -> org.antlr.runtime
  - wrimsv2.evaluator                                  -> org.antlr.runtime.tree
  - wrimsv2.wreslparser.elements                       -> org.antlr.runtime
  - wrimsv2.wreslparser.elements                       -> org.antlr.runtime.tree
  - wrimsv2.parallel                                   -> org.antlr.runtime
  - wrimsv2.tools                                      -> org.antlr.runtime
  - wrimsv2.wreslparser.grammar                        -> org.antlr.runtime
  - wrimsv2.wreslparser.grammar                        -> org.antlr.runtime.tree
  - wrimsv2.wreslplus.elements                         -> org.antlr.runtime
  - wrimsv2.wreslplus.elements.procedures              -> org.antlr.runtime
  - wrimsv2.wreslplus.grammar                          -> org.antlr.runtime
  - wrimsv2.wreslplus.grammar                          -> org.antlr.runtime.tree

- Internal dependencies (i.e. on wrimsv2.jar)
  - wrimsv2.commondata.solverdata                      -> wrimsv2.commondata.wresldata
  - wrimsv2.commondata.solverdata                      -> wrimsv2.evaluator
  - wrimsv2.commondata.wresldata                       -> wrimsv2.commondata.solverdata
  - wrimsv2.commondata.wresldata                       -> wrimsv2.components
  - wrimsv2.commondata.wresldata                       -> wrimsv2.evaluator
  - wrimsv2.commondata.wresldata                       -> wrimsv2.parallel
  - wrimsv2.components                                 -> wrimsv2.commondata.solverdata
  - wrimsv2.components                                 -> wrimsv2.commondata.wresldata
  - wrimsv2.components                                 -> wrimsv2.config
  - wrimsv2.components                                 -> wrimsv2.debug
  - wrimsv2.components                                 -> wrimsv2.evaluator
  - wrimsv2.components                                 -> wrimsv2.external
  - wrimsv2.components                                 -> wrimsv2.hdf5
  - wrimsv2.components                                 -> wrimsv2.ilp
  - wrimsv2.components                                 -> wrimsv2.launch
  - wrimsv2.components                                 -> wrimsv2.parallel
  - wrimsv2.components                                 -> wrimsv2.solver
  - wrimsv2.components                                 -> wrimsv2.solver.Gurobi
  - wrimsv2.components                                 -> wrimsv2.solver.mpmodel
  - wrimsv2.components                                 -> wrimsv2.solver.ortools
  - wrimsv2.components                                 -> wrimsv2.sql
  - wrimsv2.components                                 -> wrimsv2.tools
  - wrimsv2.components                                 -> wrimsv2.wreslparser.elements
  - wrimsv2.components                                 -> wrimsv2.wreslparser.grammar
  - wrimsv2.components                                 -> wrimsv2.wreslplus.elements
  - wrimsv2.components                                 -> wrimsv2.wreslplus.elements.procedures
  - wrimsv2.components                                 -> wrimsv2.wreslplus.grammar
  - wrimsv2.config                                     -> wrimsv2.components
  - wrimsv2.config                                     -> wrimsv2.evaluator
  - wrimsv2.config                                     -> wrimsv2.ilp
  - wrimsv2.config                                     -> wrimsv2.solver
  - wrimsv2.config                                     -> wrimsv2.wreslparser.elements
  - wrimsv2.config                                     -> wrimsv2.wreslplus.elements
  - wrimsv2.config                                     -> wrimsv2.wreslplus.grammar
  - wrimsv2.debug                                      -> wrimsv2.commondata.wresldata
  - wrimsv2.debug                                      -> wrimsv2.components
  - wrimsv2.debug                                      -> wrimsv2.evaluator
  - wrimsv2.debug                                      -> wrimsv2.external
  - wrimsv2.debug                                      -> wrimsv2.solver
  - wrimsv2.debug                                      -> wrimsv2.wreslparser.elements
  - wrimsv2.evaluator                                  -> wrimsv2.commondata.solverdata
  - wrimsv2.evaluator                                  -> wrimsv2.commondata.wresldata
  - wrimsv2.evaluator                                  -> wrimsv2.components
  - wrimsv2.evaluator                                  -> wrimsv2.external
  - wrimsv2.evaluator                                  -> wrimsv2.hdf5
  - wrimsv2.evaluator                                  -> wrimsv2.ilp
  - wrimsv2.evaluator                                  -> wrimsv2.parallel
  - wrimsv2.evaluator                                  -> wrimsv2.solver
  - wrimsv2.evaluator                                  -> wrimsv2.tools
  - wrimsv2.external                                   -> wrimsv2.commondata.wresldata
  - wrimsv2.external                                   -> wrimsv2.components
  - wrimsv2.external                                   -> wrimsv2.evaluator
  - wrimsv2.external                                   -> wrimsv2.ilp
  - wrimsv2.external                                   -> wrimsv2.solver
  - wrimsv2.hdf5                                       -> wrimsv2.commondata.wresldata
  - wrimsv2.hdf5                                       -> wrimsv2.components
  - wrimsv2.hdf5                                       -> wrimsv2.config
  - wrimsv2.hdf5                                       -> wrimsv2.evaluator
  - wrimsv2.hdf5                                       -> wrimsv2.launch
  - wrimsv2.hdf5                                       -> wrimsv2.parallel
  - wrimsv2.hdf5                                       -> wrimsv2.tools
  - wrimsv2.ilp                                        -> wrimsv2.commondata.solverdata
  - wrimsv2.ilp                                        -> wrimsv2.commondata.wresldata
  - wrimsv2.ilp                                        -> wrimsv2.components
  - wrimsv2.ilp                                        -> wrimsv2.evaluator
  - wrimsv2.ilp                                        -> wrimsv2.solver
  - wrimsv2.ilp                                        -> wrimsv2.solver.Gurobi
  - wrimsv2.ilp                                        -> wrimsv2.solver.mpmodel
  - wrimsv2.ilp                                        -> wrimsv2.solver.ortools
  - wrimsv2.ilp                                        -> wrimsv2.wreslparser.elements
  - wrimsv2.launch                                     -> wrimsv2.components
  - wrimsv2.launch                                     -> wrimsv2.evaluator
  - wrimsv2.launch                                     -> wrimsv2.ilp
  - wrimsv2.launch                                     -> wrimsv2.solver
  - wrimsv2.launch                                     -> wrimsv2.wreslparser.elements
  - wrimsv2.parallel                                   -> wrimsv2.commondata.wresldata
  - wrimsv2.parallel                                   -> wrimsv2.components
  - wrimsv2.parallel                                   -> wrimsv2.evaluator
  - wrimsv2.solver                                     -> wrimsv2.commondata.solverdata
  - wrimsv2.solver                                     -> wrimsv2.commondata.wresldata
  - wrimsv2.solver                                     -> wrimsv2.components
  - wrimsv2.solver                                     -> wrimsv2.evaluator
  - wrimsv2.solver                                     -> wrimsv2.ilp
  - wrimsv2.solver                                     -> wrimsv2.solver.Gurobi
  - wrimsv2.solver                                     -> wrimsv2.solver.cbc
  - wrimsv2.solver                                     -> wrimsv2.solver.clp
  - wrimsv2.solver                                     -> wrimsv2.tools
  - wrimsv2.solver                                     -> wrimsv2.wreslplus.elements
  - wrimsv2.solver.Gurobi                              -> wrimsv2.commondata.solverdata
  - wrimsv2.solver.Gurobi                              -> wrimsv2.commondata.wresldata
  - wrimsv2.solver.Gurobi                              -> wrimsv2.components
  - wrimsv2.solver.Gurobi                              -> wrimsv2.evaluator
  - wrimsv2.solver.mpmodel                             -> wrimsv2.solver.mpmodel.export
  - wrimsv2.solver.mpmodel                             -> wrimsv2.wreslparser.elements
  - wrimsv2.solver.mpmodel.export                      -> wrimsv2.commondata.solverdata
  - wrimsv2.solver.mpmodel.export                      -> wrimsv2.commondata.wresldata
  - wrimsv2.solver.mpmodel.export                      -> wrimsv2.components
  - wrimsv2.solver.mpmodel.export                      -> wrimsv2.evaluator
  - wrimsv2.solver.mpmodel.export                      -> wrimsv2.solver.mpmodel
  - wrimsv2.solver.ortools                             -> wrimsv2.commondata.solverdata
  - wrimsv2.solver.ortools                             -> wrimsv2.commondata.wresldata
  - wrimsv2.solver.ortools                             -> wrimsv2.components
  - wrimsv2.solver.ortools                             -> wrimsv2.evaluator
  - wrimsv2.solver.ortools                             -> wrimsv2.solver.mpmodel
  - wrimsv2.sql                                        -> wrimsv2.components
  - wrimsv2.sql                                        -> wrimsv2.evaluator
  - wrimsv2.sql                                        -> wrimsv2.sql.socket
  - wrimsv2.sql                                        -> wrimsv2.tools
  - wrimsv2.tools                                      -> wrimsv2.commondata.solverdata
  - wrimsv2.tools                                      -> wrimsv2.commondata.wresldata
  - wrimsv2.tools                                      -> wrimsv2.components
  - wrimsv2.tools                                      -> wrimsv2.evaluator
  - wrimsv2.tools                                      -> wrimsv2.wreslparser.elements
  - wrimsv2.tools                                      -> wrimsv2.wreslparser.grammar
  - wrimsv2.tools                                      -> wrimsv2.wreslplus.elements
  - wrimsv2.tools.solutionRangeFinder                  -> wrimsv2.solver.mpmodel
  - wrimsv2.tools.solutionRangeFinder                  -> wrimsv2.solver.ortools
  - wrimsv2.tools.solutionRangeFinder                  -> wrimsv2.wreslplus.elements
  - wrimsv2.wreslparser.elements                       -> wrimsv2.commondata.wresldata
  - wrimsv2.wreslparser.elements                       -> wrimsv2.components
  - wrimsv2.wreslparser.elements                       -> wrimsv2.evaluator
  - wrimsv2.wreslparser.elements                       -> wrimsv2.solver.mpmodel
  - wrimsv2.wreslparser.elements                       -> wrimsv2.sql.socket
  - wrimsv2.wreslparser.elements                       -> wrimsv2.tools
  - wrimsv2.wreslparser.elements                       -> wrimsv2.wreslparser.grammar
  - wrimsv2.wreslparser.elements                       -> wrimsv2.wreslplus.elements
  - wrimsv2.wreslparser.elements                       -> wrimsv2.wreslplus.elements.procedures
  - wrimsv2.wreslparser.grammar                        -> wrimsv2.commondata.wresldata
  - wrimsv2.wreslparser.grammar                        -> wrimsv2.wreslparser.elements
  - wrimsv2.wreslparser.grammar                        -> wrimsv2.wreslplus.elements
  - wrimsv2.wreslplus.elements                         -> wrimsv2.commondata.wresldata
  - wrimsv2.wreslplus.elements                         -> wrimsv2.components
  - wrimsv2.wreslplus.elements                         -> wrimsv2.config
  - wrimsv2.wreslplus.elements                         -> wrimsv2.wreslparser.elements
  - wrimsv2.wreslplus.elements                         -> wrimsv2.wreslplus.elements.procedures
  - wrimsv2.wreslplus.elements                         -> wrimsv2.wreslplus.grammar
  - wrimsv2.wreslplus.elements.procedures              -> wrimsv2.commondata.wresldata
  - wrimsv2.wreslplus.elements.procedures              -> wrimsv2.components
  - wrimsv2.wreslplus.elements.procedures              -> wrimsv2.config
  - wrimsv2.wreslplus.elements.procedures              -> wrimsv2.evaluator
  - wrimsv2.wreslplus.elements.procedures              -> wrimsv2.wreslparser.elements
  - wrimsv2.wreslplus.elements.procedures              -> wrimsv2.wreslplus.elements
  - wrimsv2.wreslplus.grammar                          -> wrimsv2.commondata.wresldata
  - wrimsv2.wreslplus.grammar                          -> wrimsv2.wreslparser.elements
  - wrimsv2.wreslplus.grammar                          -> wrimsv2.wreslplus.elements
