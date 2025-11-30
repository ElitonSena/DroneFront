# 🛠️ DroneFront – Guia de Build e Execução

Este documento é destinado a desenvolvedores que desejam compilar, modificar ou criar executáveis do **DroneFront**.

## 📦 Requisitos para Desenvolvedores

| Ferramenta | Versão Recomendada |
|-----------|------------------|
| JDK | 21+ |
| Maven | 3.11+ |
| IDE | IntelliJ, Eclipse ou VS Code |
| Launch4j *(opcional)* | Para gerar .exe no Windows |

Certifique-se de que o `JAVA_HOME` está configurado corretamente em seu sistema.

## 📥 Clonar o Repositório

```bash
git clone https://github.com/ElitonSena/DroneFront.git
cd DroneFront
```

## ▶️ Executar o Jogo em Desenvolvimento

A maneira recomendada é utilizar o Maven com o plugin do JavaFX já configurado:

```bash
mvn javafx:run
```

Se tudo estiver instalado corretamente, o jogo irá iniciar imediatamente.

## 📦 Gerar o Executável `.jar`

Compile e empacote:

```bash
mvn clean package
```

Após o build, o arquivo será gerado em:

```
target/DroneFront-v1.0.jar
```

Para executar:

```bash
java -jar target/DroneFront-v1.0.jar
```

## 🏁 Criar Executável `.exe` com Launch4j (Windows)

O `.jar` pode ser convertido em um executável do Windows para facilitar a distribuição.

### 📌 Passo a passo

1. Instale o Launch4j  
   🔗 https://launch4j.sourceforge.net/
2. Abra o programa e configure os seguintes campos:

| Campo | Valor |
|------|------|
| **Output file** | `target/DroneFront.exe` |
| **Jar** | `target/DroneFront-v1.0.jar` |
| **JRE Minimum Version** | `21` |
| **Bundled JRE** *(opcional)* | Use se quiser incluir Java junto |

3. Configure também:
   - **Ícone opcional:** um `.ico` do jogo
   - Definir a aplicação como GUI (sem console)
4. Gere o `.exe` clicando em **Build Wrapper**

Após concluído, você terá:

```
target/DroneFront.exe
```

Pronto para distribuir! 🚀

## 🔔 Dicas adicionais

- Certifique-se de que os assets dentro de `src/main/resources` estejam sendo incluídos corretamente no `.jar`
- Se ocorrer erro de JavaFX no .exe:  
  verifique o **class-path** e `--module-path` dentro das opções do Launch4j
- Para build com JRE incorporado → o executável funcionará mesmo sem Java instalado
