package br.upe.it;

import br.upe.controller.AdministradorViewController;
import br.upe.service.IUsuarioService;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.concurrent.FutureTask;
import static org.mockito.Mockito.*;

public class AdministradorViewControllerIT {

    private AdministradorViewController controller;
    private IUsuarioService usuarioServiceMock;

    @Test
    void deveListarUsuariosSemErro() throws Exception {
        runOnFxThread(() -> {
            controller.handleListarUsuarios();
        });

        verify(usuarioServiceMock, times(1)).listarTodosUsuarios();
    }

    private void runOnFxThread(Runnable action) {
    try {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        task.get();
    } catch (Exception e) {
        throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void initFX() {
        JavaFXInitializer.init();
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new AdministradorViewController();
        controller.initialize(); // cria o UsuarioService real

        usuarioServiceMock = mock(IUsuarioService.class);

        Field f = AdministradorViewController.class.getDeclaredField("usuarioService");
        f.setAccessible(true);
        f.set(controller, usuarioServiceMock);
    }

   @Test
    void handleListarUsuarios_quandoNaoHaUsuarios_chamaListarTodos() {
        when(usuarioServiceMock.listarTodosUsuarios()).thenReturn(Collections.emptyList());

        runOnFxThread(() -> controller.handleListarUsuarios());

        verify(usuarioServiceMock, times(1)).listarTodosUsuarios();
    }
}
