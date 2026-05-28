# Coup: Distributed Edition

Este projeto consiste na digitalização, transposição arquitetural e distribuição em rede do aclamado jogo de cartas e blefe **Coup**. 
A aplicação foi evoluída de uma versão puramente local para uma arquitetura distribuída cliente-servidor baseada no protocolo **RPC (Remote Procedure Call)** utilizando a tecnologia **Java RMI (Remote Method Invocation)**.

O sistema foi estruturado de forma robusta e altamente desacoplada através do padrão **MVC (Model-View-Controller)**, com o fluxo de fases e os turnos gerenciados por padrões de projeto comportamentais e de criação. Isso garante o sigilo absoluto das cartas ocultas nos clientes, delegando exclusivamente ao servidor a autoridade de validação e a consistência global do estado da partida.

## Lore: A TechSphere

A ambientação narrativa do jogo transpõe o cenário político corrompido do Coup original para uma megacorporação de tecnologia fictícia chamada **A TechSphere**, que controla toda a *stack* global (redes, dados, identidade e computação). Cada personagem clássico foi reinterpretado como um mascote ou tecnologia real do ecossistema de TI:

* **Duke (Java Duke):** O agente mais antigo. Controla os royalties de licenciamento de cada JVM rodando no mundo.
  * *Ação (Imposto):* Coleta 3 moedas do tesouro.
  * *Bloqueio:* Intercepta e bloqueia a Ajuda Externa.

* **Droid (Android):** Ubíquo, transitando entre bilhões de dispositivos ativos através de novas assinaturas e troca de identidades.
  * *Ação (Troca):* Compra cartas extras e troca influências com o baralho.
  * *Bloqueio:* Firewalls de permissão negam a extorsão do Capitão.

* **Octo (Git/GitHub):** O auditor interno que tem acesso a todo o histórico de commits e logs da corporação.
  * *Ação (Inquisição - Modo Inquisidor):* Força um rival a revelar ou trocar uma carta ativa.
  * *Bloqueio:* Reverte e bloqueia o roubo do Capitão via negação de *pull request*.

* **Elefante (PostgreSQL):** Controla atomicamente todas as transações financeiras e registros relacionais com ACID compliance.
  * *Ação (Roubo/Extorsão):* Subtrai até 2 moedas de um jogador alvo.
  * *Bloqueio:* Rejeita transações de roubo adversárias por constraints de integridade.

* **Kali (Kali Linux):** Opera nas camadas não documentadas e portas abertas da rede corporativa.
  * *Ação (Assassinato):* Paga 3 moedas para eliminar permanentemente uma influência alvo.

* **Ferris (Rust):** Incorruptível por design. Seu *borrow checker* estático e gerenciamento de memória impedem qualquer falha de segurança.
  * *Bloqueio:* Rejeita e barra tentativas de assassinato.

## Arquitetura do Sistema e Camadas

O projeto adota uma divisão rigorosa de responsabilidades organizada nas seguintes camadas:

1. **Camada de Modelo (Model):** Classes de domínio puro (`Jogador`, `Carta`, `Baralho`, `Personagem` e especializações), responsáveis por reter e atualizar o estado de dados do jogo. O isolamento do Model no servidor impede adulterações e trapaças vindas dos nós clientes.

2. **Camada de Controle (Controller):** Centralizada na classe `JogoController`, ela consome a View de forma polimórfica e dita o fluxo operacional das rodadas.

3. **Camada de View (View):** Interface `IJogoView` com múltiplas implementações intercambiáveis. Suporta interação via console local (`Console`), interface gráfica em Swing (`MenuGrafico`/`TelaJogo`) e ponte de rede corporativa (`JogoViewRemota`).

4. **Camada de Comunicação:** Gerenciada por stubs remotos do Java RMI expressos nas interfaces `IJogoServidor` e `IClient`.

### Estrutura de Pastas

