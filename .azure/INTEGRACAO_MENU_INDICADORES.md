# 📊 Integração MenuIndicadores → IndicadoresViewController

## 🎯 Objetivo
Adaptar a funcionalidade de gerenciamento de indicadores biomédicos da interface terminal (`MenuIndicadores.java`) para a interface gráfica JavaFX (`IndicadoresViewController.java`), utilizando **Dialogs como pop-ups** para todas as interações com o usuário.

---

## 📋 Mapeamento de Funcionalidades

### ✅ Funcionalidades Implementadas

| MenuIndicadores (Terminal) | IndicadoresViewController (JavaFX) | Status |
|----------------------------|-------------------------------------|--------|
| `cadastrarNovoIndicador()` | `cadastrarNovoIndicador()` | ✅ Dialog com GridPane |
| `listarMeusIndicadores()` | `listarMeusIndicadores()` | ✅ Dialog com TextArea |
| `exibirMenu()` | Botões na tela principal | ✅ Interface gráfica |

---

## 🔄 Conversão: Terminal → JavaFX

### 1️⃣ Cadastrar Novo Indicador

#### 🖥️ **Terminal (MenuIndicadores)**
```java
private void cadastrarNovoIndicador() {
    System.out.print("Data (AAAA-MM-DD): ");
    String dataStr = sc.nextLine();
    // ... continua com Scanner
}
```

#### 🎨 **JavaFX (IndicadoresViewController)**
```java
@FXML
void cadastrarNovoIndicador(ActionEvent event) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Cadastrar Novo Indicador");
    
    GridPane grid = new GridPane();
    TextField dataField = new TextField(LocalDate.now().format(DATE_FORMATTER));
    TextField pesoField = new TextField();
    TextField alturaField = new TextField();
    // ... adiciona campos ao grid
    
    dialog.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            // Processa dados
        }
    });
}
```

**Diferenças chave:**
- ❌ `Scanner sc.nextLine()` → ✅ `TextField.getText()`
- ❌ `System.out.println()` → ✅ `Alert.showAndWait()`
- ❌ `try-catch` com prints → ✅ `try-catch` com Alerts de erro

---

### 2️⃣ Listar Indicadores

#### 🖥️ **Terminal (MenuIndicadores)**
```java
private void listarMeusIndicadores() {
    List<IndicadorBiomedico> meusIndicadores = indicadorService.listarTodosDoUsuario(idUsuarioLogado);
    meusIndicadores.forEach(System.out::println);
}
```

#### 🎨 **JavaFX (IndicadoresViewController)**
```java
@FXML
void listarMeusIndicadores(ActionEvent event) {
    List<IndicadorBiomedico> meusIndicadores = indicadorService.listarTodosDoUsuario(idUsuarioLogado);
    
    Dialog<ButtonType> dialog = new Dialog<>();
    TextArea textArea = new TextArea();
    textArea.setEditable(false);
    textArea.setPrefWidth(600);
    textArea.setPrefHeight(400);
    
    StringBuilder sb = new StringBuilder();
    for (IndicadorBiomedico ind : meusIndicadores) {
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("Data: %s\n", ind.getData()));
        sb.append(String.format("Peso: %.1f kg | Altura: %.1f cm\n", 
            ind.getPesoKg(), ind.getAlturaCm()));
        sb.append(String.format("IMC: %.1f\n", ind.getImc()));
        // ... mais dados
    }
    textArea.setText(sb.toString());
    dialog.getDialogPane().setContent(textArea);
    dialog.showAndWait();
}
```

**Diferenças chave:**
- ❌ `System.out::println` → ✅ `TextArea` em Dialog
- ❌ Loop direto no console → ✅ `StringBuilder` formatado
- ✅ Interface gráfica com scroll automático

---

## 🏗️ Estrutura do Controller

### 📦 Imports Adicionados
```java
import br.upe.model.IndicadorBiomedico;
import br.upe.service.IIndicadorBiomedicoService;
import br.upe.service.IndicadorBiomedicoService;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
```

