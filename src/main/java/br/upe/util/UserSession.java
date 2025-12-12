package br.upe.util;

import br.upe.model.Usuario;

/**
 * Classe Singleton para gerenciar a sessão do usuário logado.
 * Mantém as informações do usuário durante toda a execução da aplicação.
 */
public class UserSession {
    
    private static UserSession instance;
    private Usuario usuarioLogado;
    
    private UserSession() {
        // Construtor privado para padrão Singleton
    }
    
    /**
     * Obtém a instância única da sessão.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    /**
     * Define o usuário que está logado no sistema.
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }
    
    /**
     * Obtém o usuário atualmente logado.
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    /**
     * Obtém o ID do usuário logado.
     * @return ID do usuário ou 0 se nenhum usuário estiver logado
     */
    public int getIdUsuarioLogado() {
        return usuarioLogado != null ? usuarioLogado.getId() : 0;
    }
    
    /**
     * Verifica se há um usuário logado.
     */
    public boolean isLogado() {
        return usuarioLogado != null;
    }
    
    /**
     * Limpa a sessão (logout).
     */
    public void limparSessao() {
        this.usuarioLogado = null;
    }
}
