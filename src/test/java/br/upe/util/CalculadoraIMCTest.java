package br.upe.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CalculadoraIMCTest {

    @Test
    void testCalcularImcComValoresValidos() {
        double imc = CalculadoraIMC.calcular(70.0, 175.0); // 70kg, 1.75m
        assertEquals(22.86, imc, 0.01); // tolerância de 0.01
    }

    @Test
    void testCalcularImcAlturaZeroDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraIMC.calcular(70.0, 0.0);
        });
    }

    @Test
    void testCalcularImcAlturaNegativaDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraIMC.calcular(70.0, -160.0);
        });
    }

    @Test
    void testClassificacaoAbaixoDoPeso() {
        assertEquals("Abaixo do peso", CalculadoraIMC.classificarImc(17.0));
    }

    @Test
    void testClassificacaoPesoNormal() {
        assertEquals("Peso normal", CalculadoraIMC.classificarImc(22.0));
    }

    @Test
    void testClassificacaoSobrepeso() {
        assertEquals("Sobrepeso", CalculadoraIMC.classificarImc(27.0));
    }

    @Test
    void testClassificacaoObesidadeGrauI() {
        assertEquals("Obesidade Grau I", CalculadoraIMC.classificarImc(33.0));
    }

    @Test
    void testClassificacaoObesidadeGrauII() {
        assertEquals("Obesidade Grau II", CalculadoraIMC.classificarImc(38.0));
    }

    @Test
    void testClassificacaoObesidadeGrauIII() {
        assertEquals("Obesidade Grau III", CalculadoraIMC.classificarImc(45.0));
    }
}
