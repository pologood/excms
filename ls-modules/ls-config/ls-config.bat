@echo off
rem 当前bat的作用
 
echo ==================begin========================
 
cls
SET DIR=%~dp0/../ls-config
color 0a
TITLE EX8项目打包批处理文件
 
CLS

ECHO.
ECHO. * * EX8项目打包批处理文件 * *

ECHO.
:ENV
    ECHO.  [1] 本地环境
    ECHO.  [2] 开发环境
	ECHO.  [3] 公司环境
	ECHO.  [4] 阳江环境
ECHO.

set pro=
set profile=
set /p profile=请输入项目环境配置:

if "%profile%"=="1" (
	set pro="local"
)else if "%profile%"=="2" (
	set pro="dev"
)else if "%profile%"=="3" (
	set pro="ls"
)else if "%profile%"=="4" (
	set pro="yj"
)else (
	goto ENV
)

ECHO.
:MENU
    ECHO.  [1] 打包ls-sys-web
    ECHO.  [2] 打包ls-static-center
ECHO.

set id=
set /p id=请输入选择项目的序号:
IF "%id%"=="1" (
	cd %~dp0/../ls-sys-web
    call mvn clean package -P%pro% -Dmaven.test.skip=true
) else if "%id%"=="2" (
	cd %~dp0/../ls-static-center
    call mvn clean package -P%pro% -Dmaven.test.skip=true
)
pause