@echo off
rem ��ǰbat������
 
echo ==================begin========================
 
cls
SET DIR=%~dp0/../ls-config
color 0a
TITLE EX8��Ŀ����������ļ�
 
CLS

ECHO.
ECHO. * * EX8��Ŀ����������ļ� * *

ECHO.
:ENV
    ECHO.  [1] ���ػ���
    ECHO.  [2] ��������
	ECHO.  [3] ��˾����
	ECHO.  [4] ��������
ECHO.

set pro=
set profile=
set /p profile=��������Ŀ��������:

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
    ECHO.  [1] ���ls-sys-web
    ECHO.  [2] ���ls-static-center
ECHO.

set id=
set /p id=������ѡ����Ŀ�����:
IF "%id%"=="1" (
	cd %~dp0/../ls-sys-web
    call mvn clean package -P%pro% -Dmaven.test.skip=true
) else if "%id%"=="2" (
	cd %~dp0/../ls-static-center
    call mvn clean package -P%pro% -Dmaven.test.skip=true
)
pause