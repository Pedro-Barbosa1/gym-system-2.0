package br.upe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item_sessao_treino")
public class ItemSessaoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idItemSessao;

    @ManyToOne()
    @JoinColumn(name="id_exercicio")
    private Exercicio exercicio;

    private int repeticoesRealizadas;
    private double cargaRealizada;

    @ManyToOne()
    @JoinColumn(name="id_sessao_treino")
    private SessaoTreino sessaoTreino;

    public ItemSessaoTreino(int idSessaoTreino, int idExercicio, int repeticoesRealizadas, double cargaRealizada) {
        this.sessaoTreino = new SessaoTreino();
        this.sessaoTreino.setIdSessao(idSessaoTreino);
        this.exercicio = new Exercicio();
        this.exercicio.setIdExercicio(idExercicio);
        this.repeticoesRealizadas = repeticoesRealizadas;
        this.cargaRealizada = cargaRealizada;
    }

    public int getIdExercicio() {
        return this.exercicio.getIdExercicio();
    }

    public void setIdExercicio(int idExercicio) {
        this.exercicio.setIdExercicio(idExercicio);
    }

    public int getRepeticoesRealizadas() {
        return repeticoesRealizadas;
    }

    public void setRepeticoesRealizadas(int repeticoesRealizadas) {
        this.repeticoesRealizadas = repeticoesRealizadas;
    }

    public double getCargaRealizada() {
        return cargaRealizada;
    }

    public void setCargaRealizada(double cargaRealizada) {
        this.cargaRealizada = cargaRealizada;
    }

    @Override
    public String toString() {
        return "ID Exercício: " + exercicio.getIdExercicio() + ", Repetições: " + repeticoesRealizadas + ", Carga: " + cargaRealizada + "kg";
    }
}