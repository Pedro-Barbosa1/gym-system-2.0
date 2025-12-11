package br.upe.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class IndicadorBiomedicoTest {

    @Test
    void deveCriarIndicadorComTodosAtributos() {
        LocalDate hoje = LocalDate.of(2025, 9, 25);

        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
        .id(1)
        .idUsuario(10)
        .data(hoje)
        .pesoKg(70.5)
        .alturaCm(175.0)
        .percentualGordura(20.0)
        .percentualMassaMagra(80.0)
        .imc(23.0)
        .build();

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

        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
        .idUsuario(15)
        .data(data)
        .pesoKg(80.0)
        .alturaCm(180.0)
        .percentualGordura(25.0)
        .percentualMassaMagra(75.0)
        .imc(24.7)
        .build();

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
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
        .id(1)
        .idUsuario(20)
        .data(LocalDate.now())
        .pesoKg(60.0)
        .alturaCm(160.0)
        .percentualGordura(18.0)
        .percentualMassaMagra(82.0)
        .imc(21.0)
        .build();

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

        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
        .id(1)
        .idUsuario(10)
        .data(data)
        .pesoKg(70.5)
        .alturaCm(175.0)
        .percentualGordura(20.0)
        .percentualMassaMagra(80.0)
        .imc(23.456)
        .build();

        String esperado = String.format(Locale.US,
                "ID: %d | Data: %-12s | Peso: %.1fkg | Altura: %.0fcm | Gordura: %.1f%% | Massa Magra: %.1f%% | IMC: %-8.2f",
                1, data, 70.5, 175.0, 20.0, 80.0, 23.456);

        assertEquals(esperado, indicador.toString());
    }
}
