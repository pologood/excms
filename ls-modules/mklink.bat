@shift 1
@echo off
CLS
color 0a
echo.
echo                          静态目录软链接
echo.
echo.
echo                        即将进行设置，是否要继续......
echo.     
echo --------------------------------------------------------------------------------
echo.
SET DIR=%~dp0\ls-sys-web\src\main\webapp\assets
SET DIR2=%~dp0\ls-front-assets\src\main\resources\assets
SET DIR3=%~dp0\ls-static-center\src\main\webapp\assets
SET /P ST=   请输入 Y (继续设置)或 N (退出设置)：
echo.
if /I "%ST%"=="Y" goto ST
if /I "%ST%"=="N" goto EX
goto EX

:ST

:regdll
echo 正在设置....
mklink /D %DIR%\common %~dp0\ls-sys-assets\src\main\resources\assets\common
mklink /D %DIR%\core %~dp0\ls-sys-assets\src\main\resources\assets\core
mklink /D %DIR%\css %~dp0\ls-sys-assets\src\main\resources\assets\css
mklink /D %DIR%\design %~dp0\ls-sys-assets\src\main\resources\assets\design
mklink /D %DIR2%\design %~dp0\ls-sys-assets\src\main\resources\assets\design
mklink /D %DIR%\font %~dp0\ls-sys-assets\src\main\resources\assets\font
mklink /D %DIR%\images %~dp0\ls-sys-assets\src\main\resources\assets\images
mklink /D %DIR%\res %~dp0\ls-sys-assets\src\main\resources\assets\res
mklink /D %DIR%\sms %~dp0\ls-sys-assets\src\main\resources\assets\sms

mklink /D %DIR%\js\mf-pattern %~dp0\ls-sys-assets\src\main\resources\assets\js\mf-pattern
mklink /D %DIR%\js\plugins %~dp0\ls-sys-assets\src\main\resources\assets\js\plugins
mklink %DIR%\js\app-min.js %~dp0\ls-sys-assets\src\main\resources\assets\js\app-min.js
mklink %DIR%\js\core.js %~dp0\ls-sys-assets\src\main\resources\assets\js\core.js
mklink %DIR%\js\core-min.js %~dp0\ls-sys-assets\src\main\resources\assets\js\core-min.js
mklink %DIR%\js\jquery.cookie.js %~dp0\ls-sys-assets\src\main\resources\assets\js\jquery.cookie.js
mklink %DIR%\js\jquery-min.js %~dp0\ls-sys-assets\src\main\resources\assets\js\jquery-min.js
mklink %DIR%\js\jt-min.js %~dp0\ls-sys-assets\src\main\resources\assets\js\jt-min.js
mklink %DIR%\js\layout.js %~dp0\ls-sys-assets\src\main\resources\assets\js\layout.js
mklink %DIR%\js\layout-min.js %~dp0\ls-sys-assets\src\main\resources\assets\js\layout-min.js
mklink %DIR%\js\myfocus-2.0.1.min.js %~dp0\ls-sys-assets\src\main\resources\assets\js\myfocus-2.0.1.min.js
mklink %DIR%\js\myfocus-min.js %~dp0\ls-sys-assets\src\main\resources\assets\js\myfocus-min.js
mklink %DIR%\js\security.js %~dp0\ls-sys-assets\src\main\resources\assets\js\security.js

mklink /D %DIR3% %~dp0\ls-front-assets\src\main\resources\assets

echo.
echo 恭喜你，设置成功！
echo.
echo ===按任意键退出===
pause>nul
exit
