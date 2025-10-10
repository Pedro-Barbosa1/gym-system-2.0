package br.upe.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import br.upe.model.SessaoTreino;
import br.upe.service.ExercicioService;
import br.upe.service.IExercicioService;
import br.upe.service.IPlanoTreinoService;
import br.upe.service.PlanoTreinoService;
import br.upe.service.SessaoTreinoService;

public class MenuTreinos {

    private final Scanner sc;
    private final int idUsuarioLogado;
    private final SessaoTreinoService sessaoTreinoService;
    private final IPlanoTreinoService planoTreinoService;
    private final IExercicioService exercicioService;

    public MenuTreinos(Scanner scanner, int idUsuarioLogado) {
        this.sc = scanner;
        this.idUsuarioLogado = idUsuarioLogado;
        this.sessaoTreinoService = new SessaoTreinoService();
        this.planoTreinoService = new PlanoTreinoService();
        this.exercicioService = new ExercicioService();
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== MEUS TREINOS =====");
            System.out.println("1. Iniciar Nova Sessão de Treino");
            System.out.println("2. Ver Histórico de Sessões (Funcionalidade futura)");
            System.out.println("0. Voltar");
            System.out.print("\nEscolha uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    iniciarNovaSessao();
                    break;
                case 2:
                    System.out.println("Funcionalidade de histórico em desenvolvimento.");
                    break;
                case 0:
                    System.out.println("Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, escolha uma das opções válidas.");
            }
        } while (opcao != 0);
    }

    private void iniciarNovaSessao() {
        System.out.println("\n===== INICIAR NOVA SESSÃO DE TREINO =====");
        List<PlanoTreino> meusPlanos = planoTreinoService.listarMeusPlanos(idUsuarioLogado);

        if (meusPlanos.isEmpty()) {
            System.out.println("Você não possui planos de treino cadastrados. Crie um plano primeiro.");
            return;
        }

        exibirPlanos(meusPlanos);
        int idPlanoEscolhido = solicitarPlanoId();

        Optional<PlanoTreino> planoOpt = planoTreinoService.buscarPlanoPorId(idPlanoEscolhido);

        if (!validacaoPlano(planoOpt, idUsuarioLogado, idPlanoEscolhido)) {
            return;
        }
        PlanoTreino planoBase = planoOpt.get();

        if (planoBase.getItensTreino().isEmpty()) {
            System.out.println("Este plano não possui exercícios. Adicione exercícios ao plano antes de iniciar uma sessão.");
            return;
        }

        try {
            SessaoTreino sessaoAtual = sessaoTreinoService.iniciarSessao(idUsuarioLogado, idPlanoEscolhido);
            System.out.println("Sessão iniciada para o plano: " + planoBase.getNome() + " em " + sessaoAtual.getDataSessao() + ".");

            registrarExercicios(sessaoAtual, planoBase);

            System.out.println("\n===== FIM DA SESSÃO =====");
            sessaoTreinoService.salvarSessao(sessaoAtual);

            List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessaoAtual);
            tratarSugestoes(sugestoes, planoBase);

        } catch (NumberFormatException e) {
            System.err.println("Entrada inválida para repetições ou carga. Por favor, digite um número.");
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao iniciar ou registrar sessão: " + e.getMessage());
        }
    }

    private void exibirPlanos(List<PlanoTreino> meusPlanos) {
        System.out.println("\n--- Seus Planos de Treino Disponíveis ---");
        meusPlanos.forEach(p -> System.out.println("ID: " + p.getIdPlano() + ", Nome: " + p.getNome()));
    }

    private int solicitarPlanoId() {
        System.out.print("Digite o ID do plano de treino para esta sessão: ");
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("ID de plano inválido. Por favor, digite um número.");
            throw e;
        }
    }

    private boolean validacaoPlano(Optional<PlanoTreino> planoOpt, int idUsuarioLogado, int idPlanoEscolhido) {
        if (!planoOpt.isPresent() || planoOpt.get().getIdUsuario() != idUsuarioLogado) {
            System.out.println("Plano com ID " + idPlanoEscolhido + " não encontrado ou não pertence a você.");
            return false;
        }
        return true;
    }

    private void registrarExercicios(SessaoTreino sessaoAtual, PlanoTreino planoBase) {
        System.out.println("\n--- Registrando Exercícios ---");
        for (ItemPlanoTreino itemPlanejado : planoBase.getItensTreino()) {
            Optional<Exercicio> exercicioOpt = exercicioService.buscarExercicioPorIdGlobal(itemPlanejado.getIdExercicio());
            String nomeExercicio = exercicioOpt.isPresent() ? exercicioOpt.get().getNome() : "Exercício Desconhecido";

            System.out.println("\n--- Exercício: " + nomeExercicio + " ---");
            System.out.println("Planejado: Carga " + itemPlanejado.getCargaKg() + "kg, Repetições " + itemPlanejado.getRepeticoes());

            int repRealizadas = lerInteiroOpcional("Repetições realizadas (deixe em branco para planejado: " + itemPlanejado.getRepeticoes() + "): ", itemPlanejado.getRepeticoes());
            double cargaRealizada = lerDoubleOpcional("Carga utilizada (kg, deixe em branco para planejado: " + itemPlanejado.getCargaKg() + "): ", itemPlanejado.getCargaKg());

            sessaoTreinoService.registrarExecucao(sessaoAtual, itemPlanejado.getIdExercicio(), repRealizadas, cargaRealizada);
        }
    }

    private int lerInteiroOpcional(String mensagem, int valorPadrao) {
        System.out.print(mensagem);
        String input = sc.nextLine();
        return input.isEmpty() ? valorPadrao : Integer.parseInt(input);
    }

    private double lerDoubleOpcional(String mensagem, double valorPadrao) {
        System.out.print(mensagem);
        String input = sc.nextLine();
        return input.isEmpty() ? valorPadrao : Double.parseDouble(input);
    }

    private void tratarSugestoes(List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes, PlanoTreino planoBase) {
        if (!sugestoes.isEmpty()) {
            System.out.println("\n--- Sugestões de Atualização do Plano ---");
            for (SessaoTreinoService.SugestaoAtualizacaoPlano sugestao : sugestoes) {
                System.out.printf("O exercício '%s' (ID: %d) teve alterações.\n", sugestao.nomeExercicio(), sugestao.idExercicio());
                System.out.printf("  Repetições: Planejado %d -> Realizado %d\n", sugestao.repPlanejadas(), sugestao.repRealizadas());
                System.out.printf("  Carga: Planejado %.0fkg -> Realizado %.0fkg\n", sugestao.cargaPlanejada(), sugestao.cargaRealizada());
                System.out.print("Deseja atualizar o plano com os novos valores? (s/n): ");
                String resposta = sc.nextLine();
                if (resposta.equalsIgnoreCase("s")) {
                    sessaoTreinoService.aplicarAtualizacoesNoPlano(planoBase.getIdPlano(), sugestao.idExercicio(), sugestao.repRealizadas(), sugestao.cargaRealizada());
                    System.out.println("Plano atualizado para " + sugestao.nomeExercicio() + ".");
                }
            }
        } else {
            System.out.println("Nenhuma alteração significativa nos exercícios para sugerir atualização do plano.");
        }
    }

}
