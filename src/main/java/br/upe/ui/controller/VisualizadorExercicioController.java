package br.upe.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VisualizadorExercicioController {

    @FXML
    private ImageView gifImageView;

    @FXML
    private TextArea descricaoArea;

    public void exibirExercicio(String gifPath, String descricao) {
        try {
            Image gif = new Image(getClass().getResourceAsStream("/" + gifPath));
            gifImageView.setImage(gif);
        } catch (Exception e) {
            descricaoArea.setText("Erro ao carregar o GIF: " + gifPath);
        }

        descricaoArea.setText(descricao);
    }
}