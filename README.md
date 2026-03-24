# Chess System - Jogo de Xadrez em Java

Este projeto é uma implementação de um jogo de xadrez para console, desenvolvido em Java. Ele foi criado como parte de um curso de programação para praticar conceitos de Programação Orientada a Objetos (POO).

O jogo permite que dois jogadores joguem em um único computador, com todas as regras e movimentos do xadrez implementados.

---

## 🚀 Funcionalidades

O jogo de xadrez para console inclui as seguintes funcionalidades:

* **Tabuleiro e Peças**: Representação completa do tabuleiro (8x8) e todas as peças de xadrez (Peão, Torre, Cavalo, Bispo, Rainha e Rei).
* **Movimentos Legais**: Validação de todos os movimentos de acordo com as regras do xadrez.
* **Jogadas Especiais**: Suporte para jogadas especiais como:
    * **Roque (Castling)**
    * **Promoção (Promotion)**
    * **En Passant**
* **Controle de Jogo**:
    * Exibe o jogador atual e o turno do jogo.
    * Armazena e exibe as peças capturadas.
    * Sistema de verificação de **Xeque** e **Xeque-Mate**.
* **Interface no Terminal**: Interface simples e funcional com cores para melhorar a experiência do usuário.
* **Interface Web Interativa (GitHub Pages)**: Tabuleiro clicável com destaque de jogadas possíveis, status da partida, promoção e lista de peças capturadas.
* **Tratamento de Erros**: Mensagens de erro claras para jogadas inválidas, garantindo a robustez do programa.

---

## 💻 Tecnologias Utilizadas

* **Linguagem**: Java (versão 11 ou superior)
* **Ambiente de Execução**: Terminal ou IDE (como Eclipse, IntelliJ, etc.)

---

## ⚙️ Como Instalar e Rodar

Siga os passos abaixo para baixar e executar o projeto na sua máquina.

### Pré-requisitos
* **Java Development Kit (JDK)**: Certifique-se de ter o Java 11 ou uma versão superior instalada.
* **Git**: Para clonar o repositório.

### Passos

1.  **Clone o Repositório**:
    Abra o seu terminal (ou Git Bash) e use o seguinte comando para clonar o projeto:
    ```bash
    git clone [https://github.com/fakezindev/chess-system-java.git](https://github.com/fakezindev/chess-system-java.git)
    ```

2.  **Entre na Pasta do Projeto**:
    Navegue até o diretório do projeto:
    ```bash
    cd chess-system-java
    ```

3.  **Executar localmente**:
    * **Versão Web (portfolio / GitHub Pages)**:

      Rode um servidor estático na raiz do projeto e acesse `docs/index.html`:

      ```bash
      python3 -m http.server 8080
      ```
      Depois abra: `http://localhost:8080/docs/`

    * **Publicar no GitHub Pages**:
      - Faça push da branch para o GitHub.
      - Em **Settings > Pages**, escolha **Deploy from a branch**.
      - Selecione a branch (ex: `main`) e a pasta `/docs`.
      - Salve e aguarde o link público da página.

    * **Modo Java CLI (legado)**:
      Continua disponível no código Java, executando `application.Program --cli` em ambiente Java.
