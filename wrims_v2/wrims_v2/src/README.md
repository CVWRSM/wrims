Source files in this folder -- wrims_v2/wrimms_v2/src -- have been moved to wrims-core/src

- java files (\*.java) were moved to wrims-core/src/main/java/wrimsv2
  - ./main/\*.java to src/main/java/main/\*.java
  - ./serial/\*.java to src/main/java/serial/\*.java
  - ./wrimsv2/\*/\*.java to src/main/java/wrims/\*/\*.java
- antlr files (\*.g) were moved to wrims-core/src/main/java/wrimsv2
  - where the antlr files were in "grammar" folders, the structure is recreated in src/main/antlr

Test files will be moved to wrims-core/src/test/java/ as test procedures are updated

C, C++, and Fortran files will be moved to sub-projects as we move forward.

Don't lose track of src/wrimsv2/external/create_c_header.bat until we determine that it's not needed.