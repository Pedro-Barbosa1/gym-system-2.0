package br.upe.session;

import br.upe.model.Usuario;

public class SessaoUsuario {
    private static Usuario UsuarioLogado;

    public static void setUsuarioLogado(Usuario usuario) {
        UsuarioLogado=usuario;
    }

    public static Usuario getUsuariologado() {
        return UsuarioLogado;
    }

    public static Integer getIdLogado() {
        return UsuarioLogado != null ? UsuarioLogado.getId() : null;
    }

    public static void limparSessao() {
        UsuarioLogado = null;
    }


    
}
