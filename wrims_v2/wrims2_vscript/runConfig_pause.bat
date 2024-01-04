echo off

IF [%1]==[] (
	echo ==================================
	echo # Error: Config file path Missing
	echo ==================================
	pause
	exit
	)

set ConfigFilePath=%1
set ConfigFilePath=%ConfigFilePath:/=\%

for %%F in (%ConfigFilePath%) do set dirname=%%~dpF
set RunDir=%dirname%run\


echo off

:-------------------------------------------------------:
: dir for sty file generation (read by groundwater.dll) :
:-------------------------------------------------------:

set t=%ConfigFilePath%
set t=%t::=@%
set t=%t:\=$%

set temp_wrims2=%~dp0_temp\%t%

IF EXIST %temp_wrims2%\study.sty (
	del /F /Q %temp_wrims2%\study.sty
)

IF NOT EXIST %temp_wrims2% (
	mkdir %temp_wrims2%
) 

set temp_wrims2=%~dp0_temp\%t%\

:------------------:
: wrims2 lib jars  :
:------------------:
set JarDir=%~dp0%lib\wrims2
set HecJarDir=%~dp0%lib\vista_hec\lib
set AppJars=%JarDir%\WRIMSv2.jar
set AppJars=%AppJars%;%HecJarDir%\heclib.jar
set AppJars=%AppJars%;%JarDir%\jnios.jar
set AppJars=%AppJars%;%JarDir%\jpy.jar
set AppJars=%AppJars%;%JarDir%\misc.jar
set AppJars=%AppJars%;%JarDir%\pd.jar
set AppJars=%AppJars%;%JarDir%\vista.jar
set AppJars=%AppJars%;%JarDir%\commons-io-2.1.jar
set AppJars=%AppJars%;%JarDir%\javatuples-1.2.jar
set AppJars=%AppJars%;%JarDir%\guava-11.0.2.jar
set AppJars=%AppJars%;%JarDir%\XAOptimizer.jar

:---------------------------------:
: user defined java class and dll :
:---------------------------------:
set ExternalDir=%RunDir%External

:------------:
: class path :
:------------:
set CLASSPATH=-classpath "%ExternalDir%;%AppJars%"

:------------:
: dll path   :
:------------:
set PATH=%ExternalDir%;%JarDir%


:-------------------------------------------------------:
: call java to run ControllerBatch class                :
:-------------------------------------------------------:



%temp_wrims2%/../../jre/bin/java -Xmx1200m -Xss1280K -Djava.library.path=%PATH% %CLASSPATH% wrimsv2.components.ControllerBatch -config="%configFilePath%"

IF /I [%2]==[-pause] GOTO pause

pause

:pause
pause