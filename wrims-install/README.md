# WRIMS-GUI: The zipped distribuition builder for WRIMS-GUI
NOTE: Given the outstanding limitations of the current Eclipse Luna (does not support build-ship),
This gradle module uses an existing template copy of the WRIMS-GUI project to build a distributable zip file
with the latest updated dropin jars and configuration file required to enable them. 

## How to build the installer/patch zip files
The installer can be built by running the "zipWrimsGui" task in the wrims-install module.
The patch can be built by running the "zipWrimsPatch" task in the wrims-install module.

The generated wrims install zip file will be located in the /wrims-install/build/installer
folder. The installer file will be named: wrims_gui_x64_<branch-name>.zip

The generated wrims patch zip file will be located in the /wrims-install/build/patch
folder. The patch file will be named: wrims_patch_v2.2.0_basis.zip

### From developer linux terminal:
You can build the installer and patch by running the following linux command from the root of the project:
```
./gradlew :wrims-install:zipWrimsGui
./gradlew :wrims-install:zipWrimsPatch
```

### From developer windows command line / terminal:
You can build the installer and patch by running the following command from the root of the project:
```
gradlew.bat :wrims-install:zipWrimsGui
gradlew.bat :wrims-install:zipWrimsPatch
```

### From GitHub Releases
The installer and patch zip files are automatically generated when a new "Release" is created from 
the github wrims site (https://github.com/CentralValleyModeling/wrims).

1. From the github wrims home page (https://github.com/CentralValleyModeling/wrims/) click the "Releases" 
link on the right hand side. Direct link: https://github.com/CentralValleyModeling/wrims/releases

2. From the Releases page, select "Draft a new release" button at the top of the page.
Direct link: https://github.com/CentralValleyModeling/wrims/releases/new

3. Click the "Choose a tag" drop down and type in a new tag name in the "Find or create new tag" field. 
For an internal release, use a date stamp in the format of "YYYYMMDD" (e.g. 20241224).

4. Click the "Target" branch drop down and select the branch you'd like to build the installer from. 

5. Click "Generate release notes" to automatically pull PR change logs into the release notes. 

6. Enter a title in the "Release Title" field. Typically, this matches the tag name. 

7. (OPTIONAL) Add any additional notes to the Description field

8. If this is a non-production ready release, check the "Set as a pre-release" checkbox. 

9. Click "Publish Release"

The installer and patch zip will be automatically generated and added to the new Release. 




