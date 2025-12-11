package br.upe.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

public class DatabaseConnectionTester {
    
    private static final Logger logger = Logger.getLogger(DatabaseConnectionTester.class.getName());
    
    /**
     * Testa a conexão com o banco de dados
     * @return ConnectionResult com status e mensagem
     */
    public static ConnectionResult testarConexao() {
        EntityManager em = null;
        try {
            logger.info("Iniciando teste de conexão com o banco de dados...");
            
            // Tenta obter o EntityManagerFactory
            EntityManagerFactory emf = JPAUtil.emf;
            
            if (emf == null || !emf.isOpen()) {
                return new ConnectionResult(false, 
                    "EntityManagerFactory não foi inicializado corretamente.");
            }
            
            // Tenta criar um EntityManager e fazer uma operação simples
            em = JPAUtil.getEntityManager();
            
            // Executa uma query simples para testar a conexão
            em.createNativeQuery("SELECT 1").getSingleResult();
            
            logger.info("Conexão com o banco de dados estabelecida com sucesso!");
            return new ConnectionResult(true, 
                "Conexão com o banco de dados estabelecida com sucesso!");
            
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Erro ao conectar com o banco de dados", e);
            return new ConnectionResult(false, 
                "Erro de conexão: " + getErrorMessage(e));
                
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro inesperado ao testar conexão", e);
            return new ConnectionResult(false, 
                "Erro inesperado: " + e.getMessage());
                
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Extrai mensagem de erro amigável
     */
    private static String getErrorMessage(Exception e) {
        String message = e.getMessage();
        
        if (message == null) {
            return "Erro desconhecido";
        }
        
        // Erros comuns e suas mensagens amigáveis
        if (message.contains("Communications link failure")) {
            return "MySQL não está rodando ou não está acessível na porta 3306. " +
                   "Verifique se o serviço MySQL está ativo.";
        }
        
        if (message.contains("Access denied")) {
            return "Usuário ou senha incorretos. Verifique as credenciais no persistence.xml";
        }
        
        if (message.contains("Unknown database")) {
            return "Banco de dados 'academia' não existe. Execute: CREATE DATABASE academia;";
        }
        
        if (message.contains("No suitable driver")) {
            return "Driver MySQL não encontrado. Execute: mvn clean install";
        }
        
        // Retorna a mensagem original se não for um erro conhecido
        return message.length() > 200 ? message.substring(0, 200) + "..." : message;
    }
    
    /**
     * Classe para encapsular o resultado do teste
     */
    public static class ConnectionResult {
        private final boolean sucesso;
        private final String mensagem;
        
        public ConnectionResult(boolean sucesso, String mensagem) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }
        
        public boolean isSucesso() {
            return sucesso;
        }
        
        public String getMensagem() {
            return mensagem;
        }
    }
}
