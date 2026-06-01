package agenda.dao;

import agenda.model.Contato;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe responsável por todas as operações no banco de dados
public class ContatoDAO {

    // salva um novo contato no banco
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

    // retorna todos os contatos em ordem alfabética
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

    // busca contatos pelo nome usando LIKE (busca parcial)
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

    // busca um contato pelo id, retorna null se não encontrar
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

    // atualiza os dados de um contato existente
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

    // remove um contato pelo id
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM contatos WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // exporta todos os contatos para um arquivo .txt
    public int exportarTxt(String caminho) throws SQLException, IOException {
        List<Contato> lista = listarTodos();

        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("========================================");
            pw.println("         AGENDA TELEFÔNICA");
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
