@echo off
chcp 65001 > nul
echo ===== Shopping 一键启动 =====
echo.

REM 启动后端（新窗口）
echo [1/2] 启动后端...
start "Shopping-Backend" cmd /c "cd /d %~dp0backend && mvn spring-boot:run"

REM 等几秒让后端先跑
timeout /t 10 /nobreak > nul

REM 启动前端（新窗口）
echo [2/2] 启动前端...
start "Shopping-Frontend" cmd /c "cd /d %~dp0frontend && npm run dev"

echo.
echo 后端: http://localhost:8080
echo 前端: http://localhost:3000
echo.
pause
