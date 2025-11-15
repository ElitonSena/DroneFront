# DroneFront Tower Defense

## Requisitos

Para compilar e rodar o projeto, você precisa ter o **Java Development Kit (JDK)**.

## Como Compilar

1.  Vá até a pasta raiz do .zip
2.  Execute ```javac dronefront/Game.java```

## Como Rodar

1.  Após a compilação, inicie o jogo com o comando: ```java dronefront.Game ```
    
## Explicação

-   **Interface**: O jogo será exibido no seu terminal. O mapa é desenhado com caracteres, onde:
    -   `X`: É o seu cursor, mova ele com WASD e Enter
    -   `.`: O caminho que os inimigos seguem.
    -   ` `: Área livre (construção).
    -   `B`: A sua base.
    -   `S` e `B`: Inimigos, são os `ScoutDrones` e `BomberDrones`.
    -   `T`: São as torres, podem ser `GunTower` ou `PEMTower`.
    IMPORTANTE: Aperte enter para enviar o input na fase de construção.
-   **Objetivo**: Sobreviver a todas as ondas de ataque.
-   **Fim de Jogo**:
    -   **Derrota**: Se a "Vida da Base" chegar a 0.
    -   **Vitória**: Se você sobreviver a todas as ondas programadas.

## Situação do projeto

`20/09`: Atualmente os inimigos apenas percorrem o caminho e causam dano, o jogo termina ao zerar vida. (Entrega 1)  
`28/10`: Implementei a lógica de adição/remoção de torres, dano com projéteis e economia inicial. (Entrega 2)  
`15/11`: Adicionei mais inimigos, torres e ondas, implementei um sistema de upgrade e melhorei alguns mecânicas de dano e vulnerabilidade. O spawm de inimigos nas ondas agora é declarado por metodos probabilísticos, adicionando um certo nível de aleatoriedade.

OBS1: Estou fazendo o projeto de forma individual, então irei commitar apenas nos checkpoints.  
OBS2: A justificativa de design e o diagrama está em `docs/E3/`