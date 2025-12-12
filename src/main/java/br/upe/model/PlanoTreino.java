package br.upe.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planos_de_treino")
public class PlanoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPlano;

    @ManyToOne()
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "planoTreino", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPlanoTreino> itensTreino = new ArrayList<>();

    @OneToMany(mappedBy = "planoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessaoTreino> sessaoTreinos = new ArrayList<>();

    public PlanoTreino() {}

    public PlanoTreino(Usuario usuario, String nome) {
        this.usuario = usuario;
        this.nome = nome;
    }

    public int getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(int id) {
        this.idPlano = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public int getIdUsuario() {
        return usuario.getId();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<ItemPlanoTreino> getItensTreino() {
        return itensTreino;
    }

    public void setItensTreino(List<ItemPlanoTreino> itensTreino) {
        this.itensTreino = itensTreino;
    }
    public void adicionarItem(ItemPlanoTreino item) {
        this.itensTreino.add(item);
    }

}