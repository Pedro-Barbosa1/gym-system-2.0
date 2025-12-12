package br.upe.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculadoraIMC Tests")
class CalculadoraIMCTest {

    @Test
    @DisplayName("Deve calcular IMC corretamente")
    void deveCalcularIMCCorretamente() {
        // Peso 75kg, Altura 175cm => IMC = 75 / (1.75 * 1.75) = 24.49
        double imc = CalculadoraIMC.calcular(75.0, 175.0);
        assertEquals(24.49, imc, 0.01);
    }

    @Test
    @DisplayName("Deve lancar excecao para altura zero")
    void deveLancarExcecaoParaAlturaZero() {
        assertThrows(IllegalArgumentException.class, () -> CalculadoraIMC.calcular(75.0, 0));
    }

    @Test
    @DisplayName("Deve lancar excecao para altura negativa")
    void deveLancarExcecaoParaAlturaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> CalculadoraIMC.calcular(75.0, -175.0));
    }

    @Test
    @DisplayName("Deve lancar excecao para peso zero")
    void deveLancarExcecaoParaPesoZero() {
        assertThrows(IllegalArgumentException.class, () -> CalculadoraIMC.calcular(0, 175.0));
    }

    @Test
    @DisplayName("Deve lancar excecao para peso negativo")
    void deveLancarExcecaoParaPesoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> CalculadoraIMC.calcular(-75.0, 175.0));
    }

    @ParameterizedTest
    @DisplayName("Deve classificar IMC corretamente")
    @CsvSource({
        "17.0, Abaixo do peso",
        "18.4, Abaixo do peso",
        "18.5, Peso normal",
        "22.0, Peso normal",
        "24.8, Peso normal",
        "25.0, Sobrepeso",
        "27.0, Sobrepeso",
        "29.8, Sobrepeso",
        "30.0, Obesidade Grau I",
        "32.0, Obesidade Grau I",
        "34.8, Obesidade Grau I",
        "35.0, Obesidade Grau II",
        "37.0, Obesidade Grau II",
        "39.8, Obesidade Grau II",
        "40.0, Obesidade Grau III",
        "45.0, Obesidade Grau III"
    })
    void deveClassificarIMCCorretamente(double imc, String classificacaoEsperada) {
        String classificacao = CalculadoraIMC.classificarImc(imc);
        assertEquals(classificacaoEsperada, classificacao);
    }

    @Test
    @DisplayName("Deve calcular IMC para diferentes pesos e alturas")
    void deveCalcularIMCParaDiferentesPesosEAlturas() {
        // Pessoa baixa e leve
        double imc1 = CalculadoraIMC.calcular(50.0, 160.0);
        assertEquals(19.53, imc1, 0.01);

        // Pessoa alta e pesada
        double imc2 = CalculadoraIMC.calcular(100.0, 190.0);
        assertEquals(27.70, imc2, 0.01);
    }
}
