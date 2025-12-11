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
        IndicadorBiomedico i = new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build();
        IndicadorBiomedico salvo = repository.salvar(i);

        assertNotNull(salvo);
        assertEquals(1, salvo.getId());
        assertEquals(1, repository.listarTodos().size());
    }

    @Test
    void testBuscarPorId() {
        IndicadorBiomedico i = repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build());
        Optional<IndicadorBiomedico> encontrado = repository.buscarPorId(i.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(1, encontrado.get().getIdUsuario());
    }

    @Test
    void testListarTodos() {
        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build());

        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(2)
        .data(LocalDate.now())
        .pesoKg(80)
        .alturaCm(180)
        .percentualGordura(20)
        .percentualMassaMagra(60)
        .imc(24.7)
        .build());

        List<IndicadorBiomedico> todos = repository.listarTodos();
        assertEquals(2, todos.size());
    }


    @Test
    void testListarPorUsuario() {
        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build());

        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(72)
        .alturaCm(170)
        .percentualGordura(16)
        .percentualMassaMagra(56)
        .imc(24.9)
        .build());

        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(2)
        .data(LocalDate.now())
        .pesoKg(80)
        .alturaCm(180)
        .percentualGordura(20)
        .percentualMassaMagra(60)
        .imc(24.7)
        .build());

        List<IndicadorBiomedico> usuario1 = repository.listarPorUsuario(1);
        List<IndicadorBiomedico> usuario2 = repository.listarPorUsuario(2);

        assertEquals(2, usuario1.size());
        assertEquals(1, usuario2.size());
    }

    @Test
    void testBuscarPorPeriodo() {
        LocalDate hoje = LocalDate.now();
        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(hoje.minusDays(5))
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build());

        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(hoje.minusDays(2))
        .pesoKg(72)
        .alturaCm(170)
        .percentualGordura(16)
        .percentualMassaMagra(56)
        .imc(24.9)
        .build());

        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(hoje.minusDays(1))
        .pesoKg(73)
        .alturaCm(170)
        .percentualGordura(16)
        .percentualMassaMagra(56)
        .imc(25.0)
        .build());

        List<IndicadorBiomedico> periodo = repository.buscarPorPeriodo(1, hoje.minusDays(3), hoje);

        assertEquals(2, periodo.size());
        assertEquals(72, periodo.get(0).getPesoKg());
    }

    @Test
    void testEditarIndicador() {
        IndicadorBiomedico i = repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build());
        i.setPesoKg(75);
        repository.editar(i);

        Optional<IndicadorBiomedico> editado = repository.buscarPorId(i.getId());
        assertTrue(editado.isPresent());
        assertEquals(75, editado.get().getPesoKg());
    }

    @Test
    void testDeletarIndicador() {
        IndicadorBiomedico i = repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build());
        repository.deletar(i.getId());

        Optional<IndicadorBiomedico> deletado = repository.buscarPorId(i.getId());
        assertFalse(deletado.isPresent());
    }

    @Test
    void testGerarProximoId() {
        // Inicialmente, o próximo ID é 1
        assertEquals(1, repository.gerarProximoId());

        // Salvar um indicador (incrementa o ID internamente)
        repository.salvar(new IndicadorBiomedico.Builder()
        .id(0)
        .idUsuario(1)
        .data(LocalDate.now())
        .pesoKg(70)
        .alturaCm(170)
        .percentualGordura(15)
        .percentualMassaMagra(55)
        .imc(24.2)
        .build());

        // Agora o próximo ID será 3, porque gerarProximoId() é chamado internamente ao salvar
        assertEquals(3, repository.gerarProximoId());
    }
}
