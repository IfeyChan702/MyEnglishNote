REM 如果有 XML 文件
set SRC=%cd%\vue
set DEST_RES=C:\Users\51695\WebstormProjects\RuoYi-Vue3-Forks\src
if not exist "%DEST_RES%" mkdir "%DEST_RES%"
xcopy "%SRC%" "%DEST_RES%" /E /Y

echo 代码复制完成！
pause