[Setup] 

AppName              =WRIMS2_Scripting_v1.20 
DefaultGroupName     =WRIMS2_Scripting_v1.20 
;AppVerName           =WRIMS2_Scripting
AppVersion=1.20 
UninstallDisplayName =WRIMS2_Scripting_v1.20 
AppId                =WRIMS2_Scripting_v1.20
OutputBaseFilename   =WRIMS2_Scripting_v1.20_Install
DefaultDirName       =c:\WRIMS2_Scripting_v1.20 
LicenseFile="..\license\COPYRIGHT.txt"

AppPublisher=CA_DWR
AllowNoIcons=no
;Compression=lzma2/normal
;Compression=lzma2/ultra
Compression=lzma2/fast
CompressionThreads=auto
SolidCompression=no
UninstallFilesDir={app}\bin
UninstallLogMode=new
;UninstallLogMode=append
;InfoBeforeFile=".\infoFile.rtf"
OutputDir="."
AlwaysRestart = no
;PrivilegesRequired = admin 
PrivilegesRequired = poweruser

[Languages]

Name: "english"; MessagesFile: "compiler:Default.isl"


[Dirs]

Name: "{app}\bin"
Name: "{app}\lib"
Name: "{app}\scripts"
Name: "{app}\studies"
Name: "{app}\doc"
Name: "{app}\license"


[Files]

;Source: "..\WRIMSv1_test.py"; Flags: dontcopy
;Source: "..\WRIMSv2_test.py"; Flags: dontcopy

Source: "..\*.bat";     Excludes: ".svn";     Destdir: "{app}\";     Flags: ignoreversion ;
Source: "..\*.py";     Excludes: ".svn";     Destdir: "{app}\";     Flags: ignoreversion ;
Source: "..\*.txt";     Excludes: ".svn";     Destdir: "{app}\";     Flags: ignoreversion ;

Source: "..\license\*.txt";    Excludes: ".svn";     Destdir: "{app}\license";     Flags: ignoreversion ;
Source: "..\template\*";     Excludes: ".svn";            DestDir: "{app}\template";     Flags: ignoreversion recursesubdirs createallsubdirs ; 
Source: "..\lib\misc\*";     Excludes: ".svn";            DestDir: "{app}\lib\misc";     Flags: ignoreversion recursesubdirs createallsubdirs ; 
Source: "..\lib\hecdss\*";     Excludes: ".svn";            DestDir: "{app}\lib\hecdss";     Flags: ignoreversion recursesubdirs createallsubdirs ; 
Source: "..\lib\jython252\*";  Excludes: ".svn, *.pkc";     DestDir: "{app}\lib\jython252";  Flags: ignoreversion recursesubdirs createallsubdirs ; 
;Source: "..\lib\wrims13\*";    Excludes: ".svn, xa*.dll";   DestDir: "{app}\lib\wrims13";    Flags: ignoreversion recursesubdirs createallsubdirs ; 
Source: "..\lib\wrims2\*";     Excludes: ".svn";   DestDir: "{app}\lib\wrims2";     Flags: ignoreversion recursesubdirs createallsubdirs ; 
Source: "..\lib\jre6\*";       Excludes: ".svn, ";          DestDir: "{app}\lib\jre6";       Flags: ignoreversion recursesubdirs createallsubdirs ; 

Source: "..\scripts\*";     Excludes: ".svn, *.bak, *.class, *.pyc, plot, positionAnalysis";   DestDir: "{app}\scripts\";   Flags: ignoreversion recursesubdirs createallsubdirs ; 
Source: "..\studies\*";     Excludes: ".svn, *.log, *.out, *.trc";     Destdir: "{app}\studies";    Flags: ignoreversion recursesubdirs createallsubdirs ; 
Source: "..\bin\*.cmd";     Excludes: ".svn";                          Destdir: "{app}\bin\";       Flags: ignoreversion ; 
;Source: "..\*.template.*";      Excludes: ".svn, distro";                    Destdir: "{app}\";              Flags: ignoreversion recursesubdirs createallsubdirs ; 

;Source: "..\doc\*.pdf";         Excludes: ".svn";           Destdir: "{app}\doc";              Flags: ignoreversion recursesubdirs createallsubdirs ; 


[Icons]

Name: "{app}\WRIM2_Scripting";          Filename: "{app}\bin\WRIMS2_Scripting.cmd"; WorkingDir: "{app}\bin" ;
Name: "{userdesktop}\WRIMS2_Scripting_v1.10"; Filename: "{app}" ; 


[UninstallDelete]

Type: files; Name: "{app}\*\*.dsc" ;
Type: files; Name: "{app}\*\*.dsd" ;
;Type: dirifempty; Name: "{app}\studies\*" ;
;Type: dirifempty; Name: "{app}\studies" ;
Type: filesandordirs; Name: "{app}\lib\*" ;
Type: filesandordirs; Name: "{app}\scripts\*" ;
Type: filesandordirs; Name: "{app}\scripts" ;
Type: files; Name: "{app}\*.log" ;
Type: dirifempty; Name: "{app}" ;

