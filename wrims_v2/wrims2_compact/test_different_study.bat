set wrims2=%~dp0\runConfig.bat

set study1="%~dp0\example\CL"
set study2="%~dp0\example\CL2"


cd %study1% 
start %wrims2% %study1%\cbc2.config
TIMEOUT /T 10

cd %study2% 
start %wrims2% %study2%\cbc.config
TIMEOUT /T 10

