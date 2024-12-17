
# TryIA - Bot de Telegram com Processamento de Voz e Texto

Este projeto é um **bot do Telegram** desenvolvido em **Java**, que integra funcionalidades de **processamento de texto e voz**, fornecendo exercícios interativos para aprendizado de inglês e respostas em áudio.

---

## 📋 Funcionalidades

- **Comandos Interativos**:
    - `/start`: Inicia o bot e exibe uma mensagem de boas-vindas.
    - `/help`: Lista os comandos disponíveis.
    - `/exercise`: Envia um exercício aleatório de inglês.
    - `/exercise easy` ou `/exercise hard`: Gera exercícios de diferentes dificuldades.

- **Processamento de Mensagens de Voz**:
    - Transcreve mensagens de voz usando **WhisperService**.
    - Gera respostas em áudio com **Amazon Polly** (Text-to-Speech).

- **Gerenciamento de Mensagens de Texto**:
    - Responde dinamicamente a mensagens de texto e comandos.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 11+
- **Frameworks e Bibliotecas**:
    - [TelegramBots API](https://github.com/rubenlagus/TelegramBots)
    - Amazon Polly (Text-to-Speech)
    - OpenAI Whisper (Transcrição de áudio)
- **Gerenciamento de Dependências**: Maven

---

## 🚀 Configuração do Projeto

### Pré-requisitos

1. **Java 11+** instalado.
2. **Maven** configurado no sistema.
3. **Token do Bot** obtido via [BotFather](https://t.me/BotFather) no Telegram.
4. Chaves de acesso da **Amazon Polly** (para gerar áudios).

### Configurando o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/TryIA.git
   cd TryIA
   ```

2. Adicione suas configurações no arquivo `BotConfig.java`:
   ```java
   public class BotConfig {
   //Procure usar váriaveis de ambiente!
       public static final String BOT_USERNAME = "SEU_BOT_USERNAME";
       public static final String BOT_TOKEN = "SEU_BOT_TOKEN";
   }
   ```

3. Configure as chaves da **Amazon Polly** no arquivo `PollyConfig.java`:
   ```java
   //Procure usar váriaveis de ambiente!
   public class PollyConfig {
       public static final String ACESS_KEY = "SUA_ACCESS_KEY";
       public static final String SECRET_KEY = "SUA_SECRET_KEY";
       public static final String REGION = "sua-regiao";
   }
   ```

4. Compile e execute o projeto:
   ```bash
   mvn clean package
   java -jar bot/target/bot-1.0-SNAPSHOT-shaded.jar
   ```

---

## 📦 Estrutura do Projeto

```
TryIA/
│
├── bot/
│   ├── src/
│   │   ├── main/java/org/menosprezo/bot/
│   │   │   ├── BotHandler.java      # Classe principal que gerencia as interações do bot
│   │   │   ├── CommandHandler.java  # Processa comandos específicos do bot
│   │   │   ├── MessageProcessor.java# Processa mensagens de texto e áudio
│   │   └── test/                    # Testes unitários (caso aplicável)
│   └── pom.xml                      # Configuração Maven do módulo 'bot'
│
├── core/
│   ├── src/main/java/org/menosprezo/
│   │   ├── services/                # Serviços auxiliares (Whisper, Text-to-Speech)
│   │   ├── utils/                   # Utilitários como manipulação de arquivos
│   └── pom.xml                      # Configuração Maven do módulo 'core'
│
├── downloads/                       # Armazena arquivos baixados e gerados
├── pom.xml                          # Configuração Maven principal do projeto
└── Procfile                         # Arquivo de execução para deploy (Heroku, etc.)
```

---

## 📄 Uso

- Execute o bot e envie os comandos no **Telegram**:
    - **Exemplo de Comando**:
      ```
      /exercise
      /exercise easy
      /exercise hard
      ```

- Envie mensagens de **voz** para o bot, e ele irá responder com uma transcrição e um áudio gerado.

---

## 🌐 Deploy

- O projeto pode ser executado localmente ou implantado em serviços como **Heroku**.
- Utilize o **Procfile** para configurar a execução:
   ```bash
   worker: java -jar bot/target/bot-1.0-SNAPSHOT-shaded.jar
   ```

---

## 🤝 Contribuição

Contribuições são bem-vindas! Siga os passos abaixo:

1. Faça um **fork** do repositório.
2. Crie uma nova branch:
   ```bash
   git checkout -b feature/nova-funcionalidade
   ```
3. Commit suas mudanças:
   ```bash
   git commit -m "Adiciona nova funcionalidade"
   ```
4. Suba para o repositório:
   ```bash
   git push origin feature/nova-funcionalidade
   ```
5. Abra um **Pull Request**.

---

## 📧 Contato

- **Desenvolvedor**: Vinicius Oliveira
- **Email**: viniciusgomes.33mes@gmail.com
- **Telegram**: [Seu Link](https://t.me/contemp1)

---

## 📝 Licença

Este projeto é licenciado sob a [MIT License](LICENSE).

---

## ⚠️ Observações
- Certifique-se de ter as permissões necessárias para usar as APIs de terceiros.
- Configure variáveis sensíveis (como tokens) de forma segura.
