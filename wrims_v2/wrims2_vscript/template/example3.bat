rem  Run 2 different studies simultaneously and pause after completes
rem  Delay 3 seconds to avoid jvm initiation error

start runConfig_limitedLicense.bat %~dp0\studies\callite_D1641\D1641.config -pause
timeout /t 3
start runConfig_limitedLicense.bat %~dp0\studies\callite_D1641\Test.config -pause

