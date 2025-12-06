package br.upe.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "exercicios")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idExercicio;

    @ManyToOne()
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @OneToOne()
    @JoinColumn(name="item_plano_id")
    private ItemPlanoTreino itemPlanoTreino;

    private String nome;

    private String descricao;

    private String caminhoGif;

    // construtor vazio exigido pelo JPA
    public Exercicio() {

    }

    public Exercicio(int idUsuario, String nome, String descricao, String caminhoGif) {
        this.usuario = new Usuario();
        this.usuario.setId(idUsuario);
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoGif = caminhoGif;
    }

    public Exercicio(int idUsuario, int idExercicio, String nome, String descricao, String caminhoGif) {
        this.usuario = new Usuario();
        this.usuario.setId(idUsuario);
        this.usuario.setId(idExercicio);
        this.idExercicio = idExercicio;
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoGif = caminhoGif;
    }

    public int getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(int idExercicio) {
        this.idExercicio = idExercicio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getIdUsuario(){
        return usuario.getId();
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoGif() {
        return caminhoGif;
    }

    public void setCaminhoGif(String caminhoGif) {
        this.caminhoGif = caminhoGif;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Descrição: %s | GIF: %s", idExercicio, nome, descricao, caminhoGif);
    }

}