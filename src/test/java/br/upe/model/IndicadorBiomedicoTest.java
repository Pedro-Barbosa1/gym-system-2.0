package br.upe.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class IndicadorBiomedicoTest {

    @Test
    void deveCriarIndicadorComTodosAtributos() {
        LocalDate hoje = LocalDate.of(2025, 9, 25);

        IndicadorBiomedico indicador = new IndicadorBiomedico(
                1, 10, hoje,
                70.5, 175.0, 20.0, 80.0, 23.0
        );

        assertEquals(1, indicador.getId());
        assertEquals(10, indicador.getIdUsuario());
        assertEquals(hoje, indicador.getData());
        assertEquals(70.5, indicador.getPesoKg());
        assertEquals(175.0, indicador.getAlturaCm());
        assertEquals(20.0, indicador.getPercentualGordura());
        assertEquals(80.0, indicador.getPercentualMassaMagra());
        assertEquals(23.0, indicador.getImc());
    }

    @Test
    void deveCriarIndicadorSemId() {
        LocalDate data = LocalDate.of(2025, 1, 1);

        IndicadorBiomedico indicador = new IndicadorBiomedico(
                15, data,
                80.0, 180.0, 25.0, 75.0, 24.7
        );

        assertEquals(15, indicador.getIdUsuario());
        assertEquals(data, indicador.getData());
        assertEquals(80.0, indicador.getPesoKg());
        assertEquals(180.0, indicador.getAlturaCm());
        assertEquals(25.0, indicador.getPercentualGordura());
        assertEquals(75.0, indicador.getPercentualMassaMagra());
        assertEquals(24.7, indicador.getImc());

        // id não informado → valor default de int é 0
        assertEquals(0, indicador.getId());
    }

    @Test
    void deveAlterarValoresComSetters() {
        IndicadorBiomedico indicador = new IndicadorBiomedico(
                1, 20, LocalDate.now(),
                60.0, 160.0, 18.0, 82.0, 21.0
        );

        indicador.setId(2);
        indicador.setIdUsuario(30);
        indicador.setData(LocalDate.of(2024, 12, 31));
        indicador.setPesoKg(90.0);
        indicador.setAlturaCm(185.0);
        indicador.setPercentualGordura(28.0);
        indicador.setPercentualMassaMagra(72.0);
        indicador.setImc(26.3);

        assertEquals(2, indicador.getId());
        assertEquals(30, indicador.getIdUsuario());
        assertEquals(LocalDate.of(2024, 12, 31), indicador.getData());
        assertEquals(90.0, indicador.getPesoKg());
        assertEquals(185.0, indicador.getAlturaCm());
        assertEquals(28.0, indicador.getPercentualGordura());
        assertEquals(72.0, indicador.getPercentualMassaMagra());
        assertEquals(26.3, indicador.getImc());
    }

    @Test
    void deveRetornarToStringFormatado() {
        LocalDate data = LocalDate.of(2025, 9, 25);

        IndicadorBiomedico indicador = new IndicadorBiomedico(
                1, 10, data,
                70.5, 175.0, 20.0, 80.0, 23.456
        );

        String esperado = String.format(Locale.US,
                "ID: %d | Data: %-12s | Peso: %.1fkg | Altura: %.0fcm | Gordura: %.1f%% | Massa Magra: %.1f%% | IMC: %-8.2f",
                1, data, 70.5, 175.0, 20.0, 80.0, 23.456);

        assertEquals(esperado, indicador.toString());
    }
}
