# WRIMS Plugin build notes

### The plugin depends on 
1. the WRIMS Core project, which is built by the gradle sub-project wrims-core
2. hec.jar and vista.jar, which should be superseded by updated HEC dependencies to match WRIMS-core
3. jfreechart.jar and jcommon.jar. There are local copies of these at 
  - wrims_v2/wrims_plugin/lib_x64/jfreechart.jar
  - wrims_v2/wrims_plugin/lib_x64/sys/jcommon.jar<br>
but they don't match the jars in Maven Central or other repositories. Based on 
file dates in the jar and comparing decompiled sources from the local jars, this 
build was configured to use the local copies, which appear to have a feature set 
 somewhere between jfreechart v 1.0.13 and 1.0.14 and jcommon v 1.0.16 and 1.0.17. 
The jars with version numbers are available on Maven central in the jfree.org 
organization. Earlier versions of the jars are available on Maven Central in the 
jfree (not jfree.org) organization.
<br>
In order to build the plugin with the Maven Central jars, it would have been necessary to 
refactor imports in ReportPDFWriter.java from
  - `import org.jfree.chart.util.RectangleInsets;`
<br>to
  - `import org.jfree.ui.RectangleInsets;`<br>
and possibly other changes. The local jars were used to avoid this refactoring. 
This should be updated to use supported jars from Maven Central. 
4. Classes in rma.jar
5. A number of jars that support Eclipse features. Some are retrieved from Eclipse 
p2 artifact repositories using com.diffplug.p2-maven-plugin. Others are local copies in 
the wrims_plugin/plugins directory. As the devops team updates, these jars should all be 
retrieved from repositories. Once we reach java 11, it should be possible to pull all 
Eclipse dependencies from Maven Central.

## Gradle build results:
1. The plugin jar is built in wrims_plugin/build/libs/wrims_plugin-*branch-id*-SNAPSHOT.jar
2. The gradle-build jar contains only the classes in the wrimsv2_plugin package, in contrast to 
the distributed jar, which also contains a folder of icons, some .dll files, and 
internal copies of many of its dependency jars.
3. Within the wrimsv2_plugin package, the gradle build produces exactly the same set of 
classes as the HEC-DSS v6 distributed jar. None of the class files in the two jar files are
identical, however.
