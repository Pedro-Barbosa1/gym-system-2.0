package br.upe.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "itens_plano_treino")
public class ItemPlanoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idItem;

    private int cargaKg;
    private int repeticoes;

    @ManyToOne()
    @JoinColumn(name = "planoTreino_idPlano", nullable = false)
    private PlanoTreino planoTreino;

    @OneToOne()
    @JoinColumn(name="idExercicio")
    private Exercicio exercicio;

    public ItemPlanoTreino() {}

    public ItemPlanoTreino(int idPlano, int idExercicio, int cargaKg, int repeticoes) {
        this.exercicio = new Exercicio();
        this.exercicio.setIdExercicio(idExercicio);
        this.cargaKg = cargaKg;
        this.repeticoes = repeticoes;
        this.planoTreino = new PlanoTreino();
        this.planoTreino.setIdPlano(idPlano);
    }

    public int getIdItem() {
        return idItem;
    }

    public int getCargaKg() {
        return cargaKg;
    }

    public void setCargaKg(int cargaKg) {
        this.cargaKg = cargaKg;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public int getIdExercicio(){
        return this.exercicio.getIdExercicio();
    }


    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    public PlanoTreino getPlanoTreino() {
        return planoTreino;
    }

    public void setPlanoTreino(PlanoTreino planoTreino) {
        this.planoTreino = planoTreino;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

}