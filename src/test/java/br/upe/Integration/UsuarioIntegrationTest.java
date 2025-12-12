package br.upe.Integration;

import br.upe.model.TipoUsuario;
import br.upe.model.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integracao para Usuario usando H2 em memoria.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Usuario Integration Tests")
public class UsuarioIntegrationTest {

    private static EntityManager em;
    private static int usuarioIdCriado;

    @BeforeAll
    static void setup() {
        IntegrationTestHelper.initEntityManagerFactory();
        em = IntegrationTestHelper.createEntityManager();
    }

    @AfterAll
    static void teardown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        IntegrationTestHelper.closeEntityManagerFactory();
    }

    @Test
    @Order(1)
    @DisplayName("Deve persistir usuario no banco de dados")
    void devePersistirUsuario() {
        Usuario usuario = new Usuario("Maria Silva", "maria@email.com", "senha123", TipoUsuario.COMUM);

        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();

        usuarioIdCriado = usuario.getId();
        assertTrue(usuarioIdCriado > 0);
    }

    @Test
    @Order(2)
    @DisplayName("Deve buscar usuario por id")
    void deveBuscarUsuarioPorId() {
        Usuario encontrado = em.find(Usuario.class, usuarioIdCriado);

        assertNotNull(encontrado);
        assertEquals("Maria Silva", encontrado.getNome());
        assertEquals("maria@email.com", encontrado.getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("Deve buscar usuario por email usando JPQL")
    void deveBuscarUsuarioPorEmail() {
        Usuario encontrado = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", "maria@email.com")
                .getSingleResult();

        assertNotNull(encontrado);
        assertEquals("Maria Silva", encontrado.getNome());
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar usuario")
    void deveAtualizarUsuario() {
        em.getTransaction().begin();
        Usuario usuario = em.find(Usuario.class, usuarioIdCriado);
        usuario.setNome("Maria Santos");
        usuario.setTipo(TipoUsuario.ADMIN);
        em.merge(usuario);
        em.getTransaction().commit();

        em.clear(); // Limpa cache para buscar do banco

        Usuario atualizado = em.find(Usuario.class, usuarioIdCriado);
        assertEquals("Maria Santos", atualizado.getNome());
        assertEquals(TipoUsuario.ADMIN, atualizado.getTipo());
    }

    @Test
    @Order(5)
    @DisplayName("Deve listar todos usuarios")
    void deveListarTodosUsuarios() {
        // Adiciona mais usuarios
        em.getTransaction().begin();
        em.persist(new Usuario("Joao Silva", "joao@email.com", "senha456", TipoUsuario.COMUM));
        em.persist(new Usuario("Pedro Santos", "pedro@email.com", "senha789", TipoUsuario.COMUM));
        em.getTransaction().commit();

        List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();

        assertNotNull(usuarios);
        assertTrue(usuarios.size() >= 3);
    }

    @Test
    @Order(6)
    @DisplayName("Deve verificar constraint de email unico")
    void deveVerificarConstraintEmailUnico() {
        em.getTransaction().begin();
        try {
            Usuario duplicado = new Usuario("Outro Usuario", "maria@email.com", "outrasenha", TipoUsuario.COMUM);
            em.persist(duplicado);
            em.getTransaction().commit();
            fail("Deveria lancar excecao por email duplicado");
        } catch (Exception e) {
            em.getTransaction().rollback();
            // Esperado - email duplicado
            assertTrue(true);
        }
    }

    @Test
    @Order(7)
    @DisplayName("Deve deletar usuario")
    void deveDeletarUsuario() {
        // Criar usuario para deletar
        em.getTransaction().begin();
        Usuario paraRemover = new Usuario("Remover", "remover@email.com", "senha", TipoUsuario.COMUM);
        em.persist(paraRemover);
        em.getTransaction().commit();

        int idRemover = paraRemover.getId();

        em.getTransaction().begin();
        Usuario encontrado = em.find(Usuario.class, idRemover);
        em.remove(encontrado);
        em.getTransaction().commit();

        Usuario deletado = em.find(Usuario.class, idRemover);
        assertNull(deletado);
    }
}
