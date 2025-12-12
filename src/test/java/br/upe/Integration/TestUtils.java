package br.upe.Integration;

import javafx.scene.control.Button;
import org.testfx.api.FxRobot;

import java.lang.reflect.Field;

public class TestUtils {

    private static final FxRobot robot = new FxRobot();

    public static void clickDialogOK() {
        robot.interact(() -> {
            Button btn = robot.lookup(".button")
                    .queryAllAs(Button.class)
                    .stream()
                    .filter(b -> "OK".equals(b.getText()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Botão OK não encontrado"));

            btn.fire();  // agora executado na FX Application Thread
        });
    }

    public static void writeInDialogField(String text) {
        robot.interact(() -> {
            javafx.scene.control.TextField field =
                    robot.lookup(".text-field")
                            .queryAs(javafx.scene.control.TextField.class);

            field.setText(text);
        });
    }

    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);

            // não precisa ser na FX thread porque é apenas reflexão em objeto comum
            f.set(target, value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
