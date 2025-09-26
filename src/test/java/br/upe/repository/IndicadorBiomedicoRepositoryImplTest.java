package br.upe.repository;

import br.upe.model.IndicadorBiomedico;
import br.upe.repository.impl.IndicadorBiomedicoRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IndicadorBiomedicoRepositoryImplTest {

    private IndicadorBiomedicoRepositoryImpl repository;

    @BeforeEach
    void setup() {
        repository = new IndicadorBiomedicoRepositoryImpl(false); // evita CSV real
    }

    @Test
    void testSalvarIndicador() {
        IndicadorBiomedico i = new IndicadorBiomedico(0, 1, LocalDate.now(), 70, 170, 15, 55, 24.2);
        IndicadorBiomedico salvo = repository.salvar(i);

        assertNotNull(salvo);
        assertEquals(1, salvo.getId());
        assertEquals(1, repository.listarTodos().size());
    }

    @Test
    void testBuscarPorId() {
        IndicadorBiomedico i = repository.salvar(new IndicadorBiomedico(0, 1, LocalDate.now(), 70, 170, 15, 55, 24.2));
        Optional<IndicadorBiomedico> encontrado = repository.buscarPorId(i.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(1, encontrado.get().getIdUsuario());
    }

    @Test
    void testListarTodos() {
        repository.salvar(new IndicadorBiomedico(0, 1, LocalDate.now(), 70, 170, 15, 55, 24.2));
        repository.salvar(new IndicadorBiomedico(0, 2, LocalDate.now(), 80, 180, 20, 60, 24.7));

        List<IndicadorBiomedico> todos = repository.listarTodos();
        assertEquals(2, todos.size());
    }

    @Test
    void testListarPorUsuario() {
        repository.salvar(new IndicadorBiomedico(0, 1, LocalDate.now(), 70, 170, 15, 55, 24.2));
        repository.salvar(new IndicadorBiomedico(0, 1, LocalDate.now(), 72, 170, 16, 56, 24.9));
        repository.salvar(new IndicadorBiomedico(0, 2, LocalDate.now(), 80, 180, 20, 60, 24.7));

        List<IndicadorBiomedico> usuario1 = repository.listarPorUsuario(1);
        List<IndicadorBiomedico> usuario2 = repository.listarPorUsuario(2);

        assertEquals(2, usuario1.size());
        assertEquals(1, usuario2.size());
    }

    @Test
    void testBuscarPorPeriodo() {
        LocalDate hoje = LocalDate.now();
        repository.salvar(new IndicadorBiomedico(0, 1, hoje.minusDays(5), 70, 170, 15, 55, 24.2));
        repository.salvar(new IndicadorBiomedico(0, 1, hoje.minusDays(2), 72, 170, 16, 56, 24.9));
        repository.salvar(new IndicadorBiomedico(0, 1, hoje.plusDays(1), 73, 170, 16, 56, 25.0));

        List<IndicadorBiomedico> periodo = repository.buscarPorPeriodo(1, hoje.minusDays(3), hoje);

        assertEquals(1, periodo.size());
        assertEquals(72, periodo.get(0).getPesoKg());
    }

    @Test
    void testEditarIndicador() {
        IndicadorBiomedico i = repository.salvar(new IndicadorBiomedico(0, 1, LocalDate.now(), 70, 170, 15, 55, 24.2));
        i.setPesoKg(75);
        repository.editar(i);

        Optional<IndicadorBiomedico> editado = repository.buscarPorId(i.getId());
        assertTrue(editado.isPresent());
        assertEquals(75, editado.get().getPesoKg());
    }

    @Test
    void testDeletarIndicador() {
        IndicadorBiomedico i = repository.salvar(new IndicadorBiomedico(0, 1, LocalDate.now(), 70, 170, 15, 55, 24.2));
        repository.deletar(i.getId());

        Optional<IndicadorBiomedico> deletado = repository.buscarPorId(i.getId());
        assertFalse(deletado.isPresent());
    }

    @Test
    void testGerarProximoId() {
        // Inicialmente, o próximo ID é 1
        assertEquals(1, repository.gerarProximoId());

        // Salvar um indicador (incrementa o ID internamente)
        repository.salvar(new IndicadorBiomedico(0, 1, LocalDate.now(), 70, 170, 15, 55, 24.2));

        // Agora o próximo ID será 3, porque gerarProximoId() é chamado internamente ao salvar
        assertEquals(3, repository.gerarProximoId());
    }
}
