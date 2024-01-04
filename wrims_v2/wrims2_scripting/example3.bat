rem  Run 2 different studies in parallel
rem  Delay 3 seconds to avoid jvm initiation error

start runConfig_limitedLicense.bat D:\cvwrsm\trunk\wrims2_scripting\studies\callite_svn47\CONV\CONV_test2.conifg
timeout /t 3
start runConfig_limitedLicense.bat D:\cvwrsm\trunk\wrims2_scripting\studies\callite_svn47\CONV\CONV_test1.conifg

