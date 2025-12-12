package br.upe.service;

import br.upe.model.IndicadorBiomedico;
import br.upe.repository.IIndicadorBiomedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IndicadorBiomedicoService Tests")
class IndicadorBiomedicoServiceTest {

    @Mock
    private IIndicadorBiomedicoRepository indicadorRepository;

    private IndicadorBiomedicoService indicadorService;
    private IndicadorBiomedico indicador;

    @BeforeEach
    void setUp() {
        indicadorService = new IndicadorBiomedicoService(indicadorRepository);
        indicador = new IndicadorBiomedico.Builder()
                .id(1)
                .idUsuario(1)
                .data(LocalDate.now())
                .pesoKg(75.0)
                .alturaCm(175.0)
                .percentualGordura(15.0)
                .percentualMassaMagra(85.0)
                .imc(24.5)
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar indicador com sucesso")
    void deveCadastrarIndicadorComSucesso() {
        when(indicadorRepository.salvar(any(IndicadorBiomedico.class))).thenReturn(indicador);
        
        IndicadorBiomedico resultado = indicadorService.cadastrarIndicador(
            1, LocalDate.now(), 75.0, 175.0, 15.0, 85.0);
        
        assertNotNull(resultado);
        assertEquals(75.0, resultado.getPesoKg());
        verify(indicadorRepository).salvar(any(IndicadorBiomedico.class));
    }

    @Test
    @DisplayName("Deve lancar excecao para peso invalido")
    void deveLancarExcecaoParaPesoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.cadastrarIndicador(1, LocalDate.now(), -1.0, 175.0, 15.0, 85.0));
    }

    @Test
    @DisplayName("Deve lancar excecao para altura invalida")
    void deveLancarExcecaoParaAlturaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.cadastrarIndicador(1, LocalDate.now(), 75.0, 0, 15.0, 85.0));
    }

    @Test
    @DisplayName("Deve lancar excecao para percentual gordura negativo")
    void deveLancarExcecaoParaPercentualGorduraNegativo() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.cadastrarIndicador(1, LocalDate.now(), 75.0, 175.0, -5.0, 85.0));
    }

    @Test
    @DisplayName("Deve listar indicadores do usuario")
    void deveListarIndicadoresDoUsuario() {
        List<IndicadorBiomedico> indicadores = Arrays.asList(indicador);
        when(indicadorRepository.listarPorUsuario(1)).thenReturn(indicadores);
        
        List<IndicadorBiomedico> resultado = indicadorService.listarTodosDoUsuario(1);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve gerar relatorio por data")
    void deveGerarRelatorioPorData() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fim = LocalDate.of(2024, 12, 31);
        List<IndicadorBiomedico> indicadores = Arrays.asList(indicador);
        
        when(indicadorRepository.buscarPorPeriodo(1, inicio, fim)).thenReturn(indicadores);
        
        List<IndicadorBiomedico> resultado = indicadorService.gerarRelatorioPorData(1, inicio, fim);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve lancar excecao quando datas sao nulas")
    void deveLancarExcecaoQuandoDatasNulas() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.gerarRelatorioPorData(1, null, LocalDate.now()));
    }

    @Test
    @DisplayName("Deve lancar excecao quando data inicio maior que fim")
    void deveLancarExcecaoQuandoDataInicioMaiorQueFim() {
        LocalDate inicio = LocalDate.of(2024, 12, 31);
        LocalDate fim = LocalDate.of(2024, 1, 1);
        
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.gerarRelatorioPorData(1, inicio, fim));
    }

    @Test
    @DisplayName("Deve deletar indicador com sucesso")
    void deveDeletarIndicadorComSucesso() {
        doNothing().when(indicadorRepository).deletar(1);
        
        indicadorService.deletarIndicador(1);
        
        verify(indicadorRepository).deletar(1);
    }
}
