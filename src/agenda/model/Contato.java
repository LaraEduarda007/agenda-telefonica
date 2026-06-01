package agenda.model;

/**
 * Classe que representa um contato da minha agenda telefônica.
 *
 * <p>
 * Criei essa classe para guardar as informações de cada contato:
 * nome e telefone são obrigatórios, e o e-mail é opcional.
 * O id é gerado automaticamente pelo banco de dados.
 * </p>
 *
 * @author Lara Eduarda Assis
 */
public class Contato {

    private int id;
    private String nome;
    private String telefone;
    private String email;

    /**
     * Construtor que uso quando vou cadastrar um contato novo.
     * Não preciso passar o id porque o banco gera automaticamente.
     *
     * @param nome     nome completo do contato
     * @param telefone telefone do contato
     * @param email    e-mail do contato (pode ser null)
     */
    public Contato(String nome, String telefone, String email) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    /**
     * Construtor completo que uso quando recupero um contato do banco de dados.
     * Aqui o id já existe, então preciso passá-lo.
     *
     * @param id       identificador gerado pelo banco
     * @param nome     nome completo do contato
     * @param telefone telefone do contato
     * @param email    e-mail do contato (pode ser null)
     */
    public Contato(int id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String tel) {
        this.telefone = tel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ── Representação textual (usada no export .txt e na listagem) ────────────
    @Override
    public String toString() {
        String emailExibido = (email != null && !email.isBlank()) ? email : "(sem e-mail)";
        return String.format("[%d] %-25s  %-18s  %s", id, nome, telefone, emailExibido);
    }
}
