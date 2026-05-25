@echo off
chcp 65001 > nul
echo Compilando Agenda Telefonica...

javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.7.0.jar" -d bin ^
  src\agenda\Cores.java ^
  src\agenda\model\Contato.java ^
  src\agenda\dao\ConnectionFactory.java ^
  src\agenda\dao\ContatoDAO.java ^
  src\agenda\service\AgendaTelefonica.java ^
  src\agenda\AgendaTeste.java

if %errorlevel% == 0 (
    echo Compilado com sucesso!
) else (
    echo Erro na compilacao. Verifique as mensagens acima.
)
pause
