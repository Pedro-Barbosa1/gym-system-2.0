package br.upe.it;

import br.upe.controller.LoginController;
import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.service.UsuarioService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginControllerIT {

    private LoginController controller;
    private UsuarioService usuarioServiceMock;

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new LoginController();

        // Criar os mocks dos campos da UI
        controller.emailTextField = new TextField();
        controller.setSenhaField = new PasswordField();
        controller.entrarBotao = new Button();

        // Criar o mock de UsuarioService
        usuarioServiceMock = Mockito.mock(UsuarioService.class);

        // Injetar o mock via reflexão
        Field field = LoginController.class.getDeclaredField("usuarioService");
        field.setAccessible(true);
        field.set(controller, usuarioServiceMock);
    }

    @Test
    void onEntrar_quandoLoginBemSucedidoUsuarioAdmin_chamaAutenticarUsuario() {
        // given
        controller.emailTextField.setText("admin@gym.com");
        controller.setSenhaField.setText("123");

        Usuario admin = new Usuario();
        admin.setEmail("admin@gym.com");
        admin.setTipo(TipoUsuario.ADMIN);

        when(usuarioServiceMock.autenticarUsuario("admin@gym.com", "123"))
                .thenReturn(admin);

        // when
        Platform.runLater(() -> controller.onEntrar(new ActionEvent()));

        // Aguarda execução no FX thread
        sleep(400);

        // then
        verify(usuarioServiceMock, times(1))
                .autenticarUsuario("admin@gym.com", "123");
    }

    @Test
    void onEntrar_quandoCredenciaisInvalidas_mostraErro() {
        // given
        controller.emailTextField.setText("user@teste.com");
        controller.setSenhaField.setText("wrong");

        when(usuarioServiceMock.autenticarUsuario(anyString(), anyString()))
                .thenReturn(null);

        // when
        Platform.runLater(() -> controller.onEntrar(new ActionEvent()));
        sleep(400);

        // then
        verify(usuarioServiceMock, times(1))
                .autenticarUsuario("user@teste.com", "wrong");
    }

    @Test
    void onEntrar_quandoCamposInvalidos_lancaIllegalArgument() {
        // given
        controller.emailTextField.setText("");
        controller.setSenhaField.setText("");

        when(usuarioServiceMock.autenticarUsuario("", ""))
                .thenThrow(new IllegalArgumentException("Campos vazios"));

        // when
        Platform.runLater(() -> controller.onEntrar(new ActionEvent()));
        sleep(400);

        // then
        verify(usuarioServiceMock, times(1)).autenticarUsuario("", "");
    }

    // Utilitário para aguardar JavaFX Thread
    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (Exception ignored) {}
    }
}
