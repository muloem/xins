; -- xins.iss --
;
; $Id$

[Setup]
AppName=XINS
AppVerName=XINS version 1.2.0-beta2
DefaultDirName={pf}\xins-1.2.0-beta2
VersionInfoVersion=1.2.0
OutputDir=c:\projects
OutputBaseFilename=xins-1.2.0-beta2
SetupIconFile=xins.ico
WizardImageFile=bigxinslogo.bmp
WizardSmallImageFile=smallxinslogo.bmp
UninstallDisplayIcon={app}\xins.ico
DefaultGroupName=Xins
DisableProgramGroupPage=yes
LicenseFile=c:\projects\xins-1.2.0-beta2\COPYRIGHT
InfoBeforeFile=xins-info1.txt

[Files]
Source: "c:\projects\xins-1.2.0-beta2\*"; DestDir: "{app}"; Flags: recursesubdirs
Source: "xins.ico"; DestDir: "{app}"

[Icons]

[Registry]
Root: HKCU; Subkey: "Environment"; ValueType: string; ValueName: "XINS_HOME"; ValueData: """{app}"""; Flags: uninsdeletevalue
Root: HKCU; Subkey: "Environment"; ValueType: expandsz; ValueName: "PATH"; ValueData: "{app}\bin;{reg:HKCU\Environment,PATH|""}"; Flags: preservestringtype

[Run]
Filename: "{app}\README.html"; Description: "View the README file."; Flags: postinstall nowait shellexec skipifsilent
Filename: "cmd"; Parameters: "/c ""set XINS_HOME=""{app}""&cd ""{app}\demo\xins-project""&""{app}\bin\xins.bat"" all-myproject&""{app}\bin\xins.bat"" -Dorg.xins.server.config=..\xins.properties  run-myproject"""; Description: "Compile and run demo."; Flags: postinstall nowait skipifsilent unchecked

[UninstallDelete]
Type: filesandordirs; Name: "{app}\demo\xins-project\build\*"
Type: dirifempty; Name: "{app}\demo\xins-project\build"
Type: dirifempty; Name: "{app}\demo\xins-project"
Type: dirifempty; Name: "{app}\demo"
Type: dirifempty; Name: "{app}"

[Code]
function ShouldSkipPage(PageID: Integer): Boolean;
begin
  { Do no show the InfoBeforeFile page if the environment variables are correct. }
  if (PageID = wpInfoBefore) and (not (GetEnv('ANT_HOME') = '')) and (not (GetEnv('JAVA_HOME') = '')) then begin
    Result := True;
  end else begin
    Result := False;
  end;
end;

