package br.upe.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTesterTest {

    @Test
    void testTestarConexao() {
        DatabaseConnectionTester.ConnectionResult resultado = DatabaseConnectionTester.testarConexao();
        
        assertNotNull(resultado);
        assertNotNull(resultado.getMensagem());
        
        // Este teste passará se houver conexão OU se houver erro esperado
        // Não vamos forçar que a conexão seja bem-sucedida porque 
        // o ambiente de teste pode não ter o banco configurado
        assertTrue(resultado.getMensagem().length() > 0, 
            "A mensagem de resultado deve conter informação");
    }
    
    @Test
    void testConnectionResultGetters() {
        DatabaseConnectionTester.ConnectionResult resultadoSucesso = 
            new DatabaseConnectionTester.ConnectionResult(true, "Teste de sucesso");
        
        assertTrue(resultadoSucesso.isSucesso());
        assertEquals("Teste de sucesso", resultadoSucesso.getMensagem());
        
        DatabaseConnectionTester.ConnectionResult resultadoErro = 
            new DatabaseConnectionTester.ConnectionResult(false, "Teste de erro");
        
        assertFalse(resultadoErro.isSucesso());
        assertEquals("Teste de erro", resultadoErro.getMensagem());
    }
}
