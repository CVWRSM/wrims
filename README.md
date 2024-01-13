The "wrims_v2" directory here contains files and directories from the WRIMS project as it existed before the DevOps revisions were started.
All other files and folders at the root level in this branch and its descendants are structured to support GitHub-style CI/CD builds with Gradle.

Directories here:
-  .github/workflow -- home to the GitHub workflows that build WRIMS components on the GitHub site
-  buildSrc -- Holds build configuration information common to all subprojects. For specifics, see Gradle documentation https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources
-  gradle -- configurations for the gradle build, most significantly the file "libs.version.toml" which sets the version numbers for the libraries that will be retrieved from Maven Central or other artifact repositories at build time
-  wrims-core -- the gradle sub-project that produces the equivalent of the previous WRIMSv2.jar file
-  wrims_v2 -- see above

The remaining files at this level are set up to support a mult-project gradle build as described at https://docs.gradle.org/current/userguide/intro_multi_project_builds.html

Files were moved (rather than copied) from wrims_v2/wrims_v2/src to new locations in wrims-core/src so that their git histories would be preserved. Source files for non-java program components were left in their original folders, and will be moved as the project goes forward. A [README file in wrims_v2/wrims_v2/src](./wrims_v2/wrims_v2/src/README.md) lists the groups of files that were moved and left behind.
