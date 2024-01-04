set javahome=%~dp0\jre8\bin
set CLP0_PATH=%~dp0\clp.exe

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
set AppJars=%AppJars%;%JarDir%\CalLiteV16.jar
set AppJars=%AppJars%;%JarDir%\kryo-2.24.0.jar
set AppJars=%AppJars%;%JarDir%\objenesis-1.2.jar
set AppJars=%AppJars%;%JarDir%\minlog-1.2.jar

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



%javahome%/java -Xmx1472m -Xss1280K -Djava.library.path=%PATH% %CLASSPATH% wrimsv2.components.ControllerBatch -config="%configFilePath%"

IF /I [%2]==[-pause] GOTO pause

exit

:pause
pause