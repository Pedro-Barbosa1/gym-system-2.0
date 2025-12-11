package br.upe.util;

import java.util.Optional;
import java.util.logging.Logger;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import br.upe.service.IUsuarioService;
import br.upe.service.UsuarioService;

/**
 * Classe responsável por inicializar dados padrão no banco de dados,
 * como o usuário administrador padrão.
 */
public class DatabaseInitializer {
    
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());
    
    // Credenciais do usuário administrador padrão
    private static final String ADMIN_EMAIL = "ADM";
    private static final String ADMIN_NOME = "ADM";
    private static final String ADMIN_SENHA = "ADM";
    
    /**
     * Inicializa os dados padrão do sistema.
     * Deve ser chamado uma vez ao iniciar a aplicação.
     */
    public static void inicializarDadosPadrao() {
        try {
            criarUsuarioAdminPadrao();
        } catch (Exception e) {
            logger.severe("Erro ao inicializar dados padrão: " + e.getMessage());
        }
    }
    
    /**
     * Cria o usuário administrador padrão se ele não existir.
     */
    private static void criarUsuarioAdminPadrao() {
        IUsuarioService usuarioService = new UsuarioService();
        
        try {
            // Verifica se o usuário admin já existe
            Optional<Usuario> adminExistente = usuarioService.buscarUsuarioPorEmail(ADMIN_EMAIL);
            
            if (adminExistente.isEmpty()) {
                // Cria o usuário admin
                Usuario admin = usuarioService.cadastrarUsuario(
                    ADMIN_NOME,
                    ADMIN_EMAIL,
                    ADMIN_SENHA,
                    TipoUsuario.ADMIN
                );
                
                logger.info("✅ Usuário administrador padrão criado com sucesso!");
                logger.info("   Email: " + ADMIN_EMAIL);
                logger.info("   Senha: " + ADMIN_SENHA);
                
            } else {
                logger.info("ℹ️ Usuário administrador padrão já existe no banco de dados.");
            }
            
        } catch (IllegalArgumentException e) {
            // Usuário já existe
            logger.info("ℹ️ Usuário administrador padrão já existe: " + e.getMessage());
            
        } catch (Exception e) {
            logger.severe("❌ Erro ao criar usuário administrador padrão: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Retorna as credenciais do admin padrão (útil para testes)
     */
    public static String getAdminEmail() {
        return ADMIN_EMAIL;
    }
    
    public static String getAdminSenha() {
        return ADMIN_SENHA;
    }
}
