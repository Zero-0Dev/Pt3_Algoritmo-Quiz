/**
 * Integrantes
 *
 * 
 * Carlos Eduardo Gomes da Silva
 * Felipe Maeta dos Santos
 * João Felipe de Melo Nascimento
 * João Vitor Pires Leite
 * Kaito Kitamura
 * Vinicius Gonçalves Lima.
 */

import java.io.*;
import java.util.Scanner;

public class QuizPROJ {

    // ----- ARRAYS SIMPLES PARA PERGUNTAS -----
    static String[] perguntas = new String[200];
    static String[] altA = new String[200];
    static String[] altB = new String[200];
    static String[] altC = new String[200];
    static String[] altD = new String[200];
    static String[] corretas = new String[200];
    static int[] niveis = new int[200];
    static int total = 0; // quantas perguntas existem

    // ----- ARRAYS PARA RANKING LOCAL -----
    static String[] rankingNome = new String[50];
    static int[] rankingPontos = new int[50];
    static long[] rankingTempo = new long[50]; // tempo em segundos
    static int rankingCount = 0;

    // ----- CAMINHO FIXO DO ARQUIVO DENTRO DA PASTA -----
    static final String ARQUIVO = "quiz.txt";

    // ----- MAIN: carrega arquivo e mostra menu principal -----
    public static void main(String[] args) throws UnsupportedEncodingException {
        // Faz o console imprimir com UTF-8 (acentos funcionando)
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));

        // Scanner configurado para ler UTF-8 (nome do usuário, perguntas, etc.)
        Scanner entrada = new Scanner(new InputStreamReader(System.in, java.nio.charset.StandardCharsets.UTF_8));

        // Carrega perguntas do arquivo quiz.txt logo no início
        carregarDoArquivo();

        int op = 0;

        // Loop do menu principal (só sai quando escolher 3)
        while (op != 3) {
            System.out.println("\n==== MENU PRINCIPAL ====");
            System.out.println("1 - Jogar");
            System.out.println("2 - Gerenciar Perguntas");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");

            try {
                op = Integer.parseInt(entrada.nextLine());
            } catch (Exception e) {
                op = 0; // evita crash caso digite algo errado
            }

            if (op == 1) {
                jogar(entrada);
            } else if (op == 2) {
                gerenciar(entrada);
            } else if (op == 3) {
                System.out.println("Saindo...");
            } else {
                System.out.println("Opção inválida!");
            }
        }

        entrada.close(); // fecha o scanner ao final
    }


    // ================================================================
    // CARREGA PERGUNTAS DO ARQUIVO quiz.txt
    // Cada linha: nivel#pergunta#A#B#C#D#correta
    // ================================================================
    public static void carregarDoArquivo() {
        total = 0;

        File f = new File(ARQUIVO);

        // Se o arquivo não existir, cria um vazio para evitar erros
        if (!f.exists()) {
            try {
                File dir = f.getParentFile();
                if (dir != null && !dir.exists()) dir.mkdirs();
                f.createNewFile(); // cria quiz.txt
            } catch (IOException e) {
                System.out.println("Não foi possível criar o arquivo " + ARQUIVO);
                return;
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String linha;

            // Lê cada linha do arquivo
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue; // pula linhas vazias

                // Divide a linha pelo caractere #
                String[] partes = linha.split("#");

                // Formato correto tem 7 partes
                if (partes.length >= 7) {

                    // Converte nível para número
                    try {
                        niveis[total] = Integer.parseInt(partes[0]);
                    } catch (Exception e) {
                        niveis[total] = 1; // usa nível 1 caso dê erro
                    }

                    // Guarda a pergunta e alternativas
                    perguntas[total] = partes[1];
                    altA[total] = partes[2];
                    altB[total] = partes[3];
                    altC[total] = partes[4];
                    altD[total] = partes[5];
                    corretas[total] = partes[6];

                    total++; // soma 1 pergunta

                    // Evita estourar o tamanho do array
                    if (total >= perguntas.length) break;
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }


    // ================================================================
    // SALVA TODAS AS PERGUNTAS NO ARQUIVO (sobrescreve tudo)
    // ================================================================
    public static void salvarNoArquivo() {
        File f = new File(ARQUIVO);
        try {
            // garante que a pasta src exista
            File dir = f.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
                for (int i = 0; i < total; i++) {
                    // escreve linha: nivel # pergunta#A#B#C#D#correta
                    bw.write(niveis[i] + "#" +
                            perguntas[i] + "#" +
                            altA[i] + "#" +
                            altB[i] + "#" +
                            altC[i] + "#" +
                            altD[i] + "#" +
                            corretas[i]);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // ================================================================
    // MENU DE GERENCIAMENTO (ADICIONAR / ALTERAR / EXCLUIR / LISTAR)
    // ================================================================
    public static void gerenciar(Scanner entrada) {
        int op = 0;
        while (op != 5) {
            System.out.println("\n==== GERENCIAR PERGUNTAS ====");
            System.out.println("1 - Adicionar");
            System.out.println("2 - Alterar");
            System.out.println("3 - Excluir");
            System.out.println("4 - Listar");
            System.out.println("5 - Voltar");
            System.out.print("Escolha: ");
            try {
                op = Integer.parseInt(entrada.nextLine());
            } catch (Exception e) {
                op = 0;
            }

            if (op == 1) adicionar(entrada);
            else if (op == 2) alterar(entrada);
            else if (op == 3) excluir(entrada);
            else if (op == 4) listar();
            else if (op == 5) {
                // volta ao menu principal
                // recarrega as perguntas do arquivo para garantir sincronização
                carregarDoArquivo();
                return;
            } else {
                System.out.println("Opção inválida!");
            }
        }
    }

    // ================================================================
    // ADICIONAR PERGUNTA -> adiciona ao array e salva o arquivo
    // ================================================================
    public static void adicionar(Scanner entrada) {
        if (total >= perguntas.length) {
            System.out.println("Limite de perguntas atingido.");
            return;
        }

        System.out.print("Pergunta: ");
        String p = entrada.nextLine();
        System.out.print("A: ");
        String a = entrada.nextLine();
        System.out.print("B: ");
        String b = entrada.nextLine();
        System.out.print("C: ");
        String c = entrada.nextLine();
        System.out.print("D: ");
        String d = entrada.nextLine();
        System.out.print("Correta (a/b/c/d): ");
        String cor = entrada.nextLine();
        System.out.print("Nível (1-5): ");
        int nivel = 1;
        try {
            nivel = Integer.parseInt(entrada.nextLine());
            if (nivel < 1 || nivel > 5) nivel = 1;
        } catch (Exception e) {
            nivel = 1;
        }

        perguntas[total] = p;
        altA[total] = a;
        altB[total] = b;
        altC[total] = c;
        altD[total] = d;
        corretas[total] = cor;
        niveis[total] = nivel;
        total++;

        // salva tudo no arquivo para manter consistência
        salvarNoArquivo();
        System.out.println("Pergunta adicionada e salva em " + ARQUIVO);
    }

    // ================================================================
    // ALTERAR PERGUNTA -> edita o array e salva o arquivo
    // ================================================================
    public static void alterar(Scanner entrada) {
        listar();
        System.out.print("Número da pergunta para alterar: ");
        int idx = -1;
        try {
            idx = Integer.parseInt(entrada.nextLine()) - 1;
        } catch (Exception e) {
            System.out.println("Entrada inválida.");
            return;
        }

        if (idx < 0 || idx >= total) {
            System.out.println("Índice inválido.");
            return;
        }

        System.out.print("Nova pergunta (Enter = manter): ");
        String novo = entrada.nextLine();
        if (!novo.equals("")) perguntas[idx] = novo;

        System.out.print("Nova A (Enter = manter): ");
        novo = entrada.nextLine();
        if (!novo.equals("")) altA[idx] = novo;

        System.out.print("Nova B (Enter = manter): ");
        novo = entrada.nextLine();
        if (!novo.equals("")) altB[idx] = novo;

        System.out.print("Nova C (Enter = manter): ");
        novo = entrada.nextLine();
        if (!novo.equals("")) altC[idx] = novo;

        System.out.print("Nova D (Enter = manter): ");
        novo = entrada.nextLine();
        if (!novo.equals("")) altD[idx] = novo;

        System.out.print("Nova correta (Enter = manter): ");
        novo = entrada.nextLine();
        if (!novo.equals("")) corretas[idx] = novo;

        System.out.print("Novo nível (Enter = manter): ");
        novo = entrada.nextLine();
        if (!novo.equals("")) {
            try {
                int n = Integer.parseInt(novo);
                if (n >= 1 && n <= 5) niveis[idx] = n;
            } catch (Exception e) {
                // ignora
            }
        }

        salvarNoArquivo();
        System.out.println("Alteração salva em " + ARQUIVO);
    }

    // ================================================================
    // EXCLUIR PERGUNTA -> remove do array e salva o arquivo
    // ================================================================
    public static void excluir(Scanner entrada) {
        listar();
        System.out.print("Número da pergunta para excluir: ");
        int idx = -1;
        try {
            idx = Integer.parseInt(entrada.nextLine()) - 1;
        } catch (Exception e) {
            System.out.println("Entrada inválida.");
            return;
        }

        if (idx < 0 || idx >= total) {
            System.out.println("Índice inválido.");
            return;
        }

        // arrasta elementos para "fechar o buraco"
        for (int i = idx; i < total - 1; i++) {
            perguntas[i] = perguntas[i + 1];
            altA[i] = altA[i + 1];
            altB[i] = altB[i + 1];
            altC[i] = altC[i + 1];
            altD[i] = altD[i + 1];
            corretas[i] = corretas[i + 1];
            niveis[i] = niveis[i + 1];
        }
        total--;

        salvarNoArquivo();
        System.out.println("Pergunta excluída e arquivo atualizado.");
    }

    // ================================================================
    // LISTAR PERGUNTAS (mostra índice e nível)
    // ================================================================
    public static void listar() {
        System.out.println("\n==== PERGUNTAS ====");
        if (total == 0) {
            System.out.println("(Nenhuma pergunta cadastrada)");
            return;
        }
        for (int i = 0; i < total; i++) {
            System.out.println((i + 1) + ") [Nível " + niveis[i] + "] " + perguntas[i]);
        }
    }

    // ================================================================
    // JOGAR O QUIZ (registra ponto e tempo, adiciona ao ranking)
    // ================================================================
    public static void jogar(Scanner entrada) {
        if (total == 0) {
            System.out.println("Não há perguntas cadastradas. Use o gerenciar para adicionar.");
            return;
        }

        System.out.print("Digite seu nome: ");
        String nome = entrada.nextLine();

        int pontos = 1000;
        long inicio = System.currentTimeMillis();

        System.out.println("\n==== INICIANDO QUIZ ====");

        for (int nivel = 1; nivel <= 5; nivel++) {

            System.out.println("\n--- NÍVEL " + nivel + " ---");

            // Vetor para guardar os índices das perguntas deste nível
            int[] indices = new int[total];
            int qtd = 0; // quantas perguntas desse nível existem

            // Procura perguntas do nível atual
            for (int i = 0; i < total; i++) {
                if (niveis[i] == nivel) {
                    indices[qtd] = i;
                    qtd++;
                }
            }

            // Se não existe pergunta nesse nível, avisa e pula pro próximo
            if (qtd == 0) {
                System.out.println("(Sem perguntas neste nível)");
                continue;
            }

            // EMBARALHA OS ÍNDICES — Fisher–Yates básico
            for (int i = 0; i < qtd; i++) {
                int ale = (int) (Math.random() * qtd); // posição aleatória
                int temp = indices[i];
                indices[i] = indices[ale];
                indices[ale] = temp;
            }

            // Agora roda as perguntas na ordem sorteada
            for (int k = 0; k < qtd; k++) {

                int i = indices[k]; // índice da pergunta real

                System.out.println("====================================");
                System.out.println(perguntas[i]);
                System.out.println("a) " + altA[i]);
                System.out.println("b) " + altB[i]);
                System.out.println("c) " + altC[i]);
                System.out.println("d) " + altD[i]);

                // Loop que força resposta válida
                String r = "";
                while (true) {
                    System.out.print("Resposta (a/b/c/d): ");
                    r = entrada.nextLine().trim().toLowerCase();

                    // Confirma se digitou algo permitido
                    if (r.equals("a") || r.equals("b") || r.equals("c") || r.equals("d"))
                        break;

                    // Caso erre, repete as alternativas
                    System.out.println("\nOpção inválida! Digite apenas a, b, c ou d.\n");
                    System.out.println("a) " + altA[i]);
                    System.out.println("b) " + altB[i]);
                    System.out.println("c) " + altC[i]);
                    System.out.println("d) " + altD[i]);
                }

                // Se errou, perde pontos
                if (!r.equalsIgnoreCase(corretas[i])) {
                    pontos -= 50;
                }
            }

            // Regra das fases: nível 5 só libera com 800 pontos
            if (nivel == 4 && pontos < 800) {
                System.out.println("\nNível 5 bloqueado (precisa de 800 pontos).");
                break;
            }
        }


        long fim = System.currentTimeMillis();
        long tempoSeg = (fim - inicio) / 1000;

        System.out.println("\n=== RESULTADO ===");
        System.out.println("Nome: " + nome);
        System.out.println("Pontos: " + pontos);
        System.out.println("Tempo (s): " + tempoSeg);

        // adiciona ao ranking local
        if (rankingCount < rankingNome.length) {
            rankingNome[rankingCount] = nome;
            rankingPontos[rankingCount] = pontos;
            rankingTempo[rankingCount] = tempoSeg;
            rankingCount++;
        } else {
            // se encher, substitui o último (simples)
            rankingNome[rankingCount - 1] = nome;
            rankingPontos[rankingCount - 1] = pontos;
            rankingTempo[rankingCount - 1] = tempoSeg;
        }

        // mostra o pódio (top 3) ordenado por pontos desc e tempo asc
        mostrarPodio();
    }

    // ================================================================
    // MOSTRA PÓDIO: ordena índice por pontos desc, tempo asc e imprime top 3
    // ================================================================
    public static void mostrarPodio() {

        if (rankingCount == 0) {
            System.out.println("Nenhum resultado no ranking ainda.");
            return;
        }

        // Vetor com índices para ordenar sem estragar os arrays originais
        Integer[] idx = new Integer[rankingCount];
        for (int i = 0; i < rankingCount; i++)
            idx[i] = i;

        // Ordenação usando lambda
        java.util.Arrays.sort(idx, (i1, i2) -> {

            // Ordena primeiro pelos pontos (maior → primeiro)
            if (rankingPontos[i2] != rankingPontos[i1])
                return rankingPontos[i2] - rankingPontos[i1];

            // Se empatar nos pontos, menor tempo → primeiro
            if (rankingTempo[i1] < rankingTempo[i2]) return -1;
            if (rankingTempo[i1] > rankingTempo[i2]) return 1;

            return 0;
        });

        // Imprime os 3 melhores
        System.out.println("\n=== PÓDIO (Top 3) ===");
        int limite = Math.min(3, rankingCount);
        for (int i = 0; i < limite; i++) {
            int pos = idx[i];
            System.out.println((i + 1) + "º - " + rankingNome[pos] +
                    " (" + rankingPontos[pos] + " pontos, " + rankingTempo[pos] + "s)");
        }
    }
}
