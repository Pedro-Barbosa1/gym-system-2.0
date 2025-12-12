package br.upe.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.service.IndicadorBiomedicoService;
import br.upe.ui.util.StyledAlert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MenuRelatoriosController {

    private static final Logger logger = Logger.getLogger(MenuRelatoriosController.class.getName());
    private static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private IndicadorBiomedicoService indicadorService;
    private int idUsuarioLogado = 1; // TODO: Integrar com login

    @FXML private Button sairB;
    @FXML private Button exportarRelatorioB;

    @FXML private GridPane calendarioGrid;
    @FXML private Button btnMesAnterior;
    @FXML private Button btnMesProximo;
    @FXML private Text txtMesAno;

    private List<LocalDate> datasSelecionadas = new ArrayList<>();
    private YearMonth mesAtual = YearMonth.now();

    @FXML
    public void initialize() {
        this.indicadorService = new IndicadorBiomedicoService();
        logger.info("Menu Relatórios inicializado.");
        configurarAcoes();

        // Navegação do calendário
        btnMesAnterior.setOnAction(e -> {
            mesAtual = mesAtual.minusMonths(1);
            montarCalendario(mesAtual);
        });
        btnMesProximo.setOnAction(e -> {
            mesAtual = mesAtual.plusMonths(1);
            montarCalendario(mesAtual);
        });

        montarCalendario(mesAtual);
    }

    private void configurarAcoes() {
        exportarRelatorioB.setOnAction(e -> handleExportarRelatorioPorDataCalendario());
        sairB.setOnAction(e -> handleSair());
    }

    private void handleExportarRelatorioPorDataCalendario() {
        if (datasSelecionadas.size() < 2) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione pelo menos duas datas no calendário.");
            return;
        }
        LocalDate inicio = Collections.min(datasSelecionadas);
        LocalDate fim = Collections.max(datasSelecionadas);

        try {
            String caminho = "src/main/resources/relatorios/relatorio_por_data_" + idUsuarioLogado + ".csv";
            indicadorService.exportarRelatorioPorDataParaCsv(idUsuarioLogado, inicio, fim, caminho);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    "Relatório exportado com sucesso!\nPeríodo: " + inicio + " a " + fim + "\nArquivo: " + caminho);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao exportar relatório: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro ao exportar relatório por data", e);
        }
    }

    private void montarCalendario(YearMonth mes) {
        calendarioGrid.getChildren().clear();
        LocalDate primeiroDia = mes.atDay(1);
        int diasNoMes = mes.lengthOfMonth();

        for (int dia = 1; dia <= diasNoMes; dia++) {
            LocalDate data = mes.atDay(dia);
            Button diaButton = new Button(String.valueOf(dia));
            diaButton.setPrefSize(40, 40);
            diaButton.setUserData(data);
            diaButton.setStyle("-fx-background-color: #1E1E1E; -fx-text-fill: #e5a000;");

            diaButton.setOnAction(e -> handleClickData(data));

            int coluna = (dia + primeiroDia.getDayOfWeek().getValue() - 2) % 7;
            int linha = (dia + primeiroDia.getDayOfWeek().getValue() - 2) / 7;
            calendarioGrid.add(diaButton, coluna, linha);
        }

        // Atualiza o texto do mês e ano
        txtMesAno.setText(mes.getMonth().name() + " " + mes.getYear());

        atualizarVisualizacaoCalendario();
    }

    private void handleClickData(LocalDate data) {
        if (datasSelecionadas.contains(data)) {
            datasSelecionadas.remove(data);
        } else {
            datasSelecionadas.add(data);
            if (datasSelecionadas.size() > 2) {
                LocalDate maisAntiga = Collections.min(datasSelecionadas);
                LocalDate maisNova = Collections.max(datasSelecionadas);
                datasSelecionadas.clear();
                datasSelecionadas.add(maisAntiga);
                datasSelecionadas.add(maisNova);
            }
        }
        atualizarVisualizacaoCalendario();
    }

    private void atualizarVisualizacaoCalendario() {
        for (Node node : calendarioGrid.getChildren()) {
            Button diaButton = (Button) node;
            LocalDate dia = (LocalDate) diaButton.getUserData();

            if (datasSelecionadas.contains(dia) ||
                (!datasSelecionadas.isEmpty() && dia.isAfter(datasSelecionadas.get(0)) && dia.isBefore(datasSelecionadas.get(1)))) {
                diaButton.setStyle("-fx-background-color: #e5a000; -fx-text-fill: #000000;");
            } else {
                diaButton.setStyle("-fx-background-color: #1E1E1E; -fx-text-fill: #e5a000;");
            }
        }
    }

    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/MenuUsuarioLogado.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sairB.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível voltar ao menu.");
            logger.log(Level.SEVERE, "Erro ao retornar ao menu", e);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        if (tipo == Alert.AlertType.ERROR) {
            StyledAlert.showErrorAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.INFORMATION) {
            StyledAlert.showInformationAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.WARNING) {
            StyledAlert.showWarningAndWait(titulo, mensagem);
        } else if (tipo == Alert.AlertType.CONFIRMATION) {
            StyledAlert.showConfirmationAndWait(titulo, mensagem);
        }
    }
}
