package agenda.service;

import agenda.Cores;
import agenda.dao.ContatoDAO;
import agenda.model.Contato;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pelo menu interativo e pela interação com o usuário.
 *
 * <p>É aqui que o sistema "conversa" com quem está usando a agenda.
 * Cada opção do menu chama um método específico, que por sua vez
 * usa o {@link agenda.dao.ContatoDAO} para acessar o banco de dados.</p>
 *
 * @author Lara Eduarda
 */
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
                    Cores.atencao("Opcao invalida. Digite um numero de 0 a 6.");
            }
        }

        Cores.println(Cores.LILAS, "\nAte logo! Sua agenda foi salva com seguranca.");
        scanner.close();
    }

    private void exibirMenu() {
        System.out.println();
        Cores.separador();
        Cores.println(Cores.LILAS, "  O QUE DESEJA FAZER?");
        Cores.separador();
        System.out.println("  1 - Adicionar contato");
        System.out.println("  2 - Listar todos os contatos");
        System.out.println("  3 - Buscar contato por nome");
        System.out.println("  4 - Editar contato");
        System.out.println("  5 - Excluir contato");
        System.out.println("  6 - Exportar agenda para .txt");
        System.out.println("  0 - Sair");
        Cores.separador();
        Cores.print(Cores.BRANCO, "  Sua escolha: ");
    }

    // opcao 1 - adicionar novo contato
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
            Cores.erro("Nao foi possivel salvar. Detalhes: " + e.getMessage());
        }
    }

    // opcao 2 - listar todos os contatos em ordem alfabetica
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

    // opcao 3 - buscar contato por parte do nome
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

    // opcao 4 - editar contato pelo id ou nome
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
                    Cores.atencao("Telefone invalido. Mantendo o anterior.");
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
                    Cores.atencao("E-mail invalido. Mantendo o anterior.");
                }
            }

            dao.atualizar(contato);
            Cores.sucesso("Contato atualizado com sucesso!");

        } catch (SQLException e) {
            Cores.erro("Erro ao editar: " + e.getMessage());
        }
    }

    // opcao 5 - excluir contato (pede confirmacao antes)
    private void excluirContato() {
        Cores.titulo("EXCLUIR CONTATO");

        try {
            Contato contato = resolverContato("excluir");
            if (contato == null) return;

            Cores.println(Cores.VERMELHO, "Voce esta prestes a excluir:");
            System.out.println("  " + contato);
            System.out.println();
            Cores.print(Cores.AMARELO, "Confirma a exclusao? (s/N): ");
            String confirmacao = scanner.nextLine().trim();

            if (confirmacao.equalsIgnoreCase("s")) {
                dao.excluir(contato.getId());
                Cores.sucesso("Contato \"" + contato.getNome() + "\" excluido.");
            } else {
                Cores.atencao("Exclusao cancelada.");
            }

        } catch (SQLException e) {
            Cores.erro("Erro ao excluir: " + e.getMessage());
        }
    }

    // opcao 6 - exportar todos os contatos para arquivo .txt
    private void exportarContatos() {
        Cores.titulo("EXPORTAR AGENDA");
        String caminho = "contatos.txt";

        try {
            if (dao.listarTodos().isEmpty()) {
                Cores.atencao("Nao ha contatos para exportar.");
                return;
            }
            int total = dao.exportarTxt(caminho);
            Cores.sucesso(total + " contato(s) exportado(s) para \"" + caminho + "\".");
            Cores.println(Cores.LILAS, "O arquivo foi salvo na pasta onde o programa esta rodando.");
        } catch (SQLException e) {
            Cores.erro("Erro ao acessar o banco: " + e.getMessage());
        } catch (IOException e) {
            Cores.erro("Erro ao criar o arquivo: " + e.getMessage());
        }
    }

    // localiza um contato por ID ou por busca parcial de nome — usado no editar e excluir
    private Contato resolverContato(String operacao) throws SQLException {
        Cores.println(Cores.LILAS, "Buscar por:");
        System.out.println("  1 - ID");
        System.out.println("  2 - Nome");
        Cores.print(Cores.BRANCO, "  Opcao: ");
        String opcao = scanner.nextLine().trim();

        if (opcao.equals("1")) {
            Integer id = lerId("ID do contato a " + operacao + ": ");
            if (id == null) return null;
            Contato contato = dao.buscarPorId(id);
            if (contato == null) {
                Cores.atencao("Nenhum contato com ID " + id + " foi encontrado.");
            }
            return contato;

        } else if (opcao.equals("2")) {
            Cores.print(Cores.BRANCO, "Digite parte do nome: ");
            String trecho = scanner.nextLine().trim();
            if (trecho.isBlank()) {
                Cores.atencao("Digite ao menos uma letra para buscar.");
                return null;
            }
            List<Contato> resultado = dao.buscarPorNome(trecho);
            if (resultado.isEmpty()) {
                Cores.atencao("Nenhum contato encontrado com \"" + trecho + "\".");
                return null;
            }
            if (resultado.size() == 1) {
                return resultado.get(0);
            }
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
            Cores.atencao("ID " + id + " nao esta entre os resultados encontrados.");
            return null;

        } else {
            Cores.atencao("Opcao invalida. Operacao cancelada.");
            return null;
        }
    }

    // le um campo obrigatorio — pede de novo se vazio, ou cancela se digitar "0"
    private String lerCampoObrigatorio(String prompt) {
        while (true) {
            Cores.print(Cores.BRANCO, prompt);
            Cores.println(Cores.AMARELO, " (digite 0 para cancelar e voltar ao menu)");
            Cores.print(Cores.BRANCO, "> ");
            String valor = scanner.nextLine().trim();
            if (valor.equals("0")) {
                Cores.atencao("Operacao cancelada.");
                return null;
            }
            if (!valor.isBlank()) return valor;
            Cores.atencao("Este campo e obrigatorio. Tente novamente.");
        }
    }

    // le e valida o telefone — so aceita digitos, parenteses, hifen, espaco e +, minimo 8 digitos
    private String lerTelefone() {
        while (true) {
            Cores.print(Cores.BRANCO, "Telefone (ex: (62) 99000-0000): ");
            String tel = scanner.nextLine().trim();
            if (tel.isBlank()) {
                Cores.atencao("Telefone e obrigatorio. Tente novamente.");
                continue;
            }
            if (tel.equals("0")) {
                Cores.atencao("Operacao cancelada.");
                return null;
            }
            long digitos = tel.chars().filter(Character::isDigit).count();
            if (!tel.matches("[0-9()\\-\\s+]+") || digitos < 8) {
                Cores.atencao("Telefone invalido. Use apenas numeros, parenteses, hifen ou espaco (minimo 8 digitos).");
                continue;
            }
            return tel;
        }
    }

    // le o e-mail (campo opcional) — aceita vazio ou valida o formato
    private String lerEmail() {
        while (true) {
            Cores.print(Cores.BRANCO, "E-mail (opcional, pressione Enter para pular): ");
            String email = scanner.nextLine().trim();
            if (email.isBlank()) return null;
            if (emailValido(email)) return email;
            Cores.atencao("E-mail invalido. Digite um e-mail com @ e dominio (ex: nome@email.com) ou pressione Enter para pular.");
        }
    }

    // valida formato de e-mail
    private boolean emailValido(String email) {
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    // le e valida um ID numerico
    private Integer lerId(String prompt) {
        while (true) {
            Cores.print(Cores.BRANCO, prompt);
            Cores.println(Cores.AMARELO, " (digite 0 para cancelar)");
            Cores.print(Cores.BRANCO, "> ");
            String entrada = scanner.nextLine().trim();
            if (entrada.equals("0")) {
                Cores.atencao("Operacao cancelada.");
                return null;
            }
            try {
                int id = Integer.parseInt(entrada);
                if (id > 0) return id;
                Cores.atencao("Digite um ID valido (numero maior que zero).");
            } catch (NumberFormatException e) {
                Cores.atencao("Digite apenas numeros.");
            }
        }
    }

    // imprime o cabecalho da tabela de contatos
    private void imprimirCabecalho() {
        Cores.separador();
        Cores.println(Cores.AMARELO, String.format("  %-5s  %-25s  %-18s  %s", "ID", "Nome", "Telefone", "E-mail"));
        Cores.separador();
    }
}
