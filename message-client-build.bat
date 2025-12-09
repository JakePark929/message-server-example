@echo off

echo 1. Cleaning...
call gradlew.bat :message-system-client:clean

echo 2. Building...
call gradlew.bat :message-system-client:build

echo 3. Running installDist...
call gradlew.bat :message-system-client:installDist

echo All tasks completed successfully!