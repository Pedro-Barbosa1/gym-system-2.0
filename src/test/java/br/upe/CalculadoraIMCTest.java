package br.upe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import br.upe.business.CalculadoraIMC;

public class CalculadoraIMCTest {

    CalculadoraIMC calculadoraIMC = new CalculadoraIMC();

    @Test
    public void testCalcularImcComValoresValidos() {
        double imc = CalculadoraIMC.calcular(70.0, 175.0); // 70kg, 1.75m
        assertEquals(22.86, imc, 0.01); // tolerância de 0.01
    }

    @Test
    public void testCalcularImcAlturaZeroDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraIMC.calcular(70.0, 0.0);
        });
    }

    @Test
    public void testCalcularImcAlturaNegativaDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraIMC.calcular(70.0, -160.0);
        });
    }

    @Test
    public void testClassificacaoAbaixoDoPeso() {
        assertEquals("Abaixo do peso", CalculadoraIMC.classificarImc(17.0));
    }

    @Test
    public void testClassificacaoPesoNormal() {
        assertEquals("Peso normal", CalculadoraIMC.classificarImc(22.0));
    }

    @Test
    public void testClassificacaoSobrepeso() {
        assertEquals("Sobrepeso", CalculadoraIMC.classificarImc(27.0));
    }

    @Test
    public void testClassificacaoObesidadeGrauI() {
        assertEquals("Obesidade Grau I", CalculadoraIMC.classificarImc(33.0));
    }

    @Test
    public void testClassificacaoObesidadeGrauII() {
        assertEquals("Obesidade Grau II", CalculadoraIMC.classificarImc(38.0));
    }

    @Test
    public void testClassificacaoObesidadeGrauIII() {
        assertEquals("Obesidade Grau III", CalculadoraIMC.classificarImc(45.0));
    }
}