```text
coup/
├── .settings/
├── src/
│   ├── cliente/
│   │   ├── Cliente.java              # Implementação e exportação do stub do Cliente
│   │   └── IClient.java              # Interface remota para callbacks do cliente
│   ├── coup/
│   │   ├── acoes/                    # Encapsulamento de habilidades (Command)
│   │   ├── controller/               # JogoController e motor de turnos
│   │   ├── estadoJogo/               # Máquina de estados explícita (State)
│   │   ├── factory/                  # Instanciação de versões do jogo (Factory Method)
│   │   ├── model/                    # Entidades e domínios de dados (Model)
│   │   └── view/                     # Classes de interface e consoles (View)
│   └── servidor/
│       ├── IJogoServidor.java        # Interface de entrada exposta pelo servidor
│       ├── JogoServidor.java         # Inicializador do RMI Registry e Lobby
│       └── JogoViewRemota.java       # Bridge/Adapter adaptando RMI para o padrão MVC
├── .classpath
├── .gitignore
└── .project

```

## Padrões de Projeto Aplicados

A modularidade e o baixo acoplamento do sistema foram alcançados pela combinação de três padrões fundamentais da Engenharia de Software:

* **Command Pattern (Hierarquia de Ações):** A interface comum `Acao` padroniza o contrato de todas as habilidades (como `Ducar`, `Capitar`, `Assassinar`). Cada ação encapsula suas próprias regras e metadados de execução (`podeSerContestada`, `requerAlvo`, etc.), permitindo que o controlador trate qualquer movimento de forma polimórfica e sem condicionais complexos.

* **State Pattern (Máquina de Estados de Turno):** Substitui checagens procedimentais por uma máquina de estados finitos robusta composta por 6 estados claros: `AguardandoAcao`, `AguardandoRespostaAcao`, `ResolvendoContestacao`, `AguardandoRespostaBloqueio`, `AguardandoDescarte` e `AguardandoTrocaEmbaixador`. As transições são autoencapsuladas, isolando o fluxo do jogo das entradas da interface.

* **Factory Method (Múltiplas Versões):** Através da abstração `IJogoFactory`, o sistema consegue alternar dinamicamente e de forma transparente entre a **Versão Original** (com o Embaixador) e a **Versão Inquisidor**.

## Comunicação Distribuída e Concorrência

* **Padrão Callback com RMI:** A comunicação em rede é bidirecional. O cliente localiza o servidor no registro e expõe a si próprio estendendo `UnicastRemoteObject`. Isso permite que o servidor atue ativamente notificando eventos aos clientes em tempo real, eliminando desperdício de banda por polling.

* **Processamento Assíncrono Paralelo:** Um dos principais desafios em um jogo reativo como Coup é aguardar que múltiplos jogadores decidam se vão contestar ou bloquear uma ação. O servidor resolve isso de forma concorrente utilizando o ecossistema `java.util.concurrent`, disparando instâncias de `CompletableFuture.runAsync()` em paralelo para coletar as respostas de todos os oponentes simultaneamente.

## Stack Tecnológica

* **Linguagem:** Java 11+ 
* **Comunicação:** Java RMI (Remote Method Invocation) 
* **Concorrência:** API `java.util.concurrent` (`CompletableFuture`) 
* **Interface Gráfica Local:** Java Swing (`JOptionPane`, `JFrame`) 
* **IDE Base:** Eclipse IDE 

## Como Executar a Aplicação

Certifique-se de possuir o Java 11 ou superior configurado no ambiente.

### 1. Inicializar o Servidor

O servidor criará automaticamente o registro do RMI (`LocateRegistry`) na porta padrão `1099` e vinculará o stub do lobby sob o identificador `"CoupServidor"`. Ele está configurado para aguardar a conexão de 3 jogadores para iniciar o motor do jogo automaticamente:

```bash
java servidor.JogoServidor

```

### 2. Inicializar os Clientes

Abra três terminais ou instâncias separadas (uma para cada jogador). Ao executar, a aplicação solicitará o nome do jogador no console e tentará se registrar no servidor remoto (`localhost:1099`):

```bash
java cliente.Cliente

```