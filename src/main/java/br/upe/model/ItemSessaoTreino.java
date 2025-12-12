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

    public ItemSessaoTreino() {

    }

    public ItemSessaoTreino(int idSessaoTreino, int idExercicio, int repeticoesRealizadas, double cargaRealizada) {
        this.sessaoTreino = new SessaoTreino();
        this.sessaoTreino.setIdSessao(idSessaoTreino);
        this.exercicio = new Exercicio();
        this.exercicio.setIdExercicio(idExercicio);
        this.repeticoesRealizadas = repeticoesRealizadas;
        this.cargaRealizada = cargaRealizada;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public SessaoTreino getSessaoTreino() {
        return sessaoTreino;
    }

    public void setSessaoTreino(SessaoTreino sessaoTreino) {
        this.sessaoTreino = sessaoTreino;
    }

    public int getIdExercicio() {
        return this.exercicio != null ? this.exercicio.getIdExercicio() : 0;
    }

    public void setIdExercicio(int idExercicio) {
        if (this.exercicio == null) {
            this.exercicio = new Exercicio();
        }
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