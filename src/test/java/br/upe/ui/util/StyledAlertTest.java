package br.upe.ui.util;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Classe de teste para verificar se os StyledAlerts estão funcionando
 */
public class StyledAlertTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #2C2C2C;");

        Button btnInfo = new Button("Testar INFORMATION");
        btnInfo.setOnAction(e -> 
            StyledAlert.showInformationAndWait("Teste de Informação", 
                "Este é um alert de informação com header roxo!")
        );

        Button btnError = new Button("Testar ERROR");
        btnError.setOnAction(e -> 
            StyledAlert.showErrorAndWait("Teste de Erro", 
                "Este é um alert de erro com header vermelho!")
        );

        Button btnWarning = new Button("Testar WARNING");
        btnWarning.setOnAction(e -> 
            StyledAlert.showWarningAndWait("Teste de Aviso", 
                "Este é um alert de aviso com header laranja!")
        );

        Button btnConfirmation = new Button("Testar CONFIRMATION");
        btnConfirmation.setOnAction(e -> {
            boolean result = StyledAlert.showConfirmationAndWait("Teste de Confirmação", 
                "Você confirma esta ação?");
            System.out.println("Resultado: " + (result ? "Confirmado" : "Cancelado"));
        });

        root.getChildren().addAll(btnInfo, btnError, btnWarning, btnConfirmation);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Teste de StyledAlert");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
