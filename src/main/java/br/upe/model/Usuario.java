package br.upe.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    @OneToMany(
            mappedBy = "usuario",         // nome do atributo em PlanoTreino
            cascade = CascadeType.ALL,    // se salvar/deletar usuario, afeta os planos
            orphanRemoval = true          // remove planos sem usuário
    )
    private List<PlanoTreino> planosTreino = new ArrayList<>();

    @OneToMany(
            mappedBy = "usuario",         // nome do atributo em Exercicios
            cascade = CascadeType.ALL,    // se salvar/deletar usuario, afeta os planos
            orphanRemoval = true          // remove planos sem usuário
    )
    private List<Exercicio> exercicios = new ArrayList<>();

    public Usuario() {}

    public Usuario(int id, String nome, String email, String senha, TipoUsuario tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public List<PlanoTreino> getPlanosTreino() {
        return planosTreino;
    }

    public void setPlanosTreino(List<PlanoTreino> planosTreino) {
        this.planosTreino = planosTreino;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nome: '" + nome + "', Email: '" + email + "', Tipo: " + tipo.name();
    }
}