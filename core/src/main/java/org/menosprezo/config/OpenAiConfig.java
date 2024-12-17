package org.menosprezo.config;

public class OpenAiConfig {

    // Chave de API do OpenAI
    public static final String API_KEY = System.getenv("API_KEY");

    // URL base da API do OpenAI para Chat Completions
    public static final String API_URL = "https://api.openai.com/v1/chat/completions";

    // URL para listar modelos disponíveis
    public static final String MODELS_URL = "https://api.openai.com/v1/models";

    public static final String WHISPER_URL = "https://api.openai.com/v1/audio/transcriptions";

    // Modelo a ser usado
    public static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    // Número máximo de tokens para as respostas da API
    public static final int MAX_TOKENS = 300;

    // Tempo limite para requisições
    public static final int CONNECTION_TIMEOUT = 10; // segundos
    public static final int READ_TIMEOUT = 30; // segundos
}
