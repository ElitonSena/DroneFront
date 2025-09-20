# DroneFront Tower Defense

## Requisitos

Para compilar e rodar o projeto, você precisa ter o **Java Development Kit (JDK)**.

## Como Compilar

1.  Vá até a pasta raiz do .zip
2.  Execute ```bash javac dronefront/Game.java ```

## Como Rodar

1.  Após a compilação, inicie o jogo com o comando: ```bash java dronefront.Game ```
    
## Explicação

-   **Interface**: O jogo será exibido no seu terminal. O mapa é desenhado com caracteres, onde:
    -   `#`: O caminho que os inimigos seguem.
    -   `.`: Área livre (construção).
    -   `B`: A sua base.
    -   `S` e `B`: Inimigos, são os `ScoutDrones` e `BomberDrones`.
-   **Objetivo**: Sobreviver a todas as ondas de ataque.
-   **Fim de Jogo**:
    -   **Derrota**: Se a "Vida da Base" chegar a 0.
    -   **Vitória**: Se você sobreviver a todas as ondas programadas.

## Situação do projeto

Atualmente os inimigos apenas percorrem o caminho e causam dano, o jogo termina ao zerar vida. (Entrega 1)

OBS1: Estou fazendo o projeto de forma individual, então irei commitar apenas nos checkpoints.\\
OBS2: A justificativa de design e o diagrama está em `docs/E1/`