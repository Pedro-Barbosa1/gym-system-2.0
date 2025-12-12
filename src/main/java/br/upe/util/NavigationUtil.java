package br.upe.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitária para navegação entre telas mantendo a responsividade.
 * Preserva as dimensões da janela ao trocar de cena.
 */
public class NavigationUtil {
    
    private static final Logger logger = Logger.getLogger(NavigationUtil.class.getName());
    
    private NavigationUtil() {
        // Classe utilitária - construtor privado
    }
    
    /**
     * Carrega uma nova tela FXML mantendo as dimensões atuais da janela.
     * 
     * @param stage Stage atual onde a cena será trocada
     * @param fxmlPath Caminho do arquivo FXML (exemplo: "/ui/MenuPrincipal.fxml")
     * @param title Título da nova janela
     * @return true se a navegação foi bem-sucedida, false caso contrário
     */
    public static boolean navigateTo(Stage stage, String fxmlPath, String title) {
        try {
            // Salva as dimensões atuais da janela
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            boolean isMaximized = stage.isMaximized();
            
            // Carrega o novo FXML
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Cria nova cena
            Scene newScene = new Scene(root);
            
            // Define a nova cena
            stage.setScene(newScene);
            stage.setTitle(title);
            
            // Restaura as dimensões (se não estiver maximizado)
            if (!isMaximized) {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
            }
            
            // Garante que a janela seja exibida
            stage.show();
            
            logger.info(() -> "Navegação bem-sucedida para: " + fxmlPath);
            return true;
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao navegar para: " + fxmlPath, e);
            return false;
        }
    }
    
    /**
     * Carrega uma nova tela FXML a partir de um botão/componente da cena atual.
     * Mantém as dimensões da janela.
     * 
     * @param node Qualquer Node da cena atual (geralmente um botão)
     * @param fxmlPath Caminho do arquivo FXML
     * @param title Título da nova janela
     * @return true se a navegação foi bem-sucedida, false caso contrário
     */
    public static boolean navigateFrom(javafx.scene.Node node, String fxmlPath, String title) {
        Stage stage = (Stage) node.getScene().getWindow();
        return navigateTo(stage, fxmlPath, title);
    }
    
    /**
     * Define um Parent já carregado como a nova cena, mantendo as dimensões da janela.
     * Útil quando o controller precisa carregar e configurar o FXML antes de navegar.
     * 
     * @param node Qualquer Node da cena atual (para obter o Stage)
     * @param root Parent já carregado
     * @param title Título da nova janela
     */
    public static void navigateTo(javafx.scene.Node node, Parent root, String title) {
        Stage stage = (Stage) node.getScene().getWindow();
        
        // Salva as dimensões atuais da janela
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        boolean isMaximized = stage.isMaximized();
        
        // Cria nova cena
        Scene newScene = new Scene(root);
        
        // Define a nova cena
        stage.setScene(newScene);
        stage.setTitle(title);
        
        // Restaura as dimensões (se não estiver maximizado)
        if (!isMaximized) {
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        }
        
        // Garante que a janela seja exibida
        stage.show();
    }
}
