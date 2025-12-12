package br.upe.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="sessao_treino")
public class SessaoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSessao;

    @ManyToOne()
    @JoinColumn(name="id_usuario")
    private Usuario usuario;

    @ManyToOne()
    @JoinColumn(name="id_plano_treino")
    private PlanoTreino planoTreino;

    private LocalDate dataSessao;

    @OneToMany(mappedBy = "sessaoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSessaoTreino> itensExecutados = new ArrayList<>();

    public SessaoTreino() {

    }

    public SessaoTreino(int idSessao, int idUsuario, int idPlanoTreino, LocalDate dataSessao, List<ItemSessaoTreino> itensExecutados) {
        this.idSessao = idSessao;
        this.usuario = new Usuario();
        this.usuario.setId(idUsuario);
        this.planoTreino = new PlanoTreino();
        this.planoTreino.setIdPlano(idPlanoTreino);
        this.dataSessao = dataSessao;
        this.itensExecutados = itensExecutados;
    }

    public SessaoTreino(int idUsuario, int idPlanoTreino) {
        this.usuario = new Usuario();
        this.usuario.setId(idUsuario);
        this.planoTreino = new PlanoTreino();
        this.planoTreino.setIdPlano(idPlanoTreino);
        this.dataSessao = LocalDate.now();
        this.itensExecutados = new ArrayList<>();
    }

    public int getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(int idSessao) {
        this.idSessao = idSessao;
    }

    public int getIdUsuario() {
        return this.usuario.getId();
    }

    public void setIdUsuario(int idUsuario) {
        this.usuario.setId(idUsuario);
    }

    public int getIdPlanoTreino() {
        return planoTreino.getIdPlano();
    }

    public void setIdPlanoTreino(int idPlanoTreino) {
        this.planoTreino.setIdPlano(idPlanoTreino);
    }

    public LocalDate getDataSessao() {
        return dataSessao;
    }

    public void setDataSessao(LocalDate dataSessao) {
        this.dataSessao = dataSessao;
    }

    public List<ItemSessaoTreino> getItensExecutados() {
        return itensExecutados;
    }

    public void setItensExecutados(List<ItemSessaoTreino> itensExecutados) {
        this.itensExecutados = itensExecutados;
    }

    public void adicionarItemExecutado(ItemSessaoTreino item) {
        this.itensExecutados.add(item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID Sessão: ").append(idSessao)
                .append(", ID Usuário: ").append(this.usuario.getId())
                .append(", ID Plano: ").append(planoTreino.getIdPlano())
                .append(", Data: ").append(dataSessao).append("\n");
        if (itensExecutados.isEmpty()) {
            sb.append("  [Nenhum exercício registrado nesta sessão.]");
        } else {
            sb.append("  Exercícios Registrados:\n");
            for (int i = 0; i < itensExecutados.size(); i++) {
                sb.append("    ").append(i + 1).append(". ").append(itensExecutados.get(i).toString()).append("\n");
            }
        }
        return sb.toString();
    }
}