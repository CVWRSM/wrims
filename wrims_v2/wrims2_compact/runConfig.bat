set javahome=%~dp0\jre8_x64\bin

echo off

IF [%1]==[] (
	echo ==================================
	echo # Error: Config file path Missing
	echo ==================================
	pause
	exit
	)
	
set ConfigFilePath=%1

for %%F in (%ConfigFilePath%) do set dirname=%%~dpF
set RunDir=%dirname%run\
set temp=%dirname%
echo off


:------------------:
: wrims2 lib jars  :
:------------------:

set JarDir2=%~dp0\jars
set libDir=%~dp0\lib_64

set AppJars=%AppJars%;%JarDir2%\heclib.jar
set AppJars=%AppJars%;%JarDir2%\jnios.jar
set AppJars=%AppJars%;%JarDir2%\jpy.jar
set AppJars=%AppJars%;%JarDir2%\misc.jar
set AppJars=%AppJars%;%JarDir2%\pd.jar
set AppJars=%AppJars%;%JarDir2%\vista.jar
set AppJars=%AppJars%;%JarDir2%\commons-io-2.1.jar
set AppJars=%AppJars%;%JarDir2%\javatuples-1.2.jar
set AppJars=%AppJars%;%JarDir2%\guava-11.0.2.jar
set AppJars=%AppJars%;%JarDir2%\XAOptimizer.jar
set AppJars=%AppJars%;%JarDir2%\kryo-2.24.0.jar
set AppJars=%AppJars%;%JarDir2%\objenesis-1.2.jar
set AppJars=%AppJars%;%JarDir2%\minlog-1.2.jar
set AppJars=%AppJars%;%JarDir2%\antlr-3.5.2-runtime.jar
set AppJars=%AppJars%;%JarDir2%\coinor.jar
set AppJars=%AppJars%;%JarDir2%\sqljdbc4-2.0.jar
set AppJars=%AppJars%;%JarDir2%\mysql-connector-java-5.1.42-bin.jar
set AppJars=%AppJars%;%JarDir2%\WRIMSv2.jar


:---------------------------------:
: user defined java class and dll :
:---------------------------------:
set ExternalDir=%RunDir%External
set SupportDll=%~dp0\supporting_dlls_64

:------------:
: class path :
:------------:
set CLASSPATH=-classpath "%ExternalDir%;%AppJars%;%libDir%;%SupportDll%"

:------------:
: dll path   :
:------------:
set PATH=%ExternalDir%;%JarDir2%;%libDir%;%SupportDll%


:-------------------------------------------------------:
: call java to run ControllerBatch class                :
:-------------------------------------------------------:



%javahome%/java -Xms4096m -Xss1024K -Djava.library.path=%PATH% %CLASSPATH% wrimsv2.components.ControllerBatch -config="%configFilePath%"

rem pause
exit