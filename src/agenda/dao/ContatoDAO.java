package agenda.dao;

import agenda.model.Contato;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO (Data Access Object) responsável por todas as operações no banco de dados.
 *
 * <p>Implementei aqui o CRUD completo da agenda: salvar, listar, buscar, atualizar
 * e excluir contatos. Cada método abre e fecha a própria conexão usando try-with-resources,
 * o que garante que nenhuma conexão fique aberta por acidente.</p>
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
     * Busca contatos pelo telefone usando busca parcial (LIKE).
     *
     * @param trecho parte do telefone a buscar
     * @return lista de contatos encontrados, em ordem alfabética
     * @throws SQLException se ocorrer erro no banco
     */
    public List<Contato> buscarPorTelefone(String trecho) throws SQLException {
        String sql = "SELECT id, nome, telefone, email FROM contatos " +
                     "WHERE telefone LIKE ? ORDER BY nome ASC";
        List<Contato> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + trecho + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Contato(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email")
                    ));
                }
            }
        }
        return lista;
    }

    /**
     * Busca contatos pelo e-mail usando busca parcial (LIKE).
     *
     * @param trecho parte do e-mail a buscar
     * @return lista de contatos encontrados, em ordem alfabética
     * @throws SQLException se ocorrer erro no banco
     */
    public List<Contato> buscarPorEmail(String trecho) throws SQLException {
        String sql = "SELECT id, nome, telefone, email FROM contatos " +
                     "WHERE email LIKE ? ORDER BY nome ASC";
        List<Contato> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + trecho + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Contato(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email")
                    ));
                }
            }
        }
        return lista;
    }

    /**
     * Verifica se já existe outro contato com o mesmo nome (ignora maiúsculas/minúsculas).
     * O parâmetro idAtual é usado para excluir o próprio contato da verificação durante edição.
     *
     * @param nome    nome a verificar
     * @param idAtual id do contato sendo editado (passa 0 no cadastro)
     * @return true se outro contato já usa esse nome
     * @throws SQLException se ocorrer erro no banco
     */
    public boolean nomeJaExiste(String nome, int idAtual) throws SQLException {
        String sql = "SELECT COUNT(*) FROM contatos WHERE LOWER(nome) = LOWER(?) AND id <> ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setInt(2, idAtual);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
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
            try (ResultSet rs