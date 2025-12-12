package br.upe;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.util.DatabaseConnectionTester;
import br.upe.util.DatabaseInitializer;
import br.upe.util.JPAUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class MainFX extends Application {

    private static final Logger logger = Logger.getLogger(MainFX.class.getName());

    @Override
    public void start(Stage stage) {
        // Testa a conexão com o banco de dados antes de abrir a aplicação
        boolean conexaoOk = testarConexaoBancoDados(stage);
        
        // Se a conexão estiver OK, inicializa dados padrão (usuário admin)
        if (conexaoOk) {
            inicializarDadosPadrao();
        }
        
        abrirTela(stage, "ui/MenuPrincipal.fxml", "SysFit - Menu Principal");
    }
    
    /**
     * Inicializa dados padrão do sistema (usuário admin, etc)
     */
    private void inicializarDadosPadrao() {
        try {
            logger.info("Inicializando dados padrão do sistema...");
            DatabaseInitializer.inicializarDadosPadrao();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erro ao inicializar dados padrão", e);
        }
    }
    
    /**
     * Testa a conexão com o banco de dados e exibe alerta se houver erro
     * @return true se a conexão foi bem-sucedida, false caso contrário
     */
    private boolean testarConexaoBancoDados(Stage stage) {
        DatabaseConnectionTester.ConnectionResult resultado = DatabaseConnectionTester.testarConexao();
        
        if (!resultado.isSucesso()) {
            // Exibe alerta de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Conexão com Banco de Dados");
            alert.setHeaderText("Não foi possível conectar ao banco de dados");
            alert.setContentText(resultado.getMensagem() + 
                "\n\nA aplicação pode não funcionar corretamente.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    logger.warning("Usuário optou por continuar sem conexão com o banco");
                }
            });
            return false;
        } else {
            // Exibe confirmação de sucesso (opcional - pode remover se preferir)
            logger.info(resultado.getMensagem());
            
            // Descomente para mostrar alerta de sucesso:
            /*
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Conexão Estabelecida");
            alert.setHeaderText("Banco de dados conectado");
            alert.setContentText(resultado.getMensagem());
            alert.show();
            
            // Fecha o alerta automaticamente após 2 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> alert.close());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            */
            return true;
        }
    }

    public void abrirTela(Stage stage, String caminhoFXML, String tituloJanela) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + caminhoFXML));
            Parent root = loader.load();

            stage.setTitle(tituloJanela);
            stage.setScene(new Scene(root));
            stage.setMinWidth(900);
            stage.setMinHeight(700);
            stage.show();

            logger.info(() -> "Tela carregada com sucesso: " + caminhoFXML);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar a tela: " + caminhoFXML, e);
        }
    }
    
    @Override
    public void stop() {
        // Fecha o EntityManagerFactory quando a aplicação for encerrada
        logger.info("Encerrando aplicação...");
        JPAUtil.fecharFactory();
    }

    public static void main(String[] args) {
        launch();
    }
}
