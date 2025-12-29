@echo off
REM ===========================================
REM Run message-system-client
REM ===========================================

echo ================================
echo Running message-system-client...
echo ================================

REM 실제 실행
call message-system-client\build\install\message-system-client\bin\message-system-client

IF %ERRORLEVEL% NEQ 0 (
    echo ❌ Execution failed!
    exit /b %ERRORLEVEL%
)