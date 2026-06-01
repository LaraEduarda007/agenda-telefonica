package agenda.dao;

import agenda.model.Contato;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsável por todas as operações no banco de dados.
 *
 * <p>Implementei aqui o CRUD completo da agenda: salvar, listar, buscar,
 * atualizar e excluir contatos. Cada método abre e fecha a própria conexão
 * usando try-with-resources, o que garante que nenhuma conexão fique aberta
 * por acidente.</p>
 *
 * @author Lara Eduarda
 */
public class ContatoDAO {

    /**
     * Salva um novo contato no banco de dados.
     *
     * @param contato o contato a ser salvo
     * @throws SQLException se ocorrer erro no banco
     */
    public void salvar(Contato contato) throws SQLException {
        String sql = "INSERT INTO contatos (nome, telefone, email) VALUES (?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, contato.getNome());
            ps.setString(2, contato.getTelefone());
            ps.setString(3, contato.getEmail());
            ps.executeUpdate();
        }
    }

    /**
     * Retorna todos os contatos cadastrados, ordenados por nome (A-Z).
     *
     * @return lista de contatos em ordem alfabética
     * @throws SQLException se ocorrer erro no banco
     */
    public List<Contato> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, telefone, email FROM contatos ORDER BY nome ASC";
        List<Contato> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Contato c = new Contato(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("telefone"),
                    rs.getString("email")
                );
                lista.add(c);
            }
        }
        return lista;
    }

    /**
     * Busca contatos pelo nome usando busca parcial (LIKE).
     * Não precisa digitar o nome completo — qualquer trecho encontra o contato.
     *
     * @param trecho parte do nome a buscar
     * @return lista de contatos encontrados, em ordem alfabética
     * @throws SQLException se ocorrer erro no banco
     */
    public List<Contato> buscarPorNome(String trecho) throws SQLException {
        String sql = "SELECT id, nome, telefone, email FROM contatos " +
                     "WHERE nome LIKE ? ORDER BY nome ASC";
        List<Contato> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + trecho + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contato c = new Contato(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email")
                    );
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    /**
     * Busca um contato pelo seu id.
     * Retorna null se nenhum contato for encontrado com esse id.
     *
     * @param id identificador do contato
     * @return o contato encontrado, ou null
     * @throws SQLException se ocorrer erro no banco
     */
    public Contato buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, telefone, email FROM contatos WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Contato(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Atualiza os dados de um contato já existente no banco.
     * Usa o id do contato para encontrar o registro correto.
     *
     * @param contato contato com os novos dados
     * @throws SQLException se ocorrer erro no banco
     */
    public void atualizar(Contato contato) throws SQLException {
        String sql = "UPDATE contatos SET nome = ?, telefone = ?, email = ? WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, contato.getNome());
            ps.setString(2, contato.getTelefone());
            ps.setString(3, contato.getEmail());
            ps.setInt(4, contato.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Remove um contato do banco pelo seu id.
     * Antes de chamar esse método, sempre peço confirmação ao usuário.
     *
     * @param id identificador do contato a ser removido
     * @throws SQLException se ocorrer erro no banco
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM contatos WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Exporta todos os contatos para um arquivo de texto (.txt).
     * O arquivo é salvo na pasta onde o programa está sendo executado.
     *
     * @param caminho caminho do arquivo a ser criado
     * @return quantidade de contatos exportados
     * @throws SQLException se ocorrer erro ao acessar o banco
     * @throws IOException  se ocorrer erro ao criar o arquivo
     */
    public int exportarTxt(String caminho) throws SQLException, IOException {
        List<Contato> lista = listarTodos();

        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("========================================");
            pw.println("         AGENDA TELEFONICA");
            pw.println("========================================");
            pw.printf("%-5s  %-25s  %-18s  %s%n", "ID", "Nome", "Telefone", "E-mail");
            pw.println("----------------------------------------");

            for (Contato c : lista) {
                String email = (c.getEmail() != null && !c.getEmail().isBlank())
                               ? c.getEmail() : "(sem e-mail)";
                pw.printf("%-5d  %-25s  %-18s  %s%n",
                    c.getId(), c.getNome(), c.getTelefone(), email);
            }

            pw.println("----------------------------------------");
            pw.printf("Total: %d contato(s)%n", lista.size());
        }

        return lista.size();
    }
}
