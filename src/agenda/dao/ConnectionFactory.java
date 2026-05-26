package agenda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões com o banco de dados MySQL.
 *
 * Centraliza as configurações de conexão em um só lugar.
 * Para mudar o banco, basta alterar as constantes abaixo.
 */
public class ConnectionFactory {

    private static final String URL      = "jdbc:mysql://localhost:3306/agenda?useSSL=false&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO  = "root";
    private static final String SENHA    = "#Ll210500@";

    // Construtor privado — classe utilitária, não deve ser instanciada
    private ConnectionFactory() {}

    /**
     * Abre e retorna uma conexão com o banco de dados.
     *
     * @return Connection pronta para uso
     * @throws SQLException se não conseguir conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
