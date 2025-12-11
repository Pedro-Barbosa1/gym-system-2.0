package br.upe.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VisualizadorExercicioController {

    private static final Logger logger = Logger.getLogger(VisualizadorExercicioController.class.getName());

    @FXML
    private ImageView gifImageView;

    @FXML
    private TextArea descricaoArea;

    public void exibirExercicio(String gifPath, String nomeExercicio, String descricao) {
        try {
            logger.info("Carregando GIF do caminho: " + gifPath);
            
            // Tentar carregar o GIF do caminho especificado
            java.io.InputStream inputStream = getClass().getResourceAsStream(gifPath);
            
            if (inputStream == null) {
                logger.warning("InputStream é null para o caminho: " + gifPath);
                descricaoArea.setText("ERRO: Não foi possível carregar o GIF.\n\n" +
                                    "Caminho tentado: " + gifPath + "\n\n" +
                                    "Descrição: " + descricao);
                return;
            }
            
            Image gif = new Image(inputStream);
            
            if (gif.isError()) {
                logger.warning("Erro ao carregar imagem do caminho: " + gifPath);
                descricaoArea.setText("ERRO: Falha ao processar a imagem.\n\n" +
                                    "Caminho: " + gifPath + "\n\n" +
                                    "Descrição: " + descricao);
                return;
            }
            
            gifImageView.setImage(gif);
            descricaoArea.setText(descricao);
            
            logger.info("GIF carregado com sucesso!");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar GIF: " + gifPath, e);
            descricaoArea.setText("ERRO: Exceção ao carregar o GIF.\n\n" +
                                "Caminho: " + gifPath + "\n" +
                                "Erro: " + e.getMessage() + "\n\n" +
                                "Descrição: " + descricao);
        }
    }
}