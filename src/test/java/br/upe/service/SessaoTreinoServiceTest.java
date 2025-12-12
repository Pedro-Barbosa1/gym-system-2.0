package br.upe.service;

import br.upe.model.*;
import br.upe.repository.IExercicioRepository;
import br.upe.repository.IPlanoTreinoRepository;
import br.upe.repository.ISessaoTreinoRepository;
import br.upe.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessaoTreinoService Tests")
class SessaoTreinoServiceTest {

    @Mock
    private ISessaoTreinoRepository sessaoRepo;
    @Mock
    private IPlanoTreinoRepository planoRepo;
    @Mock
    private IExercicioRepository exercicioRepo;
    @Mock
    private IUsuarioRepository usuarioRepo;

    private SessaoTreinoService sessaoTreinoService;
    private Usuario usuario;
    private PlanoTreino planoTreino;
    private Exercicio exercicio;
    private SessaoTreino sessaoTreino;

    @BeforeEach
    void setUp() throws Exception {
        sessaoTreinoService = new SessaoTreinoService();
        
        // Injetar mocks via reflection
        injetarCampo(sessaoTreinoService, "sessaoRepo", sessaoRepo);
        injetarCampo(sessaoTreinoService, "planoRepo", planoRepo);
        injetarCampo(sessaoTreinoService, "exercicioRepo", exercicioRepo);
        injetarCampo(sessaoTreinoService, "usuarioRepo", usuarioRepo);
        
        usuario = new Usuario(1, "Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
        planoTreino = new PlanoTreino(usuario, "Treino A");
        setId(planoTreino, "idPlano", 1);
        
        exercicio = new Exercicio(usuario, "Supino", "Supino reto", null);
        setId(exercicio, "idExercicio", 1);
        
        sessaoTreino = new SessaoTreino();
        sessaoTreino.setUsuario(usuario);
        sessaoTreino.setPlanoTreino(planoTreino);
        sessaoTreino.setDataSessao(LocalDate.now());
    }

    private void injetarCampo(Object objeto, String nomeCampo, Object valor) throws Exception {
        Field campo = objeto.getClass().getDeclaredField(nomeCampo);
        campo.setAccessible(true);
        campo.set(objeto, valor);
    }

    private void setId(Object objeto, String nomeCampo, int valor) throws Exception {
        Field campo = objeto.getClass().getDeclaredField(nomeCampo);
        campo.setAccessible(true);
        campo.set(objeto, valor);
    }

    @Test
    @DisplayName("Deve iniciar sessao com sucesso")
    void deveIniciarSessaoComSucesso() {
        when(usuarioRepo.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(planoTreino));

        SessaoTreino resultado = sessaoTreinoService.iniciarSessao(1, 1);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(planoTreino, resultado.getPlanoTreino());
        assertEquals(LocalDate.now(), resultado.getDataSessao());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario nao encontrado ao iniciar sessao")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepo.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> sessaoTreinoService.iniciarSessao(999, 1));
        
        assertTrue(exception.getMessage().contains("Usuário com ID 999 não encontrado"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando plano nao encontrado ao iniciar sessao")
    void deveLancarExcecaoQuandoPlanoNaoEncontrado() {
        when(usuarioRepo.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoRepo.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> sessaoTreinoService.iniciarSessao(1, 999));
        
        assertTrue(exception.getMessage().contains("Plano de treino com ID 999 não encontrado"));
    }

    @Test
    @DisplayName("Deve lancar excecao quando plano nao pertence ao usuario")
    void deveLancarExcecaoQuandoPlanoNaoPertenceAoUsuario() throws Exception {
        Usuario outroUsuario = new Usuario(2, "Maria", "maria@email.com", "senha", TipoUsuario.COMUM);
        PlanoTreino planoOutroUsuario = new PlanoTreino(outroUsuario, "Treino B");
        setId(planoOutroUsuario, "idPlano", 2);
        
        when(usuarioRepo.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoRepo.buscarPorId(2)).thenReturn(Optional.of(planoOutroUsuario));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> sessaoTreinoService.iniciarSessao(1, 2));
        
        assertTrue(exception.getMessage().contains("não pertence a você"));
    }

    @Test
    @DisplayName("Deve registrar execucao de exercicio com sucesso")
    void deveRegistrarExecucaoComSucesso() {
        when(exercicioRepo.buscarPorId(1)).thenReturn(Optional.of(exercicio));

        sessaoTreinoService.registrarExecucao(sessaoTreino, 1, 10, 50.0);

        assertEquals(1, sessaoTreino.getItensExecutados().size());
        ItemSessaoTreino item = sessaoTreino.getItensExecutados().get(0);
        assertEquals(10, item.getRepeticoesRealizadas());
        assertEquals(50.0, item.getCargaRealizada());
    }

    @Test
    @DisplayName("Deve lancar excecao quando exercicio nao encontrado")
    void deveLancarExcecaoQuandoExercicioNaoEncontrado() {
        when(exercicioRepo.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> sessaoTreinoService.registrarExecucao(sessaoTreino, 999, 10, 50.0));
        
        assertTrue(exception.getMessage().contains("Exercício com ID 999 não encontrado"));
    }

    @Test
    @DisplayName("Deve salvar sessao com itens executados")
    void deveSalvarSessaoComItens() throws Exception {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(10);
        item.setCargaRealizada(50.0);
        sessaoTreino.adicionarItemExecutado(item);

        sessaoTreinoService.salvarSessao(sessaoTreino);

        verify(sessaoRepo).salvar(sessaoTreino);
    }

    @Test
    @DisplayName("Nao deve salvar sessao vazia")
    void naoDeveSalvarSessaoVazia() {
        // Sessão sem itens executados
        sessaoTreinoService.salvarSessao(sessaoTreino);

        verify(sessaoRepo, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve listar sessoes por usuario")
    void deveListarSessoesPorUsuario() {
        List<SessaoTreino> sessoes = Arrays.asList(sessaoTreino);
        when(sessaoRepo.listarPorUsuario(1)).thenReturn(sessoes);

        List<SessaoTreino> resultado = sessaoTreinoService.listarSessoesPorUsuario(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve remover sessao com sucesso")
    void deveRemoverSessaoComSucesso() {
        doNothing().when(sessaoRepo).deletar(1);

        sessaoTreinoService.removerSessao(1);

        verify(sessaoRepo).deletar(1);
    }

    @Test
    @DisplayName("Deve retornar lista vazia de sugestoes quando plano nao encontrado")
    void deveRetornarListaVaziaDeSugestoesQuandoPlanoNaoEncontrado() throws Exception {
        setId(sessaoTreino, "idSessao", 1);
        setId(planoTreino, "idPlano", 1);
        sessaoTreino.setPlanoTreino(planoTreino);
        
        // Simular que getIdPlanoTreino retorna um ID
        when(planoRepo.buscarPorId(anyInt())).thenReturn(Optional.empty());

        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
            sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessaoTreino);

        assertTrue(sugestoes.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar sugestoes quando ha mudancas")
    void deveGerarSugestoesQuandoHaMudancas() throws Exception {
        setId(planoTreino, "idPlano", 1);
        sessaoTreino.setPlanoTreino(planoTreino);
        
        // Configurar item no plano
        ItemPlanoTreino itemPlano = new ItemPlanoTreino();
        itemPlano.setPlanoTreino(planoTreino);
        itemPlano.setExercicio(exercicio);
        itemPlano.setRepeticoes(8);
        itemPlano.setCargaKg(40);
        planoTreino.adicionarItem(itemPlano);
        
        // Configurar item executado (diferente do planejado)
        ItemSessaoTreino itemExecutado = new ItemSessaoTreino();
        itemExecutado.setSessaoTreino(sessaoTreino);
        itemExecutado.setExercicio(exercicio);
        itemExecutado.setRepeticoesRealizadas(12);
        itemExecutado.setCargaRealizada(50.0);
        sessaoTreino.adicionarItemExecutado(itemExecutado);
        
        when(planoRepo.buscarPorId(anyInt())).thenReturn(Optional.of(planoTreino));
        when(exercicioRepo.buscarPorId(1)).thenReturn(Optional.of(exercicio));

        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
            sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessaoTreino);

        assertFalse(sugestoes.isEmpty());
        assertEquals(1, sugestoes.size());
        SessaoTreinoService.SugestaoAtualizacaoPlano sugestao = sugestoes.get(0);
        assertEquals(8, sugestao.repPlanejadas());
        assertEquals(12, sugestao.repRealizadas());
        assertEquals(40.0, sugestao.cargaPlanejada());
        assertEquals(50.0, sugestao.cargaRealizada());
    }

    @Test
    @DisplayName("Nao deve gerar sugestoes quando nao ha mudancas")
    void naoDeveGerarSugestoesQuandoNaoHaMudancas() throws Exception {
        setId(planoTreino, "idPlano", 1);
        sessaoTreino.setPlanoTreino(planoTreino);
        
        // Configurar item no plano
        ItemPlanoTreino itemPlano = new ItemPlanoTreino();
        itemPlano.setPlanoTreino(planoTreino);
        itemPlano.setExercicio(exercicio);
        itemPlano.setRepeticoes(10);
        itemPlano.setCargaKg(50);
        planoTreino.adicionarItem(itemPlano);
        
        // Configurar item executado (igual ao planejado)
        ItemSessaoTreino itemExecutado = new ItemSessaoTreino();
        itemExecutado.setSessaoTreino(sessaoTreino);
        itemExecutado.setExercicio(exercicio);
        itemExecutado.setRepeticoesRealizadas(10);
        itemExecutado.setCargaRealizada(50.0);
        sessaoTreino.adicionarItemExecutado(itemExecutado);
        
        when(planoRepo.buscarPorId(anyInt())).thenReturn(Optional.of(planoTreino));

        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
            sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessaoTreino);

        assertTrue(sugestoes.isEmpty());
    }

    @Test
    @DisplayName("Deve aplicar atualizacoes no plano com sucesso")
    void deveAplicarAtualizacoesNoPlanoComSucesso() throws Exception {
        setId(planoTreino, "idPlano", 1);
        
        // Configurar item no plano
        ItemPlanoTreino itemPlano = new ItemPlanoTreino();
        itemPlano.setPlanoTreino(planoTreino);
        itemPlano.setExercicio(exercicio);
        itemPlano.setRepeticoes(8);
        itemPlano.setCargaKg(40);
        planoTreino.adicionarItem(itemPlano);
        
        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(planoTreino));
        doNothing().when(planoRepo).editar(any(PlanoTreino.class));

        sessaoTreinoService.aplicarAtualizacoesNoPlano(1, 1, 12, 50.0);

        verify(planoRepo).editar(planoTreino);
        assertEquals(12, itemPlano.getRepeticoes());
        assertEquals(50, itemPlano.getCargaKg());
    }

    @Test
    @DisplayName("Nao deve aplicar atualizacoes quando plano nao encontrado")
    void naoDeveAplicarAtualizacoesQuandoPlanoNaoEncontrado() {
        when(planoRepo.buscarPorId(999)).thenReturn(Optional.empty());

        sessaoTreinoService.aplicarAtualizacoesNoPlano(999, 1, 12, 50.0);

        verify(planoRepo, never()).editar(any());
    }

    @Test
    @DisplayName("Nao deve aplicar atualizacoes quando exercicio nao encontrado no plano")
    void naoDeveAplicarAtualizacoesQuandoExercicioNaoNoPlano() throws Exception {
        setId(planoTreino, "idPlano", 1);
        // Plano sem itens
        
        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(planoTreino));

        sessaoTreinoService.aplicarAtualizacoesNoPlano(1, 999, 12, 50.0);

        verify(planoRepo, never()).editar(any());
    }

    @Test
    @DisplayName("Deve testar record SugestaoAtualizacaoPlano")
    void deveTestarRecordSugestaoAtualizacaoPlano() {
        SessaoTreinoService.SugestaoAtualizacaoPlano sugestao = 
            new SessaoTreinoService.SugestaoAtualizacaoPlano(1, "Supino", 8, 12, 40.0, 50.0);
        
        assertEquals(1, sugestao.idExercicio());
        assertEquals("Supino", sugestao.nomeExercicio());
        assertEquals(8, sugestao.repPlanejadas());
        assertEquals(12, sugestao.repRealizadas());
        assertEquals(40.0, sugestao.cargaPlanejada());
        assertEquals(50.0, sugestao.cargaRealizada());
    }
}
