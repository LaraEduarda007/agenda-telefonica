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
            System.out.println("  (Pressione Enter para manter o valor atual)");
            System.out.println();

            System.out.print("Novo nome [" + contato.getNome() + "]: ");
            String nome = scanner.nextLine().trim();
            if (!nome.isBlank()) contato.setNome(nome);

            System.out.print("Novo telefone [" + contato.getTelefone() + "]: ");
            String tel = scanner.nextLine().trim();
            if (!tel.isBlank()) {
                if (!tel.matches("[0-9()\\-\\s+]+")) {
                    Cores.atencao("Telefone contém caracteres inválidos. Mantendo o anterior.");
                } else {
                    contato.setTelefone(tel);
                }
            }

            String emailAtual = contato.getEmail() != null ? contato.getEmail() : "(sem e-mail)";
            System.out.print("Novo e-mail [" + emailAtual + "]: ");
            String email = scanner.nextLine().trim();
            if (!email.isBlank()) {
                if (emailValido(email)) {
                    contato.setEmail(email);
                } else {
                    Cores.atencao("E-mail inválido. Mantendo o anterior.");
                }
            }

            dao.atualizar(contato);
            Cores.sucesso("Contato atualizado com sucesso!");

        } catch (SQLException e) {
            Cores.erro("Erro ao editar: " + e.getMessage());
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
                if (contato != null) return contato;
                if (!perguntarNovaBusca()) return null;
                continue;
            }

            // opções 2, 3 e 4 — busca parcial por texto
            String prompt;
            switch (opcao) {
                case "2": prompt = "Digite parte do nome: ";     break;
                case "3": prompt = "Digite parte do telefone: "; break;
                case "4": prompt = "Digite parte do e-mail: ";   break;
                default:
                    Cores.atencao("Opção inválida. Operação cancelada.");
                    return null;
            }

            Cores.print(Cores.BRANCO, prompt);
            String trecho = scanner.nextLine().trim();
            if (trecho.isBlank()) {
                Cores.atencao("Campo vazio. Operação cancelada.");
                return null;
            }

            List<Contato> resultado;
            switch (opcao) {
                case "2": resultado = dao.buscarPorNome(trecho);     break;
                case "3": resultado = dao.buscarPorTelefone(trecho); break;
                default:  resultado = dao.buscarPorEmail(trecho);    break;
            }

            if (resultado.isEmpty()) {
                if (!perguntarNovaBusca()) return null;
                continue;
            }
            if (resultado.size() == 1) {
                return resultado.get(0);
            }

            // mais de um resultado — mostra a lista e pede para escolher
            Cores.println(Cores.AMARELO, resultado.size() + " contato(s) encontrado(s):");
            imprimirCabecalho();
            for (Contato c : resultado) {
                System.out.println("  " + c);
            }
            Cores.separador();
            Integer id = lerId("Digite o ID do contato que deseja " + operacao + ": ");
            if (id == null) return null;
            for (Contato c : resultado) {
                if (c.getId() == id) return c;
            }
            Cores.atencao("ID " + id + " não está entre os resultados encontrados.");
            if (!perguntarNovaBusca()) return null;
        }
    }

    // pergunta se quer pesquisar novamente após não encontrar contato
    // retorna true = tentar de novo, false = voltar ao menu
    private boolean perguntarNovaBusca() {
        Cores.println(Cores.AMARELO, "Contato não encontrado. Deseja pesquisar novamente?");
        Cores.print(Cores.BRANCO, "Pressione Enter para voltar ao menu ou digite S para tentar novamente: ");
        String resp = scanner.nextLine().trim();
        return res