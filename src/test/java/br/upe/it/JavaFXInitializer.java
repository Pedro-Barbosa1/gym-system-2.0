package br.upe.it;

import javafx.application.Platform;

import org.junit.jupiter.api.BeforeAll;
import org.testfx.util.WaitForAsyncUtils;

public class JavaFXInitializer {

        @BeforeAll
    static void initFX() {
        Platform.startup(() -> {}); // inicializa toolkit
    }

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        Platform.startup(() -> {});
        initialized = true;
        WaitForAsyncUtils.waitForFxEvents(); // garante que o FX terminou de iniciar
    }
}
