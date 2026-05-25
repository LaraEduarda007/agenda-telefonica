@echo off
chcp 65001 > nul
java -cp "bin;lib\mysql-connector-j-9.7.0.jar" agenda.AgendaTeste
pause
