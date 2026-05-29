package agenda.service;

import agenda.Cores;
import agenda.dao.ContatoDAO;
import agenda.model.Contato;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// Classe responsável pelo menu e interação com o usuário
public class AgendaTelefonica {

    private ContatoDAO dao = new ContatoDAO();
    private Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        Cores.titulo("BEM-VINDA À AGENDA TELEFÔNICA");

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    adicionarContato();
                    break;
                case "2":
                    listarContatos();
                    break;
                case "3":
                    buscarContato();
                    break;
                case "4":
                    editarContato();
                    break;
                case "5":
                    excluirContato();
                    break;
                case "6":
                    exportarContatos();
                    break;
                case "0":
                    rodando = false;
                    break;
                default:
                    Cores.atencao("Opção inválida. Digite um número de 0 a 6.");
            }
        }

        Cores.println(Cores.LILAS, "\nAté logo! Sua agenda foi salva com segurança. 💜");
        scanner.close();
    }

    private void exibirMenu() {
        System.out.println();
        Cores.separador();
        Cores.println(Cores.LILAS, "  O QUE DESEJA FAZER?");
        Cores.separador();
        System.out.println("  1 → Adicionar contato");
        System.out.println("  2 → Listar todos os contatos");
        System.out.println("  3 → Buscar contato por nome");
        System.out.println("  4 → Editar contato");
        System.out.println("  5 → Excluir contato");
        System.out.println("  6 → Exportar agenda para .txt");
        System.out.println("  0 → Sair");
        Cores.separador();
        Cores.print(Cores.BRANCO, "  Sua escolha: ");
    }

    // opção 1 - adicionar novo contato
    private void adicionarContato() {
        Cores.titulo("NOVO CONTATO");

        String nome = lerCampoObrigatorio("Nome completo: ");
        if (nome == null) return;

        try {
            while (dao.nomeJaExiste(nome, 0)) {
                Cores.atencao("Já existe um contato com o nome \"" + nome + "\". Digite outro nome.");
                nome = lerCampoObrigatorio("Nome completo: ");
                if (nome == null) return;
            }
        } catch (SQLException e) {
            Cores.erro("Erro ao verificar nome: " + e.getMessage());
            return;
        }

        String telefone = lerTelefone();
        if (telefone == null) return;

        String email = lerEmail();

        try {
            dao.salvar(new Contato(nome, telefone, email));
            Cores.sucesso("Contato \"" + nome + "\" salvo com sucesso!");
        } catch (SQLException e) {
            Cores.erro("Não foi possível salvar. Detalhes: " + e.getMessage());
        }
    }

    // opção 2 - listar todos os contatos em ordem alfabética
    private void listarContatos() {
        Cores.titulo("TODOS OS CONTATOS");
        try {
            List<Contato> lista = dao.listarTodos();
            if (lista.isEmpty()) {
                Cores.atencao("Nenhum contato cadastrado ainda.");
            } else {
                imprimirCabecalho();
                for (Contato c : lista) {
                    System.out.println("  " + c);
                }
                Cores.separador();
                Cores.println(Cores.AMARELO, "  Total: " + lista.size() + " contato(s)");
            }
        } catch (SQLException e) {
            Cores.erro("Erro ao buscar contatos: " + e.getMessage());
        }
    }

    // opção 3 - buscar contato por parte do nome
    private void buscarContato() {
        Cores.titulo("BUSCAR CONTATO");
        Cores.print(Cores.BRANCO, "Digite parte do nome: ");
        String trecho = scanner.nextLine().trim();

        if (trecho.isBlank()) {
            Cores.atencao("Digite ao menos uma letra para buscar.");
            return;
        }

        try {
            List<Contato> resultado = dao.buscarPorNome(trecho);
            if (resultado.isEmpty()) {
                Cores.atencao("Nenhum contato encontrado com \"" + trecho + "\".");
            } else {
                imprimirCabecalho();
                for (Contato c : resultado) {
                    System.out.println("  " + c);
                }
                Cores.separador();
                Cores.println(Cores.AMARELO, "  " + resultado.size() + " resultado(s) encontrado(s).");
            }
        } catch (SQLException e) {
            Cores.erro("Erro na busca: " + e.getMessage());
        }
    }

    // opção 4 - editar contato pelo id ou nome
    private void editarContato() {
        Cores.titulo("EDITAR CONTATO");

        try {
            Contato contato = resolverContato("editar");
            if (contato == null) return;

            Cores.println(Cores.LILAS, "Contato atual: " + contato);
            Cores.println(Cores.AMARELO, "  (Pressione Enter para manter o valor atual | Digite 0 para cancelar a edição)");
            System.out.println();

            // --- NOME ---
            String nome = editarNome(contato);
            if (nome == null) return; // cancelou
            contato.setNome(nome);

            // --- TELEFONE ---
            String tel = editarTelefone(contato);
            if (tel == null) return; // cancelou
            contato.setTelefone(tel);

            // --- E-MAIL ---
            String email = editarEmail(contato);
            if (email == null && emailEditadoComCancelamento) return; // cancelou
            contato.setEmail(email);

            dao.atualizar(contato);
            Cores.sucesso("Contato atualizado com sucesso!");

        } catch (SQLException e) {
            Cores.erro("Erro ao editar: " + e.getMessage());
        }
    }

    // flag usada para distinguir "email deixado em branco" de "cancelamento"
    private boolean emailEditadoComCancelamento = false;

    private String editarNome(Contato contato) throws SQLException {
        while (true) {
            Cores.print(Cores.BRANCO, "Novo nome [" + contato.getNome() + "]: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equals("0")) {
                Cores.atencao("Edição cancelada.");
                return null;
            }
            if (entrada.isBlank()) {
                // Enter sem digitar = manter o atual
                return contato.getNome();
            }
            if (dao.nomeJaExiste(entrada, contato.getId())) {
                Cores.atencao("Já existe um contato com o nome \"" + entrada + "\". Digite outro nome.");
                continue;
            }
            return entrada;
        }
    }

    private String editarTelefone(Contato contato) {
        while (true) {
            Cores.print(Cores.BRANCO, "Novo telefone [" + contato.getTelefone() + "]: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equals("0")) {
                Cores.atencao("Edição cancelada.");
                return null;
            }
            if (entrada.isBlank()) {
                return contato.getTelefone();
            }
            String apenasDigitos = entrada.replaceAll("[^0-9]", "");
            if (entrada.matches("[0-9()\\-\\s+]+") && apenasDigitos.length() >= 8) {
                return entrada;
            }
            Cores.atencao("Telefone inválido. Use apenas números, parênteses, hífen ou espaço (mínimo 8 dígitos).");
        }
    }

    private String editarEmail(Contato contato) {
        emailEditadoComCancelamento = false;
        String emailAtual = contato.getEmail() != null ? contato.getEmail() : "(sem e-mail)";
        while (true) {
            Cores.print(Cores.BRANCO, "Novo e-mail [" + emailAtual + "]: ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equals("0")) {
                emailEditadoComCancelamento = true;
                Cores.atencao("Edição cancelada.");
                return null;
            }
            if (entrada.isBlank()) {
                // Enter = manter o atual (pode ser null)
                return contato.getEmail();
            }
            if (emailValido(entrada)) {
                return entrada;
            }
            Cores.atencao("E-mail inválido. Digite um e-mail com @ e domínio (ex: nome@email.com) ou pressione Enter para manter o atual.");
        }
    }

    // opção 5 - excluir contato (pede confirmação antes)
    private void excluirContato() {
        Cores.titulo("EXCLUIR CONTATO");

        try {
            Contato contato = resolverContato("excluir");
            if (contato == null) return;

            Cores.println(Cores.VERMELHO, "Você está prestes a excluir:");
            System.out.println("  " + contato);
            System.out.println();
            Cores.print(Cores.AMARELO, "Confirma a exclusão? (s/N): ");
            String confirmacao = scanner.nextLine().trim();

            if (confirmacao.equalsIgnoreCase("s")) {
                dao.excluir(contato.getId());
                Cores.sucesso("Contato \"" + contato.getNome() + "\" excluído.");
            } else {
                Cores.atencao("Exclusão cancelada.");
            }

        } catch (SQLException e) {
            Cores.erro("Erro ao excluir: " + e.getMessage());
        }
    }

    // opção 6 - exportar todos os contatos para arquivo .txt
    private void exportarContatos() {
        Cores.titulo("EXPORTAR AGENDA");
        String caminho = "contatos.txt";

        try {
            int total = dao.exportarTxt(caminho);
            Cores.sucesso(total + " contato(s) exportado(s) para \"" + caminho + "\".");
            Cores.println(Cores.LILAS, "O arquivo foi salvo na pasta onde o programa está rodando.");
        } catch (SQLException e) {
            Cores.erro("Erro ao acessar o banco: " + e.getMessage());
        } catch (IOException e) {
            Cores.erro("Erro ao criar o arquivo: " + e.getMessage());
        }
    }

    // localiza um contato por ID, nome, telefone ou e-mail — usado no editar e excluir
    private Contato resolverContato(String operacao) throws SQLException {
        while (true) {
            Cores.println(Cores.LILAS, "Buscar por:");
            System.out.println("  1 → ID");
            System.out.println("  2 → Nome");
            System.out.println("  3 → Número de telefone");
            System.out.println("  4 → E-mail");
            Cores.print(Cores.BRANCO, "  Opção: ");
            String opcao = scanner.nextLine().trim();

            if (opcao.equals("1")) {
                Integer id = lerId("ID do contato a " + operacao + ": ");
                if (id == null) return null;
                Contato contato = dao.buscarPorId(id);
                if (contato != null) return con