package br.upe.it;

import br.upe.controller.ExercicioViewController;
import br.upe.model.Exercicio;
import br.upe.service.IExercicioService;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExercicioViewControllerIT {

    private IExercicioService exercicioServiceMock;
    private ExercicioViewController controller;

    @BeforeAll
    void initFX() {
        new JFXPanel(); // inicia JavaFX
        //Platform.startup(() -> {});
    }

    private ExercicioViewController loadController() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/ExercicioView.fxml")
        );
        Parent root = loader.load();
        return loader.getController();
    }

    private void injectService(ExercicioViewController controller) throws Exception {
        Field field = ExercicioViewController.class.getDeclaredField("exercicioService");
        field.setAccessible(true);
        field.set(controller, exercicioServiceMock);
    }

    @BeforeEach
    void setup() throws Exception {
        exercicioServiceMock = mock(IExercicioService.class);
        controller = loadController();
        injectService(controller);
    }

    @Test
    void handleListarExercicios_quandoNaoHaExercicios_mostraAlerta() {
        when(exercicioServiceMock.listarExerciciosDoUsuario(1))
                .thenReturn(Collections.emptyList());

        Platform.runLater(() -> controller.handleListarExercicios(new ActionEvent()));

        verify(exercicioServiceMock, times(1)).listarExerciciosDoUsuario(1);
    }

    @Test
    void handleListarExercicios_quandoHaExercicios_listaCorretamente() {
        Exercicio e1 = new Exercicio("Supino", "Peito", "supino.gif", 1);
        Exercicio e2 = new Exercicio("Agachamento", "Pernas", "agacha.gif", 1);

        when(exercicioServiceMock.listarExerciciosDoUsuario(1))
                .thenReturn(Arrays.asList(e1, e2));

        Platform.runLater(() -> controller.handleListarExercicios(new ActionEvent()));

        verify(exercicioServiceMock, times(1)).listarExerciciosDoUsuario(1);
    }

    @Test
    void handleCadastrarExercicio_quandoServiceFalha_exibeErro() {
        when(exercicioServiceMock.cadastrarExercicio(anyInt(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Erro simulado"));

        Platform.runLater(() -> controller.handleCadastrarExercicio(new ActionEvent()));

        verify(exercicioServiceMock, times(1))
                .cadastrarExercicio(anyInt(), anyString(), anyString(), anyString());
    }

    @Test
    void handleEditarExercicio_quandoListaVazia_exibeAviso() {
        when(exercicioServiceMock.listarExerciciosDoUsuario(1))
                .thenReturn(Collections.emptyList());

        Platform.runLater(() -> controller.handleEditarExercicio(new ActionEvent()));

        verify(exercicioServiceMock, times(1)).listarExerciciosDoUsuario(1);
    }

    @Test
    void handleExcluirExercicio_listaVazia_mostraAviso() {
        when(exercicioServiceMock.listarExerciciosDoUsuario(1))
                .thenReturn(Collections.emptyList());

        Platform.runLater(() -> controller.handleExcluirExercicio(new ActionEvent()));

        verify(exercicioServiceMock, times(1)).listarExerciciosDoUsuario(1);
    }

    @Test
    void handleVerDetalhesExercicio_listaVazia_alerta() {
        when(exercicioServiceMock.listarExerciciosDoUsuario(1))
                .thenReturn(Collections.emptyList());

        Platform.runLater(() -> controller.handleVerDetalhesExercicio(new ActionEvent()));

        verify(exercicioServiceMock, times(1)).listarExerciciosDoUsuario(1);
    }
}
