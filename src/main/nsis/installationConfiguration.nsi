;MyApp

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"
  !include ..\..\..\target\project.nsh
  !include x64.nsh
;--------------------------------
;General

  ;Name and file
  Name "${PROJECT_NAME}"
  
  ; Handled by plugin
  ;OutFile "${PROJECT_BUILD_DIR}\Setup${PROJECT_NAME}.exe"

  ;Default installation folder
  InstallDir "$LOCALAPPDATA\${PROJECT_NAME}"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\${PROJECT_NAME}" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user


;--------------------------------
;Variables

  Var StartMenuFolder
;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING
  !define MUI_HEADERIMAGE
  !define MUI_LANGDLL_REGISTRY_ENCODING UTF-8

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !define MUI_PAGE_CUSTOMFUNCTION_SHOW licpageshow
  !insertmacro MUI_PAGE_LICENSE "LICENSE"
  !insertmacro MUI_PAGE_DIRECTORY

;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${PROJECT_NAME}"
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PROJECT_NAME}"
  
  !insertmacro MUI_PAGE_INSTFILES
  !define MUI_FINISHPAGE_NOAUTOCLOSE
  !define MUI_FINISHPAGE_RUN
  !define MUI_FINISHPAGE_RUN_NOTCHECKED
  !define MUI_FINISHPAGE_RUN_TEXT "Start ${PROJECT_NAME} Now"
  !define MUI_FINISHPAGE_RUN_FUNCTION "LaunchLink"
  !define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED
  !define MUI_FINISHPAGE_SHOWREADME $INSTDIR\acw\readme.txt
  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "Spanish"

;--------------------------------
RequestExecutionLevel admin ;Require admin rights on NT6+ (When UAC is turned on)

;Installer Sections


Section "${PROJECT_NAME}" MyApp
  
  SetOutPath "$INSTDIR"
  ;ADD YOUR OWN FILES HERE...
  ; File /r ..\..\..\sgdfd-acw-dev.exe
  SetOutPath "$INSTDIR\acw\"
  File /r ..\..\..\target\getdown-stub\*.*
  File myapp.ico
  File unmsmActualizador.png
  File Readme.txt
  File /r ..\..\..\dll\*.*
  File /r ..\..\..\JRE\*.*
  
  ;Store installation folder
  WriteRegStr HKCU "Software\${PROJECT_NAME}" "" $INSTDIR
  ;Que se inicie en el navegador
  WriteRegStr HKCR "${PROJECT_NAME}" "" "URL:${PROJECT_NAME} Protocol"
  WriteRegStr HKCR "${PROJECT_NAME}" "URL Protocol" ""
  ; Crea la clave shell en acw
  WriteRegStr HKCR "${PROJECT_NAME}\shell" "" ""

  ; Crea la clave open en acw\shell
  WriteRegStr HKCR "${PROJECT_NAME}\shell\open" "" ""

  ; Crea la clave command en acw\shell\open
  WriteRegStr HKCR "${PROJECT_NAME}\shell\open\command" "" 'cmd.exe /c start "" "$INSTDIR\acw\sgdfd-acw-dev.lnk"'

  ; WriteRegStr HKCR "${PROJECT_NAME}\shell\open\command" "" '"$INSTDIR\acw\getdown.exe" "%1"'

  ; se incia junto al sistema"
  ; WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Run" "sgdfd-acw-dev" "$INSTDIR\getdown.exe"
  ;poner en en el panel de control
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "DisplayName" "sgdfd-acw-dev"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "InstallLocation" "$INSTDIR"
  
  WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}" "DisplayIcon" "$INSTDIR\acw\myapp.ico"

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

  ;Create shortcuts

  ;directorio menu de inicio
  CreateDirectory "$SMPROGRAMS\$StartMenuFolder"

  ;shortcut menu de inicio
  CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PROJECT_NAME}.lnk" "$INSTDIR\acw\getdown.exe" \
	"." \
	$INSTDIR\acw\myapp.ico 0 SW_SHOWNORMAL ALT|CONTROL|SHIFT|U "${PROJECT_NAME}"
  ;shortcut en la carpeta de instalación para el inicio desde el navegador
  CreateShortCut "$INSTDIR\acw\${PROJECT_NAME}.lnk" "$INSTDIR\acw\getdown.exe" \
	"." \
	$INSTDIR\acw\myapp.ico 0 SW_SHOWNORMAL ALT|CONTROL|SHIFT|U "${PROJECT_NAME}"
  ;shortcut para la desinstalacion del programa
  CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\acw\Uninstall.exe"

  ;Shortcut Escritorio
  CreateShortCut "$DESKTOP\${PROJECT_NAME}.lnk" "$INSTDIR\acw\getdown.exe" \
	"." \
	$INSTDIR\acw\myapp.ico 0 SW_SHOWNORMAL ALT|CONTROL|SHIFT|U "${PROJECT_NAME}"
SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_MyApp ${LANG_SPANISH} "${PROJECT_NAME}"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${MyApp} $(DESC_MyApp)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\Uninstall.exe" ; delete self
  Delete "$INSTDIR\*"
  RMDir /r "$INSTDIR\lib"
  RMDir /r "$INSTDIR\assets"
  RMDir /r "$INSTDIR\acw"

  RMDir /REBOOTOK "$INSTDIR"

  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
    
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\${PROJECT_NAME}.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"

  Delete "$DESKTOP\${PROJECT_NAME}.lnk" ; delete desktop shortcut

  DeleteRegKey HKCU "Software\${PROJECT_NAME}"
  DeleteRegKey HKCU "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${PROJECT_NAME}"
  DeleteRegKey HKCR "${PROJECT_NAME}"

  DeleteRegKey HKCR "${PROJECT_NAME}\shell\open\command"
  DeleteRegKey HKCR "${PROJECT_NAME}\shell\open"
  DeleteRegKey HKCR "${PROJECT_NAME}\shell"
  DeleteRegKey HKCR "${PROJECT_NAME}"


SectionEnd

Function licpageshow
    FindWindow $0 "#32770" "" $HWNDPARENT
    CreateFont $1 "Courier New" "$(^FontSize)"
    GetDlgItem $0 $0 1000
    SendMessage $0 ${WM_SETFONT} $1 1
FunctionEnd

Function LaunchLink
  ExecShell "" "$SMPROGRAMS\$StartMenuFolder\${PROJECT_NAME}.lnk"
FunctionEnd