### 🔧 Serviços Injetados
```java
private final IIndicadorBiomedicoService indicadorService;
private int idUsuarioLogado = 1; // TODO: Integrar com sistema de login

public IndicadoresViewController() {
    this.indicadorService = new IndicadorBiomedicoService();
}
```

---

## 🎨 Tipos de Dialogs Utilizados

### 1. **Dialog com GridPane** (Cadastro)
```java
Dialog<ButtonType> dialog = new Dialog<>();
GridPane grid = new GridPane();
grid.setHgap(10);
grid.setVgap(10);
grid.setPadding(new Insets(20, 150, 10, 10));

// Adicionar campos
grid.add(new Label("Data:"), 0, 0);
grid.add(dataField, 1, 0);

dialog.getDialogPane().setContent(grid);
dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
```

### 2. **Dialog com TextArea** (Listagem)
```java
Dialog<ButtonType> dialog = new Dialog<>();
TextArea textArea = new TextArea();
textArea.setEditable(false);
textArea.setPrefWidth(600);
textArea.setPrefHeight(400);
textArea.setText(conteudo);

dialog.getDialogPane().setContent(textArea);
dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
```

### 3. **Alert Simples** (Mensagens)
```java
Alert alert = new Alert(Alert.AlertType.INFORMATION);
alert.setTitle("Sucesso");
alert.setContentText("Indicador cadastrado com sucesso!");
alert.showAndWait();
```

---

## 🔗 Vinculação FXML → Controller

### Arquivo: `IndicadoresView.fxml`

```xml
<!-- Botão Cadastrar -->
<Button fx:id="BCadastrarIN" 
        onAction="#cadastrarNovoIndicador" 
        text="Cadastrar novo indicador" />

<!-- Botão Listar -->
<Button fx:id="BListarIN" 
        onAction="#listarMeusIndicadores" 
        text="Ver meus indicadores" />

<!-- Botão Sair -->
<Button fx:id="sairB" 
        onAction="#handleSair" />

<!-- ImageView com evento de mouse -->
<ImageView fx:id="IFechar" 
           onMouseClicked="#voltar" />
```

---

## 🎯 Fluxo de Cadastro Completo

```
1. Usuário clica no botão "Cadastrar novo indicador"
   ↓
2. `cadastrarNovoIndicador()` é chamado
   ↓
3. Dialog com GridPane é exibido
   ├─ Campo Data (pré-preenchido com hoje)
   ├─ Campo Peso (kg)
   ├─ Campo Altura (cm)
   ├─ Campo % Gordura
   └─ Campo % Massa Magra
   ↓
4. Usuário preenche os campos
   ↓
5. Clica em "OK"
   ↓
6. Controller valida e processa:
   ├─ Parse de data (DateTimeFormatter)
   ├─ Parse de doubles
   └─ Chamada ao service
   ↓
7. Sucesso → Alert de confirmação com IMC calculado
   Erro → Alert de erro com mensagem específica
```

---

## 🎯 Fluxo de Listagem Completo

```
1. Usuário clica no botão "Ver meus indicadores"
   ↓
2. `listarMeusIndicadores()` é chamado
   ↓
3. Service busca todos os indicadores do usuário
   ↓
4. Verifica se há indicadores:
   ├─ Vazio → Alert "Sem indicadores"
   └─ Com dados → Dialog com TextArea
   ↓
5. TextArea exibe lista formatada:
   ═══════════════════════════════════════
   Data: 2025-10-17
   Peso: 75.5 kg | Altura: 175.0 cm
   IMC: 24.6
   Gordura: 15.2% | Massa Magra: 70.8%
   ═══════════════════════════════════════
   [próximo indicador...]
```

---

## 🛡️ Tratamento de Erros

### Validações Implementadas

| Erro | Tipo de Alert | Mensagem |
|------|---------------|----------|
| Formato de data inválido | ERROR | "Formato de data inválido. Use AAAA-MM-DD (ex: 2025-10-17)" |
| Valores não numéricos | ERROR | "Por favor, digite valores numéricos válidos." |
| Erro de validação do service | ERROR | "Erro ao cadastrar indicador: [mensagem]" |
| Sem indicadores | INFORMATION | "Você ainda não possui indicadores registrados." |

