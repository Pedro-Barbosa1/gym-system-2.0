package br.upe.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@DisplayName("IndicadorBiomedico Model Tests")
class IndicadorBiomedicoTest {

    @Test
    @DisplayName("Deve criar indicador usando Builder")
    void deveCriarIndicadorUsandoBuilder() {
        LocalDate data = LocalDate.of(2024, 1, 15);
        
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
                .id(1)
                .idUsuario(1)
                .data(data)
                .pesoKg(75.0)
                .alturaCm(175.0)
                .percentualGordura(15.0)
                .percentualMassaMagra(85.0)
                .imc(24.5)
                .build();
        
        assertEquals(1, indicador.getId());
        assertEquals(1, indicador.getIdUsuario());
        assertEquals(data, indicador.getData());
        assertEquals(75.0, indicador.getPesoKg());
        assertEquals(175.0, indicador.getAlturaCm());
        assertEquals(15.0, indicador.getPercentualGordura());
        assertEquals(85.0, indicador.getPercentualMassaMagra());
        assertEquals(24.5, indicador.getImc());
    }

    @Test
    @DisplayName("Deve alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder().build();
        LocalDate data = LocalDate.of(2024, 6, 20);
        
        indicador.setId(5);
        indicador.setIdUsuario(2);
        indicador.setData(data);
        indicador.setPesoKg(80.0);
        indicador.setAlturaCm(180.0);
        indicador.setPercentualGordura(18.0);
        indicador.setPercentualMassaMagra(82.0);
        indicador.setImc(24.7);
        
        assertEquals(5, indicador.getId());
        assertEquals(2, indicador.getIdUsuario());
        assertEquals(data, indicador.getData());
        assertEquals(80.0, indicador.getPesoKg());
        assertEquals(180.0, indicador.getAlturaCm());
        assertEquals(18.0, indicador.getPercentualGordura());
        assertEquals(82.0, indicador.getPercentualMassaMagra());
        assertEquals(24.7, indicador.getImc());
    }

    @Test
    @DisplayName("Builder deve permitir construcao parcial")
    void builderDevePermitirConstrucaoParcial() {
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder()
                .idUsuario(1)
                .pesoKg(70.0)
                .build();
        
        assertEquals(1, indicador.getIdUsuario());
        assertEquals(70.0, indicador.getPesoKg());
        assertEquals(0.0, indicador.getAlturaCm());
    }

    @Test
    @DisplayName("Deve criar indicador vazio com Builder padrao")
    void deveCriarIndicadorVazioComBuilderPadrao() {
        IndicadorBiomedico indicador = new IndicadorBiomedico.Builder().build();
        
        assertNotNull(indicador);
        assertEquals(0, indicador.getId());
        assertEquals(0, indicador.getIdUsuario());
    }
}
