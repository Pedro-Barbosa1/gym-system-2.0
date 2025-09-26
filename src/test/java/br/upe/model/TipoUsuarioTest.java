package br.upe.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoUsuarioTest {

    @Test
    void testGetValor() {
        assertEquals(0, TipoUsuario.COMUM.getValor());
        assertEquals(1, TipoUsuario.ADMIN.getValor());
    }

    @Test
    void testFromValorComValoresValidos() {
        assertEquals(TipoUsuario.COMUM, TipoUsuario.fromValor(0));
        assertEquals(TipoUsuario.ADMIN, TipoUsuario.fromValor(1));
    }

    @Test
    void testFromValorComValorInvalido() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TipoUsuario.fromValor(2)
        );
        assertEquals("Valor de TipoUsuario inválido: 2", exception.getMessage());
    }
}
