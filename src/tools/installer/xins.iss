; -- xins.iss --

[Setup]
AppName=XINS
AppVerName=XINS version 1.1.0
DefaultDirName={pf}\xins-1.1.0
VersionInfoVersion=1.1.0
OutputBaseFilename=xins-1.1.0
SetupIconFile=xins.ico
WizardImageFile=bigxinslogo.bmp
WizardSmallImageFile=smallxinslogo.bmp
UninstallDisplayIcon={app}\xins.ico
DefaultGroupName=Xins
DisableProgramGroupPage=yes
LicenseFile=c:\xins-1.1.0\COPYRIGHT
InfoBeforeFile=xins-info1.txt

[Files]
Source: "c:\xins-1.1.0\*"; DestDir: "{app}"; Flags: recursesubdirs
Source: "xins.ico"; DestDir: "{app}"

[Icons]

[Registry]
Root: HKCU; Subkey: "Environment"; ValueType: string; ValueName: "XINS_HOME"; ValueData: """{app}"""; Flags: uninsdeletevalue
Root: HKCU; Subkey: "Environment"; ValueType: expandsz; ValueName: "PATH"; ValueData: "%XINS_HOME%\bin;{reg:HKCU\Environment,PATH|""}"; Flags: preservestringtype

[Run]
Filename: "{app}\README.html"; Description: "View the README file."; Flags: postinstall nowait shellexec skipifsilent
Filename: "cmd"; Parameters: "/c ""set XINS_HOME=""{app}""&cd ""{app}\demo\xins-project""&""{app}\bin\xins.bat"" all-myproject&java -Dorg.xins.server.config=..\xins.properties -Djetty.home={%JETTY_HOME} -jar {%JETTY_HOME}\start.jar ..\jetty_myproject.xml"""; Description: "Compile and run demo (using Jetty)."; Flags: postinstall nowait skipifsilent unchecked

[UninstallDelete]
Type: filesandordirs; Name: "{app}\demo\xins-project\build\*"

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

