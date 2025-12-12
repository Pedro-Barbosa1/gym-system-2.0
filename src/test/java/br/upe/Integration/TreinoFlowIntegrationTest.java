package br.upe.Integration;

import br.upe.controller.TreinoViewController;
import br.upe.model.*;
import br.upe.service.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

public class TreinoFlowIntegrationTest extends ApplicationTest {

    private SessaoTreinoService sessaoServiceMock;
    private PlanoTreinoService planoServiceMock;
    private ExercicioService exercicioServiceMock;

    private TreinoViewController controller;

    @Override
    public void start(Stage stage) throws Exception {

        // ---------- MOCKS ----------
        sessaoServiceMock = Mockito.mock(SessaoTreinoService.class);
        planoServiceMock = Mockito.mock(PlanoTreinoService.class);
        exercicioServiceMock = Mockito.mock(ExercicioService.class);

        // Usuário fake
        Usuario usuarioFake = new Usuario("Teste", "kevin@teste.com","123",TipoUsuario.COMUM);
        usuarioFake.setId(1);

        // Plano de treino fake
        PlanoTreino plano = getPlanoTreino();

        // Sessão fake
        SessaoTreino sessaoFake = new SessaoTreino(usuarioFake.getId(), plano.getIdPlano());

        // Configura mocks
        when(planoServiceMock.listarMeusPlanos(1)).thenReturn(List.of(plano));
        when(sessaoServiceMock.iniciarSessao(1, 10)).thenReturn(sessaoFake);
        when(exercicioServiceMock.buscarExercicioPorIdGlobal(5))
                .thenReturn(java.util.Optional.of(new Exercicio(
                        usuarioFake,
                        usuarioFake.getNome(),
                        "descricao",
                        "src/main/resources/gif/agachamento_livre.gif"
                )));

        // ---------- Carregar FXML e injetar mocks ----------
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/TreinoView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        TestUtils.setField(controller, "sessaoTreinoService", sessaoServiceMock);
        TestUtils.setField(controller, "planoTreinoService", planoServiceMock);
        TestUtils.setField(controller, "exercicioService", exercicioServiceMock);

        stage.setScene(new Scene(root));
        stage.show();
    }

    private static PlanoTreino getPlanoTreino() {

        PlanoTreino plano = new PlanoTreino();
        plano.setIdPlano(10);
        plano.setNome("Treino A");

        // Exercício fake
        Exercicio ex = new Exercicio();
        ex.setIdExercicio(5);
        ex.setNome("Supino");

        // ---- CRIAÇÃO DO ITEM USANDO APENAS O CONSTRUTOR DISPONÍVEL ----
        ItemPlanoTreino ipt = new ItemPlanoTreino(
                plano.getIdPlano(),
                ex.getIdExercicio(),
                12,
                20
        );

        // Ajusta relacionamento
        ipt.setPlanoTreino(plano);

        // Adiciona o item ao plano
        plano.adicionarItem(ipt);

        return plano;
    }

    @Test
    public void testarFluxoCompletoDeSessaoDeTreino() {

        clickOn("#BListarTR");

        TestUtils.clickDialogOK();

        TestUtils.writeInDialogField("12");
        TestUtils.writeInDialogField("20");
        TestUtils.clickDialogOK();

        TestUtils.clickDialogOK();

        verify(sessaoServiceMock, times(1))
                .iniciarSessao(1, 10);

        verify(sessaoServiceMock, atLeastOnce())
                .registrarExecucao(any(), eq(5), anyInt(), anyDouble());

        verify(sessaoServiceMock, atLeastOnce())
                .salvarSessao(any());
    }
}