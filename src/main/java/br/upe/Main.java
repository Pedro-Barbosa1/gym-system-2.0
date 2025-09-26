package br.upe;

import br.upe.ui.MenuPrincipal;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());
        Scanner scGlobal = new Scanner(System.in);

        // Exibir menu e iniciar
        MenuPrincipal menuPrincipal = new MenuPrincipal(scGlobal);
        menuPrincipal.exibirMenuInicial();

        scGlobal.close();
        logger.info("Programa encerrado.");
    }
}