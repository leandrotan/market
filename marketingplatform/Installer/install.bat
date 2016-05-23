@ECHO OFF

if "%OS%" == "Windows_NT" setlocal

setlocal enabledelayedexpansion

set LIQUIBASE_HOME=%~dp0tools\liquibase
set ANT_HOME=%~dp0tools\ant

set CMD_LINE_ARGS=%1
if ""%1""=="""" goto done
shift
:setup
if ""%1""=="""" goto done
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setup
:done

"%ANT_HOME%\bin\ant.bat" -f install.xml -Denv.file=install.properties %CMD_LINE_ARGS%