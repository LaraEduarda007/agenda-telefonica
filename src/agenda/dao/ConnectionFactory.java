package agenda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por criar a conexão com o banco de dados MySQL.
 *
 * <p>Centralizei as configurações de conexão aqui para facilitar a manutenção.
 * Se precisar mudar o banco, a senha ou o servidor, basta alterar as constantes
 * nessa classe — o resto do sistema não precisa ser tocado.</p>
 *
 * @author Lara Eduarda
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
