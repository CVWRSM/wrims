#include "..\setup_include.iss"

[Setup]

AppPublisher=CA DWR
AppPublisherURL=http://baydeltaoffice.water.ca.gov/modeling/hydrology/CalSim/index.cfm
AppSupportURL=http://baydeltaoffice.water.ca.gov/modeling/hydrology/CalSim/index.cfm
AppUpdatesURL=http://baydeltaoffice.water.ca.gov/modeling/hydrology/CalSim/index.cfm
AllowNoIcons=yes
;Compression=lzma/ultra
Compression=lzma/fast
CompressionThreads=auto
SolidCompression=no
ChangesEnvironment=yes
UninstallFilesDir={app}\bin\uninstall
UninstallLogMode=new
;UninstallLogMode=append
InfoBeforeFile=".\infoFile.rtf"
OutputDir="."
AlwaysRestart = no
PrivilegesRequired = admin
LicenseFile="..\license\COPYRIGHT.txt"


[Languages]

Name: "english"; MessagesFile: "compiler:Default.isl"


[Tasks]

Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked


[Dirs]

Name: "{app}\bin"
Name: "{app}\jre"
Name: "{app}\lib"
Name: "{app}\svg"


[Components]

Name: "main"; Description: "Main Files"; Types: full compact custom;


[Files]

Source: "..\bin\*";        Excludes: ".svn, xa*.dll, *.py";   DestDir: "{app}\bin\";          Flags: ignoreversion recursesubdirs createallsubdirs ; Components: main
Source: "..\lib\*";        Excludes: ".svn";            DestDir: "{app}\lib\";                Flags: ignoreversion recursesubdirs createallsubdirs ; Components: main
Source: "..\svg\*";        Excludes: ".svn";            DestDir: "{app}\svg\";                Flags: ignoreversion recursesubdirs createallsubdirs ; Components: main
Source: "..\license\*";    Excludes: ".svn";            Destdir: "{app}\";                    Flags: ignoreversion recursesubdirs createallsubdirs ; Components: main
Source: "..\graphics\*";   Excludes: ".svn";  Destdir: "{app}\lib\graphics\calsim\graphics";  Flags: ignoreversion recursesubdirs createallsubdirs ; Components: main


[Icons]

Name:       "{group}\Uninstall";                    Filename: "{uninstallexe}"

[UninstallDelete]

Type: files; Name: "{app}\bin\WRIMS.bat"

[code]

procedure CurStepChanged(CurStep: TSetupStep);
var
    location: String;
    template: String;
    batchString: String;
begin

  location:=ExpandConstant('{app}\bin\WRIMS.bat');
  ///template:= '@echo off'+#13#10+'rem ###############'+#13#10
  
  template :=           '@echo off'
             + #13#10
			 + #13#10 + 'set path={app}\bin;%path%'
             + #13#10
			 + #13#10 + 'cd /d {app}\bin'
			 + #13#10
             + #13#10 + 'start {app}\jre\bin\java -Xmx512m -Dcalsim.home={app} -cp '
             +          '"'
             +          '{app}\lib\calsim.jar;'
             +          '{app}\lib\graphics\;'			 
             +          '{app}\lib\vista.jar;'
             +          '{app}\lib\Acme.jar;'
             +          '{app}\lib\COM.jar;'
             +          '{app}\lib\jpython.jar;'
             +          '{app}\lib\jhall.jar;'
             +          '{app}\lib\collections.jar;'
             +          '{app}\lib\calsim-help.jar;'
             +          '{app}\lib\data.jar;'
             +          '{app}\lib\hec.jar;'
             +          '{app}\lib\heclib.jar;'
             +          '{app}\lib\JGo.jar;'
             +          '{app}\lib\JGoSVG.jar;'
             +          '{app}\lib\msgsystem.jar;'
             +          '{app}\lib\rma.jar;'
             +          '{app}\lib\WrimsSchematic.jar;'
             +          '{app}\lib\xml.jar"'
             +          ' calsim.gui.CalsimGui %1';

  batchString := ExpandConstant(template);
  
  case CurStep of
    ssPostInstall:
        SaveStringToFile(location, batchString, False);
  end;
end;
