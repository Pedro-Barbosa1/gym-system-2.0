package br.upe.it;

import br.upe.controller.IndicadoresViewController;
import br.upe.model.IndicadorBiomedico;
import br.upe.service.IIndicadorBiomedicoService;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IndicadoresViewControllerIT {

    private IndicadoresViewController controller;
    private IIndicadorBiomedicoService indicadorServiceMock;

    private MockedStatic<br.upe.ui.util.StyledAlert> styledAlertMock;

    @BeforeAll
    void iniciarJavaFX() {
        new JFXPanel(); // inicia toolkit
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setup() throws Exception {
        controller = new IndicadoresViewController();

        // --- Mock do service ---
        indicadorServiceMock = mock(IIndicadorBiomedicoService.class);

        // --- Injetando mock via reflexão ---
        Field serviceField = IndicadoresViewController.class.getDeclaredField("indicadorService");
        serviceField.setAccessible(true);
        serviceField.set(controller, indicadorServiceMock);

        // --- Mock dos alerts (evita janelas reais) ---
        styledAlertMock = mockStatic(br.upe.ui.util.StyledAlert.class);

        // --- Simular botão do FXML que o controller usa em carregarNovaTela ---
        Field botao = IndicadoresViewController.class.getDeclaredField("BCadastrarIN");
        botao.setAccessible(true);
        botao.set(controller, new Button());
    }

    @AfterEach
    void cleanup() {
        styledAlertMock.close();
    }

    @Test
    void listarIndicadores_quandoNaoHaIndicadores_exibeAviso() {
        when(indicadorServiceMock.listarTodosDoUsuario(1))
                .thenReturn(Collections.emptyList());

        Platform.runLater(() -> controller.listarMeusIndicadores(new ActionEvent()));

        // Verifica chamada do service
        verify(indicadorServiceMock, times(1)).listarTodosDoUsuario(1);

        // Verifica que mostrou alerta de INFORMATION
        styledAlertMock.verify(() ->
                br.upe.ui.util.StyledAlert.showInformationAndWait(anyString(), anyString()),
                times(1)
        );
    }

    @Test
    void listarIndicadores_quandoHaIndicadores_mostraTabela() {
        IndicadorBiomedico i1 = new IndicadorBiomedico(1, LocalDate.now(), 80, 180, 12, 40);
        IndicadorBiomedico i2 = new IndicadorBiomedico(2, LocalDate.now(), 82, 180, 13, 39);

        when(indicadorServiceMock.listarTodosDoUsuario(1))
                .thenReturn(Arrays.asList(i1, i2));

        Platform.runLater(() -> controller.listarMeusIndicadores(new ActionEvent()));

        verify(indicadorServiceMock, times(1)).listarTodosDoUsuario(1);

        // Nenhum alerta deve ocorrer
        styledAlertMock.verifyNoInteractions();
    }

    @Test
    void cadastrarIndicador_quandoDadosValidos_salvaComSucesso() {
        IndicadorBiomedico indicadorMockado =
                new IndicadorBiomedico(1, LocalDate.now(), 80, 180, 10, 40);

        when(indicadorServiceMock.cadastrarIndicador(
                anyInt(), any(), anyDouble(), anyDouble(), anyDouble(), anyDouble()
        )).thenReturn(indicadorMockado);

        Platform.runLater(() -> controller.cadastrarNovoIndicador(new ActionEvent()));

        verify(indicadorServiceMock, times(1))
                .cadastrarIndicador(anyInt(), any(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void cadastrarIndicador_quandoServiceFalha_exibeErro() {
        when(indicadorServiceMock.cadastrarIndicador(
                anyInt(), any(), anyDouble(), anyDouble(), anyDouble(), anyDouble()
        )).thenThrow(new RuntimeException("Erro simulado"));

        Platform.runLater(() -> controller.cadastrarNovoIndicador(new ActionEvent()));

        styledAlertMock.verify(() ->
                br.upe.ui.util.StyledAlert.showErrorAndWait(anyString(), anyString()),
                times(1)
        );
    }
}
