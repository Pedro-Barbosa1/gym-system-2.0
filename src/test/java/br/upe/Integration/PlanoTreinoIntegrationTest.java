package br.upe.Integration;

import br.upe.model.Exercicio;
import br.upe.model.ItemPlanoTreino;
import br.upe.model.PlanoTreino;
import br.upe.model.Usuario;
import br.upe.model.TipoUsuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PlanoTreino Original Integration Test")
public class PlanoTreinoIntegrationTest {

    private EntityManager em;
    private static Usuario usuarioTeste;

    private static String gerarEmailUnico() {
        return "plano_" + UUID.randomUUID().toString().substring(0, 8) + "@email.com";
    }

    @BeforeAll
    void setup() {
        IntegrationTestHelper.initEntityManagerFactory();
        em = IntegrationTestHelper.createEntityManager();
        
        // Criar usuario para os testes
        em.getTransaction().begin();
        usuarioTeste = new Usuario("Teste Original", gerarEmailUnico(), "senha", TipoUsuario.COMUM);
        em.persist(usuarioTeste);
        em.getTransaction().commit();
    }

    @AfterAll
    void teardown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        IntegrationTestHelper.closeEntityManagerFactory();
    }

    @BeforeEach
    void limparDados() {
        // Com H2 em memoria, nao precisamos limpar dados entre testes
    }

    @Test
    @DisplayName("Deve persistir plano de treino com item")
    void devePersistirPlanoTreinoComItem() {

        //exercício
        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Supino Reto");
        exercicio.setDescricao("Exercício de peitoral");
        exercicio.setUsuario(usuarioTeste);

        //plano de treino
        PlanoTreino plano = new PlanoTreino();
        plano.setNome("Treino A");
        plano.setUsuario(usuarioTeste);

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