### Código de Tratamento
```java
try {
    double peso = Double.parseDouble(pesoField.getText().trim());
    // ... outros campos
    IndicadorBiomedico novo = indicadorService.cadastrarIndicador(/*...*/);
    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", /*...*/);
    
} catch (NumberFormatException e) {
    mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", /*...*/);
} catch (DateTimeParseException e) {
    mostrarAlerta(Alert.AlertType.ERROR, "Erro de Data", /*...*/);
} catch (IllegalArgumentException e) {
    mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
}
```

---

## 📊 Comparação: Terminal vs JavaFX

| Aspecto | Terminal | JavaFX |
|---------|----------|--------|
| **Input** | Scanner readline | TextField em Dialog |
| **Output** | System.out.println | TextArea / Alert |
| **Navegação** | Loop + switch-case | Botões com @FXML |
| **Validação** | try-catch com prints | try-catch com Alerts |
| **UX** | Linear, sequencial | Assíncrono, não-bloqueante |
| **Formato** | Texto simples | Formatação rica (GridPane, cores) |

---

## 🎨 Componentes JavaFX Utilizados

### 1. **GridPane**
- Organização de campos em grid (Label + TextField)
- `setHgap(10)` e `setVgap(10)` para espaçamento
- `setPadding()` para margem interna

### 2. **TextArea**
- Exibição de múltiplos indicadores
- `setEditable(false)` para apenas leitura
- Scroll automático para listas longas

### 3. **TextField**
- Entrada de dados do usuário
- Validação com `getText().trim()`
- Pré-preenchimento com valores padrão

### 4. **Alert**
- Mensagens de sucesso/erro/informação
- Tipos: INFORMATION, WARNING, ERROR, CONFIRMATION

---

## ✅ Checklist de Implementação

- [x] Importar serviços necessários
- [x] Adicionar variável `indicadorService`
- [x] Criar construtor inicializando serviços
- [x] Implementar `cadastrarNovoIndicador()` com GridPane Dialog
- [x] Implementar `listarMeusIndicadores()` com TextArea Dialog
- [x] Adicionar tratamento de erros com Alerts
- [x] Vincular métodos aos botões no FXML
- [x] Adicionar `onAction` aos botões
- [x] Adicionar `onMouseClicked` às ImageViews
- [x] Testar compilação (mvn clean compile)
- [x] Documentar código com JavaDoc

---

## 🚀 Próximos Passos (Funcionalidades Futuras)

### 📈 Relatórios (do MenuIndicadores original)
Estas funcionalidades ainda não foram implementadas, mas podem ser adicionadas:

1. **Relatório por Data**
   - Dialog com 2 DatePickers (data início e fim)
   - Exibir resultados em TextArea

2. **Relatório de Diferença**
   - Dialog com 2 DatePickers
   - Usar `RelatorioDiferencaIndicadores`
   - Exibir comparação em TextArea formatada

3. **Gráficos de Evolução**
   - Usar JavaFX Charts (LineChart)
   - Plotar IMC, peso, etc. ao longo do tempo

---

## 📚 Referências

- **JavaFX Dialog**: [Oracle Docs - Dialog](https://openjfx.io/javadoc/17/javafx.controls/javafx/scene/control/Dialog.html)
- **GridPane**: [Oracle Docs - GridPane](https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/layout/GridPane.html)
- **Alert**: [Oracle Docs - Alert](https://openjfx.io/javadoc/17/javafx.controls/javafx/scene/control/Alert.html)
- **DateTimeFormatter**: [Java Docs - DateTimeFormatter](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/format/DateTimeFormatter.html)

---

## 🎉 Resultado Final

✅ **Interface gráfica completa e funcional**
- Dialogs modernos e intuitivos
- Validações robustas com feedback visual
- Código limpo e bem documentado
- Compilação sem erros (BUILD SUCCESS)
- Integração perfeita com serviços existentes

---

*Documentação gerada em 17/10/2025*
*Versão do JavaFX: 21.0.4*
*Build Tool: Maven 3.x*
