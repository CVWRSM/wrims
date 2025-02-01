@echo off
if "%~n0%~x0" == "WRIMS2_GUI_Update_and_Start.bat" (
	copy WRIMS2_GUI_Update_and_Start.bat WRIMS2_GUI_Update_and_Start_tmp.bat > update.log
	start /B WRIMS2_GUI_Update_and_Start_tmp.bat
	Exit
)
echo Reset tmp directory...
if not exist "C:\tmp\" mkdir C:\tmp
if exist "C:\tmp\releasedate.log" del C:\tmp\releasedate.log  > update.log
if exist "C:\tmp\wrims_patch_v2.2.0_basis.zip" del C:\tmp\wrims_patch_v2.2.0_basis.zip > update.log
if exist "C:\tmp\wrims_patch_v2.2.0_basis" (
	del /S /Q C:\tmp\wrims_patch_v2.2.0_basis\*.* > update.log
	rmdir /S /Q C:\tmp\wrims_patch_v2.2.0_basis   > update.log
)

echo Download the patch release date...
curl\bin\curl https://data.cnra.ca.gov/dataset/0f6b03b4-7de8-4579-8aa0-60f73d9d21fb/resource/23861121-d916-4442-94e0-1d5309c44762/download/releasedate_v2.2.0_basis.log -L --insecure -o C:\tmp\releasedate.log
echo Compare with the local release date...
FC C:\tmp\releasedate.log releasedate.log> nul
if %errorlevel% EQU 1 (
	echo Download Update Patch...
	curl\bin\curl https://data.cnra.ca.gov/dataset/0f6b03b4-7de8-4579-8aa0-60f73d9d21fb/resource/e58abe5c-005f-4f3f-9bed-87e03b4ee4f6/download/wrims_patch_v2.2.0_basis.zip -L --insecure -o C:\tmp\wrims_patch_v2.2.0_basis.zip
	echo Clear the old dropins and libs directories
    del /S /Q dropins\*.* > update.log
    del /S /Q lib\*.* > update.log
    echo Unzip the Patch and Update...
    powershell -command "Expand-Archive C:\tmp\wrims_patch_v2.2.0_basis.zip c:\tmp\wrims_patch_v2.2.0_basis"
    xcopy /S /E /Y C:\tmp\wrims_patch_v2.2.0_basis . > update.log
)
echo Start WRIMS 2 GUI
start WRIMS2_GUI_Start
Exit

