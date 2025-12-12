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

    @Test
    @DisplayName("Deve cadastrar indicador com data null usando data atual")
    void deveCadastrarIndicadorComDataNullUsandoDataAtual() {
        when(indicadorRepository.salvar(any(IndicadorBiomedico.class))).thenReturn(indicador);
        
        IndicadorBiomedico resultado = indicadorService.cadastrarIndicador(
            1, null, 75.0, 175.0, 15.0, 85.0);
        
        assertNotNull(resultado);
        verify(indicadorRepository).salvar(any(IndicadorBiomedico.class));
    }

    @Test
    @DisplayName("Deve lancar excecao para percentual massa magra negativo")
    void deveLancarExcecaoParaPercentualMassaMagraNegativo() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.cadastrarIndicador(1, LocalDate.now(), 75.0, 175.0, 15.0, -5.0));
    }

    @Test
    @DisplayName("Deve gerar relatorio diferenca com indicadores")
    void deveGerarRelatorioDiferencaComIndicadores() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fim = LocalDate.of(2024, 12, 31);
        
        IndicadorBiomedico indicadorInicio = new IndicadorBiomedico.Builder()
                .id(1).idUsuario(1).data(inicio).pesoKg(80.0).alturaCm(175.0)
                .percentualGordura(20.0).percentualMassaMagra(80.0).imc(26.0).build();
        IndicadorBiomedico indicadorFim = new IndicadorBiomedico.Builder()
                .id(2).idUsuario(1).data(fim).pesoKg(75.0).alturaCm(175.0)
                .percentualGordura(15.0).percentualMassaMagra(85.0).imc(24.5).build();
        
        when(indicadorRepository.buscarPorPeriodo(1, inicio, fim))
                .thenReturn(Arrays.asList(indicadorInicio, indicadorFim));
        
        RelatorioDiferencaIndicadores resultado = indicadorService.gerarRelatorioDiferenca(1, inicio, fim);
        
        assertNotNull(resultado);
        assertEquals(-5.0, resultado.getDiferencaPeso(), 0.01);
    }

    @Test
    @DisplayName("Deve gerar relatorio diferenca vazio sem indicadores")
    void deveGerarRelatorioDiferencaVazioSemIndicadores() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fim = LocalDate.of(2024, 12, 31);
        
        when(indicadorRepository.buscarPorPeriodo(1, inicio, fim))
                .thenReturn(java.util.Collections.emptyList());
        
        RelatorioDiferencaIndicadores resultado = indicadorService.gerarRelatorioDiferenca(1, inicio, fim);
        
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve lancar excecao no relatorio diferenca quando datas nulas")
    void deveLancarExcecaoNoRelatorioDiferencaQuandoDatasNulas() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.gerarRelatorioDiferenca(1, null, LocalDate.now()));
        
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.gerarRelatorioDiferenca(1, LocalDate.now(), null));
    }

    @Test
    @DisplayName("Deve lancar excecao no relatorio diferenca quando data inicio maior que fim")
    void deveLancarExcecaoNoRelatorioDiferencaQuandoDataInicioMaiorQueFim() {
        LocalDate inicio = LocalDate.of(2024, 12, 31);
        LocalDate fim = LocalDate.of(2024, 1, 1);
        
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.gerarRelatorioDiferenca(1, inicio, fim));
    }

    @Test
    @DisplayName("Deve editar indicador com sucesso")
    void deveEditarIndicadorComSucesso() {
        when(indicadorRepository.buscarPorId(1)).thenReturn(java.util.Optional.of(indicador));
        doNothing().when(indicadorRepository).editar(any(IndicadorBiomedico.class));
        
        indicadorService.editarIndicador(1, LocalDate.now(), 78.0, 175.0, 14.0, 86.0);
        
        verify(indicadorRepository).editar(any(IndicadorBiomedico.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar indicador inexistente")
    void deveLancarExcecaoAoEditarIndicadorInexistente() {
        when(indicadorRepository.buscarPorId(999)).thenReturn(java.util.Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.editarIndicador(999, LocalDate.now(), 78.0, 175.0, 14.0, 86.0));
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar com peso invalido")
    void deveLancarExcecaoAoEditarComPesoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.editarIndicador(1, LocalDate.now(), 0, 175.0, 14.0, 86.0));
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar com altura invalida")
    void deveLancarExcecaoAoEditarComAlturaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.editarIndicador(1, LocalDate.now(), 78.0, -1, 14.0, 86.0));
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar com data nula")
    void deveLancarExcecaoAoEditarComDataNula() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.editarIndicador(1, null, 78.0, 175.0, 14.0, 86.0));
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar com percentual gordura negativo")
    void deveLancarExcecaoAoEditarComPercentualGorduraNegativo() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.editarIndicador(1, LocalDate.now(), 78.0, 175.0, -5.0, 86.0));
    }

    @Test
    @DisplayName("Deve lancar excecao ao editar com percentual massa magra negativo")
    void deveLancarExcecaoAoEditarComPercentualMassaMagraNegativo() {
        assertThrows(IllegalArgumentException.class, () -> 
            indicadorService.editarIndicador(1, LocalDate.now(), 78.0, 175.0, 14.0, -5.0));
    }
}
