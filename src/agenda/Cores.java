package agenda;

/**
 * Constantes de cores ANSI para o terminal.
 * Tons pastéis de rosa e lilás para títulos,
 * verde para sucesso e vermelho para erros.
 *
 * Uso:
 *   System.out.println(Cores.ROSA + "Título" + Cores.RESET);
 *   Cores.println(Cores.VERDE, "Contato salvo!");
 */
public class Cores {

    // Reset — sempre use no final para não "vazar" a cor
    public static final String RESET    = "\033[0m";

    // Rosa pastel (título principal)
    public static final String ROSA     = "\033[38;5;218m";

    // Lilás / violeta pastel (subtítulo, destaques)
    public static final String LILAS    = "\033[38;5;183m";

    // Lavanda suave (bordas, separadores)
    public static final String LAVANDA  = "\033[38;5;189m";

    // Verde pastel (sucesso, confirmação)
    public static final String VERDE    = "\033[38;5;157m";

    // Vermelho suave (erro, aviso)
    public static final String VERMELHO = "\033[38;5;210m";

    // Amarelo pastel (atenção, informações)
    public static final String AMARELO  = "\033[38;5;229m";

    // Branco brilhante (texto normal em destaque)
    public static final String BRANCO   = "\033[97m";

    // Negrito — combine com uma cor: Cores.NEGRITO + Cores.ROSA
    public static final String NEGRITO  = "\033[1m";

    // Construtor privado — classe utilitária, não precisa instanciar
    private Cores() {}

    /**
     * Imprime uma linha com a cor indicada e reseta automaticamente no final.
     *
     * Exemplo:
     *   Cores.println(Cores.ROSA, "=== Agenda Telefônica ===");
     *   Cores.println(Cores.VERDE, "Contato salvo com sucesso!");
     *
     * @param cor   Uma das constantes desta classe (ex: Cores.ROSA)
     * @param texto O texto a exibir
     */
    public static void println(String cor, String texto) {
        System.out.println(cor + texto + RESET);
    }

    /**
     * Imprime texto com a cor indicada (sem quebra de linha) e reseta.
     *
     * @param cor   Uma das constantes desta classe
     * @param texto O texto a exibir
     */
    public static void print(String cor, String texto) {
        System.out.print(cor + texto + RESET);
    }

    /**
     * Imprime um separador decorativo em lavanda.
     * Útil para dividir seções no menu.
     */
    public static void separador() {
        System.out.println(LAVANDA + "─".repeat(40) + RESET);
    }

    /**
     * Imprime uma mensagem de sucesso (verde) com prefixo ✔
     */
    public static void sucesso(String texto) {
        println(VERDE, "✔ " + texto);
    }

    /**
     * Imprime uma mensagem de erro (vermelho) com prefixo ✘
     */
    public static void erro(String texto) {
        println(VERMELHO, "✘ " + texto);
    }

    /**
     * Imprime uma mensagem de atenção (amarelo) com prefixo ⚠
     */
    public static void atencao(String texto) {
        println(AMARELO, "⚠ " + texto);
    }

    /**
     * Imprime um título centralizado em rosa e negrito,
     * entre separadores lavanda.
     */
    public static void titulo(String texto) {
        separador();
        println(NEGRITO + ROSA, "  " + texto);
        separador();
    }
}
