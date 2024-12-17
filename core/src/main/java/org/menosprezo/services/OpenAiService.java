package org.menosprezo.services;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.menosprezo.config.OpenAiConfig;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OpenAiService {

    private final OkHttpClient client;

    public OpenAiService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(OpenAiConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(OpenAiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public String getResponseFromOpenAI(List<JSONObject> conversationHistory) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", OpenAiConfig.DEFAULT_MODEL);
        requestBody.put("messages", new JSONArray(conversationHistory));
        requestBody.put("max_tokens", OpenAiConfig.MAX_TOKENS);

        int retryCount = 3;
        while (retryCount > 0) {
            try {
                Request request = new Request.Builder()
                        .url(OpenAiConfig.API_URL)
                        .header("Authorization", "Bearer " + OpenAiConfig.API_KEY)
                        .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        return jsonResponse.getJSONArray("choices").getJSONObject(0)
                                .getJSONObject("message").getString("content").trim();
                    } else {
                        logError(response);
                    }
                }
            } catch (IOException e) {
                retryCount--;
                if (retryCount == 0) {
                    throw e;
                }
                System.err.println("Erro na requisição OpenAI. Tentando novamente...");
            }
        }
        throw new IOException("Falha ao obter resposta da API OpenAI após várias tentativas.");
    }

    private void logError(Response response) throws IOException {
        String errorBody = response.body() != null ? response.body().string() : "Sem detalhes.";
        System.err.println("Erro na API: Código " + response.code() + " - " + response.message());
        System.err.println("Detalhes: " + errorBody);
    }

    public void listAvailableModels() throws IOException {
        Request request = new Request.Builder()
                .url(OpenAiConfig.MODELS_URL)
                .header("Authorization", "Bearer " + OpenAiConfig.API_KEY)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                JSONArray models = jsonResponse.getJSONArray("data");

                System.out.println("Modelos disponíveis:");
                for (int i = 0; i < models.length(); i++) {
                    JSONObject model = models.getJSONObject(i);
                    System.out.println("- " + model.getString("id"));
                }
            } else {
                String errorBody = response.body() != null ? response.body().string() : "Nenhum detalhe de erro.";
                System.err.println("Erro ao listar os modelos: Código " + response.code() + " - " + response.message());
                System.err.println("Detalhes: " + errorBody);
            }
        }
    }
}
