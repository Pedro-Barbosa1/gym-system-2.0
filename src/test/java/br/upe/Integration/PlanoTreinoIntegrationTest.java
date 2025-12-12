package br.upe.Integration;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlanoTreinoIntegrationTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    void setup() {
        emf = Persistence.createEntityManagerFactory("AcademiaPU");
        em = emf.createEntityManager();
    }

    @AfterAll
    void teardown() {
        em.close();
        emf.close();
    }

    @BeforeEach
    void limparDados() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM ItemSessaoTreino").executeUpdate();
        em.createQuery("DELETE FROM SessaoTreino").executeUpdate();
        em.createQuery("DELETE FROM ItemPlanoTreino").executeUpdate();
        em.createQuery("DELETE FROM PlanoTreino").executeUpdate();
        em.createQuery("DELETE FROM Exercicio WHERE nome = 'Supino Reto'").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    void devePersistirPlanoTreinoComItem() {

        //exercício
        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Supino Reto");
        exercicio.setDescricao("Exercício de peitoral");

        //plano de treino
        PlanoTreino plano = new PlanoTreino();
        plano.setNome("Treino A");

        //item do plano
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setCargaKg(50);
        item.setRepeticoes(10);
        item.setPlanoTreino(plano);

        setExercicioForTest(item, exercicio);
        plano.getItensTreino().add(item);


        em.getTransaction().begin();
        em.persist(exercicio);
        em.persist(plano);
        em.persist(item);
        em.getTransaction().commit();

        //Validação
        List<ItemPlanoTreino> itens = em
                .createQuery("SELECT i FROM ItemPlanoTreino i", ItemPlanoTreino.class)
                .getResultList();

        assertEquals(1, itens.size());

        ItemPlanoTreino salvo = itens.get(0);

        assertEquals(50, salvo.getCargaKg());
        assertEquals(10, salvo.getRepeticoes());
        assertNotNull(salvo.getPlanoTreino());
        assertEquals("Treino A", salvo.getPlanoTreino().getNome());
        assertEquals("Supino Reto", salvo.getIdExercicio() > 0 ? exercicio.getNome() : null);
    }

    private void setExercicioForTest(ItemPlanoTreino item, Exercicio exercicio) {
        try {
            var field = ItemPlanoTreino.class.getDeclaredField("exercicio");
            field.setAccessible(true);
            field.set(item, exercicio);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}