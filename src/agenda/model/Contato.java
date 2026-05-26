package agenda.model;

/**
 * Representa um contato da agenda telefônica.
 *
 * Campos:
 *   id       — gerado automaticamente pelo banco
 *   nome     — obrigatório
 *   telefone — obrigatório
 *   email    — opcional (pode ser null)
 */
public class Contato {

    private int    id;
    private String nome;
    private String telefone;
    private String email;

    // ── Construtor para novo contato (sem id — banco gera automaticamente) ──
    public Contato(String nome, String telefone, String email) {
        this.nome     = nome;
        this.telefone = telefone;
        this.email    = email;
    }

    // ── Construtor completo (usado ao recuperar do banco) ──
    public Contato(int id, String nome, String telefone, String email) {
        this.id       = id;
        this.nome     = nome;
        this.telefone = telefone;
        this.email    = email;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int    getId()       { return id; }
    public String getNome()     { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail()    { return email; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setId(int id)             { this.id       = id; }
    public void setNome(String nome)      { this.nome     = nome; }
    public void setTelefone(String tel)   { this.telefone = tel; }
    public void setEmail(String email)    { this.email    = email; }

    // ── Representação textual (usada no export .txt e na listagem) ────────────
    @Override
    public String toString() {
        String emailExibido = (email != null && !email.isBlank()) ? email : "(sem e-mail)";
        return String.format("[%d] %-25s  %-18s  %s", id, nome, telefone, emailExibido);
    }
}
