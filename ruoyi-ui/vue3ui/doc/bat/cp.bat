@echo off
REM ============================================
REM RuoYi 代码复制脚本
REM 源路径：当前目录下 \main\java\com\ruoyi\system
REM 目标路径：ruoyi-kaka-amazone 模块
REM ============================================

REM 设置源目录（当前目录下的 system）
set SRC=%cd%\main\java\com\ruoyi\system

REM 设置目标目录（你新建的模块）
set DEST=C:\Users\51695\Desktop\Ifey\IfeyCode\GitHub\NewTeam\RuoYi-Vue\ruoyi-system\src\main\java\com\ruoyi\system

echo 源目录: %SRC%
echo 目标目录: %DEST%

REM 创建目标目录（如果不存在）
if not exist "%DEST%" mkdir "%DEST%"

REM 复制 domain
xcopy "%SRC%\domain" "%DEST%\domain" /E /Y
REM 复制 mapper
xcopy "%SRC%\mapper" "%DEST%\mapper" /E /Y
REM 复制 service
xcopy "%SRC%\service" "%DEST%\service" /E /Y
REM 复制 controller
xcopy "%SRC%\controller" "%DEST%\controller" /E /Y

REM 如果有 XML 文件
set SRC=%cd%\vue
set DEST_RES=C:\Users\51695\WebstormProjects\RuoYi-Vue3-Forks\src
if not exist "%DEST_RES%" mkdir "%DEST_RES%"
xcopy "%SRC%" "%DEST_RES%" /E /Y

echo 代码复制完成！
pause
