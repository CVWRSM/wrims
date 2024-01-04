start %~dp0\..\..\bin\runConfig_limitedXA %~dp0\example_0_global_vars.config

rem wait 3 secs
timeout /t 3 /nobreak

start %~dp0\..\..\bin\runConfig_limitedXA %~dp0\example_1_initial_vars.config

rem wait 3 secs
timeout /t 3 /nobreak

start %~dp0\..\..\bin\runConfig_limitedXA %~dp0\example_3_conditional_file_includes.config

rem wait 3 secs
timeout /t 3 /nobreak

start %~dp0\..\..\bin\runConfig_limitedXA %~dp0\example_4_conditional_var_includes.config

